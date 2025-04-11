package persistent.access;

import com.reader.entity.net.CookieStorage;
import com.reader.entity.net.LoginStatus;
import com.reader.webpage.access.AccessLoginPermission;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author ：李冠良
 * @description ：无描述
 * @date ：2025 3月 24 11:21
 */


public class AccessLoginPermissionTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        getAsync(loginStatus -> {
            List<CookieStorage> cookieStorageList = loginStatus.getCookieStorageList();
            Map<String, Object> localStorageMap = loginStatus.getLocalStorageMap();
            System.out.println("cookie:\n" + cookieStorageList);
            System.out.println("localStorage:\n" + localStorageMap);
        });
    }

    private void getAsync(Consumer<LoginStatus> onSuccess) {
        Task<LoginStatus> task = new Task<>() {
            @Override
            protected LoginStatus call() {
                AccessLoginPermission accessLoginPermission = new AccessLoginPermission("https://bilibili.com/");
                return accessLoginPermission.start();
            }
        };
        task.setOnSucceeded(_ -> onSuccess.accept(task.getValue()));
        new Thread(task).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}