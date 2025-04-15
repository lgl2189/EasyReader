package com.reader.webpage.access;

import com.reader.entity.net.Website;
import com.reader.entity.template.BookTemplate;
import javafx.scene.control.Alert;
import javafx.scene.web.WebEngine;
import javafx.stage.WindowEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author ：李冠良
 * @description ：无描述
 * @date ：2025 4月 02 14:42
 */


public class AccessBookTemplateXpath extends AccessWebPageWithMenu {

    private final CompletableFuture<BookTemplate> bookTemplateFuture = new CompletableFuture<>();
    private final BookTemplate bookTemplate = new BookTemplate();

    /**
     * 构造函数，初始化输入的 URL 并进行必要的初始化操作。
     *
     * @param website 要访问的网站的相关信息，将使用url字段作为访问的地址。
     * @see #init(String)
     */
    public AccessBookTemplateXpath(Website website) {
        super(website.getUrl());
    }

    public BookTemplate start() {
        addBookTemplateMenuItem();
        run();
        try {
            return bookTemplateFuture.get();
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void addBookTemplateMenuItem() {
        addMenuItemForUiOperator("生成书名", bookTemplate::setBookNameXpath);
        addMenuItemForUiOperator("生成作者", bookTemplate::setAuthorXpath);
        addMenuItemForUiOperator("生成出版社", bookTemplate::setPublisherXpath);
        addMenuItemForUiOperator("生成ISBN", bookTemplate::setIsbnXpath);
        addMenuItemForUiOperator("生成语言", bookTemplate::setLanguageXpath);
        addMenuItemForUiOperator("生成描述", bookTemplate::setDescriptionXpath);
        addMenuItemForUiOperator("生成是否完结", bookTemplate::setIsFinishedXpath);
    }

    @Override
    protected void doWithWebView(ContextWrapper context) {
        super.doWithWebView(context);
        WebEngine webEngine = context.webView().getEngine();
        // 设置 alert 处理器
        webEngine.setOnAlert(event -> {
            // 获取 alert 的内容
            String message = event.getData();
            System.out.println(message);
            // 可以用 JavaFX Alert 显示
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("JavaScript Alert");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @Override
    protected void onCloseStage(WindowEvent event, ContextWrapper context) {
        super.onCloseStage(event, context);
        bookTemplateFuture.complete(bookTemplate);
    }

    @Override
    protected JavaBridge setJavaBridgeObject() {
        return new JavaBridge();
    }
}