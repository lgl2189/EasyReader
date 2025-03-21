package com.reader.net;

import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author      ：李冠良
 * @description ：无描述
 * @date        ：2025 2月 26 15:57
 */


public class AccessXpath {
    private final List<String> urlList;
    private String xpath;
    private boolean isSingleElement = false;

    public AccessXpath(String url, boolean isSingleElement) {
        this(url);
        this.isSingleElement = isSingleElement;
    }

    public AccessXpath(List<String> urlList, boolean isSingleElement) {
        this(urlList);
        this.isSingleElement = isSingleElement;
    }

    public AccessXpath(String url) {
        this.urlList = new ArrayList<>();
        this.urlList.add(url);
    }

    public AccessXpath(List<String> urlList) {
        this.urlList = urlList;
    }

    public void execute() {
        CompletableFuture.runAsync(() -> {
            List<String> xpathList = new ArrayList<>();
            for (String url : this.urlList) {
                if (isSingleElement) {
                    String resultStr = XPathGenerator.getXPathForUrl(url);
                    if (resultStr != null) {
                        xpathList.add(resultStr);
                    }
                }
                else {
                    List<String> resultList = XPathGenerator.getXPathListForUrl(url);
                    if (resultList != null) {
                        xpathList.addAll(resultList);
                    }
                }
            }
            this.xpath = analyze(xpathList);
        });
    }

    private String analyze(List<String> xpathList) {
        if (xpathList.isEmpty()) {
            return "";
        }

        if (xpathList.size() == 1) {
            return xpathList.getFirst();
        }

        // 找出公共前缀
        String commonPrefix = findCommonPrefix(xpathList);

        // 统计每个 XPath 剩余部分的属性信息
        List<String> remainingParts = new ArrayList<>();
        for (String xpath : xpathList) {
            remainingParts.add(xpath.substring(commonPrefix.length()));
        }

        // 尝试根据优先级生成更精确的 XPath
        String additionalLocator = findBestLocator(remainingParts);
        if (!additionalLocator.isEmpty()) {
            return commonPrefix + additionalLocator;
        }

        // 如果没有找到更精确的定位信息，返回第一个 XPath
        return xpathList.getFirst();
    }

    private String findCommonPrefix(List<String> xpathList) {
        String[] partsArray = new String[xpathList.size()];
        int minLength = Integer.MAX_VALUE;
        for (int i = 0; i < xpathList.size(); i++) {
            partsArray[i] = xpathList.get(i);
            minLength = Math.min(minLength, partsArray[i].length());
        }

        StringBuilder commonPrefix = new StringBuilder();
        for (int i = 0; i < minLength; i++) {
            char currentChar = partsArray[0].charAt(i);
            for (int j = 1; j < partsArray.length; j++) {
                if (partsArray[j].charAt(i) != currentChar) {
                    return commonPrefix.toString();
                }
            }
            commonPrefix.append(currentChar);
        }
        return commonPrefix.toString();
    }

    private String findBestLocator(List<String> remainingParts) {
        // 统计每个属性的出现次数
        Map<String, Integer> idCount = new HashMap<>();
        Map<String, Integer> classCount = new HashMap<>();
        Map<String, Integer> attrCount = new HashMap<>();

        for (String part : remainingParts) {
            String[] steps = part.split("/");
            for (String step : steps) {
                if (step.contains("@id=")) {
                    String id = extractAttributeValue(step, "@id=");
                    idCount.put(id, idCount.getOrDefault(id, 0) + 1);
                }
                else if (step.contains("@class=")) {
                    String className = extractAttributeValue(step, "@class=");
                    classCount.put(className, classCount.getOrDefault(className, 0) + 1);
                }
                else if (step.contains("@")) {
                    String[] attrParts = step.split("=");
                    if (attrParts.length > 1) {
                        String attr = attrParts[0].trim();
                        String value = extractAttributeValue(step, attr + "=");
                        String fullAttr = attr + "='" + value + "'";
                        attrCount.put(fullAttr, attrCount.getOrDefault(fullAttr, 0) + 1);
                    }
                }
            }
        }

        // 按照优先级选择最佳定位器
        return getBestLocator(idCount, classCount, attrCount);
    }

    private static String getBestLocator(Map<String, Integer> idCount, Map<String, Integer> classCount, Map<String, Integer> attrCount) {
        String bestLocator = "";
        int maxCount = 0;

        // 优先考虑 id
        for (Map.Entry<String, Integer> entry : idCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                bestLocator = "[@id='" + entry.getKey() + "']";
            }
        }

        if (bestLocator.isEmpty()) {
            // 其次考虑 class
            for (Map.Entry<String, Integer> entry : classCount.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    bestLocator = "[@class='" + entry.getKey() + "']";
                }
            }
        }

        if (bestLocator.isEmpty()) {
            // 然后考虑其他属性
            for (Map.Entry<String, Integer> entry : attrCount.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    bestLocator = "[" + entry.getKey() + "]";
                }
            }
        }
        return bestLocator;
    }

    private String extractAttributeValue(String step, String attrPrefix) {
        int startIndex = step.indexOf(attrPrefix) + attrPrefix.length();
        int endIndex = step.indexOf("'", startIndex);
        return step.substring(startIndex, endIndex);
    }

    public String getXpath() {
        return xpath;
    }
}