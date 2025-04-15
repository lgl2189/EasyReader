package com.reader.webpage.action.base;

import com.reader.webpage.action.result.Result;
import org.openqa.selenium.WebDriver;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author ：李冠良
 * @description ：无描述
 * @date ：2025 4月 11 22:48
 */


public abstract class BaseAction implements Serializable {
    @Serial
    private static final long serialVersionUID = 1137719672592657412L;

    private String actionName;

    private String elementXpath;

    private Result result;

    public BaseAction() {
    }

    public BaseAction(String elementXpath) {
        this.elementXpath = elementXpath;
    }

    public BaseAction(String actionName, String elementXpath) {
        this.actionName = actionName;
        this.elementXpath = elementXpath;
    }

    public abstract Result run(WebDriver webDriver);

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getElementXpath() {
        return elementXpath;
    }

    public void setElementXpath(String elementXpath) {
        this.elementXpath = elementXpath;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Action{" +
                "actionName='" + actionName + '\'' +
                ", elementXpath='" + elementXpath + '\'' +
                ", result=" + result +
                '}';
    }
}