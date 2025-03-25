package com.reader.net.webpage;

import com.reader.ui.util.JavaScriptUtil;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.concurrent.CompletableFuture;

/**
 * @author ：李冠良
 * @description ：访问网页内容的基类，提供基本的功能和接口。
 * 执行 {@link #run()} 方法，只会打开一个 {@link javafx.stage.Stage}，并加载指定的网页，不会进行任何其他操作。
 * 这个类提供了一些方法供子类调用，包括 {@link #injectScript(WebEngine)} 方法，
 * 可以注入 JavaScript 脚本，以实现更丰富的功能。
 * 这个类还提供了一些方法供子类重写，允许子类在生命周期中进行额外操作，
 * 以实现更复杂的功能，这些方法包括 {@link #beforeCreateStage}、
 * {@link #doWithStage}、{@link #onCloseStage} 和 {@link #afterShowStage}。
 * @date ：2025 3月 24 10:15
 */
public class AccessWebPage {
    protected static String scriptContent;
    protected String inputUrl;
    private static String stageTitle = "网页访问器";
    protected boolean isStart = false;

    /**
     * 加载指定路径的 JavaScript 脚本，需要在子类的静态代码块中调用。如果未指定脚本路径，将不注入 JavaScript 脚本。
     * 路径从当前类的资源目录开始，以 "/" 开头。
     * 如果脚本路径为空，将抛出 NullPointerException。
     * 如果加载脚本失败，将抛出 RuntimeException。
     *
     * @throws NullPointerException 如果脚本路径为空或加载脚本失败
     * @throws RuntimeException     如果加载脚本过程中出现异常
     * @see #scriptContent
     */
    protected static void loadScript(String scriptPath) {
        scriptContent = JavaScriptUtil.loadScript(scriptPath);
    }

    /**
     * 构造函数，初始化输入的 URL 并进行必要的初始化操作。
     *
     * @param inputUrl 要访问的网页的 URL
     * @see #init(String)
     */
    public AccessWebPage(String inputUrl) {
        this.inputUrl = inputUrl;
        init(inputUrl);
    }

    /**
     * 初始化方法，调用 initCommonVariable 方法进行通用变量的初始化。
     *
     * @param url 要访问的网页的 URL
     * @see #initCommonVariable(String)
     */
    protected void init(String url) {
        initCommonVariable(url);
    }

    /**
     * 初始化通用变量，确保输入的 URL 以 "https://" 开头。
     * 如果类已经启动，调用此方法可能会抛出异常（当前注释部分代码未启用该限制）。
     *
     * @param url 要访问的网页的 URL
     * @see #inputUrl
     * @see #isStart
     */
    protected void initCommonVariable(String url) {
        inputUrl = url;
        if (!inputUrl.startsWith("https://") && !inputUrl.startsWith("http://")) {
            inputUrl = "https://" + inputUrl;
        }
    }

    /**
     * 启动网页访问操作。
     * 在创建 {@link javafx.stage.Stage} 之前调用 beforeCreateStage 方法，然后在 JavaFX 应用程序线程中创建并显示 {@link javafx.stage.Stage}，
     * 最后调用 afterShowStage 方法。
     *
     * @see #beforeCreateStage()
     * @see #createStage()
     * @see #afterShowStage()
     */
    public void run() {
        beforeCreateStage();
        Platform.runLater(() -> {
            createStage();
            afterShowStage();
        });
    }

    /**
     * 创建并显示一个包含网页视图的 {@link javafx.stage.Stage}。
     * 初始化 {@link javafx.stage.Stage} 的属性，创建 {@link javafx.scene.web.WebView} 和导航按钮，加载指定的 URL，并监听页面加载状态。
     * 当页面加载成功时，注入 JavaScript 脚本。
     *
     * @see #doWithStage(Stage)
     * @see #doWithWebView(WebView)
     * @see #injectScript(WebEngine)
     * @see #inputUrl
     */
    private void createStage() {
        Stage webpageStage = new Stage();
        webpageStage.initModality(Modality.WINDOW_MODAL);
        webpageStage.setTitle(stageTitle);

        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        // 创建导航按钮
        javafx.scene.control.Button backButton = new javafx.scene.control.Button("←");
        javafx.scene.control.Button forwardButton = new javafx.scene.control.Button("→");

        // 初始化按钮状态
        backButton.setDisable(true);
        forwardButton.setDisable(true);

        // 按钮点击事件
        backButton.setOnAction(_ -> engine.getHistory().go(-1));
        forwardButton.setOnAction(_ -> engine.getHistory().go(1));

        // 监听历史变化更新按钮状态
        engine.getHistory().currentIndexProperty().addListener((_, _, newIndex) -> {
            int currentIndex = newIndex.intValue();
            backButton.setDisable(currentIndex <= 0);
            forwardButton.setDisable(currentIndex >= engine.getHistory().getEntries().size() - 1);
        });

        // 创建顶部按钮栏
        HBox buttonBar = new HBox(10, backButton, forwardButton);
        buttonBar.setPadding(new Insets(5));
        buttonBar.setStyle("-fx-background-color: #f0f0f0;");

        // 组合布局
        VBox root = new VBox(buttonBar, webView);
        root.setPrefSize(800, 600);

        engine.getLoadWorker().stateProperty().addListener((_, _, newState) -> {
            if (newState == Worker.State.SUCCEEDED && scriptContent != null && !scriptContent.isEmpty()) {
                injectScript(engine);
            }
        });

        doWithStage(webpageStage);
        doWithWebView(webView);

        engine.load(inputUrl);
        webpageStage.setScene(new Scene(root));
        webpageStage.setOnCloseRequest(_ -> onCloseStage());
        webpageStage.show();
        isStart = true;
    }

