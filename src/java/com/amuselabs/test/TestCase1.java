package com.amuselabs.test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.*;
public class TestCase1
{
    WebDriver driver;
    /*   public String onWhichPage(Pages s) {
           driver = new ChromeDriver();
           switch (s) {
               case Specify_Email_Sources:
                   return "Specify_Email_Sources";
               break;

               case Choose_Folders:
                   return "Choose_Folders";
               break;
               case Archive_Information:
                   return "Archive_Information";
               break;
               case Correspondents:
                   return "Correspondents";
               break;
               case Browse:
                   return "Browse";
               break;
               case Edit_Correspondents:
                   return "Edit_Correspondents";
               break;
           }
           }*/

    public boolean isOnCorrespondentsPage()
    {
        driver = new ChromeDriver();
        String currentURL=driver.getCurrentUrl();
        if(currentURL.compareTo("localhost:9099/epadd/correspondents")==0) {
            return true;
        }
        throw new RuntimeException("Naviagte Till Correspondents page::");
    }
    public boolean isBrowsePage()
    {
       driver = new ChromeDriver();
        String currentURL=driver.getCurrentUrl();
        if(currentURL.compareTo("http://localhost:9099/epadd/browse")==0)
        {
            return true;
        }
        else
        {
            throw new RuntimeException("Something went wrong,Message window should be displayed.");
        }
    }

    public void onClickName(WebDriver driver)
    {
       // if(new TestCase1().isOnCorrespondentsPage())
        //{
            //  String onClickValue = driver.findElement(By.xpath("//div[@class= 'taLnk hvrIE6 fl']")).getAttribute("onclick");
         //   String onClickValue = driver.findElement(By.cssSelector("#people > tbody > tr:nth-child(2) > td:nth-child(1)")).getText();
        String onClickValue = driver.findElement(By.cssSelector("#people > tbody > tr:nth-child(2) > td:nth-child(1)")).getText();
            // driver.findElement(By.cssSelector("a[onclick^='javascript: confirmDelete']")).click();
          //  driver.findElement(By.cssSelector("#people > tbody > tr:nth-child(2) > td:nth-child(1)")).click();
         //   if(new TestCase1().isBrowsePage())
           // {
                String value_in_From=strings_From(driver);
                if(value_in_From.contains(onClickValue))
                {
                    System.out.println("Test case pass");
                }
                String extracted_strings_To[]=strings_To(driver);
                for(int i=0;i<extracted_strings_To.length;i++)
                {
                    if(extracted_strings_To[i].contains(onClickValue))
                    {
                        System.out.println("Test Case Passed::");
                        break;
                    }
                }
                String extracted_strings_Cc[]=strings_Cc(driver);
                for(int i=0;i<extracted_strings_To.length;i++)
                {
                    if(extracted_strings_Cc[i].contains(onClickValue))
                    {
                        System.out.println("Test Case Passed::");
                        break;
                    }
                }
                String extracted_strings_bcc[]=strings_bcc(driver);
                for(int i=0;i<extracted_strings_To.length;i++)
                {
                    if(onClickValue.compareTo(extracted_strings_bcc[i])==0)
                    {
                        System.out.println("Test Case Passed::");
                        break;
                    }
                }
          //  }
 /*           else
            {
                throw new RuntimeException("Message window should have opened::");
            }*/
        //}
     /*   else
        {
            throw new RuntimeException("Navigate to Correspondent's page");
        }*/
    }
    public String strings_From(WebDriver driver)
    {
       // String onClickValue = driver.findElement(By.cssSelector("#jog_contents > div.muse-doc > div.muse-doc-header > table > tbody > tr:nth-child(2) > td:nth-child(2)")).getAttribute("onclick");
      //  String onClickValue=driver.findElement(By.xpath("//*[@id=\"people\"]/tbody/tr[2]/td[1]/a")).getAttribute("onclick");
      //  driver.navigate().refresh();
       // driver.switchTO().window("windowName/id");
        String window1=driver.getWindowHandle();
        Set<String> windows=driver.getWindowHandles();
        for(String x:windows)
        {
            if(x.equals(window1)==false)
            {
                driver.switchTo().window(x);
                break;
            }
        }
        WebElement e=driver.findElement(By.xpath("//*[@id=\"jog_contents\"]/div[2]/div[1]/table/tbody/tr[2]/td[2]/a"));
     // WebElement e=driver.findElement(By.cssSelector("#jog_contents > div.muse-doc > div.muse-doc-header > table > tbody > tr:nth-child(2) > td:nth-child(2) > a"));
       String onClickValue=e.getText();
        return onClickValue;
    }
    public String[] strings_To(WebDriver driver)
    {
     //   String onClickValue = driver.findElement(By.cssSelector("#jog_contents > div.muse-doc > div.muse-doc-header > table > tbody > tr:nth-child(3) > td:nth-child(2)")).getAttribute("onclick");
        WebElement e=driver.findElement(By.xpath("//*[@id=\"jog_contents\"]/div[2]/div[1]/table/tbody/tr[3]/td[2]/a "));
        //String[] allTextArray = onClickValue.split(",");
        String onClickValue=e.getText();
        String[] allTextArray = onClickValue.split(",");
        return allTextArray;
    }
    public String[] strings_Cc(WebDriver driver)
    {
       // String onClickValue = driver.findElement(By.cssSelector("#jog_contents > div.muse-doc > div.muse-doc-header > table > tbody > tr:nth-child(4) > td:nth-child(2)")).getAttribute("onclick");
        WebElement e=driver.findElement(By.xpath("//*[@id=\"jog_contents\"]/div[2]/div[1]/table/tbody/tr[4]/td[2]/a"));
        String onClickValue=e.getText();
        String[] allTextArray = onClickValue.split(",");
        return allTextArray;
    }
    public String[] strings_bcc(WebDriver driver)
    {
        WebElement e=driver.findElement(By.xpath("//*[@id=\"jog_contents\"]/div[2]/div[1]/table/tbody/tr[5]/td[2]/a"));
        String onClickValue=e.getText();
        String[] allTextArray = onClickValue.split(",");
        return allTextArray;
    }
    public enum Pages { Specify_Email_Sources,Choose_Folders,Archive_Information,Correspondents,Browse,Edit_Correspondents }


    public void navigate_To_Message_Window()
    {
        try {
            driver = new ChromeDriver();
            driver.get("http://localhost:9099/epadd/correspondents");
            //driver.findElement(By.cssSelector("#people > tbody > tr:nth-child(2) > td:nth-child(1)")).click();
         //   driver.findElement(By.xpath("//*[@id=\"people\"]/tbody/tr[2]/td[1]")).click();
            WebElement e= driver.findElement(By.xpath("//*[@id=\"people\"]/tbody/tr[2]/td[1]/a"));
            e.click();
            //wait(2);
            onClickName(driver);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // new TestCase1().onClickName();
        /*TestCase1 obj=new TestCase1();
        for (Pages s : Pages.values())
        {
         String pageName=obj.onWhichPage(s);
         new TestCase1().onClickName(pageName);
        }*/
       // new TestCase1().onClickName();
    }
}