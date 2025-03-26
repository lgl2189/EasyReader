package persistent.access;

import com.reader.net.webpage.AccessLoginPermission;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.stage.Stage;

import java.util.function.Consumer;

/**
 * @author ：李冠良
 * @description ：无描述
 * @date ：2025 3月 24 11:21
 */


public class AccessLoginPermissionTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        getAsync("https://www.baidu.com", futureWrapper -> {
            System.out.println(futureWrapper.getLocalStorage());
            System.out.println(futureWrapper.getCookie());
        });
    }

    private void getAsync(String url, Consumer<AccessLoginPermission.FutureWrapper> onSuccess) {
        Task<AccessLoginPermission.FutureWrapper> task = new Task<>() {
            @Override
            protected AccessLoginPermission.FutureWrapper call() {
                AccessLoginPermission accessLoginPermission = new AccessLoginPermission("https://www.bilibili.com/");
                AccessLoginPermission.FutureWrapper futureWrapper = accessLoginPermission.start();
                return futureWrapper;
            }
        };
        task.setOnSucceeded(_ -> onSuccess.accept(task.getValue()));
        new Thread(task).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}