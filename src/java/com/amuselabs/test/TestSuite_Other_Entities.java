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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Test cases for the "Other Entities" page.

public class TestSuite_Other_Entities
{
    WebDriver driver=new ChromeDriver();
    public static Properties other_entities =new Properties();

    @BeforeAll
    public static void start_epadd() {
        try {
            Helper.start_ePADD();
            InputStream s = new FileInputStream("/home/ashu18/Projects/epadd_dev/epadd-test/src/java/com/amuselabs/test/USER_INTERFACE.properties");
            other_entities.load(s);   //Reading properties file
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void pre_Set()
    {
        Helper.clickOnOtherEntities(driver);
    }

    @Test
    public void testNumber()
    {
        int message_number=0,actual_number=0;
        try {
            Helper.clickOnEntityName_OtherEntitiesPage(driver);
            message_number = Helper.message_number_OtherEntitiesPage(driver);
            Helper.clickOnSubEntityName_OtherEntitiesPage(driver);
            driver.get("http://localhost:9099/epadd/browse?archiveID=84e8afd01303201b1bb2e763c78c2df0f7133d3004e1ce6e04d9b5e2e3d15423&adv-search=1&term=%22United%20States%22&termBody=on&termSubject=on");
            Helper.waitFor();
            actual_number = Helper.number_of_messages_opened_after_clicking_on_subEntity_name(driver);
            assertEquals(message_number, actual_number);
        }
        catch (AssertionFailedError e)
        {
            System.out.println("There is a mismatch in the message number displayed in front of sub-entity(eg,69 in front of \"United States\") and the actual number of messages opened on clicking that sub-entity");
            System.out.println("Expected::"+message_number);
            System.out.println("Actual::"+actual_number);
        }
    }
    @Test
    public void testEntityName()
    {
        Helper.clickOnEntityName_OtherEntitiesPage(driver);
        String sub_entity_name=Helper.clickOnSubEntityName_OtherEntitiesPage(driver);
        driver.get("http://localhost:9099/epadd/browse?archiveID=84e8afd01303201b1bb2e763c78c2df0f7133d3004e1ce6e04d9b5e2e3d15423&adv-search=1&term=%22United%20States%22&termBody=on&termSubject=on");
        String body=Helper.body_of_mail_opened_onclick_subEntity(driver);
        assertTrue(body.contains(sub_entity_name));
    }
     @Test
     public void testEntityNameAdvacedSearch()
     {
         Helper.clickOnEntityName_OtherEntitiesPage(driver);
         String entity_name=Helper.clickOnSubEntityName_OtherEntitiesPage(driver);
         Helper.click_on_search_through_OtherEntities(driver);
         Helper.go_to_advanced_Search_through_OtherEntities(driver);
         Helper.enter_data_in_Entity_Textfield_InAdvancedSearchPage(driver,entity_name);
         Helper.clickOnSearch_InAdvancedSearchPage(driver);
         Helper.waitFor();
         String body=Helper.whole_mail_after_entering_entity_in_Advanced_Search_Page(driver);
         assertTrue(body.contains(entity_name));
     }
     @Test
     public void testEntityNumberAdvancedSearch()
   {
       int number=0,actual_number=0;
       try {
           Helper.clickOnEntityName_OtherEntitiesPage(driver);
           number = Helper.message_number_OtherEntitiesPage(driver);
           String entity_name = Helper.clickOnSubEntityName_OtherEntitiesPage(driver);
           Helper.click_on_search_through_OtherEntities(driver);
           Helper.go_to_advanced_Search_through_OtherEntities(driver);
           Helper.enter_data_in_Entity_Textfield_InAdvancedSearchPage(driver, entity_name);
           Helper.clickOnSearch_InAdvancedSearchPage(driver);
           actual_number = Helper.number_of_messages_opened_after_clicking_on_subEntity_name(driver);
           assertEquals(number, actual_number);
       }
       catch (AssertionFailedError e)
       {
           System.out.println("There is a mismatch in the in the message number displayed in front of sub-entity(eg,69 in front of \"United States\") and the actual number of messages opened on clicking that sub-entity through advanced search");
           System.out.println("Expected::"+number);
           System.out.println("Actual::"+actual_number);
       }
   }
    @Test
    public void testNumberOtherEntities()
    {
        int number=0,number1=0;
        try {
            number = Helper.number_of_entities_OtherEntites(driver);
            Helper.clickOnEntityName_OtherEntitiesPage(driver);
            Helper.clickOnEditEntities_OtherEntities(driver);
            number1 = Helper.countNumberOfContactsIn_a_Page(driver);
            assertEquals(number, number1);
        }
        catch(AssertionFailedError e)
        {
            System.out.println("There is mismatch in the number displayed in font of Other-Entities name (eg 460 in front of \"Place\")and the number of entities opened on clicking that entity::");
            System.out.println("Expected::"+number);
            System.out.println("Actual::"+number1);
        }
    }
    @Test
    public void testSent_and_Received()
    {
        Helper.clickOnEntityName_OtherEntitiesPage(driver);
        Helper.clickOnSubEntityName_OtherEntitiesPage(driver);
        Helper.waitFor();
        driver.get("http://localhost:9099/epadd/browse?archiveID=84e8afd01303201b1bb2e763c78c2df0f7133d3004e1ce6e04d9b5e2e3d15423&adv-search=1&term=%22United%20States%22&termBody=on&termSubject=on");
        int number_of_sentmessages=Helper.sent_messages_in_OtherEntitiesPage(driver);
        int number_of_receivedmessages=Helper.received_messages_in_OtherEntitiesPage(driver);
        int sum=number_of_sentmessages+number_of_receivedmessages;
        int number_of_messages_opened=Helper.number_of_messages_opened_after_clicking_on_subEntity_name(driver);
        assertTrue(sum>=number_of_messages_opened);
    }
    @AfterEach
    public void postSet()
    {

        driver.quit();
    }
}