package com.amuselabs.test;
//import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Set;
//import java.util.Properties;
//import java.util.Set;

//import org.junit.jupiter.api.AfterEach;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.util.Assert;

import static java.lang.System.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class TestSuite_1 {
    String nametocheck;
    String value_in_From;
    WebDriver driver = new ChromeDriver();
    @BeforeAll
    public static void start_epadd() {
        try {
            StepDefs browser;
            browser = new StepDefs();
            // browser.openBrowser("chrome");
            browser.openEpadd("appraisal");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @BeforeEach
    public void open_Correspondents() {
            driver.get("http://localhost:9099/epadd/correspondents");
            testOnClickName();
    }

    @Test
    public void testOnClickName() {
        WebElement e = driver.findElement(By.xpath("//*[@id=\"people\"]/tbody/tr[2]/td[1]/a"));
        WebElement onClickValue = driver.findElement(By.xpath("//*[@id=\"people\"]/tbody/tr[2]/td[1]/a"));
        nametocheck = onClickValue.getText();
        e.click();
        //testOnClickName(driver);
        //String onClickValue = driver.findElement(By.cssSelector("#people > tbody > tr:nth-child(2) > td:nth-child(1)")).getText();
        value_in_From = new Helper().strings_From(driver);
        String s = value_in_From;
     //   assertFalse(value_in_From.contains(nametocheck));
    }
    @AfterEach
    public void check()
     {
     //    value_in_From = new Helper().strings_From(driver);
         assertFalse(value_in_From.contains(nametocheck));
     }
}