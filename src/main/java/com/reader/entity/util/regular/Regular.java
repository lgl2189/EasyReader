package com.reader.entity.util.regular;

/**
 * @author ：李冠良
 * @description ：Regular 接口，实现这个接口，表示这个类表示一种公式。
 * @date ：2025 4月 08 23:48
 */

public interface Regular {
    /**
     * 应用规则
     * @param input 输入
     * @return 输出
     */
    Object applyRule(Object input);
}