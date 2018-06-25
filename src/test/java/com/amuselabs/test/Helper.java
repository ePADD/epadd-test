package com.amuselabs.test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;


//CREATED BY ASHUTOSH
//This class contains the major helper methods for running test cases

public class Helper {
    public static Properties user_interface =new Properties();  //user_interface variable is used to read properties file named as USER_INTERFACE.properties
    public static String BASE_DIR;
    private static String BROWSER_NAME;

    private static String DEFAULT_BASE_DIR = System.getProperty ("user.home") + File.separator + "epadd-test";
    //private static Log log = LogFactory.getLog(StepDefs.class);
    static Properties VARS;
    private static String EPADD_TEST_PROPS_FILE = System.getProperty("user.home") + File.separator + "epadd.test.properties";
    private static Process epaddProcess = null;
    private Stack<String> tabStack = new Stack<>();
    private String screenshotsDir;

   public Helper()
   {
       VARS = new Properties();

       File f = new File(EPADD_TEST_PROPS_FILE);
       if (f.exists() && f.canRead()) {
          // log.info("Reading configuration from: " + EPADD_TEST_PROPS_FILE);
           try {
               InputStream is = new FileInputStream(EPADD_TEST_PROPS_FILE);
               VARS.load(is);
           } catch (Exception e) {
               //print_exception("Error reading epadd properties file " + EPADD_TEST_PROPS_FILE, e, log);
               e.printStackTrace();
           }
       } else {
          // log.warn("ePADD properties file " + EPADD_TEST_PROPS_FILE + " does not exist or is not readable");
       }

       for (String key: VARS.stringPropertyNames()) {
           String val = System.getProperty (key);
           if (val != null && val.length() > 0)
               VARS.setProperty(key, val);
       }

       BASE_DIR = VARS.getProperty("epadd.test.dir");
       if (BASE_DIR == null)
           BASE_DIR = DEFAULT_BASE_DIR+"/src/test/resources";

       new File(BASE_DIR).mkdirs();
       screenshotsDir = BASE_DIR + File.separator + "screenshots";
       new File(screenshotsDir).mkdirs();

      // log.info ("Base dir for this test run is: " + BASE_DIR);
   }

