package temp;

import com.reader.net.webpage.AccessLoginPermission;
import com.reader.net.webpage.AccessWebPageContent;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author      ：李冠良
 * @description ：无描述
 * @date        ：2025 3月 24 11:21
 */


public class AccessLoginPermissionTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        AccessWebPageContent accessWebPageContent = new AccessLoginPermission("www.baidu.com");
        accessWebPageContent.run();
    }

    public static void main(String[] args) {
        launch(args);
    }
}