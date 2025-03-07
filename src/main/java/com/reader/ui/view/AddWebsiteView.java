// AddWebsiteView.java
package com.reader.ui.view;

import com.reader.entity.storage.Website;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AddWebsiteView {

    // 左侧组件
    @FXML private ListView<Website> websiteListView;
    @FXML private Button btnAddNew;

    // 右侧添加面板组件
    @FXML private TextField tfUrl;
    @FXML private TextField tfName;
    @FXML private Button btnConfirmAdd;

    // 右侧详情面板组件
    @FXML private Label lblWebsiteName;
    @FXML private Label lblWebsiteUrl;
    @FXML private TextField tfXpath;
    @FXML private Button btnUpdateXpath;

    private ObservableList<Website> websiteList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupListView();
        setupEventHandlers();
//        loadInitialData();
    }

    private void setupListView() {
        websiteListView.setItems(websiteList);
        websiteListView.setCellFactory(lv -> new ListCell<Website>() {
            @Override
            protected void updateItem(Website item, boolean empty) {
                super.updateItem(item, empty);
//                setText(empty ? null : item.getName());
            }
        });
    }

    private void setupEventHandlers() {
        // 添加新网站按钮
//        btnAddNew.setOnAction(e -> showAddForm());

        // 确认添加按钮
        btnConfirmAdd.setOnAction(e -> handleAddWebsite());

        // 列表选择监听
        websiteListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
//            if (newVal != null) showDetailPanel(newVal);
        });

        // 更新XPath按钮
        btnUpdateXpath.setOnAction(e -> updateXpathRule());
    }

    private void handleAddWebsite() {
        Website website = new Website(
                tfUrl.getText().trim(),
                tfName.getText().trim(),
                "" // 初始xpath
        );

        websiteList.add(website);
//        clearAddForm();

        // 数据存储伪代码
        // DataStorage.saveWebsite(website);
    }

    private void updateXpathRule() {
        Website selected = websiteListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // 调用XPath获取逻辑
//            String newXpath = XpathFetcher.fetchXpath(selected.getUrl());
//            selected.setXpath(newXpath);
//            tfXpath.setText(newXpath);

            // 数据存储伪代码
            // DataStorage.updateXpath(selected);
        }
    }

    // 其他辅助方法...
}