    public static void read_properties_file()
    {
        try {
            InputStream s = Helper.class.getClassLoader().getResourceAsStream("USER_INTERFACE.properties");
            user_interface.load(s);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //this method opens epadd
    public  void open_ePADD(String mode)throws IOException {
           /* StepDefs browser;
            browser = new StepDefs();   //makes use of "StepDefs" class
            browser.openEpadd("appraisal");*/
                // we'll always launch using epadd-standalone.jar
                //     updateTestStatus("Starting ePADD");

                String errFile = System.getProperty("java.io.tmpdir") + File.separator + "epadd-test.err.txt";
                String outFile = System.getProperty("java.io.tmpdir") + File.separator + "epadd-test.out.txt";
                String cmd = VARS.getProperty ("cmd");
                if (cmd == null) {
                   // log.warn ("Please confirm cmd in " + EPADD_TEST_PROPS_FILE);
                    throw new RuntimeException ("no command to start epadd");
                }

                cmd = "java -Depadd.mode=" + mode +  " -Depadd.base.dir=" + BASE_DIR + " " + cmd;
                cmd = cmd + " --no-browser-open"; // we'll open our own browser
                ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));

//		ProcessBuilder pb = new ProcessBuilder("java", "-Xmx2g", "-jar", "epadd-standalone.jar", "--no-browser-open");
                pb.redirectError(new File(errFile));
                pb.redirectOutput(new File(outFile));
              //  log.info ("Sending epadd output to: " + outFile);
                epaddProcess = pb.start();
             //   log.info ("Started ePADD");
        }
        //start ePADD method calls open_ePADD and therefore enables epadd to run.
    public static void start_ePADD()throws Exception
    {
        Helper browser=new Helper();
        browser.open_ePADD("appraisal");
    }
      //this method is used to switch between tabs.
    public static void changeWindow(WebDriver driver) {
        String window1 = driver.getWindowHandle();
        Set<String> windows = driver.getWindowHandles();
        for (String x : windows) {
            if (x.equals(window1) == false) {
                driver.switchTo().window(x);
                break;
            }
            driver.switchTo().window(window1);
            driver.close();                 //close the parent tab
        }
        waitFor();

    }
  //this method is for wait,after clicking on a button,if wait is invoked,it waits for 5 seconds.Uses "StepDefs" class.
    public static void waitFor() {
        /*StepDefs sf = new StepDefs();
        try {
            sf.waitFor(5);
        } catch (InterruptedException ex) {
        }*/
       // driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        try {
            TimeUnit.SECONDS.sleep(5);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void longWait()
    {
        try {
            TimeUnit.SECONDS.sleep(105);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //METHOD THAT RETURNS ALL THE UNDERLINE WORDS IN A PAGE.(test cases for underlined and highlighted is written for person-entity)
    public static List strings_underlined_on_message_window_of_Person_Entities(WebDriver driver) {
        String underlined_words = TestSuite_Person_EntitiesTest.user_interface.getProperty("underlined_words_Person_Entities");
        List<WebElement> underline = driver.findElements(By.cssSelector(underlined_words));
        return underline;
    }

    //METHOD THAT RETURNS ALL THE HIGHLIGHTED ENTITIES IN A PAGE
    public static List strings_highlighted_on_message_window_of_Person_Entities(WebDriver driver) {
        String highlighted_words = TestSuite_Person_EntitiesTest.user_interface.getProperty("highlighted_words_Person_Entities");
        List<WebElement> highlighted = driver.findElements(By.cssSelector(highlighted_words));
        return highlighted;
    }


    //HELPER METHODS FOR CORRESPONDENTS

    //this method returns unique contact id of a name which is to be clicked.For eg,contact id of "Kathleen Shanahan" is 14.
    public static int get_contact_id(WebDriver driver) {
        String name_of_a_correspondent = TestSuite_CorrespondentsTest.user_interface.getProperty("name_of_a_Correspondent");
        WebElement element = driver.findElement(By.xpath(name_of_a_correspondent));
        String s = element.getAttribute("href");
        int pos1 = 0, pos2 = 0, num = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '&') {
                pos2 = i;
                break;
            }
        }
        for (int j = pos2; j >= 0; j--) {
            if (s.charAt(j) == '=') {
                pos1 = j;
                num = Integer.parseInt(s.substring(pos1 + 1, pos2));
                break;
            }
        }
        return num;
    }
    //this method returns the contact id of the string present in "From" of the mail opened.
    public static int get_contact_id_From(WebDriver driver) {
        String To = TestSuite_CorrespondentsTest.user_interface.getProperty("From");
        WebElement e = driver.findElement(By.xpath(To));
        String s = e.getAttribute("href");
        int id = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '=') {
                id = Integer.parseInt(s.substring(i + 1));
                break;
            }
        }
        return id;
    }
    //this method returns the conatact id of the string present in "To" of the mail opened.
    public static int get_contact_id_To(WebDriver driver) {
        String To = TestSuite_CorrespondentsTest.user_interface.getProperty("To");
        WebElement e = driver.findElement(By.xpath(To));
        String s = e.getAttribute("href");
        int id = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '=') {
                id = Integer.parseInt(s.substring(i + 1));
                break;
            }
        }
        return id;
    }

    //this method returns the conatact id of the string present in "Cc" of the mail opened.
    public static int get_contact_id_Cc(WebDriver driver) {
        String cc = TestSuite_CorrespondentsTest.user_interface.getProperty("Cc");
        WebElement e = driver.findElement(By.xpath(cc));
        String names[]=e.getText().split(">,");
       // TestSuite_CorrespondentsTest.flag=1;
        String s = e.getAttribute("href");
        int id = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '=') {
                id = Integer.parseInt(s.substring(i + 1));
                break;
            }
        }
        return id;
    }
    //this method returns the conatact id of the string present in "bcc" of the mail opened.
    public static int get_contact_id_bcc(WebDriver driver) {
        String bcc = TestSuite_CorrespondentsTest.user_interface.getProperty("bcc");
        WebElement e = driver.findElement(By.xpath(bcc));
        String s = e.getAttribute("href");
        int id = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '=') {
                id = Integer.parseInt(s.substring(i + 1));
                break;
            }
        }
        return id;
    }
    //this method clicks on "Correspondents" button in browse-top page.
    public static void click_on_Correspondents_BrowseTopPage(WebDriver driver) {
        driver.get("http://localhost:9099/epadd/browse-top");    //navigating to browse-top page.
        String correspondents = TestSuite_CorrespondentsTest.user_interface.getProperty("correspondents");  //reading a correspondent's name(cssSelector) through properties file(user_interface)
        WebElement e = driver.findElement(By.cssSelector(correspondents));  //finding a correspondent's name according to cssSelector
        e.click();
        waitFor();
    }
    //this method clicks on a name in Correspondents ,eg,it will click on "Kathleen Shanahan" if its selector is given in USER_INTERFACE.properties.
    public static String clickOnNameInCorrespondents(WebDriver driver) {
        String name_of_a_correspondent = TestSuite_CorrespondentsTest.user_interface.getProperty("name_of_a_Correspondent");
        WebElement e = driver.findElement(By.xpath(name_of_a_correspondent));
        String nametocheck = e.getText();
        e.click();
        waitFor();
        changeWindow(driver);
        return nametocheck;
    }
    //this method returns the ID present in messages opened,eg "137f596e4ed37eae766d40276fad9f3c5452b29c2649f39318b9221bd89e8de9"
    public static String string_ID(WebDriver driver) {
        String id = TestSuite_CorrespondentsTest.user_interface.getProperty("id");
        WebElement e = driver.findElement(By.cssSelector(id));
        String onClickValue = e.getText();
        onClickValue = onClickValue.substring(0, onClickValue.length() - 6);
        return onClickValue;
    }
    //this method return the ID of the message that has opened after entering original ID in "Message ID" text field in "Advanced Search" page and clicking on "Search" button
    public static String string_newID_After_Entering_MessageID_on_AdvancedSearchPage(WebDriver driver) {
        String new_id = TestSuite_CorrespondentsTest.user_interface.getProperty("new_id");
        WebElement e = driver.findElement(By.cssSelector(new_id));
        String onClickValue = e.getText();
        onClickValue = onClickValue.substring(0, onClickValue.length() - 6);
        return onClickValue;
    }
    //this method returns the number mentioned in Correspondents button in "Browse-top" page.eg "1749"
    public static int number_in_Correspondents_BrowseTopPage(WebDriver driver) {
        driver.get("http://localhost:9099/epadd/browse-top");
        String data_in_CorrespondentsButton = TestSuite_CorrespondentsTest.user_interface.getProperty("data_in_CorrespondentsButton");
        WebElement e = driver.findElement(By.cssSelector(data_in_CorrespondentsButton));
        String name = e.getText();
        int number = 0;
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (ch >= 48 && ch <= 57) {
                int pos = i;
                number = Integer.parseInt(name.substring(pos, name.length() - 1));
                break;
            }
        }
        String correspondents = TestSuite_CorrespondentsTest.user_interface.getProperty("correspondents");
        WebElement e1 = driver.findElement(By.cssSelector(correspondents));
        e1.click();
        waitFor();
        return number;
    }

    //this method clicks on "Edit Correspondents" button
    public static void clickOnEditCorrespondents_Correspondents(WebDriver driver) {
        String edit_correspondents = TestSuite_CorrespondentsTest.user_interface.getProperty("edit_correspondents");
        WebElement e = driver.findElement(By.cssSelector(edit_correspondents));
        e.click();
        waitFor();
    }
     //this method returns the number of sent messages mentioned in message window.
    public static int sent_messages_in_CorrespondentsPage(WebDriver driver) {
        String sent_messages_in_CorrespondentsPage = TestSuite_CorrespondentsTest.user_interface.getProperty("sent_messages_in_CorrespondentsPage");
        WebElement e = driver.findElement(By.cssSelector(sent_messages_in_CorrespondentsPage));
        int numberOfSentMessages = Integer.parseInt(e.getText().substring(1, e.getText().length() - 1));
        return numberOfSentMessages;
    }
    //this method returns the number of received messages mentioned in message window.
    public static int received_messages_in_CorrespondentsPage(WebDriver driver) {
        String received_messages_in_CorrespondentsPage = TestSuite_CorrespondentsTest.user_interface.getProperty("received_messages_in_CorrespondentsPage");
        WebElement e = driver.findElement(By.cssSelector(received_messages_in_CorrespondentsPage));
        int numberOfReceivedMessages = Integer.parseInt(e.getText().substring(1, e.getText().length() - 1));
        return numberOfReceivedMessages;
    }
    //returns the entire text of mail,including strings mentioned in "From","To","cc","bcc"
    public static String get_text_of_entire_mail_opened(WebDriver driver)
    {
        String text_of_entire_mail= TestSuite_CorrespondentsTest.user_interface.getProperty("text_of_entire_mail");
        WebElement e=driver.findElement(By.cssSelector(text_of_entire_mail));
        String body=e.getText();
        return body;
    }
    //return the actual number of messages opened after clicking on a name.for eg, it returns 1020 if messages opened are "1/1020"
    public static int number_of_messages_opened_after_clicking_on_Correspondents_name(WebDriver driver) {
        String number_of_messages_opened_after_clicking_on_Correspondents_name = TestSuite_CorrespondentsTest.user_interface.getProperty("number_of_messages_opened_after_clicking_on_Correspondents_name");
        WebElement e = driver.findElement(By.cssSelector(number_of_messages_opened_after_clicking_on_Correspondents_name));
        String number = e.getText();
        int pos = 0;
        for (int i = 0; i < number.length(); i++) {
            if (number.charAt(i) == '/') {
                pos = i + 1;
                break;
            }
        }
        number = number.substring(pos);
        return Integer.parseInt(number);
    }

    //HELPER METHODS FOR PERSON-ENTITY

    //this method clicks on "Person-Entities" button in browse-top page
    public static void click_on_Person_Entities_BrowseTopPage(WebDriver driver) {
        driver.get("http://localhost:9099/epadd/browse-top");
        String click_on_Person_Entities= TestSuite_Person_EntitiesTest.user_interface.getProperty("click_on_Person_Entities");
        WebElement e = driver.findElement(By.cssSelector(click_on_Person_Entities));
        e.click();
        waitFor();
    }
   //this method returns the number mentioned in front of Person-Entities's name (eg 914 mentioned in front of "Jeb Bush") and then clicks on that name
    public static int get_message_number_PersonEntity_and_click_on_name(WebDriver driver) {
        String click_on_name_in_Person_Entities = TestSuite_Person_EntitiesTest.user_interface.getProperty("click_on_name_in_Person_Entities");
        WebElement name = driver.findElement(By.cssSelector(click_on_name_in_Person_Entities)); // PersonEntities name to be clicked
        String message_number = TestSuite_Person_EntitiesTest.user_interface.getProperty("message_number");
        WebElement message_number_of_a_PersonEntity = driver.findElement(By.cssSelector(message_number));//message number in front of name
        int number = Integer.parseInt(message_number_of_a_PersonEntity.getText());//storing that message number as number
        name.click();
        waitFor();
        changeWindow(driver);
        return  number;
    }
    //returns the name of a Person-Entity(eg Jeb Bush) and clicks on it.
    public static String get_name_from_Person_Entity_and_click_on_name(WebDriver driver) {
        String click_on_name_in_Person_Entities = TestSuite_Person_EntitiesTest.user_interface.getProperty("click_on_name_in_Person_Entities");
        WebElement e = driver.findElement(By.cssSelector(click_on_name_in_Person_Entities));
        String name = e.getText();
        e.click();
        waitFor();
        changeWindow(driver);
        return name;
    }
    //returns the text of the body of mail excluding "To","From","cc","bcc" and other headers.
    public static String body_of_mail_after_clicking_a_name_in_PersonEntity(WebDriver driver) {
        String body_of_mail = TestSuite_Person_EntitiesTest.user_interface.getProperty("body_of_mail");
        WebElement e1 = driver.findElement(By.cssSelector(body_of_mail));
        String body = e1.getText();
        return body;
    }
    //returns the number mentioned in the "Person-Entities" button in browse-top page and clicks on Person-entities
    public static int get_number_in_PersonEntity_BrowseTopPage_and_click_on_Person_Entities(WebDriver driver) {
        driver.get("http://localhost:9099/epadd/browse-top");
        String number_in_PersonEntity_BrowseTopPage = TestSuite_Person_EntitiesTest.user_interface.getProperty("number_in_PersonEntity_BrowseTopPage");
        WebElement e = driver.findElement(By.cssSelector(number_in_PersonEntity_BrowseTopPage));
        String name = e.getText();
        int number = 0;
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (ch >= 48 && ch <= 57) {
                int pos = i;
                number = Integer.parseInt(name.substring(pos, name.length() - 1));
                break;
            }
        }
        String click_on_PersonEntities = TestSuite_Person_EntitiesTest.user_interface.getProperty("click_on_Person_Entities");
        WebElement n = driver.findElement(By.cssSelector(click_on_PersonEntities));// PersonEntities name to be clicked
        n.click();
        waitFor();
        return number;
    }
    //clicks on Edit Entities in Person-Entities page
    public static void clickOnEditEntities_PersonEntity(WebDriver driver) {
        String clickOnEditEntities_PersonEntity= TestSuite_Person_EntitiesTest.user_interface.getProperty("clickOnEditEntities_PersonEntity");
        WebElement e = driver.findElement(By.cssSelector(clickOnEditEntities_PersonEntity));
        e.click();
        waitFor();
    }

    public static int sent_messages_in_PersonEntitiesPage(WebDriver driver) {
        String sent_messages_PersonEntities = TestSuite_Person_EntitiesTest.user_interface.getProperty("sent_messages_PersonEntities");
        WebElement e = driver.findElement(By.cssSelector(sent_messages_PersonEntities));
        int numberOfSentMessages = Integer.parseInt(e.getText().substring(1, e.getText().length() - 1));
        return numberOfSentMessages;
    }

    public static int received_messages_in_PersonEntitiesPage(WebDriver driver) {

        String received_messages_PersonEntities = TestSuite_Person_EntitiesTest.user_interface.getProperty("received_messages_PersonEntities");
        WebElement e = driver.findElement(By.cssSelector(received_messages_PersonEntities));
        int numberOfSentMessages = Integer.parseInt(e.getText().substring(1, e.getText().length() - 1));
        return numberOfSentMessages;
    }

    public static int number_of_messages_opened_after_clicking_on_PersonEntities_name(WebDriver driver) {
        String number_of_messages_opened_after_clicking_on_PersonEntities_name = TestSuite_Person_EntitiesTest.user_interface.getProperty("number_of_messages_opened_after_clicking_on_PersonEntities_name");
        WebElement e = driver.findElement(By.cssSelector(number_of_messages_opened_after_clicking_on_PersonEntities_name));
        String number = e.getText();
        int pos = 0;
        for (int i = 0; i < number.length(); i++) {
            if (number.charAt(i) == '/') {
                pos = i + 1;
                break;
            }
        }
        number = number.substring(pos);
        return Integer.parseInt(number);
    }

    //HELPER METHODS FOR OTHER ENTITIES
    public static void clickOnOtherEntities(WebDriver driver) {
        driver.get("http://localhost:9099/epadd/browse-top");
        String clickOnOtherEntities = TestSuite_Other_EntitiesTest.user_interface.getProperty("clickOnOtherEntities");
        WebElement e = driver.findElement(By.cssSelector(clickOnOtherEntities));
        e.click();
        waitFor();
    }

    public static void clickOnEntityName_OtherEntitiesPage(WebDriver driver) {
        String clickOnEntityName_OtherEntitiesPage = TestSuite_Other_EntitiesTest.user_interface.getProperty("clickOnEntityName_OtherEntitiesPage");
        WebElement e = driver.findElement(By.cssSelector(clickOnEntityName_OtherEntitiesPage));
        e.click();
        waitFor();
        changeWindow(driver);
    }

    public static String clickOnSubEntityName_OtherEntitiesPage(WebDriver driver) {
        String sub_entity_name = TestSuite_Other_EntitiesTest.user_interface.getProperty("sub_entity_name");
        WebElement e = driver.findElement(By.cssSelector(sub_entity_name));
        String entity_name = e.getText();
        e.click();
        waitFor();
        changeWindow(driver);
        return entity_name;
    }

    public static String body_of_mail_opened_onclick_subEntity(WebDriver driver) {
        Helper.waitFor();
        Helper.waitFor();
        Helper.waitFor();
        String body_of_mail_opened_onclick_subEntity = TestSuite_Other_EntitiesTest.user_interface.getProperty("body_of_mail_opened_onclick_subEntity");
        WebElement e2 = driver.findElement(By.cssSelector(body_of_mail_opened_onclick_subEntity));
        String body = e2.getText();
        return body;
    }

    public static int message_number_OtherEntitiesPage(WebDriver driver) {
        String message_number = TestSuite_Other_EntitiesTest.user_interface.getProperty("message_number");
        WebElement e = driver.findElement(By.cssSelector(message_number));
        int number = Integer.parseInt(e.getText());
        return number;
    }

    public static int number_of_entities_OtherEntites(WebDriver driver)       //mail entity like Place(number=411)
    {
        String number_of_entities_OtherEntites = TestSuite_Other_EntitiesTest.user_interface.getProperty("number_of_entities_OtherEntites");
        WebElement e = driver.findElement(By.cssSelector(number_of_entities_OtherEntites));
        int number = Integer.parseInt(e.getText());
        return number;
    }

    public static int sent_messages_in_OtherEntitiesPage(WebDriver driver) {
        String sent_messages_in_OtherEntitiesPage = TestSuite_Other_EntitiesTest.user_interface.getProperty("sent_messages_in_OtherEntitiesPage");
        WebElement e = driver.findElement(By.cssSelector(sent_messages_in_OtherEntitiesPage));
        int numberOfSentMessages = Integer.parseInt(e.getText().substring(1, e.getText().length() - 1));
        return numberOfSentMessages;
    }

    public static int received_messages_in_OtherEntitiesPage(WebDriver driver) {
        String received_messages_in_OtherEntitiesPage = TestSuite_Other_EntitiesTest.user_interface.getProperty("received_messages_in_OtherEntitiesPage");
        WebElement e = driver.findElement(By.cssSelector(received_messages_in_OtherEntitiesPage));
        int numberOfReceivedMessages = Integer.parseInt(e.getText().substring(1, e.getText().length() - 1));
        return numberOfReceivedMessages;
    }

    public static void clickOnEditEntities_OtherEntities(WebDriver driver) {
        String clickOnEditEntities = TestSuite_Other_EntitiesTest.user_interface.getProperty("clickOnEditEntities");
        WebElement e1 = driver.findElement(By.cssSelector(clickOnEditEntities));
        e1.click();
        waitFor();
    }

    public static int number_of_messages_opened_after_clicking_on_subEntity_name(WebDriver driver) {
        String number_of_messages_opened_after_clicking_on_subEntity_name = TestSuite_Other_EntitiesTest.user_interface.getProperty("number_of_messages_opened_after_clicking_on_subEntity_name");
        WebElement e = driver.findElement(By.cssSelector(number_of_messages_opened_after_clicking_on_subEntity_name));
        String number = e.getText();
        int pos = 0;
        for (int i = 0; i < number.length(); i++) {
            if (number.charAt(i) == '/') {
                pos = i + 1;
                break;
            }
        }
        number = number.substring(pos);
        return Integer.parseInt(number);
    }

    //HELPER METHODS FOR LABELS
    public static void clickOnLabels(WebDriver driver) {
        driver.get("http://localhost:9099/epadd/browse-top");
        String click_on_labels = TestSuite_LabelsTest.user_interface.getProperty("click_on_labels");
        WebElement e = driver.findElement(By.cssSelector(click_on_labels));
        e.click();
    }

    public static String get_label_name(WebDriver driver) {
        String label_name = TestSuite_LabelsTest.user_interface.getProperty("label_name");
        WebElement e = driver.findElement(By.cssSelector(label_name));
        String name = e.getText();
        return name;
    }

    public static String click_on_label_and_return_its_name(WebDriver driver) {
        String label_name = TestSuite_LabelsTest.user_interface.getProperty("label_name");
        WebElement e = driver.findElement(By.cssSelector(label_name));
        String name = e.getText();
        e.click();
        changeWindow(driver);
        return name;
    }

    public static String get_label_type(WebDriver driver) {
        String label_type = TestSuite_LabelsTest.user_interface.getProperty("label_type");
        WebElement e = driver.findElement(By.cssSelector(label_type));
        String type = e.getText();
        return type;
    }

    public static void click_on_edit_label(WebDriver driver) {
        String click_on_edit_label = TestSuite_LabelsTest.user_interface.getProperty("click_on_edit_label");
        WebElement e = driver.findElement(By.cssSelector(click_on_edit_label));
        e.click();
        waitFor();
    }

    public static String edit_label_name(WebDriver driver, String label_name_to_be_modified) {
        label_name_to_be_modified = label_name_to_be_modified + "xy";
        String content_of_label_name_textfield_in_edit_labels = TestSuite_LabelsTest.user_interface.getProperty("content_of_label_name_textfield_in_edit_labels");
        WebElement e = driver.findElement(By.cssSelector(content_of_label_name_textfield_in_edit_labels));
        e.clear();
        e.sendKeys(label_name_to_be_modified);
        return label_name_to_be_modified;
    }

    public static int number_of_labels(WebDriver driver) {
        String total_labels_in_page = TestSuite_LabelsTest.user_interface.getProperty("total_labels_in_page");
        WebElement e = driver.findElement(By.cssSelector(total_labels_in_page));
        String info = e.getText();
        int number_of_labels = 0;
        for (int i = info.length() - 1; i >= 0; i--) {
            if (info.charAt(i) >= 48 && info.charAt(i) <= 57) {
                number_of_labels = Integer.parseInt(info.charAt(i) + "");
                break;
            }
        }
        return number_of_labels;
    }

    public static int number_of_labels_on_messages(WebDriver driver) {
        String number_of_labels_on_messages = TestSuite_LabelsTest.user_interface.getProperty("number_of_labels_on_messages");
        WebElement e = driver.findElement(By.cssSelector(number_of_labels_on_messages));
        int n = Integer.parseInt(e.getText());
        return n;
    }

    public static void click_on_new_label(WebDriver driver) {
        String click_on_new_label = TestSuite_LabelsTest.user_interface.getProperty("click_on_new_label");
        WebElement new_label = driver.findElement(By.cssSelector(click_on_new_label));
        new_label.click();
    }

    public static void enter_data_in_label_name(WebDriver driver) {
        String enter_data_in_label_name = TestSuite_LabelsTest.user_interface.getProperty("enter_data_in_label_name");
        WebElement label_name = driver.findElement(By.cssSelector(enter_data_in_label_name));
        label_name.sendKeys("Sample Label");
    }

    public static void click_on_label_type(WebDriver driver) {

        String click_on_label_type = TestSuite_LabelsTest.user_interface.getProperty("click_on_label_type");
        WebElement label_type = driver.findElement(By.cssSelector(click_on_label_type));
        label_type.click();
    }

    public static void choose_label_type(WebDriver driver) {
        String choose_label_type = TestSuite_LabelsTest.user_interface.getProperty("choose_label_type");
        WebElement e = driver.findElement(By.cssSelector(choose_label_type));
        e.click();
    }

    public static void enter_data_in_label_description(WebDriver driver) {
        String enter_data_in_label_description = TestSuite_LabelsTest.user_interface.getProperty("enter_data_in_label_description");
        WebElement label_description = driver.findElement(By.cssSelector(enter_data_in_label_description));
        label_description.sendKeys("Just a sample label");
    }

    public static void click_on_update(WebDriver driver) {
        String click_on_update = TestSuite_LabelsTest.user_interface.getProperty("click_on_update");
        WebElement update = driver.findElement(By.cssSelector(click_on_update));
        update.click();
        waitFor();
    }

    public static void click_on_ok(WebDriver driver) {
        String click_on_ok = TestSuite_LabelsTest.user_interface.getProperty("click_on_ok");
        WebElement ok = driver.findElement(By.cssSelector(click_on_ok));
        ok.click();
        waitFor();
    }

    public static String click_on_label_in_message_window_of_correspondents_and_choose_a_label(WebDriver driver) {
        String click_on_label_in_message_window_of_correspondents = TestSuite_LabelsTest.user_interface.getProperty("click_on_label_in_message_window_of_correspondents");
        WebElement e = driver.findElement(By.cssSelector(click_on_label_in_message_window_of_correspondents));
        e.click();
        String choose_label = TestSuite_LabelsTest.user_interface.getProperty("choose_label");
        WebElement e1 = driver.findElement(By.cssSelector(choose_label));
        e1.click();
        String s = e1.getText();
        return s;
    }

    public static void click_on_Correspondents_BrowseTopPage_through_labels(WebDriver driver) {
        driver.get("http://localhost:9099/epadd/browse-top");    //navigating to browse-top page.
        String correspondents = TestSuite_LabelsTest.user_interface.getProperty("correspondents");  //reading a correspondent's name(cssSelector) through properties file(user_interface)
        WebElement e = driver.findElement(By.cssSelector(correspondents));  //finding a correspondent's name according to cssSelector
        e.click();
        waitFor();
    }

    public static String clickOnNameInCorrespondents_through_labels(WebDriver driver) {
        String name_of_a_correspondent = TestSuite_LabelsTest.user_interface.getProperty("name_of_a_Correspondent");
        WebElement e = driver.findElement(By.xpath(name_of_a_correspondent));
        String nametocheck = e.getText();
        e.click();
        waitFor();
        changeWindow(driver);
        waitFor();
        return nametocheck;
    }

    public static int number_of_messages_opened_after_clicking_on_Label_name(WebDriver driver) {
        String number_of_messages_opened_after_clicking_on_Label_name = TestSuite_LabelsTest.user_interface.getProperty("number_of_messages_opened_after_clicking_on_Label_name");
        WebElement e = driver.findElement(By.cssSelector(number_of_messages_opened_after_clicking_on_Label_name));
        String number = e.getText();
        int pos = 0;
        for (int i = 0; i < number.length(); i++) {
            if (number.charAt(i) == '/') {
                pos = i + 1;
                break;
            }
        }
        number = number.substring(pos);
        return Integer.parseInt(number);
    }

    //HELPER METHODS RELATED TO LEXICON
    public static void clickOnLexicon(WebDriver driver) {
        driver.get("http://localhost:9099/epadd/browse-top");
        String click_on_labels = TestSuite_LexiconTest.lexicon.getProperty("click_on_lexicon");
        WebElement e = driver.findElement(By.cssSelector(click_on_labels));
        e.click();
    }

    public static String click_on_Lexicon_category_and_return_name(WebDriver driver) {
        String Lexicon_category = TestSuite_LexiconTest.lexicon.getProperty("Lexicon_category");
        WebElement e = driver.findElement(By.cssSelector(Lexicon_category));
        String s = e.getText();
        e.click();
        waitFor();
        changeWindow(driver);
        return s;
    }

    public static int number_of_messages_displayed_in_front_of_Lexicon(WebDriver driver) {
        String number_of_messages_displayed_in_front_of_Lexicon = TestSuite_LexiconTest.lexicon.getProperty("number_of_messages_displayed_in_front_of_Lexicon");
        WebElement e1 = driver.findElement(By.cssSelector(number_of_messages_displayed_in_front_of_Lexicon));
        int n = Integer.parseInt(e1.getText());
        return n;
    }

    public static int number_of_messages_opened_after_clicking_on_Lexicon_category(WebDriver driver) {
        String number_of_messages_opened_after_clicking_on_Lexicon_category = TestSuite_LexiconTest.lexicon.getProperty("number_of_messages_opened_after_clicking_on_Lexicon_category");
        WebElement e = driver.findElement(By.cssSelector(number_of_messages_opened_after_clicking_on_Lexicon_category));
        String number = e.getText();
        int pos = 0;
        for (int i = 0; i < number.length(); i++) {
            if (number.charAt(i) == '/') {
                pos = i + 1;
                break;
            }
        }
        number = number.substring(pos);
        return Integer.parseInt(number);
    }
    public static void go_to_graph_view_lexicon(WebDriver driver) {
        String go_to_graph_view_lexicon = TestSuite_LexiconTest.lexicon.getProperty("go_to_graph_view_lexicon");
        WebElement e = driver.findElement(By.cssSelector(go_to_graph_view_lexicon));
        e.click();
    }

    public static int number_of_messages_of_lexicon_category_displayed_in_graph_view(WebDriver driver) {
        String number_of_messages_of_lexicon_category_displayed_in_graph_view = TestSuite_LexiconTest.lexicon.getProperty("number_of_messages_of_lexicon_category_displayed_in_graph_view");
        WebElement e = driver.findElement(By.cssSelector(number_of_messages_of_lexicon_category_displayed_in_graph_view));
        String s = e.getText();
        String s1 = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                s1 = s.substring(i + 1, s.length() - 1);
                break;
            }
        }
        int num = Integer.parseInt(s1);
        return num;
    }

    //HELPER METHODS RELATED TO ADVANCED SEARCH
    public static void click_on_search(WebDriver driver) {
        read_properties_file();
        String click_on_search = user_interface.getProperty("click_on_search");
        WebElement e = driver.findElement(By.cssSelector(click_on_search));
        e.click();
        waitFor();
    }
    public static void go_to_advanced_Search(WebDriver driver) {
        String go_to_advanced_search =user_interface.getProperty("go_to_advanced_search");
        WebElement e2 = driver.findElement(By.cssSelector(go_to_advanced_search));
        e2.click();
        waitFor();
    }
    public static void clickOnSearch_InAdvancedSearchPage(WebDriver driver) {
        WebElement e4 = driver.findElement(By.cssSelector("#search-button"));
        e4.click();
        waitFor();
    }

    public static void enter_data_in_Message_ID_Advanced_Search(WebDriver driver, String id) {

        WebElement e1 = driver.findElement(By.cssSelector("#uniqueId"));     //Message ID text field in "Advanced Search" page.
        e1.sendKeys(id);                                                   //Entering extracted ID in "Message ID" textfield.
    }

    public static void enter_data_in_Entity_Textfield_InAdvancedSearchPage(WebDriver driver, String name) {
        WebElement e2 = driver.findElement(By.cssSelector("#entity"));
        e2.sendKeys(name);
    }

    public static String whole_mail_after_entering_entity_in_Advanced_Search_Page(WebDriver driver) {
        WebElement e3 = driver.findElement(By.cssSelector("#jog_contents"));
        String whole_text = e3.getText();
        return whole_text;
    }


//HELPER METHOD FOR COUNTING NUMBER OF CONTACTS IN ANY PAGE

    public static int countNumberOfContactsIn_a_Page(WebDriver driver) {
        WebElement e = driver.findElement(By.cssSelector("#text"));
        String contact_list = e.getText();
        int count = 0;
        for (int i = 0; i < contact_list.length(); i++) {
            if (contact_list.charAt(i) == '-' && contact_list.charAt(i + 1) == '-') {
                count++;
            }
        }
        return count;
    }

    //HELPER METHOD FOR CHECKING PAGES

    public static boolean isPageOpened(WebDriver driver,String title)
    {
      if(driver.getTitle().contains(title))
      {
          return true;
      }
      else
      {
          return false;
      }
    }
}