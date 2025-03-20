package persistent;

import com.reader.net.XPathGenerator;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.control.Button;

public class XPathGeneratorTest extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("XPath 测试程序");

        Button singleXPathButton = new Button("生成单个 XPath");
        Button listXPathButton = new Button("生成多个 XPath");

        singleXPathButton.setOnAction(e -> testSingleXPath());
        listXPathButton.setOnAction(e -> testListXPath());

        VBox root = new VBox(10, singleXPathButton, listXPathButton);
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void testSingleXPath() {
        String url = "https://www.baidu.com"; // 替换为实际测试页面 URL
        new Thread(() -> {
            String xpath = XPathGenerator.getXPathForUrl(url, primaryStage.getOwner());
            System.out.println("生成的单个 XPath: " + xpath);
        }).start();
    }

    private void testListXPath() {
        String url = "https://www.baidu.com"; // 替换为实际测试页面 URL
        new Thread(() -> {
            var xpathList = XPathGenerator.getXPathListForUrl(url, primaryStage.getOwner());
            System.out.println("Final XPath List Length: " + xpathList.size());
            System.out.println("Final XPath List:\n: " + xpathList);
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}