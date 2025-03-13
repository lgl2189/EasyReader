package ui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

public class NotificationUtil {
    private static final String ERROR_COLOR = "#ff4444dd";
    private static final String SUCCESS_COLOR = "#00cc00dd";
    private static final String WARNING_COLOR = "#ffcc00dd";

    public enum NotificationPosition {
        TopLeft((root, notification, x, y) -> {
            double newX = Math.max(0, Math.min(x, root.getWidth() - notification.getWidth()));
            double newY = Math.max(0, Math.min(y, root.getHeight() - notification.getHeight()));
            return new double[]{newX, newY};
        }),
        TopCenter((root, notification, _, _) -> {
            double x = (root.getWidth() - notification.getWidth()) / 2;
            x = Math.max(0, Math.min(x, root.getWidth() - notification.getWidth()));
            return new double[]{x, 30};
        }),
        TopRight((root, notification, _, _) -> {
            double x = root.getWidth() - notification.getWidth();
            x = Math.max(0, x);
            return new double[]{x, 30};
        }),
        Right((root, notification, _, _) -> {
            double x = root.getWidth() - notification.getWidth() - 30;
            x = Math.max(0, x);
            double y = (root.getHeight() - notification.getHeight()) / 2;
            y = Math.max(0, Math.min(y, root.getHeight() - notification.getHeight()));
            return new double[]{x, y};
        }),
        BottomRight((root, notification, _, _) -> {
            double x = root.getWidth() - notification.getWidth() - 30;
            x = Math.max(0, x);
            double y = root.getHeight() - notification.getHeight() - 30;
            y = Math.max(0, y);
            return new double[]{x, y};
        }),
        BottomCenter((root, notification, _, _) -> {
            double x = (root.getWidth() - notification.getWidth()) / 2;
            x = Math.max(0, Math.min(x, root.getWidth() - notification.getWidth()));
            double y = root.getHeight() - notification.getHeight() - 30;
            y = Math.max(0, y);
            return new double[]{x, y};
        }),
        BottomLeft((root, notification, _, _) -> {
            double x = 30;
            x = Math.min(x, root.getWidth() - notification.getWidth());
            double y = root.getHeight() - notification.getHeight() - 30;
            y = Math.max(0, y);
            return new double[]{x, y};
        }),
        Left((root, notification, _, _) -> {
            double x = 30;
            x = Math.min(x, root.getWidth() - notification.getWidth());
            double y = (root.getHeight() - notification.getHeight()) / 2;
            y = Math.max(0, Math.min(y, root.getHeight() - notification.getHeight()));
            return new double[]{x, y};
        }),
        Custom((root, notification, x, y) -> {
            double newX = Math.max(0, Math.min(x, root.getWidth() - notification.getWidth()));
            double newY = Math.max(0, Math.min(y, root.getHeight() - notification.getHeight()));
            return new double[]{newX, newY};
        });

        private final PositionCalculator calculator;

        NotificationPosition(PositionCalculator calculator) {
            this.calculator = calculator;
        }

        public double[] calculatePosition(Region root, Label notification, double x, double y) {
            return calculator.calculate(root, notification, x, y);
        }

        @FunctionalInterface
        interface PositionCalculator {
            double[] calculate(Region root, Label notification, double x, double y);
        }
    }

    private static StackPane globalContainer; // 全局唯一通知容器

    public static void showError(String message) {
        showError(message, NotificationPosition.TopCenter);
    }

    public static void showError(String message, NotificationPosition position) {
        createAndShowNotification(message, ERROR_COLOR, position, 0, 0);
    }

    public static void showError(String message, double x, double y) {
        createAndShowNotification(message, ERROR_COLOR, NotificationPosition.Custom, x, y);
    }

    public static void showSuccess(String message) {
        showSuccess(message, NotificationPosition.TopCenter);
    }

    public static void showSuccess(String message, NotificationPosition position) {
        createAndShowNotification(message, SUCCESS_COLOR, position, 0, 0);
    }

