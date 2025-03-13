package com.reader.ui.util;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * @author ：李冠良
 * @description： 提示类，用于显示通知信息，包括成功、错误、警告等。可以直接在程序中调用，无需额外配置。当前只有TopCenter位置可以正常使用。
 * @date ：2024/10/4 下午4:12
 */

public class NotificationUtil {
    // 颜色常量
    private static final String ERROR_COLOR = "#ff4444dd";
    private static final String SUCCESS_COLOR = "#00cc00dd";
    private static final String WARNING_COLOR = "#ffcc00dd";

    // 全局通知容器
    private static AnchorPane globalContainer;

    // 通知位置枚举
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
            // 修改这里，确保通知不会超出窗口右边界
            double x = root.getWidth() - notification.getWidth();
            x = Math.max(0, Math.min(x, root.getWidth() - notification.getWidth()));
            return new double[]{x, 30};
        }),
        Right((root, notification, _, _) -> {
            // 修改这里，确保通知不会超出窗口右边界
            double x = root.getWidth() - notification.getWidth() - 30;
            x = Math.max(0, Math.min(x, root.getWidth() - notification.getWidth()));
            double y = (root.getHeight() - notification.getHeight()) / 2;
            y = Math.max(0, Math.min(y, root.getHeight() - notification.getHeight()));
            return new double[]{x, y};
        }),
        BottomRight((root, notification, _, _) -> {
            // 修改这里，确保通知不会超出窗口右边界和下边界
            double x = root.getWidth() - notification.getWidth() - 30;
            x = Math.max(0, Math.min(x, root.getWidth() - notification.getWidth()));
            double y = root.getHeight() - notification.getHeight() - 30;
            y = Math.max(0, Math.min(y, root.getHeight() - notification.getHeight()));
            return new double[]{x, y};
        }),
        BottomCenter((root, notification, _, _) -> {
            double x = (root.getWidth() - notification.getWidth()) / 2;
            x = Math.max(0, Math.min(x, root.getWidth() - notification.getWidth()));
            double y = root.getHeight() - notification.getHeight() - 30;
            y = Math.max(0, Math.min(y, root.getHeight() - notification.getHeight()));
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

    public static void showError(String message) {
        showError(message, NotificationPosition.TopCenter);
    }

    public static void showError(String message, NotificationPosition position) {
        show(message, ERROR_COLOR, position);
    }

    public static void showSuccess(String message) {
        showSuccess(message, NotificationPosition.TopCenter);
    }

    public static void showSuccess(String message, NotificationPosition position) {
        show(message, SUCCESS_COLOR, position);
    }

    public static void showWarning(String message) {
        showWarning(message, NotificationPosition.TopCenter);
    }

    public static void showWarning(String message, NotificationPosition position) {
        show(message, WARNING_COLOR, position);
    }

    private static void show(String message, String color, NotificationPosition position) {
        Platform.runLater(() -> {
            Window window = Stage.getWindows().stream()
                    .filter(Window::isFocused)
                    .findFirst()
                    .orElse(null);

            if (!(window instanceof Stage)) return;

            Scene scene = window.getScene();
            if (scene == null) return;

            Parent root = scene.getRoot();
            initGlobalContainer(root, window); // 修改此处，传递窗口对象
            globalContainer.getChildren().clear();
            Label notification = createNotification(message, color, root);
            positionNotification(position, notification, root);
            setupAnimations(notification);
        });
    }

    // 初始化全局容器
    private static synchronized void initGlobalContainer(Parent root, Window window) {
        if (globalContainer != null) return;

        globalContainer = new AnchorPane();
        globalContainer.setId("global-notification-container");
        globalContainer.setMouseTransparent(true);
        globalContainer.setPickOnBounds(false);
        // 确保globalContainer不影响其他布局
        globalContainer.setManaged(false);

        if (root instanceof BorderPane borderPane) {
            // 将globalContainer添加到最上层
            borderPane.getChildren().add(globalContainer);
            AnchorPane.setTopAnchor(globalContainer, 0.0);
            AnchorPane.setLeftAnchor(globalContainer, 0.0);
            AnchorPane.setRightAnchor(globalContainer, 0.0);
            AnchorPane.setBottomAnchor(globalContainer, 0.0);
        }
        else if (root instanceof Pane pane) {
            // 将globalContainer添加到最上层
            pane.getChildren().add(globalContainer);
        }
    }

    // 创建通知标签
    private static Label createNotification(String message, String color, Parent root) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(calculateMaxWidth(root));
        label.setStyle(buildStyleString(color));
        label.setOpacity(0);
        return label;
    }

    // 样式构建
    private static String buildStyleString(String color) {
        return "-fx-background-color: " + color + ";" +
                "-fx-background-radius: 15;" +
                "-fx-padding: 10 20;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);";
    }

    // 动态计算最大宽度
    private static double calculateMaxWidth(Parent root) {
        return root.getLayoutBounds().getWidth() * 0.8;
    }

    // 定位通知
    private static void positionNotification(NotificationPosition position, Label notification, Parent root) {
        if (!(root instanceof Region region)) return;
        // 强制布局计算
        notification.applyCss();
        notification.layout();
        region.applyCss();
        region.layout();

        double[] pos = position.calculatePosition(region, notification, 0, 0);
        AnchorPane.setLeftAnchor(notification, pos[0]);
        AnchorPane.setTopAnchor(notification, pos[1]);
    }

    // 动画配置
    private static void setupAnimations(Label notification) {
        globalContainer.getChildren().add(notification);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), notification);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), notification);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(_ -> globalContainer.getChildren().remove(notification));

        fadeIn.setOnFinished(_ -> pause.play());
        pause.setOnFinished(_ -> fadeOut.play());
        fadeIn.play();
    }
}