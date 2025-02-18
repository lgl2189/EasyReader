package com.reader.entity.content;

import com.reader.entity.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author      ：李冠良
 * @description ： 用于存储图书的一个部分，该部分可以是一卷、一章、一节等，可以包含更小的部分，也可以作为图书的最小单位。
 * @date        ：2025 2月 16 20:43
 */


public class Section {

    private int illustrationNextIndex = 1;

    private String title = "";
    private String content = "";
    // 封面图片列表
    private List<Image> coverList = new ArrayList<>();
    // 插图列表
    private List<Pair<Integer, Image>> illustrationList = new ArrayList<>();
    // 子节列表
    private List<Section> sectionList = new ArrayList<>();

    public Section() {
    }

    public Section(String title) {
        this.title = title;
    }

    public Section(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Section(String title, String content, List<Image> coverList, List<Pair<Integer, Image>> illustrationList, List<Section> sectionList) {
        this.title = title;
        this.content = content;
        this.coverList = coverList;
        this.illustrationList = illustrationList;
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
    public void addSection(int index, Section section)
            throws IndexOutOfBoundsException, NullPointerException {
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
     * @throws IndexOutOfBoundsException 如果索引超出范围，抛出IndexOutOfBoundsException
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

    /**
     * 添加封面图片到coverList的尾部
     * @param cover 要添加的封面图片对象
     */
    public void addCover(Image cover) {
        if (coverList == null) {
            coverList = new ArrayList<>();
        }
        coverList.add(cover);
    }

    /**
     * 添加封面图片到指定位置，将在该位置和之后的元素后移
     * @param index 索引从0开始，index < 0 || index > coverList.size()。如果索引超出范围，则抛出IndexOutOfBoundsException
     * @param cover 要添加的封面图片对象
     * @throws IndexOutOfBoundsException 如果索引超出范围，抛出IndexOutOfBoundsException
     * @throws NullPointerException 如果cover为null，抛出NullPointerException
     */
    public void addCover(int index, Image cover) throws IndexOutOfBoundsException, NullPointerException {
        if (coverList == null) {
            coverList = new ArrayList<>();
        }
        if (index < 0 || index > coverList.size()) {
            throw new IndexOutOfBoundsException("索引超出范围，无法添加");
        }
        if (cover == null) {
            throw new NullPointerException("cover不能为null");
        }
        coverList.add(index, cover);
    }

    /**
     * 获取指定位置的封面图片
     * @param index 索引从0开始，index < 0 || index >= coverList.size()。如果索引超出范围，则抛出IndexOutOfBoundsException
     * @return 指定位置的封面图片对象
     * @throws IndexOutOfBoundsException 如果索引超出范围，抛出IndexOutOfBoundsException
     */
    public Image getCover(int index) throws IndexOutOfBoundsException {
        if (coverList == null || index < 0 || index >= coverList.size()) {
            throw new IndexOutOfBoundsException("索引超出范围，无法获取");
        }
        return coverList.get(index);
    }

    /**
     * 设置指定位置的封面图片，将该位置的元素替换为新的封面图片
     * @param index 索引从0开始，index < 0 || index >= coverList.size()。如果索引超出范围，则抛出IndexOutOfBoundsException
     * @param cover 要设置的封面图片对象,如果cover为null，则抛出NullPointerException
     * @throws IndexOutOfBoundsException 如果索引超出范围，抛出IndexOutOfBoundsException
     * @throws NullPointerException 如果cover为null，抛出NullPointerException
     */
    public void setCover(int index, Image cover) throws IndexOutOfBoundsException, NullPointerException {
        if (coverList == null || index < 0 || index >= coverList.size()) {
            throw new IndexOutOfBoundsException("索引超出范围，无法设置");
        }
        if (cover == null) {
            throw new NullPointerException("cover不能为null");
        }
        coverList.set(index, cover);
    }

    /**
     * 交换两个封面图片的位置
     * @param index1 第一个封面图片的索引
     * @param index2 第二个封面图片的索引
     * @throws IndexOutOfBoundsException 如果索引index1或者index2超出范围，抛出IndexOutOfBoundsException
     */
    public void swapCover(int index1, int index2) throws IndexOutOfBoundsException {
        if (coverList == null || index1 < 0 || index1 >= coverList.size() ||
                index2 < 0 || index2 >= coverList.size() || index1 == index2) {
            throw new IndexOutOfBoundsException("索引超出范围或相等，无法交换");
        }
        Image cover1 = coverList.get(index1);
        Image cover2 = coverList.get(index2);
        coverList.set(index1, cover2);
        coverList.set(index2, cover1);
    }

    /**
     * 删除指定位置的封面图片，将该位置之后的元素前移
     * @param index 索引从0开始，index < 0 || index >= coverList.size()。如果索引超出范围，则不删除
     * @throws IndexOutOfBoundsException 如果索引超出范围，抛出IndexOutOfBoundsException
     */
    public void removeCover(int index) throws IndexOutOfBoundsException {
        if (coverList == null || index < 0 || index >= coverList.size()) {
            throw new IndexOutOfBoundsException("索引超出范围，无法删除");
        }
        coverList.remove(index);
    }

    /**
     * 添加插图到illustrationList中，并分配唯一的ID
     * @param illustration 要添加的插图对象
     * @throws NullPointerException 如果illustration为null，抛出NullPointerException
     */
    public void addIllustration(Image illustration) throws NullPointerException {
        if (illustration == null) {
            throw new NullPointerException("Illustration cannot be null");
        }
        Pair<Integer, Image> pair = new Pair<>(illustrationNextIndex++, illustration);
        illustrationList.add(pair);
    }

    /**
     * 根据ID获取指定的插图
     * @param id 插图的唯一ID
     * @return 对应ID的插图对象
     * @throws NoSuchElementException 如果没有找到对应ID的插图，抛出NoSuchElementException
     */
    public Image getIllustration(int id) throws NoSuchElementException {
        if (illustrationList == null) {
            throw new NoSuchElementException("插图列表为空");
        }
        for (Pair<Integer, Image> pair : illustrationList) {
            if (pair.getKey().equals(id)) {
                return pair.getValue();
            }
        }
        throw new NoSuchElementException("列表中没有ID为" + id + "的插图");
    }

    /**
     * 更新指定ID的插图
     * @param id 插图的唯一ID
     * @param newIllustration 新的插图对象
     * @throws NoSuchElementException 如果没有找到对应ID的插图，抛出NoSuchElementException
     */
    public void setIllustration(int id, Image newIllustration) throws NoSuchElementException {
        if (newIllustration == null) {
            throw new NullPointerException("newIllustration cannot be null");
        }
        if (illustrationList == null) {
            throw new NoSuchElementException("插图列表为空");
        }
        for (Pair<Integer, Image> pair : illustrationList) {
            if (pair.getKey().equals(id)) {
                pair.setValue(newIllustration);
                return;
            }
        }
        throw new NoSuchElementException("列表中没有ID为" + id + "的插图");
    }

    /**
     * 根据ID删除插图
     * @param id 插图的唯一ID
     * @throws NoSuchElementException 如果没有找到对应ID的插图，抛出NoSuchElementException
     */
    public void removeIllustration(int id) throws NoSuchElementException {
        if (illustrationList == null) {
            throw new NoSuchElementException("插图列表为空");
        }
        for (int i = 0; i < illustrationList.size(); i++) {
            if (illustrationList.get(i).getKey().equals(id)) {
                illustrationList.remove(i);
                return;
            }
        }
        throw new NoSuchElementException("列表中没有ID为" + id + "的插图");
    }

    /**
     * 交换两个插图的位置
     * @param id1 第一个插图的ID
     * @param id2 第二个插图的ID
     * @throws NoSuchElementException 如果任一ID不存在于列表中，抛出NoSuchElementException
     */
    public void swapIllustration(int id1, int id2) throws NoSuchElementException {
        if (illustrationList == null || illustrationList.isEmpty()) {
            throw new NoSuchElementException("插图列表为空");
        }
        Pair<Integer, Image> pair1 = null;
        Pair<Integer, Image> pair2 = null;

        for (Pair<Integer, Image> pair : illustrationList) {
            if (pair.getKey().equals(id1)) {
                pair1 = pair;
            }
            else if (pair.getKey().equals(id2)) {
                pair2 = pair;
            }
            if (pair1 != null && pair2 != null) break;
        }

        if (pair1 == null || pair2 == null) {
            throw new NoSuchElementException("其中一个或者两个id不存在于插图列表中");
        }

        Image tempValue = pair1.getValue();
        pair1.setValue(pair2.getValue());
        pair2.setValue(tempValue);
    }

    public int getIllustrationNextIndex() {
        return illustrationNextIndex;
    }

    private void setIllustrationNextIndex(int illustrationNextIndex) {
        this.illustrationNextIndex = illustrationNextIndex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Image> getCoverList() {
        return coverList;
    }

    public void setCoverList(List<Image> coverList) {
        this.coverList = coverList;
    }

    public List<Pair<Integer, Image>> getIllustrationList() {
        return illustrationList;
    }

    public void setIllustrationList(List<Pair<Integer, Image>> illustrationList) {
        this.illustrationList = illustrationList;
    }

    public List<Section> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<Section> sectionList) {
        this.sectionList = sectionList;
    }

    @Override
    public String toString() {
        return "Section{" +
                "illustrationNextIndex=" + illustrationNextIndex +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", coverList=" + coverList +
                ", illustrationList=" + illustrationList +
                ", sectionList=" + sectionList +
                '}';
    }
}