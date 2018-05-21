package com.amuselabs.test;
import com.ibm.icu.impl.Assert;
import org.junit.*;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestSuite_1
{
    /*  @BeforeClass
      public static void beforeClass()
      {
          System.out.println("I am before class Method::");
      }
  */
    WebDriver driver;
    @BeforeEach
    public void navigate_To_Message_Window()
    {
        try {
            driver = new ChromeDriver();
            driver.get("http://localhost:9099/epadd/correspondents");
            WebElement e= driver.findElement(By.xpath("//*[@id=\"people\"]/tbody/tr[2]/td[1]/a"));
            e.click();
            onClickName(driver);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testOnClickName(WebDriver driver)
    {
        String onClickValue = driver.findElement(By.cssSelector("#people > tbody > tr:nth-child(2) > td:nth-child(1)")).getText();
        String value_in_From=strings_From(driver);
        assertEquals(true,value_in_From.contains(onClickValue));

    }


    public String strings_From(WebDriver driver)
    {
        String window1=driver.getWindowHandle();
        Set<String> windows=driver.getWindowHandles();
        for(String x:windows)
        {
            if(x.equals(window1)==false)
            {
                driver.switchTo().window(x);
                break;
            }
        }
        WebElement e=driver.findElement(By.xpath("//*[@id=\"jog_contents\"]/div[2]/div[1]/table/tbody/tr[2]/td[2]/a"));
        String onClickValue=e.getText();
        return onClickValue;
    }
}