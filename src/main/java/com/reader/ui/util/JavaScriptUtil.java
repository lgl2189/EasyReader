package com.reader.ui.util;

import com.reader.webpage.access.AccessWebPage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * @author ：李冠良
 * @description ：无描述
 * @date ：2025 3月 25 08:55
 */


public class JavaScriptUtil {

    /**
     * 加载脚本文件
     * @param scriptPath 脚本文件路径
     * @return 脚本内容
     * @throws IOException 加载脚本时发生IO异常
     */
    public static String loadScript(String scriptPath) {
        if (scriptPath == null || scriptPath.isEmpty()) {
            throw new NullPointerException("脚本路径不能为空");
        }
        try (InputStream inputStream = AccessWebPage.class.getResourceAsStream(scriptPath)) {
            if (inputStream == null) {
                throw new NullPointerException("加载脚本失败: " + scriptPath);
            }
            StringBuilder script;
            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                script = new StringBuilder();
                int data;
                while ((data = reader.read()) != -1) {
                    script.append((char) data);
                }
            }
            return script.toString();
        }
        catch (IOException e) {
            throw new RuntimeException("加载脚本时发生IO异常", e);
        }
    }

}