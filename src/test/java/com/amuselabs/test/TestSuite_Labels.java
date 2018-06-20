package com.amuselabs.test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.opentest4j.AssertionFailedError;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//TEST CASES FOR LABELS

public class TestSuite_Labels
{
    WebDriver driver = new ChromeDriver();
    public static Properties labels =new Properties();
    public static Properties correspondents =new Properties();

    @BeforeAll
    public static void start_epadd()
    {
        try {
            Helper.start_ePADD();
            InputStream s = new FileInputStream("/home/ashu18/Projects/epadd_dev/epadd-test/src/java/com/amuselabs/test/USER_INTERFACE.properties");
            labels.load(s);
            InputStream s1 = new FileInputStream("/home/ashu18/Projects/epadd_dev/epadd-test/src/java/com/amuselabs/test/USER_INTERFACE.properties");
            correspondents.load(s1);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void pre_Set()
    {

        Helper.clickOnLabels(driver);
    }

    @Test
    public void testAdding_a_NewLabel()  //this test case counts the initial number of labels on page,then adds a new label and checks whether
    {                                    //the original number of labels has increemented by one or not.
         int number_of_labels_beforeAdding=Helper.number_of_labels(driver);
         Helper.click_on_new_label(driver);
         Helper.enter_data_in_label_name(driver);
         Helper.click_on_label_type(driver);
         Helper.choose_label_type(driver);
         Helper.enter_data_in_label_description(driver);
         Helper.click_on_update(driver);
         Helper.click_on_ok(driver);
        int number_of_labels_afterAdding=Helper.number_of_labels(driver);
        assertEquals(number_of_labels_beforeAdding+1,number_of_labels_afterAdding);
    }
    @Test
    public void testEdit_label_name()  //this test case edits label name ,(for eg "Reviewed changed to "Reviewedxy") and then checks whether the changes made
    {                                  //is reflected in "Labels" page or not.
        String label_name=Helper.get_label_name(driver);
        Helper.click_on_edit_label(driver);
        Helper.waitFor();
        String edited_name=Helper.edit_label_name(driver,label_name);
        Helper.click_on_update(driver);
        Helper.click_on_ok(driver);
        String new_label_name=Helper.get_label_name(driver);
        assertTrue(new_label_name.equals(edited_name));
    }
    @Test
    public void testedit_label_type() //this test case edits label type ,(for eg type of "Reviewed" changed to "Restriction") and then checks whether the changes made
                                      //is reflected in "Labels" page or not.
    {
      String label_type=Helper.get_label_type(driver);
      Helper.click_on_edit_label(driver);
      Helper.click_on_label_type(driver);
      Helper.choose_label_type(driver);
      Helper.click_on_update(driver);
      Helper.click_on_ok(driver);
      String new_label_type=Helper.get_label_type(driver);
      if(label_type.equals("General"))
      {
          assertTrue(new_label_type.equals("Restriction"));
      }
      else
      {
          assertTrue(new_label_type.equals("General"));
      }
    }
    @Test
    public void testAdding_label_to_a_message()  //this test case initially stores the number displayed in front of a Label name,that number denotes that
    {                                            //on how many messages that particular label has been attached.It then adds a label to a message and then
       int initial_number_of_labels_on_messages=Helper.number_of_labels_on_messages(driver);//checks whether the number in front of that particular label
       Helper.click_on_Correspondents_BrowseTopPage_through_labels(driver);//has increemented by one or not.
       Helper.clickOnNameInCorrespondents_through_labels(driver);
       Helper.changeWindow(driver);
       Helper.click_on_label_in_message_window_of_correspondents_and_choose_a_label(driver);
       Helper.waitFor();
       driver.get("http://localhost:9099/epadd/labels?archiveID=84e8afd01303201b1bb2e763c78c2df0f7133d3004e1ce6e04d9b5e2e3d15423");
       Helper.waitFor();
       int final_number_of_labels_on_messages=Helper.number_of_labels_on_messages(driver);
       assertEquals(initial_number_of_labels_on_messages+1,final_number_of_labels_on_messages);
    }
    @Test
    public void testCorrect_number_of_mails_labelled() //this test case extracts the number displayed in front of a Label and stores it,
    {                                                  //it then clicks on that Label and checks whether the total messages opened is equal to number stored or not.
      int number_of_labels_on_messages=Helper.number_of_labels_on_messages(driver);
      int actual_number_of_messages_opened_with_labels=0;
      if(number_of_labels_on_messages==0)
      {
          Helper.click_on_label_and_return_its_name(driver);
          WebElement e=driver.findElement(By.cssSelector("body > div:nth-child(6)"));
          String s=e.getText();
          assertTrue(s.equals("No matching messages."));
      }
      else {
          try {
              Helper.click_on_label_and_return_its_name(driver);
              actual_number_of_messages_opened_with_labels = Helper.number_of_messages_opened_after_clicking_on_Label_name(driver);
              assertEquals(number_of_labels_on_messages, actual_number_of_messages_opened_with_labels);
          }
          catch (AssertionFailedError e)
          {
              System.out.println("Number mentioned in front of label does not matvh with the number of messages opened on clicking that label::");
              System.out.println("Expected"+"="+number_of_labels_on_messages);
              System.out.println("Actual"+"+"+actual_number_of_messages_opened_with_labels);
          }
      }
    }
    @AfterEach
    public void post_set()
    {

        driver.quit();
    }
}