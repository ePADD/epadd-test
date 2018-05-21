package com.amuselabs.test;


import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.amuselabs.test.CSSSelector.*;
import static com.amuselabs.test.LinkText.puzzleMe;
import static com.amuselabs.test.TestMain.mobile;

/*import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;*/

/**
 * Created by hangal on 11/29/16.
 */
public class Tester {

    private static Log log = LogFactory.getLog(Tester.class);
    public String BASE_URL;
    String puzzleTitle;
    StepDefs browser;
    String browserName;
    String id;
    String error;
    int passed; //0 for failed, 1 for passed, 2 for undetermined
    ArrayList<String> screenshotPath;
    ArrayList<String> steps;
    TestConfig testConfig;
    String testDesc;

    public Tester(TestConfig t, String bName) {
        testConfig = t;
        BASE_URL = testConfig.crosswordLink;
        browserName = bName;
        passed = 1;
        steps = new ArrayList<>();
        screenshotPath = new ArrayList<>();
    }

    private static Options getOpt() {
        // create the Options
        // consider a local vs. global (hosted) switch. some settings will be disabled if its in global mode
        Options options = new Options();
        options.addOption("ai", "import", false, "check appraisal import");
        options.addOption("vap", "visit-all-pages", false, "visit all pages and check that they are alive (appraisal mode)");
        options.addOption("si", "set-images", false, "set archive images");
        options.addOption("t", "test", false, "self-test mode");

        options.addOption("b", "browse", false, "check browse (appraisal mode)");
        options.addOption("fl", "flags", false, "check flags (appraisal)");
        options.addOption("as", "adv-search", false, "check advanced search (appraisal mode)");
        options.addOption("f", "facets", false, "check facets (appraisal mode)");
        options.addOption("lex", "lexicon", false, "check lexicon");
        options.addOption("sens", "sensitive", false, "check sensitive messages");
        options.addOption("s", "settings", false, "check settings");
        options.addOption("ae", "export", false, "check expraisal export");
        options.addOption("pi", "processing import", false, "check import into processing");
        options.addOption("pe", "processing-export", false, "check export from processing");
        options.addOption("ds", "discovery", false, "check discovery module");
        options.addOption("dl", "delivery", false, "check delivery module");
        options.addOption("del", "delete-archive", false, "delete archive when done");

        //	options.addOption( "ns", "no-shutdown", false, "no auto shutdown");
        return options;
    }

    public void verifyWaPoPuzzleSeriesList() throws InterruptedException, IOException {

        browser.openURL("https://cdn1.amuselabs.com/wapo/wp-picker?set=wapo-daily&embed=1&limit=14&test=1");

        //        browser.openURL("https://www.washingtonpost.com/crossword-puzzles/daily/");
        // browser.waitFor("iframe[name='crossword']", 5); // wait for up to 20 seconds
        // browser.waitFor(2); // wait for 2 seconds
        // browser.switchToFrame("crossword"); // this is the name of the frame on the WaPo page
        // browser.maximizeWindow();

        browser.waitFor("#puzzles", 25); // wait for up to 25 seconds
        browser.waitFor(2); // wait for 2 seconds
        browser.verifyNumElements("#puzzles .tile", 14);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEEE, MMMMM d, yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String todaysDate = sdf.format(new Date());
        log.info(todaysDate);

        browser.verifyContains("#puzzles .tile .daily-tile-date", todaysDate);
        // browser.takeScreenshot("picker");
//        browser.driver.switchTo().defaultContent();
//        browser.switchToFrame("crossword"); // this is the name of the frame on the WaPo page
        String puzzleTitle = browser.getTextValueOf("#puzzles .tile");
        browser.clickOnCSS("#puzzles .tile");
        browser.waitFor(5);

        // opens in a new tab for some reason; switch to it
        browser.switchToNextTab();

        // verify the modal is shown, it contains "LA times" in the start-message field and has a start button
        browser.waitFor("#info-modal .start-message", 5);
        browser.verifyContains("#info-modal .start-message", "LA Times");
        browser.verifyContains("#footer-btn", "Start");
        browser.waitFor(2);
        browser.clickOn("button", "Start");

        // verify that the visual elements are present
        browser.verifyContains(".clock-time", "0:00"); // clock should not have started yet, it starts only when the first key is pressed

        // there must be 225 boxes on screen for a 15x15
        // browser.verifyNumElements (".box", 225);
        browser.verifyNumElementsGE(".cluenum-in-box", 10); // there must be at least 10 clue numbers in boxes
        browser.verifyNumElementsGE(".stop,.empty", 10); // there must be at least one stop or empty box
        browser.verifyContains(".crossword-footer-message", "LA Times");
        browser.verifyContains(".crossword-footer-message", "Powered by PuzzleMe");

        // verify clues
        {
            // verify across and down, 2 cluelists
            browser.verifyContains("#aclues .clue-header", "Across");
            browser.verifyContains("#dclues .clue-header", "Down");
            browser.verifyNumElements(".clue-list", 2);

            // there must be at least 10 clue numbers and 10 clues in the across and down clue lists
            browser.verifyNumElementsGE("#aclues .clueNum", 10);
            browser.verifyNumElementsGE("#aclues .clue", 10);
            browser.verifyNumElementsGE("#dclues .clueNum", 10);
            browser.verifyNumElementsGE("#dclues .clue", 10);
            // need to check that the clues are not empty...
            browser.checkNonEmpty("span.clueText");
        }

        // check the reveal menu
        {
            browser.verifyContains("li#hint-reveal", "Reveal");
            browser.clickOn("li", "Reveal");
            browser.waitFor("#reveal-letter-button", 5);
            browser.verifyContains("#reveal-letter-button", "Reveal current letter");
            browser.verifyContains("#reveal-word-button", "Reveal current word");
            browser.verifyContains("#answers-button", "Reveal entire grid");
            browser.clickOn("li", "Reveal"); // dismiss it
        }

        // check key input
        {
            browser.clickOnCSS(".letter");
            browser.sendKeysTo(".letter", "abc");
        }

        // check the check menu
        {
            browser.verifyContains("li#check", "check");
            browser.clickOn("li", "Check");
            browser.waitFor("#check-letter-button", 5);
            browser.verifyContains("#check-letter-button", "Check current letter");
            browser.verifyContains("#check-word-button", "Check current word");
            browser.verifyContains("#check-all-button", "Check entire grid");
            browser.clickOn("li", "Check"); // dismiss it
        }

        // check hamburger menu
        {
            browser.clickOnCSS("img.nav-icon");
            browser.verifyContains("li#puzzle-list", "All Puzzles");
            browser.verifyContains("li#options-help", "Help");
            browser.verifyContains("li.play-save", "Save");
            browser.verifyContains("li#options-clear", "Clear");
            browser.verifyContains("a#about", "About");
            browser.clickOnCSS("img.nav-icon");
        }

        {
            // check print and settings menu options
            browser.verifyContains("li#print-selection", "Print");
            browser.verifyContains("li#settings-selection", "Settings");
        }
        // browser.takeScreenshot(browser.getTextValueOf("title"));
        /*
        {
            browser.clickOn("li", "Reveal");
            browser.clickOnCSS("#answers-button");
            browser.clickOnCSS(".confirm-yes");
        }
*/
        //   browser.takeScreenshot("empty-crossword");
        browser.driver.switchTo().defaultContent();
    }


    // Helper methods
    public StepDefs start() {
        try {
            browser = new StepDefs();
            if (testConfig.cookiesDisabled)
                browser.openBrowserWithCookiesDisabled(browserName);
            else
                browser.openBrowser(browserName);
            browser.openURL(testConfig.crosswordLink);
            if (testConfig.embedded) {
                browser.waitFor("iframe[name='" + ifframeName + "']", 5); // wait for up to 20 seconds
                browser.waitFor(2); // wait for 2 seconds
                String URL = browser.driver.findElement(By.cssSelector("#iframe-xword")).getAttribute("src");
                browser.openURL(URL);
                if (mobile && testConfig.puzzleType == 0)
                    browser.clickOnCSS(playAdButtonMobile);
                browser.waitFor(pickerDiv, 60);
                browser.clickOnCSS(pickerDiv + " " + pickerListItem);
                browser.waitFor(2);
                browser.closeTab();
            }
            if (testConfig.startButtonPresent) {
                browser.waitFor(startButton, 15);
                browser.clickOnCSS(startButton);
            }
            puzzleTitle = browser.driver.getTitle();
            if (mobile) {
                openSettingsPopup();
                browser.clickOnCSS(stayCurrent);
                browser.clickOnCSS(applySettings);
            }

        } catch (Exception e) {
            log.error(e);
        }
        return browser;
    }

    public void clear() throws InterruptedException {
        browser.clickOnCSS(puzzleMenu);
        browser.clickOnCSS(clear);
        browser.switchToPopUpWindow();
        browser.clickOnCSS("button.btn.confirm-yes");
    }

    public void restoreDefaultSettings() throws InterruptedException {
        browser.clickOnCSS(settings);
        browser.switchToPopUpWindow();
        //Error check mode should be off
        try {
            browser.checkAttributeContains(errorCheckDiv + " > img", "src", sliderOffImg, 0);
        } catch (Exception e) {
            ;
            browser.clickOnCSS(errorCheckDiv + " > img");
        }
        //Timer should be enabled
        try {
            browser.checkAttributeContains(timerdiv + " > img", "src", sliderOnImg, 0);
        } catch (Exception e) {
            ;
            browser.clickOnCSS(timerdiv + " > img");
        }
        //Skip over filled letters should be selected
        try {
            browser.isSelected(skipSquares);
        } catch (Exception e) {
            browser.clickOnCSS(skipSquares);
        }
        //At the end of clue, stay in current clue should be selected
        browser.clickOnCSS(stayCurrent);
        //Use space key to toggle across and down
        browser.clickOnCSS("#space-arrow");
        browser.clickOnCSS(applySettings);
    }

    public void openPuzzleMenu() throws InterruptedException {
        if (mobile)
            browser.clickOnCSS(puzzleMenuMobile);
        else
            browser.clickOnCSS(puzzleMenu);
    }

    public void openSettingsPopup() throws InterruptedException {
        if (mobile) {
            openPuzzleMenu();
            browser.clickOnCSS(settings);
        } else {
            browser.clickOnCSS(settings);
            browser.switchToPopUpWindow();
        }
    }

    public void openPrintPopup() throws InterruptedException {
        if (mobile) {
            openPuzzleMenu();
            browser.clickOnCSS(print);
        } else {
            browser.clickOnCSS(print);
            browser.switchToPopUpWindow();
        }
    }

    private void toggleTimer() throws IOException, InterruptedException {
        openSettingsPopup();
        browser.clickOnCSS(showTimerDiv + " > img");
        browser.clickOnCSS(applySettings);
    }

    private void isErrorCheckModeOff() throws IOException, InterruptedException {
        openSettingsPopup();
        browser.checkAttributeContains(errorCheckDiv + " > img", "src", sliderOffImg, 0);
        browser.clickOnCSS(applySettings);
    }

    private void toggleErrorCheckMode() throws IOException, InterruptedException {
        openSettingsPopup();
        browser.clickOnCSS(errorCheckDiv + " > img");
        browser.clickOnCSS(applySettings);
    }

    private void enterCorrectWord() throws IOException, InterruptedException {
        browser.clickOnCSS(letterBox);
        browser.sendKeysTo(letterBox, testConfig.firstWordAcross);
    }

    private void enterIncorrectWord() throws IOException, InterruptedException {
        //SPECIFICATION: adds an incorrect word starting at the first box in the grid
        // Assumes only English alphabets and digits are valid characters for answers
        browser.clickOnCSS(letterBox);
        //make it incorrect
        char ch = testConfig.firstWordAcross.charAt(0);
        if (ch >= 65 && ch <= 90) //alphabet
            ch = (char) (65 + (((ch - 65) + 1) % 26));
        else
            ch = (char) (48 + (((ch - 48) + 1) % 10)); //digit
        browser.sendKeysTo(letterBox, ch + testConfig.firstWordAcross.substring(1));
    }

    private void checkClueGrey() throws InterruptedException {
        //checks if either the first across clue or the first down clue has become grey
        browser.checkAttributeContains(clueList + " div[direction=\"across\"]", "class", greyClue, 0);
    }

    private void checkClueGreen() throws InterruptedException {
        browser.checkAttributeContains(clueList + " div[direction=\"across\"]", "class", greenClue, 0);
    }

    private void checkClueRed() throws InterruptedException {
        browser.checkAttributeContains(clueList + " div[direction=\"across\"]", "class", redClue, 0);
    }

    private void checkIncorrectLettersRed() throws InterruptedException {
        browser.clickOnCSS(letterBox);
        browser.checkAttributeContains(".crossword > div.box.letter", "class", errorLetter, 0);
    }

    private void reveal(String selector) throws InterruptedException {
        browser.clickOnCSS(revealdiv);
        browser.waitFor(selector, 5);
        browser.clickOnCSS(selector);
    }

    private void handleError(Exception e) {

        error = e.toString();
        passed = 0;
    }

    private void onFinish() {
        try {
            screenshotPath.add(browser.takeScreenshot(""));
            if (puzzleTitle == null)
                puzzleTitle = browser.driver.getTitle();
            browser.quitBrowser();
        } catch (Exception e1) {
            log.error(e1);
        }
    }

    private void onFinish(boolean end) {
        try {
            screenshotPath.add(browser.takeScreenshot(""));
            if (puzzleTitle == null)
                puzzleTitle = browser.driver.getTitle();
            if (end)
                browser.quitBrowser();
        } catch (Exception e1) {
            log.error(e1);
        }
    }

    private void enterAnotherWordIncorrect() throws InterruptedException {
        browser.clickOnCSS(letterBox, testConfig.firstWordAcross.length());
        char ch = testConfig.secondWordAcross.charAt(0);
        if (ch >= 65 && ch <= 90) //alphabet
            ch = (char) (65 + (((ch - 65) + 1) % 26));
        else
            ch = (char) (48 + (((ch - 48) + 1) % 10)); //digit
        browser.sendKeysTo(input, ch + testConfig.secondWordAcross.substring(1));
    }

    private void checkCurrent(String selector) throws InterruptedException {
        if (mobile) {
            browser.clickOnCSS(revealdiv);
        } else
            browser.clickOnCSS(checkdiv);
        browser.waitFor(checkLetter, 5);
        browser.clickOnCSS(selector);
    }

    private String getWord() {
        List<WebElement> letters = browser.driver.findElements(By.cssSelector(letter));

        String word = "";
        // Now using Iterator we will iterate all elements
        Iterator<WebElement> iter = letters.iterator();
        // this will check whether list has some element or not
        while (iter.hasNext()) {

            // Iterate one by one
            WebElement item = iter.next();
            // get the text
            String label = item.getText();
            if (label.isEmpty() || label.equals(" ")) {
                break;
            }
            word += label;


        }
        log.info("Word is " + word);
        return word;
    }

    private int nextWordIndex() {
        //returns the index of the starting letter of the second word wrt letter sequence
        List<WebElement> letters = browser.driver.findElements(By.cssSelector(".box"));

        int index = 0;
        // Now using Iterator we will iterate all elements
        Iterator<WebElement> iter = letters.iterator();
        // this will check whether list has some element or not
        while (iter.hasNext()) {
            // Iterate one by one
            WebElement item = iter.next();
            if (item.getAttribute("class").contains("empty")) {
                break;
            }
            ++index;
        }
        log.info("Next word index:" + index);
        return index;
    }

    private int getColumns() {
        List<WebElement> boxes = browser.driver.findElements(By.cssSelector(".crossword > div"));
        Iterator<WebElement> iter = boxes.iterator();
        int nboxes = 0;
        while (iter.hasNext()) {
            WebElement item = iter.next();
            if (item.getAttribute("class").contains("endRow"))
                break;
            ++nboxes;
        }
        log.info("Number of columns:" + nboxes);
        return nboxes;
    }

    private void checkTicking() throws InterruptedException {
        int startTime = Integer.parseInt(browser.getTextValueOf(clock).substring(browser.getTextValueOf(clock).indexOf(":") + 1));
        long start = System.currentTimeMillis();
        openSettingsPopup();
        browser.clickOnCSS(applySettings);
        long elapsedTimeMillis = System.currentTimeMillis() - start;
        float elapsedTimeSec = elapsedTimeMillis / 1000F;
        int time = Integer.parseInt(browser.getTextValueOf(clock).substring(browser.getTextValueOf(clock).indexOf(":") + 1));
        if (!(Math.abs(elapsedTimeSec + startTime - time) <= 1))
            throw new RuntimeException("Timer is not ticking.");
    }

    /*
    public boolean verifyPDFContent(String strURL, String reqTextInPDF) {

        boolean flag = false;

        PDFTextStripper pdfStripper = null;
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;
        String parsedText = null;

        try {
            URL url = new URL(strURL);
            BufferedInputStream file = new BufferedInputStream(url.openStream());
            PDFParser parser = new PDFParser(file);

            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(1);

            pdDoc = new PDDocument(cosDoc);
            parsedText = pdfStripper.getText(pdDoc);
        } catch (MalformedURLException e2) {
           log.error("URL string could not be parsed "+e2.getMessage());
        } catch (IOException e) {
            log.error("Unable to open PDF Parser. " + e.getMessage());
            try {
                if (cosDoc != null)
                    cosDoc.close();
                if (pdDoc != null)
                    pdDoc.close();
            } catch (Exception e1) {
                e.printStackTrace();
            }
        }

        log.info("+++++++++++++++++");
        log.info(parsedText);
        log.info("+++++++++++++++++");

        if(!parsedText.contains(reqTextInPDF)) {
            throw new RuntimeException("Required text:"+reqTextInPDF+" is not present in PDF.");
        }

        return flag;
    }
    */


