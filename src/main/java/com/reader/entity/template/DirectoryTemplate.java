package com.reader.entity.template;

import java.io.Serializable;

/**
 * @author ：李冠良
 * @description ：无描述
 * @date ：2025 4月 08 23:37
 */


public class DirectoryTemplate implements Serializable {
    private static final long serialVersionUID = 8223673115786971656L;

    /**
     * 目录页表达式，用于移动到目录的页面，应该为一个格式字符串，其中包含一个{}用于替换页码
     * 例如：http://www.example.com/category/page/{}
     */
    private String pageUrlRegular;


}