    public static void showSuccess(String message, double x, double y) {
        createAndShowNotification(message, SUCCESS_COLOR, NotificationPosition.Custom, x, y);
    }

    public static void showWarning(String message) {
        showWarning(message, NotificationPosition.TopCenter);
    }

    public static void showWarning(String message, NotificationPosition position) {
        createAndShowNotification(message, WARNING_COLOR, position, 0, 0);
    }

    public static void showWarning(String message, double x, double y) {
        createAndShowNotification(message, WARNING_COLOR, NotificationPosition.Custom, x, y);
    }

    private static void createAndShowNotification(String message, String backgroundColor,
                                                  NotificationPosition position, double x, double y) {
        Platform.runLater(() -> {
            Window window = Stage.getWindows().stream()
                    .filter(Window::isFocused)
                    .findFirst()
                    .orElse(null);
            if (!(window instanceof Stage)) return;

            Scene scene = window.getScene();
            if (scene == null) return;

            // 获取场景根节点并初始化容器
            Parent root = scene.getRoot();
            initGlobalContainer(root);

            showNotification(message, backgroundColor, position, x, y, root, globalContainer);
        });
    }

    /**
     * 初始化全局通知容器（确保只创建一次）
     */
    private static void initGlobalContainer(Parent root) {
        if (globalContainer != null) return;

        globalContainer = new StackPane();
        globalContainer.setId("globalNotificationContainer");
        globalContainer.setAlignment(Pos.TOP_CENTER);
        globalContainer.setMouseTransparent(true); // 允许鼠标穿透
        globalContainer.setPickOnBounds(false);    // 不响应边界外事件

        // 将容器添加到场景根节点的最上层
        if (root instanceof BorderPane borderPane) {
            // BorderPane特殊处理：创建新的StackPane包裹原有内容
            StackPane contentWrapper = new StackPane();
            contentWrapper.getChildren().add(borderPane.getCenter());
            borderPane.setCenter(contentWrapper);
            contentWrapper.getChildren().add(globalContainer);
        }
        else if (root instanceof Pane) {
            ((Pane) root).getChildren().add(globalContainer);
        }
    }

    private static void showNotification(String message, String color, NotificationPosition position,
                                         double x, double y, Parent root, StackPane container) {
        container.getChildren().clear();

        Label notification = new Label(message);
        configureLabelStyle(notification, color, root);
        container.getChildren().add(notification);

        // 强制布局计算
        notification.applyCss();
        notification.layout();

        positionNotification(position, x, y, root, notification);
        setupAnimations(notification, container);
    }

    private static void configureLabelStyle(Label label, String color, Parent root) {
        label.setWrapText(true);
        label.setMaxWidth(root.getLayoutBounds().getWidth() * 0.8);
        label.setStyle("-fx-background-color: " + color + ";"
                + "-fx-background-radius: 15;"
                + "-fx-padding: 10 20;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 14;"
                + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        label.setOpacity(0);
    }

    private static void positionNotification(NotificationPosition position, double x, double y,
                                             Parent root, Label notification) {
        if (root instanceof Region region) {
            // 动态计算可用区域
            double maxWidth = region.getWidth() * 0.8;
            notification.setMaxWidth(maxWidth);

            // 获取正确的位置
            double[] pos = position.calculatePosition(region, notification, x, y);
            pos[0] = Math.max(0, Math.min(pos[0], region.getWidth() - notification.getWidth()));
            pos[1] = Math.max(0, Math.min(pos[1], region.getHeight() - notification.getHeight()));

            notification.relocate(pos[0], pos[1]);
        }
    }

    private static void setupAnimations(Label notification, StackPane container) {
        notification.setOpacity(1); // 重置透明度
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), notification);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), notification);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(_ -> container.getChildren().remove(notification));

        fadeIn.setOnFinished(_ -> pause.play());
        pause.setOnFinished(_ -> fadeOut.play());
        fadeIn.play();
    }
}