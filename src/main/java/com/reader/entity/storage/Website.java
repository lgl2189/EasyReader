// Website.java
package com.reader.entity.storage;

import java.io.Serial;
import java.io.Serializable;

public class Website implements Serializable {
    @Serial
    private static final long serialVersionUID = 3476374593623737650L;

    private String id;
    private String url;
    private String name;
    private String xpath;

    public Website() {
    }

    public Website(String id, String url, String name, String xpath) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.xpath = xpath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Website{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", xpath='" + xpath + '\'' +
                '}';
    }
}