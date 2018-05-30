package com.amuselabs.test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Set;


//CREATED BY ASHUTOSH


public class Helper {

    public static void start_ePADD()
    {
        try {
            StepDefs browser;
            browser = new StepDefs();
            browser.openEpadd("appraisal");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeWindow(WebDriver driver)
    {
        String window1 = driver.getWindowHandle();
        Set<String> windows = driver.getWindowHandles();
        for (String x : windows) {
            if (x.equals(window1) == false) {
                driver.switchTo().window(x);
                break;
            }
        }
       waitFor();
    }
    public static void waitFor()
    {
        StepDefs sf = new StepDefs();
        try {
            sf.waitFor(5);
        } catch (InterruptedException ex) {}
    }


    //METHOD THAT RETURNS ALL THE UNDERLINED ENTITIES IN A PAGE
    public static List strings_underlined(WebDriver driver)
    {
        List<WebElement> underline=driver.findElements(By.cssSelector(".custom-people"));
        return underline;
    }

    //METHOD THAT RETURNS ALL THE HIGHLIGHTED ENTITIES IN A PAGE
    public static List strings_highlighted(WebDriver driver)
    {
        List<WebElement> highlighted=driver.findElements(By.cssSelector(".hilitedTerm"));
        return highlighted;
    }


    //HELPER METHODS FOR CORRESPONDENTS

    public static String clickOnNameInCorrespondents(WebDriver driver) {
        WebElement e = driver.findElement(By.xpath("//*[@id=\"people\"]/tbody/tr[2]/td[1]/a"));
        String nametocheck = e.getText();
        e.click();
        new Helper().waitFor();
        return nametocheck;
    }
    public static String strings_From(WebDriver driver) {
        changeWindow(driver);
        WebElement e = driver.findElement(By.xpath("//*[@id=\"jog_contents\"]/div[2]/div[1]/table/tbody/tr[2]/td[2]/a"));
        String onClickValue = e.getText();
        return onClickValue;
    }

    public static String[] strings_To(WebDriver driver) {
        changeWindow(driver);
        WebElement e = driver.findElement(By.xpath("//*[@id=\"jog_contents\"]/div[2]/div[1]/table/tbody/tr[3]/td[2]/a "));
        String onClickValue = e.getText();
        String[] allTextArray = onClickValue.split(",");
        return allTextArray;
    }

    public static String[] strings_Cc(WebDriver driver) {
        changeWindow(driver);
        WebElement e = driver.findElement(By.xpath("//*[@id=\"jog_contents\"]/div[2]/div[1]/table/tbody/tr[4]/td[2]/a"));
        String onClickValue = e.getText();
        String[] allTextArray = onClickValue.split(",");
        return allTextArray;
    }

    public static String[] strings_bcc(WebDriver driver) {
        changeWindow(driver);
        WebElement e = driver.findElement(By.cssSelector("WebElement e=driver.findElement(By.xpath(\"//*[@id=\\\"jog_contents\\\"]/div[2]/div[1]/table/tbody/tr[5]/td[2]/a\"));"));
        String onClickValue = e.getText();
        String[] allTextArray = onClickValue.split(",");
        return allTextArray;
    }

    public static String string_ID(WebDriver driver) {
        changeWindow(driver);
        WebElement e = driver.findElement(By.cssSelector("#jog_contents > div.muse-doc > div.muse-doc-header > table > tbody > tr:nth-child(5) > td:nth-child(2)"));
        String onClickValue = e.getText();
        return onClickValue;
    }
    public static int number_in_Correspondents_BrowseTopPage(WebDriver driver)
    {
        driver.get("http://localhost:9099/epadd/browse-top");
        WebElement e=driver.findElement(By.cssSelector("#all-cards > div:nth-child(1) > a > p.cta-text-1"));
        String name=e.getText();
        int number=0;
        for(int i=0;i<name.length();i++)
        {
            char ch=name.charAt(i);
            if(ch>=48 && ch<=57)
            {
                int pos=i;
                number=Integer.parseInt(name.substring(pos,name.length()-1));
                break;
            }
        }
        WebElement e1=driver.findElement(By.cssSelector("#all-cards > div:nth-child(1)"));
        e1.click();
        new Helper().waitFor();
        return number;
    }
    public static void clickOnEditCorrespondents_Correspondents(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("body > div:nth-child(5) > button:nth-child(2)"));
        e.click();
        new Helper().waitFor();
    }
    public static int sent_messages_in_CorrespondentsPage(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("body > div.browsepage > div:nth-child(1) > div > span:nth-child(18) > span"));
        int numberOfSentMessages=Integer.parseInt(e.getText().substring(1,e.getText().length()-1));
        return numberOfSentMessages;
    }
    public static int received_messages_in_CorrespondentsPage(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("body > div.browsepage > div:nth-child(1) > div > span:nth-child(20) > span"));
        int numberOfReceivedMessages=Integer.parseInt(e.getText().substring(1,e.getText().length()-1));
        return numberOfReceivedMessages;
    }

   //HELPER METHODS FOR PERSON-ENTITY

    public static int message_number_PersonEntity(WebDriver driver)
    {
        WebElement name=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td:nth-child(1) > span"));//name to be clicked
        WebElement message_number=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td.sorting_2"));//message number in front of name
        int number=Integer.parseInt(message_number.getText());//storing that message number as number
        name.click();
        changeWindow(driver);
        return number;
    }
    public static String name_from_Person_Entity(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td:nth-child(1) > span"));
        String name=e.getText();
        e.click();
        return name;
    }
    public static int number_in_PersonEntity_BrowseTopPage(WebDriver driver)
    {
        driver.get("http://localhost:9099/epadd/browse-top");
        WebElement e=driver.findElement(By.cssSelector("#all-cards > div:nth-child(2) > a > p.cta-text-1"));
        String name=e.getText();
        int number=0;
        for(int i=0;i<name.length();i++) {
            char ch = name.charAt(i);
            if (ch >= 48 && ch <= 57) {
                int pos = i;
                number = Integer.parseInt(name.substring(pos, name.length() - 1));
                break;
            }
        }
        WebElement e1=driver.findElement(By.cssSelector("#all-cards > div:nth-child(2)"));
        e1.click();
        new Helper().waitFor();
        return number;
    }
    public static void clickOnEditEntities_PersonEntity(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("body > div:nth-child(6) > div:nth-child(7) > button"));
        e.click();
        new Helper().waitFor();
    }
    public static int sent_messages_in_PersonEntitiesPage(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("body > div.browsepage > div:nth-child(1) > div > span:nth-child(23) > span"));
        int numberOfSentMessages=Integer.parseInt(e.getText().substring(1,e.getText().length()-1));
        return numberOfSentMessages;
    }
    public static int received_messages_in_PersonEntitiesPage(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("body > div.browsepage > div:nth-child(1) > div > span:nth-child(25) > span"));
        int numberOfReceivedMessages=Integer.parseInt(e.getText().substring(1,e.getText().length()-1));
        return numberOfReceivedMessages;
    }

    //HELPER METHODS FOR OTHER ENTITIES

    public static void clickOnEntityName_OtherEntitiesPage(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td:nth-child(1) > a"));
        e.click();
        new Helper().waitFor();
        new Helper().changeWindow(driver);
    }
    public static String clickOnSubEntityName_OtherEntitiesPage(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td:nth-child(1) > span"));
        String entity_name=e.getText();
        e.click();
        new Helper().waitFor();
        return entity_name;
    }
    public static int message_number_OtherEntitiesPage(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td.sorting_2"));
        int number=Integer.parseInt(e.getText());
        return number;
    }
    public static int number_of_entities_OtherEntites(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td.dt-right.sorting_1"));
        int number=Integer.parseInt(e.getText());
        return number;
    }
    public static int sent_messages_in_OtherEntitiesPage(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("body > div.browsepage > div:nth-child(1) > div > span:nth-child(25) > span"));
        int numberOfSentMessages=Integer.parseInt(e.getText().substring(1,e.getText().length()-1));
        return numberOfSentMessages;
    }
    public static int received_messages_in_OtherEntitiesPage(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("body > div.browsepage > div:nth-child(1) > div > span:nth-child(23) > span"));
        int numberOfReceivedMessages=Integer.parseInt(e.getText().substring(1,e.getText().length()-1));
        return numberOfReceivedMessages;
    }

    //HELPER METHODS FOR LABELS

    public static int number_of_labels(WebDriver driver)
    {
       WebElement e=driver.findElement(By.cssSelector("#labels_info"));
       String info=e.getText();
       int number_of_labels=0;
       for(int i=info.length()-1;i>=0;i--)
       {
           if(info.charAt(i)>=48 && info.charAt(i)<=57)
           {
               number_of_labels=Integer.parseInt(info.charAt(i)+"");
               break;
           }
       }
       return number_of_labels;
   }
   public static void click_on_new_label(WebDriver driver)
   {
       WebElement new_label=driver.findElement(By.cssSelector("body > div:nth-child(5) > button"));
       new_label.click();
   }
   public static void enter_data_in_label_name(WebDriver driver)
   {
       WebElement label_name=driver.findElement(By.cssSelector("#labelName"));
       label_name.sendKeys("Sample Label");
   }
   public static void click_on_label_type(WebDriver driver)
   {
       WebElement label_type=driver.findElement(By.cssSelector("#save-label-form > div > div > div.form-wraper.clearfix.panel > div:nth-child(3) > div.form-group.col-sm-6 > div > button > span.filter-option.pull-left"));
       label_type.click();
   }
   public static void choose_label_type(WebDriver driver)
   {
       WebElement choose_label_type=driver.findElement(By.cssSelector("#save-label-form > div > div > div.form-wraper.clearfix.panel > div:nth-child(3) > div.form-group.col-sm-6 > div > div > ul > li:nth-child(3) > a > span.text"));
       choose_label_type.click();
   }
   public static void enter_data_in_label_description(WebDriver driver)
   {
       WebElement label_description=driver.findElement(By.cssSelector("#labelDescription"));
       label_description.sendKeys("Just a sample label");
   }
   public static void click_on_update(WebDriver driver)
   {
       WebElement update=driver.findElement(By.cssSelector("#save-button"));
       update.click();
       waitFor();
   }
   public static void click_on_ok(WebDriver driver)
   {
       WebElement ok=driver.findElement(By.cssSelector("#ok-button"));
       ok.click();
       waitFor();
   }
   //HELPER METHODS RELATED TO ADVANCED SEARCH

    public static void go_to_advanced_Search(WebDriver driver)
    {
        WebElement e2=driver.findElement(By.cssSelector("body > div:nth-child(7) > a"));
        e2.click();
    }
    public static void clickOnSearch_InAdvancedSearchPage(WebDriver driver)
    {
        WebElement e4=driver.findElement(By.cssSelector("#search-button"));
        e4.click();
        new Helper().waitFor();
    }

//HELPER METHOD FOR COUNTING NUMBER OF CONTACTS IN ANY PAGE

    public static int countNumberOfContactsIn_a_Page(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("#text"));
        String contact_list=e.getText();
        int count=0;
        for(int i=0;i<contact_list.length();i++)
        {
            if(contact_list.charAt(i)=='-' && contact_list.charAt(i+1)=='-')
            {
                count++;
            }
        }
        return count;
    }
}
/*   int flag=0;
          for(WebElement e:underline)
          {
              String s=e.getText();
              if(s.compareTo(name)==0) {
                  flag=1;
                  break;
              }
          }
          if(flag==1) {
              return true;
          }
          else
              return false;*/