package com.amuselabs.test;
import org.junit.jupiter.api.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.opentest4j.AssertionFailedError;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


//Test Cases for Correspondents

public class TestSuite_Correspondents
   {
    String nametocheck;   //Variable for name in "All Correspondents" Page,eg "Kathleen Shanahan","Eli Ferrera",etc.
    WebDriver driver= new ChromeDriver();
    static int flag=0;  //flag variable is used to determine whether "cc" or "bcc" is present in mail or not.
    public static Properties user_interface = new Properties();   //user_interface variable representing, reading selectors from properties file for Correspondents.

    @BeforeAll
    public static void start_epadd() {
        try {
            Helper.start_ePADD();    //method for starting ePADD
            InputStream s = new FileInputStream("/home/ashu18/Projects/epadd_dev/epadd-test/src/java/com/amuselabs/test/USER_INTERFACE.properties");
            user_interface.load(s);       //Reading properties files
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

              Helper.click_on_Correspondents_BrowseTopPage(driver);
              assertTrue(Helper.isCorrespondents_Page_Open(driver));
          }
          catch (AssertionFailedError e)
          {
              System.out.println("Incorrect page opened::");
          }
    }

    @Test
    public void testFrom()   //This TestCase fails if "From" is not present or if name which was clicked was not present in "From"
    {
         int check=0; //check variable determines that if assert fails,then what is the reason,if check=0 assert fails because of incorrect page opened on click of a particular correspondents name.
        try {
            int contact_id = Helper.get_contact_id(driver); //if check=1 and assert fails ,it means that it is because name clicked is not present in "From" of the mail opened.
            nametocheck = Helper.clickOnNameInCorrespondents(driver);//finds a name ,store its text,click on it and returns name in "nametocheck" variable
            assertTrue(Helper.isMessage_Window_Page_Opened(driver));
            check=1;
            int contact_id_From = Helper.get_contact_id_From(driver);
            assertEquals(contact_id, contact_id_From);
        }
        catch(AssertionFailedError e)
        {
            if(check==0)
            {
                System.out.println("Incorrect page opened on clicking a Correspondent's name::");
            }
            if(check!=0) {
                System.out.println("The name" + " " + nametocheck + " " + "is not present in \"From\" of mail opened::");
            }
        }
    }

     @Test
     public void testTo()    //same test case as above,checks string in "To" of mail
     {
         int check=0;
         try {
             int contact_id = Helper.get_contact_id(driver);
             nametocheck = Helper.clickOnNameInCorrespondents(driver);
             assertTrue(Helper.isMessage_Window_Page_Opened(driver));
             check=1;
             int contact_id_to = Helper.get_contact_id_To(driver);
             assertEquals(contact_id, contact_id_to);
         }
         catch(AssertionFailedError e)
         {
             if(check==0) {
                 System.out.println("Incorrect page opened on clicking a Correspondent's name::");
             }
             if(check!=0)
             {
                 System.out.println("The name" + " " + nametocheck + " " + "is not present in \"To\" of mail opened");
             }
         }
     }

     @Test
     public void testCc() {                                      //same test case as above two,checks in "Cc" of mail.
         boolean result = false;
         try {
             int contact_id = Helper.get_contact_id(driver);
             nametocheck = Helper.clickOnNameInCorrespondents(driver);
             ////////////////////////////////////////////////////////
             int contact_id_Cc = Helper.get_contact_id_Cc(driver);
             if(flag==1)//if "NoSuchElementException" occurs then flag will remain zero.If not,that means "cc" is presnt and then check test case according to assertion written.
             {
                 result=true;
                 try
                 {
                     assertEquals(contact_id,contact_id_Cc);
                 }
                 catch (AssertionFailedError e)
                 {
                     System.out.println("The name"+" "+nametocheck+" "+"is not present in \"Cc\" of mail opened");
                 }
             }
         }
         catch (NoSuchElementException e) {
             result = true;
             System.out.println("Cc is not present in the mail opened for"+" "+nametocheck);
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
             //////////////////////////////////////////////////////
             int contact_id_bcc = Helper.get_contact_id_bcc(driver);
             if(flag==1) //if "NoSuchElementException" occurs then flag will remain zero.If not,that means "bcc" is presnt and then check test case according to assertion written.
              {
                  result=true;
                 try
                 {
                     assertEquals(contact_id,contact_id_bcc);
                 }
                 catch (AssertionFailedError e)
                 {
                     System.out.println("The name"+" "+nametocheck+" "+"is not present in \"bcc\" of mail opened");
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
      public void testCorrespondents_name_advanced_search()  //This test case clicks on a correspondent's name(eg Eli Ferrera) and stores it.
      {                                                                //then it navigates to advanced search page.
          nametocheck=Helper.clickOnNameInCorrespondents(driver);//fills same name in Entity name text field and clicks on search
          /////////////////////////////////////////////////
          Helper.click_on_search(driver);                           //now it test whether the name clicked is somewhere present in mails opened or not.
          ////////////////////////////////////////////////
          Helper.go_to_advanced_Search(driver);
          ///////////////////////////////////////////////
          Helper.enter_data_in_Entity_Textfield_InAdvancedSearchPage(driver, nametocheck);
          Helper.clickOnSearch_InAdvancedSearchPage(driver);
          ////////////////////////////////////////////////
          String text_of_entire_mail=Helper.get_text_of_entire_mail_opened(driver);
          assertTrue(text_of_entire_mail.contains(nametocheck));
      }
      @Test
      public void testID() //testID clicks on a correspondent's name,extracts ID from its mail,navigates to advanced search page,enters the extracted message ID in "Message ID" text field,and then checks that the new message opened contains same ID or not.
      {
          Helper.clickOnNameInCorrespondents(driver);   //finds a Correspondent's name and clicks on it
          ///////////////////////////////////////////
          Helper.waitFor();
          String id=Helper.string_ID(driver);           //getting the id of message
          Helper.click_on_search(driver);
          /////////////////////////////////////////
          Helper.waitFor();
          Helper.go_to_advanced_Search(driver);
          ////////////////////////////////////////
          Helper.waitFor();
          Helper.enter_data_in_Message_ID_Advanced_Search(driver,id);
          Helper.clickOnSearch_InAdvancedSearchPage(driver);
          //////////////////////////////////////////
          String newID=Helper.string_newID_After_Entering_MessageID_on_AdvancedSearchPage(driver);
          assertTrue(id.equals(newID));
      }
      @Test
      public void testNumberBrowseTopPageCorrespondents() //this test case matches the total user_interface with the number of contacts in correspondent's address book.
      {
          int number=0,number_Of_Contacts=0;
          try {
              number = Helper.number_in_Correspondents_BrowseTopPage(driver);    //returns total user_interface number,(eg it returns (2251) since we have Correspondents(2251) in our browse-top page.
              Helper.clickOnEditCorrespondents_Correspondents(driver);
              number_Of_Contacts = Helper.countNumberOfContactsIn_a_Page(driver);   //counts and return number of contacts in address book.
              assertTrue(number == number_Of_Contacts);
          }
          catch (AssertionFailedError e)
          {
              System.out.println("There is a mismatch in the number displayed in Correspondents button in browse top page and actual number of contacts in page.");
              System.out.println("Expected="+number);
              System.out.println("Actual="+number_Of_Contacts);
          }
      }
    @Test
      public void test_Sent_and_Received()  //this test case calculates the sum of sent messages and received messages in message window of correspondent's page and checks that it should be either greater than or equals to the number of messages(mails) opened.
      {
          try {
              Helper.clickOnNameInCorrespondents(driver);
              Helper.waitFor();
              int number_of_sentmessages = Helper.sent_messages_in_CorrespondentsPage(driver);
              int number_of_receivedmessages = Helper.received_messages_in_CorrespondentsPage(driver);
              int number_of_messages_opened = Helper.number_of_messages_opened_after_clicking_on_Correspondents_name(driver);
              int sum = number_of_sentmessages + number_of_receivedmessages;
              assertTrue(sum >= number_of_messages_opened);
          }
          catch (NoSuchElementException e)
          {
              System.out.println("");
          }
      }
     @AfterEach
    public void post_Set()
     {

     // driver.quit();
     }

}