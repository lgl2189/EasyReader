package com.reader.ui.view;

import com.reader.entity.net.Website;
import com.reader.entity.template.BookTemplate;
import com.reader.storage.DataStorage;
import com.reader.storage.common.impl.ObjectDepository;
import com.reader.ui.util.NotificationUtil;
import com.reader.webpage.access.AccessBookTemplateXpath;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.function.Consumer;

/**
 * @author ：李冠良
 * @description ：无描述
 * @date ：2025 3月 21 19:39
 */
public class AddBookView {
    // 左侧组件
    @FXML
    private ListView<BookTemplate> bookListView;
    @FXML
    private Button addBookTemplateBtn;
    // 添加表单组件
    @FXML
    private GridPane addFormPane;
    @FXML
    private TextField templateNameInputField;
    @FXML
    private ComboBox<Website> websiteComboBox;
    @FXML
    private TextField bookNameXpathField;
    @FXML
    private TextField authorXpathField;
    @FXML
    private TextField publisherXpathField;
    @FXML
    private TextField isbnXpathField;
    @FXML
    private TextField languageXpathField;
    @FXML
    private TextField descriptionXpathField;
    @FXML
    private TextField isFinishedXpathField;
    @FXML
    private Spinner<Integer> layerCountSpinner;
    @FXML
    private Button getXpathBtnOnAdd;
    @FXML
    private Button confirmAddBtn;
    // 详情面板组件
    @FXML
    private ScrollPane detailPane;
    @FXML
    private TextField detailTemplateName;
    @FXML
    private TextField detailWebsite;
    @FXML
    private TextField detailBookNameXpath;
    @FXML
    private TextField detailAuthorXpath;
    @FXML
    private TextField detailPublisherXpath;
    @FXML
    private TextField detailIsbnXpath;
    @FXML
    private TextField detailLanguageXpath;
    @FXML
    private TextField detailDescriptionXpath;
    @FXML
    private TextField detailIsFinishedXpath;
    @FXML
    private TextField detailLayerCount;
    @FXML
    private Button changeBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button getXpathBtnOnDetail;
    @FXML
    private Button deleteBtn;

    private final ObservableList<BookTemplate> bookList = FXCollections.observableArrayList();
    private final ObservableList<Website> websiteList = FXCollections.observableArrayList();
    private final ObjectDepository bookDepository = DataStorage.BOOK_DEPOSITORY;
    private final ObjectDepository websiteDepository = DataStorage.WEBSITE_DEPOSITORY;
    private Website currentWebsite; // 新增：用于保存当前选中书籍的website

    @FXML
    public void initialize() {
        setupInitialData();
        setupInitialUi();
        setupListView();
        setupEventHandlers();
    }

    private void setupInitialData() {
        // 加载书籍模板数据
        bookDepository.getAll().forEach((key, value) -> bookList.add((BookTemplate) value));
        // 加载网站数据
        refreshData();
    }

    private void setupInitialUi() {
        addFormPane.setVisible(false);
        detailPane.setVisible(false);
        // 初始化层级数选择器
        layerCountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
    }

