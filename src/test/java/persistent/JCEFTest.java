package persistent;

import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLifeSpanHandlerAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author      ：李冠良
 * @description ：无描述
 * @date        ：2025 2月 19 21:07
 */


public class JCEFTest {
    public static void main(String[] args) {
        // 初始化CefSettings
        CefSettings settings = new CefSettings();
        // 初始化CefApp（全局唯一）
        CefApp cefApp = CefApp.getInstance(settings);
        // 创建CefClient（可以有多个）
        CefClient client = cefApp.createClient();
        // 添加生命周期处理器以处理新窗口事件等
        client.addLifeSpanHandler(new CefLifeSpanHandlerAdapter() {
            @Override
            public boolean onBeforePopup(CefBrowser browser, CefFrame frame, String target_url, String target_frame_name) {
                // 在这里阻止弹出新窗口，并在当前窗口打开链接
                browser.loadURL(target_url);
                return true; // 返回true表示已处理该事件
            }
        });

        // 创建一个浏览器实例
        String startUrl = "https://www.baidu.com"; // 你想要加载的起始页面
        CefBrowser browser = client.createBrowser(startUrl, false, false);
        // 创建Swing组件
        JFrame frame = new JFrame("JCEF Test");
        Component component = browser.getUIComponent();

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(component, BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setVisible(true);

        // 确保当关闭窗口时释放资源
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                browser.close(true); // 强制关闭浏览器实例
                CefApp.getInstance().dispose(); // 清理CefApp
            }
        });
    }
}