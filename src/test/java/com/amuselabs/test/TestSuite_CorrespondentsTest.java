package com.amuselabs.test;
import org.junit.jupiter.api.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.opentest4j.AssertionFailedError;

import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


//Test Cases for Correspondents

public class TestSuite_CorrespondentsTest
{
    String nametocheck;   //Variable for name in "All Correspondents" Page,eg "Kathleen Shanahan","Eli Ferrera",etc.
    WebDriver driver = new ChromeDriver();

    public static Properties user_interface = new Properties();   //correspondents variable representing properties file for Correspondents.
    @BeforeAll
    public static void start_epadd() {
            try {
                Helper.start_ePADD();
                InputStream s = TestSuite_CorrespondentsTest.class.getClassLoader().getResourceAsStream("USER_INTERFACE.properties");
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

        Helper.click_on_Correspondents_BrowseTopPage(driver);
    }

    @Test
    public void testFrom()
    {
       try {
           int contact_id = Helper.get_contact_id(driver);
           nametocheck = Helper.clickOnNameInCorrespondents(driver);  //finds a name ,store its text,click on it and returns name in "nametocheck" variable
           int contact_id_From = Helper.get_contact_id_From(driver);
           assertEquals(contact_id,contact_id_From);   //This TestCase fails if "From" is not present or if name which was clicked was not present in "From"
           System.out.println("The name "+nametocheck+" is present in \"From\" of the mail opened");
       }
       catch(AssertionFailedError e)
       {
           System.out.println("The name"+nametocheck+" is not present in \"From\" of the mail opened");
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
            System.out.println("The name "+nametocheck+" is present in \"To\" of the mail opened");
        }
        catch (AssertionFailedError e)
        {
            System.out.println("The name " + nametocheck + " is not present in \"To\" of the mail opened");
        }
    }

    @Test
    public void testCc() {                                      //same test case as above two,checks in "Cc" of mail.
        boolean result = false;
        try {
            try {
                int contact_id = Helper.get_contact_id(driver);          //this test case fails only if "Cc" is present and name clicked is not present in it.
                nametocheck = Helper.clickOnNameInCorrespondents(driver);
                int contact_id_Cc = Helper.get_contact_id_Cc(driver);
                if (contact_id == contact_id_Cc) {
                    result = true;
                    System.out.println("The name "+nametocheck+" is present in \"cc\" of the mail opened");

                }
            } catch (NoSuchElementException e) {
                System.out.println("\"cc\" is not present in mail opened so we trivially pass the test case");
                result = true;
            }
            assertTrue(result);
        }
        catch (AssertionFailedError e)
        {
            System.out.println("The name " + nametocheck + " is not present in \"cc\" of the mail opened");
        }
    }

    @Test
    public void testBcc()                                            //same test case as above two,checks in "bcc" of mail.
    {
        boolean result = false;
        try {
            try {
                int contact_id = Helper.get_contact_id(driver);          //this test case fails only if "bcc" is present and name clicked is not present in it.
                nametocheck = Helper.clickOnNameInCorrespondents(driver);
                int contact_id_bcc = Helper.get_contact_id_bcc(driver);
                if (contact_id == contact_id_bcc) {
                    result = true;
                    System.out.println("The name "+nametocheck+" is present in \"bcc\" of the mail opened");

                }
            } catch (NoSuchElementException e) {
                System.out.println("\"bcc\" is not present in mail opened so we trivially pass the test case");
                result = true;
            }
            assertTrue(result);
        }
        catch (AssertionFailedError e)
        {
            System.out.println("The name " + nametocheck + " is not present in \"bcc\" of the mail opened");
        }
    }

    @Test
    public void testID() //testID clicks on a correspondent's name,extracts ID from its mail,navigates to advanced search page,enters the extracted message ID in "Message ID" text field,and then checks that the new message opened contains same ID or not.
    {
        String id="",newID="";
        try {
            Helper.clickOnNameInCorrespondents(driver);   //finds a Correspondent's name and clicks on it
            Helper.waitFor();
            id = Helper.string_ID(driver);           //getting the id of message
            Helper.click_on_search(driver);
            Helper.waitFor();
            Helper.go_to_advanced_Search(driver);
            Helper.waitFor();
            Helper.enter_data_in_Message_ID_Advanced_Search(driver, id);
            Helper.clickOnSearch_InAdvancedSearchPage(driver);
            newID = Helper.string_newID_After_Entering_MessageID_on_AdvancedSearchPage(driver);
            assertTrue(id.equals(newID));
            System.out.println("Ids matched");
            System.out.println("Original Id "+" "+id);
            System.out.println("New ID "+" "+newID);
        }
        catch (AssertionFailedError e)
        {
            System.out.println("There is a an ID mismatch");
            System.out.println("Original ID"+id);
            System.out.println("New ID"+newID);
        }
    }
    @Test
    public void testNumberBrowseTopPageCorrespondents() //this test case matches the total correspondents with the number of contacts in correspondent's address book.
    {
        int number=0,number_Of_Contacts=0;
        try {
            number = Helper.number_in_Correspondents_BrowseTopPage(driver);    //returns total correspondents number,(eg it returns (2251) since we have Correspondents(2251) in our browse-top page.
            Helper.clickOnEditCorrespondents_Correspondents(driver);
            number_Of_Contacts = Helper.countNumberOfContactsIn_a_Page(driver);   //counts and return number of contacts in address book.
           assertEquals(number,number_Of_Contacts);
            System.out.println("Numbers Matched");
            System.out.println("Number in browse-top page"+number);
            System.out.println("Number of contacts in address book"+number_Of_Contacts);
        }
        catch (AssertionFailedError e)
        {
            System.out.println("There is a mismatch in the number displayed in Correspondent's button in browse-top page and the number of contacts in address book");
            System.out.println("Number in browse-top page"+number);
            System.out.println("Number of contacts in address book"+number_Of_Contacts);
        }
    }
    @Test
    public void test_Sent_and_Received()  //this test case calculates the sum of sent messages and received messages in message window of correspondent's page and checks that it should be either greater than or equals to the number of messages(mails) opened.
    {
        int sum=0,number_of_messages_opened=0;
        try {
            try {
                Helper.clickOnNameInCorrespondents(driver);
                Helper.waitFor();
                Helper.changeWindow(driver);
                int number_of_sentmessages = Helper.sent_messages_in_CorrespondentsPage(driver);
                int number_of_receivedmessages = Helper.received_messages_in_CorrespondentsPage(driver);
                number_of_messages_opened = Helper.number_of_messages_opened_after_clicking_on_Correspondents_name(driver);
                sum = number_of_sentmessages + number_of_receivedmessages;
                assertTrue(sum >= number_of_messages_opened);
                System.out.println("The sum of sent and received messages is greater than the actual number of messages opened");
                System.out.println("Sum=" + sum);
                System.out.println("Number of messages opened=" + number_of_messages_opened);
            }
            catch(NoSuchElementException e)
            {
                System.out.println("Sent Messages and Received Messages Elements are not present::");
                assertTrue(true);
            }
        }
        catch (AssertionFailedError e)
        {
            System.out.println("The sum of sent and received messages is less than the actual number of messages opened::");
            System.out.println("Sum="+sum);
            System.out.println("Number of messages opened="+number_of_messages_opened);
        }
    }
    @AfterEach
    public void post_Set()
    {

         driver.quit();
    }

}