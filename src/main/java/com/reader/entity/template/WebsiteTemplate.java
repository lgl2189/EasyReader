package com.reader.entity.template;

import com.reader.entity.net.Website;

import java.io.Serializable;

/**
 * @author ：李冠良
 * @description ：保存一个网站的模板信息，包括网站信息、书籍模板信息、目录模板信息
 * 一个网站对应一套图书模板，如果一个网站有多种图书模板，则需要创建多个WebsiteTemplate对象
 * @date ：2025 4月 08 23:38
 */


public class WebsiteTemplate implements Serializable {
    private static final long serialVersionUID = 1874132608794641395L;

    private Website website;
    private BookTemplate bookTemplate;
    private DirectoryTemplate directoryTemplate;    
}