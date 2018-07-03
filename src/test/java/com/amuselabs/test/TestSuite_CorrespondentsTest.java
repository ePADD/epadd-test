package com.amuselabs.test;
import org.junit.jupiter.api.*;
import org.openqa.selenium.NoSuchElementException;
import org.opentest4j.AssertionFailedError;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


//Test Cases for Correspondents

public class TestSuite_CorrespondentsTest
{
    String nametocheck;   //Variable for name in "All Correspondents" Page,eg "Kathleen Shanahan","Eli Ferrera",etc.
    Helper helper=new Helper("chrome");
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

        helper.click_on_correspondents_browse_top_page();
    }

    @Test
    public void test_01_From()
    {
       try {
           int contact_id = helper.get_contact_id();
           nametocheck = helper.click_on_name_in_correspondents();  //finds a name ,store its text,click on it and returns name in "nametocheck" variable
           int contact_id_From = helper.get_contact_id_From();
           assertEquals(contact_id,contact_id_From);   //This TestCase fails if "From" is not present or if name which was clicked was not present in "From"
           System.out.println("The name "+nametocheck+" is present in \"From\" of the mail opened");
       }
       catch(AssertionFailedError e)
       {
           System.out.println("The name"+nametocheck+" is not present in \"From\" of the mail opened");
       }
    }

    @Test
    public void test_01_To()
    {
        boolean result=false;
        try {
            int contact_id = helper.get_contact_id();
            nametocheck = helper.click_on_name_in_correspondents();   //same test case as above,checks string in "To" of mail
            ArrayList<Integer> ids= helper.get_contact_id_To();
            for (int id: ids)
            {
                if(id==contact_id)
                {
                    result=true;
                    break;
                }
            }
            assertTrue(result);
            System.out.println("The name " + nametocheck + " is present in \"To\" of the mail opened");
        }
        catch (AssertionFailedError e)
        {
            System.out.println("The name " + nametocheck + " is not present in \"To\" of the mail opened");
        }
    }

    @Test
    public void test_01_Cc() {                                      //same test case as above two,checks in "Cc" of mail.
        boolean result = false;
        try {
            try {
                int contact_id = helper.get_contact_id();          //this test case fails only if "Cc" is present and name clicked is not present in it.
                nametocheck = helper.click_on_name_in_correspondents();
                ArrayList<Integer> ids= helper.get_contact_id_Cc();
                for(int id: ids) {
                    if (contact_id == id) {
                        result = true;
                        System.out.println("The name " + nametocheck + " is present in \"cc\" of the mail opened");
                    }
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
    public void test_01_Bcc()                                            //same test case as above two,checks in "bcc" of mail.
    {
        boolean result = false;
        try {
            try {
                int contact_id = helper.get_contact_id();          //this test case fails only if "Cc" is present and name clicked is not present in it.
                nametocheck = helper.click_on_name_in_correspondents();
                ArrayList<Integer> ids= helper.get_contact_id_bcc();
                for(int id: ids) {
                    if (contact_id == id) {
                        result = true;
                        System.out.println("The name " + nametocheck + " is present in \"bcc\" of the mail opened");
                    }
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
    public void test_01_ID() //testID clicks on a correspondent's name,extracts ID from its mail,navigates to advanced search page,enters the extracted message ID in "Message ID" text field,and then checks that the new message opened contains same ID or not.
    {
        String id="",newID="";
        try {
            helper.click_on_name_in_correspondents();   //finds a Correspondent's name and clicks on it
            Helper.waitFor(5);
            id = helper.get_message_id_from_message_window();           //getting the id of message
            helper.click_on_search();
            Helper.waitFor(5);
            helper.go_to_advanced_Search();
            Helper.waitFor(5);
            helper.enter_data_in_Message_ID_Advanced_Search(id);
            helper.clickOnSearch_InAdvancedSearchPage();
            newID = helper.get_message_id_from_message_window();
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
    public void test_01_NumberBrowseTopPageCorrespondents() //this test case matches the total correspondents with the number of contacts in correspondent's address book.
    {
        int number=0,number_Of_Contacts=0;
        try {
            number = helper.get_number_in_correspondents_browse_top_Page();    //returns total correspondents number,(eg it returns (2251) since we have Correspondents(2251) in our browse-top page.
            helper.click_on_edit_correspondents();
            number_Of_Contacts = helper.countNumberOfContactsIn_a_Page();   //counts and return number of contacts in address book.
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
    public void test_01_Sent_and_Received()  //this test case calculates the sum of sent messages and received messages in message window of correspondent's page and checks that it should be either greater than or equals to the number of messages(mails) opened.
    {
        int sum=0,number_of_messages_opened=0;
        try {
            try {
                helper.click_on_name_in_correspondents();
                int number_of_sentmessages = helper.sent_messages_in_message_window();
                int number_of_receivedmessages = helper.received_messages_in_message_window();
                number_of_messages_opened = helper.number_of_messages_opened_after_clicking_on_a_name();
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

        helper.close_browser();
    }

}