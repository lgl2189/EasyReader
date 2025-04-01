// MainController.java
package com.reader.ui.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainView {

    @FXML private TabPane tabPane;
    @FXML private StackPane contentArea;

    private final Map<String, Node> viewCache = new HashMap<>();
    private final Map<String, Object> controllerCache = new HashMap<>();

    @FXML
    public void initialize() {
        setupTabs();
        setupTabListener();
        loadInitialView();
    }

    private void setupTabs() {
        addTab("首页", "/com/reader/ui/view/home-view.fxml");
        addTab("添加网站", "/com/reader/ui/view/addWebsite-view.fxml");
        addTab("添加书籍模板", "/com/reader/ui/view/addBook-view.fxml");
        addTab("下载书籍", "/com/reader/ui/view/downloadBook-view.fxml");
    }

    private void addTab(String title, String fxmlPath) {
        Tab tab = new Tab(title);
        tab.setUserData(fxmlPath);
        tabPane.getTabs().add(tab);
    }

    private void setupTabListener() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((_, _, newTab) -> {
            if (newTab != null) {
                loadView((String) newTab.getUserData());
            }
        });
    }

    private void loadInitialView() {
        if (!tabPane.getTabs().isEmpty()) {
            Tab firstTab = tabPane.getTabs().getFirst();
            loadView((String) firstTab.getUserData());
        }
    }

    private void loadView(String fxmlPath) {
        try {
            Node view = viewCache.get(fxmlPath);
            if (view == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                view = loader.load();
                viewCache.put(fxmlPath, view);
                controllerCache.put(fxmlPath, loader.getController());
            }
            else{
                if (fxmlPath.equals("/com/reader/ui/view/addBook-view.fxml")) {
                    Object controller = controllerCache.get(fxmlPath);
                    if (controller instanceof AddBookView) {
                        ((AddBookView) controller).refreshData();
                    }
                }
            }
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
            contentArea.getChildren().setAll(createErrorView("加载视图失败: " + fxmlPath));
        }
    }

    private Node createErrorView(String message) {
        javafx.scene.control.Label label = new javafx.scene.control.Label(message);
        label.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
        return label;
    }
}