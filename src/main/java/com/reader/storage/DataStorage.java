package com.reader.storage;

import com.reader.entity.storage.Website;
import com.reader.storage.common.DataDepository;
import com.reader.storage.common.DataManager;
import com.reader.storage.common.impl.ObjectDepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataStorage {

    public static final String STORAGE_ROOT_PATH;
    public static final DataDepository<?> WEBSITE_DEPOSITORY;
    public static final DataManager.DepositoryPair[] DEPOSITORIES;
    private static final DataManager DATA_MANAGER;

    static {
        // 初始化存储路径
        STORAGE_ROOT_PATH = System.getProperty("user.dir") + File.separator + "data";
        // 初始化存储仓库名称
        String WEBSITE_REPOSITORY_NAME = "website";
        // 初始化存储仓库
        DEPOSITORIES = new DataManager.DepositoryPair[]{
                new DataManager.DepositoryPair(WEBSITE_REPOSITORY_NAME, ObjectDepository.class)
        };
        DATA_MANAGER =
                new DataManager(DataStorage.STORAGE_ROOT_PATH, DataStorage.DEPOSITORIES);
        // 获取存储仓库
        WEBSITE_DEPOSITORY = DATA_MANAGER.getDepository(WEBSITE_REPOSITORY_NAME);

    }

    public static void saveWebsite(Website website) {
        // 实现存储逻辑
    }

    public static void updateXpath(Website website) {
        // 实现更新逻辑
    }

    public static List<Website> loadWebsites() {
        // 返回存储的网站列表
        return new ArrayList<>();
    }
}