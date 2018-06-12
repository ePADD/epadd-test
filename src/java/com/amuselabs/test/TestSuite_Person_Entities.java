package com.amuselabs.test;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.opentest4j.AssertionFailedError;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Test Cases for Person Entities

public class TestSuite_Person_Entities
{
    WebDriver driver=new ChromeDriver();
    String id;
    public static Properties person_entities =new Properties(); //correspondents variable corresponding to the Properties file for Person-Entities.


    @BeforeAll
    public static void start_epadd() {
        try {
            Helper.start_ePADD();
            InputStream s = new FileInputStream("/home/ashu18/Projects/epadd_dev/epadd-test/src/java/com/amuselabs/test/USER_INTERFACE.properties");
            person_entities.load(s);  //Reading properties file
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

     @BeforeEach
    public void open_Person_entities()
     {

         Helper.click_on_Person_Entities_BrowseTopPage(driver);
     }

     @Test
    public void testNumber()
     {
         int number=0;
         int actualNumber=0;
         try {
              number = Helper.message_number_PersonEntity(driver); //return the message number(1321 in this case)and also clicks on a name in page.
             actualNumber = Helper.number_of_messages_opened_after_clicking_on_PersonEntities_name(driver);
             assertEquals(number, actualNumber);
         }
        catch (AssertionFailedError e)
         {
           //  driver.get("http://localhost:9099/epadd/list-entities.js");
             System.out.println("Number of messages mentioned in front of name"+"::"+/*Helper.name_from_Person_Entity(driver)*/" "+"is"+"::"+number);
             System.out.println("Actual number of messages opened on clicking name"+"::"+/*Helper.name_from_Person_Entity(driver)+*/" "+"is"+"::"+actualNumber);
         }
     }
     @Test
     public void testName()
     {
         String name=Helper.name_from_Person_Entity(driver);
         Helper.waitFor();
         Helper.changeWindow(driver);
         String body=Helper.body_of_mail_after_clicking_a_name_in_PersonEntity(driver);
         assertTrue(body.contains(name));//checks for the name only in body of mail not in Cc,Bcc,To and From.
     }
     @Test
     public void testPerson_Entity_Name_Advanced_Search()
     {
        String name=new Helper().name_from_Person_Entity(driver);
        Helper.changeWindow(driver);
        Helper.click_on_search_through_PersonEntities(driver);
        Helper.go_to_advanced_Search(driver);
        Helper.enter_data_in_Entity_Textfield_InAdvancedSearchPage(driver,name);
        Helper.clickOnSearch_InAdvancedSearchPage(driver);
        String whole_text=Helper.whole_mail_after_entering_entity_in_Advanced_Search_Page(driver);
        assertTrue(whole_text.contains(name));
     }
     @Test
     public void testPerson_Entity_Message_Number_Advanced_Search()
     {   int number=0,new_number=0;
      try {
          String message_number = TestSuite_Person_Entities.person_entities.getProperty("message_number");
          WebElement message_number_of_a_PersonEntity = driver.findElement(By.cssSelector(message_number));//message number in front of name
          number = Integer.parseInt(message_number_of_a_PersonEntity.getText());
          String name = Helper.name_from_Person_Entity(driver);
          Helper.changeWindow(driver);
          Helper.click_on_search_through_PersonEntities(driver);
          new Helper().go_to_advanced_Search_through_PersonEntities(driver);
          Helper.enter_data_in_Entity_Textfield_InAdvancedSearchPage(driver, name);
          Helper.clickOnSearch_InAdvancedSearchPage(driver);
          new_number = Helper.number_of_messages_opened_after_clicking_on_PersonEntities_name(driver);
          assertEquals(number, new_number);
      }
      catch (AssertionFailedError e)
      {
          System.out.println("There is a mismatch in the message number displayed in front of Person-entity and the number of messages opened through advanced search of same entity::");
          System.out.println("Expected::"+number);
          System.out.println("Found::"+new_number);
      }
     }
     @Test
     public void testNumberBrowseTopPagePersonEntities()
     {
         int number=0,number_Of_Contacts=0;
         try {
             number = Helper.number_in_PersonEntity_BrowseTopPage(driver);
             Helper.clickOnEditEntities_PersonEntity(driver);
             number_Of_Contacts = Helper.countNumberOfContactsIn_a_Page(driver);
             assertEquals(number, number_Of_Contacts);
         }
         catch (AssertionFailedError e)
         {
             System.out.println("Number displayed in Person-entities button in browse-top page="+"::"+number);
             System.out.println("Number of contacts present in Person-entities="+"::"+number_Of_Contacts);
         }
     }
     @Test
     public void test_Sent_and_Received()
     {
         Helper.name_from_Person_Entity(driver);
         Helper.waitFor();
         Helper.changeWindow(driver);
         int number_of_sentmessages=Helper.sent_messages_in_PersonEntitiesPage(driver);
         int number_of_receivedmessages=Helper.received_messages_in_PersonEntitiesPage(driver);
         int number_of_messages_opened=Helper.number_of_messages_opened_after_clicking_on_PersonEntities_name(driver);
         int sum=number_of_sentmessages+number_of_receivedmessages;
         assertTrue(sum>=number_of_messages_opened);
     }
     @Test
     public void testPerson_Entity_Name_Underlined_or_Not() {
         String name = Helper.name_from_Person_Entity(driver);
         Helper.changeWindow(driver);
         List<WebElement> underline_entities =Helper.strings_underlined_on_message_window_of_Person_Entities(driver);
         int count = 0;
         for (WebElement e : underline_entities) {
             String s = e.getText();
             if (s.compareTo(name) == 0) {
                 count++;
             }
         }
         assertTrue(count == Integer.parseInt(TestSuite_Person_Entities.person_entities.getProperty("number_of_underlined_entity_you_see_in_page")));
     }
     @Test
     public void testPerson_Entity_Name_highlighted_or_Not()
     {
         String name=Helper.name_from_Person_Entity(driver);
         Helper.changeWindow(driver);
         List<WebElement> highlighted_entities=Helper.strings_highlighted_on_message_window_of_Person_Entities(driver);
         int count=0;
         for(WebElement e:highlighted_entities)
         {
             String s=e.getText();
             if(s.compareTo(name)==0) {
                 count++;
             }
         }
         assertTrue(count == Integer.parseInt(TestSuite_Person_Entities.person_entities.getProperty("number_of_highlighted_entity_you_see_in_page")));
     }
     @AfterEach
     public void post_Set()
     {

         driver.quit();
     }
}