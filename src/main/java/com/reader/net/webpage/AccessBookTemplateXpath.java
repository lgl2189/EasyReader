package com.reader.net.webpage;

/**
 * @author ：李冠良
 * @description ：无描述
 * @date ：2025 4月 02 14:42
 */


public class AccessBookTemplateXpath extends AccessWebPageWithMenu {

    static {
        loadScript("/js/getXpath.js");
    }

    /**
     * 构造函数，初始化输入的 URL 并进行必要的初始化操作。
     *
     * @param inputUrl 要访问的网页的 URL
     * @see #init(String)
     */
    public AccessBookTemplateXpath(String inputUrl) {
        super(inputUrl);
    }

    @Override
    protected JavaBridge setJavaBridgeObject() {
        return null;
    }

    protected static class JavaBridge implements AccessWebPageWithMenu.JavaBridge {
        @Override
        public void doOnContextMenu(String elementType) {

        }
    }
}