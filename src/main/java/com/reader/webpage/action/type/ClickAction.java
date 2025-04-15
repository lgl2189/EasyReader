package com.reader.webpage.action.type;

import com.reader.webpage.action.base.BaseAction;
import com.reader.webpage.action.result.Result;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * @author ：李冠良
 * @description ：无描述
 * @date ：2025 4月 15 15:26
 */


public class ClickAction extends BaseAction {

    @Override
    public Result run(ChromeDriver driver) {
        try {
            WebElement element = driver.findElement(By.xpath(getElementXpath()));
            element.click();
        }
        catch (StaleElementReferenceException e) {
            return Result.error("未点击到元素，元素不存在\n"+e.getMessage());
        }
        return Result.success();
    }
}