package com.reader.storage.common.impl;

import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * @author ：李冠良
 * @description： 无描述
 * @date ：2024/10/4 下午12:45
 */
public class ByteDepository extends CommonDepository<byte[]> {
    /**
     * 构造一个缓存仓库
     * @param rootPath 缓存仓库根目录路径，路径末尾建议不添加分隔符。推荐使用DataManager创建仓库。
     * @param depositoryName 缓存仓库名称，名称不能包含分隔符。
     */
    public ByteDepository(String rootPath, String depositoryName) {
        super(rootPath, depositoryName);
        File depositoryPath = new File(this.depositoryPath);
        if (!depositoryPath.exists()) {
            if (!depositoryPath.mkdirs()) {
                throw new RuntimeException("创建仓库失败！" + this.depositoryPath);
            }
        }
    }

    @Override
    public void add(String key, byte[] value) {
        add(key, value, false);
    }

    @Override
    public void add(String key, byte[] value, boolean isOverwrite) {
        String filePath = getValueFilePath(key);
        File file = new File(filePath);
        if (file.exists()) {
            if(!isOverwrite) {
                System.out.println("添加失败，键已存在：" + key);
                return;
            }
            else{
                delete(key);
            }
        }
        try {
            if (!file.createNewFile()) {
                throw new RuntimeException("创建文件失败！" + filePath);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(value);
        }
        catch (FileNotFoundException ignored) {
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean contain(String key) {
        String filePath = getValueFilePath(key);
        File file = new File(filePath);
        return file.exists();
    }

    @Override
    public byte[] get(String key) {
        String filePath = getValueFilePath(key);
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("查找失败，键不存在：" + key);
            return null;
        }
        byte[] value = null;
        try (FileInputStream fis = new FileInputStream(filePath)) {
            value = fis.readAllBytes();
        }
        catch (FileNotFoundException ignored) {
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    @Override
    public void update(String key, byte[] value) {
        delete(key);
        add(key, value);
    }

    @Override
    public void delete(String key) {
        String filePath = getValueFilePath(key);
        File file = new File(filePath);
        if (file.exists()) {
            if (!file.delete()) {
                throw new RuntimeException("删除文件失败！" + filePath);
            }
        }
        else {
            System.out.println("删除失败，键不存在：" + key);
        }
    }

    @Override
    public void deleteDepository() {
        File depositoryFile = new File(this.depositoryPath);
        try {
            FileUtils.deleteDirectory(depositoryFile);
        }
        catch (IOException e) {
            throw new RuntimeException("仓库删除失败：" + this.depositoryPath + "\n" + e.getMessage());
        }
    }

    @Override
    public boolean isEmpty() {
        File depositoryFile = new File(this.depositoryPath);
        try {
            return FileUtils.isEmptyDirectory(depositoryFile);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "ByteDepository{" +
                "depositoryPath='" + depositoryPath + '\'' +
                ", depositoryPathLength=" + depositoryPathLength +
                ", depositoryName='" + depositoryName + '\'' +
                '}';
    }
}