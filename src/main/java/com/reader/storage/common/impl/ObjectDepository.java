package com.reader.storage.common.impl;

import com.reader.storage.common.DataUtil;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用ObjectDepository存储的对象，其类必须实现Serializable接口，否则无法序列化。
 */
public class ObjectDepository extends CommonDepository<Object> {

    public static final String DEFAULT_FILE_SUFFIX = ".obj";

    private final Map<String, Object> dataMap = new HashMap<>();

    public ObjectDepository(String rootPath, String depositoryName) {
        super(rootPath, depositoryName);
        File depositoryDir = new File(this.depositoryPath);
        if (!depositoryDir.exists()) {
            if (!depositoryDir.mkdirs()) {
                throw new RuntimeException("创建仓库目录失败: " + this.depositoryPath);
            }
        } else {
            loadExistingFiles(depositoryDir);
        }
    }

    private void loadExistingFiles(File depositoryDir) {
        File[] files = depositoryDir.listFiles((_, name) -> name.endsWith(DEFAULT_FILE_SUFFIX));
        if (files == null) return;

        for (File file : files) {
            String fileName = file.getName();
            String key = fileName.substring(0, fileName.length() - DEFAULT_FILE_SUFFIX.length());
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                dataMap.put(key, obj);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("从文件加载对象失败: " + file.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加数据，如果数据已经存在，则添加失败
     * @param key 数据的键
     * @param value 数据的值
     */
    @Override
    public void add(String key, Object value) {
        add(key, value, false);
    }

    /**
     * 添加数据，如果数据已经存在，则根据isOverwrite参数决定是否覆盖。
     * @param key 数据的键
     * @param value 数据的值
     * @param isOverwrite 是否覆盖已存在的数据
     */
    @Override
    public void add(String key, Object value, boolean isOverwrite) {
        validateKey(key);
        key = cleanFileName(key);
        File file = getFileForKey(key);
        if(file.exists()){
            if(isOverwrite){
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                    oos.writeObject(value);
                    dataMap.put(key, value);
                } catch (IOException e) {
                    throw new RuntimeException("添加对象失败，key: " + key, e);
                }
            }
            else{
                throw new IllegalArgumentException("key已存在: " + key);
            }
        }
        else{
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(value);
                dataMap.put(key, value);
            } catch (IOException e) {
                throw new RuntimeException("添加对象失败，key: " + key, e);
            }
        }
    }

    /**
     * 获取数据，如果数据存在，则返回数据对象。如果数据不存在，则返回null
     * @param key 数据的键
     * @return 数据对象
     */
    @Override
    public Object get(String key) {
        return dataMap.get(key);
    }

    @Override
    public Map<String,Object> getAll() {
        return Map.copyOf(dataMap);
    }

    @Override
    public boolean isKeyExists(String key) {
        return dataMap.containsKey(key);
    }

    @Override
    public boolean contain(String key) {
        return dataMap.containsKey(key);
    }

    @Override
    public void update(String key, Object value) {
        if (!contain(key)) {
            throw new IllegalArgumentException("key不存在: " + key);
        }
        add(key, value,true);
    }

    @Override
    public void delete(String key) {
        File file = getFileForKey(key);
        if (file.delete()) {
            dataMap.remove(key);
        } else {
            throw new RuntimeException("删除对象失败，key: " + key);
        }
    }

    @Override
    public void deleteDepository() {
        dataMap.clear();
        File depositoryDir = new File(this.depositoryPath);
        File[] files = depositoryDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.delete()) {
                    throw new RuntimeException("删除文件失败: " + file.getAbsolutePath());
                }
            }
        }
        if (!depositoryDir.delete()) {
            throw new RuntimeException("删除仓库目录失败: " + depositoryDir.getAbsolutePath());
        }
    }

    @Override
    public boolean isEmpty() {
        return dataMap.isEmpty();
    }

    private File getFileForKey(String key) {
        return new File(this.depositoryPath, key + DEFAULT_FILE_SUFFIX);
    }

    private void validateKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("key不能为空或空白字符");
        }
        if (key.contains(File.separator) || key.contains("/") || key.contains("\\")) {
            throw new IllegalArgumentException("key中包含文件路径分隔符");
        }
    }

    public String cleanFileName(String input) {
        // 定义在 Windows 文件名中不允许出现的字符的正则表达式
        String invalidChars = "[\\\\/:*?\"<>|]";
        // 使用空字符串替换所有不允许的字符
        return input.replaceAll(invalidChars, "_");
    }
}