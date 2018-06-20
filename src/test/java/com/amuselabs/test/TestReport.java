package com.amuselabs.test;

/**
 * Created by charu on 12/15/17.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import com.amuselabs.util.*;
import static com.amuselabs.test.PropertyNames.*;

/**
 * Created by charu on 12/15/17.
 */
class TestReport {

    String browserName;
    int nPassed;
    int nFailed;
    int nUndetermined;
    String time;

    ArrayList<TesterPM> tests;

    static String screenshotsDir;
    static String reportsDir;

    private static String DEFAULT_WAPO_PICKER_LINK = "https://www.washingtonpost.com/crossword-puzzles/daily/";
    private static String DEFAULT_NEWSDAY_PICKER_LINK = "https://www.newsday.com/entertainment/extras/crossword-puzzle-1.6375288";
    private static String DEFAULT_REBUS_PUZZLE_LINK = "http://cdn1.amuselabs.com/wapo/crossword?id=ebirnholz_170430&set=wapo-eb&compact=1&popup=0";
    private static String DEFAULT_CIRCLED_LETTER_PUZZLE_LINK = "http://cdn1.amuselabs.com/wapo/crossword?id=ebirnholz_170430&set=wapo-eb&compact=1&popup=0";
    private static String DEFAULT_CROSSWORD_LINK = "http://cdn1.amuselabs.com/wapo/crossword?id=tca171226&set=wapo-daily";

    TestConfig wapoConfig;
    TestConfig newsdayConfig;
    TestConfig defaultConfig;
    TestConfig rebusConfig;
    TestConfig circledLetterConfig;
    ArrayList<String> options;
    private static String BASE_DIR;
    private static String DEFAULT_BASE_DIR = System.getProperty("user.home") + File.separator + "pm-test";
    private static String PMM_TEST_PROPS_FILE = System.getProperty("user.home") + File.separator + PROPS_FILE_NAME;
    private static Log log = LogFactory.getLog(TestReport.class);
    static Properties VARS;

    static final String userHome = System.getProperty ("user.home");

    static String defaultTestPuzzleLink;

    public TestReport(String rDir, String bName, String[] args) {
        reportsDir = rDir;
        browserName = bName;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        time = dateFormat.format(date);

        nFailed = 0;
        nPassed = 0;
        tests = new ArrayList<>();
        createTestConfigs();
        options = new ArrayList<>();
        for (int i = 1; i < args.length; ++i) {
            options.add(args[i]);
        }
    }

    public void createTestConfigs() {
        VARS = new Properties();

        File f = new File(PMM_TEST_PROPS_FILE);
        if (f.exists() && f.canRead()) {
            log.info("Reading configuration from: " + PMM_TEST_PROPS_FILE);
            try {
                InputStream is = new FileInputStream(PMM_TEST_PROPS_FILE);
                VARS.load(is);
            } catch (Exception e) {
                TestUtils.print_exception("Error reading pmm properties file " + PMM_TEST_PROPS_FILE, e, log);
            }
        } else {
            log.warn("PMM properties file " + PMM_TEST_PROPS_FILE + " does not exist or is not readable");
        }

        for (String key : VARS.stringPropertyNames()) {
            String val = System.getProperty(key);
            if (val != null && val.length() > 0)
                VARS.setProperty(key, val);
        }

        BASE_DIR = VARS.getProperty(PROPS_FILE_NAME);
        if (BASE_DIR == null)
            BASE_DIR = DEFAULT_BASE_DIR;

        new File(BASE_DIR).mkdirs();
        screenshotsDir = TestReport.reportsDir+ File.separator + "screenshots";
        new File(screenshotsDir).mkdirs();

        String wapoLink = VARS.getProperty(PROP_WAPO_PICKER_LINK);
        wapoConfig = new TestConfig();
        wapoConfig.crosswordLink = (Util.nullOrEmpty(wapoLink) ? DEFAULT_WAPO_PICKER_LINK : wapoLink);
        wapoConfig.embedded = true;
        wapoConfig.puzzleType = 0;

        String newsdayLink = VARS.getProperty(PROP_NEWSDAY_PICKER_LINK);
        newsdayConfig = new TestConfig();
        newsdayConfig.crosswordLink = (Util.nullOrEmpty(newsdayLink) ? DEFAULT_NEWSDAY_PICKER_LINK : newsdayLink);
        newsdayConfig.embedded = true;
        newsdayConfig.puzzleType = 2;

        String crosswordLink = VARS.getProperty(PROP_CROSSWORD_LINK);
        defaultConfig = new TestConfig();
        defaultConfig.crosswordLink = (Util.nullOrEmpty(crosswordLink) ? DEFAULT_CROSSWORD_LINK : crosswordLink);

        String rebusLink = VARS.getProperty(PROP_REBUS_CROSSWORD_LINK);
        rebusConfig = new TestConfig();
        rebusConfig.crosswordLink = (Util.nullOrEmpty(rebusLink) ? DEFAULT_REBUS_PUZZLE_LINK : rebusLink);
        rebusConfig.rebus = true;
        rebusConfig.embedded = false;
        rebusConfig.startButtonPresent = true;

        String circledLetterLink = VARS.getProperty(PROP_CIRCLED_LETTER_CROSSWORD_LINK);
        circledLetterConfig = new TestConfig();
        circledLetterConfig.crosswordLink = (Util.nullOrEmpty(rebusLink) ? DEFAULT_CIRCLED_LETTER_PUZZLE_LINK : circledLetterLink);
        circledLetterConfig.embedded = false;
        circledLetterConfig.startButtonPresent = true;
    }

