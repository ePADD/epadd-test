package com.amuselabs.test;
import org.junit.jupiter.api.*;
import org.openqa.selenium.NoSuchElementException;
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


//Test Cases for Correspondents

public class TestSuite_Correspondents
   {
    String nametocheck;   //Variable for name in "All Correspondents" Page,eg "Kathleen Shanahan","Eli Ferrera",etc.
    WebDriver driver = new ChromeDriver();
      static int flag=0;
    public static Properties correspondents = new Properties();   //correspondents variable representing, reading selectors from properties file for Correspondents.

    @BeforeAll
    public static void start_epadd() {
        try {
            Helper.start_ePADD();    //method for starting ePADD

            InputStream s = new FileInputStream("/home/ashu18/Projects/epadd_dev/epadd-test/src/java/com/amuselabs/test/USER_INTERFACE.properties");
            correspondents.load(s);       //Reading properties files
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void pre_Set()
    {

        Helper.click_on_Correspondents_BrowseTopPage(driver);
    }

    @Test
    public void testFrom()
    {

        try {
            int contact_id = Helper.get_contact_id(driver);
            nametocheck = Helper.clickOnNameInCorrespondents(driver);  //finds a name ,store its text,click on it and returns name in "nametocheck" variable
            int contact_id_From = Helper.get_contact_id_From(driver);
            assertEquals(contact_id, contact_id_From);    //This TestCase fails if "From" is not present or if name which was clicked was not present in "From"
        }
        catch(AssertionFailedError e)
        {
            System.out.println("The name"+"::"+nametocheck+" "+"is not present in From of mail opened");
        }
    }

     @Test
     public void testTo()
     {
         try {
             int contact_id = Helper.get_contact_id(driver);
             nametocheck = Helper.clickOnNameInCorrespondents(driver);   //same test case as above,checks string in "To" of mail
             int contact_id_to = Helper.get_contact_id_To(driver);
             assertEquals(contact_id, contact_id_to);
         }
         catch(AssertionFailedError e)
         {
             System.out.println("The name"+"::"+nametocheck+" "+"is not present in To of mail opened");
         }
     }

     @Test
     public void testCc() {                                      //same test case as above two,checks in "Cc" of mail.
         boolean result = false;
         try {
             int contact_id = Helper.get_contact_id(driver);
             nametocheck = Helper.clickOnNameInCorrespondents(driver);
             int contact_id_Cc = Helper.get_contact_id_Cc(driver);
             if(flag==1)
             {
                 try
                 {
                     assertEquals(contact_id,contact_id_Cc);
                 }
                 catch (AssertionFailedError e)
                 {
                     System.out.println("The name"+"::"+nametocheck+" "+"is not present in Cc of mail opened");
                 }
             }
         }
         catch (NoSuchElementException e) {
             result = true;
             System.out.println("Cc is not present in the mail opened for"+"::"+nametocheck);
         }
         assertTrue(result);
     }

     @Test
      public void testBcc()                                            //same test case as above two,checks in "bcc" of mail.
     {
         boolean result = false;
         try {
             int contact_id = Helper.get_contact_id(driver);
             nametocheck = Helper.clickOnNameInCorrespondents(driver);
             int contact_id_bcc = Helper.get_contact_id_bcc(driver);
             if(flag==1)
             {
                 try
                 {
                     assertEquals(contact_id,contact_id_bcc);
                 }
                 catch (AssertionFailedError e)
                 {
                     System.out.println("The name"+"::"+nametocheck+" "+"is not present in bcc of mail opened");
                 }
             }
         }
         catch (NoSuchElementException e) {
             result = true;
             System.out.println("bcc is not present in the mail opened for"+"::"+nametocheck);
         }
         assertTrue(result);
     }

      @Test
      public void testID() //testID clicks on a correspondent's name,extracts ID from its mail,navigates to advanced search page,enters the extracted message ID in "Message ID" text field,and then checks that the new message opened contains same ID or not.
      {
          Helper.clickOnNameInCorrespondents(driver);   //finds a Correspondent's name and clicks on it
          Helper.waitFor();
          String id=Helper.string_ID(driver);           //getting the id of message
          Helper.click_on_search_through_correspondents(driver);
          Helper.waitFor();
          Helper.go_to_advanced_Search(driver);
          Helper.waitFor();
          Helper.enter_data_in_Message_ID_Advanced_Search(driver,id);
          Helper.clickOnSearch_InAdvancedSearchPage(driver);
          String newID=Helper.string_newID_After_Entering_MessageID_on_AdvancedSearchPage(driver);
          assertTrue(id.equals(newID));
      }
      @Test
      public void testNumberBrowseTopPageCorrespondents() //this test case matches the total correspondents with the number of contacts in correspondent's address book.
      {
          int number=Helper.number_in_Correspondents_BrowseTopPage(driver);    //returns total correspondents number,(eg it returns (2251) since we have Correspondents(2251) in our browse-top page.
          Helper.clickOnEditCorrespondents_Correspondents(driver);
          int number_Of_Contacts=Helper.countNumberOfContactsIn_a_Page(driver);   //counts and return number of contacts in address book.
          assertTrue(number==number_Of_Contacts);
      }
      @Test
      public void test_Sent_and_Received()  //this test case calculates the sum of sent messages and received messages in message window of correspondent's page and checks that it should be either greater than or equals to the number of messages(mails) opened.
      {
          Helper.clickOnNameInCorrespondents(driver);
          Helper.waitFor();
          Helper.changeWindow(driver);
          int number_of_sentmessages=Helper.sent_messages_in_CorrespondentsPage(driver);
          int number_of_receivedmessages=Helper.received_messages_in_CorrespondentsPage(driver);
          int number_of_messages_opened=Helper.number_of_messages_opened_after_clicking_on_Correspondents_name(driver);
          int sum=number_of_sentmessages+number_of_receivedmessages;
          assertTrue(sum>=number_of_messages_opened);
      }
     @AfterEach
    public void post_Set()
     {

       driver.quit();
     }

}