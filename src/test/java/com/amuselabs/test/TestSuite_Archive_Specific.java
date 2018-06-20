package com.amuselabs.test;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.opentest4j.AssertionFailedError;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class TestSuite_Archive_Specific
{
    WebDriver driver = new ChromeDriver();
    public static Properties expected_values = new Properties();
    public static Properties actual_values = new Properties();
    public static Properties archive = new Properties();

    @BeforeAll
    public static void start_epadd ()
    {
        Helper.start_ePADD();
        try
        {
            InputStream s = TestSuite_Correspondents.class.getClassLoader().getResourceAsStream("Archive_Specific_Properties_Container.properties");
            archive.load(s);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @BeforeEach
    public void pre_set ()
    {
        driver.get("http://localhost:9099/epadd/email-sources");
    }

    @Test
    public void test1 () {
        for (Object archivist :TestSuite_Archive_Specific.archive.keySet()) {
            try {
                String path = TestSuite_Archive_Specific.archive.getProperty((String) archivist);
                InputStream file_for_an_archivist_conatining_expected_values = new FileInputStream(path);
                expected_values.load(file_for_an_archivist_conatining_expected_values);       //Reading properties files
                InputStream browse_top = TestSuite_Archive_Specific.class.getClassLoader().getResourceAsStream("Browse_Top.properties");
                actual_values.load(browse_top);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            String actualvalue = "";
            String expectedvalue = "";
            Helper_Archive_Specific.open_browse_top_page_navigating_from_import_page(driver,(String)archivist);
            for (Object property : TestSuite_Archive_Specific.expected_values.keySet()) {
                try {
                    String selector = TestSuite_Archive_Specific.actual_values.getProperty((String) property);
                    expectedvalue = TestSuite_Archive_Specific.expected_values.getProperty((String) property);
                    //find the value specified by the selector. It should match the expected value.
                    driver.navigate().refresh();
                    actualvalue = driver.findElement(By.cssSelector(selector)).getText();
                    Assertions.assertEquals(expectedvalue, actualvalue);
                }
                catch (AssertionFailedError e)
                {
                    System.out.println("There is a mismatch in the expected and actual values of" + " " + property + " " + "property" + " " + "in property file of" + ":"+archivist);
                    System.out.println(" Expected value:" + expectedvalue + " " + "Found:" + actualvalue);
                }

            }
            driver.get("http://localhost:9099/epadd/email-sources");
            WebElement e=driver.findElement(By.cssSelector("#mboxes > div.account > div:nth-child(2) > div.input-field > input"));
            e.clear();
        }

    }
    /*@Test
    public void test2()
    {
        Helper.open_browse_top_page_navigating_from_import_page(driver);
        String messages_expected=Archive_Specific.user_interface.getProperty("Messages_Expected");
        String actual_messages=driver.findElement(By.cssSelector(Archive_Specific.correspondents1.getProperty("Messages"))).getText();
        Assertions.assertEquals(messages_expected,actual_messages);
    }*/
    @AfterEach
    public void post_set ()
    {
        driver.quit();
    }
}