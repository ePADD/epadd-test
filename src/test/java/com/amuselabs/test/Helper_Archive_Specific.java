package com.amuselabs.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.InputStream;
import java.util.Properties;

public class Helper_Archive_Specific {

    //MBOX SPECIFIC TEST CASES HELPER  METHODS

    public static Properties mbox_file_path = new Properties();

    public static void open_browse_top_page_navigating_from_import_page(WebDriver driver,String archivist)
    {
        try
        {
            InputStream s = Helper_Archive_Specific.class.getClassLoader().getResourceAsStream("mbox_file_paths_of_archivist.properties");
            mbox_file_path.load(s);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        WebElement e=driver.findElement(By.cssSelector("#mboxes > div.account > div:nth-child(2) > div.input-field > input"));
        String mbox_file_path=Helper_Archive_Specific.mbox_file_path.getProperty(archivist);
        e.sendKeys(mbox_file_path);
        WebElement e1=driver.findElement(By.cssSelector("#gobutton"));
        e1.click();
        Helper.waitFor();;
        WebElement e2=driver.findElement(By.cssSelector("#selectall0"));
        e2.click();
        Helper.waitFor();
        WebElement e3=driver.findElement(By.cssSelector("#go-button"));
        e3.click();
        Helper.longWait();
    }
}