    private void setupListView() {
        bookListView.setItems(bookList);
        bookListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(BookTemplate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getId());
                }
            }
        });
    }

    private void setupEventHandlers() {
        // 添加新书按钮
        addBookTemplateBtn.setOnAction(e -> {
            addFormPane.setVisible(true);
            detailPane.setVisible(false);
            bookListView.getSelectionModel().clearSelection();
            clearInputFields();
        });
        getXpathBtnOnAdd.setOnAction(_ -> {
            Website selectedWebsite = websiteComboBox.getValue();
            if (selectedWebsite == null) {
                NotificationUtil.showError("请先选择网站");
                return;
            }
            getXpathRuleAsync(selectedWebsite, bookTemplate -> {
                // 将获取到的XPath数据填充到表单
                if (bookTemplate.getBookNameXpath() != null) {
                    bookNameXpathField.setText(bookTemplate.getBookNameXpath());
                }
                if (bookTemplate.getAuthorXpath() != null) {
                    authorXpathField.setText(bookTemplate.getAuthorXpath());
                }
                if (bookTemplate.getPublisherXpath() != null) {
                    publisherXpathField.setText(bookTemplate.getPublisherXpath());
                }
                if (bookTemplate.getIsbnXpath() != null) {
                    isbnXpathField.setText(bookTemplate.getIsbnXpath());
                }
                if (bookTemplate.getLanguageXpath() != null) {
                    languageXpathField.setText(bookTemplate.getLanguageXpath());
                }
                if (bookTemplate.getDescriptionXpath() != null) {
                    descriptionXpathField.setText(bookTemplate.getDescriptionXpath());
                }
                if (bookTemplate.getIsFinishedXpath() != null) {
                    isFinishedXpathField.setText(bookTemplate.getIsFinishedXpath());
                }
                NotificationUtil.showSuccess("XPath规则已自动填充");
            });
        });
        // 确认添加按钮
        confirmAddBtn.setOnAction(e -> handleAddBook());
        // 列表选择监听
        bookListView.getSelectionModel().selectedItemProperty().addListener((_, _, newVal) -> {
            if (newVal != null) {
                showDetailPanel(newVal);
                currentWebsite = newVal.getWebsite(); // 保存当前选中书籍的website
                addFormPane.setVisible(false);
                detailPane.setVisible(true);
            } else {
                detailPane.setVisible(false);
            }
        });
        // 点击修改按钮
        changeBtn.setOnAction(_ -> {
            if (saveBtn.isVisible()) {
                saveBtn.setVisible(false);
                getXpathBtnOnDetail.setVisible(false); // 新增
                detailTemplateName.setEditable(false);
                detailWebsite.setEditable(false);
                detailBookNameXpath.setEditable(false);
                detailAuthorXpath.setEditable(false);
                detailPublisherXpath.setEditable(false);
                detailIsbnXpath.setEditable(false);
                detailLanguageXpath.setEditable(false);
                detailDescriptionXpath.setEditable(false);
                detailIsFinishedXpath.setEditable(false);
                detailLayerCount.setEditable(false);
                changeBtn.setText("修改");
            } else {
                saveBtn.setVisible(true);
                getXpathBtnOnDetail.setVisible(true); // 新增
                detailTemplateName.setEditable(true);
                detailWebsite.setEditable(true);
                detailBookNameXpath.setEditable(true);
                detailAuthorXpath.setEditable(true);
                detailPublisherXpath.setEditable(true);
                detailIsbnXpath.setEditable(true);
                detailLanguageXpath.setEditable(true);
                detailDescriptionXpath.setEditable(true);
                detailIsFinishedXpath.setEditable(true);
                detailLayerCount.setEditable(true);
                changeBtn.setText("退出修改");
            }
        });
        // 在修改状态中重新获取xpath
        getXpathBtnOnDetail.setOnAction(_ -> {
            BookTemplate selectedBook = bookListView.getSelectionModel().getSelectedItem();
            if (selectedBook == null || selectedBook.getWebsite() == null) {
                NotificationUtil.showError("请先选择有效的书籍");
                return;
            }
            getXpathRuleAsync(selectedBook.getWebsite(), bookTemplate -> {
                // 将获取到的XPath数据填充到详情表单
                if (bookTemplate.getBookNameXpath() != null) {
                    detailBookNameXpath.setText(bookTemplate.getBookNameXpath());
                }
                if (bookTemplate.getAuthorXpath() != null) {
                    detailAuthorXpath.setText(bookTemplate.getAuthorXpath());
                }
                if (bookTemplate.getPublisherXpath() != null) {
                    detailPublisherXpath.setText(bookTemplate.getPublisherXpath());
                }
                if (bookTemplate.getIsbnXpath() != null) {
                    detailIsbnXpath.setText(bookTemplate.getIsbnXpath());
                }
                if (bookTemplate.getLanguageXpath() != null) {
                    detailLanguageXpath.setText(bookTemplate.getLanguageXpath());
                }
                if (bookTemplate.getDescriptionXpath() != null) {
                    detailDescriptionXpath.setText(bookTemplate.getDescriptionXpath());
                }
                if (bookTemplate.getIsFinishedXpath() != null) {
                    detailIsFinishedXpath.setText(bookTemplate.getIsFinishedXpath());
                }
                NotificationUtil.showSuccess("XPath规则已自动填充");
            });
        });
        // 保存按钮
        saveBtn.setOnAction(_ -> handleUpdateBook());
        deleteBtn.setOnAction(_ -> {
            handleDeleteBook();
        });
    }

    private void handleAddBook() {
        // 获取选中的网站
        Website selectedWebsite = websiteComboBox.getValue();
        if (selectedWebsite == null) {
            NotificationUtil.showError("请选择网站");
            return;
        }
        // 创建新的书籍模板
        BookTemplate newBook = new BookTemplate(
                templateNameInputField.getText().trim(),
                selectedWebsite,
                bookNameXpathField.getText().trim(),
                authorXpathField.getText().trim(),
                publisherXpathField.getText().trim(),
                isbnXpathField.getText().trim(),
                languageXpathField.getText().trim(),
                descriptionXpathField.getText().trim(),
                isFinishedXpathField.getText().trim(),
                layerCountSpinner.getValue()
        );
        // 保存到仓库和列表
        bookDepository.add(newBook.getId(), newBook);
        bookList.add(newBook);
        addFormPane.setVisible(false);
        clearInputFields();
    }

    private void handleUpdateBook() {
        BookTemplate selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            NotificationUtil.showError("请先选择要更新的书籍！");
            return;
        }
        String newId = detailTemplateName.getText().trim();
        // 使用保存的website信息
        Website newWebsite = currentWebsite;
        String newBookNameXpath = detailBookNameXpath.getText().trim();
        String newAuthorXpath = detailAuthorXpath.getText().trim();
        String newPublisherXpath = detailPublisherXpath.getText().trim();
        String newIsbnXpath = detailIsbnXpath.getText().trim();
        String newLanguageXpath = detailLanguageXpath.getText().trim();
        String newDescriptionXpath = detailDescriptionXpath.getText().trim();
        String newIsFinishedXpath = detailIsFinishedXpath.getText().trim();
        int newLayerCount = Integer.parseInt(detailLayerCount.getText());

        // 更新书籍模板
        selectedBook.setId(newId);
        selectedBook.setWebsite(newWebsite);
        selectedBook.setBookNameXpath(newBookNameXpath);
        selectedBook.setAuthorXpath(newAuthorXpath);
        selectedBook.setPublisherXpath(newPublisherXpath);
        selectedBook.setIsbnXpath(newIsbnXpath);
        selectedBook.setLanguageXpath(newLanguageXpath);
        selectedBook.setDescriptionXpath(newDescriptionXpath);
        selectedBook.setIsFinishedXpath(newIsFinishedXpath);
        selectedBook.setLayerCount(newLayerCount);

        // 更新仓库和列表
        bookDepository.update(selectedBook.getId(), selectedBook);
        NotificationUtil.showSuccess("已保存修改！");
    }

    private void handleDeleteBook(){
        BookTemplate selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            NotificationUtil.showError("请先选择要删除的书籍！");
            return;
        }
        // 从仓库中删除
        bookDepository.delete(selectedBook.getId());
        // 从列表中删除
        bookList.remove(selectedBook);
        // 隐藏详情面板
        detailPane.setVisible(false);
        NotificationUtil.showSuccess("书籍已删除！");
    }

    private void getXpathRuleAsync(Website website, Consumer<BookTemplate> onSuccess) {
        Task<BookTemplate> task = new Task<>() {
            @Override
            protected BookTemplate call() {
                AccessBookTemplateXpath accessBookTemplateXpath = new AccessBookTemplateXpath(website);
                return accessBookTemplateXpath.start();
            }
        };
        task.setOnSucceeded(_ -> onSuccess.accept(task.getValue()));
        new Thread(task).start();
    }

    private void clearInputFields() {
        templateNameInputField.clear();
        bookNameXpathField.clear();
        authorXpathField.clear();
        publisherXpathField.clear();
        isbnXpathField.clear();
        languageXpathField.clear();
        descriptionXpathField.clear();
        isFinishedXpathField.clear();
        layerCountSpinner.getValueFactory().setValue(1);
    }

    private void showDetailPanel(BookTemplate book) {
        detailTemplateName.setText(book.getId());
        detailWebsite.setText(book.getWebsite().getName());
        detailBookNameXpath.setText(book.getBookNameXpath());
        detailAuthorXpath.setText(book.getAuthorXpath());
        detailPublisherXpath.setText(book.getPublisherXpath());
        detailIsbnXpath.setText(book.getIsbnXpath());
        detailLanguageXpath.setText(book.getLanguageXpath());
        detailDescriptionXpath.setText(book.getDescriptionXpath());
        detailIsFinishedXpath.setText(book.getIsFinishedXpath());
        detailLayerCount.setText(String.valueOf(book.getLayerCount()));
    }

    /**
     * 刷新网站数据列表
     */
    public void refreshData() {
        websiteList.clear();
        websiteDepository.getAll().forEach((key, value) -> websiteList.add((Website) value));
        websiteComboBox.setItems(websiteList);
        websiteComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Website item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                }
                else {
                    setText(item.getName());
                }
            }
        });
        // 设置buttonCell以确保选择后只显示网站名称
        websiteComboBox.setButtonCell(new ListCell<Website>() {
            @Override
            protected void updateItem(Website item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
    }
}