package com.reader.webpage.action;

/**
 * @author ：李冠良
 * @description ：无描述
 * @date ：2025 4月 11 22:48
 */

@Deprecated
public enum ActionType {
    // 鼠标操作
    CLICK,
    DOUBLE_CLICK,
    RIGHT_CLICK,
    HOVER,
    DRAG_AND_DROP,
    // 输入操作
    TYPE_TEXT,
    APPEND_TEXT,
    CLEAR_TEXT,
    // 选择操作
    SELECT_OPTION,
    SELECT_ALL_OPTIONS,
    DESELECT_OPTION,
    // 导航操作
    NAVIGATE_TO_URL,
    REFRESH_PAGE,
    GO_BACK,
    GO_FORWARD,
    // 滚动操作
    SCROLL_UP,
    SCROLL_DOWN,
    SCROLL_TO_ELEMENT,
    // 检查操作
    CHECK_VISIBILITY,
    CHECK_ENABLED,
    CHECK_SELECTED,
    // 获取操作
    GET_TEXT
}