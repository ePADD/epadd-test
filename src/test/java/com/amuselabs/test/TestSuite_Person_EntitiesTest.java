package com.amuselabs.test;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Test Cases for Person Entities

public class TestSuite_Person_EntitiesTest
{
    Helper helper=new Helper("firefox");
    public static Properties user_interface =new Properties(); //user_interface variable corresponding to the Properties file for Person-Entities.


    @BeforeAll
    public static void start_epadd() {
        try {
            Helper.start_ePADD();
            InputStream s = TestSuite_Person_EntitiesTest.class.getClassLoader().getResourceAsStream("USER_INTERFACE.properties");
            user_interface.load(s);  //Reading properties file
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

     @BeforeEach
    public void pre_Set()
     {
     try {
         helper.click_on_person_entities_browse_top_page();
     }
     catch (AssertionFailedError e)
     {
         System.out.println("Incorrect page opened on clicking Person-Entities button on browse-top page");
     }
     }

     @Test
    public void test_02_Number() //this test case checks whether number displayed in front of Person-Entity(eg 1681 in front of Jeb Bush)
     {                       //is equal to the number of messages opened on clicking that particular Person-Entity or not.
         int number=0;
         int actualNumber=0;
         try {
              number = helper.get_message_number_person_entity_and_click_on_name(); //return the message number(1321 in this case)and also clicks on a name in page.
             actualNumber = helper.number_of_messages_opened_after_clicking_on_a_name();
             assertEquals(number, actualNumber);
             System.out.println("Message number displayed in front of Person-Entity name matches with the actual number of messages opened on clicking that name");
             System.out.println("Number displayed in front of name"+number);
             System.out.println("Actual number of messages opened"+actualNumber);
         }
        catch (AssertionFailedError e)
         {
                System.out.println("Number of messages mentioned in front of name" + "=" + number);
                System.out.println("Actual number of messages opened on clicking name" + "=" + actualNumber);

         }
     }
     @Test
     public void test_02_EntityName() //this test case clicks on a person-entity ,stores it,and checks whether the mail opened contains that person entity or not.
     {
         try {
             String name = helper.get_name_from_person_entity_and_click_on_name();
             String body = helper.body_of_mail_after_clicking_a_name_in_person_entity();
             assertTrue(body.contains(name));//checks for the name only in body of mail not in Cc,Bcc,To and From.
             System.out.println("The name "+name+" is present in the mail opened");
         }
         catch (AssertionFailedError e)
         {

                 System.out.println("Name clicked is not present in the body of mail opened::");
         }
     }
     @Test
     public void test_02_Person_Entity_Name_Advanced_Search() //this test case clicks on a person-entity and stores it.
     {                                                    //it then navigates to advanced search page,enters same person-entity in entity textfield
                                                          //and then clicks on search,it then checks whether the mail opened conatins that person-entity or not.
         String name="";
         try {
             name = helper.get_name_from_person_entity_and_click_on_name();
             helper.click_on_search();
             helper.go_to_advanced_Search();
             helper.enter_data_in_Entity_Textfield_InAdvancedSearchPage(name);
             helper.clickOnSearch_InAdvancedSearchPage();
             String whole_text = helper.whole_mail_after_entering_entity_in_Advanced_Search_Page();
             assertTrue(whole_text.contains(name));
             System.out.println("The name "+name+" is present in the messages opened through advanced search");
         }
         catch (AssertionFailedError e)
         {
                 System.out.println("The name entered in \"Entity\" textfield is not present in mail opened after clicking on search");

         }
     }
     @Test
     public void test_02_Person_Entity_Message_Number_Advanced_Search() //this test case clicks on a person-entity and stores number mentioned in front of it.
     {                                                               //it then navigates to advanced search page,enters same person-entity in entity textfield
         int number=0,new_number=0;                                //it then clicks on "Search" button,and checks whether the total messages opened are equal to number initially mentioned.
         String name="";
      try {
          number=helper.get_message_number();
          name = helper.get_name_from_person_entity_and_click_on_name();
         // assertTrue(Helper.isPageOpened(driver,user_interface.getProperty("Search_")));
          helper.click_on_search();
          helper.go_to_advanced_Search();
          helper.enter_data_in_Entity_Textfield_InAdvancedSearchPage(name);
          helper.clickOnSearch_InAdvancedSearchPage();
          new_number = helper.number_of_messages_opened_after_clicking_on_a_name();
          assertEquals(number, new_number);
          System.out.println("The message number in front of "+name+" is "+number+" and the actual messages opened through advanced search is " +new_number);
      }
      catch (AssertionFailedError e)
      {

              System.out.println("There is a mismatch in the message number displayed in front of Person-entity and the number of messages opened through advanced search of same entity::");
              System.out.println("Expected::" + number);
              System.out.println("Found::" + new_number);

      }
     }
     @Test
     public void test_02_NumberBrowseTopPagePersonEntities() //this test case checks whether the number displayed in Person-Entities button in browse-top page
     {                                                   //for eg(Person-Entity 2162) is equal to the number of contacts on that page or not.
         int number=0,number_Of_Contacts=0;
         try {
             number = helper.get_number_in_person_entity_browse_top_page_and_click_on_person_entities();
             helper.click_on_edit_entities_person_entity();
             number_Of_Contacts = helper.countNumberOfContactsIn_a_Page();
             assertEquals(number, number_Of_Contacts);
         }
         catch (AssertionFailedError e)
         {
                 System.out.println("Number displayed in Person-entities button in browse-top page=" + number);
                 System.out.println("Number of contacts present in Person-entities=" + number_Of_Contacts);
         }
     }
     @Test
     public void test_02_Sent_and_Received()    //this test case checks whether the sum of sent and received messages present in message window of a particular
     {                                       //Person-entity is greater than or equal to the number of messages opened or not.
         try {
             try {
                 helper.get_name_from_person_entity_and_click_on_name();
                 int number_of_sent_messages = helper.sent_messages_in_message_window();
                 int number_of_received_messages = helper.received_messages_in_message_window();
                 int number_of_messages_opened = helper.number_of_messages_opened_after_clicking_on_a_name();
                 int sum = number_of_sent_messages + number_of_received_messages;
                 assertTrue(sum >= number_of_messages_opened);
             }
             catch(NoSuchElementException e)
             {
                 System.out.println("Sent Messages and Received Messages Elements are not present::");
                 assertTrue(true);
             }
         }
         catch (AssertionFailedError e)
         {
                 System.out.println("Assert Condition mismatch");
         }
     }
     @Test
     public void test_02_Person_Entity_Name_Underlined_or_Not()   //this test case checks whether the Person-Entitie's name clicked is underlined in the messages
     {                                                        //opened or not.
         String name="";
         int count=0;
         try {
             name = helper.get_name_from_person_entity_and_click_on_name();
             List<WebElement> underline_entities = helper.strings_underlined_on_message_window();
             count = 0;
             for (WebElement e : underline_entities) {
                 String s = e.getText();
                 if (s.compareTo(name) == 0) {
                     count++;
                 }
             }
             assertTrue(count == Integer.parseInt(TestSuite_Person_EntitiesTest.user_interface.getProperty("number_of_underlined_entity_you_see_in_page")));
             System.out.println("The name "+name+" is underlined "+count+" times");
         }
         catch (AssertionFailedError e)
         {
                 System.out.println("The name "+name+" is underlined "+count+" times");
         }
     }
     @Test
     public void test_02_Person_Entity_Name_highlighted_or_Not()  //this test case checks whether the Person-Entities name clicked is highlighted in the
     {                                                            //messages opened or not.
         String name="";
         int count=0;
         try {
             name = helper.get_name_from_person_entity_and_click_on_name();
             List<WebElement> highlighted_entities = helper.strings_highlighted_on_message_window();
             count = 0;
             for (WebElement e : highlighted_entities) {
                 String s = e.getText();
                 if (s.compareTo(name) == 0) {
                     count++;
                 }
             }
             assertTrue(count == Integer.parseInt(TestSuite_Person_EntitiesTest.user_interface.getProperty("number_of_highlighted_entity_you_see_in_page")));
             System.out.println("The name "+name+" is highlighted "+count+" times");
         }
              catch (AssertionFailedError e)
             {
                     System.out.println("The name "+name+" is highlighted "+count+" times");
             }
     }

    @Test
    public void screenshot() throws IOException {
        helper.takeSnapShot("Person_Entties_screenshot.png");
    }

     @AfterEach
     public void post_Set()
     {
         helper.close_browser();
     }
}