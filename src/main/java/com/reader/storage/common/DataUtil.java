package com.reader.storage.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ：李冠良
 * @description： 无描述
 * @date ：2024/10/4 下午4:12
 */


public class DataUtil {
    private static final Pattern INVALID_FILENAME_CHARS = Pattern.compile("[\\\\/:*?\"<>|]");
    private static final int MAX_LENGTH = 250; // 根据文件系统或应用需求设置最大长度
    private static final String REPLACEMENT_CHAR = "_";

    public static String cleanFileName(String originFileName) {
        return cleanFileName(originFileName,MAX_LENGTH,0);
    }

    public static String cleanFileName(String originFileName,int maxLength) {
        return cleanFileName(originFileName,maxLength,0);
    }

    public static String cleanFileName(String originFileName,String prefixPath) {
        return cleanFileName(originFileName,prefixPath.length(),0);
    }

    public static String cleanFileName(String originFileName,int prefixPathLength,int suffixPathLength) {
        int maxLength = MAX_LENGTH - prefixPathLength - suffixPathLength;
        if (originFileName == null || originFileName.isEmpty()) {
            throw new IllegalArgumentException("键名不能为空");
        }
        // 去除路径遍历字符，替换为下划线
        String sanitizedFilename = originFileName.replaceAll("\\.\\./", REPLACEMENT_CHAR);
        // 去除无效的文件名字符
        Matcher matcher = INVALID_FILENAME_CHARS.matcher(sanitizedFilename);
        sanitizedFilename = matcher.replaceAll(REPLACEMENT_CHAR);
        // 去除文件名开头和结尾的无效字符（如点号）
        sanitizedFilename = sanitizedFilename.trim().replaceAll("^\\.*|\\.*$", "");
        // 如果文件名长度超过限制，则直接截断
        if (sanitizedFilename.length() > maxLength ) {
            sanitizedFilename = sanitizedFilename.substring(0, maxLength);
            sanitizedFilename = sanitizedFilename.trim(); // 去除可能的尾随空格
        }
        if (sanitizedFilename.isEmpty()) {
            throw new IllegalArgumentException("键名只存在特殊字符");
        }
        return sanitizedFilename;
    }
    public static String cleanFileName(String originFileName,String prefixPath,String suffixPath) {
        return cleanFileName(originFileName,prefixPath.length(),suffixPath.length());
    }

}