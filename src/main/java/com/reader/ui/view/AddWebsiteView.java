package com.reader.ui.view;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.scene.control.Label;

/**
 * @author      ：李冠良
 * @description ：无描述
 * @date        ：2025 3月 11 16:01
 */

public class NotificationUtil {
    private static final String ERROR_COLOR = "#ff4444dd";
    private static final String SUCCESS_COLOR = "#00cc00dd";
    private static final String WARNING_COLOR = "#ffcc00dd";

    // 新增枚举类来预置位置
    public enum NotificationPosition {
        TopLeft((_, _) -> new double[]{30, 30}),
        TopCenter((root, notification) -> new double[]{root.getWidth() / 2 - notification.getWidth() / 2, 30}),
        TopRight((root, notification) -> new double[]{root.getWidth() - notification.getWidth() - 30, 30}),
        Right((root, notification) -> new double[]{root.getWidth() - notification.getWidth() - 30, root.getHeight() / 2 - notification.getHeight() / 2}),
        BottomRight((root, notification) -> new double[]{root.getWidth() - notification.getWidth() - 30, root.getHeight() - notification.getHeight() - 30}),
        BottomCenter((root, notification) -> new double[]{root.getWidth() / 2 - notification.getWidth() / 2, root.getHeight() - notification.getHeight() - 30}),
        BottomLeft((root, notification) -> new double[]{30, root.getHeight() - notification.getHeight() - 30}),
        Left((root, notification) -> new double[]{30, root.getHeight() / 2 - notification.getHeight() / 2});

        private final PositionCalculator calculator;

        NotificationPosition(PositionCalculator calculator) {
            this.calculator = calculator;
        }

        public double[] calculatePosition(Pane root, Label notification) {
            return calculator.calculate(root, notification);
        }
    }

    // 定义一个函数式接口来计算位置
    @FunctionalInterface
    interface PositionCalculator {
        double[] calculate(Pane root, Label notification);
    }

    // 新增重载方法，可传入位置参数，默认为 TopCenter
    public static void showError(String message) {
        showError(message, NotificationPosition.TopCenter);
    }

    public static void showError(String message, NotificationPosition position) {
        createAndShowNotification(message, ERROR_COLOR, position);
    }

    public static void showSuccess(String message) {
        showSuccess(message, NotificationPosition.TopCenter);
    }

    public static void showSuccess(String message, NotificationPosition position) {
        createAndShowNotification(message, SUCCESS_COLOR, position);
    }

    public static void showWarning(String message) {
        showWarning(message, NotificationPosition.TopCenter);
    }

    public static void showWarning(String message, NotificationPosition position) {
        createAndShowNotification(message, WARNING_COLOR, position);
    }

    private static void createAndShowNotification(String message, String backgroundColor, NotificationPosition position) {
        Window focusedWindow = Stage.getWindows().stream()
                .filter(Window::isFocused)
                .findFirst()
                .orElse(null);
        if (!(focusedWindow instanceof Stage)) return;

        Scene scene = focusedWindow.getScene();
        if (scene == null) return;

        Pane root = (Pane) scene.getRoot();
        if (root == null) return;

        StackPane notificationContainer = findOrCreateNotificationContainer(root);

        Label notification = new Label(message);
        notification.setMinWidth(Label.USE_PREF_SIZE);
        notification.setMaxWidth(Double.MAX_VALUE);
        notification.setStyle(
                "-fx-background-color: " + backgroundColor + ";" +
                        "-fx-background-radius: 15;" +
                        "-fx-padding: 10 20;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);"
        );

        notification.setOpacity(0);
        notificationContainer.getChildren().add(notification);

        // 根据位置参数调整通知的位置
        double[] pos = position.calculatePosition(root, notification);
        notification.setTranslateX(pos[0]);
        notification.setTranslateY(pos[1]);

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

    private static StackPane findOrCreateNotificationContainer(Pane root) {
        // 查找现有容器
        return root.getChildren().stream()
                .filter(node -> node instanceof StackPane && "notificationContainer".equals(node.getId()))
                .map(node -> (StackPane) node)
                .findFirst()
                .orElseGet(() -> {
                    StackPane container = new StackPane();
                    container.setId("notificationContainer");
                    container.setAlignment(Pos.TOP_CENTER);
                    container.setMouseTransparent(true);
                    container.setPickOnBounds(false);
                    root.getChildren().add(container);
                    return container;
                });
    }
}