package com.reader.storage.common.impl;

import com.reader.storage.common.DataDepository;
import com.reader.storage.common.DataUtil;

import java.io.File;

public abstract class CommonDepository<T> implements DataDepository<T> {

    protected final String depositoryPath;
    protected final int depositoryPathLength;
    protected final String depositoryName;

    /**
     * 构造一个缓存仓库
     * @param rootPath 缓存仓库根目录路径，路径末尾建议不添加分隔符。推荐使用DataManager创建仓库。
     * @param depositoryName 缓存仓库名称，名称不能包含分隔符。
     */
    public CommonDepository(String rootPath, String depositoryName,String fileSuffix) {
        if (!rootPath.endsWith(File.separator)) {
            rootPath = rootPath + File.separator;
        }
        depositoryName = DataUtil.cleanFileName(depositoryName, rootPath, fileSuffix);
        this.depositoryPath = rootPath + depositoryName + fileSuffix;
        this.depositoryName = depositoryName;
        this.depositoryPathLength = depositoryPath.length();
    }

    public CommonDepository(String rootPath, String depositoryName) {
        if (!rootPath.endsWith(File.separator)) {
            rootPath = rootPath + File.separator;
        }
        depositoryName = DataUtil.cleanFileName(depositoryName, rootPath);
        this.depositoryPath = rootPath + depositoryName;
        this.depositoryName = depositoryName;
        this.depositoryPathLength = depositoryPath.length();
    }

    @Override
    public String getDepositoryPath() {
        return depositoryPath;
    }

    @Override
    public String getDepositoryName() {
        return depositoryName;
    }

    public String getValueFilePath(String key) {
        int prefixPathLength = depositoryPathLength + File.separator.length();
        key = DataUtil.cleanFileName(key, prefixPathLength);
        return depositoryPath + File.separator + key;
    }
}