package com.reader.entity.content;

import java.util.ArrayList;
import java.util.List;

/**
 * @author      ：李冠良
 * @description : 用于存放整个书籍的信息，书籍下属更细化的部分将存放在Section类中
 * @date        ：2025 2月 16 20:43
 */


public class Book {

    private String bookName;
    private String author;
    private String publisher;
    private String isbn;
    private String language;
    private String description;    // 图书是否已完结
    private Boolean isFinished;
    private List<Section> sectionList = new ArrayList<>();

    public Book() {
    }

    public Book(String bookName, String author, String publisher, String isbn, String language, String description, Boolean isFinished, List<Section> sectionList) {
        this.bookName = bookName;
        this.author = author;
        this.publisher = publisher;
        this.isbn = isbn;
        this.language = language;
        this.description = description;
        this.isFinished = isFinished;
        this.sectionList = sectionList;
    }

    /**
     * 添加section到sectionList的尾部
     * @param section 要添加的section对象
     */
    public void addSection(Section section) {
        sectionList.add(section);
    }

    /**
     * 添加section到指定位置，将在该位置和之后的元素后移，即这些元素的index
     * @param index 索引从0开始，index < 0 || index > sectionList.size()。如果索引超出范围，则抛出IndexOutOfBoundsException
     * @param section 要添加的section对象
     * @throws IndexOutOfBoundsException 如果索引超出范围，抛出IndexOutOfBoundsException
     * @throws NullPointerException 如果section为null，抛出NullPointerException
     */
    public void addSection(int index, Section section) throws IndexOutOfBoundsException, NullPointerException {
        if (index < 0 || index > sectionList.size()) {
            throw new IndexOutOfBoundsException("索引超出范围，无法添加");
        }
        if (section == null) {
            throw new NullPointerException("section不能为null");
        }
        sectionList.add(index, section);
    }

    /**
     * 获取指定位置的section
     * @param index 索引从0开始，index < 0 || index >= sectionList.size()。如果索引超出范围，则抛出IndexOutOfBoundsException
     * @return 指定位置的section对象
     * throws IndexOutOfBoundsException 如果索引超出范围，抛出IndexOutOfBoundsException
     */
    public Section getSection(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= sectionList.size()) {
            throw new IndexOutOfBoundsException("索引超出范围，无法获取");
        }
        return sectionList.get(index);
    }

    /**
     * 设置指定位置的section，将该位置的元素替换为新的section
     * @param index 索引从0开始，index < 0 || index >= sectionList.size()。如果索引超出范围，则抛出IndexOutOfBoundsException
     * @param section 要设置的section对象,如果section为null，则抛出NullPointerException
     * @throws IndexOutOfBoundsException 如果索引超出范围，抛出IndexOutOfBoundsException
     * @throws NullPointerException 如果section为null，抛出NullPointerException
     */
    public void setSection(int index, Section section) throws IndexOutOfBoundsException, NullPointerException {
        if (index < 0 || index >= sectionList.size()) {
            throw new IndexOutOfBoundsException("索引超出范围，无法设置");
        }
        if (section == null) {
            throw new NullPointerException("section不能为null");
        }
        sectionList.set(index, section);
    }

    /**
     * 交换两个section的位置
     * @param index1 第一个section的索引
     * @param index2 第二个section的索引
     * @throws IndexOutOfBoundsException 如果索引index1或者index2超出范围，抛出IndexOutOfBoundsException
     */
    public void swapSection(int index1, int index2) throws IndexOutOfBoundsException {
        if (index1 < 0 || index1 >= sectionList.size() ||
                index2 < 0 || index2 >= sectionList.size() || index1 == index2) {
            throw new IndexOutOfBoundsException("索引超出范围，无法交换");
        }
        Section section1 = sectionList.get(index1);
        Section section2 = sectionList.get(index2);
        sectionList.set(index1, section2);
        sectionList.set(index2, section1);
    }

    /**
     * 删除指定位置的section，将该位置之后的元素前移，即这些元素的index-1
     * @param index 索引从0开始，index < 0 || index >= sectionList.size()。如果索引超出范围，则不删除
     * @throws IndexOutOfBoundsException 如果索引超出范围，抛出IndexOutOfBoundsException
     */
    public void removeSection(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= sectionList.size()) {
            throw new IndexOutOfBoundsException("索引超出范围，无法删除");
        }
        sectionList.remove(index);
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getFinished() {
        return isFinished;
    }

    public void setFinished(Boolean finished) {
        isFinished = finished;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookName='" + bookName + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", isbn='" + isbn + '\'' +
                ", language='" + language + '\'' +
                ", description='" + description + '\'' +
                ", isFinished=" + isFinished +
                '}';
    }
}