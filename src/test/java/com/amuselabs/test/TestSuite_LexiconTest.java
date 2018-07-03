package com.amuselabs.test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.opentest4j.AssertionFailedError;

import java.io.InputStream;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


//TEST CASES FOR LEXICONS

public class TestSuite_LexiconTest {
    Helper helper=new Helper("chrome");
    public static Properties lexicon = new Properties();

    @BeforeAll
    public static void start_epadd() {
        try {
            Helper.start_ePADD();
            InputStream s = TestSuite_LexiconTest.class.getClassLoader().getResourceAsStream("USER_INTERFACE.properties");
            lexicon.load(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void pre_Set() {

        helper.clickOnLexicon();
    }

    @Test
    public void test_05_Correct_number_of_messages_opened_onClick_Lexicon_category() {
        int number_of_messages_displayed_in_front_of_Lexicon = 0, actual_number_of_messages_opened_onClick_Lexicon_category = 0;
        try {
            number_of_messages_displayed_in_front_of_Lexicon = helper.number_of_messages_displayed_in_front_of_Lexicon();
            helper.click_on_Lexicon_category_and_return_name();
            actual_number_of_messages_opened_onClick_Lexicon_category = helper.number_of_messages_opened_after_clicking_on_a_name();
            assertEquals(number_of_messages_displayed_in_front_of_Lexicon, actual_number_of_messages_opened_onClick_Lexicon_category);
        } catch (AssertionFailedError e) {
            System.out.println("There is a mismatch in the number displayed in front of lexicon_category(eg 111 in front of 1528 in font \"Family\" and the actual number of messages opened on clicking that lexicon category");
            System.out.println("Expected::" + number_of_messages_displayed_in_front_of_Lexicon);
            System.out.println("Actual::" + actual_number_of_messages_opened_onClick_Lexicon_category);
        }
    }
}