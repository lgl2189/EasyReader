package com.reader.entity.template;

import com.reader.webpage.action.base.BaseActionSequence;

import java.io.Serializable;

/**
 * @author ：李冠良
 * @description ：无描述
 * @date ：2025 4月 08 23:37
 */


public class DirectoryTemplate implements Serializable {
    private static final long serialVersionUID = 8223673115786971656L;

    /**
     * 目录页url
     */
    private String directoryUrl;
    /**
     * 获取目录的每一项的动作序列
     */
    private BaseActionSequence getItemActionSequence;
    /**
     * 进入下一页的动作序列
     */
    private BaseActionSequence nextPageXpath;
    /**
     * 确认是否有下一页的动作序列
     */
    private BaseActionSequence endPageXpath;


}