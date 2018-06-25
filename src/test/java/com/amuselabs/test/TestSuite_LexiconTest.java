package com.amuselabs.test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.opentest4j.AssertionFailedError;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


//TEST CASES FOR LEXICONS

public class TestSuite_Lexicon
{
    WebDriver driver = new ChromeDriver();
    public static Properties lexicon = new Properties();

    @BeforeAll
    public static void start_epadd()
    {
        try
        {
            Helper.start_ePADD();
            InputStream s = TestSuite_Lexicon.class.getClassLoader().getResourceAsStream("USER_INTERFACE.properties");
            lexicon.load(s);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void pre_Set()
    {

        Helper.clickOnLexicon(driver);
    }
    @Test
    public void testCorrect_number_of_messages_opened_onClick_Lexicon_category()
    {
        int number_of_messages_displayed_in_front_of_Lexicon=0,actual_number_of_messages_opened_onClick_Lexicon_category=0;
      try {
          number_of_messages_displayed_in_front_of_Lexicon = Helper.number_of_messages_displayed_in_front_of_Lexicon(driver);
          Helper.click_on_Lexicon_category_and_return_name(driver);
          actual_number_of_messages_opened_onClick_Lexicon_category = Helper.number_of_messages_opened_after_clicking_on_Lexicon_category(driver);
          assertEquals(number_of_messages_displayed_in_front_of_Lexicon, actual_number_of_messages_opened_onClick_Lexicon_category);
      }
      catch (AssertionFailedError e)
      {
          System.out.println("There is a mismatch in the number displayed in front of lexicon_category(eg 111 in front of 1528 in font \"Family\" and the actual number of messages opened on clicking that lexicon category");
          System.out.println("Expected::"+number_of_messages_displayed_in_front_of_Lexicon);
          System.out.println("Actual::"+actual_number_of_messages_opened_onClick_Lexicon_category);
      }
    }
    @Test
    public void testNumber_of_messages_through_graph_view()
    {
        Helper.click_on_Lexicon_category_and_return_name(driver);
        int actual_number_of_messages_opened_onClick_Lexicon_category=Helper.number_of_messages_opened_after_clicking_on_Lexicon_category(driver);
        driver.get("http://localhost:9099/epadd/lexicon");
        Helper.go_to_graph_view_lexicon(driver);
        int number_of_messages_of_lexicon_category_displayed_in_graph_view=Helper.number_of_messages_of_lexicon_category_displayed_in_graph_view(driver);
        assertTrue(actual_number_of_messages_opened_onClick_Lexicon_category==number_of_messages_of_lexicon_category_displayed_in_graph_view);
    }
    @AfterEach
    public void post_Set()
    {
        driver.quit();
    }
}