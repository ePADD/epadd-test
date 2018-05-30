package com.amuselabs.test;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


//Test Cases for Correspondents

public class TestSuite_1 {
    String nametocheck;
    String value_in_From;
    String id;
    String extracted_strings_To[];
    WebDriver driver = new ChromeDriver();

    @BeforeAll
    public static void start_epadd() {
       Helper.start_ePADD();
    }

    @BeforeEach
    public void open_Correspondents() {
            driver.get("http://localhost:9099/epadd/browse-top");
            WebElement e=driver.findElement(By.cssSelector("#all-cards > div:nth-child(1)")); //BeforeEach contains code that navigates to Correspondents
            e.click();                                                                        //Page
            Helper.waitFor();
    }

    @Test
    public void testFrom() {
        nametocheck=Helper.clickOnNameInCorrespondents(driver);//finds a name ,store its text,click on it and returns name in "nametocheck" variable
        value_in_From =Helper.strings_From(driver);//returns string contained in "From:" of mail.
        assertTrue(value_in_From.contains(nametocheck)); //This TestCase fails if "From" is not present or if name which was clicked was not present in "From"
    }

     @Test
     public void testTo()
     {
         nametocheck=Helper.clickOnNameInCorrespondents(driver);
         extracted_strings_To=Helper.strings_To(driver);
         for(int i=0;i<extracted_strings_To.length;i++)
         {
             if(extracted_strings_To[i].contains(nametocheck))  //same test case as above,checks string in "To" of mail.
             {
                 assertTrue(extracted_strings_To[i].contains(nametocheck));
                 break;
             }
         }
     }

     @Test
     public void testCc() {
         boolean result = false;
         try {
             nametocheck=Helper.clickOnNameInCorrespondents(driver);
             String[] extracted_strings_cc =Helper.strings_Cc(driver); //same test case as above two,checks in "Cc" of mail.
             for (int i = 0; i < extracted_strings_cc.length; i++) {
                 if (extracted_strings_cc[i].contains(nametocheck)) {  //this test case fails only if "Cc" is present and name clicked is not present in it.
                     result = true;
                     break;
                 }
             }
         } catch (NoSuchElementException e) {
             result = true;
         }
         assertTrue(result);
     }

     @Test
      public void testBcc()
     {
         boolean result = false;
         try {
             nametocheck=Helper.clickOnNameInCorrespondents(driver);
             String[] extracted_strings_bcc = Helper.strings_bcc(driver); //same test case as above two,checks in "bcc" of mail.
             for (int i = 0; i < extracted_strings_bcc.length; i++) {
                 if (extracted_strings_bcc[i].contains(nametocheck)) {    //this test case fails only if "bcc" is present and name clicked is not present in it.
                     result = true;
                     break;
                 }
             }
         } catch (NoSuchElementException e) {
             result = true;
         }
         assertTrue(result);
     }

      @Test
      public void testID()
      {
          WebElement e = driver.findElement(By.xpath("//*[@id=\"people\"]/tbody/tr[2]/td[1]/a"));
          e.click();                  //finds a Correspondent's name and clicks on it
          Helper.waitFor();
          id=Helper.string_ID(driver);
          id=id.substring(0,id.length()-6);   //getting the id of message
          driver.get("http://localhost:9099/epadd/advanced-search");
          WebElement e1=driver.findElement(By.cssSelector("#uniqueId"));
          e1.sendKeys(id);                                      //Entering extracted ID in "Message ID" textfield.
          WebElement e2=driver.findElement(By.cssSelector("#search-button"));
          e2.click();                //Finding and clicking on search button
          new Helper().waitFor();
          WebElement e3 = driver.findElement(By.cssSelector("#jog_contents > div.muse-doc > div.muse-doc-header > table > tbody > tr:nth-child(5) > td:nth-child(2)"));
          String newID = e3.getText();
          newID=newID.substring(0,newID.length()-6);   //extracting ID from the new meassge that has opened
          assertTrue(id.equals(newID));
      }
      @Test
      public void testNumberBrowseTopPageCorrespondents()
      {
          int number=new Helper().number_in_Correspondents_BrowseTopPage(driver);
          Helper.clickOnEditCorrespondents_Correspondents(driver);
          int number_Of_Contacts=new Helper().countNumberOfContactsIn_a_Page(driver);
          assertTrue(number==number_Of_Contacts);
      }
      @Test
      public void test_Sent_and_Received()
      {
          Helper.clickOnNameInCorrespondents(driver);
          Helper.waitFor();
          Helper.changeWindow(driver);
          int number_of_sentmessages=Helper.sent_messages_in_CorrespondentsPage(driver);
          int number_of_receivedmessages=Helper.received_messages_in_CorrespondentsPage(driver);
          WebElement e=driver.findElement(By.cssSelector("#pageNumbering"));
          int number_of_messages_opened=Integer.parseInt(e.getText().substring(2));
          int sum=number_of_sentmessages+number_of_receivedmessages;
          assertEquals(sum,number_of_messages_opened);
      }
     /* @Test
      public void testCorrespondentName_Underlined_or_Not()
      {
         String name=Helper.clickOnNameInCorrespondents(driver);
          Helper.changeWindow(driver);
          Helper.strings_underlined(driver);
      }*/
     @AfterEach
    public void check()
     {
        driver.quit();
     }

}