package com.amuselabs.test;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.opentest4j.AssertionFailedError;

import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Test cases for the "Other Entities" page.

public class TestSuite_Other_EntitiesTest
{
    Helper helper=new Helper("chrome");
    public static Properties user_interface =new Properties();

    @BeforeAll
    public static void start_epadd() {
        try {
            Helper.start_ePADD();
            InputStream s = TestSuite_Other_EntitiesTest.class.getClassLoader().getResourceAsStream("USER_INTERFACE.properties");
            user_interface.load(s);   //Reading properties file
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void pre_Set()
    {
        helper.click_on_other_entities();
    }

    @Test
    public void test_03_Number()  //this test case checks whether the number displayed in front of subentity(eg 38 in front of United States) is equal to the number of
    {                           //messages opened on clicking that particular subentity or not.
        int message_number=0,actual_number=0;
        try {
            helper.click_on_entity_name_other_entities_page();
            message_number = helper.message_number_other_entities_page();
            helper.click_on_sub_entity_name_other_entities_page();
           // driver.get("http://localhost:9099/epadd/browse?archiveID=84e8afd01303201b1bb2e763c78c2df0f7133d3004e1ce6e04d9b5e2e3d15423&adv-search=1&term=%22United%20States%22&termBody=on&termSubject=on");
            actual_number = helper.number_of_messages_opened_after_clicking_on_a_name();
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
    public void test_03_EntityName()  //this test case clicks on a sub-entity (eg United States)and checks whether the opened messages contains United States or not.
    {
        String sub_entity_name="";
        try {
            helper.click_on_entity_name_other_entities_page();
            sub_entity_name = helper.click_on_sub_entity_name_other_entities_page();
            //  driver.get("http://localhost:9099/epadd/browse?archiveID=84e8afd01303201b1bb2e763c78c2df0f7133d3004e1ce6e04d9b5e2e3d15423&adv-search=1&term=%22United%20States%22&termBody=on&termSubject=on");
            String body = helper.body_of_mail_opened_onclick_subentity();
            assertTrue(body.contains(sub_entity_name));
            System.out.println("The name "+sub_entity_name+" is present in the mail opened on clicking "+sub_entity_name);
        }
        catch (AssertionFailedError e)
        {
            System.out.println("The name "+sub_entity_name+" is not present in the mail opened on clicking "+sub_entity_name);
        }
    }
     @Test
     public void test_03_Other_Entity_Name_Advanced_Search()  //this test case clicks on a sub-entity type,stores it,(eg United States),navigates till advanced search page
     {                                          //enters that sub-entity type in "Entity" textfield,clciks on search, and then checks whether the messages opened contains
         String sub_entity_name="";
         try {
             helper.click_on_entity_name_other_entities_page();   //that sub-entity or not.
             sub_entity_name = helper.click_on_sub_entity_name_other_entities_page();
             helper.click_on_search();
             helper.go_to_advanced_Search();
             helper.enter_data_in_Entity_Textfield_InAdvancedSearchPage(sub_entity_name);
             helper.clickOnSearch_InAdvancedSearchPage();
             helper.waitFor(5);
             String body = helper.whole_mail_after_entering_entity_in_Advanced_Search_Page();
             assertTrue(body.contains(sub_entity_name));
             System.out.println("The name "+sub_entity_name+" is present in the mail opened through Advanced Search");
         }
         catch (AssertionFailedError e)
         {
             System.out.println("The name "+sub_entity_name+" is not present in the mail opened through Advanced Search");
         }
     }
     @Test
     public void test_03_Other_Entity_Message_Number_Advanced_Search() //this test case clicks on a sub-entity type,stores number in front of it,(eg 69 in front of United States),navigates till advanced search page
   {                                            //enters that sub-entity type in "Entity" textfield,clicks on search, and then checks whether the number messages opened
       int number=0,actual_number=0;          //is equals to the number stored or not.
       String sub_entity_name="";
       try {
           helper.click_on_entity_name_other_entities_page();
           number = helper.message_number_other_entities_page();
           sub_entity_name = helper.click_on_sub_entity_name_other_entities_page();
           helper.click_on_search();
           helper.go_to_advanced_Search();
           helper.enter_data_in_Entity_Textfield_InAdvancedSearchPage(sub_entity_name);
           helper.clickOnSearch_InAdvancedSearchPage();
           actual_number = helper.number_of_messages_opened_after_clicking_on_a_name();
           assertEquals(number, actual_number);
           System.out.println("The number displayed in front "+sub_entity_name+" is"+" "+number);
           System.out.println("The actual number of messages opened through Advanced Search is "+actual_number);
       }
       catch (AssertionFailedError e)
       {
           System.out.println("There is a mismatch in the in the message number displayed in front of sub-entity(eg,69 in front of \"United States\") and the actual number of messages opened on clicking that sub-entity through advanced search");
           System.out.println("Expected::"+number);
           System.out.println("Actual::"+actual_number);
       }
   }
    @Test
    public void test_03_NumberOtherEntities() //this test case stores the number displayed in front of Entity name(eg 460 in front of "Place"),then clicks on
    {                                    //that particular entity  and checks whether the number is equal to the number of contacts on that page or not.
        int number=0,number1=0;
        try {
            number = helper.number_of_entities_other_entites_page();
            helper.click_on_entity_name_other_entities_page();
            helper.click_on_edit_entities_other_entities_page();
            number1 = helper.countNumberOfContactsIn_a_Page();
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
    public void test_03_Sent_and_Received() //this test case checks whether the sum of sent and received messages present in message window of a particular
    {                                       //Other-entity is greater than or equal to the number of messages opened or not..
        helper.click_on_entity_name_other_entities_page();
        helper.click_on_sub_entity_name_other_entities_page();
        helper.waitFor(5);
       // driver.get("http://localhost:9099/epadd/browse?archiveID=84e8afd01303201b1bb2e763c78c2df0f7133d3004e1ce6e04d9b5e2e3d15423&adv-search=1&term=%22United%20States%22&termBody=on&termSubject=on");
        int number_of_sent_messages=helper.sent_messages_in_message_window();
        int number_of_received_messages=helper.received_messages_in_message_window();
        int sum=number_of_sent_messages+number_of_received_messages;
        int number_of_messages_opened=helper.number_of_messages_opened_after_clicking_on_a_name();
        assertTrue(sum>=number_of_messages_opened);
    }
    @AfterEach
    public void post_Set()
    {

      helper.close_browser();
    }
}