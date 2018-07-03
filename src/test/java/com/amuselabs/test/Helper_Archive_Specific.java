package com.amuselabs.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.InputStream;
import java.util.Properties;

public class Helper_Archive_Specific {

    WebDriver driver;
    public Helper_Archive_Specific(String browser_name) {
        if (browser_name.equalsIgnoreCase("chrome")) {
            driver = new ChromeDriver();
        } else if (browser_name.equalsIgnoreCase("firefox")) {
            driver = new FirefoxDriver();
        }
    }
    //MBOX SPECIFIC TEST CASES HELPER  METHODS

    public static Properties mbox_file_path = new Properties();

    public void open_browse_top_page_navigating_from_import_page(String archivist)
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
        Helper.waitFor(5);;
        WebElement e2=driver.findElement(By.cssSelector("#selectall0"));
        e2.click();
        Helper.waitFor(5);
        WebElement e3=driver.findElement(By.cssSelector("#go-button"));
        e3.click();
        Helper.longWait(105);
    }
    public String get_actual_value_of_component(String selector)
    {
        String actualvalue = driver.findElement(By.cssSelector(selector)).getText();
        return actualvalue;
    }
    public void navigate_to_import_page()
    {
        driver.get("http://localhost:9099/epadd/email-sources");
    }
    public void clear_the_contents_of_mbox_file_location_textfield_in_import_page()
    {
        //  String mbox_file_location_textfield=Helper.user_interface.getProperty("mbox_file_location_textfield");
        WebElement e=driver.findElement(By.cssSelector("#mboxes > div.account > div:nth-child(2) > div.input-field > input"));
        e.clear();
    }
    public void refresh_page()
    {
        driver.navigate().refresh();
    }
    public void close_browser()
    {
        driver.quit();
    }
}

