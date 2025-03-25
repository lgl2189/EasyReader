package persistent.access;

import com.reader.net.webpage.AccessWebPageWithMenu;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author      ：李冠良
 * @description ：无描述
 * @date        ：2025 3月 24 11:21
 */


public class AccessLoginWithMenuTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        AccessWebPageWithMenu accessWebPageWithMenu = new AccessWebPageWithMenu("www.baidu.com");
        accessWebPageWithMenu.addMenuItem("自定义操作1", (_) -> System.out.println("自定义操作1"));
        accessWebPageWithMenu.addMenuItem("自定义操作2", (_) -> System.out.println("自定义操作2"));
        accessWebPageWithMenu.run();
    }

    public static void main(String[] args) {
        launch(args);
    }
}