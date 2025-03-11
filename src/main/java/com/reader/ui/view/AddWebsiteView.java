package com.reader.ui.view;

import com.reader.entity.storage.Website;
import com.reader.entity.util.UrlBasedIdGenerator;
import com.reader.storage.DataStorage;
import com.reader.storage.common.impl.ObjectDepository;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class AddWebsiteView {
    // 悬浮提示容器
    @FXML
    private StackPane notificationContainer;
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

    private final ObservableList<Website> websiteList = FXCollections.observableArrayList();
    private final ObjectDepository websiteDepository = DataStorage.WEBSITE_DEPOSITORY;

    @FXML
    public void initialize() {
        setupInitialData();
        setupInitialUi();
        setupListView();
        setupEventHandlers();
        setupNotificationSystem(); // 新增初始化方法
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
            String xpath = getXpathRule();
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
        updateXpathBtn.setOnAction(_ -> getXpathRule());
    }

    // 新增通知系统初始化
    private void setupNotificationSystem() {
        notificationContainer.setMouseTransparent(true); // 允许点击穿透
        notificationContainer.setPickOnBounds(false); // 不拦截边界内事件
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
            showErrorNotification("网站名称、网址和XPath不能为空！");
            return;
        }
        if (websiteList.stream().anyMatch(w -> w.getName().equals(newWebsite.getName()))) {
            showErrorNotification("网站名称已存在，不能使用相同名称！");
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
            showErrorNotification("请先选择要更新的网站！");
            return;
        }
        // 获取用户输入的新XPath（假设通过xpathDisplayField修改）
        String newXpath = xpathDisplayField.getText().trim();
        if (newXpath.isEmpty()) {
            showErrorNotification("XPath不能为空！");
            return;
        }
        // 更新所有同名网站的XPath
        String targetId = selectedWebsite.getId();
        websiteList.stream()
                .filter(website -> website.getId().equals(targetId))
                .forEach(website -> website.setXpath(newXpath));
        showErrorNotification("已更新网站的XPath！");
    }

    private void showErrorNotification(String message) {
        Label notification = new Label(message);
        notification.setStyle("-fx-background-color: #ff4444dd; -fx-background-radius: 15;"
                + "-fx-padding: 10 20; -fx-text-fill: white; -fx-font-size: 14;"
                + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // 设置初始状态
        notification.setOpacity(0);
        notificationContainer.getChildren().add(notification);

        // 定位到顶部居中
        notification.translateYProperty().bind(
                notificationContainer.heightProperty()
                        .subtract(notification.heightProperty())
                        .divide(2).subtract(30)
        );

        // 动画序列
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), notification);
        fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), notification);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(_ -> notificationContainer.getChildren().remove(notification));

        fadeIn.setOnFinished(_ -> pause.play());
        pause.setOnFinished(_ -> fadeOut.play());
        fadeIn.play();
    }

    private void showDetailPanel(Website website) {
        nameDisplayLabel.setText(website.getName());
        urlDisplayLabel.setText(website.getUrl());
        xpathDisplayField.setText(website.getXpath());
    }

    private String getXpathRule() {
//        Website selected = websiteListView.getSelectionModel().getSelectedItem();
//        if (selected != null) {
//            // 调用XPath获取逻辑示例
//             String newXpath = XpathService.fetchXpath(selected.getUrl());
//             selected.setXpath(newXpath);
//             xpathDisplayField.setText(newXpath);
//        }
        return "xpath rule here";
    }
}