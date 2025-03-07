package com.reader.storage.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ：李冠良
 * @description： 一个键值对本地存储系统，提供缓存仓库的管理和数据操作。
 * @date ：2024/10/4 下午12:26
 */

public class DataManager {
    private final String storageRootPath;
    private final Map<String, DataDepository<?>> depositoryMap = new HashMap<>();

    /**
     *
     * @param storageRootPath 缓存根目录路径，路径末尾建议添加分隔符。路径中的所有分隔符必须使用File.separator，避免出现未知错误
     */
    public DataManager(String storageRootPath) {
        this.storageRootPath = storageRootPath;
    }

    public record DepositoryPair(String name, Class<? extends DataDepository<?>> depositoryClazz) {
    }

    /**
     * 构造函数，默认加载提供仓库名称的缓存仓库
     * @param storageRootPath 缓存根目录路径，路径末尾建议不添加分隔符。路径中的所有分隔符必须使用File.separator，避免出现未知错误
     * @param depositoryPairs 需要创建的仓库的列表，DepositoryPair数组
     */
    public DataManager(String storageRootPath, DepositoryPair[] depositoryPairs) {
        this(storageRootPath);
        for (DepositoryPair depositoryPair : depositoryPairs) {
            addDepository(depositoryPair.name(),depositoryPair.depositoryClazz());
        }
    }

    /**
     * 添加缓存仓库
     * @param depositoryName 仓库名称。仓库名称不包含任何分隔符
     * @param depositoryClass 缓存仓库类型
     */
    public void addDepository(String depositoryName, Class<? extends DataDepository<?>> depositoryClass) {
        try {
            // 通过反射获取构造函数（假设所有Depository实现类都有(String, String)的构造函数）
            Constructor<? extends DataDepository<?>> constructor =
                    depositoryClass.getDeclaredConstructor(String.class, String.class);
            // 创建新实例
            DataDepository<?> depository =
                    constructor.newInstance(this.storageRootPath, depositoryName);
            depositoryMap.put(depositoryName, depository);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("该存储类" + depositoryClass.getName() + "没有找到(String,String)类型的构造函数", e);
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("实例化存储类" + depositoryClass.getName() + "失败", e);
        }
    }

    public DataDepository<?> getDepository(String depositoryName, Class<? extends DataDepository<?>> depositoryClass) {
        DataDepository<?> depository = getDepository(depositoryName);
        try {
            // 使用Class.cast()方法进行类型转换
            return depositoryClass.cast(depository);
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("仓库类型不匹配: " + depositoryName + ". 预期类型: " + depositoryClass.getName() + ", 实际类型: " + depository.getClass().getName(), e);
        }
    }

    public DataDepository<?> getDepository(String depositoryName) {
        DataDepository<?> depository = depositoryMap.get(depositoryName);
        if (depository == null) {
            throw new IllegalArgumentException("仓库不存在：" + depositoryName);
        }
        return depositoryMap.get(depositoryName);
    }

    public void deleteDepository(String depositoryName) {
        DataDepository<?> depository = getDepository(depositoryName);
        depositoryMap.remove(depositoryName);
        depository.deleteDepository();
    }

    public void loadDepository(String depositoryName, Class<? extends DataDepository<?>> depositoryClass) {
        addDepository(depositoryName, depositoryClass);
    }

    public void unloadDepository(String depositoryName) {
        depositoryMap.remove(depositoryName);
    }

    public String getStorageRootPath() {
        return storageRootPath;
    }

    public Map<String, DataDepository<?>> getDepositoryMap() {
        return depositoryMap;
    }

    @Override
    public String toString() {
        return "DataManager{" +
                "storageRootPath='" + storageRootPath + '\'' +
                ", depositoryMap=" + depositoryMap +
                '}';
    }
}