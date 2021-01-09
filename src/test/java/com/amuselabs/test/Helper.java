package com.amuselabs.test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;


//CREATED BY ASHUTOSH
//This class contains the major helper methods for running test cases

public class Helper {
    public static Properties user_interface = new Properties();  //user_interface variable is used to read properties file named as USER_INTERFACE.properties
    public static String BASE_DIR;

    private static String DEFAULT_BASE_DIR = System.getProperty("user.home") + File.separator + "epadd-test";
    static Properties VARS;
    private static String EPADD_TEST_PROPS_FILE = System.getProperty("user.home") + File.separator + "epadd.test.properties";
    private static Process epaddProcess = null;
    private Stack<String> tabStack = new Stack<>();
    private String screenshotsDir;

    public static String DRIVER_DIR;
    WebDriver driver;

    public Helper() {
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

        for (String key : VARS.stringPropertyNames()) {
            String val = System.getProperty(key);
            if (val != null && val.length() > 0)
                VARS.setProperty(key, val);
        }

        BASE_DIR = VARS.getProperty("epadd.test.dir");
        if (BASE_DIR == null)
            BASE_DIR = DEFAULT_BASE_DIR + "/src/test/resources";

        new File(BASE_DIR).mkdirs();
        screenshotsDir = BASE_DIR + File.separator + "screenshots";
        new File(screenshotsDir).mkdirs();

        // log.info ("Base dir for this test run is: " + BASE_DIR);

        DRIVER_DIR = VARS.getProperty("webDriver.dir"); //webDriver.Dir can be uncommented in epadd.test.properties if the selenium driver is not located in the resources folder
        if(DRIVER_DIR == null)
            DRIVER_DIR = "src/test/resources";
    }

    public Helper(String browser_name) {

        if (browser_name.equalsIgnoreCase("chrome")) {
            System.setProperty("webdriver.gecko.driver", DRIVER_DIR + File.separator + "chromedriver.exe");
            driver = new ChromeDriver();
        } else if (browser_name.equalsIgnoreCase("firefox")) {
            System.setProperty("webdriver.gecko.driver", DRIVER_DIR + File.separator + "geckodriver");
            driver = new FirefoxDriver();
        }
    }