    //Test Cases in order of id
    //Test Case: 1.2.1: Verifies that puzzles are published everyday
    //Program should be run everyday at 10pm EST/EDT  for Newsday
    public void isPublishedNewsDay() {
        id = "1.2.1";
        testDesc = "Test Case: 1.2.1: Verifies that puzzles are published everyday";
        try {
            //BASE_URL = "https://www.newsday.com/entertainment/extras/crossword-puzzle-1.6375288";
            steps.add("Opening puzzle list.");
            browser = new StepDefs();
            browser.openBrowser(browserName);
            browser.openURL(BASE_URL);
            steps.add("Switching to crossword frame.");
            browser.waitFor("iframe[name='" + ifframeName + "']", 5); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            browser.switchToFrame(ifframeName); // this is the name of the frame on the WaPo page
            if (!mobile)
                browser.maximizeWindow();
            browser.waitFor(pickerDiv, 60);
            steps.add("Verifying that the crosswords for the past 14 days are displayed.");
            browser.verifyNumElements(pickerListItem, 14);
            SimpleDateFormat sdf = new SimpleDateFormat("d MMMMM yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            String todaysDate = sdf.format(new Date());
            log.info(todaysDate);
            steps.add("Verifying today's crossword is present.");
            browser.verifyContains("a.puzzle-link strong", todaysDate);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 1.2.2: Verifies that puzzles are published everyday
    //Program should be run everyday at midnight EST/EDT for  WaPo
    public void isPublishedWaPo() {
        id = "1.2.2";
        testDesc = "Test Case: 1.2.2: Verifies that puzzles are published everyday";
        try {
            //BASE_URL = "https://www.washingtonpost.com/crossword-puzzles/daily/";
            steps.add("Opening puzzle list.");
            browser = new StepDefs();
            browser.openBrowser(browserName);
            browser.openURL(BASE_URL);
            steps.add("Switching to crossword frame.");
            browser.waitFor("iframe[name='" + ifframeName + "']", 5); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            browser.switchToFrame(ifframeName); // this is the name of the frame on the WaPo page
            if (!mobile)
                browser.maximizeWindow();
            if (mobile)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 60);
            steps.add("Verifying that the crosswords for the past 14 days are displayed.");
            browser.verifyNumElements(pickerDiv + " > " + pickerListItem, 14);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEEE, MMMMM d, yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            String todaysDate = sdf.format(new Date());
            log.info(todaysDate);
            steps.add("Verifying today's crossword is present.");
            browser.verifyContains(pickerDiv + " " + pickerListItem + " " + dateTile, todaysDate);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 2.1.1: Verifies that the picker page does not show the puzzle completion status if 3rd party cookies are not enabled
    public void checkNoCompletionStatusCookiesDisabled() {
        id = "2.1.1";
        testDesc = "Test Case: 2.1.1: Verifies that the picker page does not show the puzzle completion status if 3rd party cookies are not enabled";
        try {
            steps.add("Play 5-10% of a few WaPo puzzles");
            //Play 5-10% of a few WaPo puzzles
            BASE_URL = "https://www.washingtonpost.com/crossword-puzzles/daily/";
            steps.add("Opening puzzle list.");
            browser = new StepDefs();
            browser.openBrowserWithCookiesDisabled(browserName);
            browser.openURL(BASE_URL);
            steps.add("Switching to crossword frame.");
            browser.waitFor("iframe[name='" + ifframeName + "']", 5); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            browser.switchToFrame(ifframeName); // this is the name of the frame on the WaPo page
            if (!mobile)
                browser.maximizeWindow();
            if (mobile)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 60);
            browser.clickOnCSS(pickerListItem);
            browser.clickOnCSS(startButton);
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, "AAA");
            steps.add("Disable 3rd party cookies");
            //Disable 3rd party cookies
            //browser.driver.manage().deleteAllCookies();
            steps.add("Go to the WaPo picker page");
            //Go to the WaPo picker page
            browser.driver.navigate().back();
            steps.add("The puzzle completion status is not shown, since 3rd party cookies are disabled");
            //The puzzle completion status is not shown, since 3rd party cookies are disabled
            browser.waitFor("iframe[name='" + ifframeName + "']", 20); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            browser.switchToFrame(ifframeName); // this is the name of the frame on the WaPo page
            browser.maximizeWindow();
            browser.waitFor(pickerDiv, 60);
            browser.isElementNotPresent("div.tile-progress");
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }

    }

    //Test Case: 2.2.1 : Check that the puzzle picker and the puzzle grid load even if the incognito/ privacy mode is on and 3rd party cookies/data are blocked.
    public void isPickerLoading() {
        id = "2.2.1";
        testDesc = "Test Case: 2.2.1 : Check that the puzzle picker and the puzzle grid load even if the incognito/ privacy mode is on and 3rd party cookies/data are blocked.";
        try {
            //Selenium by dafult starts in incognito/private mode : https://stackoverflow.com/questions/27425116/python-start-firefox-with-selenium-in-private-mode
            steps.add("Opening puzzle list.");
            browser = new StepDefs();
            browser.openBrowserWithCookiesDisabled(browserName);
            browser.openURL(BASE_URL);
            steps.add("Switching to crossword frame.");
            browser.waitFor("iframe[name='" + ifframeName + "']", 5); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            browser.switchToFrame(ifframeName); // this is the name of the frame on the WaPo page
            if (!mobile)
                browser.maximizeWindow();
            if (mobile && testConfig.puzzleType == 0)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 25);
            steps.add("Verifying that the crosswords for the past 14 days are displayed.");
            browser.verifyNumElements(pickerDiv + "  " + pickerListItem, 14);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 2.3.1: Verifies that even if 3rd party cookies are blocked, pre-roll ad should still be displayed
    public void isAdDisplayed() {
        id = "2.3.1";
        testDesc = "Test Case: 2.3.1: Verifies that even if 3rd party cookies are blocked, pre-roll ad should still be displayed";
        try {
            steps.add("Disable 3rd party cookies. Go to the WaPo picker page");
            browser = new StepDefs();
            browser.openBrowserWithCookiesDisabled(browserName);
            browser.openURL(BASE_URL);
            steps.add("Switching to crossword frame.");
            browser.waitFor("iframe[name='" + ifframeName + "']", 5); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            browser.switchToFrame(ifframeName); // this is the name of the frame on the WaPo page
            browser.maximizeWindow();
            steps.add("The pre-roll ad should still be shown");
            browser.waitFor(ad, 5);
            browser.isVisible(ad);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 4.2.1 : Checks if error check mode is working properly for correct answers
    public void checkErrorCheckModeCorrect(boolean start) {
        id = "4.2.1";
        testDesc = "Test Case: 4.2.1 : Checks if error check mode is working properly for correct answers";
        try {
            steps.add("Open a WaPo puzzle.\n");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Verify that the “Error Check Mode” option is off in the Settings popup.");
            //Verify that the “Error Check Mode” option is off in the Settings popup
            isErrorCheckModeOff();
            steps.add("Enter a few correct answers in the grid.");
            //Enter a few correct answers in the grid.
            enterCorrectWord();
            steps.add("Verify that completely filled in clues are marked as grey.");
            //Verify that completely filled in clues are marked as grey.
            checkClueGrey();
            steps.add("Now turn Error check mode on via Settings popup.");
            //Now turn “Error check mode” on via Settings popup.
            toggleErrorCheckMode();
            steps.add("Verify that clues with correct words in the grid are marked in green.");
            //Verify that clues with correct words in the grid are marked in green.
            checkClueGreen();
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 4.2.2 : Checks if error check mode is working properly for incorrect answers.
    public void checkErrorCheckModeIncorrect(boolean start) {
        id = "4.2.2";
        testDesc = "Test Case: 4.2.2 : Checks if error check mode is working properly for incorrect answers.";
        try {
            steps.add("Open a WaPo puzzle.");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Verify that the “Error Check Mode” option is off in the Settings popup");
            //Verify that the “Error Check Mode” option is off in the Settings popup
            isErrorCheckModeOff();
            steps.add("Enter a few incorrect answers in the grid.");
            //Enter a few incorrect answers in the grid.
            enterIncorrectWord();
            steps.add("Verify that completely filled in clues are marked as grey.");
            //Verify that completely filled in clues are marked as grey.
            checkClueGrey();
            steps.add("Now turn Error check mode on via Settings popup.");
            //Now turn “Error check mode” on via Settings popup.
            toggleErrorCheckMode();
            steps.add("Verify that clues with incorrect letters in the grid are marked in red.");
            //Verify that clues with incorrect letters in the grid are marked in red.
            checkClueRed();
            steps.add("Verify incorrect letters in the grid are marked red as well.");
            //Verify incorrect letters in the grid are marked red as well.
            checkIncorrectLettersRed();
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 4.2.3 : Checks if error check mode is working properly for correct and incorrect answers
    public void checkErrorCheckModeCorrectIncorrect(boolean start) {
        id = "4.2.3";
        testDesc = "Test Case: 4.2.3 : Checks if error check mode is working properly for correct and incorrect answers";
        try {
            steps.add("Open a WaPo puzzle");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Turn the “Error Check Mode” option on in the Settings popup");
            //Turn the “Error Check Mode” option on in the Settings popup
            toggleErrorCheckMode();
            steps.add("Enter a few correct answers in the grid.");
            //Enter a few correct answers in the grid.
            enterCorrectWord();
            steps.add("Enter a few incorrect answers in the grid.");
            //Enter a few incorrect answers in the grid.
            enterAnotherWordIncorrect();
            steps.add("Verify that clues with correct words in the grid are marked in green.");
            //Verify that clues with correct words in the grid are marked in green.
            checkClueGreen();
            steps.add("Clues with incorrect letters in the grid are marked in red.");
            // Clues with incorrect letters in the grid are marked in red.
            browser.checkAttributeContains("div[direction=\"across\"]", "class", redClue, 1);
            steps.add("Incorrect letters in the grid are marked red as well.");
            // Incorrect letters in the grid are marked red as well.);
            browser.checkAttributeContains(".crossword > div.box.letter", "class", errorLetter, testConfig.firstWordAcross.length());
            steps.add("Now turn “Error check mode” off via Settings popup");
            //Now turn “Error check mode” off via Settings popup
            toggleErrorCheckMode();
            steps.add("Verify that completely filled in clues are marked as grey.");
            //Verify that completely filled in clues are marked as grey.
            browser.checkAttributeContains("div[direction=\"across\"]", "class", greyClue, 0);
            browser.checkAttributeContains("div[direction=\"across\"]", "class", greyClue, 1);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case 4.3.1: Verify if check current letter feature is working
    public void checkCurrentLetter(boolean start) {
        id = "4.3.1";
        testDesc = "Test Case 4.3.1: Verify if check current letter feature is working";
        try {
            steps.add("Open a WaPo puzzle");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Verify that the “Error Check Mode” option is off in the Settings popup");
            //Verify that the “Error Check Mode” option is off in the Settings popup
            isErrorCheckModeOff();
            steps.add("Enter a few correct letters in the grid.");
            //Enter a few correct letters in the grid.
            enterCorrectWord();
            steps.add("Use the “Check current letter” option to check one of the entered letters.");
            //Use the “Check current letter” option to check one of the entered letters.
            browser.clickOnCSS(letterBox);
            checkCurrent(checkLetter);
            steps.add("If the letter is incorrect, it is colored red, else it is left as it is");
            //If the letter is incorrect, it is colored red, else it is left as it is
            browser.checkAttributeDoesNotContain(".crossword > div.box.letter", "class", wrongLetter, 0);
            browser.checkAttributeDoesNotContain(".crossword > div.box.letter", "class", errorLetter, 0);
            steps.add("now enter incorrect letter");
            //now enter incorrect letter
            browser.clickOnCSS(letterBox);
            char ch = browser.getTextValueOf(".letter-in-box").toUpperCase().charAt(0);
            if (ch >= 65 && ch <= 90) //alphabet
                ch = (char) (65 + (((ch - 65) + 1) % 26));
            else
                ch = (char) (48 + (((ch - 48) + 1) % 10));
            browser.sendKeysTo(".letter", "" + ch);
            steps.add("move focus back to current letter");
            //move focus back to current letter
            browser.clickOnCSS(".letter");
            steps.add("Use the “Check current letter” option to check one of the entered letters.");
            //Use the “Check current letter” option to check one of the entered letters.
            checkCurrent(checkLetter);
            browser.checkAttributeContains(".crossword > div.box.letter", "class", wrongLetter, 0);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case 4.3.2: Verifies that turning off Error Check Mode does not alter letters colored by check current letter option
    public void checkLetterColor(boolean start) {
        id = "4.3.2";
        testDesc = "Test Case 4.3.2: Verifies that turning off Error Check Mode does not alter letters colored by check current letter option";
        try {
            steps.add("Open a WaPo puzzle");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Verify that the “Error Check Mode” option is off in the Settings popup");
            //Verify that the “Error Check Mode” option is off in the Settings popup
            isErrorCheckModeOff();
            steps.add("Enter a few incorrect and correct letters in the grid.");
            //Enter a few incorrect and correct letters in the grid.
            enterIncorrectWord();
            enterAnotherWordIncorrect();
            steps.add("Use the “Check current letter” option to check one of the incorrectly entered letters.");
            //Use the “Check current letter” option to check one of the incorrectly entered letters.
            browser.clickOnCSS(letterBox);
            checkCurrent(checkLetter);
            steps.add("Since the letter is incorrect, it is colored red.");
            // Since the letter is incorrect, it is colored red.
            browser.checkAttributeContains(".crossword > div.box.letter", "class", wrongLetter, 0);
            steps.add("Now turn “Error check mode” on via Settings popup.");
            // Now turn “Error check mode” on via Settings popup.
            toggleErrorCheckMode();
            steps.add("Verify that clues with incorrect letters in the grid are marked red.");
            // Verify that clues with incorrect letters in the grid are marked red.
            browser.checkAttributeContains(clueList + " div[direction=\"across\"]", "class", redClue, 0);
            browser.checkAttributeContains(clueList + " div[direction=\"across\"]", "class", redClue, 1);
            steps.add("Incorrect letters in the grid are marked red as well.");
            // Incorrect letters in the grid are marked red as well.
            browser.checkAttributeContains(".crossword > div.box.letter", "class", errorLetter, 0);
            browser.checkAttributeContains(".crossword > div.box.letter", "class", errorLetter, testConfig.firstWordAcross.length()); //last div is endRow
            steps.add("Now turn “Error check mode” off via Settings popup");
            // Now turn “Error check mode” off via Settings popup
            toggleErrorCheckMode();
            steps.add("Verify that all letters in the grid should be in black except for those marked as red by the “Check current letter” option used earlier.");
            // Verify that all letters in the grid should be in black except for those marked as red by the “Check current letter” option used earlier.
            browser.checkAttributeContains(".crossword > div.box.letter", "class", wrongLetter, 0);
            browser.checkAttributeDoesNotContain(".crossword > div.box.letter", "class", errorLetter, testConfig.firstWordAcross.length());
            browser.checkAttributeDoesNotContain(".crossword > div.box.letter", "class", wrongLetter, testConfig.firstWordAcross.length());
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case 4.4.1: Verifies that the check current word feature is working
    public void checkCurrentWord(boolean start) {
        id = "4.4.1";
        testDesc = "Test Case 4.4.1: Verifies that the check current word feature is working";
        try {
            steps.add("Open a WaPo puzzle");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Verify that the “Error Check Mode” option is off in the Settings popup");
            //Verify that the “Error Check Mode” option is off in the Settings popup
            isErrorCheckModeOff();
            steps.add("Enter a few incorrect and correct words in the grid.");
            //Enter a few incorrect and correct words in the grid.
            enterCorrectWord();
            steps.add("Verify that completely filled in clues are marked as grey.");
            //Verify that completely filled in clues are marked as grey.
            browser.checkAttributeContains(".aclues div[direction=\"across\"]", "class", greyClue, 0);
            steps.add("Use the “Check current word” option to check one of the entered words.");
            //Use the “Check current word” option to check one of the entered words.
            browser.clickOnCSS(letterBox);
            checkCurrent(checkWord);
            steps.add("Word is Correct, so left as it is");
            //Word is Correct, so left as it is
            for (int i = 0; i < testConfig.firstWordAcross.length(); ++i) {
                browser.checkAttributeDoesNotContain(".crossword > div.box.letter", "class", wrongLetter, i);
                browser.checkAttributeDoesNotContain(".crossword > div.box.letter", "class", errorLetter, i);
            }
            steps.add("Verify that completely filled in clues are marked as grey.");
            //Verify that completely filled in clues are marked as grey.
            checkClueGrey();
            steps.add("make word incorrect");
            //make word incorrect
            browser.clickOnCSS(letterBox);
            browser.clickOnCSS(letterBox);
            char ch = browser.getTextValueOf(".letter-in-box").toUpperCase().charAt(0);
            if (ch >= 65 && ch <= 90) //alphabet
                ch = (char) (65 + (((ch - 65) + 1) % 26));
            else
                ch = (char) (48 + (((ch - 48) + 1) % 10));
            browser.sendKeysTo(letterBox, "" + ch);
            steps.add("Use the “Check current word” option to check one of the entered words.");
            //Use the “Check current word” option to check one of the entered words.
            browser.clickOnCSS(letterBox);
            checkCurrent(checkWord);
            steps.add("Word is incorrect, it is colored red");
            //Word is incorrect, it is colored red
            browser.checkAttributeContains(".crossword > div.box.letter", "class", wrongLetter, 0);
            steps.add("Verify that completely filled in clues are marked as grey.");
            //Verify that completely filled in clues are marked as grey.
            checkClueGrey();
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case 4.4.2: Checks if Error Check Mode does not override check current word mode
    public void checkWordColor(boolean start) {
        id = "4.4.2";
        testDesc = "Test Case 4.4.2: Checks if Error Check Mode does not override check current word mode";
        try {
            steps.add("Open any puzzle");
            //Open any puzzle
            if (start)
                start();
            steps.add("Verify that the “Error Check Mode” option is off in the Settings popup");
            //Verify that the “Error Check Mode” option is off in the Settings popup
            isErrorCheckModeOff();
            steps.add("Enter a few incorrect and correct words in the grid.");
            //Enter a few incorrect and correct words in the grid.
            enterIncorrectWord();
            enterAnotherWordIncorrect();
            steps.add("Verify that completely filled in clues are marked as grey.");
            //Verify that completely filled in clues are marked as grey.
            checkClueGrey();
            browser.checkAttributeContains(".aclues div[direction=\"across\"]", "class", greyClue, 1);
            steps.add("Use the “Check current word” option to check one of the incorrectly entered words.");
            //Use the “Check current word” option to check one of the incorrectly entered words.
            browser.clickOnCSS(letterBox);
            checkCurrent(checkWord);
            steps.add("Since the word is incorrect, it is colored red.");
            //Since the word is incorrect, it is colored red.
            browser.checkAttributeContains(".crossword > div.box.letter", "class", wrongLetter, 0);
            steps.add("Now turn “Error check mode” on via Settings popup.");
            //Now turn “Error check mode” on via Settings popup.
            toggleErrorCheckMode();
            steps.add("Verify that clues with incorrect letters in the grid are marked in red.");
            //Verify that clues with incorrect letters in the grid are marked in red.
            browser.checkAttributeContains(".aclues div[direction=\"across\"]", "class", redClue, 0);
            browser.checkAttributeContains(".aclues div[direction=\"across\"]", "class", redClue, 1);
            steps.add("Incorrect letters in the grid are marked red as well.");
            // Incorrect letters in the grid are marked red as well.
            browser.checkAttributeContains(".crossword > div.box.letter", "class", errorLetter, 0);
            browser.checkAttributeContains(".crossword > div.box.letter", "class", errorLetter, testConfig.firstWordAcross.length()); //last div is endRow
            steps.add("Now turn “Error check mode” off via Settings popup");
            //Now turn “Error check mode” off via Settings popup
            toggleErrorCheckMode();
            steps.add("Verify that completely filled in clues are marked as grey.");
            //Verify that completely filled in clues are marked as grey.
            checkClueGrey();
            browser.checkAttributeContains(".aclues div[direction=\"across\"]", "class", greyClue, 1);
            steps.add("All letters in the grid should be in black except for those marked as red by the “Check current word” option used earlier.");
            // All letters in the grid should be in black except for those marked as red by the “Check current word” option used earlier.
            browser.checkAttributeContains(".crossword > div.box.letter", "class", wrongLetter, 0);
            for (int i = 1; i < (testConfig.firstWordAcross.length() + testConfig.secondWordAcross.length()); ++i) {
                browser.checkAttributeDoesNotContain(".crossword > div.box.letter", "class", errorLetter, i);
                browser.checkAttributeDoesNotContain(".crossword > div.box.letter", "class", wrongLetter, i);
            }
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case 4.5.1: Verifies that the check entire grid feature is working
    public void checkEntireGrid(boolean start) {
        id = "4.5.1";
        testDesc = "Test Case 4.5.1: Verifies that the check entire grid feature is working";
        try {
            steps.add("Open any puzzle");
            //Open any puzzle
            if (start)
                start();
            steps.add("Verify that the “Error Check Mode” option is off in the Settings popup");
            //Verify that the “Error Check Mode” option is off in the Settings popup
            isErrorCheckModeOff();
            steps.add("Enter a few incorrect and correct words in the grid.");
            //Enter a few incorrect and correct words in the grid.
            enterAnotherWordIncorrect();
            enterCorrectWord();
            steps.add("Use the “Check entire grid” option to check the entire grid.");
            //Use the “Check entire grid” option to check the entire grid.
            checkCurrent(checkAll);
            steps.add("If any letter is incorrect, it is colored red, else it is left as it is");
            //If any letter is incorrect, it is colored red, else it is left as it is
            browser.checkAttributeContains(".crossword > div.box.letter", "class", wrongLetter, testConfig.firstWordAcross.length());
            for (int i = 0; i < (testConfig.firstWordAcross.length() + testConfig.secondWordAcross.length()); ++i) {
                if (i == testConfig.firstWordAcross.length())
                    continue;
                browser.checkAttributeDoesNotContain(".crossword > div.box.letter", "class", errorLetter, i);
                browser.checkAttributeDoesNotContain(".crossword > div.box.letter", "class", wrongLetter, i);
            }
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case 4.5.2: Checks if Error Check Mode does not override check entire grid option
    public void checkColorAll(boolean start) {
        id = "4.5.2";
        testDesc = "Test Case 4.5.2: Checks if Error Check Mode does not override check entire grid option";
        try {
            steps.add("Open any puzzle");
            //Open any puzzle
            if (start)
                start();
            steps.add("Verify that the “Error Check Mode” option is off in the Settings popup");
            //Verify that the “Error Check Mode” option is off in the Settings popup
            isErrorCheckModeOff();
            steps.add("Enter a few incorrect and correct words in the grid.");
            //Enter a few incorrect and correct words in the grid.
            enterCorrectWord();
            enterAnotherWordIncorrect();
            steps.add("Verify that completely filled in clues are marked as grey.");
            //Verify that completely filled in clues are marked as grey.
            browser.checkAttributeContains(".aclues div[direction=\"across\"]", "class", greyClue, 1);
            checkClueGrey();
            steps.add("Use the “Check entire grid” option to check the entire grid.");
            //Use the “Check entire grid” option to check the entire grid.
            checkCurrent(checkAll);
            steps.add("Incorrect letters are marked as red in the grid.");
            //Incorrect letters are marked as red in the grid.
            browser.checkAttributeContains(".crossword > div.box.letter", "class", wrongLetter, testConfig.firstWordAcross.length());
            for (int i = 0; i < (testConfig.firstWordAcross.length() + testConfig.secondWordAcross.length()); ++i) {
                if (i == testConfig.firstWordAcross.length())
                    continue;
                browser.checkAttributeDoesNotContain(".crossword > div.box.letter", "class", errorLetter, i);
                browser.checkAttributeDoesNotContain(".crossword > div.box.letter", "class", wrongLetter, i);
            }
            steps.add("Now turn “Error check mode” on via Settings popup.");
            //Now turn “Error check mode” on via Settings popup.
            toggleErrorCheckMode();
            steps.add("Verify that clues with correct words in the grid are marked in green.");
            //Verify that clues with correct words in the grid are marked in green.
            browser.checkAttributeContains("div[direction=\"across\"]", "class", greenClue, 0);
            steps.add("Clues with incorrect letters in the grid are marked in red.");
            // Clues with incorrect letters in the grid are marked in red.
            browser.checkAttributeContains("div[direction=\"across\"]", "class", redClue, 1);
            steps.add("Incorrect letters in the grid are marked red as well.");
            // Incorrect letters in the grid are marked red as well.
            browser.checkAttributeContains(".crossword > div.box.letter", "class", wrongLetter, testConfig.firstWordAcross.length());
            steps.add("Now turn “Error check mode” off via Settings popup");
            //Now turn “Error check mode” off via Settings popup
            toggleErrorCheckMode();
            steps.add("Verify that completely filled in clues are marked as grey.");
            //Verify that completely filled in clues are marked as grey.
            checkClueGrey();
            browser.checkAttributeContains(".aclues div[direction=\"across\"]", "class", greyClue, 1);
            steps.add("All letters in the grid should be in black except for those marked as red by the “Check entire grid” option used earlier.");
            // All letters in the grid should be in black except for those marked as red by the “Check entire grid” option used earlier.
            browser.checkAttributeContains(".crossword > div.box.letter", "class", wrongLetter, testConfig.firstWordAcross.length());
            for (int i = 0; i < (testConfig.firstWordAcross.length() + testConfig.secondWordAcross.length()); ++i) {
                if (i == testConfig.firstWordAcross.length())
                    continue;
                browser.checkAttributeDoesNotContain(".crossword > div.box.letter", "class", errorLetter, i);
                browser.checkAttributeDoesNotContain(".crossword > div.box.letter", "class", wrongLetter, i);
            }

        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case 4.6.3: Verifies that no "Hmmm... you're close. Incorrect letters are marked in red." message is shown and incorrect letters are not marked red in the grid.
    public void noMessageOnFinish(boolean start) {
        id = "4.6.3";
        testDesc = "Test Case 4.6.3: Verifies that no \"Hmmm... you're close. Incorrect letters are marked in red.\" message is shown and incorrect letters are not marked red in the grid.";
        try {
            steps.add("Open a WaPo puzzle");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Verify that the “Error Check Mode” option is off in the Settings popup");
            //Verify that the “Error Check Mode” option is off in the Settings popup
            isErrorCheckModeOff();
            openSettingsPopup();
            browser.clickOnCSS(moveToNext);
            browser.clickOnCSS(applySettings);
            steps.add("Complete the grid by entering letters till the end with a few incorrect letters/words in the grid");
            //Complete the grid by entering letters till the end with a few incorrect letters/words in the grid
            browser.clickOnCSS(".letter");
            for (int i = 0; i < testConfig.entireGridSequence.length(); ++i) {
                char ch = testConfig.entireGridSequence.charAt(i);
                if (i == 0 || i == 5) {
                    if (ch >= 65 && ch <= 90) //alphabet
                        ch = (char) (65 + (((ch - 65) + 1) % 26));
                    else
                        ch = (char) (48 + (((ch - 48) + 1) % 10));
                }
                browser.sendKeysTo(input, ch + "");
            }
            steps.add("When the last remaining box is filled, \"Hmmm... you're close. Incorrect letters are marked in red.\" message is not shown and incorrect letters are not marked red in the grid");
            //When the last remaining box is filled, "Hmmm... you're close. Incorrect letters are marked in red." message is not shown and incorrect letters are not marked red in the grid
            browser.checkAttributeDoesNotContain("div.box.letter", "class", wrongLetter, 0);
            browser.checkAttributeDoesNotContain("div.box.letter", "class", errorLetter, 0);
            browser.checkAttributeDoesNotContain("div.box.letter", "class", wrongLetter, 5);
            browser.checkAttributeDoesNotContain("div.box.letter", "class", errorLetter, 5);
            browser.switchToPopUpWindow();
            browser.isElementNotPresent(".final-msg");
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case 4.11.1: Verifies that the reveal current letter is working
    public void checkRevealCurrentLetter(boolean start) {
        id = "4.11.1";
        testDesc = "Test Case 4.11.1: Verifies that the reveal current letter is working";
        try {
            steps.add("Open a WaPo puzzle");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Select a box in the grid");
            //Select a box in the grid
            browser.clickOnCSS(letterBox);
            steps.add("Click the Reveal Option in the navbar and select the Reveal current letter sub-option");
            //Click the Reveal Option in the navbar and select the “Reveal current letter” sub-option
            reveal(revealLetter);
            steps.add("The letter gets revealed in the selected box.");
            //The letter gets revealed in the selected box.
            log.info(browser.getTextValueOf(letter) + " " + testConfig.firstWordAcross.charAt(0));
            if (!browser.getTextValueOf(letter).equals(testConfig.firstWordAcross.charAt(0) + ""))
                throw new RuntimeException("Letter is not revealed");
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case 4.12.1: Verifies that the reveal current word is working
    public void checkRevealCurrentWord(boolean start) {
        id = "4.12.1";
        testDesc = "Test Case 4.12.1: Verifies that the reveal current word is working";
        try {
            steps.add("Open a WaPo puzzle");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Select a word in the grid");
            //Select a word in the grid
            browser.clickOnCSS(letterBox);
            steps.add("Click the Reveal Option in the navbar and select the “Reveal current word” sub-option");
            //Click the Reveal Option in the navbar and select the “Reveal current word” sub-option
            reveal(revealWord);
            steps.add("The selected word gets revealed in its entirety.");
            //The selected word gets revealed in its entirety.
            // Then the entire grid is revealed.
            List<WebElement> elements = browser.driver.findElements(By.cssSelector(letter));
            for (int i = 0; i < testConfig.firstWordAcross.length(); ++i) {
                if (!elements.get(i).getText().equals(testConfig.firstWordAcross.charAt(i) + ""))
                    throw new RuntimeException("Grid is not revealed correctly");
            }
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case 4.13.1: Verifies that the reveal entire grid is working
    public void checkRevealEntireGrid(boolean start) {
        id = "4.13.1";
        testDesc = "Test Case 4.13.1: Verifies that the reveal entire grid is working";
        try {
            steps.add("Open a WaPo puzzle");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Select a box in the grid");
            //Select a box in the grid
            browser.clickOnCSS(letterBox);
            steps.add("Click the Reveal Option in the navbar and select the “Reveal entire grid” sub-option");
            //Click the Reveal Option in the navbar and select the “Reveal entire grid” sub-option
            reveal(revealAll);
            steps.add("A popup comes up asking “Are you sure you want to reveal all answers?”");
            //A popup comes up asking “Are you sure you want to reveal all answers?”
            browser.switchToPopUpWindow();
            steps.add("Check that the focus is on the cancel button to avoid any inadvertent clicking of the OK button. Click the Cancel button.");
            //Check that the focus is on the cancel button to avoid any inadvertent clicking of the OK button. Click the Cancel button.
            browser.clickOnCSS(".btn.btn-primary.confirm-no");
            steps.add("Then the popup is closed and the grid remains as it is");
            //Then the popup is closed and the grid remains as it is.
            browser.checkNotFilled(letter);
            steps.add("Click the Reveal Option in the navbar and select the “Reveal entire grid” sub-option");
            //Click the Reveal Option in the navbar and select the “Reveal entire grid” sub-option
            reveal(revealAll);
            steps.add("Click OK button in the popup that comes up.");
            browser.switchToPopUpWindow();
            //Click OK button in the popup that comes up.
            browser.clickOnCSS(".btn.confirm-yes");
            steps.add("Then the entire grid is revealed.");
            // Then the entire grid is revealed.
            List<WebElement> elements = browser.driver.findElements(By.cssSelector(letter));
            for (int i = 0; i < testConfig.entireGridSequence.length(); ++i) {
                if (!elements.get(i).getText().equals(testConfig.entireGridSequence.charAt(i) + ""))
                    throw new RuntimeException("Grid is not revealed correctly");
            }
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case 6.1.1: Verifies that the delete key is functioning as required
    public void checkDeleteKey(boolean start) {
        id = "6.1.1";
        testDesc = "Test Case 6.1.1: Verifies that the delete key is functioning as required";
        try {
            steps.add("Open a WaPo puzzle on a desktop");
            //Open a WaPo puzzle on a desktop
            if (start)
                start();
            steps.add("Enter a letter in the grid and select that box.");
            //Enter a letter in the grid and select that box.
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, "a");
            browser.clickOnCSS(letterBox);
            steps.add("Press 'Delete/.' key.");
            //Press 'Delete/.' key in the number pad.
            browser.sendKeysTo(input, Keys.DELETE + "");
            steps.add("The letter in the selected box should be deleted and the focus remains there.");
            // The letter in the selected box should be deleted and the focus remains there.
            if (!browser.getTextValueOf(letter).isEmpty() && !browser.getTextValueOf(letter).equals(" "))
                throw new RuntimeException("Letter is not deleted");
            browser.checkAttributeContains("div.crossword > div.box.letter", "class", "hilited-box-with-focus", 0);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 6.1.4: Verifies that backtick enters into rebus mode
    public void checkEnterRebusMode(boolean start) {
        id = "6.1.4";
        passed = 2;
        testDesc = "Test Case: 6.1.4: Verifies that backtick enters into rebus mode";
        try {
            steps.add("Open a WaPo puzzle on a desktop");
            //Open a WaPo puzzle on a desktop
            if (start)
                start();
            steps.add("In a cell in the grid, press  ` (backtick).");
            //In a cell in the grid, press  ` (backtick).
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, "`");
            steps.add("Check that rebus mode is entered (a text entry box is shown)");
            //Check that rebus mode is entered (a text entry box is shown)
            browser.isElementPresent(rebusInput);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 6.1.5: Verifies that enter key exits rebus mode
    public void checkExitRebusMode(boolean start) {
        id = "6.1.5";
        testDesc = "Test Case: 6.1.5: Verifies that enter key exits rebus mode";
        try {
            steps.add("Open a WaPo puzzle on a desktop");
            //Open a WaPo puzzle on a desktop
            if (start)
                start();
            steps.add("In a cell in the grid, enter Rebus mode");
            //In a cell in the grid, enter Rebus mode
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, "`");
            steps.add("Now enter 2 letters in the rebus textbox and press enter or return");
            //Now enter 2 letters in the rebus textbox and press enter or return
            browser.sendKeysTo(letterBox, "AA" + Keys.ENTER);
            steps.add("Check that rebus mode is exited.");
            //Check that rebus mode is exited.
            browser.isElementNotPresent(rebusInput);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 6.2.3:  Verifies that letters are being entered in uppercase
    public void checkUpperCase(boolean start) {
        id = "6.2.3";
        testDesc = "Test Case: 6.2.3:  Verifies that letters are being entered in uppercase";
        try {
            steps.add("Enable 3rd party cookies in the browser");
            //Enable 3rd party cookies in the browser. Cookies will be saved manually.
            steps.add("Open a WaPo puzzle");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Enter a few letters in the grid - check that they are shown in uppercase");
            //Enter a few letters in the grid - check that they are shown in uppercase
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, "abc");
            String s = browser.getTextValueOf(letter);
            if (!s.toUpperCase().equals(s))
                throw new RuntimeException("Letters are not being entered as uppercase");
            log.info("Letters are being entered as uppercase");
            steps.add("After selecting a word in the grid, use “Reveal current word” option to reveal the entire word. The letters of the revealed word need to be in uppercase");
            browser.clickOnCSS(".letter");
            //After selecting a word in the grid, use “Reveal current word” option to reveal the entire word. The letters of the revealed word need to be in uppercase
            reveal(revealWord);
            String word = getWord();
            if (!word.toUpperCase().equals(word))
                throw new RuntimeException("Words are not being revealed as uppercase");
            log.info("Words are being revealed as uppercase.");
            steps.add("Now reload the puzzle in the browser - check that the previously entered letters in the grid are shown in uppercase");
            //Now reload the puzzle in the browser - check that the previously entered letters in the grid are shown in uppercase
            browser.refreshPage();
            String word1 = getWord();
            if (!word1.trim().equals(word))
                throw new RuntimeException("Words are not uppercase after page reload.");
            log.info("Words are uppercase after page reload.");
            //Before closing the browser, read the cookies
            Set allCookies = browser.driver.manage().getCookies();
            log.info("Number of cookie:" + allCookies.toString());
            steps.add("Close and reopen the browser and open the same puzzle again - check that the previously entered letters in the grid are shown in uppercase");
            //Close and reopen the browser and open the same puzzle again - check that the previously entered letters in the grid are shown in uppercase
            String URL = browser.driver.getCurrentUrl();
            String domain = URL.substring(0, URL.lastIndexOf("/"));
            log.info(URL + " " + domain);
            browser.quitBrowser();
            browser.openBrowser(browserName);
            browser.openURL(URL);
            //restore all cookies from previous session
            Iterator<Cookie> iter = allCookies.iterator();
            while (iter.hasNext()) {

                // Iterate one by one
                Cookie cookie = iter.next();
                browser.driver.manage().addCookie(cookie);
            }
            allCookies = browser.driver.manage().getCookies();
            log.info("Number of cookie:" + allCookies.toString());
            browser.openURL(URL);
            allCookies = browser.driver.manage().getCookies();
            log.info("Number of cookie:" + allCookies.toString());
            word1 = getWord();
            if (!word1.equals(word))
                throw new RuntimeException("Words are not uppercase after reopening browser.");
            log.info("Words are uppercase after reopening browser.");
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 6.2.6:  Verifies that letters entered in rebus mode are legible
    public void checkRebusLetters(boolean start) {
        id = "6.2.6";
        testDesc = "Test Case: 6.2.6:  Verifies that letters entered in rebus mode are legible";
        passed = 2;
        try {
            steps.add("Open a WaPo Sunday-EB puzzle");
            //Open a WaPo Sunday-EB puzzle
            if (start)
                start();
            steps.add("Enter 3-4 letters in a box using the Rebus option");
            //Enter 3-4 letters in a box using the Rebus option
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, "`ABC" + Keys.ENTER);
            steps.add("Check that the letters are legible and not cut off, while entering into the textbox");
            //Check that the letters are legible and not cut off, while entering into the textbox
            steps.add("Check that the letters are legible and not cut off, while entering into the grid");
            //Check that the letters are legible and not cut off, while entering into the grid
            browser.verifyContains(letter, "ABC");
            if (mobile) {
                //need to scroll back to first letter
                ((JavascriptExecutor) browser.driver).executeScript("scroll(0, -250);");
            }
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    // Test Case: 6.3.02 : Verifies navigation keys are working properly.
    public void checkNavigation(boolean start) {
        id = "6.3.02";
        testDesc = "Test Case: 6.3.02 : Verifies navigation keys are working properly.";
        try {
            steps.add("Open a WaPo puzzle.");
            //Open a WaPo puzzle.
            if (start)
                start();
            steps.add("Check that in the Settings popup, in \"After entering a letter\" option, \"Skip over filled letters\" is selected");
            //Check that in the Settings popup, in "After entering a letter" option, "Skip over filled letters" is selected
            openSettingsPopup();
            browser.isSelected(skipSquares);
            browser.clickOnCSS(applySettings);
            steps.add("Complete the grid by revealing the grid.");
            // Complete the grid by revealing the grid.
            reveal(revealAll);
            browser.switchToPopUpWindow();
            //Click OK button in the popup that comes up.
            browser.clickOnCSS(".btn.confirm-yes");
            browser.switchToPopUpWindow();
            browser.clickOnCSS("button.close");
            int index = nextWordIndex();
            steps.add("Pressing the enter key in the completed grid should work -- the control goes to the end of the next word on pressing enter");
            //Pressing the enter key in the completed grid should work -- the control goes to the next word on pressing enter
            browser.clickOnCSS(".letter");
            browser.sendKeysTo(".dummy", Keys.ENTER + "");
            browser.checkAttributeContains(".letter", "class", "hilited-box-with-focus", index);
            steps.add("Pressing the tab key in the completed grid should work -- the control goes to the end of the next word on pressing tab");
            //Pressing the tab key in the completed grid should work -- the control goes to the next word on pressing tab
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(input, Keys.TAB + "");
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", index);
          /*  steps.add("Pressing the shift-enter keys in the completed grid should work -- the control goes to the next word on pressing shift-enter");
            //Pressing the shift-enter keys in the completed grid should work -- the control goes to the next word on pressing shift-enter
            browser.clickOnCSS(".letter");
            browser.sendKeysTo(".dummy", Keys.chord(Keys.SHIFT, Keys.ENTER));
            browser.checkAttributeContains(".letter", "class", "hilited-box-with-focus", index);
            steps.add("Pressing the shift-tab keys in the completed grid should work -- the control goes to the next word on pressing shift-tab");
            //Pressing the shift-tab keys in the completed grid should work -- the control goes to the next word on pressing shift-tab
            browser.clickOnCSS(".letter");
            browser.sendKeysTo(".dummy", Keys.chord(Keys.SHIFT, Keys.TAB));
            browser.checkAttributeContains(".letter", "class", "hilited-box-with-focus", index); */
            steps.add("Pressing the right arrow key in the completed grid should work -- the control goes to the next box on pressing the right arrow key");
            //Pressing the right arrow key in the completed grid should work -- the control goes to the next box on pressing the right arrow key
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(input, Keys.ARROW_RIGHT + "");
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", 1);
            steps.add("Pressing the left arrow key in the completed grid should work -- the control goes to the previous box on pressing the left arrow key");
            //Pressing the left arrow key in the completed grid should work -- the control goes to the previous box on pressing the left arrow key
            browser.sendKeysTo(input, Keys.ARROW_LEFT + "");
            browser.checkAttributeContains(".letter", "class", "hilited-box-with-focus", 0);
            steps.add("Pressing the down arrow key in the completed grid should work -- the control goes to the box below on pressing the down arrow key");
            //Pressing the down arrow key in the completed grid should work -- the control goes to the box below on pressing the down arrow key
            browser.sendKeysTo(input, Keys.ARROW_DOWN + "");
            browser.sendKeysTo(input, Keys.ARROW_DOWN + "");
            browser.checkAttributeContains("div.box", "class", "hilited-box-with-focus", 0 + getColumns());
            steps.add("Pressing the up arrow key in the completed grid should work -- the control goes to the box above on pressing the up arrow key");
            //Pressing the up arrow key in the completed grid should work -- the control goes to the box above on pressing the up arrow key
            browser.sendKeysTo(input, Keys.ARROW_UP + "");
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", 0);
            //Clear the grid.
            steps.add("Clear the grid.");
            openPuzzleMenu();
            browser.clickOnCSS(clear);
            browser.switchToPopUpWindow();
            browser.clickOnCSS("button.btn.confirm-yes");
            browser.clickOnCSS(letterBox);
            //Deselect  "Skip over filled letters" in "After entering a letter" option in the Settings popup
            steps.add("Deselect  \"Skip over filled letters\" in \"After entering a letter\" option in the Settings popup");
            openSettingsPopup();
            browser.clickOnCSS(skipSquares);
            browser.clickOnCSS(applySettings);
            //Complete the grid by revealing the grid.
            steps.add("Complete the grid by revealing the grid.");
            reveal(revealAll);
            browser.switchToPopUpWindow();
            //Click OK button in the popup that comes up.
            browser.clickOnCSS(".btn.confirm-yes");
            browser.switchToPopUpWindow();
            browser.clickOnCSS("button.close");
            steps.add("Pressing the enter key in the completed grid should work -- the control goes to the end of the next word on pressing enter");
            //Pressing the enter key in the completed grid should work -- the control goes to the end of the next word on pressing enter
            browser.clickOnCSS(".letter");
            browser.sendKeysTo(".dummy", Keys.ENTER + "");
            browser.checkAttributeContains(".letter", "class", "hilited-box-with-focus", index);
            steps.add("Pressing the tab key in the completed grid should work -- the control goes to the end of the next word on pressing tab");
            //Pressing the tab key in the completed grid should work -- the control goes to the end of the next word on pressing tab
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(input, Keys.TAB + "");
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", index);
            steps.add("Pressing the right arrow key in the completed grid should work -- the control goes to the next box on pressing the right arrow key");
            //Pressing the right arrow key in the completed grid should work -- the control goes to the next box on pressing the right arrow key
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(input, Keys.ARROW_RIGHT + "");
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", 1);
            steps.add("Pressing the left arrow key in the completed grid should work -- the control goes to the previous box on pressing the left arrow key");
            //Pressing the left arrow key in the completed grid should work -- the control goes to the previous box on pressing the left arrow key
            browser.sendKeysTo(input, Keys.ARROW_LEFT + "");
            browser.checkAttributeContains(".letter", "class", "hilited-box-with-focus", 0);
            steps.add("Pressing the down arrow key in the completed grid should work -- the control goes to the box below on pressing the down arrow key");
            //Pressing the down arrow key in the completed grid should work -- the control goes to the box below on pressing the down arrow key
            browser.sendKeysTo(input, Keys.ARROW_DOWN + "");
            browser.sendKeysTo(input, Keys.ARROW_DOWN + "");
            browser.checkAttributeContains("div.box", "class", "hilited-box-with-focus", 0 + getColumns());
            steps.add("Pressing the up arrow key in the completed grid should work -- the control goes to the box above on pressing the up arrow key");
            //Pressing the up arrow key in the completed grid should work -- the control goes to the box above on pressing the up arrow key
            browser.sendKeysTo(input, Keys.ARROW_UP + "");
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", 0);

        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 6.3.03 : Verifies Navigation buttons are working properly with incorrect characters.
    public void checkNavigationWithIncorrect(boolean start) {
        id = "6.3.03";
        testDesc = "Test Case: 6.3.03 : Verifies Navigation buttons are working properly with incorrect characters.";
        try {
            steps.add("Open a WaPo puzzle.");
            //Open a WaPo puzzle.
            if (start)
                start();
            steps.add("Check that in the Settings popup, in \"After entering a letter\" option, \"Skip over filled letters\" is selected");
            //Check that in the Settings popup, in "After entering a letter" option, "Skip over filled letters" is selected
            openSettingsPopup();
            browser.isSelected(skipSquares);
            browser.clickOnCSS(applySettings);
            steps.add("Enter a few incorrect letters in the first 2-3 boxes of the grid. Then fill in the rest of the grid correctly.");
            //Enter a few incorrect letters in the first 2-3 boxes of the grid. Then fill in the rest of the grid correctly.
            openSettingsPopup();
            browser.clickOnCSS(moveToNext);
            browser.clickOnCSS(applySettings);
            browser.clickOnCSS(letterBox);
            for (int i = 0; i < testConfig.entireGridSequence.length(); ++i) {
                char ch = testConfig.entireGridSequence.charAt(i);
                if (i == 0 || i == 1 || i == 2) {
                    if (ch >= 65 && ch <= 90) //alphabet
                        ch = (char) (65 + (((ch - 65) + 1) % 26));
                    else
                        ch = (char) (48 + (((ch - 48) + 1) % 10));
                }
                browser.sendKeysTo(input, ch + "");
            }
            int index = nextWordIndex();
            steps.add("Pressing the enter key in the filled grid should work -- the control goes to the next word on pressing enter");
            //Pressing the enter key in the filled grid should work -- the control goes to the next word on pressing enter
            if (mobile) {
                //need to scroll back to first letter
                ((JavascriptExecutor) browser.driver).executeScript("scroll(0, -250);");
            }
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(input, Keys.ENTER + "");
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", index + testConfig.secondWordAcross.length() - 1);
            steps.add("Pressing the tab key in the filled grid should work -- the control goes to the next word on pressing tab");
            //Pressing the tab key in the filled grid should work -- the control goes to the next word on pressing tab
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(input, Keys.TAB + "");
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", index + testConfig.secondWordAcross.length() - 1);
            steps.add("Pressing the right arrow key in the filled grid should work -- the control goes to the next box on pressing the right arrow key");
            //Pressing the right arrow key in the filled grid should work -- the control goes to the next box on pressing the right arrow key
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(input, Keys.ARROW_RIGHT + "");
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", 1);
            steps.add("Pressing the left arrow key in the filled grid should work -- the control goes to the previous box on pressing the left arrow key");
            //Pressing the left arrow key in the filled grid should work -- the control goes to the previous box on pressing the left arrow key
            browser.sendKeysTo(input, Keys.ARROW_LEFT + "");
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", 0);
            steps.add("Pressing the down arrow key in the filled grid should work -- the control goes to the box below on pressing the down arrow key");
            //Pressing the down arrow key in the filled grid should work -- the control goes to the box below on pressing the down arrow key
            browser.sendKeysTo(input, Keys.ARROW_DOWN + "");
            browser.sendKeysTo(input, Keys.ARROW_DOWN + "");
            browser.checkAttributeContains("div.box", "class", "hilited-box-with-focus", 0 + getColumns());
            steps.add("Pressing the up arrow key in the filled grid should work -- the control goes to the box above on pressing the up arrow key");
            //Pressing the up arrow key in the filled grid should work -- the control goes to the box above on pressing the up arrow key
            browser.sendKeysTo(input, Keys.ARROW_UP + "");
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", 0);
            steps.add("Clear the grid.");
            //Clear the grid.
            if (mobile) {
                //need to scroll back to first letter
                ((JavascriptExecutor) browser.driver).executeScript("scroll(0, -250);");
            }
            openPuzzleMenu();
            browser.clickOnCSS(clear);
            browser.switchToPopUpWindow();
            browser.clickOnCSS("button.btn.confirm-yes");
            steps.add("Deselect  \"Skip over filled letters\" in \"After entering a letter\" option in the Settings popup");
            //Deselect  "Skip over filled letters" in "After entering a letter" option in the Settings popup
            openSettingsPopup();
            browser.clickOnCSS(skipSquares);
            browser.clickOnCSS(applySettings);
            steps.add("Enter a few incorrect letters in the first 2-3 boxes of the grid");
            //Enter a few incorrect letters in the first 2-3 boxes of the grid
            steps.add("Then fill in the rest of the grid correctly.");
            //Then fill in the rest of the grid correctly.
            openSettingsPopup();
            browser.clickOnCSS(moveToNext);
            browser.clickOnCSS(applySettings);
            browser.clickOnCSS(letterBox);
            for (int i = 0; i < testConfig.entireGridSequence.length(); ++i) {
                char ch = testConfig.entireGridSequence.charAt(i);
                if (i == 0 || i == 1 || i == 2) {
                    if (ch >= 65 && ch <= 90) //alphabet
                        ch = (char) (65 + (((ch - 65) + 1) % 26));
                    else
                        ch = (char) (48 + (((ch - 48) + 1) % 10));
                }
                browser.sendKeysTo(input, ch + "");
            }
            if (mobile) {
                //need to scroll back to first letter
                ((JavascriptExecutor) browser.driver).executeScript("scroll(0, -250);");
            }
            steps.add("Pressing the enter key in the filled grid should work -- the control goes to the end of the next word on pressing enter");
            //Pressing the enter key in the filled grid should work -- the control goes to the end of the next word on pressing enter
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(input, Keys.ENTER + "");
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", index + testConfig.secondWordAcross.length() - 1);
            steps.add("Pressing the tab key in the filled grid should work -- the control goes to the end of the next word on pressing tab");
            //Pressing the tab key in the filled grid should work -- the control goes to the end of the next word on pressing tab
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(input, Keys.TAB + "");
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", index + testConfig.secondWordAcross.length() - 1);
            steps.add("Pressing the right arrow key in the filled grid should work -- the control goes to the next box on pressing the right arrow key");
            //Pressing the right arrow key in the filled grid should work -- the control goes to the next box on pressing the right arrow key
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(input, Keys.ARROW_RIGHT + "");
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", 1);
            steps.add("Pressing the left arrow key in the filled grid should work -- the control goes to the previous box on pressing the left arrow key");
            //Pressing the left arrow key in the filled grid should work -- the control goes to the previous box on pressing the left arrow key
            browser.sendKeysTo(input, Keys.ARROW_LEFT + "");
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", 0);
            steps.add("Pressing the down arrow key in the filled grid should work -- the control goes to the box below on pressing the down arrow key");
            //Pressing the down arrow key in the filled grid should work -- the control goes to the box below on pressing the down arrow key
            browser.sendKeysTo(input, Keys.ARROW_DOWN + "");
            browser.sendKeysTo(input, Keys.ARROW_DOWN + "");
            browser.checkAttributeContains("div.box", "class", "hilited-box-with-focus", 0 + getColumns());
            steps.add("Pressing the up arrow key in the filled grid should work -- the control goes to the box above on pressing the up arrow key");
            //Pressing the up arrow key in the filled grid should work -- the control goes to the box above on pressing the up arrow key
            browser.sendKeysTo(input, Keys.ARROW_UP + "");
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", 0);

        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 7.1.1 : Verifies that no completion status is shown for unplayed puzzles
    public void checkNoCompletionStatus() {
        id = "7.1.1";
        testDesc = "Test Case: 7.1.1 : Verifies that no completion status is shown for unplayed puzzles";
        try {
            steps.add("Ensure that 3rd party cookies are enabled and browser cache has not been cleared");
            //Ensure that 3rd party cookies are enabled and browser cache has not been cleared
            steps.add("Go to the WaPo picker page");
            //Go to the WaPo picker page
            BASE_URL = "https://www.washingtonpost.com/crossword-puzzles/daily/";
            steps.add("Opening puzzle list.");
            browser = new StepDefs();
            browser.openBrowser(browserName);
            browser.openURL(BASE_URL);
            steps.add("Switching to crossword frame.");
            browser.waitFor("iframe[name='" + ifframeName + "']", 5); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            browser.switchToFrame(ifframeName); // this is the name of the frame on the WaPo page
            if (!mobile)
                browser.maximizeWindow();
            if (mobile && testConfig.puzzleType == 0)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 60);
            steps.add("For unplayed puzzles, completion status is not shown on the picker page");
            //For unplayed puzzles, completion status is not shown on the picker page
            browser.isElementNotPresent("div.tile-progress");
            steps.add("Open any unplayed puzzle");
            //Open any unplayed puzzle
            browser.clickOnCSS(pickerListItem);
            steps.add("Without entering anything into the grid, go back to the Picker page");
            //Without entering anything into the grid, go back to the Picker page
            browser.waitFor(startButton, 5);
            browser.clickOnCSS(startButton);
            if (browserName.equals("safari")) {
                browser.switchToPopUpWindow();
                browser.refreshPage();
            } else
                browser.driver.navigate().back();
            steps.add("The completion status for that puzzle should still be blank");
            //The completion status for that puzzle should still be blank
            browser.waitFor("iframe[name='" + ifframeName + "']", 20); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            browser.switchToFrame(ifframeName); // this is the name of the frame on the WaPo page
            if (!mobile)
                browser.maximizeWindow();
            if (mobile && testConfig.puzzleType == 0)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 60);
            browser.isElementNotPresent("div.tile-progress");
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }

    }

    //Test Case: 7.1.2: Checks that completion status is shown  for 1% filled puzzles
    public void checkCompletionStatus1() {
        id = "7.1.2";
        testDesc = "Test Case: 7.1.2: Checks that completion status is shown  for 1% filled puzzles";
        try {
            steps.add("Ensure that 3rd party cookies are enabled and browser cache has not been cleared");
            //Ensure that 3rd party cookies are enabled and browser cache has not been cleared
            steps.add("Go to the WaPo picker page");
            //Go to the WaPo picker page
            BASE_URL = "https://www.washingtonpost.com/crossword-puzzles/daily/";
            steps.add("Opening puzzle list.");
            browser = new StepDefs();
            browser.openBrowser(browserName);
            browser.openURL(BASE_URL);
            steps.add("Switching to crossword frame.");
            browser.waitFor("iframe[name='" + ifframeName + "']", 5); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            browser.switchToFrame(ifframeName); // this is the name of the frame on the WaPo page
            if (!mobile)
                browser.maximizeWindow();
            if (mobile && testConfig.puzzleType == 0)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 60);
            steps.add("Open any unplayed puzzle (completion status for that puzzle should be blank in the picker page)");
            //Open any unplayed puzzle (completion status for that puzzle should be blank in the picker page)
            browser.isElementNotPresent("div.tile-progress");
            browser.clickOnCSS(pickerListItem);
            steps.add("Enter 1-2 letters in the grid and save the puzzle");
            //Enter 1-2 letters in the grid and save the puzzle
            browser.waitFor(startButton, 15);
            browser.clickOnCSS(startButton);
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, "A");
            steps.add("Go back to the Picker page");
            //Go back to the Picker page
            if (browserName.equals("safari")) {
                browser.switchToPopUpWindow();
                browser.refreshPage();
            } else
                browser.driver.navigate().back();
            steps.add("The completion status for that puzzle should show “1% Answered”");
            //The completion status for that puzzle should show “1% Answered”
            browser.waitFor("iframe[name='" + ifframeName + "']", 20); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            browser.switchToFrame(ifframeName); // this is the name of the frame on the WaPo page
            if (!mobile)
                browser.maximizeWindow();
            if (mobile && testConfig.puzzleType == 0)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 60);
            browser.waitFor("div.tile-progress", 5);
            browser.verifyContains("div.tile-progress", "1% Answered");
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 7.1.3: Checks that completion status is shown for 50% filled puzzles
    public void checkCompletionStatus50() {
        id = "7.1.3";
        testDesc = "Test Case: 7.1.3: Checks that completion status is shown for 50% filled puzzles";
        try {
            steps.add("Ensure that 3rd party cookies are enabled and browser cache has not been cleared");
            //Ensure that 3rd party cookies are enabled and browser cache has not been cleared
            steps.add("Go to the WaPo picker page");
            // Go to the WaPo picker page
            BASE_URL = "https://www.washingtonpost.com/crossword-puzzles/daily/";
            steps.add("Opening puzzle list.");
            browser = new StepDefs();
            browser.openBrowser(browserName);
            browser.openURL(BASE_URL);
            steps.add("Switching to crossword frame.");
            browser.waitFor("iframe[name='" + ifframeName + "']", 5); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            browser.switchToFrame(ifframeName); // this is the name of the frame on the WaPo page
            if (!mobile)
                browser.maximizeWindow();
            if (mobile && testConfig.puzzleType == 0)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 60);
            steps.add("Open any unplayed puzzle (completion status for that puzzle should be blank in the picker page)");
            //Open any unplayed puzzle (completion status for that puzzle should be blank in the picker page)
            browser.isElementNotPresent("div.tile-progress");
            browser.clickOnCSS(pickerListItem);
            steps.add("Fill half of the grid and save the puzzle");
            //Fill half of the grid and save the puzzle
            browser.waitFor(startButton, 15);
            browser.clickOnCSS(startButton);
            browser.clickOnCSS(letterBox);
            int nLettersEntered = 0;
            boolean prevBlack = false;
            for (int i = 0; i < browser.driver.findElements(By.cssSelector("#crossword > div")).size(); ++i) {
                if (nLettersEntered == browser.driver.findElements(By.cssSelector(letterBox)).size() / 2)
                    break;
                try {
                    browser.checkAttributeContains("#crossword > div", "class", "letter", i);
                    browser.sendKeysTo(input, "A");
                    ++nLettersEntered;
                    prevBlack = false;
                } catch (Exception e) {
                    if (prevBlack)
                        continue;
                    browser.sendKeysTo(input, Keys.RIGHT + "");
                    prevBlack = true;
                }
            }
            steps.add("Go back to the Picker page");
            //Go back to the Picker page
            if (browserName.equals("safari")) {
                browser.switchToPopUpWindow();
                browser.refreshPage();
            } else
                browser.driver.navigate().back();
            steps.add("The completion status for that puzzle should show “50% Answered”");
            //The completion status for that puzzle should show “50% Answered”
            browser.waitFor("iframe[name='" + ifframeName + "']", 20); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            browser.switchToFrame(ifframeName); // this is the name of the frame on the WaPo page
            if (!mobile)
                browser.maximizeWindow();
            if (mobile && testConfig.puzzleType == 0)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 60);
            browser.verifyContains("div.tile-progress", "50% Answered");
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 7.1.4: Checks that completion status is shown for 99% filled puzzles
    public void checkCompletionStatus99() {
        id = "7.1.4";
        testDesc = "Test Case: 7.1.4: Checks that completion status is shown for 99% filled puzzles";
        try {
            steps.add("Ensure that 3rd party cookies are enabled and browser cache has not been cleared");
            //Ensure that 3rd party cookies are enabled and browser cache has not been cleared
            steps.add("Go to the WaPo picker page");
            //Go to the WaPo picker page
            BASE_URL = "https://www.washingtonpost.com/crossword-puzzles/daily/";
            steps.add("Opening puzzle list.");
            browser = new StepDefs();
            browser.openBrowser(browserName);
            browser.openURL(BASE_URL);
            steps.add("Switching to crossword frame.");
            browser.waitFor("iframe[name='" + ifframeName + "']", 5); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            browser.switchToFrame(ifframeName); // this is the name of the frame on the WaPo page
            if (!mobile)
                browser.maximizeWindow();
            if (mobile && testConfig.puzzleType == 0)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 60);
            steps.add("Open any unplayed puzzle (completion status for that puzzle should be blank in the picker page)");
            //Open any unplayed puzzle (completion status for that puzzle should be blank in the picker page)
            browser.isElementNotPresent("div.tile-progress");
            browser.clickOnCSS(pickerListItem);
            steps.add("Fill in most of the grid except for 1-2 squares and save the puzzle");
            //Fill in most of the grid except for 1-2 squares and save the puzzle
            browser.waitFor(startButton, 15);
            browser.clickOnCSS(startButton);
            browser.clickOnCSS(letterBox);
            int nLettersEntered = 0;
            boolean prevBlack = false;
            for (int i = 0; i < browser.driver.findElements(By.cssSelector("#crossword > div")).size(); ++i) {
                if (nLettersEntered == browser.driver.findElements(By.cssSelector(letterBox)).size() - 1)
                    break;
                try {
                    browser.checkAttributeContains("#crossword > div", "class", "letter", i);
                    browser.sendKeysTo(input, "A");
                    ++nLettersEntered;
                    prevBlack = false;
                } catch (Exception e) {
                    if (prevBlack)
                        continue;
                    browser.sendKeysTo(input, Keys.RIGHT + "");
                    prevBlack = true;
                }
            }
            steps.add("Go back to the Picker page");
            //Go back to the Picker page
            if (browserName.equals("safari")) {
                browser.switchToPopUpWindow();
                browser.refreshPage();
            } else
                browser.driver.navigate().back();
            steps.add("The completion status for that puzzle should show “99% Answered”");
            //The completion status for that puzzle should show “99% Answered”
            browser.waitFor("iframe[name='" + ifframeName + "']", 20); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            browser.switchToFrame(ifframeName); // this is the name of the frame on the WaPo page
            if (!mobile)
                browser.maximizeWindow();
            if (mobile && testConfig.puzzleType == 0)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 60);
            browser.verifyContains("div.tile-progress", "99% Answered");
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 7.1.5: Checks that completion status is shown for 100% filled puzzles with incorrect letters
    public void checkCompletionStatus100Incorrect() {
        id = "7.1.5";
        testDesc = "Test Case: 7.1.5: Checks that completion status is shown for 100% filled puzzles with incorrect letters";
        try {
            steps.add("Ensure that 3rd party cookies are enabled and browser cache has not been cleared");
            //Ensure that 3rd party cookies are enabled and browser cache has not been cleared
            steps.add("Go to the WaPo picker page");
            //Go to the WaPo picker page
            BASE_URL = "https://www.washingtonpost.com/crossword-puzzles/daily/";
            steps.add("Opening puzzle list.");
            browser = new StepDefs();
            browser.openBrowser(browserName);
            browser.openURL(BASE_URL);
            steps.add("Switching to crossword frame.");
            browser.waitFor("iframe[name='" + ifframeName + "']", 5); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            browser.switchToFrame(ifframeName); // this is the name of the frame on the WaPo page
            if (!mobile)
                browser.maximizeWindow();
            if (mobile && testConfig.puzzleType == 0)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 60);
            steps.add("Open any unplayed puzzle (completion status for that puzzle should be blank in the picker page)");
            //Open any unplayed puzzle (completion status for that puzzle should be blank in the picker page)
            browser.isElementNotPresent("div.tile-progress");
            browser.clickOnCSS(pickerListItem);
            steps.add("Fill in the grid till the last word and enter the last word incorrectly (say BBBB) and save the puzzle");
            //Fill in the grid till the last word and enter the last word incorrectly (say BBBB) and save the puzzle
            browser.waitFor(startButton, 15);
            browser.clickOnCSS(startButton);
            browser.clickOnCSS(letterBox);
            int nLettersEntered = 0;
            boolean prevBlack = false;
            for (int i = 0; i < browser.driver.findElements(By.cssSelector("#crossword > div")).size(); ++i) {
                if (nLettersEntered == browser.driver.findElements(By.cssSelector(letterBox)).size())
                    break;
                try {
                    browser.checkAttributeContains("#crossword > div", "class", "letter", i);
                    browser.sendKeysTo(input, "A");
                    ++nLettersEntered;
                    prevBlack = false;
                } catch (Exception e) {
                    if (prevBlack)
                        continue;
                    browser.sendKeysTo(input, Keys.RIGHT + "");
                    prevBlack = true;
                }
            }
            steps.add("Go back to the Picker page");
            //Go back to the Picker page
            if (browserName.equals("safari")) {
                browser.switchToPopUpWindow();
                browser.refreshPage();
            } else
                browser.driver.navigate().back();
            steps.add("The completion status for that puzzle should show “100% Answered”");
            //The completion status for that puzzle should show “100% Answered”
            browser.waitFor("iframe[name='" + ifframeName + "']", 20); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            browser.switchToFrame(ifframeName); // this is the name of the frame on the WaPo page
            if (!mobile)
                browser.maximizeWindow();
            if (mobile && testConfig.puzzleType == 0)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 60);
            browser.verifyContains("div.tile-progress", "100% Answered");
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 7.1.6: Checks that completion status 100% is shown with green tick for correctly solved puzzles
    public void checkCompletionStatus100Correct(boolean start) {
        id = "7.1.6";
        testDesc = "Test Case: 7.1.6: Checks that completion status 100% is shown with green tick for correctly solved puzzles";
        try {
            steps.add("Ensure that 3rd party cookies are enabled and browser cache has not been cleared");
            //Ensure that 3rd party cookies are enabled and browser cache has not been cleared
            steps.add("Go to the WaPo picker page.Open any unplayed puzzle (completion status for that puzzle should be blank in the picker page)");
            //Go to the WaPo picker page. Open any unplayed puzzle (completion status for that puzzle should be blank in the picker page)
            BASE_URL = "https://www.washingtonpost.com/crossword-puzzles/daily/";
            if (start)
                start();
            steps.add("Fill in the grid correctly");
            //Fill in the grid correctly
            browser.clickOnCSS(letterBox);
            int nLettersEntered = 0;
            boolean prevBlack = false;
            for (int i = 0; i < browser.driver.findElements(By.cssSelector("#crossword > div")).size(); ++i) {
                if (nLettersEntered == browser.driver.findElements(By.cssSelector(letterBox)).size())
                    break;
                try {
                    browser.checkAttributeContains("#crossword > div", "class", "letter", i);
                    browser.sendKeysTo(input, testConfig.entireGridSequence.charAt(nLettersEntered) + "");
                    ++nLettersEntered;
                    prevBlack = false;
                } catch (Exception e) {
                    if (prevBlack)
                        continue;
                    browser.sendKeysTo(input, Keys.RIGHT + "");
                    prevBlack = true;
                }
            }
            steps.add("Click “All Puzzles” on the End message popup");
            //Click “All Puzzles” on the End message popup
            browser.switchToPopUpWindow();
            browser.clickOnCSS("button#next-action-btn");
            steps.add("On the Picker page, the completion status for that puzzle should show “Completed” (with a green tick image next to it) and the completion date below it");
            //On the Picker page, the completion status for that puzzle should show “Completed” (with a green tick image next to it) and the completion date below it
            browser.waitFor(pickerDiv, 60);
            browser.isElementPresent(".tile-completed");
            browser.isElementPresent(".tile-completed img");
            browser.verifyContains(".tile-completed-msg", "Completed");
            SimpleDateFormat sdf = new SimpleDateFormat("MMMMM d, yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            String todaysDate = sdf.format(new Date());
            browser.verifyContains(".tile-completion-date", todaysDate);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 8.1.1: Checks that print options are being shown properly
    public void checkPrintOptions(boolean start) {
        id = "8.1.1";
        testDesc = "Test Case: 8.1.1: Checks that print options are being shown properly";
        try {
            steps.add("Open a WaPo puzzle");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("In the empty grid, click the Print option");
            //In the empty grid, click the Print option
            browser.clickOnCSS(print);
            steps.add("The Print popup should show 2 options - “Blank puzzle” and “Solution”");
            //The Print popup should show 2 options - “Blank puzzle” and “Solution”
            browser.switchToPopUpWindow();
            browser.isVisible(blank_puzzle);
            browser.isVisible(print_solution);
            browser.isNotVisible(print_filled);
            steps.add("Close the Print popup");
            //Close the Print popup
            browser.clickOnCSS("#print-modal");
            steps.add("Enter a few letters in the grid and then click the Print option");
            //Enter a few letters in the grid and then click the Print option
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, "ABC");
            steps.add("The Print popup should show 3 options - “Blank puzzle”; “Puzzle with Filled Letters” and “Solution”");
            //The Print popup should show 3 options - “Blank puzzle”; “Puzzle with Filled Letters” and “Solution”
            browser.clickOnCSS(print);
            browser.switchToPopUpWindow();
            browser.isVisible(blank_puzzle);
            browser.isVisible(print_filled);
            browser.isVisible(print_solution);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 8.2.1: Checks that a blank puzzle is being printed properly
    public void checkPrintBlank(boolean start) {
        id = "8.2.1";
        passed = 2;
        testDesc = "Test Case: 8.2.1: Checks that a blank puzzle is being printed properly";
        try {
            steps.add("Open a WaPo puzzle");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Click the Print option and select the option “Blank puzzle”");
            //Click the Print option and select the option “Blank puzzle”
            browser.clickOnCSS(print);
            browser.switchToPopUpWindow();
            browser.clickOnCSS(blank_puzzle);
            browser.clickOnCSS(print_button);
            steps.add("In a new tab, the printable HTML of the blank grid along with the clue columns gets displayed and the print dialog gets shown as well. In Firefox browsers, the pdf gets downloaded automatically");
            //In a new tab, the printable HTML of the blank grid along with the clue columns gets displayed and the print dialog gets shown as well. In Firefox browsers, the pdf gets downloaded automatically
            browser.switchToNextTab();
            String getURL = browser.driver.getCurrentUrl();
            if (!(getURL.contains(".pdf"))) {
                screenshotPath.add(browser.takeScreenshot(""));
            } else {
                screenshotPath.add(browser.driver.getCurrentUrl());
            }
            steps.add("Check that the generated PDF is readable, all the clues are displayed correctly without getting overwritten or truncated and the title is also shown");
            //Check that the generated PDF is readable, all the clues are displayed correctly without getting overwritten or truncated and the title is also shown
            //taking screenshot for this, needs to be inspected manually
        } catch (Exception e) {
            handleError(e);
        } finally {
            browser.quitBrowser();
        }
    }

    //Test Case: 8.3.1: Checks that a filled puzzle is being printed properly
    public void checkPrintFilled(boolean start) {
        id = "8.3.1";
        passed = 2;
        testDesc = "Test Case: 8.3.1: Checks that a filled puzzle is being printed properly";
        try {
            steps.add("Open a WaPo puzzle");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Enter a few letters in the grid");
            //Enter a few letters in the grid
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, "ABC");
            steps.add("Click the Print option and select the option “Puzzle with Filled Letters”");
            //Click the Print option and select the option “Puzzle with Filled Letters”
            browser.clickOnCSS(print);
            browser.switchToPopUpWindow();
            browser.clickOnCSS(print_filled);
            browser.clickOnCSS(print_button);
            steps.add("In a new tab, the printable HTML of the blank grid along with the clue columns gets displayed and the print dialog gets shown as well. In Firefox browsers, the pdf gets downloaded automatically");
            //In a new tab, the printable HTML of the blank grid along with the clue columns gets displayed and the print dialog gets shown as well. In Firefox browsers, the pdf gets downloaded automatically
            browser.switchToNextTab();
            steps.add("Check that the generated PDF is readable, all the clues are displayed correctly without getting overwritten or truncated and the title is also shown");
            //Check that the generated PDF is readable, all the clues are displayed correctly without getting overwritten or truncated and the title is also shown
            //taking screenshot, needs to be inspected manually
            browser.waitFor(5);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 8.4.1: Checks that the solution puzzle is being printed properly
    public void checkPrintSolution(boolean start) {
        id = "8.4.1";
        testDesc = "Test Case: 8.4.1: Checks that the solution puzzle is being printed properly";
        passed = 2;
        try {
            steps.add("Open a WaPo puzzle");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Click the Print option and select the option “Solution”");
            //Click the Print option and select the option “Solution”
            browser.clickOnCSS(print);
            browser.switchToPopUpWindow();
            browser.clickOnCSS(print_solution);
            browser.clickOnCSS(print_button);
            steps.add("In a new tab, the printable HTML of the grid along with the answers filled in and the clue columns gets displayed and the print dialog gets shown as well.");
            //In a new tab, the printable HTML of the grid along with the answers filled in and the clue columns gets displayed and the print dialog gets shown as well.
            browser.switchToNextTab();
            String getURL = browser.driver.getCurrentUrl();
            if (!(getURL.contains(".pdf"))) {
                screenshotPath.add(browser.takeScreenshot(""));
            } else {
                screenshotPath.add(browser.driver.getCurrentUrl());
            }
            steps.add("Check that the generated PDF is readable, all the letters in the grid are displayed correctly, all the clues are displayed correctly without getting overwritten or truncated and the title is also shown");
            //Check that the generated PDF is readable, all the letters in the grid are displayed correctly, all the clues are displayed correctly without getting overwritten or truncated and the title is also shown
            //taking screenshot, needs to be inspected manually
        } catch (Exception e) {
            handleError(e);
        } finally {
            browser.quitBrowser();
        }

    }

    //Test Case: 8.6.1: Checks that rebus letters are being printed properly
    public void checkRebusPrint(boolean start) {
        id = "8.6.1";
        passed = 2;
        testDesc = "Test Case: 8.6.1: Checks that rebus letters are being printed properly";
        try {
            steps.add("Open a WaPo Sunday-EB puzzle");
            //Open a WaPo Sunday-EB puzzle
            if (start)
                start();
            steps.add("Enter 2-3 Rebus letters in a square a couple of times using the Rebus option");
            //Enter 2-3 Rebus letters in a square a couple of times using the Rebus option
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, "`ABCD" + Keys.ENTER);
            browser.sendKeysTo(input, "`XYZ" + Keys.ENTER);
            steps.add("Print the filled in grid using “Print”->  “Filled Letters” option");
            //Print the filled in grid using “Print”->  “Filled Letters” option
            openPrintPopup();
            browser.clickOnCSS(print_filled);
            browser.clickOnCSS(print_button);
            browser.switchToNextTab();
            steps.add("Verify that the Rebus letters are printed correctly and legibly in the resulting pdf");
            //Verify that the Rebus letters are printed correctly and legibly in the resulting pdf
            browser.waitFor(5);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }

    }

    //Test Case: 8.6.2: Checks that rebus letters are being printed properly in the solution mode
    public void checkRebusPrintSolution(boolean start) {
        id = "8.6.2";
        passed = 2;
        testDesc = "Test Case: 8.6.2: Checks that rebus letters are being printed properly in the solution mode";
        try {
            steps.add("Open a WaPo Sunday-EB puzzle containing Rebus letters");
            //Open a WaPo Sunday-EB puzzle containing Rebus letters
            if (start)
                start();
            steps.add("Print the filled in grid using “Print”->  “Solutions” option");
            //Print the filled in grid using “Print”->  “Solutions” option
            openPrintPopup();
            browser.clickOnCSS(print_solution);
            browser.clickOnCSS(print_button);
            browser.switchToNextTab();
            steps.add("Verify that the Rebus letters are printed correctly and legibly in the resulting pdf");
            //Verify that the Rebus letters are printed correctly and legibly in the resulting pdf
        } catch (Exception e) {
            handleError(e);
        } finally {
            screenshotPath.add(browser.driver.getCurrentUrl());
            browser.quitBrowser();
        }
    }

    //Test Case: 8.8.1: Checks that circled letters are being printed properly
    public void checkCircledLettersPrint(boolean start) {
        id = "8.8.1";
        passed = 2;
        testDesc = "Test Case: 8.8.1: Checks that circled letters are being printed properly";
        try {
            steps.add("Open a WaPo Puzzle containing circled cells (daily puzzle)");
            //Open a WaPo Puzzle containing circled cells (daily puzzle)
            if (start)
                start();
            steps.add("Note the position of the circled cells");
            //Note the position of the circled cells
            steps.add("Using the print option, print the blank and solution pdfs");
            //Using the print option, print the blank and solution pdfs
            browser.clickOnCSS(print);
            browser.switchToPopUpWindow();
            browser.clickOnCSS(blank_puzzle);
            browser.clickOnCSS(print_button);
            browser.switchToNextTab();
            screenshotPath.add(browser.driver.getCurrentUrl());
            browser.closeTab();
            browser.clickOnCSS(print);
            browser.switchToPopUpWindow();
            browser.clickOnCSS(print_solution);
            browser.clickOnCSS(print_button);
            browser.switchToNextTab();
            screenshotPath.add(browser.driver.getCurrentUrl());
            browser.closeTab();
            steps.add("The cells shown as circled in the grid should be circled in the print pdf as well");
            //The cells shown as circled in the grid should be circled in the print pdf as well
            steps.add("In the solution pdf, check that the circled letters are readable");
            //In the solution pdf, check that the circled letters are readable
            steps.add("Fill in a few letters in the circled cells in the grid and take a print pdf using the filled letters option");
            //Fill in a few letters in the circled cells in the grid and take a print pdf using the filled letters option
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, "ABC");
            browser.clickOnCSS(print);
            browser.switchToPopUpWindow();
            browser.clickOnCSS(print_filled);
            browser.clickOnCSS(print_button);
            browser.switchToNextTab();
            steps.add("Check that the filled letters in the circled cells are readable");
            //Check that the filled letters in the circled cells are readable

        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 8.9.1 : Checks that rebus and circled letters are being printed properly
    public void checkRebusCircledLettersPrint(boolean start) {
        id = "8.9.1";
        passed = 2;
        testDesc = "Test Case: 8.9.1 : Checks that rebus and circled letters are being printed properly";
        try {
            steps.add("Open a WaPo Puzzle containing circled cells and rebus (Sunday - Evan Birnholz puzzle)");
            //Open a WaPo Puzzle containing circled cells and rebus (Sunday - Evan Birnholz puzzle)
            if (start)
                start();
            steps.add("Note the position of the circled cells");
            //Note the position of the circled cells
            steps.add("Using the print option, print the blank and solution pdfs");
            //Using the print option, print the blank and solution pdfs
            browser.clickOnCSS(print);
            browser.switchToPopUpWindow();
            browser.clickOnCSS(blank_puzzle);
            browser.clickOnCSS(print_button);
            browser.switchToNextTab();
            screenshotPath.add(browser.driver.getCurrentUrl());
            browser.closeTab();
            browser.clickOnCSS(print);
            browser.switchToPopUpWindow();
            browser.clickOnCSS(print_solution);
            browser.clickOnCSS(print_button);
            browser.switchToNextTab();
            screenshotPath.add(browser.driver.getCurrentUrl());
            browser.closeTab();
            steps.add("The cells shown as circled in the grid should be circled in the print pdf as well");
            //The cells shown as circled in the grid should be circled in the print pdf as well
            steps.add("In the solution pdf, check that the circled letters and the rebus letters are readable");
            //In the solution pdf, check that the circled letters and the rebus letters are readable
            steps.add("Fill in a few rebus letters in the circled cells in the grid and take a print pdf using the filled letters option");
            //Fill in a few rebus letters in the circled cells in the grid and take a print pdf using the filled letters option
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, "`ABC" + Keys.ENTER);
            browser.sendKeysTo(input, "DEF");
            browser.clickOnCSS(print);
            browser.switchToPopUpWindow();
            browser.clickOnCSS(print_filled);
            browser.clickOnCSS(print_button);
            browser.switchToNextTab();
            steps.add("Check that the filled rebus letters in the circled cells are readable");
            //Check that the filled rebus letters in the circled cells are readable

        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.1.2 : Check the about option is working.
    //Does not check copyright information as the puzzle may not be copyrighted
    public void checkAbout() {
        id = "9.1.2";
        testDesc = "Test Case: 9.1.2 : Check the about option is working.";
        try {
            steps.add("Open WaPo puzzle");
            //Open WaPo puzzle
            browser = new StepDefs();
            browser.openBrowser(browserName);
            browser.openURL(BASE_URL);
            browser.waitFor("iframe[name='" + ifframeName + "']", 5); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            String URL = browser.driver.findElement(By.cssSelector(iframeXwordId)).getAttribute("src");
            browser.openURL(URL);
            if (mobile && testConfig.puzzleType == 0)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 60);
            browser.clickOnCSS(pickerDiv + " " + pickerListItem);
            browser.closeTab();
            //browser.switchToNextTab();
            browser.waitFor(startButton, 5);
            browser.clickOnCSS(startButton);
            steps.add("Go to the Puzzle menu by clicking the hamburger icon");
            //Go to the Puzzle menu by clicking the hamburger icon
            openPuzzleMenu();
            steps.add("Go to the About option in the Puzzle Menu");
            //Go to the About option in the Puzzle Menu
            browser.clickOnCSS(about);
            steps.add("In the About popup that comes up, verify that AmuseLabs contact information, puzzle copyright, base-64 encoding of the URL of the embedding page;server descriptor;playId are displayed.");
            //In the About popup that comes up, verify that AmuseLabs contact information, puzzle copyright, base-64 encoding of the URL of the embedding page;server descriptor;playId are displayed.
            browser.waitFor(5);
            browser.switchToPopUpWindow();
            browser.waitFor("a", 5);
            browser.verifyContains("a[href=\"mailto:puzzlemaster@amuselabs.com\"]", "puzzlemaster@amuselabs.com"); //AmuseLabs contact information
            browser.verifyContains("a[href=\"http://amuselabs.com\"]", "PuzzleMe™"); //AmuseLabs website link
            //  browser.verifyContains(".modal-body","© 2017 Tribune Content Agency, LLC"); //puzzle copyright
            String text = browser.getTextValueOf(".modal-body span");
            text = text.trim();
            String encodedURL = text.substring(0, text.indexOf(";"));
            text = text.substring(text.indexOf(";") + 1);
            String server_descriptor = text.substring(0, text.indexOf(";"));
            text = text.substring(text.indexOf(";") + 1);
            String play_id = text.substring(0, text.indexOf(" ")).substring(0, text.indexOf("Also"));
            if (encodedURL.length() == 0 || server_descriptor.length() == 0 || play_id.length() == 0)
                throw new RuntimeException("All three components- base-64 encoding of the URL of the embedding page;server descriptor;playId are not present");
            else log.info("All elements present" + " " + encodedURL + " " + server_descriptor + " " + play_id);
            String myencodedURL = encodedURL.replaceAll("[ \n]", "");
            // Encoding URL
            byte[] bytes = URL.getBytes("UTF-8");
            String encoded = Base64.getEncoder().encodeToString(bytes);
            if (!encoded.equals(myencodedURL))
                throw new RuntimeException("Invalid encoded URL. Expected encoded URL:" + encoded + " Found encoded URL:" + myencodedURL);
            else log.info("Encoded URL matches actual URL:" + myencodedURL);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case 9.3.3: Checks that the clear option is working
    public void checkReset(boolean start) {
        id = "9.3.3";
        testDesc = "Test Case 9.3.3: Checks that the clear option is working";
        try {
            steps.add("Open WaPo puzzle");
            //Open WaPo puzzle
            if (start)
                start();
            steps.add("Go to the Puzzle menu by clicking the hamburger icon");
            //Go to the Puzzle menu by clicking the hamburger icon
            openPuzzleMenu();
            steps.add("Go to the Clear option in the Puzzle Menu");
            //Go to the Clear option in the Puzzle Menu
            browser.clickOnCSS(clear);
            steps.add("A popup comes up asking “Are you sure you want to clear the puzzle?”");
            //A popup comes up asking “Are you sure you want to clear the puzzle?”
            browser.switchToPopUpWindow();
            steps.add("Verify that the focus is on the cancel button to avoid any inadvertent clicking of the Clear button");
            //Verify that the focus is on the cancel button to avoid any inadvertent clicking of the Clear button
            browser.verifyContains("button.btn-primary.confirm-no", "Cancel");
            steps.add("Click the Cancel button. Then the popup is closed and the grid remains as it is.");
            //Click the Cancel button. Then the popup is closed and the grid remains as it is.
            browser.clickOnCSS("button.btn-primary.confirm-no");
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, testConfig.firstWordAcross);
            steps.add("Go to the Puzzle menu by clicking the hamburger icon");
            //Go to the Puzzle menu by clicking the hamburger icon
            openPuzzleMenu();
            steps.add("Go to the Clear option in the Puzzle Menu.");
            //Go to the Clear option in the Puzzle Menu.
            browser.clickOnCSS(clear);
            steps.add("Click Clear button in the popup that comes up. Then the entire grid is cleared.");
            //Click Clear button in the popup that comes up. Then the entire grid is cleared.
            browser.switchToPopUpWindow();
            browser.clickOnCSS("button.btn.confirm-yes");
            browser.checkNotFilled(letter);
            steps.add("Verify that the score and timer are also reset. (Note - Score is not visible in Newsday and WaPo -- resetting of score can be crosschecked by entering a few words and revealing the grid and checking the scores.)");
            //Verify that the score and timer are also reset. (Note - Score is not visible in Newsday and WaPo -- resetting of score can be crosschecked by entering a few words and revealing the grid and checking the scores.)
            if (testConfig.puzzleType == 1) //pmm puzzle
            {
                browser.verifyContains(score, 0 + "");
            } else {
                browser.verifyContains(clock, "0:00");
                reveal(revealAll);
                browser.switchToPopUpWindow();
                browser.clickOnCSS(".btn.confirm-yes");
                browser.switchToPopUpWindow();
                browser.verifyContains("td.final-score", "0");
            }
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.5.1: Verifies that clues are marked grey when error-check-mode if off
    public void isClueGrey(boolean start) {
        id = "9.5.1";
        testDesc = "Test Case: 9.5.1: Verifies that clues are marked grey when error-check-mode if off";
        try {
            steps.add("Open a WaPo Puzzle");
            //Open a WaPo Puzzle
            if (start)
                start();
            steps.add("Verify that “Error Check Mode” option is not on in the Settings popup");
            //Verify that “Error Check Mode” option is not on in the Settings popup
            isErrorCheckModeOff();
            steps.add("Enter a word completely in the grid");
            //Enter a word completely in the grid
            enterCorrectWord();
            steps.add("The corresponding clue column is turned grey");
            //The corresponding clue column is turned grey
            checkClueGrey();
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.6.3: Checks that help option shows about rebus
    public void checkRebusHelp(boolean start) {
        id = "9.6.3";
        testDesc = "Test Case: 9.6.3: Checks that help option shows about rebus";
        try {
            steps.add("Open a WaPo Sunday-EB puzzle");
            //Open a WaPo Sunday-EB puzzle
            if (start)
                start();
            steps.add("Open the Help Guide via the Hamburger menu");
            //Open the Help Guide via the Hamburger menu
            openPuzzleMenu();
            browser.clickOnCSS(help);
            browser.switchToPopUpWindow();
            steps.add("Check that the Help includes Rebus related information");
            //Check that the Help includes Rebus related information
            browser.verifyContains("div.help-item-title", "Rebus", true);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case 9.6.4: Checks that help option does not show about rebus
    public void checkNoRebusHelp(boolean start) {
        id = "9.6.4";
        testDesc = "Test Case: 9.6.4: Checks that help option does not show about rebus";
        try {
            steps.add("Open a WaPo Daily or Classic puzzle");
            //Open a WaPo Daily or Classic puzzle
            if (start)
                start();
            steps.add("Open the Help Guide via the Hamburger menu");
            //Open the Help Guide via the Hamburger menu
            openPuzzleMenu();
            browser.clickOnCSS(help);
            browser.switchToPopUpWindow();
            steps.add("Check that the Help does not include Rebus related information");
            //Check that the Help does not include Rebus related information
            if (browser.driver.findElement(By.cssSelector("div.help-content")).getText().contains("Rebus"))
                throw new RuntimeException("Rebus information present");
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.8.1: Verifies that the info option, if present, is working properly
    public void checkInfo(boolean start) {
        id = "9.8.1";
        testDesc = "Test Case: 9.8.1: Verifies that the info option, if present, is working properly";
        try {
            steps.add("Open a WaPo Classic puzzle");
            //Open a WaPo Classic puzzle
            BASE_URL = "https://cdn1.amuselabs.com/wapo/crossword?id=mreagle_171231&set=wapo-mr&embed=1&compact=1&picker=1&src=https%3A%2F%2Fcdn1.amuselabs.com%2Fwapo%2Fwp-picker%3Fset%3Dwapo-mr%26embed%3D1%26limit%3D4";
            if (start)
                start();
            if (browser.driver.findElements(By.cssSelector("#puzzle-instructions-button")).size() > 0) {
                steps.add("Click the Info button on the Navbar");
                //Click the Info button on the Navbar
                browser.clickOnCSS("#puzzle-instructions-button > a");
                steps.add("Verify that a popup with the puzzle information and copyright comes up");
                //Verify that a popup with the puzzle information and copyright comes up
                browser.switchToPopUpWindow();
                browser.isElementPresent("div.start-message");
                browser.checkNonEmpty(".start-message > div");
                browser.checkNonEmpty(".start-message");
            } else steps.add("Info button not present for this puzzle.");
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.9.01: Verifies that the puzzle is intialized properly
    //0 for WaPo, 1 for pmm, 2 for NewsDay
    public void isInitialized(boolean start, boolean end) {
        testDesc = "Test Case: 9.9.01: Verifies that the puzzle is intialized properly";
        id = "9.9.01";
        try {
            switch (testConfig.puzzleType) {
                case 0:
                    steps.add("Open a WaPo puzzle");
                    //Open a WaPo puzzle
                    if (start)
                        start();
                    steps.add("Verify that the timer is set to 0:00");
                    //Verify that the timer is set to 0:00
                    browser.waitFor(".clock-time", 5);
                    browser.verifyContains(".clock-time", "0:00");
                    steps.add("Verify the grid is blank.");
                    // Verify the grid is blank.
                    browser.checkNotFilled(".letter-in-box");
                    break;
                case 1:
                    steps.add("Open a pmm puzzle");
                    //Open a pmm puzzle
                    if (start)
                        start();
                    steps.add("Verify that the Score and Timer are set to 0 and 0:00 respectively.");
                    //Verify that the Score and Timer are set to 0 and 0:00 respectively.
                    browser.waitFor(".clock-time", 5);
                    browser.verifyContains(".clock-time", "0:00");
                    browser.verifyContains("span#score.clock-time", "0");
                    steps.add("The grid is blank. ");
                    // The grid is blank.
                    browser.checkNotFilled(".letter-in-box");
                    steps.add("Amuselabs logo is shown");
                    // Amuselabs logo is shown
                    browser.isElementPresent("a[href=\"http://amuselabs.com\"]");
                    browser.isElementPresent("img[src=\"resources/al-logo-small.png\"]");
                    break;
                case 2:
                    steps.add("Open a Newsday puzzle");
                    //Open a Newsday puzzle
                    if (start)
                        start();
                    steps.add("Verify that the grid is blank.");
                    //Verify that the grid is blank.
                    browser.waitFor(".letter", 5);
                    browser.checkNotFilled(".letter-in-box");
                    break;
                default:
                    throw new RuntimeException("Puzzle not recognized.");
            }
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish(end);
        }

    }

    //Test Case: 9.9.03: Verifies that clue numbers are proper
    public void checkClueNumbers(boolean start, boolean end) { //This is not working
        id = "9.9.03";
        testDesc = "Test Case: 9.9.03: Verifies that clue numbers are proper";
        try {
            steps.add("Open any puzzle");
            //Open any puzzle (preferably a 15x15 or 21x21)
            if (start)
                start();
            steps.add("Check that the clue numbers in the clue columns are greater than 0");
            //Check that the clue numbers in the clue columns are greater than 0
            List<WebElement> elements = browser.driver.findElements(By.cssSelector("div.cluenum"));
            for (int i = 0; i < elements.size(); ++i) {
                WebElement e = elements.get(i);
                log.info("Current clue:" + e.getAttribute("textContent"));
                if (!(Integer.parseInt(e.getAttribute("textContent")) > 0))
                    throw new RuntimeException("Clue Number in clue column:" + e.getText() + " is not greater than zero.");
            }
            steps.add("Check that the clue numbers in the grid are greater than 0");
            //Check that the clue numbers in the grid are greater than 0
            List<WebElement> elements1 = browser.driver.findElements(By.cssSelector("span.cluenum-in-box"));
            for (int i = 0; i < elements1.size(); ++i) {
                WebElement e = elements1.get(i);
                log.info("Current clue:" + e.getAttribute("textContent"));
                if (!(Integer.parseInt(e.getAttribute("textContent")) > 0))
                    throw new RuntimeException("Clue Number in grid:" + e.getText() + " is not greater than zero.");
            }
            steps.add("Check that the clue numbers in the grid need to match with those in the clue columns. Check this for the following");
            //Check that the clue numbers in the grid need to match with those in the clue columns. Check this for the following
            steps.add("The first Across word");
            //The first Across word
            browser.clickOnCSS(".aclues .cluediv");
            WebElement webElement = browser.driver.findElement(By.cssSelector(".aclues .cluediv"));
            String clue = webElement.findElement(By.cssSelector("span.cluetext")).getText();
            WebElement e = browser.driver.findElement(By.cssSelector(".crossword .hilited-box-with-focus"));
            if (!e.findElement(By.cssSelector("span.cluenum-in-box")).getText().equals("1"))
                throw new RuntimeException("Corresponding word is not highlited");
            for (int i = 0; i < testConfig.firstWordAcross.length(); ++i)
                browser.checkAttributeContains(".crossword .box", "class", "hilited-box", i);
            if (mobile)
                browser.verifyContains("div.top-clue-text", "1A");
            else
                browser.verifyContains("div.top-clue-text", "1 ACROSS");
            browser.verifyContains("div.top-clue-text", clue);
            steps.add("The first Down word");
            //The first Down word
            browser.clickOnCSS(".dclues .cluediv");
            webElement = browser.driver.findElement(By.cssSelector(".dclues .cluediv"));
            clue = webElement.findElement(By.cssSelector("span.cluetext")).getText();
            e = browser.driver.findElement(By.cssSelector(".crossword .hilited-box-with-focus"));
            if (!e.findElement(By.cssSelector("span.cluenum-in-box")).getText().equals("1"))
                throw new RuntimeException("Corresponding word is not highlited");
            for (int i = 0; i < testConfig.firstWordDown.length(); ++i)
                browser.checkAttributeContains(".crossword .box", "class", "hilited-box", i * getColumns());
            if (mobile)
                browser.verifyContains("div.top-clue-text", "1D");
            else
                browser.verifyContains("div.top-clue-text", "1 DOWN");
            browser.verifyContains("div.top-clue-text", clue);
            steps.add("The last Across word");
            //The last Across word
            List<WebElement> elementList = browser.driver.findElements(By.cssSelector(".aclues .cluediv"));
            WebElement element = elementList.get(elementList.size() - 1);
            clue = element.findElement(By.cssSelector("span.cluetext")).getText();
            ((JavascriptExecutor) browser.driver).executeScript("arguments[0].scrollIntoView(true);", element);
            Thread.sleep(500);
            element.click();
            String cluenum = element.getAttribute("cluenum");
            e = browser.driver.findElement(By.cssSelector(".crossword .hilited-box-with-focus"));
            List<WebElement> boxes = browser.driver.findElements(By.cssSelector(".crossword .box"));
            int index = 0;
            for (int i = 0; i < boxes.size(); ++i) {
                if (boxes.get(i).getAttribute("class").contains("hilited-box-with-focus")) {
                    index = i;
                    break;
                }
            }
            if (!e.findElement(By.cssSelector("span.cluenum-in-box")).getText().equals(cluenum))
                throw new RuntimeException("Corresponding word is not highlited");
            for (int i = 0; i < testConfig.lastWordAcross.length(); ++i)
                browser.checkAttributeContains(".crossword .box", "class", "hilited-box", i + index);
            if (mobile)
                browser.verifyContains("div.top-clue-text", cluenum + "A");
            else
                browser.verifyContains("div.top-clue-text", cluenum + " ACROSS");
            browser.verifyContains("div.top-clue-text", clue);
            steps.add("The last Down word");
            //The last Down word
            elementList = browser.driver.findElements(By.cssSelector(".dclues .cluediv"));
            element = elementList.get(elementList.size() - 1);
            clue = element.findElement(By.cssSelector("span.cluetext")).getText();
            ((JavascriptExecutor) browser.driver).executeScript("arguments[0].scrollIntoView(true);", element);
            Thread.sleep(500);
            element.click();
            cluenum = element.getAttribute("cluenum");
            e = browser.driver.findElement(By.cssSelector(".crossword .hilited-box-with-focus"));
            if (!e.findElement(By.cssSelector("span.cluenum-in-box")).getText().equals(cluenum))
                throw new RuntimeException("Corresponding word is not highlited");
            boxes = browser.driver.findElements(By.cssSelector(".box"));
            index = 0;
            for (int i = 0; i < boxes.size(); ++i) {
                if (boxes.get(i).getAttribute("class").contains("hilited-box-with-focus")) {
                    index = i;
                    break;
                }
            }
            for (int i = 0; i < testConfig.lastWordDown.length(); ++i)
                browser.checkAttributeContains(".crossword .box", "class", "hilited-box", index + i * getColumns());
            if (mobile)
                browser.verifyContains("div.top-clue-text", cluenum + "D");
            else
                browser.verifyContains("div.top-clue-text", cluenum + " DOWN");
            browser.verifyContains("div.top-clue-text", clue);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish(end);
        }
    }

    //Test Case 9.9.04: Checks that Powered by PuzzleMe is shown
    public void isPowered(boolean start, boolean end) {
        id = "9.9.04";
        testDesc = "Test Case 9.9.04: Checks that Powered by PuzzleMe is shown";
        try {
            steps.add("Open a WaPo puzzle ");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Verify that “Powered by PuzzleMe™” text is below the grid and has a hyperlink to amuselabs.com website");
            //Verify that “Powered by PuzzleMe™” text is below the grid and has a hyperlink to amuselabs.com website
            browser.verifyContains("div", "Powered by ");
            browser.verifyContains(".footer-powered-by a[href=\"http://amuselabs.com\"]", "PuzzleMe™");
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish(end);
        }
    }

    //Test Case : 9.9.05: Verifies logo is not appearing in embed mode
    public void verifyEmbed(boolean start) {
        id = "9.9.05";
        testDesc = "Test Case : 9.9.05: Verifies logo is not appearing in embed mode";
        try {
            steps.add("Open a WaPo puzzle");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Verify that the AmuseLabs logo does not appear anywhere on the puzzle page");
            //Verify that the AmuseLabs logo does not appear anywhere on the puzzle page
            browser.isElementNotPresent("img[src=\"resources/al-logo-small.png\"]");
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.9.06: WaPo: Verify that author name and puzzle name appearing below the grid
    public void checkAuthorNameWaPo(boolean start) {
        id = "9.9.06";
        testDesc = "Test Case: 9.9.06: WaPo: Verify that author name and puzzle name appearing below the grid";
        try {
            steps.add("Open a WaPo puzzle");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Verify that the Author name and Puzzle name appear below the grid.");
            //Verify that the Author name and Puzzle name appear below the grid.
            if (mobile) {
                //need to scroll back to first letter
                ((JavascriptExecutor) browser.driver).executeScript("scroll(0, 250);");
            }
            browser.checkNonEmpty(".crossword-footer-message div.footer-author"); //Author Name
            browser.checkNonEmpty(".crossword-footer-message div.footer-title"); //Puzzle Name
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.9.07: NewsDay: Verify that author name and puzzle name appearing below the grid
    public void checkAuthorNameND(boolean start) {
        id = "9.9.07";
        testDesc = "Test Case: 9.9.07: NewsDay: Verify that author name and puzzle name appearing below the grid";
        try {
            steps.add("Open a Newsday puzzle");
            //Open a Newsday puzzle
            if (start)
                start();
            steps.add("Verify that the Puzzle name and Author name appear below the grid. The author name should be followed by \"edited by Stanley Newman\" with a hyperlink to his website.");
            //Verify that the Puzzle name and Author name appear below the grid. The author name should be followed by "edited by Stanley Newman" with a hyperlink to his website.
            browser.verifyContains(".crossword-footer-message a[href=\"http://www.StanXwords.com\"]", "edited by Stanley Newman"); //Author Name
            browser.checkNonEmpty(".crossword-footer-message span"); //Puzzle Name
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.9.22: Verifies that cells marked as circled during puzzle creation (either through pm-creator or formats) should show up in the grid as circled
    public void checkCircledLetters() {
        id = "9.9.22";
        passed = 2;
        testDesc = "Test Case: 9.9.22: Verifies that cells marked as circled during puzzle creation (either through pm-creator or formats) should show up in the grid as circled";
        try {
            steps.add("Open a puzzle where cells were marked as circled during puzzle creation");
            //Open a puzzle where cells were marked as circled during puzzle creation http://amuselabs.com/pmm/crossword?id=541160d4&set=2f0e9eb1-1c06-4fad-92be-c0da61186e95
            testConfig.crosswordLink = "http://amuselabs.com/pmm/crossword?id=541160d4&set=2f0e9eb1-1c06-4fad-92be-c0da61186e95";
            testConfig.embedded = false;
            testConfig.startButtonPresent = true;
            start();
            steps.add("The circled cells should show up in the grid as circled");
            //The circled cells should show up in the grid as circled
            List<WebElement> box = browser.driver.findElements(By.cssSelector(letterBox));
            for (int i = 0; i < box.size(); ++i) {
                WebElement e = box.get(i);
                ((JavascriptExecutor) browser.driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].style.border = '2px solid red';", e);
                if (i == 3 || i == 23 || i == 24 || i == 37) {
                    if (e.findElements(By.cssSelector("span.box-with-background-shape")).size() == 0)
                        throw new RuntimeException("Expected circle. Circle not present.");
                } else {
                    if (e.findElements(By.cssSelector("span.box-with-background-shape")).size() > 0)
                        throw new RuntimeException("Not expected circle. Circle present.");
                }
                ((JavascriptExecutor) browser.driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].style.border = '2px solid green';", e);
            }
            if (mobile) {
                //need to scroll back to first letter
                ((JavascriptExecutor) browser.driver).executeScript("scroll(0, -250);");
            }
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.18.1: Verifies that puzzle is not erased on refresh
    public void checkRefresh(boolean start) {
        id = "9.18.1";
        testDesc = "Test Case: 9.18.1: Verifies that puzzle is not erased on refresh";
        try {
            steps.add("Enable 3rd party cookies in the browser");
            //Enable 3rd party cookies in the browser
            steps.add("Open a WaPo puzzle");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Enter a few words in the grid");
            //Enter a few words in the grid
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, testConfig.firstWordAcross);
            steps.add("Reload the puzzle via the refresh button");
            //Reload the puzzle via the refresh button
            browser.refreshPage();
            String word1 = getWord();
            log.info(word1 + "HEre " + testConfig.firstWordAcross + word1.length());
            if (!word1.equals(testConfig.firstWordAcross))
                throw new RuntimeException("Words are not present after page reload.");
            log.info("Words are present after page reload.");
            //Before closing the browser, read the cookies
            Set allCookies = browser.driver.manage().getCookies();
            log.info("Number of cookie:" + allCookies.toString());
            steps.add("Close and reopen the browser and open the same puzzle again - check that the previously entered letters in the grid are shown ");
            //Close and reopen the browser and open the same puzzle again - check that the previously entered letters in the grid are shown
            String URL = browser.driver.getCurrentUrl();
            String domain = URL.substring(0, URL.lastIndexOf("/"));
            log.info(URL + " " + domain);
            browser.quitBrowser();
            browser.openBrowser(browserName);
            browser.openURL(URL);
            //restore all cookies from previous session
            Iterator<Cookie> iter = allCookies.iterator();
            while (iter.hasNext()) {

                // Iterate one by one
                Cookie cookie = iter.next();
                browser.driver.manage().addCookie(cookie);
            }
            browser.openURL(URL);
            word1 = getWord();
            if (!word1.equals(testConfig.firstWordAcross))
                throw new RuntimeException("Words are not present after reopening browser.");
            log.info("Words are present after reopening browser.");
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.18.4: Checks that the settings are retained on refresh
    public void checkSettingsOnRefresh() {
        id = "9.18.4";
        testDesc = "Test Case: 9.18.4: Checks that the settings are retained on refresh";
        try {
            steps.add("Open a WaPo puzzle.");
            //Clear the browser cache and enable 3rd party cookies.
            //Open a WaPo puzzle.
            testConfig.crosswordLink = "https://www.washingtonpost.com/crossword-puzzles/daily/";
            browser = new StepDefs();
            browser.openBrowser(browserName);
            browser.openURL(testConfig.crosswordLink);
            browser.waitFor("iframe[name='" + ifframeName + "']", 5); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            String URL = browser.driver.findElement(By.cssSelector("#iframe-xword")).getAttribute("src");
            browser.openURL(URL);
            if (mobile && testConfig.puzzleType == 0)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 60);
            browser.clickOnCSS(pickerDiv + " " + pickerListItem);
            browser.waitFor(2);
            browser.closeTab();
            if (testConfig.startButtonPresent) {
                browser.waitFor(startButton, 15);
                browser.clickOnCSS(startButton);
            }
            steps.add("Open the Settings popup");
            //Open the Settings popup
            openSettingsPopup();
            steps.add("Change the setting “At the end of a word” from “Stay in current clue” to “Move to next clue”  and click “Apply”");
            //Change the setting “At the end of a word” from “Stay in current clue” to “Move to next clue”  and click “Apply”
            if (!mobile) {
                browser.isSelected(stayCurrent);
                browser.clickOnCSS(moveToNext);
            } else {
                browser.isSelected(moveToNext);
                browser.clickOnCSS(stayCurrent);
            }
            browser.clickOnCSS(applySettings);
            //Do not clear the browser cache or disable 3rd party cookies.
            steps.add("Reload the puzzle by clicking the browser refresh button and reselecting it in the picker");
            //Reload the puzzle by clicking the browser refresh button and reselecting it in the picker
            browser.openURL(URL);
            steps.add("Once the grid loads, open the Settings popup.");
            //Once the grid loads, open the Settings popup.
            if (mobile && testConfig.puzzleType == 0)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 60);
            browser.clickOnCSS(pickerDiv + " " + pickerListItem);
            browser.waitFor(2);
            browser.closeTab();
            steps.add("For the setting “At the end of a word”, option “Move to next clue” should be selected");
            //For the setting “At the end of a word”, option “Move to next clue” should be selected
            if (testConfig.startButtonPresent) {
                browser.waitFor(startButton, 15);
                browser.clickOnCSS(startButton);
            }
            openSettingsPopup();
            if (mobile)
                browser.isSelected(stayCurrent);
            else
                browser.isSelected(moveToNext);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.19.1: Checks that Rebus is enabled for WaPo - Sunday - EB
    public void isRebusEnabled() {
        id = "9.19.1";
        testDesc = "Test Case: 9.19.1: Checks that Rebus is enabled for WaPo - Sunday - EB";
        try {
            steps.add("Open a WaPo Sunday puzzle");
            //Open a WaPo Sunday puzzle
            BASE_URL = "https://www.washingtonpost.com/crossword-puzzles/sunday-evan-birnholz/";
            browser = new StepDefs();
            browser.openBrowser(browserName);
            browser.openURL(BASE_URL);
            browser.waitFor("iframe[name='" + ifframeName + "']", 5); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            String URL = browser.driver.findElement(By.cssSelector("#iframe-xword")).getAttribute("src");
            browser.openURL(URL);
            if (mobile && testConfig.puzzleType == 0)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 60);
            browser.clickOnCSS(pickerDiv + " " + pickerListItem);
            browser.waitFor(2);
            browser.closeTab();
            browser.waitFor(startButton, 15);
            browser.clickOnCSS(startButton);
            steps.add("Check that Rebus option is enabled for this puzzle");
            //Check that Rebus option is enabled for this puzzle
            browser.isElementPresent(rebusDiv);
            screenshotPath.add(browser.takeScreenshot(""));
            steps.add("Open a WaPo Daily puzzle");
            //Open a WaPo Daily puzzle
            browser.openURL("https://www.washingtonpost.com/crossword-puzzles/daily/");
            browser.waitFor("iframe[name='" + ifframeName + "']", 5); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            URL = browser.driver.findElement(By.cssSelector("#iframe-xword")).getAttribute("src");
            browser.openURL(URL);
            if (mobile && testConfig.puzzleType == 0)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 60);
            browser.clickOnCSS(pickerDiv + " " + pickerListItem);
            browser.waitFor(2);
            browser.closeTab();
            browser.waitFor(startButton, 15);
            browser.clickOnCSS(startButton);
            steps.add("Check that Rebus is not enabled for this puzzle");
            //Check that Rebus is not enabled for this puzzle
            browser.isElementNotPresent(rebusDiv);
            screenshotPath.add(browser.takeScreenshot(""));
            steps.add("Open a WaPo Classic puzzle");
            //Open a WaPo Classic puzzle
            browser.openURL("https://www.washingtonpost.com/crossword-puzzles/merl-reagle/");
            browser.waitFor("iframe[name='" + ifframeName + "']", 5); // wait for up to 20 seconds
            browser.waitFor(2); // wait for 2 seconds
            URL = browser.driver.findElement(By.cssSelector("#iframe-xword")).getAttribute("src");
            browser.openURL(URL);
            if (mobile && testConfig.puzzleType == 0)
                browser.clickOnCSS(playAdButtonMobile);
            browser.waitFor(pickerDiv, 60);
            browser.clickOnCSS(pickerDiv + " " + pickerListItem);
            browser.waitFor(2);
            browser.closeTab();
            browser.waitFor(startButton, 15);
            browser.clickOnCSS(startButton);
            steps.add("Check that Rebus is not enabled for this puzzle");
            //Check that Rebus is not enabled for this puzzle
            browser.isElementNotPresent(rebusDiv);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.19.2: Checks that Rebus is not enabled for Newsday and pmm
    public void isRebusNotEnabled() {
        id = "9.19.2";
        testDesc = "Test Case: 9.19.2: Checks that Rebus is not enabled for Newsday and pmm";
        try {
            steps.add("Open a Newsday puzzle");
            //Open a Newsday puzzle
            testConfig.crosswordLink = "https://www.newsday.com/entertainment/extras/crossword-puzzle-1.6375288";
            testConfig.embedded = true;
            testConfig.startButtonPresent = true;
            start();
            steps.add("Check that Rebus is not enabled for this puzzle");
            //Check that Rebus is not enabled for this puzzle
            browser.isElementNotPresent(rebusDiv);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.19.3: Checks that when rebus option is shown(Sunday-EB), clicking the Rebus option will allow entry of multiple letters in a box.
    public void checkRebusButton(boolean start) {
        id = "9.19.3";
        testDesc = "Test Case: 9.19.3: Checks that when rebus option is shown(Sunday-EB), clicking the Rebus option will allow entry of multiple letters in a box.";
        try {
            steps.add("Open a WaPo Sunday puzzle");
            //Open a WaPo Sunday puzzle
            if (start)
                start();
            steps.add("Using the Rebus option, check that multiple letters can be entered in a box");
            //Using the Rebus option, check that multiple letters can be entered in a box
            browser.clickOnCSS(letterBox);
            browser.clickOnCSS(rebusDiv);
            browser.isElementPresent(rebusInput);
            browser.sendKeysTo(letterBox, "ABCD" + Keys.ENTER);
            browser.verifyContains(letter, "ABCD");
            if (mobile) {
                //need to scroll back to first letter
                ((JavascriptExecutor) browser.driver).executeScript("scroll(0, -250);");
            }
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.20.5: Checks that rebus letters are being displayed properly on resize
    public void checkRebusOnResize(boolean start) {
        id = "9.20.5";
        testDesc = "Test Case: 9.20.5: Checks that rebus letters are being displayed properly on resize";
        passed = 2;
        try {
            steps.add("Open a WaPo Sunday-EB puzzle");
            //Open a WaPo Sunday-EB puzzle
            if (start)
                start();
            steps.add("Enter a few Rebus letters in the grid using the Rebus option");
            //Enter a few Rebus letters in the grid using the Rebus option
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, "`ABC" + Keys.ENTER);
            if (!mobile) {
                steps.add("Check that the Rebus letters are displayed correctly, when the browser is resized");
                //Check that the Rebus letters are displayed correctly, when the browser is resized
                Dimension dimension = new Dimension(600, 400);
                browser.driver.manage().window().setSize(dimension);
                screenshotPath.add(browser.takeScreenshot(""));
                browser.maximizeWindow();
            }
            steps.add("Check that the Rebus letters are displayed correctly, when the browser zoom is 125%");
            //Check that the Rebus letters are displayed correctly, when the browser zoom is 125%
            JavascriptExecutor executor = (JavascriptExecutor) browser.driver;
            executor.executeScript("document.body.style.zoom = '1.25'");
            screenshotPath.add(browser.takeScreenshot(""));
            steps.add("Check that the Rebus letters are displayed correctly, when the browser zoom is 80%");
            //Check that the Rebus letters are displayed correctly, when the browser zoom is 80%
            executor.executeScript("document.body.style.zoom = '0.8'");
            browser.waitFor(1);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.21.01: Verifies that the settings menu is being displayed properly
    public void checkSettings(boolean start) {
        id = "9.21.01";
        testDesc = "Test Case: 9.21.01: Verifies that the settings menu is being displayed properly";
        try {
            steps.add("Open a WaPo puzzle on desktop.");
            //Open a WaPo puzzle on desktop.
            if (start)
                start();
            steps.add("Open the Settings popup via the Navbar");
            //Open the Settings popup via the Navbar
            openSettingsPopup();
            steps.add("The following settings should be shown:");
            //The following settings should be shown:
            steps.add("Error check mode");
            //Error check mode
            browser.isElementPresent(errorCheckDiv + " img");
            browser.verifyContains(errorCheckDiv + " span", "Error check mode");
            steps.add("Show Timer");
            //Show Timer
            browser.isElementPresent(showTimerDiv + " img");
            browser.verifyContains(showTimerDiv + " span", "Show Timer");
            steps.add("After entering a letter");
            //After entering a letter
            browser.verifyContains("div.block-title", "After entering a letter", true);
            steps.add("At the end of a word");
            //At the end of a word
            browser.verifyContains("div.block-title", "At the end of a word", true);
            steps.add("Use space key to");
            //Use space key to
            browser.verifyContains("div.block-title", "Use space key to", true);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.21.03: Check if stay in current clue feature is working properly
    public void checkStayInCurrentClue(boolean start) {
        id = "9.21.03";
        testDesc = "Test Case: 9.21.03: Check if stay in current clue feature is working properly";
        try {
            steps.add("Open a WaPo puzzle on desktop.");
            //Open a WaPo puzzle on desktop.
            if (start)
                start();
            steps.add("Open the Settings popup via the Navbar");
            //Open the Settings popup via the Navbar
            openSettingsPopup();
            steps.add("Verify that \"Stay in current clue\" option in \"At the end of a word\" setting is the default option.");
            //Verify that "Stay in current clue" option in "At the end of a word" setting is the default option.
            browser.isSelected(stayCurrent);
            browser.clickOnCSS(applySettings);
            steps.add("In the grid, verify that the cursor stays in the current clue, when the end of the word is reached.");
            //In the grid, verify that the cursor stays in the current clue, when the end of the word is reached.
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, testConfig.firstWordAcross);
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", testConfig.firstWordAcross.length() - 1);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }

    }

    //Test Case: 9.21.04: Checks that move to next clue feature is working properly
    public void checkMoveToNextClue(boolean start) {
        id = "9.21.04";
        testDesc = "Test Case: 9.21.04: Checks that move to next clue feature is working properly";
        try {
            //Open a WaPo puzzle on desktop.
            steps.add("Open a WaPo puzzle on desktop.");
            if (start)
                start();
            //Open the Settings popup via the Navbar
            steps.add("Open the Settings popup via the Navbar");
            openSettingsPopup();
            //Select "Move to next clue" option in "At the end of a word" setting.
            steps.add("Select \"Move to next clue\" option in \"At the end of a word\" setting.");
            browser.clickOnCSS(moveToNext);
            browser.clickOnCSS(applySettings);
            //In the grid, verify that the cursor moves to the next clue when the end of the current word is reached.
            steps.add("In the grid, verify that the cursor moves to the next clue when the end of the current word is reached.");
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, testConfig.firstWordAcross);
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", testConfig.firstWordAcross.length());
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.21.07: Checks that use space key to toggle between across and down is working
    public void checkToggleBetweenAcrossAndDown(boolean start) {
        id = "9.21.07";
        testDesc = "Test Case: 9.21.07: Checks that use space key to toggle between across and down is working";
        try {
            steps.add("Open a WaPo puzzle on desktop.");
            //Open a WaPo puzzle on desktop.
            if (start)
                start();
            steps.add("Open the Settings popup via the Navbar");
            //Open the Settings popup via the Navbar
            openSettingsPopup();
            steps.add("Verify that  \"Toggle between across and down\" option in \"Use space key to\" setting is the default option");
            //Verify that  "Toggle between across and down" option in "Use space key to" setting is the default option
            browser.isSelected("#space-arrow");
            browser.clickOnCSS(applySettings);
            steps.add("Verify that when the spacebar is pressed, the cursor should toggle between across and down.");
            //Verify that when the spacebar is pressed, the cursor should toggle between across and down.
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, Keys.SPACE + "");
            for (int i = 0; i < testConfig.firstWordDown.length(); ++i)
                browser.checkAttributeContains(".crossword .box", "class", "hilited-box", getColumns() * i);
            browser.sendKeysTo(letterBox, Keys.SPACE + "");
            for (int i = 0; i < testConfig.firstWordAcross.length(); ++i)
                browser.checkAttributeContains(".crossword .box", "class", "hilited-box", i);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.21.08 : Verify that  when the spacebar is pressed, it should clear the current box and move to the next.
    public void checkUseSpaceToClear(boolean start) {
        id = "9.21.08";
        testDesc = "Test Case: 9.21.08 : Verify that  when the spacebar is pressed, it should clear the current box and move to the next.";
        try {
            steps.add("Open a WaPo puzzle on desktop.");
            //Open a WaPo puzzle on desktop.
            if (start)
                start();
            steps.add("Open the Settings popup via the Navbar");
            //Open the Settings popup via the Navbar
            openSettingsPopup();
            steps.add("Select \"Clear the current box and move to the next\" option in \"Use space key to\" setting");
            //Select "Clear the current box and move to the next" option in "Use space key to" setting
            browser.clickOnCSS("input#space-clear");
            browser.clickOnCSS(applySettings);
            steps.add("Verify that  when the spacebar is pressed, it should clear the current box and move to the next.");
            //Verify that  when the spacebar is pressed, it should clear the current box and move to the next.
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, "a");
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, Keys.SPACE + "");
            if (!browser.getTextValueOf(letter).isEmpty() && !browser.getTextValueOf(letter).equals(" "))
                throw new RuntimeException("Letter is not deleted");
            browser.checkAttributeContains(letterBox, "class", "hilited-box-with-focus", 1);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 9.21.10: Checks that skip over filled letters feature is working
    public void checkSkipOverFilled(boolean start) {
        id = "9.21.10";
        testDesc = "Test Case: 9.21.10: Checks that skip over filled letters feature is working";
        try {
            steps.add("Open a WaPo puzzle on desktop.");
            //Open a WaPo puzzle on desktop.
            if (start)
                start();
            steps.add("Open the Settings popup via the Navbar");
            //Open the Settings popup via the Navbar
            openSettingsPopup();
            steps.add("In \"After entering a letter\" option, deselect \"Skip over filled letters\".");
            //In "After entering a letter" option, deselect "Skip over filled letters".
            browser.isSelected(root + " " + skipSquares);
            browser.clickOnCSS(root + " " + skipSquares);
            browser.clickOnCSS(root + " " + applySettings);
            steps.add("In the grid, enter a few letters in the middle of a word.");
            //In the grid, enter a few letters in the middle of a word.
            browser.clickOnCSS(root + " " + letterBox + ":nth-of-type(2)");
            browser.sendKeysTo(root + " " + letterBox + ":nth-of-type(2)", "a");
            steps.add("Enter letters in the beginning of the word and continue to type in letters");
            //Enter letters in the beginning of the word and continue to type in letters
            browser.clickOnCSS(root + " " + letterBox);
            browser.sendKeysTo(root + " " + letter, "he");
            steps.add("Verify that the filled letters are overwritten while entering in the grid.");
            //Verify that the filled letters are overwritten while entering in the grid.
            browser.verifyContains(root + " " + letterBox + ":nth-of-type(2) " + letter, "e");
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 11.2.1 : Verifies Timer is ticking during the play
    public void isTicking(boolean start) {
        id = "11.2.1";
        testDesc = "Test Case: 11.2.1 : Verifies Timer is ticking during the play";
        try {
            steps.add("Open a WaPo Puzzle");
            if (start)
                start();
            // verify that the clock is present
            steps.add("verify that the clock is present\n");
            browser.verifyContains(clock, "0:00"); // clock should not have started yet, it starts only when the first key is pressed
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, "abc");
            long start1 = System.currentTimeMillis();
            steps.add("go to settings and switch timer OFF\n");
            //go to settings and switch timer OFF
            toggleTimer();
            steps.add("Verify that it is hidden.\n");
            // Verify that it is hidden.
            browser.isNotVisible(clock);
            steps.add("Go to settings and turn it on.\n");
            // Go to settings and turn it on.
            toggleTimer();
            long elapsedTimeMillis = System.currentTimeMillis() - start1;
            steps.add("Verify that it is back with the updated time.\n");
            // Verify that it is back with the updated time.
            float elapsedTimeSec = elapsedTimeMillis / 1000F;
            if (!mobile) {
                if (elapsedTimeSec < 10)
                    browser.verifyContains(clock, "0:0" + (int) elapsedTimeSec);
                else
                    browser.verifyContains(clock, "0:" + (int) elapsedTimeSec);
            } else {
                int time = Integer.parseInt(browser.getTextValueOf(clock).substring(browser.getTextValueOf(clock).indexOf(":") + 1));
                if (!(Math.abs(elapsedTimeSec - time) <= 3))
                    throw new RuntimeException("Timer is not the same after resume.");
            }
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 11.4.1 : Checks timer is automatically paused
    public void isTimerPaused(boolean start) {
        id = "11.4.1";
        testDesc = "Test Case: 11.4.1 : Checks timer is automatically paused";
        try {
            steps.add("Open a WaPo puzzle");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Start playing the crossword");
            //Start playing the crossword
            browser.clickOnCSS(letterBox);
            long start1 = System.currentTimeMillis();
            browser.sendKeysTo(letterBox, "abcd");
            openSettingsPopup();
            browser.clickOnCSS(applySettings);
            long elapsedTimeMillis = System.currentTimeMillis() - start1;
            steps.add("the timer should be ticking ");
            //the timer should be ticking
            float elapsedTimeSec = elapsedTimeMillis / 1000F;
            if (elapsedTimeSec < 10)
                browser.verifyContains(root + " " + clock, "0:0" + (int) elapsedTimeSec);
            else
                browser.verifyContains(root + " " + clock, "0:" + (int) elapsedTimeSec);
            steps.add("Press the ticking timer in the grid -- the timer is stopped and a clock icon is shown instead");
            //Press the ticking timer in the grid -- the timer is stopped and a clock icon is shown instead
            browser.clickOnCSS(root + " " + timerdiv);
            browser.isNotVisible(root + " " + clock);
            browser.isVisible(root + " " + clockPaused);
            steps.add("Leave the crossword page");
            //Leave the crossword page
            if (mobile) {
                browser.openURL("https://amuselabs.com/");
                browser.driver.navigate().back();
                browser.clickOnCSS(startButton);
            } else {
                browser.clickOn(puzzleMe);
                browser.switchToNextTab();
                steps.add("The timer is paused when the crossword page is viewed again");
                // The timer is paused when the crossword page is viewed again
                browser.switchToNextTab();
            }
            browser.isNotVisible(root + " " + clock);
            browser.isVisible(root + " " + clockPaused);
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    //Test Case: 11.5.1: Verifies that timer resumes automatically
    public void isTimerResumed(boolean start) {
        id = "11.5.1";
        testDesc = "Test Case: 11.5.1: Verifies that timer resumes automatically";
        try {
            steps.add("Open a WaPo puzzle");
            //Open a WaPo puzzle
            if (start)
                start();
            steps.add("Start playing the crossword - the timer should be ticking");
            //Start playing the crossword - the timer should be ticking
            browser.clickOnCSS(letterBox);
            long start1 = System.currentTimeMillis();
            browser.sendKeysTo(letterBox, "abcd");
            openSettingsPopup();
            browser.clickOnCSS(applySettings);
            long elapsedTimeMillis = System.currentTimeMillis() - start1;
            float elapsedTimeSec = elapsedTimeMillis / 1000F;
            if (elapsedTimeSec < 10)
                browser.verifyContains(root + " " + clock, "0:0" + (int) elapsedTimeSec);
            else
                browser.verifyContains(root + " " + clock, "0:" + (int) elapsedTimeSec);
            steps.add("Leave the crossword page");
            //Leave the crossword page
            int begin;
            if (mobile) {
                browser.openURL("https://amuselabs.com/");
                elapsedTimeMillis = System.currentTimeMillis() - start1;
                elapsedTimeSec = elapsedTimeMillis / 1000F;
                begin = (int) elapsedTimeSec;
                browser.driver.navigate().back();
                browser.clickOnCSS(startButton);
            } else {
                browser.clickOn(puzzleMe);
                elapsedTimeMillis = System.currentTimeMillis() - start1;
                elapsedTimeSec = elapsedTimeMillis / 1000F;
                begin = (int) elapsedTimeSec;
                browser.switchToNextTab();
                steps.add("The timer is ticking when the crossword page is viewed again");
                //The timer is ticking when the crossword page is viewed again
                browser.closeTab();
            }
            browser.isNotVisible(clockPaused);
            browser.isVisible(clock);
            int time = Integer.parseInt(browser.getTextValueOf(clock).substring(browser.getTextValueOf(clock).indexOf(":") + 1));
            if (!(Math.abs(begin - time) <= 3))
                throw new RuntimeException("Timer is not the same after resume.");
            checkTicking();
            steps.add("Press the ticking timer in the grid -- the timer is paused and a clock icon is shown instead");
            //Press the ticking timer in the grid -- the timer is paused and a clock icon is shown instead
            browser.clickOnCSS(root + " " + timerdiv);
            browser.isNotVisible(root + " " + clock);
            browser.isVisible(root + " " + clockPaused);
            steps.add("Press the clock icon in the grid and the timer resumes");
            //Press the clock icon in the grid and the timer resumes
            browser.clickOnCSS(clockPaused);
            browser.isVisible(clock);
            checkTicking();
            steps.add("Press the ticking timer in the grid -- the timer is paused");
            //Press the ticking timer in the grid -- the timer is paused
            browser.clickOnCSS(root + " " + timerdiv);
            browser.isNotVisible(root + " " + clock);
            browser.isVisible(root + " " + clockPaused);
            steps.add("Enter a letter in the grid and the timer resumes");
            //Enter a letter in the grid and the timer resumes
            browser.clickOnCSS(letterBox);
            browser.sendKeysTo(letterBox, "x");
            browser.isVisible(root + " " + clock);
            browser.isNotVisible(root + " " + clockPaused);
            checkTicking();
        } catch (Exception e) {
            handleError(e);
        } finally {
            onFinish();
        }
    }

    public void doIt() throws IOException, InterruptedException, ParseException {
        browser = new StepDefs();
        browser.openBrowser(browserName);
        verifyWaPoPuzzleSeriesList();
    }

}


