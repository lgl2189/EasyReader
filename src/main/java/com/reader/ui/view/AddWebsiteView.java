package com.reader.ui.view;

import com.reader.entity.net.LoginStatus;
import com.reader.entity.storage.Website;
import com.reader.net.webpage.AccessLoginPermission;
import com.reader.storage.DataStorage;
import com.reader.storage.common.impl.ObjectDepository;
import com.reader.ui.util.NotificationUtil;
import com.reader.util.IdGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class AddWebsiteView {
    // 左侧组件
    @FXML
    private ListView<Website> websiteListView;
    @FXML
    private Button addWebsiteBtn;

    // 右侧组件
    // 添加表单组件
    @FXML
    private GridPane addFormPane;
    @FXML
    private TextField urlInputField;
    @FXML
    private TextField nameInputField;
    @FXML
    private Button confirmAddBtn;

    // 详情面板组件
    @FXML
    private VBox detailPanel;
    @FXML
    private TextField nameDisplayTextField;
    @FXML
    private TextField urlDisplayTextField;
    @FXML
    private Button changeBtn;
    @FXML
    private Button getLoginStatusOnDetail;
    @FXML
    private Button saveBtn;
    @FXML
    private Button deleteBtn;
    // 新增的登录状态显示组件
    @FXML
    private ListView<String> cookieListView;
    @FXML
    private ListView<String> localStorageListView;

    private final ObservableList<Website> websiteList = FXCollections.observableArrayList();
    private final ObjectDepository websiteDepository = DataStorage.WEBSITE_DEPOSITORY;

    @FXML
    public void initialize() {
        setupInitialData();
        setupInitialUi();
        setupListView();
        setupEventHandlers();
    }

    private void setupInitialData() {
        websiteDepository.getAll().forEach((_, value) -> websiteList.add((Website) value));
    }

    private void setupInitialUi() {
        addFormPane.setVisible(false);
        detailPanel.setVisible(false);
    }

    private void setupListView() {
        websiteListView.setItems(websiteList);
        websiteListView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Website item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });
    }

    private void setupEventHandlers() {
        // 添加新网站按钮
        addWebsiteBtn.setOnAction(_ -> {
            addFormPane.setVisible(true);
            detailPanel.setVisible(false);
            websiteListView.getSelectionModel().clearSelection();
            clearInputFields();
        });
        // 确认添加按钮
        confirmAddBtn.setOnAction(_ -> handleAddWebsite());
        // 列表选择监听
        websiteListView.getSelectionModel().selectedItemProperty().addListener((_, _, newVal) -> {
            if (newVal != null) {
                showDetailPanel(newVal);
                addFormPane.setVisible(false);
                detailPanel.setVisible(true);
            }
            else {
                detailPanel.setVisible(false);
            }
        });
        //点击修改按钮
        changeBtn.setOnAction(_ -> {
            if (getLoginStatusOnDetail.isVisible()) {
                getLoginStatusOnDetail.setVisible(false);
                saveBtn.setVisible(false);
                nameDisplayTextField.setEditable(false);
                urlDisplayTextField.setEditable(false);
                changeBtn.setText("修改");
            }
            else {
                getLoginStatusOnDetail.setVisible(true);
                saveBtn.setVisible(true);
                nameDisplayTextField.setEditable(true);
                urlDisplayTextField.setEditable(true);
                changeBtn.setText("退出修改");
            }
        });
        // 更新XPath按钮
        getLoginStatusOnDetail.setOnAction(_ -> {
            String url = urlDisplayTextField.getText().trim();
            getXpathRuleAsync(url, loginStatus -> websiteListView.getSelectionModel().getSelectedItem().setLoginStatus(loginStatus));
            updateLoginStatusDisplay(websiteListView.getSelectionModel().getSelectedItem().getLoginStatus());
        });
        saveBtn.setOnAction(_ -> handleUpdateWebsite());
        // 新增的删除按钮事件
        deleteBtn.setOnAction(_ -> handleDeleteWebsite());
        deleteBtn.setFocusTraversable(false);
    }

    private void handleAddWebsite() {
        Website newWebsite = new Website(
                IdGenerator.generateIdFromUrl(urlInputField.getText().trim()),
                urlInputField.getText().trim(),
                nameInputField.getText().trim(),
                null
        );
        if (newWebsite.getName().isEmpty() || newWebsite.getUrl().isEmpty()) {
            NotificationUtil.showError("网站名称和网址不能为空！", NotificationUtil.NotificationPosition.TopRight);
            return;
        }
        websiteList.add(newWebsite);
        websiteDepository.add(newWebsite.getId(), newWebsite);
        addFormPane.setVisible(false);
        clearInputFields();
    }

    private void handleUpdateWebsite() {
        Website selectedWebsite = websiteListView.getSelectionModel().getSelectedItem();
        if (selectedWebsite == null) {
            NotificationUtil.showError("请先选择要更新的网站！");
            return;
        }
        String newName = nameDisplayTextField.getText().trim();
        if (newName.isEmpty()) {
            NotificationUtil.showError("网站名称不能为空！");
            return;
        }
        String newUrl = urlDisplayTextField.getText().trim();
        if (newUrl.isEmpty()) {
            NotificationUtil.showError("网址不能为空！");
            return;
        }
        LoginStatus loginStatus = selectedWebsite.getLoginStatus();
        // 更新所有同id的XPath
        String targetId = selectedWebsite.getId();
        websiteList.stream()
                .filter(website -> website.getId().equals(targetId))
                .forEach(website -> {
                    website.setName(newName);
                    website.setUrl(newUrl);
                    website.setLoginStatus(loginStatus);
                });
        updateLoginStatusDisplay(loginStatus);
        websiteDepository.update(targetId, selectedWebsite);
        NotificationUtil.showSuccess("已保存修改！");
    }

    private void handleDeleteWebsite() {
        Website selectedWebsite = websiteListView.getSelectionModel().getSelectedItem();
        if (selectedWebsite == null) {
            NotificationUtil.showError("请先选择要删除的网站！");
            return;
        }
        // 从数据源删除
        websiteDepository.delete(selectedWebsite.getId());
        // 从列表删除
        websiteList.remove(selectedWebsite);
        // 重置UI状态
        detailPanel.setVisible(false);
        websiteListView.getSelectionModel().clearSelection();
        NotificationUtil.showSuccess("已成功删除网站配置！");
    }

    private void clearInputFields() {
        urlInputField.clear();
        nameInputField.clear();
    }

    private void showDetailPanel(Website website) {
        nameDisplayTextField.setText(website.getName());
        urlDisplayTextField.setText(website.getUrl());
        // 更新登录状态显示
        updateLoginStatusDisplay(website.getLoginStatus());
    }

    private void updateLoginStatusDisplay(LoginStatus loginStatus) {
        ObservableList<String> cookieItems = FXCollections.observableArrayList();
        ObservableList<String> localStorageItems = FXCollections.observableArrayList();

        if (loginStatus != null) {
            // 添加cookie信息
            if (loginStatus.getCookieStorageList() != null) {
                loginStatus.getCookieStorageList().forEach(cookie ->
                        cookieItems.add(cookie.toString())
                );
            }

            // 添加localStorage信息
            if (loginStatus.getLocalStorageMap() != null) {
                loginStatus.getLocalStorageMap().forEach((key, value) ->
                        localStorageItems.add(key + " = " + value)
                );
            }
        }

        cookieListView.setItems(cookieItems);
        localStorageListView.setItems(localStorageItems);
    }

    private void getXpathRuleAsync(String url, Consumer<LoginStatus> onSuccess) {
        Task<LoginStatus> task = new Task<>() {
            @Override
            protected LoginStatus call() {
                AccessLoginPermission accessLoginPermission = new AccessLoginPermission(url);
                return accessLoginPermission.start();
            }
        };
        task.setOnSucceeded(_ -> onSuccess.accept(task.getValue()));
        new Thread(task).start();
    }
}