    public void printReport() {
        //prints the test report in html format
        File file = new File(reportsDir + File.separator + "report.html");

        String screenShotsDir = reportsDir + File.separator + "screenshots";
        new File(screenShotsDir).mkdir();
        String screenShotsFilename = screenShotsDir + File.separator + StepDefsPM.BROWSER_NAME + "-screenshots.html";
        File file1 = new File(screenShotsFilename);

        FileWriter fr = null;
        FileWriter fr1 = null;
        try {

            fr = new FileWriter(file);
            fr1 = new FileWriter(file1);
            fr.write("<html> <head>\n" +
                    "<style>\n" +
                    "td, th { border: 1px solid #ddd;\n" +
                    "    padding: 10px;}\n" +
                    "tr:hover {background-color: #F5F7FC;}" +
                    "th {\n" +
                    "    padding-top: 12px;\n" +
                    "    padding-bottom: 12px;\n" +
                    "    text-align: left;\n" +
                    "    background-color: #0A54E9 ;\n" +
                    "    color: white;\n" +
                    "}" +
                    "table {\n" +
                    "    border-collapse: collapse;" +
                    "font-family: \"Trebuchet MS\", Arial, Helvetica, sans-serif;" +
                    "   width: 100%;\n}" +
                    "</style>\n" +
                    "</head> <body> <h1> Automation Report </h1> <h2> Time tested: " + time + "</h2>  <br> <b> Browser Name: </b> " + StepDefsPM.BROWSER_NAME + " <br>" + "<b> OS Name: </b>" + StepDefsPM.opsystem + " <br> <font color=\"green\"> No. Of Test Cases Passed:" + nPassed + "/" + (nPassed + nFailed + nUndetermined) + "</font> <br> <font color=\"red\"> No. Of Test Cases Failed:" + nFailed + "/" + (nPassed + nFailed + nUndetermined) + "</font>" + "<br> <font color=\"blue\"> No. Of Test Cases requiring manual inspection:" + nUndetermined + "/" + (nPassed + nFailed + nUndetermined) + "</font>" +
                    "<br> <a href=\"" + screenShotsFilename + "\" > Click here to see all screenshots </a>");
            fr1.write("<html>" + "<head>" + "<style>\n" + "td, th { " + "   padding: 15px;}\n" + "</style> </head>" + "<body> <h1> Automation Report - Screenshots </h1>  <h2> Time tested: " + time + "</h2>  <br> <b> Browser Name: </b>" + StepDefsPM.BROWSER_NAME + " <br>" + "<b> OS Name: </b>" + StepDefsPM.opsystem + " <br>" + "<table>\n" +
                    "  <tr>\n" +
                    "    <td bgcolor=\"#CEF8CB\" style=\"width:10px; height:10px;\" > </td>\n" +
                    "    <td> Passed </td>\n" +
                    "     <td bgcolor=\"#EEA4A4\" style=\"width:10px; height:10px;\" > </td>\n" +
                    "     <td> Failed </td>\n" +
                    "      <td bgcolor=\"#A4D2EE\" style=\"width:10px; height:10px;\" > </td>\n" +
                    "      <td> Undetermined </td>\n" +
                    "     </tr>\n" +
                    "</table>\n" +
                    "<br>\n" +
                    "<hr>\n" +
                    "<br>\n" + "  <table>");
            fr.write("<br> <br> <table border=\"2px\"> <tr> <th> Test Id </th> <th> Puzzle Tested </th> <th> Status </th> <th> Error Message</th> <th> ScreenShot </th> <th> Steps Carried Out </th> </tr>");
            for (int i = 0; i < tests.size(); ++i) {
                fr.write("<tr>");
                fr.write("<td>" + tests.get(i).id + "</td>");
                fr.write("<td> <a href=\"" + tests.get(i).BASE_URL + "\">" + tests.get(i).puzzleTitle + "</a> </td>");
                if (tests.get(i).passed == 1) {
                    fr.write("<td  bgcolor=\"#4CAF50\"> Passed </td>");
                    fr.write("<td>  </td> <td>");
                    fr1.write("<tr> <td bgcolor=\"#CEF8CB\"> ");
                    for (int m = 0; m < tests.get(i).screenshotPath.size(); ++m) {
                        if (tests.get(i).screenshotPath.get(m).indexOf(".png") >= 0) {
                            fr.write(" <a href=\"" + "screenshots" + tests.get(i).screenshotPath.get(m).substring(tests.get(i).screenshotPath.get(m).lastIndexOf(File.separator)) + "\"> See Screenshot </a> <br> ");
                            fr1.write("<img src=\"" + tests.get(i).screenshotPath.get(m).substring(tests.get(i).screenshotPath.get(m).lastIndexOf(File.separator) + 1) + "\" height=\"500px\"> <br> ");
                        } else
                            fr.write("<a href=\"" + tests.get(i).screenshotPath.get(m) + "\"> See PDF </a> <br>");
                    }
                    fr.write("</td> <td> <ul>");
                    for (int j = 0; j < tests.get(i).steps.size(); ++j) {
                        fr.write("<li>" + tests.get(i).steps.get(j) + "</li>");
                    }
                    fr.write("</ul> </td>");
                } else if (tests.get(i).passed == 0) {
                    fr.write("<td  bgcolor=\"red\"> Failed </td>");
                    fr.write("<td>" + tests.get(i).error + "</td> <td>");
                    fr1.write("<tr> <td bgcolor=\"#EEA4A4\">");
                    for (int m = 0; m < tests.get(i).screenshotPath.size(); ++m) {
                        if (tests.get(i).screenshotPath.get(m).indexOf(".png") >= 0) {
                            fr.write("<a href=\"" + "screenshots" + tests.get(i).screenshotPath.get(m).substring(tests.get(i).screenshotPath.get(m).lastIndexOf(File.separator)) + "\"> See Screenshot </a> <br>");
                            fr1.write(" <img src=\"" + tests.get(i).screenshotPath.get(m).substring(tests.get(i).screenshotPath.get(m).lastIndexOf(File.separator) + 1) + "\" height=\"500px\"> <br>");
                        } else
                            fr.write("<a href=\"" + tests.get(i).screenshotPath.get(m) + "\"> See PDF </a> <br>");
                    }
                    fr.write("<td> <ul>");
                    for (int j = 0; j < tests.get(i).steps.size() - 1; ++j) {
                        fr.write("<li>" + tests.get(i).steps.get(j) + "</li>");
                    }
                    fr.write("<li> <font color=\"red\"> " + tests.get(i).steps.get(tests.get(i).steps.size() - 1) + " </font> </li>");
                    fr.write("</ul> </td>");
                } else {
                    fr.write("<td  bgcolor=\"#2896D9\"> Undetermined </td>");
                    fr.write("<td></td> <td>");
                    fr1.write("<tr> <td bgcolor=\"#A4D2EE\">");
                    for (int m = 0; m < tests.get(i).screenshotPath.size(); ++m) {
                        if (tests.get(i).screenshotPath.get(m).indexOf(".png") >= 0) {
                            fr.write("<a href=\"" + "screenshots" + tests.get(i).screenshotPath.get(m).substring(tests.get(i).screenshotPath.get(m).lastIndexOf(File.separator)) + "\"> See Screenshot </a> <br>");
                            fr1.write(" <img src=\"" + tests.get(i).screenshotPath.get(m).substring(tests.get(i).screenshotPath.get(m).lastIndexOf(File.separator) + 1) + "\" height=\"500px\"> <br> ");
                        } else {
                            fr.write("<a href=\"" + tests.get(i).screenshotPath.get(m) + "\"> See PDF </a> <br>");
                            fr1.write("<a href=\"" + tests.get(i).screenshotPath.get(m) + "\"> See PDF </a> <br>");
                        }
                    }
                    fr.write("<td> <ul>");
                    for (int j = 0; j < tests.get(i).steps.size(); ++j) {
                        fr.write("<li>" + tests.get(i).steps.get(j) + "</li>");
                    }
                }
                fr.write("</tr>");
                fr1.write("<br>" + tests.get(i).testDesc + "</td> </tr>");
            }
            fr1.write("</table>");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //close resources
            try {
                fr.close();
                fr1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void runWaPoTests() {
        log.info("Base dir for this test run is: " + BASE_DIR);
        log.info("WaPo URL for this test run is: " + wapoConfig.crosswordLink);
        tests.add(new TesterPM(wapoConfig, browserName));
        tests.get(tests.size() - 1).isPublishedWaPo();
        tests.add(new TesterPM(wapoConfig, browserName));
        tests.get(tests.size() - 1).verifyEmbed(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkAuthorNameWaPo(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).noMessageOnFinish(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).isTicking(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).isTimerPaused(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).isTimerResumed(true);
        tests.add(new TesterPM(wapoConfig, browserName));
        tests.get(tests.size() - 1).checkAbout();
        tests.add(new TesterPM(wapoConfig, browserName));
        tests.get(tests.size() - 1).isRebusEnabled();
        tests.add(new TesterPM(wapoConfig, browserName));
        tests.get(tests.size() - 1).checkSettingsOnRefresh();
    }

    public void runNDTests() {
        log.info("Newsday URL for this test run is: " + newsdayConfig.crosswordLink);
        tests.add(new TesterPM(newsdayConfig, browserName));
        tests.get(tests.size() - 1).isPublishedNewsDay();
        tests.add(new TesterPM(newsdayConfig, browserName));
        tests.get(tests.size() - 1).verifyEmbed(true);
        tests.add(new TesterPM(newsdayConfig, browserName));
        tests.get(tests.size() - 1).checkAuthorNameND(true);
        tests.add(new TesterPM(newsdayConfig, browserName));
        tests.get(tests.size() - 1).isRebusNotEnabled();
    }

    public void runPickerTests() {
        log.info("Picker URL for this test run is: " + wapoConfig.crosswordLink);
        tests.add(new TesterPM(wapoConfig, browserName));
        tests.get(tests.size() - 1).isPickerLoading();
        tests.add(new TesterPM(wapoConfig, browserName));
        tests.get(tests.size() - 1).isAdDisplayed();
        tests.add(new TesterPM(wapoConfig, browserName));
        tests.get(tests.size() - 1).checkNoCompletionStatusCookiesDisabled();
        tests.add(new TesterPM(wapoConfig, browserName));
        tests.get(tests.size() - 1).checkNoCompletionStatus();
        tests.add(new TesterPM(wapoConfig, browserName));
        tests.get(tests.size() - 1).checkCompletionStatus1();
        tests.add(new TesterPM(wapoConfig, browserName));
        tests.get(tests.size() - 1).checkCompletionStatus50();
        tests.add(new TesterPM(wapoConfig, browserName));
        tests.get(tests.size() - 1).checkCompletionStatus99();
        tests.add(new TesterPM(wapoConfig, browserName));
        tests.get(tests.size() - 1).checkCompletionStatus100Correct(true);
        tests.add(new TesterPM(wapoConfig, browserName));
        tests.get(tests.size() - 1).checkCompletionStatus100Incorrect();
    }

    public void runPlayerInitTests() {
        tests.add(new TesterPM(defaultConfig, browserName));
        StepDefsPM browser = tests.get(tests.size() - 1).start();
        tests.get(tests.size() - 1).isInitialized(false, false);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).browser = browser;
        tests.get(tests.size() - 1).checkClueNumbers(false, false);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).browser = browser;
        tests.get(tests.size() - 1).isPowered(false, true);
    }

    public void runPlayerTests() {
        runNavBarTests();
        runCookiesTests();
        runSettingsTests();
        runErrorCheckModeTests();
        runCheckTests();
        runRevealTests();
        runKeysTests();
        runKeysTests();
        runRebusTests();
        runCircledLettersTests();
        runPrintTests();
    }

    public void runNavBarTests() {
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkReset(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkInfo(true);
    }

    public void runCookiesTests() {
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkRefresh(true);
    }

    public void runSettingsTests() {
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkSettings(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkStayInCurrentClue(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkMoveToNextClue(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkToggleBetweenAcrossAndDown(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkSkipOverFilled(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkUseSpaceToClear(true);
    }

    public void runErrorCheckModeTests() {
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkErrorCheckModeCorrect(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkErrorCheckModeIncorrect(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkErrorCheckModeCorrectIncorrect(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).isClueGrey(true);
    }

    public void runCheckTests() {
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkCurrentLetter(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkLetterColor(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkCurrentWord(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkWordColor(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkEntireGrid(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkColorAll(true);
    }

    public void runRevealTests() {
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkRevealCurrentLetter(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkRevealCurrentWord(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkRevealEntireGrid(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkUpperCase(true);
    }

    public void runKeysTests() {
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkDeleteKey(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkNavigation(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkNavigationWithIncorrect(true);
    }

    public void runRebusTests() {
        log.info("Rebus URL for this test run is: " + rebusConfig.crosswordLink);
        tests.add(new TesterPM(rebusConfig, browserName));
        tests.get(tests.size() - 1).checkRebusOnResize(true);
        tests.add(new TesterPM(rebusConfig, browserName));
        tests.get(tests.size() - 1).checkEnterRebusMode(true);
        tests.add(new TesterPM(rebusConfig, browserName));
        tests.get(tests.size() - 1).checkExitRebusMode(true);
        tests.add(new TesterPM(rebusConfig, browserName));
        tests.get(tests.size() - 1).checkRebusLetters(true);
        tests.add(new TesterPM(rebusConfig, browserName));
        tests.get(tests.size() - 1).checkRebusPrint(true);
        tests.add(new TesterPM(rebusConfig, browserName));
        tests.get(tests.size() - 1).checkRebusPrintSolution(true);
        tests.add(new TesterPM(rebusConfig, browserName));
        tests.get(tests.size() - 1).checkRebusHelp(true);
        tests.add(new TesterPM(rebusConfig, browserName));
        tests.get(tests.size() - 1).checkRebusButton(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkNoRebusHelp(true);
    }

    public void runCircledLettersTests() {
        log.info("Circled letter URL for this test run is: " + circledLetterConfig.crosswordLink);
        tests.add(new TesterPM(circledLetterConfig, browserName));
        tests.get(tests.size() - 1).checkCircledLettersPrint(true);
        tests.add(new TesterPM(circledLetterConfig, browserName));
        tests.get(tests.size() - 1).checkRebusCircledLettersPrint(true);
        tests.add(new TesterPM(circledLetterConfig, browserName));
        tests.get(tests.size() - 1).checkCircledLetters();
    }

    public void runPrintTests() {
        log.info("Print URL for this test run is: " + defaultConfig.crosswordLink);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkPrintOptions(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkPrintBlank(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkPrintFilled(true);
        tests.add(new TesterPM(defaultConfig, browserName));
        tests.get(tests.size() - 1).checkPrintSolution(true);
    }

    public void runTests() {
        for (int i = 0; i < options.size(); ++i) {
            String op = options.get(i);
            switch (op) {
                case "-a": {
                    runWaPoTests();
                    runNDTests();
                    runPickerTests();
                    runPlayerInitTests();
                    runPlayerTests();
                    break;
                }
                case "-wapo": {
                    runWaPoTests();
                    break;
                }
                case "-nd": {
                    runNDTests();
                    break;
                }
                case "-picker": {
                    runPickerTests();
                    break;
                }
                case "-init": {
                    runPlayerInitTests();
                    break;
                }
                case "-player": {
                    runPlayerTests();
                    break;
                }
                case "-navbar": {
                    runNavBarTests();
                    break;
                }
                case "-cookies": {
                    runCookiesTests();
                    break;
                }
                case "-settings": {
                    runSettingsTests();
                    break;
                }
                case "-error": {
                    runErrorCheckModeTests();
                    break;
                }
                case "-check": {
                    runCheckTests();
                    break;
                }
                case "-reveal": {
                    runRevealTests();
                    break;
                }
                case "-rebus": {
                    runRebusTests();
                    break;
                }
                case "-keys": {
                    runKeysTests();
                    break;
                }
                case "circle": {
                    runCircledLettersTests();
                    break;
                }
                case "-print": {
                    runPrintTests();
                    break;
                }
                default:
                    System.out.println("Option not recognized. Type -help for available options.");

            }
        }
        for (int i = 0; i < tests.size(); ++i) {
            if (tests.get(i).passed == 1)
                ++nPassed;
            else if (tests.get(i).passed == 0) ++nFailed;
            else ++nUndetermined;
        }

    }
}
