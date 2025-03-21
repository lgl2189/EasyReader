package com.reader.ui.view;

import com.reader.entity.book.Book;
import com.reader.storage.DataStorage;
import com.reader.storage.common.DataDepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * @author      ：李冠良
 * @description ：无描述
 * @date        ：2025 3月 21 19:39
 */



public class AddBookView {

    // 左侧组件
    @FXML
    private ListView<Book> bookListView;
    @FXML private Button addBookBtn;

    // 添加表单组件
    @FXML private GridPane addFormPane;
    @FXML private TextField nameInputField;
    @FXML private TextField urlInputField;
    @FXML private Button confirmAddBtn;

    // 详情面板组件
    @FXML private VBox detailPanel;

    private final ObservableList<Book> bookList = FXCollections.observableArrayList();
    private final DataDepository<?> bookDepository = DataStorage.BOOK_DEPOSITORY;

    @FXML
    public void initialize() {
        setupInitialData();
        setupInitialUi();
        setupListView();
        setupEventHandlers();
    }

    private void setupInitialData() {
        bookDepository.getAll().forEach((key, value) -> bookList.add((Book) value));
    }

    private void setupInitialUi() {
        addFormPane.setVisible(false);
        detailPanel.setVisible(false);
    }

    private void setupListView() {
        bookListView.setItems(bookList);
        bookListView.setCellFactory(lv -> new ListCell<Book>() {
            @Override
            protected void updateItem(Book item, boolean empty) {
                super.updateItem(item, empty);
            }
        });
    }

    private void setupEventHandlers() {
        // 添加新书按钮
        addBookBtn.setOnAction(e -> {
            addFormPane.setVisible(true);
            detailPanel.setVisible(false);
            bookListView.getSelectionModel().clearSelection();
            clearInputFields();
        });

        // 确认添加按钮
        confirmAddBtn.setOnAction(e -> handleAddBook());

        // 列表选择监听
        bookListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showDetailPanel(newVal);
                addFormPane.setVisible(false);
                detailPanel.setVisible(true);
            } else {
                detailPanel.setVisible(false);
            }
        });
    }

    private void handleAddBook() {
//        Book newBook = new Book(
//                nameInputField.getText().trim(),
//                urlInputField.getText().trim()
//        );
//
//        if (newBook.getName().isEmpty() || newBook.getUrl().isEmpty()) {
//            // 此处可添加错误提示逻辑
//            return;
//        }

//        bookList.add(newBook);
        addFormPane.setVisible(false);
        clearInputFields();
    }

    private void clearInputFields() {
        nameInputField.clear();
        urlInputField.clear();
    }

    private void showDetailPanel(Book book) {
        // 可在此处实现书籍详情显示逻辑
    }
}