    public void read_properties_file() {
        try {
            InputStream s = Helper.class.getClassLoader().getResourceAsStream("USER_INTERFACE.properties");
            user_interface.load(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //this method opens epadd
    public void open_ePADD(String mode) throws IOException {

        String errFile = System.getProperty("java.io.tmpdir") + File.separator + "epadd-test.err.txt";
        String outFile = System.getProperty("java.io.tmpdir") + File.separator + "epadd-test.out.txt";
        String cmd = VARS.getProperty("cmd");
        if (cmd == null) {
            // log.warn ("Please confirm cmd in " + EPADD_TEST_PROPS_FILE);
            throw new RuntimeException("no command to start epadd");
        }

        cmd = "java -Depadd.mode=" + mode + " -Depadd.base.dir=" + BASE_DIR + " " + cmd;
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
    public static void start_ePADD() throws Exception {
        Helper browser = new Helper();
        browser.open_ePADD("appraisal");
    }

    //this method is used to switch between tabs.
    public void changeWindow() {
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
        waitFor(5);

    }

    //this method is for wait,after clicking on a button,if wait is invoked,it waits for 5 seconds.Uses "StepDefs" class.
    public static void waitFor(int time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void longWait(int time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //METHOD THAT RETURNS ALL THE UNDERLINE WORDS IN A PAGE.
    public List strings_underlined_on_message_window() {
        read_properties_file();
        String underlined_words = user_interface.getProperty("underlined_words_on_message_window");
        List<WebElement> underline = driver.findElements(By.cssSelector(underlined_words));
        return underline;
    }

    //METHOD THAT RETURNS ALL THE HIGHLIGHTED ENTITIES IN A PAGE
    public List strings_highlighted_on_message_window() {
        read_properties_file();
        String highlighted_words = user_interface.getProperty("highlighted_words_on_message_window");
        List<WebElement> highlighted = driver.findElements(By.cssSelector(highlighted_words));
        return highlighted;
    }


    //HELPER METHODS FOR CORRESPONDENTS

    //this method returns unique contact id of a name which is to be clicked.For eg,contact id of "Kathleen Shanahan" is 14.
    public int get_contact_id() {
        String name_of_a_correspondent = TestSuite_CorrespondentsTest.user_interface.getProperty("name_of_a_Correspondent");
        WebElement element = driver.findElement(By.xpath(name_of_a_correspondent));
        String s = element.getAttribute("href");
        int num = 0;
        String split[] = s.split("&");
        for (String s1 : split) {
            if (s1.contains("contact=")) {
                num = Integer.parseInt(s1.substring(s1.indexOf('=') + 1));
                break;
            }
        }
        return num;
    }

    //this method returns the contact id of the string present in "From" of the mail opened.
    public int get_contact_id_From() {
        String From = TestSuite_CorrespondentsTest.user_interface.getProperty("From");
        WebElement e = driver.findElement(By.xpath(From));
        String href = e.getAttribute("href");
        String split[] = href.split("&");
        int id = 0;
        for (String s : split) {
            if (s.contains("contact=")) {
                id = Integer.parseInt(s.substring(s.indexOf('=') + 1));
                break;
            }
        }
        return id;
    }

    //this method returns the conatact id of the string present in "To" of the mail opened.
    public ArrayList<Integer> get_contact_id_To() {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        Collection<WebElement> anchors = driver.findElements(By.xpath(TestSuite_CorrespondentsTest.user_interface.getProperty("To")));
        for (WebElement a : anchors) {
            String href = a.getAttribute("href");
            int num = 0;
            String split[] = href.split("&");
            for (String s : split) {
                if (s.contains("contact=")) {
                    num = Integer.parseInt(s.substring(s.indexOf('=') + 1));
                    ids.add(num);
                    break;
                }
            }
        }
        return ids;
    }

    public ArrayList<Integer> get_contact_id_Cc() {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        Collection<WebElement> anchors = driver.findElements(By.xpath(TestSuite_CorrespondentsTest.user_interface.getProperty("Cc")));
        for (WebElement a : anchors) {
            String href = a.getAttribute("href");
            int num = 0;
            String split[] = href.split("&");
            for (String s : split) {
                if (s.contains("contact=")) {
                    num = Integer.parseInt(s.substring(s.indexOf('=') + 1));
                    ids.add(num);
                    break;
                }
            }
        }
        return ids;
    }

    //this method returns the contact id of the string present in "bcc" of the mail opened.
    public ArrayList<Integer> get_contact_id_bcc() {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        Collection<WebElement> anchors = driver.findElements(By.xpath(TestSuite_CorrespondentsTest.user_interface.getProperty("bcc")));
        for (WebElement a : anchors) {
            String href = a.getAttribute("href");
            int num = 0;
            String split[] = href.split("&");
            for (String s : split) {
                if (s.contains("contact=")) {
                    num = Integer.parseInt(s.substring(s.indexOf('=') + 1));
                    ids.add(num);
                    break;
                }
            }
        }
        return ids;
    }

    //this method clicks on "Correspondents" button in browse-top page.
    public void click_on_correspondents_browse_top_page() {
        driver.get("http://localhost:9099/epadd/browse-top");    //navigating to browse-top page.
        String correspondents = TestSuite_CorrespondentsTest.user_interface.getProperty("correspondents");  //reading a correspondent's name(cssSelector) through properties file(user_interface)
        WebElement e = driver.findElement(By.cssSelector(correspondents));  //finding a correspondent's name according to cssSelector
        e.click();
        waitFor(5);
    }

    //this method clicks on a name in Correspondents ,eg,it will click on "Kathleen Shanahan" if its selector is given in USER_INTERFACE.properties.
    public String click_on_name_in_correspondents() {
        String name_of_a_correspondent = TestSuite_CorrespondentsTest.user_interface.getProperty("name_of_a_Correspondent");
        WebElement e = driver.findElement(By.xpath(name_of_a_correspondent));
        String nametocheck = e.getText();
        e.click();
        waitFor(5);
        changeWindow();
        return nametocheck;
    }

    //this method returns the ID present in messages opened,eg "137f596e4ed37eae766d40276fad9f3c5452b29c2649f39318b9221bd89e8de9"
    public String get_message_id_from_message_window() {
        String id = TestSuite_CorrespondentsTest.user_interface.getProperty("id");
        WebElement e = driver.findElement(By.cssSelector(id));
        String onClickValue = e.getText();
        onClickValue = onClickValue.substring(0, onClickValue.length() - 6);
        return onClickValue;
    }

    //this method returns the number mentioned in Correspondents button in "Browse-top" page.eg "1749"
    public int get_number_in_correspondents_browse_top_Page() {
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
        waitFor(5);
        return number;
    }

    //this method clicks on "Edit Correspondents" button
    public void click_on_edit_correspondents() {
        String edit_correspondents = TestSuite_CorrespondentsTest.user_interface.getProperty("edit_correspondents");
        WebElement e = driver.findElement(By.cssSelector(edit_correspondents));
        e.click();
        waitFor(5);
    }

    //returns the entire text of mail,including strings mentioned in "From","To","cc","bcc"
    public String get_text_of_entire_mail_opened() {
        String text_of_entire_mail = TestSuite_CorrespondentsTest.user_interface.getProperty("text_of_entire_mail");
        WebElement e = driver.findElement(By.cssSelector(text_of_entire_mail));
        String body = e.getText();
        return body;
    }
    //HELPER METHOD FOR COUNTING NUMBER OF CONTACTS IN ANY PAGE

    public int countNumberOfContactsIn_a_Page() {
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

    //common Helper methods

    //this method returns the number of sent messages mentioned in message window.
    public int sent_messages_in_message_window() {
        read_properties_file();
        String sent_messages_in_message_window = user_interface.getProperty("sent_messages_in_message_window");
        WebElement e = driver.findElement(By.cssSelector(sent_messages_in_message_window));
        int numberOfSentMessages = Integer.parseInt(e.getText().substring(1, e.getText().length() - 1));
        return numberOfSentMessages;
    }

    //this method returns the number of received messages mentioned in message window.
    public int received_messages_in_message_window() {
        read_properties_file();
        String received_messages_in_message_window = user_interface.getProperty("received_messages_in_message_window");
        WebElement e = driver.findElement(By.cssSelector(received_messages_in_message_window));
        int numberOfReceivedMessages = Integer.parseInt(e.getText().substring(1, e.getText().length() - 1));
        return numberOfReceivedMessages;
    }

    //return the actual number of messages opened after clicking on a name.for eg, it returns 1020 if messages opened are "1/1020"
    public int number_of_messages_opened_after_clicking_on_a_name() {
        read_properties_file();
        String number_of_messages_opened_after_clicking_on_a_name = user_interface.getProperty("number_of_messages_opened_after_clicking_on_a_name");
        WebElement e = driver.findElement(By.cssSelector(number_of_messages_opened_after_clicking_on_a_name));
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

    //HELPER METHODS RELATED TO ADVANCED SEARCH
    public void click_on_search() {
        read_properties_file();
        String click_on_search = user_interface.getProperty("click_on_search");
        WebElement e = driver.findElement(By.cssSelector(click_on_search));
        e.click();
        waitFor(5);
    }

    public void go_to_advanced_Search() {
        String go_to_advanced_search = user_interface.getProperty("go_to_advanced_search");
        WebElement e2 = driver.findElement(By.cssSelector(go_to_advanced_search));
        e2.click();
        waitFor(5);
    }

    public void clickOnSearch_InAdvancedSearchPage() {
        WebElement e4 = driver.findElement(By.cssSelector("#search-button"));
        e4.click();
        waitFor(5);
    }

    public void enter_data_in_Message_ID_Advanced_Search(String id) {

        WebElement e1 = driver.findElement(By.cssSelector("#uniqueId"));     //Message ID text field in "Advanced Search" page.
        e1.sendKeys(id);                                                   //Entering extracted ID in "Message ID" textfield.
    }

    public void enter_data_in_Entity_Textfield_InAdvancedSearchPage(String name) {
        WebElement e2 = driver.findElement(By.cssSelector("#entity"));
        e2.sendKeys(name);
    }

    public String whole_mail_after_entering_entity_in_Advanced_Search_Page() {
        WebElement e3 = driver.findElement(By.cssSelector("#jog_contents"));
        String whole_text = e3.getText();
        return whole_text;
    }

    public void close_browser() {
        driver.quit();
    }

    //HELPER METHODS FOR PERSON-ENTITY

    //this method clicks on "Person-Entities" button in browse-top page
    public void click_on_person_entities_browse_top_page() {
        driver.get("http://localhost:9099/epadd/browse-top");
        String click_on_Person_Entities = TestSuite_Person_EntitiesTest.user_interface.getProperty("click_on_Person_Entities");
        WebElement e = driver.findElement(By.cssSelector(click_on_Person_Entities));
        e.click();
        waitFor(5);
    }

    //this method returns the number mentioned in front of Person-Entities's name (eg 914 mentioned in front of "Jeb Bush") and then clicks on that name
    public int get_message_number_person_entity_and_click_on_name() {
        String click_on_name_in_Person_Entities = TestSuite_Person_EntitiesTest.user_interface.getProperty("click_on_name_in_Person_Entities");
        WebElement name = driver.findElement(By.cssSelector(click_on_name_in_Person_Entities)); // PersonEntities name to be clicked
        String message_number = TestSuite_Person_EntitiesTest.user_interface.getProperty("message_number");
        WebElement message_number_of_a_PersonEntity = driver.findElement(By.cssSelector(message_number));//message number in front of name
        int number = Integer.parseInt(message_number_of_a_PersonEntity.getText());//storing that message number as number
        name.click();
        waitFor(5);
        changeWindow();
        return number;
    }

    public int get_message_number() {
        String message_number = TestSuite_Person_EntitiesTest.user_interface.getProperty("message_number");
        WebElement message_number_of_a_PersonEntity = driver.findElement(By.cssSelector(message_number));//message number in front of name
        int number = Integer.parseInt(message_number_of_a_PersonEntity.getText());
        return number;
    }

    //returns the name of a Person-Entity(eg Jeb Bush) and clicks on it.
    public String get_name_from_person_entity_and_click_on_name() {
        String click_on_name_in_Person_Entities = TestSuite_Person_EntitiesTest.user_interface.getProperty("click_on_name_in_Person_Entities");
        WebElement e = driver.findElement(By.cssSelector(click_on_name_in_Person_Entities));
        String name = e.getText();
        e.click();
        waitFor(5);
        changeWindow();
        return name;
    }

    //returns the text of the body of mail excluding "To","From","cc","bcc" and other headers.
    public String body_of_mail_after_clicking_a_name_in_person_entity() {
        String body_of_mail = TestSuite_Person_EntitiesTest.user_interface.getProperty("body_of_mail");
        WebElement e1 = driver.findElement(By.cssSelector(body_of_mail));
        String body = e1.getText();
        return body;
    }

    //returns the number mentioned in the "Person-Entities" button in browse-top page and clicks on Person-entities
    public int get_number_in_person_entity_browse_top_page_and_click_on_person_entities() {
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
        waitFor(5);
        return number;
    }

    //clicks on Edit Entities in Person-Entities page
    public void click_on_edit_entities_person_entity() {
        String clickOnEditEntities_PersonEntity = TestSuite_Person_EntitiesTest.user_interface.getProperty("clickOnEditEntities_PersonEntity");
        WebElement e = driver.findElement(By.cssSelector(clickOnEditEntities_PersonEntity));
        e.click();
        waitFor(5);
    }

    //HELPER METHODS FOR OTHER ENTITIES
    public void click_on_other_entities() {
        driver.get("http://localhost:9099/epadd/browse-top");
        String clickOnOtherEntities = TestSuite_Other_EntitiesTest.user_interface.getProperty("clickOnOtherEntities");
        WebElement e = driver.findElement(By.cssSelector(clickOnOtherEntities));
        e.click();
        waitFor(5);
    }

    public void click_on_entity_name_other_entities_page() {
        String clickOnEntityName_OtherEntitiesPage = TestSuite_Other_EntitiesTest.user_interface.getProperty("clickOnEntityName_OtherEntitiesPage");
        WebElement e = driver.findElement(By.cssSelector(clickOnEntityName_OtherEntitiesPage));
        e.click();
        waitFor(5);
        changeWindow();
    }

    public String click_on_sub_entity_name_other_entities_page() {
        String sub_entity_name = TestSuite_Other_EntitiesTest.user_interface.getProperty("sub_entity_name");
        WebElement e = driver.findElement(By.cssSelector(sub_entity_name));
        String entity_name = e.getText();
        e.click();
        waitFor(5);
        changeWindow();
        return entity_name;
    }

    public String body_of_mail_opened_onclick_subentity() {
        waitFor(5);
        waitFor(5);
        waitFor(5);
        String body_of_mail_opened_onclick_subEntity = TestSuite_Other_EntitiesTest.user_interface.getProperty("body_of_mail_opened_onclick_subEntity");
        WebElement e2 = driver.findElement(By.cssSelector(body_of_mail_opened_onclick_subEntity));
        String body = e2.getText();
        return body;
    }

    public int message_number_other_entities_page() {
        String message_number = TestSuite_Other_EntitiesTest.user_interface.getProperty("message_number");
        WebElement e = driver.findElement(By.cssSelector(message_number));
        int number = Integer.parseInt(e.getText());
        return number;
    }

    public int number_of_entities_other_entites_page()       //mail entity like Place(number=411)
    {
        String number_of_entities_OtherEntites = TestSuite_Other_EntitiesTest.user_interface.getProperty("number_of_entities_OtherEntites");
        WebElement e = driver.findElement(By.cssSelector(number_of_entities_OtherEntites));
        int number = Integer.parseInt(e.getText());
        return number;
    }

    public void click_on_edit_entities_other_entities_page() {
        String clickOnEditEntities = TestSuite_Other_EntitiesTest.user_interface.getProperty("clickOnEditEntities");
        WebElement e1 = driver.findElement(By.cssSelector(clickOnEditEntities));
        e1.click();
        waitFor(5);
    }

    //HELPER METHODS FOR LABELS
    public void click_on_labels() {
        driver.get("http://localhost:9099/epadd/browse-top");
        String click_on_labels = TestSuite_LabelsTest.user_interface.getProperty("click_on_labels");
        WebElement e = driver.findElement(By.cssSelector(click_on_labels));
        e.click();
    }

    public String get_label_name() {
        String label_name = TestSuite_LabelsTest.user_interface.getProperty("label_name");
        WebElement e = driver.findElement(By.cssSelector(label_name));
        String name = e.getText();
        return name;
    }

    public String click_on_label_and_return_its_name() {
        String label_name = TestSuite_LabelsTest.user_interface.getProperty("label_name");
        WebElement e = driver.findElement(By.cssSelector(label_name));
        String name = e.getText();
        e.click();
        changeWindow();
        return name;
    }

    public String get_label_type() {
        String label_type = TestSuite_LabelsTest.user_interface.getProperty("label_type");
        WebElement e = driver.findElement(By.cssSelector(label_type));
        String type = e.getText();
        return type;
    }

    public void click_on_edit_label() {
        String click_on_edit_label = TestSuite_LabelsTest.user_interface.getProperty("click_on_edit_label");
        WebElement e = driver.findElement(By.cssSelector(click_on_edit_label));
        e.click();
        waitFor(5);
    }

    public String edit_label_name(String label_name_to_be_modified) {
        label_name_to_be_modified = label_name_to_be_modified + "xy";
        String content_of_label_name_textfield_in_edit_labels = TestSuite_LabelsTest.user_interface.getProperty("content_of_label_name_textfield_in_edit_labels");
        WebElement e = driver.findElement(By.cssSelector(content_of_label_name_textfield_in_edit_labels));
        e.clear();
        e.sendKeys(label_name_to_be_modified);
        return label_name_to_be_modified;
    }

    public int number_of_labels() {
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

    public int number_of_labels_on_messages() {
        String number_of_labels_on_messages = TestSuite_LabelsTest.user_interface.getProperty("number_of_labels_on_messages");
        WebElement e = driver.findElement(By.cssSelector(number_of_labels_on_messages));
        int n = Integer.parseInt(e.getText());
        return n;
    }

    public void click_on_new_label() {
        String click_on_new_label = TestSuite_LabelsTest.user_interface.getProperty("click_on_new_label");
        WebElement new_label = driver.findElement(By.cssSelector(click_on_new_label));
        new_label.click();
    }

    public void enter_data_in_label_name() {
        String enter_data_in_label_name = TestSuite_LabelsTest.user_interface.getProperty("enter_data_in_label_name");
        WebElement label_name = driver.findElement(By.cssSelector(enter_data_in_label_name));
        label_name.sendKeys("Sample Label");
    }

    public void click_on_label_type() {

        String click_on_label_type = TestSuite_LabelsTest.user_interface.getProperty("click_on_label_type");
        WebElement label_type = driver.findElement(By.cssSelector(click_on_label_type));
        label_type.click();
    }

    public void choose_label_type() {
        String choose_label_type = TestSuite_LabelsTest.user_interface.getProperty("choose_label_type");
        WebElement e = driver.findElement(By.cssSelector(choose_label_type));
        e.click();
    }

    public void enter_data_in_label_description() {
        String enter_data_in_label_description = TestSuite_LabelsTest.user_interface.getProperty("enter_data_in_label_description");
        WebElement label_description = driver.findElement(By.cssSelector(enter_data_in_label_description));
        label_description.sendKeys("Just a sample label");
    }

    public void click_on_update() {
        String click_on_update = TestSuite_LabelsTest.user_interface.getProperty("click_on_update");
        WebElement update = driver.findElement(By.cssSelector(click_on_update));
        update.click();
        waitFor(5);
    }

    public void click_on_ok() {
        String click_on_ok = TestSuite_LabelsTest.user_interface.getProperty("click_on_ok");
        WebElement ok = driver.findElement(By.cssSelector(click_on_ok));
        ok.click();
        waitFor(5);
    }

    public String click_on_label_in_message_window_of_correspondents_and_choose_a_label() {
        String click_on_label_in_message_window_of_correspondents = TestSuite_LabelsTest.user_interface.getProperty("click_on_label_in_message_window_of_correspondents");
        WebElement e = driver.findElement(By.cssSelector(click_on_label_in_message_window_of_correspondents));
        e.click();
        String choose_label = TestSuite_LabelsTest.user_interface.getProperty("choose_label");
        WebElement e1 = driver.findElement(By.cssSelector(choose_label));
        e1.click();
        String s = e1.getText();
        return s;
    }

    public void click_on_Correspondents_BrowseTopPage_through_labels() {
        driver.get("http://localhost:9099/epadd/browse-top");    //navigating to browse-top page.
        String correspondents = TestSuite_LabelsTest.user_interface.getProperty("correspondents");  //reading a correspondent's name(cssSelector) through properties file(user_interface)
        WebElement e = driver.findElement(By.cssSelector(correspondents));  //finding a correspondent's name according to cssSelector
        e.click();
        waitFor(5);
    }

    public String clickOnNameInCorrespondents_through_labels() {
        String name_of_a_correspondent = TestSuite_LabelsTest.user_interface.getProperty("name_of_a_Correspondent");
        WebElement e = driver.findElement(By.xpath(name_of_a_correspondent));
        String nametocheck = e.getText();
        e.click();
        waitFor(5);
        changeWindow();
        waitFor(5);
        return nametocheck;
    }

    //Method that selects the element of "No matching messages." and return its test
    public String get_string_no_matching_messages() {
        String selector_of_NoMatchingMessages_message = TestSuite_LabelsTest.user_interface.getProperty("selector_of_NoMatchingMessages_message");
        WebElement e = driver.findElement(By.cssSelector(selector_of_NoMatchingMessages_message));
        String s = e.getText();
        return s;
    }

    //HELPER METHODS RELATED TO LEXICON
    public void clickOnLexicon() {
        driver.get("http://localhost:9099/epadd/browse-top");
        String click_on_labels = TestSuite_LexiconTest.lexicon.getProperty("click_on_lexicon");
        WebElement e = driver.findElement(By.cssSelector(click_on_labels));
        e.click();
    }

    public String click_on_Lexicon_category_and_return_name() {
        String Lexicon_category = TestSuite_LexiconTest.lexicon.getProperty("Lexicon_category");
        WebElement e = driver.findElement(By.cssSelector(Lexicon_category));
        String s = e.getText();
        e.click();
        waitFor(5);
        changeWindow();
        return s;
    }

    public int number_of_messages_displayed_in_front_of_Lexicon() {
        String number_of_messages_displayed_in_front_of_Lexicon = TestSuite_LexiconTest.lexicon.getProperty("number_of_messages_displayed_in_front_of_Lexicon");
        WebElement e1 = driver.findElement(By.cssSelector(number_of_messages_displayed_in_front_of_Lexicon));
        int n = Integer.parseInt(e1.getText());
        return n;
    }
}
