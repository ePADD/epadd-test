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
//This class contains only one test case,this test case is designed in such a way that it covers all test scenarios related with mbox specific things.
//This test case will execute for any archivist.
/*
The concept behind the test case is that we are maintaining two properties file denoted by expected_values and actual_values,the expected_values property file
contains the expected values of some mbox specific components,for eg if the Archivist is Bush Small then expected values for "Date Range" and "Messages:"
are "January 1,1960 to March 29,2003" and "1842 incoming,0 outgoing" respectively.Now the test case reads these expected values from expexted_values
property file and reads the selector of the same components from actual_values property file.The values which will be fetched from these selectors
will be the actual value.If the expected values and actual values match,test is passed positively ,if not then test is passed with a mismatch message.
Important point to be noted is that name of keys in both expected_values and actual_values must be same.
 */
public class TestSuite_Archive_Specific
{
    WebDriver driver = new ChromeDriver();
    public static Properties expected_values = new Properties();
    public static Properties actual_values = new Properties();
    public static Properties archive = new Properties();    //this file reads the expected properties file for different archivist.

    @BeforeAll
    public static void start_epadd ()throws Exception
    {
        Helper.start_ePADD();
        try
        {
            InputStream s = TestSuite_Archive_Specific.class.getClassLoader().getResourceAsStream("Archive_Specific_Properties_Container.properties");
            archive.load(s);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @BeforeEach
    public void pre_Set ()
    {
        driver.get("http://localhost:9099/epadd/email-sources");
    }

    @Test
    public void test1 () {
        for (Object archivist :TestSuite_Archive_Specific.archive.keySet()) {
            try {
                String path = TestSuite_Archive_Specific.archive.getProperty((String) archivist);
                InputStream file_for_an_archivist_containing_expected_values = TestSuite_Archive_Specific.class.getClassLoader().getResourceAsStream(path);
                expected_values.load(file_for_an_archivist_containing_expected_values);       //Reading properties files
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
          //  String mbox_file_location_textfield=Helper.user_interface.getProperty("mbox_file_location_textfield");
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
    public void post_Set ()
    {
        driver.quit();
    }
}