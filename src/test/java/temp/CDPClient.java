package temp;

import com.google.gson.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class CDPClient {

    private static final String CHROME_DRIVER_PATH;
    private static final String CHROME_PATH;

    static {
        CHROME_DRIVER_PATH = System.getProperty("user.dir")
                + File.separator + "lib"
                + File.separator + "selenium"
                + File.separator + "ChromeDriver121.0.6156.2"
                + File.separator + "chromedriver.exe";
        CHROME_PATH = System.getProperty("user.dir")
                + File.separator + "lib"
                + File.separator + "selenium"
                + File.separator + "Chrome121.0.6156.2"
                + File.separator + "chrome.exe";
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
    }

    private static final Gson gson = new Gson();
    private static final String DEBUG_URL = "http://localhost:9222/json";

    public static void main(String[] args) throws Exception {
        // 启动 Chrome 进程
        Process chrome = startChrome();
        // 等待 Chrome 调试端口就绪（最大等待 30 秒）
        waitForChromeReady(30);
        // 获取所有 Target 信息
        JsonArray targets = getTargets();
        for (JsonElement target : targets) {
            String wsUrl = target.getAsJsonObject().get("webSocketDebuggerUrl").getAsString();
            connectToTarget(wsUrl);
        }
    }

    private static Process startChrome() throws IOException {
        return new ProcessBuilder(
                CHROME_PATH,
                "--remote-debugging-port=9222",
                "--user-data-dir=./chrome-profile",
                "--no-first-run",
                "--no-default-browser-check"
        ).redirectErrorStream(true).start();
    }

    private static void waitForChromeReady(int maxWaitSeconds) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < maxWaitSeconds * 1000L) {
            try {
                URL url = new URL(DEBUG_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(1000);
                conn.setReadTimeout(1000);
                if (conn.getResponseCode() == 200) {
                    System.out.println("Chrome 调试端口已就绪");
                    return;
                }
            } catch (IOException e) {
                // 端口未就绪时忽略错误
            }
            try {
                Thread.sleep(500); // 每 0.5 秒重试一次
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        throw new RuntimeException("Chrome 启动超时，请检查路径: " + CHROME_PATH);
    }

    private static JsonArray getTargets() throws Exception {
        // 发起 HTTP 请求获取调试信息
        URL url = new URL(DEBUG_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        return gson.fromJson(reader, JsonArray.class);
    }

    private static void connectToTarget(String wsUrl) throws URISyntaxException {
        WebSocketClient client = new WebSocketClient(new URI(wsUrl)) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                // 初始化 CDP 会话
                sendCommand(1, "Page.enable", new JsonObject());
                sendCommand(2, "Runtime.enable", new JsonObject());
                injectDoubleClickScript();
            }

            @Override
            public void onMessage(String message) {
                handleCDPMessage(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("WebSocket 关闭: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }

            // 内部方法：发送 CDP 命令
            private void sendCommand(int id, String method, JsonObject params) {
                JsonObject command = new JsonObject();
                command.addProperty("id", id);
                command.addProperty("method", method);
                command.add("params", params);
                this.send(command.toString()); // 使用 WebSocketClient 的 send 方法
            }

            private void injectDoubleClickScript() {
                String script = "document.addEventListener('dblclick', e => {" +
                        "  console.log('DOUBLE_CLICK:' + e.target.outerHTML);" +
                        "});";
                JsonObject params = new JsonObject();
                params.addProperty("expression", script);
                sendCommand(3, "Runtime.evaluate", params);
            }

            private void handleCDPMessage(String message) {
                try {
                    JsonObject json = gson.fromJson(message, JsonObject.class);
                    if (json.has("method") &&
                            "Runtime.consoleAPICalled".equals(json.get("method").getAsString())) {
                        JsonObject params = json.getAsJsonObject("params");
                        String text = params.getAsJsonArray("args").get(0).getAsJsonObject()
                                .get("value").getAsString();
                        if (text.startsWith("DOUBLE_CLICK:")) {
                            System.out.println("双击元素: " + text.substring(13));
                        }
                    }
                } catch (JsonSyntaxException e) {
                    System.err.println("JSON 解析失败: " + e.getMessage());
                }
            }
        };
        client.connect();
    }
}