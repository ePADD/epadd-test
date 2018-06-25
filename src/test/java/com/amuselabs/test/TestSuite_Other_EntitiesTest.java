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

public class TestSuite_Other_Entities
{
    WebDriver driver=new ChromeDriver();
    public static Properties user_interface =new Properties();

    @BeforeAll
    public static void start_epadd() {
        try {
            Helper.start_ePADD();
            InputStream s = TestSuite_Other_Entities.class.getClassLoader().getResourceAsStream("USER_INTERFACE.properties");
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
        Helper.clickOnOtherEntities(driver);
    }

    @Test
    public void testNumber()  //this test case checks whether the number displayed in front of subentity(eg 38 in front of United States) is equal to the number of
    {                           //messages opened on clicking that particular subentity or not.
        int message_number=0,actual_number=0;
        try {
            Helper.clickOnEntityName_OtherEntitiesPage(driver);
            message_number = Helper.message_number_OtherEntitiesPage(driver);
            Helper.clickOnSubEntityName_OtherEntitiesPage(driver);
           // driver.get("http://localhost:9099/epadd/browse?archiveID=84e8afd01303201b1bb2e763c78c2df0f7133d3004e1ce6e04d9b5e2e3d15423&adv-search=1&term=%22United%20States%22&termBody=on&termSubject=on");
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
    public void testEntityName()  //this test case clicks on a sub-entity (eg United States)and checks whether the opened messages contains United States or not.
    {
        Helper.clickOnEntityName_OtherEntitiesPage(driver);
        String sub_entity_name=Helper.clickOnSubEntityName_OtherEntitiesPage(driver);
      //  driver.get("http://localhost:9099/epadd/browse?archiveID=84e8afd01303201b1bb2e763c78c2df0f7133d3004e1ce6e04d9b5e2e3d15423&adv-search=1&term=%22United%20States%22&termBody=on&termSubject=on");
        String body=Helper.body_of_mail_opened_onclick_subEntity(driver);
        assertTrue(body.contains(sub_entity_name));
    }
     @Test
     public void testOther_Entity_Name_Advanced_Search()  //this test case clicks on a sub-entity type,stores it,(eg United States),navigates till advanced search page
     {                                          //enters that sub-entity type in "Entity" textfield,clciks on search, and then checks whether the messages opened contains
         Helper.clickOnEntityName_OtherEntitiesPage(driver);   //that sub-entity or not.
         String entity_name=Helper.clickOnSubEntityName_OtherEntitiesPage(driver);
         Helper.click_on_search(driver);
         Helper.go_to_advanced_Search(driver);
         Helper.enter_data_in_Entity_Textfield_InAdvancedSearchPage(driver,entity_name);
         Helper.clickOnSearch_InAdvancedSearchPage(driver);
         Helper.waitFor();
         String body=Helper.whole_mail_after_entering_entity_in_Advanced_Search_Page(driver);
         assertTrue(body.contains(entity_name));
     }
     @Test
     public void testOther_Entity_Message_Number_Advanced_Search() //this test case clicks on a sub-entity type,stores number in front of it,(eg 69 in front of United States),navigates till advanced search page
   {                                            //enters that sub-entity type in "Entity" textfield,clicls on search, and then checks whether the number messages opened
       int number=0,actual_number=0;          //is equals to the number stored or not.
       try {
           Helper.clickOnEntityName_OtherEntitiesPage(driver);
           number = Helper.message_number_OtherEntitiesPage(driver);
           String entity_name = Helper.clickOnSubEntityName_OtherEntitiesPage(driver);
           Helper.click_on_search(driver);
           Helper.go_to_advanced_Search(driver);
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
    public void testNumberOtherEntities() //this test case stores the number displayed in front of Entity name(eg 460 in front of "Place"),then clicks on
    {                                    //that particular entity  and checks whether the number is equal to the number of contacts on that page or not.
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
    public void testSent_and_Received() //this test case checks whether the sum of sent and received messages present in message window of a particular
    {                                       //Other-entity is greater than or equal to the number of messages opened or not..
        Helper.clickOnEntityName_OtherEntitiesPage(driver);
        Helper.clickOnSubEntityName_OtherEntitiesPage(driver);
        Helper.waitFor();
       // driver.get("http://localhost:9099/epadd/browse?archiveID=84e8afd01303201b1bb2e763c78c2df0f7133d3004e1ce6e04d9b5e2e3d15423&adv-search=1&term=%22United%20States%22&termBody=on&termSubject=on");
        int number_of_sentmessages=Helper.sent_messages_in_OtherEntitiesPage(driver);
        int number_of_receivedmessages=Helper.received_messages_in_OtherEntitiesPage(driver);
        int sum=number_of_sentmessages+number_of_receivedmessages;
        int number_of_messages_opened=Helper.number_of_messages_opened_after_clicking_on_subEntity_name(driver);
        assertTrue(sum>=number_of_messages_opened);
    }
    @AfterEach
    public void post_Set()
    {

      driver.quit();
    }
}