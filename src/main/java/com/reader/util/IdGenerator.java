package com.reader.util;

import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

public class IdGenerator {

    /**
     * 生成唯一ID，格式：{域名}_{时间戳}_{随机字符串}
     * @param url 原始网址（如 "https://www.example.com/path"）
     * @return 唯一ID（如 "example.com_656f3a7d_k5gT9pL2"）
     */
    public static String generateId(String url) {
        // 1. 处理网址部分：提取并标准化域名
        String domain = extractDomain(url);
        // 2. 生成时间戳部分：Base64编码的当前秒级时间戳
        String timestampPart = encodeTimestamp();
        // 3. 生成随机部分：4位随机字母数字组合
        String randomPart = generateRandomString(10);
        // 拼接完整ID
        return String.join("_", domain, timestampPart, randomPart);
    }

    // 提取域名并移除协议和路径（如 "https://www.example.com/path" → "example.com"）
    private static String extractDomain(String url) {
        String cleaned = url.replaceAll("^(https?://)?(www\\.)?", "") // 移除协议和www
                .split("/")[0]                             // 取域名部分
                .replaceAll("[^a-zA-Z0-9.-]", "");         // 移除非合法字符
        return cleaned.isEmpty() ? "invalid_domain" : cleaned;
    }

    // 编码当前时间戳（秒级）为Base64短字符串（如 1700000000 → "ZvTk"）
    private static String encodeTimestamp() {
        long epochSecond = Instant.now().getEpochSecond();
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) ((epochSecond >> (8 * (3 - i))) & 0xFF);
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // 生成指定长度的随机字母数字字符串（如长度4 → "k5gT"）
    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = ThreadLocalRandom.current().nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    // 示例用法
    public static void main(String[] args) {
        String url = "https://www.example.com/path?query=123";
        System.out.println(generateId(url)); // 输出示例: example.com_ZvTk_k5gT
    }
}