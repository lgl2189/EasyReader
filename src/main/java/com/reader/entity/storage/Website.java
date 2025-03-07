// Website.java
package com.reader.entity.storage;

public class Website {
    private String url;
    private String name;
    private String xpath;


    // 构造函数、getters、setters


    public Website() {
    }

    public Website(String url, String name, String xpath) {
        this.url = url;
        this.name = name;
        this.xpath = xpath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }
}