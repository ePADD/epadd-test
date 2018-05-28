package com.amuselabs.test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.Set;

public class Helper {

    public void changeWindow(WebDriver driver)
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
    public void waitFor()
    {
        StepDefs sf = new StepDefs();
        try {
            sf.waitFor(5);
        } catch (InterruptedException ex) {}
    }

    //HELPER METHODS FOR CORRESPONDENTS

    public String clickOnNameInCorrespondents(WebDriver driver) {
        WebElement e = driver.findElement(By.xpath("//*[@id=\"people\"]/tbody/tr[2]/td[1]/a"));
        String nametocheck = e.getText();
        e.click();
        new Helper().waitFor();
        return nametocheck;
    }
    public String strings_From(WebDriver driver) {
        changeWindow(driver);
        WebElement e = driver.findElement(By.xpath("//*[@id=\"jog_contents\"]/div[2]/div[1]/table/tbody/tr[2]/td[2]/a"));
        String onClickValue = e.getText();
        return onClickValue;
    }

    public String[] strings_To(WebDriver driver) {
        changeWindow(driver);
        WebElement e = driver.findElement(By.xpath("//*[@id=\"jog_contents\"]/div[2]/div[1]/table/tbody/tr[3]/td[2]/a "));
        String onClickValue = e.getText();
        String[] allTextArray = onClickValue.split(",");
        return allTextArray;
    }

    public String[] strings_Cc(WebDriver driver) {
        changeWindow(driver);
        WebElement e = driver.findElement(By.xpath("//*[@id=\"jog_contents\"]/div[2]/div[1]/table/tbody/tr[4]/td[2]/a"));
        String onClickValue = e.getText();
        String[] allTextArray = onClickValue.split(",");
        return allTextArray;
    }

    public String[] strings_bcc(WebDriver driver) {
        changeWindow(driver);
        WebElement e = driver.findElement(By.cssSelector("WebElement e=driver.findElement(By.xpath(\"//*[@id=\\\"jog_contents\\\"]/div[2]/div[1]/table/tbody/tr[5]/td[2]/a\"));"));
        String onClickValue = e.getText();
        String[] allTextArray = onClickValue.split(",");
        return allTextArray;
    }

    public String string_ID(WebDriver driver) {
        changeWindow(driver);
        WebElement e = driver.findElement(By.cssSelector("#jog_contents > div.muse-doc > div.muse-doc-header > table > tbody > tr:nth-child(5) > td:nth-child(2)"));
        String onClickValue = e.getText();
        return onClickValue;
    }
    public int number_in_Correspondents_BrowseTopPage(WebDriver driver)
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
    public void clickOnEditCorrespondents_Correspondents(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("body > div:nth-child(5) > button:nth-child(2)"));
        e.click();
        new Helper().waitFor();
    }
    public int sent_messages_in_CorrespondentsPage(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("body > div.browsepage > div:nth-child(1) > div > span:nth-child(18) > span"));
        int numberOfSentMessages=Integer.parseInt(e.getText().substring(1,e.getText().length()-1));
        return numberOfSentMessages;
    }
    public int received_messages_in_CorrespondentsPage(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("body > div.browsepage > div:nth-child(1) > div > span:nth-child(20) > span"));
        int numberOfReceivedMessages=Integer.parseInt(e.getText().substring(1,e.getText().length()-1));
        return numberOfReceivedMessages;
    }

   //HELPER METHODS FOR PERSON-ENTITY

    public int message_number_PersonEntity(WebDriver driver)
    {
        WebElement name=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td:nth-child(1) > span"));//name to be clicked
        WebElement message_number=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td.sorting_2"));//message number in front of name
        int number=Integer.parseInt(message_number.getText());//storing that message number as number
        name.click();
        changeWindow(driver);
        return number;
    }
    public String name_from_Person_Entity(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td:nth-child(1) > span"));
        String name=e.getText();
        e.click();
        return name;
    }
    public int number_in_PersonEntity_BrowseTopPage(WebDriver driver)
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
    public void clickOnEditEntities_PersonEntity(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("body > div:nth-child(6) > div:nth-child(7) > button"));
        e.click();
        new Helper().waitFor();
    }
    public int sent_messages_in_PersonEntitiesPage(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("body > div.browsepage > div:nth-child(1) > div > span:nth-child(23) > span"));
        int numberOfSentMessages=Integer.parseInt(e.getText().substring(1,e.getText().length()-1));
        return numberOfSentMessages;
    }
    public int received_messages_in_PersonEntitiesPage(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("body > div.browsepage > div:nth-child(1) > div > span:nth-child(25) > span"));
        int numberOfReceivedMessages=Integer.parseInt(e.getText().substring(1,e.getText().length()-1));
        return numberOfReceivedMessages;
    }


//HELPER METHODS FOR OTHER ENTITIES

    public void clickOnEntityName_OtherEntitiesPage(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td:nth-child(1) > a"));
        e.click();
        new Helper().waitFor();
        new Helper().changeWindow(driver);
    }
    public String clickOnSubEntityName_OtherEntitiesPage(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td:nth-child(1) > span"));
        String entity_name=e.getText();
        e.click();
        new Helper().waitFor();
        return entity_name;
    }
    public int message_number_OtherEntitiesPage(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td.sorting_2"));
        int number=Integer.parseInt(e.getText());
        return number;
    }
    public int number_of_entities_OtherEntites(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("#entities > tbody > tr:nth-child(1) > td.dt-right.sorting_1"));
        int number=Integer.parseInt(e.getText());
        return number;
    }
    public int sent_messages_in_OtherEntitiesPage(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("body > div.browsepage > div:nth-child(1) > div > span:nth-child(25) > span"));
        int numberOfSentMessages=Integer.parseInt(e.getText().substring(1,e.getText().length()-1));
        return numberOfSentMessages;
    }
    public int received_messages_in_OtherEntitiesPage(WebDriver driver)
    {
        WebElement e=driver.findElement(By.cssSelector("body > div.browsepage > div:nth-child(1) > div > span:nth-child(23) > span"));
        int numberOfReceivedMessages=Integer.parseInt(e.getText().substring(1,e.getText().length()-1));
        return numberOfReceivedMessages;
    }

//HELPER METHODS RELATED TO ADVANCED SEARCH

    public void go_to_advanced_Search(WebDriver driver)
    {
        WebElement e2=driver.findElement(By.cssSelector("body > div:nth-child(7) > a"));
        e2.click();
    }
    public void clickOnSearch_InAdvancedSearchPage(WebDriver driver)
    {
        WebElement e4=driver.findElement(By.cssSelector("#search-button"));
        e4.click();
        new Helper().waitFor();
    }

//HELPER METHOD FOR COUNTING NUMBER OF CONTACTS IN ANY PAGE

    public int countNumberOfContactsIn_a_Page(WebDriver driver)
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