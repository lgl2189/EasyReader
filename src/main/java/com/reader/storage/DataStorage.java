package com.reader.storage;

import com.reader.storage.common.DataDepository;
import com.reader.storage.common.DataManager;
import com.reader.storage.common.impl.ObjectDepository;

import java.io.File;

public class DataStorage {

    public static final String STORAGE_ROOT_PATH;
    public static final ObjectDepository WEBSITE_DEPOSITORY;
    public static final ObjectDepository BOOK_DEPOSITORY;
    public static final DataManager DATA_MANAGER;

    static {
        // 初始化存储路径
        STORAGE_ROOT_PATH = System.getProperty("user.dir") + File.separator + "data";
        // 初始化存储仓库名称
        String WEBSITE_REPOSITORY_NAME = "website";
        String BOOK_REPOSITORY_NAME = "book";
        // 初始化存储仓库
        DataDepository<?>[] DEPOSITORIES = {
                WEBSITE_DEPOSITORY = new ObjectDepository(STORAGE_ROOT_PATH, WEBSITE_REPOSITORY_NAME),
                BOOK_DEPOSITORY = new ObjectDepository(STORAGE_ROOT_PATH, BOOK_REPOSITORY_NAME)
        };
        DATA_MANAGER = new DataManager(DataStorage.STORAGE_ROOT_PATH, DEPOSITORIES);
    }
}