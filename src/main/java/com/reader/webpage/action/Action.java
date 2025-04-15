package com.reader.webpage.action;

import com.reader.webpage.action.result.Result;
import com.reader.webpage.action.result.ResultType;
import javafx.scene.web.WebEngine;
import org.openqa.selenium.WebDriver;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author ：李冠良
 * @description ：无描述
 * @date ：2025 4月 11 22:48
 */


public class Action implements Serializable {
    @Serial
    private static final long serialVersionUID = 1137719672592657412L;

    private String actionName;

    private String elementXpath;

    private ActionType actionType;

    private Result result;

    public Action() {
    }

    public Action(String elementXpath, ActionType actionType) {
        this.elementXpath = elementXpath;
        this.actionType = actionType;
    }

    public Action(String actionName, String elementXpath, ActionType actionType) {
        this.actionName = actionName;
        this.elementXpath = elementXpath;
        this.actionType = actionType;
    }

    public void run(WebDriver webDriver){
        switch (actionType) {

        }
    }

    public Result execute(){
        return new Result(ResultType.SUCCESS);
    }

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

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
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
                "elementXpath='" + elementXpath + '\'' +
                ", actionType=" + actionType +
                '}';
    }
}