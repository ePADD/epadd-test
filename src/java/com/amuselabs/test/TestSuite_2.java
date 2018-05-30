package com.amuselabs.test;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Test Cases for Person Entities

public class TestSuite_2
{
  WebDriver driver=new ChromeDriver();
    String id;

    @BeforeAll
    public static void start_epadd() {
        Helper.start_ePADD();
    }

     @BeforeEach
    public void pre_Set()
     {
         driver.get("http://localhost:9099/epadd/browse-top");
         WebElement e=driver.findElement(By.cssSelector("#all-cards > div:nth-child(2)"));
         e.click();
         Helper.waitFor();
     }

     @Test
    public void testNumber()
     {
         int number=Helper.message_number_PersonEntity(driver); //return the message number(1321 in this case)and also clicks ona name in page.
         WebElement actual_number=driver.findElement(By.cssSelector("#pageNumbering"));//actual message number(y) of form x/y
         String actualNumber=actual_number.getText();
         actualNumber=actualNumber.substring(2);
         int new_number=Integer.parseInt(actualNumber);  //actual number(like 1490 in this case).
         assertTrue(number==new_number);
     }
     @Test
     public void testName()
     {
         String name=Helper.name_from_Person_Entity(driver);
         Helper.waitFor();
         Helper.changeWindow(driver);
         WebElement e1=driver.findElement(By.cssSelector("#jog_contents > div.muse-doc > div.muse-doc-body"));
         String body=e1.getText();
         assertTrue(body.contains(name));//checks for the name only in body of mail not in Cc,Bcc,To and From.
     }
     @Test
     public void testPerson_Entity_Name_Advanced_Search()
     {
        String name=new Helper().name_from_Person_Entity(driver);
        Helper.changeWindow(driver);
        WebElement e1=driver.findElement(By.cssSelector("#bs-example-navbar-collapse > ul:nth-child(1) > li:nth-child(3) > a"));
        e1.click();
        Helper.go_to_advanced_Search(driver);
        WebElement e2=driver.findElement(By.cssSelector("#entity"));
        e2.sendKeys(name);
        Helper.clickOnSearch_InAdvancedSearchPage(driver);
        WebElement e3=driver.findElement(By.cssSelector("#jog_contents"));
        String whole_text=e3.getText();
        assertTrue(whole_text.contains(name));
     }
     @Test
     public void testPerson_Entity_Message_Number_Advanced_Search()
     {
         WebElement e=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td:nth-child(1) > span"));
         String name=e.getText();
         WebElement e1=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td.sorting_2"));
         int message_number=Integer.parseInt(e1.getText());
         e1.click();
         Helper.changeWindow(driver);
         WebElement e2=driver.findElement(By.cssSelector("#bs-example-navbar-collapse > ul:nth-child(1) > li:nth-child(3) > a"));
         e2.click();
         new Helper().go_to_advanced_Search(driver);
         WebElement e3=driver.findElement(By.cssSelector("#entity"));
         e3.sendKeys(name);
         Helper.clickOnSearch_InAdvancedSearchPage(driver);
         WebElement e4=driver.findElement(By.cssSelector("#pageNumbering"));
         String num=e4.getText().substring(2);
         int new_number=Integer.parseInt(num);
         assertTrue(message_number==new_number);
     }
     @Test
     public void testNumberBrowseTopPagePersonEntities()
     {
       int number=Helper.number_in_PersonEntity_BrowseTopPage(driver);
       Helper.clickOnEditEntities_PersonEntity(driver);
       int number_Of_Contacts=Helper.countNumberOfContactsIn_a_Page(driver);
       assertFalse(number==number_Of_Contacts);
     }
     @Test
     public void test_Sent_and_Received()
     {
         Helper.name_from_Person_Entity(driver);
         Helper.waitFor();
         Helper.changeWindow(driver);
         int number_of_sentmessages=Helper.sent_messages_in_PersonEntitiesPage(driver);
         int number_of_receivedmessages=Helper.received_messages_in_PersonEntitiesPage(driver);
         WebElement e=driver.findElement(By.cssSelector("#pageNumbering"));
         int number_of_messages_opened=Integer.parseInt(e.getText().substring(2));
         int sum=number_of_sentmessages+number_of_receivedmessages;
         assertEquals(sum,number_of_messages_opened);
     }
     @Test
     public void testPerson_Entity_Name_Underlined_or_Not() {
         String name = Helper.name_from_Person_Entity(driver);
         Helper.changeWindow(driver);
         List<WebElement> underline_entities =Helper.strings_underlined(driver);
         int count = 0;
         for (WebElement e : underline_entities) {
             String s = e.getText();
             if (s.compareTo(name) == 0) {
                 count++;
             }
             assertTrue(count == 6);
         }
     }
     @Test
     public void testPerson_Entity_Name_highlighted_or_Not()
     {
         String name=Helper.name_from_Person_Entity(driver);
         Helper.changeWindow(driver);
         List<WebElement> highlighted_entities=Helper.strings_highlighted(driver);
         int count=0;
         for(WebElement e:highlighted_entities)
         {
             String s=e.getText();
             if(s.compareTo(name)==0) {
                 count++;
             }
         }
         assertTrue(count==6);
     }
     @AfterEach
     public void post_Set()
     {
         //driver.quit();
     }
}