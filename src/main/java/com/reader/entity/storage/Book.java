package com.reader.entity.storage;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author      ：李冠良
 * @description ：用于存储一本书所有需要存储在本地的信息，不仅仅包括一本书本身的信息，还包括获取这本书所需要的规则等额外信息。
 * @date        ：2025 3月 21 19:54
 */


public class Book implements Serializable {
    @Serial
    private static final long serialVersionUID = 8953341596805301998L;

    private String id;
    private com.reader.entity.book.Book book;
    private String bookNameXpath;
    private String authorXpath = null;
    private String publisherXpath = null;
    private String isbnXpath = null;
    private String languageXpath = null;
    private String descriptionXpath = null;    // 图书是否已完结
    private Boolean isFinishedXpath = null;
    /**
     * 这本书有多少层目录，默认为1层，即所有章节都在同一层（不存在更小的层级）
     */
    private int layerCount = 1;

    public Book(String id, com.reader.entity.book.Book book, String bookNameXpath) {
        this.id = id;
        this.book = book;
        this.bookNameXpath = bookNameXpath;
    }

    public Book(String id, com.reader.entity.book.Book book, String bookNameXpath, String authorXpath, String publisherXpath, String isbnXpath, String languageXpath, String descriptionXpath, Boolean isFinishedXpath, int layerCount) {
        this.id = id;
        this.book = book;
        this.bookNameXpath = bookNameXpath;
        this.authorXpath = authorXpath;
        this.publisherXpath = publisherXpath;
        this.isbnXpath = isbnXpath;
        this.languageXpath = languageXpath;
        this.descriptionXpath = descriptionXpath;
        this.isFinishedXpath = isFinishedXpath;
        this.layerCount = layerCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public com.reader.entity.book.Book getBook() {
        return book;
    }

    public void setBook(com.reader.entity.book.Book book) {
        this.book = book;
    }

    public String getBookNameXpath() {
        return bookNameXpath;
    }

    public void setBookNameXpath(String bookNameXpath) {
        this.bookNameXpath = bookNameXpath;
    }

    public String getAuthorXpath() {
        return authorXpath;
    }

    public void setAuthorXpath(String authorXpath) {
        this.authorXpath = authorXpath;
    }

    public String getPublisherXpath() {
        return publisherXpath;
    }

    public void setPublisherXpath(String publisherXpath) {
        this.publisherXpath = publisherXpath;
    }

    public String getIsbnXpath() {
        return isbnXpath;
    }

    public void setIsbnXpath(String isbnXpath) {
        this.isbnXpath = isbnXpath;
    }

    public String getLanguageXpath() {
        return languageXpath;
    }

    public void setLanguageXpath(String languageXpath) {
        this.languageXpath = languageXpath;
    }

    public String getDescriptionXpath() {
        return descriptionXpath;
    }

    public void setDescriptionXpath(String descriptionXpath) {
        this.descriptionXpath = descriptionXpath;
    }

    public Boolean getFinishedXpath() {
        return isFinishedXpath;
    }

    public void setFinishedXpath(Boolean finishedXpath) {
        isFinishedXpath = finishedXpath;
    }

    public int getLayerCount() {
        return layerCount;
    }

    public void setLayerCount(int layerCount) {
        this.layerCount = layerCount;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", book=" + book +
                ", bookNameXpath='" + bookNameXpath + '\'' +
                ", authorXpath='" + authorXpath + '\'' +
                ", publisherXpath='" + publisherXpath + '\'' +
                ", isbnXpath='" + isbnXpath + '\'' +
                ", languageXpath='" + languageXpath + '\'' +
                ", descriptionXpath='" + descriptionXpath + '\'' +
                ", isFinishedXpath=" + isFinishedXpath +
                ", layerCount=" + layerCount +
                '}';
    }
}