    /**
     * 设置 {@link javafx.stage.Stage} 的标题。
     * 如果 {@link javafx.stage.Stage} 已经启动，调用此方法将抛出 IllegalCallerException。
     *
     * @param title 要设置的 {@link javafx.stage.Stage} 标题
     * @throws IllegalCallerException 如果在启动 {@link javafx.stage.Stage} 后调用此方法
     * @see #stageTitle
     * @see #isStart
     */
    protected void setStageTitle(String title) {
        if (!isStart) {
            stageTitle = title;
        }
        else {
            throw new IllegalCallerException("不能在启动Stage后修改Stage标题！");
        }
    }

    /**
     * 在创建 {@link javafx.stage.Stage} 之前执行的方法，供子类重写以进行额外操作。
     *
     * @see #run()
     */
    protected void beforeCreateStage() {
    }

    /**
     * 设置 {@link javafx.stage.Stage} 的属性，供子类重写以进行额外操作。
     *
     * @param stage 要设置属性的 {@link javafx.stage.Stage}
     * @see #createStage()
     */
    protected void doWithStage(Stage stage) {
    }

    /**
     * 设置 {@link javafx.scene.web.WebView} 的属性，供子类重写以进行额外操作。
     *
     * @param webView 要设置属性的 {@link javafx.scene.web.WebView}
     */
    protected void doWithWebView(WebView webView) {

    }

    /**
     * 在 {@link javafx.stage.Stage} 关闭时执行的方法，供子类重写以进行额外操作。
     *
     * @see #createStage()
     */
    protected void onCloseStage() {
    }

    /**
     * 此方法会在 JavaFX 的 {@link javafx.stage.Stage} 成功显示之后执行。它被设计为可被子类重写，
     * 旨在让子类能够在 Stage 显示后执行额外的异步操作，例如发起网络请求、进行耗时的数据加载等。
     * <p>
     * 方法返回一个 {@link java.util.concurrent.CompletableFuture} 对象，这表明它支持异步操作。
     * 子类在重写此方法时，可以在其中进行耗时的操作，然后通过 {@link java.util.concurrent.CompletableFuture}
     * 来管理操作的结果和状态。
     *
     * @return 返回一个 {@link java.util.concurrent.CompletableFuture} 对象，该对象代表异步操作的结果。
     * 异步操作完成后，该对象会包含操作结果。如果子类重写此方法时不进行异步操作，
     * 也可以返回 null，但通常建议返回一个已完成的 {@link java.util.concurrent.CompletableFuture} 对象。
     * @implNote 默认实现返回 null。子类重写时，若有异步操作，需要创建并返回合适的
     * {@link java.util.concurrent.CompletableFuture} 对象以正确管理异步操作的结果和状态。
     * &#064;example  以下是一个子类重写此方法进行异步数据加载的示例：
     * <pre>
     * {@code
     * public class SubClass extends AccessWebPageContent {
     *     @Override
     *     protected CompletableFuture<String> afterShowStage() {
     *         return CompletableFuture.supplyAsync(() -> {
     *             // 模拟耗时的数据加载操作
     *             try {
     *                 Thread.sleep(2000);
     *             } catch (InterruptedException e) {
     *                 Thread.currentThread().interrupt();
     *             }
     *             return "Data loaded";
     *         });
     *     }
     * }
     * }
     * </pre>
     * @see #run() 此方法会在 {@link #run()} 方法的执行流程中，Stage 显示后被调用。
     */
    protected CompletableFuture<?> afterShowStage() {
        return null;
    }


    /**
     * 向 {@link javafx.scene.web.WebEngine} 供子类重写，在注入 JavaScript 脚本之前执行，以实现额外的注入逻辑。
     *
     * @param engine 要注入脚本的 {@link javafx.scene.web.WebEngine}
     * @see #createStage()
     */
    protected void beforeInjectScript(WebEngine engine) {
    }

    /**
     * 向 {@link javafx.scene.web.WebEngine} 注入 JavaScript 脚本。
     *
     * @param engine 要注入脚本的 {@link javafx.scene.web.WebEngine}
     * @see #createStage()
     */
    private void injectScript(WebEngine engine) {
        beforeInjectScript(engine);
        engine.executeScript(scriptContent);
    }

    /**
     * 用于包装 {@link java.util.concurrent.CompletableFuture} 对象，方便同时获取多个{@link java.util.concurrent.CompletableFuture}的结果。
     */
    protected interface FutureWrapper {

    }

    /**
     * 用于包装 Java 代码和 JavaScript 代码之间的通信接口。
     */
    protected interface JavaBridge {

    }
}