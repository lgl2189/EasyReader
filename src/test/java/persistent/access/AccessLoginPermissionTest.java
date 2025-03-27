package persistent.access;

import com.reader.entity.net.CookieStorage;
import com.reader.net.webpage.AccessLoginPermission;
import com.reader.util.CookieUtil;
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
        getAsync(futureWrapper -> {
            List<CookieStorage> cookieStorageList = CookieUtil.parseCookies(futureWrapper.getCookie());
            Map<String, Object> localStorageMap = CookieUtil.parseLocalStorage(futureWrapper.getLocalStorage());
            System.out.println("cookie:\n" + cookieStorageList);
            System.out.println("localStorage:\n" + localStorageMap);
        });
    }

    private void getAsync(Consumer<AccessLoginPermission.FutureWrapper> onSuccess) {
        Task<AccessLoginPermission.FutureWrapper> task = new Task<>() {
            @Override
            protected AccessLoginPermission.FutureWrapper call() {
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