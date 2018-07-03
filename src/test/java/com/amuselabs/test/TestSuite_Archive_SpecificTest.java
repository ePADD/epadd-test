package com.amuselabs.test;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.opentest4j.AssertionFailedError;

import java.io.InputStream;
import java.util.Properties;

public class TestSuite_Archive_SpecificTest
{
    Helper_Archive_Specific helper_archive_specific=new Helper_Archive_Specific("chrome");
    public static Properties expected_values = new Properties();
    public static Properties actual_values = new Properties();
    public static Properties archive = new Properties();    //this file reads the expected properties file for different archivist.

    @BeforeAll
    public static void start_epadd ()throws Exception
    {
        Helper.start_ePADD();
        try
        {
            InputStream s = TestSuite_Archive_SpecificTest.class.getClassLoader().getResourceAsStream("Archive_Specific_Properties_Container.properties");
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

            helper_archive_specific.navigate_to_import_page();

    }

    @Test
    public void test_06_Archive_Specific() {
        for (Object archivist : TestSuite_Archive_SpecificTest.archive.keySet()) {
            try {
                String path = TestSuite_Archive_SpecificTest.archive.getProperty((String) archivist);
                InputStream file_for_an_archivist_containing_expected_values = TestSuite_Archive_SpecificTest.class.getClassLoader().getResourceAsStream(path);
                expected_values.load(file_for_an_archivist_containing_expected_values);       //Reading properties files
                InputStream browse_top = TestSuite_Archive_SpecificTest.class.getClassLoader().getResourceAsStream("Browse_Top.properties");
                actual_values.load(browse_top);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            String actualvalue = "";
            String expectedvalue = "";
            helper_archive_specific.open_browse_top_page_navigating_from_import_page((String)archivist);
            for (Object property : TestSuite_Archive_SpecificTest.expected_values.keySet()) {
                try {
                    String selector = TestSuite_Archive_SpecificTest.actual_values.getProperty((String) property);
                    expectedvalue = TestSuite_Archive_SpecificTest.expected_values.getProperty((String) property);
                    //find the value specified by the selector. It should match the expected value.
                    helper_archive_specific.refresh_page();
                    actualvalue=helper_archive_specific.get_actual_value_of_component(selector);
                    Assertions.assertEquals(expectedvalue, actualvalue);
                }
                catch (AssertionFailedError e)
                {
                    System.out.println("There is a mismatch in the expected and actual values of" + " " + property + " " + "property" + " " + "in property file of" + ":"+archivist);
                    System.out.println(" Expected value:" + expectedvalue + " " + "Found:" + actualvalue);
                }

            }
            helper_archive_specific.navigate_to_import_page();
            helper_archive_specific.clear_the_contents_of_mbox_file_location_textfield_in_import_page();
        }

    }
    @AfterEach
    public void post_Set()
    {
        helper_archive_specific.close_browser();
    }

}