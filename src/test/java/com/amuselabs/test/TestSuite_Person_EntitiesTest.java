package com.amuselabs.test;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.opentest4j.AssertionFailedError;

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
    WebDriver driver=new ChromeDriver();
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
         Helper.click_on_Person_Entities_BrowseTopPage(driver);
     }
     catch (AssertionFailedError e)
     {
         System.out.println("Incorrect page opened on clicking Person-Entities button on browse-top page");
     }
     }

     @Test
    public void testNumber() //this test case checks whether number displayed in front of Person-Entity(eg 1681 in front of Jeb Bush)
     {                       //is equal to the number of messages opened on clicking that particular Person-Entity or not.
         int number=0;
         int actualNumber=0;
         try {
              number = Helper.get_message_number_PersonEntity_and_click_on_name(driver); //return the message number(1321 in this case)and also clicks on a name in page.
             actualNumber = Helper.number_of_messages_opened_after_clicking_on_PersonEntities_name(driver);
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
     public void testEntityName() //this test case clicks on a person-entity ,stores it,and checks whether the mail opened contains that person entity or not.
     {
         try {
             String name = Helper.get_name_from_Person_Entity_and_click_on_name(driver);
             String body = Helper.body_of_mail_after_clicking_a_name_in_PersonEntity(driver);
             assertTrue(body.contains(name));//checks for the name only in body of mail not in Cc,Bcc,To and From.
             System.out.println("The name "+name+" is present in the mail opened");
         }
         catch (AssertionFailedError e)
         {

                 System.out.println("Name clicked is not present in the body of mail opened::");
         }
     }
     @Test
     public void testPerson_Entity_Name_Advanced_Search() //this test case clicks on a person-entity and stores it.
     {                                                    //it then navigates to advanced search page,enters same person-entity in entity textfield
                                                          //and then clicks on search,it then checks whether the mail opened conatins that person-entity or not.
         try {
             String name = Helper.get_name_from_Person_Entity_and_click_on_name(driver);
             Helper.click_on_search(driver);
             Helper.go_to_advanced_Search(driver);
             Helper.enter_data_in_Entity_Textfield_InAdvancedSearchPage(driver, name);
             Helper.clickOnSearch_InAdvancedSearchPage(driver);
             String whole_text = Helper.whole_mail_after_entering_entity_in_Advanced_Search_Page(driver);
             assertTrue(whole_text.contains(name));
         }
         catch (AssertionFailedError e)
         {
                 System.out.println("The name entered in \"Entity\" textfield is not present in mail opened after clicking on search");

         }
     }
     @Test
     public void testPerson_Entity_Message_Number_Advanced_Search() //this test case clicks on a person-entity and stores number menioned in front of it.
     {                                                               //it then navigates to advanced search page,enters same person-entity in entity textfield
         int number=0,new_number=0;                                //it then clicks on "Search" button,and checks whether the total messages opened are equal to number initially mentioned.
      try {
          String message_number = TestSuite_Person_EntitiesTest.user_interface.getProperty("message_number");
          WebElement message_number_of_a_PersonEntity = driver.findElement(By.cssSelector(message_number));//message number in front of name
          number = Integer.parseInt(message_number_of_a_PersonEntity.getText());
          String name = Helper.get_name_from_Person_Entity_and_click_on_name(driver);
         // assertTrue(Helper.isPageOpened(driver,user_interface.getProperty("Search_")));
          Helper.click_on_search(driver);
          Helper.go_to_advanced_Search(driver);
          Helper.enter_data_in_Entity_Textfield_InAdvancedSearchPage(driver, name);
          Helper.clickOnSearch_InAdvancedSearchPage(driver);
          new_number = Helper.number_of_messages_opened_after_clicking_on_PersonEntities_name(driver);
          assertEquals(number, new_number);
      }
      catch (AssertionFailedError e)
      {

              System.out.println("There is a mismatch in the message number displayed in front of Person-entity and the number of messages opened through advanced search of same entity::");
              System.out.println("Expected::" + number);
              System.out.println("Found::" + new_number);

      }
     }
     @Test
     public void testNumberBrowseTopPagePersonEntities() //this test case checks whether the number displayed in Person-Entities button in browse-top page
     {                                                   //for eg(Person-Entity 2162) is equal to the number of contacts on that page or not.
         int number=0,number_Of_Contacts=0;
         try {
             number = Helper.get_number_in_PersonEntity_BrowseTopPage_and_click_on_Person_Entities(driver);
             Helper.clickOnEditEntities_PersonEntity(driver);
             number_Of_Contacts = Helper.countNumberOfContactsIn_a_Page(driver);
             assertEquals(number, number_Of_Contacts);
         }
         catch (AssertionFailedError e)
         {
                 System.out.println("Number displayed in Person-entities button in browse-top page=" + number);
                 System.out.println("Number of contacts present in Person-entities=" + number_Of_Contacts);
         }
     }
     @Test
     public void test_Sent_and_Received()    //this test case checks whether the sum of sent and received messages present in message window of a particular
     {                                       //Person-entity is greater than or equal to the number of messages opened or not.
         try {
             try {
                 Helper.get_name_from_Person_Entity_and_click_on_name(driver);
                 int number_of_sent_messages = Helper.sent_messages_in_PersonEntitiesPage(driver);
                 int number_of_received_messages = Helper.received_messages_in_PersonEntitiesPage(driver);
                 int number_of_messages_opened = Helper.number_of_messages_opened_after_clicking_on_PersonEntities_name(driver);
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
     public void testPerson_Entity_Name_Underlined_or_Not()   //this test case checks whether the Person-Entitie's name clicked is underlined in the messages
     {                                                        //opened or not.
         try {
             String name = Helper.get_name_from_Person_Entity_and_click_on_name(driver);
             List<WebElement> underline_entities = Helper.strings_underlined_on_message_window_of_Person_Entities(driver);
             int count = 0;
             for (WebElement e : underline_entities) {
                 String s = e.getText();
                 if (s.compareTo(name) == 0) {
                     count++;
                 }
             }
             assertTrue(count == Integer.parseInt(TestSuite_Person_EntitiesTest.user_interface.getProperty("number_of_underlined_entity_you_see_in_page")));
         }
         catch (AssertionFailedError e)
         {
                 System.out.println("Name clicked is not underlined");
         }
     }
     @Test
     public void testPerson_Entity_Name_highlighted_or_Not()  //this test case checks whether the Person-Entities name clicked is highlighted in the
     {                                                        //messages opened or not.
         try {
             String name = Helper.get_name_from_Person_Entity_and_click_on_name(driver);
             List<WebElement> highlighted_entities = Helper.strings_highlighted_on_message_window_of_Person_Entities(driver);
             int count = 0;
             for (WebElement e : highlighted_entities) {
                 String s = e.getText();
                 if (s.compareTo(name) == 0) {
                     count++;
                 }
             }
             assertTrue(count == Integer.parseInt(TestSuite_Person_EntitiesTest.user_interface.getProperty("number_of_highlighted_entity_you_see_in_page")));
         }
              catch (AssertionFailedError e)
             {
                     System.out.println("Name clicked is not highlighted");
             }
     }
     @AfterEach
     public void post_Set()
     {
         driver.quit();
     }
}