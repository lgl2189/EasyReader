package com.reader.webpage.action;

import com.reader.entity.util.Pair;
import com.reader.webpage.action.result.Result;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author ：李冠良
 * @description ：用于表示一个动作序列，包含多个动作，动作按照一定规则顺序执行。
 * @date ：2025 4月 11 22:47
 */


public abstract class ActionSequence implements Serializable {
    @Serial
    private static final long serialVersionUID = 6610868168301757776L;

    protected final List<Pair<Integer, Action>> actionList = new ArrayList<>();

    public ActionSequence() {
    }

    /**
     * 执行动作序列，并返回结果。
     * @return 动作序列的结果，当全部动作成功执行完毕时，返回成功的结果；否则返回失败的结果。
     */
    public abstract Result run();

    /**
     * 添加动作到动作序列中，并返回动作的索引，默认索引为当前动作序列中最大的索引+1，索引值从1开始。
     * @param action 要添加的动作
     * @return 动作的索引
     */
    public int addAction(Action action) {
        int index = findMaxIndex() + 1;
        actionList.add(new Pair<>(index, action));
        return index;
    }

    /**
     * 添加动作到动作序列中，使用指定的索引值，并返回动作的索引，索引值从1开始。
     * @param action 要添加的动作
     * @param index 要添加的动作的索引值
     * @return 动作的索引
     */
    @Deprecated
    public int addAction(Action action, int index) {
        if (isIndexExist(index)) {
            actionList.stream()
                    .filter(pair -> pair.getKey() >= index)
                    .forEach(pair -> pair.setKey(pair.getKey() + 1));
        }
        actionList.add(new Pair<>(index, action));
        return index;
    }

    protected int findMaxIndex() {
        return actionList.stream().mapToInt(Pair::getKey).max().orElse(0);
    }

    protected boolean isIndexExist(int index) {
        return actionList.stream().anyMatch(pair -> pair.getKey() == index);
    }

    /**
     * 调用后会对动作序列进行排序，并重设动作的索引值，索引值从1开始。
     */
    protected void sortActionList() {
        actionList.sort(Comparator.comparingInt(Pair::getKey));
    }

    public List<Pair<Integer, Action>> getActionList() {
        return actionList;
    }

    @Override
    public String toString() {
        return "ActionSequence{" +
                "actionList=" + actionList +
                '}';
    }
}