package com.amuselabs.test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.io.InputStream;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//TEST CASES FOR LABELS

public class TestSuite_LabelsTest
{
    Helper helper=new Helper("firefox");
    public static Properties user_interface =new Properties();

    @BeforeAll
    public static void start_epadd()
    {
        try {
            Helper.start_ePADD();
            InputStream s = TestSuite_LabelsTest.class.getClassLoader().getResourceAsStream("USER_INTERFACE.properties");
            user_interface.load(s);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void pre_Set()
    {

        helper.click_on_labels();
    }

    @Test
    public void test_04_Adding_a_NewLabel()  //this test case counts the initial number of Labels on page,then adds a new label and checks whether
    {                                    //the original number of Labels has incremented by one or not.
        try {
            int number_of_labels_beforeAdding = helper.number_of_labels();
            helper.click_on_new_label();
            helper.enter_data_in_label_name();
            helper.click_on_label_type();
            helper.choose_label_type();
            helper.enter_data_in_label_description();
            helper.click_on_update();
            helper.click_on_ok();
            int number_of_labels_afterAdding = helper.number_of_labels();
            assertEquals(number_of_labels_beforeAdding + 1, number_of_labels_afterAdding);
            System.out.println("Initial number of Labels="+" "+number_of_labels_beforeAdding);
            System.out.println("New number of Labels="+" "+number_of_labels_afterAdding);
        }
        catch (AssertionFailedError e)
        {
            System.out.println("Adding a new Label has not incremented the count of Labels By 1");
        }
    }
    @Test
    public void test_04_Edit_label_name()  //this test case edits label name ,(for eg "Reviewed changed to "Reviewedxy") and then checks whether the changes made
    {                                  //is reflected in "Labels" page or not.
        String label_name="",edited_name="",new_label_name="";
        try {
            label_name = helper.get_label_name();
            helper.click_on_edit_label();
            edited_name = helper.edit_label_name(label_name);
            helper.click_on_update();
            helper.click_on_ok();
            new_label_name = helper.get_label_name();
            assertTrue(new_label_name.equals(edited_name));
            System.out.println("Original Label name is "+label_name);
            System.out.println("Edited Name is "+edited_name);
            System.out.println("New Label name displayed is "+new_label_name);
        }
        catch (AssertionFailedError e)
        {
            System.out.println("Change in the name of label has not done properly");
            System.out.println("Initial name was"+label_name);
            System.out.println("Edited name is"+edited_name);
            System.out.println("Changed name is"+new_label_name);
        }
    }
    @Test
    public void test_04_edit_label_type() //this test case edits label type ,(for eg type of "Reviewed" changed to "Restriction") and then checks whether the changes made
                                      //is reflected in "Labels" page or not.
    {
      String label_type=helper.get_label_type();
      helper.click_on_edit_label();
      helper.click_on_label_type();
      helper.choose_label_type();
      helper.click_on_update();
      helper.click_on_ok();
      String new_label_type=helper.get_label_type();
      if(label_type.equals("General"))
      {
          assertTrue(new_label_type.equals("Restriction"));
          System.out.println("Original label type is "+label_type);
          System.out.println("New label type is "+new_label_type);
      }
      else
      {
          assertTrue(new_label_type.equals("General"));
          System.out.println("Original label type is "+label_type);
          System.out.println("New label type is "+new_label_type);
      }
    }
    @Test
    public void test_04_Adding_label_to_a_message()  //this test case initially stores the number displayed in front of a Label name,that number denotes that
    {                                            //on how many messages that particular label has been attached.It then adds a label to a message and then
        try {
            int initial_number_of_labels_on_messages = helper.number_of_labels_on_messages();//checks whether the number in front of that particular label
            helper.click_on_Correspondents_BrowseTopPage_through_labels();//has increemented by one or not.
            helper.clickOnNameInCorrespondents_through_labels();
            helper.click_on_label_in_message_window_of_correspondents_and_choose_a_label();
            helper.click_on_labels();
            helper.waitFor(5);
            int final_number_of_labels_on_messages = helper.number_of_labels_on_messages();
            assertEquals(initial_number_of_labels_on_messages + 1, final_number_of_labels_on_messages);
            System.out.println("Original number of labels attached to messages is "+initial_number_of_labels_on_messages);
            System.out.println("New number of labels attached to messages is"+final_number_of_labels_on_messages);
        }
        catch (AssertionFailedError e)
        {
            System.out.println("After adding label to a single message,number in front of corresponding label has not incremented by 1");
        }
    }
    @Test
    public void test_04_Correct_number_of_mails_labelled() //this test case extracts the number displayed in front of a Label and stores it,
    {                                                  //it then clicks on that Label and checks whether the total messages opened is equal to number stored or not.
      int number_of_labels_on_messages=helper.number_of_labels_on_messages();
      int actual_number_of_messages_opened_with_labels=0;
      if(number_of_labels_on_messages==0)
      {
          helper.click_on_label_and_return_its_name();
          String s=helper.get_string_no_matching_messages();
          assertTrue(s.equals("No matching messages."));
          System.out.println("Since the number of labels attached to messages were "+number_of_labels_on_messages+" therefore displayed message is "+s);
      }
      else {
          try {
              helper.click_on_label_and_return_its_name();
              actual_number_of_messages_opened_with_labels = helper.number_of_messages_opened_after_clicking_on_a_name();
              assertEquals(number_of_labels_on_messages, actual_number_of_messages_opened_with_labels);
              System.out.println("Number of Labels on messages "+number_of_labels_on_messages);
              System.out.println("Number of messages opened on clicking that label is "+actual_number_of_messages_opened_with_labels);
          }
          catch (AssertionFailedError e)
          {
              System.out.println("Number mentioned in front of label does not match with the number of messages opened on clicking that label::");
              System.out.println("Expected"+"="+number_of_labels_on_messages);
              System.out.println("Actual"+"+"+actual_number_of_messages_opened_with_labels);
          }
      }
    }
    @AfterEach
    public void post_Set()
    {

        helper.close_browser();
    }
}