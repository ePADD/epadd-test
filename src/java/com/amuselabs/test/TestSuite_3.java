package com.amuselabs.test;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Test cases for the "Other Entities" page.

public class TestSuite_3
{
    WebDriver driver=new ChromeDriver();

    @BeforeAll
    public static void start_epadd() {
        Helper.start_ePADD();
    }

    @BeforeEach
    public void pre_Set()
    {

        driver.get("http://localhost:9099/epadd/browse-top");
        WebElement e=driver.findElement(By.cssSelector("#all-cards > div:nth-child(3)"));
        e.click();
        Helper.waitFor();
    }

    @Test
    public void testNumber()
    {
        Helper.clickOnEntityName_OtherEntitiesPage(driver);
        WebElement e1=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td:nth-child(1) > span"));
        WebElement e2=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td.sorting_2"));
        int number=Integer.parseInt(e2.getText());
        e1.click();
        driver.get("http://localhost:9099/epadd/browse?archiveID=84e8afd01303201b1bb2e763c78c2df0f7133d3004e1ce6e04d9b5e2e3d15423&adv-search=1&term=%22United%20States%22&termBody=on&termSubject=on");
        Helper.waitFor();
        WebElement e3=driver.findElement(By.cssSelector("#pageNumbering"));
        String actualNumber=e3.getText();
        actualNumber=actualNumber.substring(2);
        int new_number=Integer.parseInt(actualNumber);
        assertFalse(number==new_number);
    }
    @Test
    public void testEntityName()
    {
        Helper.clickOnEntityName_OtherEntitiesPage(driver);
        WebElement e1=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td:nth-child(1) > span"));
        String entity_name=e1.getText();
        e1.click();
        driver.get("http://localhost:9099/epadd/browse?archiveID=84e8afd01303201b1bb2e763c78c2df0f7133d3004e1ce6e04d9b5e2e3d15423&adv-search=1&term=%22United%20States%22&termBody=on&termSubject=on");
        Helper.waitFor();
        Helper.waitFor();
        Helper.waitFor();
        WebElement e2=driver.findElement(By.cssSelector("#jog_contents > div.muse-doc > div.muse-doc-body"));
        String body=e2.getText();
        assertTrue(body.contains(entity_name));
    }
     @Test
     public void testEntityNameAdvacedSearch()
     {
         Helper.clickOnEntityName_OtherEntitiesPage(driver);
         String entity_name=Helper.clickOnSubEntityName_OtherEntitiesPage(driver);
         WebElement e1=driver.findElement(By.cssSelector("#bs-example-navbar-collapse > ul:nth-child(1) > li:nth-child(3) > a"));
         e1.click();
         Helper.go_to_advanced_Search(driver);
         WebElement e2=driver.findElement(By.cssSelector("#entity"));
         e2.sendKeys(entity_name);
         Helper.clickOnSearch_InAdvancedSearchPage(driver);
         Helper.waitFor();
         WebElement e3=driver.findElement(By.cssSelector("#jog_contents"));
         String body=e3.getText();
         assertTrue(body.contains(entity_name));
     }
     @Test
     public void testEntityNumberAdvancedSearch()
   {
       Helper.clickOnEntityName_OtherEntitiesPage(driver);
       int number=Helper.message_number_OtherEntitiesPage(driver);
       String entity_name=Helper.clickOnSubEntityName_OtherEntitiesPage(driver);
       WebElement e2=driver.findElement(By.cssSelector("#bs-example-navbar-collapse > ul:nth-child(1) > li:nth-child(3) > a"));
       e2.click();
       Helper.go_to_advanced_Search(driver);
       WebElement e3=driver.findElement(By.cssSelector("#entity"));
       e3.sendKeys(entity_name);
       Helper.clickOnSearch_InAdvancedSearchPage(driver);
       WebElement e4=driver.findElement(By.cssSelector("#pageNumbering"));
       int actual_number=Integer.parseInt(e4.getText().substring(2));
       assertTrue(number==actual_number);
   }
    @Test
    public void testNumberBrowseTopPageOtherEntities()
    {
        int number=Helper.number_of_entities_OtherEntites(driver);
        Helper.clickOnEntityName_OtherEntitiesPage(driver);
        WebElement e1=driver.findElement(By.cssSelector("body > div:nth-child(6) > div:nth-child(7) > button"));
        e1.click();
        int number1=Helper.countNumberOfContactsIn_a_Page(driver);
        assertFalse(number==number1);
    }
    @Test
    public void testSent_and_Received()
    {
        Helper.clickOnEntityName_OtherEntitiesPage(driver);
        Helper.clickOnSubEntityName_OtherEntitiesPage(driver);
        Helper.waitFor();
        driver.get("http://localhost:9099/epadd/browse?archiveID=84e8afd01303201b1bb2e763c78c2df0f7133d3004e1ce6e04d9b5e2e3d15423&adv-search=1&term=%22United%20States%22&termBody=on&termSubject=on");
        int number_of_sentmessages=Helper.sent_messages_in_OtherEntitiesPage(driver);
        int number_of_receivedmessages=Helper.received_messages_in_OtherEntitiesPage(driver);
        int sum=number_of_sentmessages+number_of_receivedmessages;
        WebElement e1=driver.findElement(By.cssSelector("#pageNumbering"));
        int number_of_messages_opened=Integer.parseInt(e1.getText().substring(2));
        assertEquals(sum,number_of_messages_opened);
    }
    @AfterEach
    public void postSet()
    {
       driver.quit();
    }
}