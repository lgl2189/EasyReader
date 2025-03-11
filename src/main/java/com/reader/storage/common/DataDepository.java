package com.reader.storage.common;

import java.util.Map;

/**
 * 这个接口定义了数据仓库的基本操作，包括添加、获取、删除、更新等。
 * 具体的实现类需要实现这个接口。
 * @param <T> 可存储的数据类型
 */
public interface DataDepository<T> {

    /**
     * 添加数据，如果数据已经存在，则添加失败。
     * @param key 数据的键
     * @param value 数据的值
     */
    void add(String key, T value);

    /**
     * 添加数据，如果数据已经存在，则根据isOverwrite参数决定是否覆盖。
     * @param key 数据的键
     * @param value 数据的值
     * @param isOverwrite 是否覆盖已存在的数据
     */
    void add(String key, T value, boolean isOverwrite);

    T get(String key);

    Map<String, T> getAll();

    boolean isKeyExists(String key);

    boolean contain(String key);

    void update(String key, T value);

    void delete(String key);

    void deleteDepository();

    boolean isEmpty();

    String getDepositoryPath();

    String getDepositoryName();

}