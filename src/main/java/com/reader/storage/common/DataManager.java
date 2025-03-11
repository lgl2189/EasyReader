package com.reader.storage.common;

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
     * @param depositories 需要创建的仓库的列表，DataDepository<?>数组
     */
    public DataManager(String storageRootPath, DataDepository<?>[] depositories) {
        this(storageRootPath);
        for (DataDepository<?> depository : depositories) {
            addDepository(depository);
        }
    }

    /**
     * 添加缓存仓库
     * @param depository 仓库引用对象
     */
    public void addDepository(DataDepository<?>  depository) {
        depositoryMap.put(depository.getDepositoryName(), depository);
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

    public void loadDepository(DataDepository<?> depository) {
        addDepository(depository);
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