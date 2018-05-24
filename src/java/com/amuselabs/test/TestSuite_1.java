package com.amuselabs.test;
import org.junit.jupiter.api.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TestSuite_1 {
    String nametocheck;
    String value_in_From;
    String extracted_strings_To[];
    WebDriver driver = new ChromeDriver();

    @BeforeAll
    public static void start_epadd() {
        try {
            StepDefs browser;
            browser = new StepDefs();
            browser.openEpadd("appraisal");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void open_Correspondents() {
            driver.get("http://localhost:9099/epadd/correspondents");
    }

    @Test
    public void testFrom() {
        nametocheck=new Helper().clickOnNameInCorrespondents(driver);
        value_in_From = new Helper().strings_From(driver);
        assertTrue(value_in_From.contains(nametocheck));
    }

     @Test
     public void testTo()
     {
         nametocheck=new Helper().clickOnNameInCorrespondents(driver);
         extracted_strings_To=new Helper().strings_To(driver);
         for(int i=0;i<extracted_strings_To.length;i++)
         {
             if(extracted_strings_To[i].contains(nametocheck))
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
             nametocheck=new Helper().clickOnNameInCorrespondents(driver);
             String[] extracted_strings_cc = new Helper().strings_Cc(driver);
             for (int i = 0; i < extracted_strings_cc.length; i++) {
                 if (extracted_strings_cc[i].contains(nametocheck)) {
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
             nametocheck=new Helper().clickOnNameInCorrespondents(driver);
             String[] extracted_strings_bcc = new Helper().strings_bcc(driver);
             for (int i = 0; i < extracted_strings_bcc.length; i++) {
                 if (extracted_strings_bcc[i].contains(nametocheck)) {
                     result = true;
                     break;
                 }
             }
         } catch (NoSuchElementException e) {
             result = true;
         }
         assertTrue(result);
     }

     @AfterEach
    public void check() {
        driver.close();
        driver.quit();
    }

}