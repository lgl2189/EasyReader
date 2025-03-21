package com.reader.ui.view;

import com.reader.entity.storage.Website;
import com.reader.entity.util.UrlBasedIdGenerator;
import com.reader.net.AccessXpath;
import com.reader.storage.DataStorage;
import com.reader.storage.common.impl.ObjectDepository;
import com.reader.ui.util.NotificationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

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
    private TextField xpathInputField;
    @FXML
    private Button confirmAddBtn;
    @FXML
    private Button getXpathBtn;

    // 详情面板组件
    @FXML
    private VBox detailPanel;
    @FXML
    private Label nameDisplayLabel;
    @FXML
    private Label urlDisplayLabel;
    @FXML
    private TextField xpathDisplayField;
    @FXML
    private Button updateXpathBtn;
    @FXML
    private Button updateXpathBtnByUI;

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
        // 获取XPath按钮
        getXpathBtn.setOnAction(_ -> {
            String xpath = getXpathRule(urlInputField.getText().trim());
            xpathInputField.setText(xpath);
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
        // 更新XPath按钮
        updateXpathBtnByUI.setOnAction(_ -> xpathDisplayField.setText(getXpathRule(
                urlDisplayLabel.getText().trim())
        ));
        updateXpathBtn.setOnAction(_ -> handleUpdateWebsite());

    }

    private void clearInputFields() {
        urlInputField.clear();
        nameInputField.clear();
        xpathInputField.clear();
    }

    private void handleAddWebsite() {
        Website newWebsite = new Website(
                UrlBasedIdGenerator.generateId(urlInputField.getText().trim()),
                urlInputField.getText().trim(),
                nameInputField.getText().trim(),
                xpathInputField.getText().trim()
        );
        if (newWebsite.getName().isEmpty() || newWebsite.getUrl().isEmpty() || newWebsite.getXpath().isEmpty()) {
            NotificationUtil.showError("网站名称、网址和XPath不能为空！", NotificationUtil.NotificationPosition.TopRight);
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
        // 获取用户输入的新XPath（假设通过xpathDisplayField修改）
        String newXpath = xpathDisplayField.getText().trim();
        if (newXpath.isEmpty()) {
            NotificationUtil.showError("XPath不能为空！");
            return;
        }
        // 更新所有同id的XPath
        String targetId = selectedWebsite.getId();
        websiteList.stream()
                .filter(website -> website.getId().equals(targetId))
                .forEach(website -> website.setXpath(newXpath));
        NotificationUtil.showSuccess("已更新网站的XPath！");
        websiteDepository.update(targetId, selectedWebsite);
    }

    private void showDetailPanel(Website website) {
        nameDisplayLabel.setText(website.getName());
        urlDisplayLabel.setText(website.getUrl());
        xpathDisplayField.setText(website.getXpath());
    }

    private String getXpathRule(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        AccessXpath accessXpath = new AccessXpath(url);
        accessXpath.execute();
        return accessXpath.getXpath();
    }
}