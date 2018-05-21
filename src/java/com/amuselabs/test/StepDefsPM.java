package com.amuselabs.test;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/** class that provides generic primities to drive and verify things in the browser.
 * Should not be specific to any particular app. */
public class StepDefsPM {
    public WebDriver driver;
    static String opsystem = System.getProperty("os.name");
    static String BROWSER_NAME;
    private static Log log = LogFactory.getLog(StepDefsPM.class);
     Stack<String> tabStack = new Stack<>();
    public static String testStatus = "1...2...3", testStatusColor = "rgba(10,140,10,0.8)";
    private static String screenshotsDir = TestReport.screenshotsDir;

    public boolean runningOnMac() {
        return System.getProperty("os.name").startsWith("Mac");
    }


    // @Given("^I navigate to \"(.*?)\"$")
    public void openURL(String url) {
        driver.navigate().to(url);
    }
    // @Given("^I wait for (\\d+) sec$")
    public void waitFor(int time) throws InterruptedException {
        TimeUnit.SECONDS.sleep(time);
    }
    public void waitFor(String cssSelector, int timeInSecs) throws InterruptedException {
        long startMillis = System.currentTimeMillis();
        WebDriverWait wait = new WebDriverWait(driver, timeInSecs);
        WebElement elem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssSelector)));
        long elapsedMillis = System.currentTimeMillis() - startMillis;
        log.info ("Elem " + cssSelector + " found after " + (elapsedMillis) + "ms vs. max of " + (timeInSecs * 1000) + "ms");
    }
    // @Given("^I enter (.*) into input field with name \"(.*?)\"$")
	public void enterValueInInputField( String fieldName, String inputValue) throws InterruptedException {
		inputValue = resolveValue(inputValue);
		try {
			WebElement inputField = driver.findElement(By.name(fieldName));
			inputField.sendKeys(inputValue);
		} catch (Exception e) {
			throw new RuntimeException ("Unable to find an input field to enter value in: (" + inputValue + ") " + "field: " + fieldName + " page: " + driver.getCurrentUrl());
		}
	}
	// @Then("I navigate back$")
	public void navigateBack() {
		driver.navigate().back();
	}
	// @Then("CSS element \"(.*)\" should have value (.*)$")
	public void verifyEquals(String selector, String expectedValue) {
		expectedValue = resolveValue(expectedValue);
		String actualText = driver.findElement(By.cssSelector(selector)).getText();
	    
		if (!actualText.equals(expectedValue)) {
			log.warn ("ACTUAL text for CSS selector " + selector + ": " + actualText + " EXPECTED: " + expectedValue);
			throw new RuntimeException();
		}
		log.info ("Found expected text for CSS selector " + selector + ": " + actualText);
		
	}
    /** this is now working */
    public void sendKeysTo(String selector, String sequence) {
            Actions builder = new Actions(driver);
            builder.sendKeys(sequence).perform();
            log.info("Sent key sequence to CSS selector " + selector + ": " + sequence);

        /*
        WebElement e =  driver.findElement(By.cssSelector(selector));
        new Actions(driver).moveToElement(e).perform();
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].style.border = '2px solid red';", e);

        e.sendKeys(sequence);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].style.border = '2px solid green';", e);
        */
    }
    public void sendKeysToLast(String selector, String sequence) {
        Actions builder = new Actions(driver);
        List<WebElement> clues = driver.findElements(By.cssSelector(selector));
        WebElement e = clues.get(clues.size()-1);
        builder .moveToElement(e).perform();
        builder.sendKeys(sequence).perform();
        log.info ("Sent key sequence to CSS selector " + selector + ": " + sequence);
    }
    /** verifies that the text in the given selector contains the given string (ignores case)
     (only checks the first element if multiple elements match the selector)
     *
     */
    public void verifyContains(String selector, String expectedValue) {
        expectedValue = resolveValue(expectedValue);
        WebElement e=  driver.findElement(By.cssSelector(selector));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].style.border = '2px solid red';", e);
            String actualText = e.getText();
            actualText = actualText.toLowerCase();
            expectedValue = expectedValue.toLowerCase();
            if (actualText.contains(expectedValue)) {
                log.info ("Found expected text for CSS selector " + selector + ": " + actualText);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].style.border = '2px solid green';", e);
            }
            else
            {
                log.warn("ACTUAL text for CSS selector " + selector + ": " + actualText + " EXPECTED TO CONTAIN: " + expectedValue);
                throw new RuntimeException();
            }

    }
	public void verifyContains(String selector, String expectedValue, boolean list) {
		expectedValue = resolveValue(expectedValue);
		List<WebElement> elements =  driver.findElements(By.cssSelector(selector));
		Iterator<WebElement> iter=elements.iterator();
		boolean found=false;
		while(iter.hasNext()) {
		    WebElement e = iter.next();
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].style.border = '2px solid red';", e);

            String actualText = e.getText();
            actualText = actualText.toLowerCase();
            expectedValue = expectedValue.toLowerCase();
            if (actualText.contains(expectedValue)) {
                log.info ("Found expected text for CSS selector " + selector + ": " + actualText);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].style.border = '2px solid green';", e);
                found=true;
                break;
            }
            else
                log.warn("ACTUAL text for CSS selector " + selector + ": " + actualText + " EXPECTED TO CONTAIN: " + expectedValue);

        }
        if(!found)
        {
            throw new RuntimeException();
        }
	}
    /** returns the string contained in the given selector (ignores case)
     (only checks the first element if multiple elements match the selector)
     *
     */
    public String getTextValueOf(String selector) {
        String actualText = driver.findElement(By.cssSelector(selector)).getText();
        //actualText = actualText.toLowerCase();
        log.info ("text for CSS selector " + selector + ": " + actualText);
        return actualText;
    }
    // @Then("CSS element \"([^\"]*)\" should contain (.*)$")
    public void verifyNumElements(String selector, int expectedCount) {
        List<WebElement> elements = driver.findElements(By.cssSelector(selector));
        if (elements == null || elements.size() != expectedCount) {
            log.warn ("ACTUAL #elements for CSS selector " + selector + ": " + elements.size() + " EXPECTED TO CONTAIN: " + expectedCount);
            throw new RuntimeException();
        }
        log.info ("Found expected #elements for CSS selector " + selector + ": " + elements.size());
    }
    // verify that the #elements with the given selector is >= count
    public void verifyNumElementsGE(String selector, int minCount) {
        List<WebElement> elements = driver.findElements(By.cssSelector(selector));
        if (elements == null || elements.size() < minCount) {
            log.warn ("ACTUAL #elements for CSS selector " + selector + ": " + elements.size() + " EXPECTED TO CONTAIN at Least: " + minCount);
            throw new RuntimeException();
        }
        log.info ("Found expected #elements for CSS selector" + selector + ": " + elements.size() + " > " + minCount);
    }
    //checks whether the elements matching selector contain text or not
    public void checkNonEmpty(String selector) {
        //checks all elements are non empty

        List<WebElement> clues = driver.findElements(By.cssSelector(selector));


        // Now using Iterator we will iterate all elements
        Iterator<WebElement> iter = clues.iterator();
        // this will check whether list has some element or not
        int i=0;
        while (iter.hasNext()) {

            // Iterate one by one
            WebElement item = iter.next();

            // get the text
            String label = item.getText();
            log.info("Current label:"+label);
            if (label.isEmpty()) {
                log.warn ("Label "+(i)+" is empty") ;
                throw new RuntimeException();
            }

            ++i;
        }

        log.info ("All "+i+" labels are non empty");
    }
    public void checkNotFilled(String selector) {
        //checks that all elements are empty

        List<WebElement> clues = driver.findElements(By.cssSelector(selector));


        // Now using Iterator we will iterate all elements
        Iterator<WebElement> iter = clues.iterator();
        // this will check whether list has some element or not
        while (iter.hasNext()) {

            // Iterate one by one
            WebElement item = iter.next();

            // get the text
            String label = item.getText();
            log.info("Current label:"+label);
            if (!label.isEmpty() && !label.equals(" ")) {
                log.warn ("Label is non-empty:"+label) ;
                throw new RuntimeException();
            }

        }
        log.info ("All labels are empty.");
    }
    public void switchToFrame (String framename) {
        List<WebElement> frames = driver.findElements(By.name(framename));
        if (frames == null || frames.size() != 1) {
            log.warn ("frame " + framename + " does not exist or is not unique");
            throw new RuntimeException();
        }
        WebDriver target = driver.switchTo().frame(frames.get(0));
        log.info ("switched to iframe, returned value is " + target);
    }
    public void switchToPopUpWindow () throws InterruptedException {
        if(BROWSER_NAME.equalsIgnoreCase("ipad")||BROWSER_NAME.equalsIgnoreCase("iphone"))
            return;
        if(BROWSER_NAME.equalsIgnoreCase("android")){
            String currentWindow=driver.getWindowHandle();
//Action that gets new window
            int ctime=1;
            //waiting till the windows size is 2 for some time
            while(driver.getWindowHandles().size()<3)
            {
                Thread.sleep(5000);
                ctime++;
                if(ctime==5)
                    break;
            }
            //Switching to window
            Set<String> windows2=driver.getWindowHandles();
            for(String window:windows2)
            {
                if(!window.equalsIgnoreCase(currentWindow))
                {
                    driver.switchTo().window(window);
                    break;
                }

            }
        }
        else {
            String parentWindowHandler = driver.getWindowHandle(); // Store your parent window
            String subWindowHandler = null;

            Set<String> handles = driver.getWindowHandles(); // get all window handles
            log.info("No. of handles:" + handles.size());
            if (handles == null) {
                log.warn("Pop Up " + " does not exist or is not unique" + handles.size());
                throw new RuntimeException();
            }
            Iterator<String> iterator = handles.iterator();
            while (iterator.hasNext()) {
                subWindowHandler = iterator.next();
            }
            driver.switchTo().window(subWindowHandler); // switch to popup window
            log.info("switched to popup");
        }
    }

    // @Then("^open browser$")
	public void openBrowser(String bname) throws MalformedURLException {
        try {
            // String consoleOutputFile = this.getValue("browserConsoleOutputFile");
            // System.setProperty("webdriver.log.file", consoleOutputFile + "-" + this.getValue("browser") + ".txt");

            //BROWSER_NAME = VARS.getProperty("browser");
            BROWSER_NAME=bname;
            if (BROWSER_NAME == null)
                BROWSER_NAME = "chrome";
            // declaration and instantiation of objects/variables
            if ("firefox".equalsIgnoreCase(BROWSER_NAME)) {
                if (runningOnMac()) {
                    String macDriver = TestReport.VARS.getProperty("webdriver.gecko.driver");
                    if (macDriver == null)
                        macDriver =  TestReport.userHome + "/" + "pm-test/src/test/resources/geckodriver";
                  System.setProperty("webdriver.gecko.driver", macDriver);
                   // DesiredCapabilities capabilities = DesiredCapabilities.firefox();
                    //capabilities.setCapability("marionette", true);
                   driver = new FirefoxDriver();
                } else {
                    System.err.println("WARNING!!!! Chrome driver is only specified for Mac?");
                }
            } else if ("chrome".equalsIgnoreCase(BROWSER_NAME)) {
                if (runningOnMac()) {
                    String macDriver = TestReport.VARS.getProperty ("webdriver.chrome.driver");
                    if (macDriver == null)
                        macDriver = "chromedriver";
                    System.setProperty("webdriver.chrome.driver", macDriver);
                } else {
                    System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
                }
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--incognito");
                options.addArguments("--start-maximized");
                driver = new ChromeDriver(options);
            } else if ("ie".equalsIgnoreCase(BROWSER_NAME)) {
                if(!runningOnMac()) {
                    System.setProperty("webdriver.ie.driver", "IEDriverServer.exe");
                    driver = new InternetExplorerDriver();
                }
                else System.err.println("Cannot test internet explorer on Mac");
            } else if ("safari".equalsIgnoreCase(BROWSER_NAME)) {
                System.setProperty("webdriver.safari.driver", "/usr/bin/safaridriver");
                driver = new SafariDriver();
            }
            else if("iphone".equalsIgnoreCase(BROWSER_NAME))
            {
                TestMain.mobile = true;
                DesiredCapabilities caps = new DesiredCapabilities();
                caps=DesiredCapabilities.iphone();
                caps.setCapability("platformName", "iOS");
                caps.setCapability("platformVersion", "11.0");
                caps.setCapability("deviceName", "iPhone Simulator");
                caps.setCapability("automationName", "XCUITest");
                caps.setCapability("browserName", "Safari");
                driver = new RemoteWebDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
            }
            else if("ipad".equalsIgnoreCase(BROWSER_NAME))
            {
                DesiredCapabilities caps = new DesiredCapabilities();
                caps=DesiredCapabilities.ipad();
                caps.setCapability("platformName", "iOS");
                caps.setCapability("platformVersion", "11.0");
                caps.setCapability("deviceName", "iPad Simulator");
                //caps.setCapability("automationName", "XCUITest");
                caps.setCapability("browserName", "Safari");
                driver = new RemoteWebDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
            }
            else if("android".equalsIgnoreCase(BROWSER_NAME)){
                TestMain.mobile = true;
                DesiredCapabilities caps = new DesiredCapabilities();
                caps=DesiredCapabilities.android();
                caps.setCapability("deviceName", "Galaxy J2");
             //   caps.setCapability("udid", "ENUL6303030010"); //Give Device ID of your mobile phone
                caps.setCapability("platformName", "Android");
                caps.setCapability("platformVersion", "5.1");
                caps.setCapability("browserName", "Chrome");
               // caps.setCapability("noReset", "true");
                driver = new RemoteWebDriver(new URL("http://0.0.0.0:4723/wd/hub"), caps);
                opsystem="Android";
                BROWSER_NAME="Chrome";
            }
            else {
                log.warn ("Unknown or unsupported browser: " + BROWSER_NAME);
            }
          driver.manage().deleteAllCookies();
        } catch (Exception e) {
            TestUtils.print_exception("Error opening browser", e, log);
        }
    }

    public void openBrowserWithCookiesDisabled(String bname){
        try {
            // String consoleOutputFile = this.getValue("browserConsoleOutputFile");
            // System.setProperty("webdriver.log.file", consoleOutputFile + "-" + this.getValue("browser") + ".txt");

            BROWSER_NAME = bname;

            if (BROWSER_NAME == null)
                BROWSER_NAME = "chrome";
            // declaration and instantiation of objects/variables
            if ("firefox".equalsIgnoreCase(BROWSER_NAME)) {
                if (runningOnMac()) {
                    String macDriver = TestReport.VARS.getProperty("webdriver.gecko.driver");
                    if (macDriver == null)
                        macDriver =  TestReport.userHome + "/" + "workspace/pm-test/src/test/resources/geckodriver";
                    System.setProperty("webdriver.gecko.driver", macDriver);
                    FirefoxProfile profile = new ProfilesIni().getProfile("default");
                    profile.setPreference("network.cookie.cookieBehavior", 2);
                    driver = new FirefoxDriver(); // profile);
                } else {
                    System.err.println("WARNING!!!! Chrome driver is only specified for Mac?");
                }
            } else if ("chrome".equalsIgnoreCase(BROWSER_NAME)) {
                if (runningOnMac()) {
                    String macDriver = TestReport.VARS.getProperty ("webdriver.chrome.driver");
                    if (macDriver == null)
                        macDriver = "chromedriver";
                    System.setProperty("webdriver.chrome.driver", macDriver);
                } else {
                    System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
                }
                ChromeOptions options = new ChromeOptions();
                Map prefs = new HashMap();
                prefs.put("profile.default_content_settings.cookies", 2);
                options.setExperimentalOption("prefs", prefs);
                options.addArguments("--incognito");
                options.addArguments("--start-maximized");
                driver = new ChromeDriver(options);
            } else if ("ie".equalsIgnoreCase(BROWSER_NAME)) {
                driver = new InternetExplorerDriver();
            } else if ("safari".equalsIgnoreCase(BROWSER_NAME)) {
                // You basically can't disable cookies in Safari, programmatically at least. You can clear cookies, but not disable them. https://groups.google.com/forum/#!topic/selenium-users/EmCp4pcbuPU
                System.setProperty("webdriver.safari.driver", "/usr/bin/safaridriver");
                driver = new SafariDriver();
            }  else if("iphone".equalsIgnoreCase(BROWSER_NAME))
            {
                TestMain.mobile = true;
                DesiredCapabilities caps = new DesiredCapabilities();
                caps=DesiredCapabilities.iphone();
                caps.setCapability("platformName", "iOS");
                caps.setCapability("platformVersion", "11.0");
                caps.setCapability("deviceName", "iPhone Simulator");
                caps.setCapability("automationName", "XCUITest");
                caps.setCapability("browserName", "Safari");
                driver = new RemoteWebDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
            }
            else if("ipad".equalsIgnoreCase(BROWSER_NAME))
            {
                DesiredCapabilities caps = new DesiredCapabilities();
                caps=DesiredCapabilities.ipad();
                caps.setCapability("platformName", "iOS");
                caps.setCapability("platformVersion", "11.0");
                caps.setCapability("deviceName", "iPad Simulator");
                //caps.setCapability("automationName", "XCUITest");
                caps.setCapability("browserName", "Safari");
                driver = new RemoteWebDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
            }
            else if("android".equalsIgnoreCase(BROWSER_NAME)){
                TestMain.mobile = true;
                DesiredCapabilities caps = new DesiredCapabilities();
                caps=DesiredCapabilities.android();
                caps.setCapability("deviceName", "Galaxy J2");
                //   caps.setCapability("udid", "ENUL6303030010"); //Give Device ID of your mobile phone
                caps.setCapability("platformName", "Android");
                caps.setCapability("platformVersion", "5.1");
                caps.setCapability("browserName", "Chrome");
                // caps.setCapability("noReset", "true");
                driver = new RemoteWebDriver(new URL("http://0.0.0.0:4723/wd/hub"), caps);
                opsystem="Android";
                BROWSER_NAME="Chrome";
            }
            else {
                log.warn ("Unknown or unsupported browser: " + BROWSER_NAME);
            }
            driver.manage().deleteAllCookies();
        } catch (Exception e) {
            TestUtils.print_exception("Error opening browser", e, log);
        }
    }
    public void closeBrowser() {
		driver.close();
	}
	public void quitBrowser(){
        driver.quit();
    }
	public void maximizeWindow() {
        /*for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
        }*/
        driver.manage().window().maximize();
    }
	public String takeScreenshot(String pageName) throws IOException {
       // String savedStatus = testStatus;
       // updateTestStatus("Taking screenshot: " + pageName);
		String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		String stamp = timestamp + ".png";
	//	Dimension saved = driver.manage().window().getSize();
//		driver.manage().window().setSize(new Dimension(1280, 2000));
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(scrFile, new File(screenshotsDir + File.separator + BROWSER_NAME + "-" + pageName + "-" + stamp));
//		driver.manage().window().setSize(saved);
       // updateTestStatus(savedStatus);
        return screenshotsDir + File.separator + BROWSER_NAME + "-" + pageName + "-" + stamp;
	}
	public void visitAndTakeScreenshot(String url) throws IOException, InterruptedException {
		visitAndTakeScreenshot(url, 1);
	}
	public void visitAndTakeScreenshot(String url, int waitSecs) throws IOException, InterruptedException {
        openURL (url);
        int idx = url.lastIndexOf ("/");
        String page = (idx >= 0) ? url.substring (idx+1) : url;
		Thread.sleep (waitSecs * 1000);
        takeScreenshot(page);
    }
	// @Then("I verify that I am on page \"(.*?)\"$")
	public void verifyURL(String expectedURL) {
		String currentURL = driver.getCurrentUrl();
		if (!currentURL.contains(expectedURL))
			throw new RuntimeException("Expected URL: " + expectedURL + " actual URL: " + currentURL);
	}
	public void clickOn(String linkText) throws InterruptedException {
		clickOn ("", linkText);
	}
	public void enterPrompt (String value) {
        Alert alert = driver.switchTo().alert();
        alert.sendKeys(value);
        alert.accept();
    }
    // @Given("I find CSS element \"(.*)\" and click on it$")
    public void clickOnCSS(String cssSelector) throws InterruptedException {
        // this could hit any element with the text! e.g. a button, an a tag, or even a td tag!
        String prevURL = driver.getCurrentUrl();
        log.info ("clicking on " + cssSelector);
        waitFor (2);
        WebElement e = driver.findElement(By.cssSelector(cssSelector));
        if (e == null) {
            log.warn("ERROR: CSS element " + cssSelector + " not found!");
            throw new RuntimeException();
        }
        e.click();
        waitFor (2);

        String newURL = driver.getCurrentUrl();
        //if (!prevURL.equals(newURL))
          //  updateTestStatus(); // new page, so status has to be refreshed on it
    }
    public void clickOnCSS(String cssSelector, int index) throws InterruptedException {
        // this could hit any element with the text! e.g. a button, an a tag, or even a td tag!
        String prevURL = driver.getCurrentUrl();
        log.info ("clicking on " + cssSelector);
        waitFor (2);
        List<WebElement> clues = driver.findElements(By.cssSelector(cssSelector));
        WebElement e = clues.get(index);
        if (e == null) {
            log.warn("ERROR: CSS element " + cssSelector + " not found!");
            throw new RuntimeException();
        }
        e.click();
        waitFor (2);

        String newURL = driver.getCurrentUrl();
        if (!prevURL.equals(newURL))
            updateTestStatus(); // new page, so status has to be refreshed on it
    }
    // will click on the link with the exact linkText if available; if not, on a link containing linkText
	// linkText is case insensitive
	// can use as:
	// I click on "Search" --> searches button, link, td tags with this text (or their sub-elements), in that order
	// or
	// I click on button "Search"
	// @Given("I click on (.*) *\"(.*?)\"$")
	public void clickOn(String elementType, String linkText) throws InterruptedException {
        // testStatus = "Clicking on" + ((elementType != null) ? elementType + " " : "") + linkText;

		elementType = elementType.trim(); // required because linkText might come as "button " due to regex matching above
		linkText = resolveValue(linkText);
		linkText = linkText.toLowerCase();
		WebElement e = null;

		// we'll look for linkText in a few specific tags, in this defined order
		// sometimes the text we're looking for is under a further element, like <a><p>...</p></a>
		String searchOrderEType[] = (elementType.length() != 0) ? new String[]{elementType, elementType + "//*"} : new String[]{"button","div//button","div//*", "a", "td", "button//*","a//*", "td//*","span"};

		// prefer to find an exact match first if possible
		// go in order of searchOrderEtype
		// be careful to ignore invisible elements
		// be case-insensitive
		for (String s: searchOrderEType) {
			String xpath = "//" + s + "[translate(text(),  'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '" + linkText + "')]";
			try { e = driver.findElement(By.xpath(xpath)); } catch (Exception e1) { } // ignore the ex, we'll try to find a link containing it
			if (e != null && !e.isDisplayed())
				e = null; // doesn't count if the element is not visible
			if (e != null)
				break;
		}

		// no exact match? try to find a contained match, again in order of searchOrderEtype
		if (e == null) {
			for (String s: searchOrderEType) {
				String xpath = "//" + s + "[contains(translate(text(),  'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + linkText + "')]";
				try { e = driver.findElement(By.xpath(xpath)); } catch (Exception e1) { } // ignore the ex, we'll try to find a link containing it
				if (e != null && !e.isDisplayed())
					e = null; // doesn't count if the element is not visible
				if (e != null)
					break;
			}
		}

        String prevURL = driver.getCurrentUrl();

        // ok, we have an element to click on?
		if (e != null) {
			// color the border red of the selected element to make it easier to understand what is happening
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].style.border = '2px solid red';", e);
			log.info ("Clicking on (" + e.getTagName() + ") containing " + linkText);
			waitFor(1);
            Actions actions = new Actions (driver);
            actions.moveToElement(e);
            waitFor (1);
			e.click(); // seems to be no way of getting text of a link through CSS
			waitFor(1); // always wait for 1 sec after click

            // wait for next page to load by checking its readyState, up to 30 secs
            new WebDriverWait(driver, 30).until((ExpectedCondition<Boolean>) wd ->
                    ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete")); // from http://stackoverflow.com/questions/15122864/selenium-wait-until-document-is-ready

            String newURL = driver.getCurrentUrl();
            if (!prevURL.equals(newURL))
                updateTestStatus();
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].style.border = '2px solid green';", e);
        } else
			throw new RuntimeException ("Unable to find an element to click on: (" + elementType + ") " + linkText + " page: " + driver.getCurrentUrl());
	}
    void updateTestStatus() {
        updateTestStatus(testStatus);
    }
    void updateTestStatus(String status) {
        testStatus = status;
        String script = "var e123 = document.getElementById('test-status'); if (e123 != null) { e123.remove(); }";
        script +=  "document.body.innerHTML += '<div id=\"test-status\" style=\"font-family:sans-serif,serif;position:fixed;bottom:0px; width:100%; text-align:center; font-size:18px; background-color:" + testStatusColor + ";color:white;border-top: solid 2px black; padding: 5px;\">Test status: " + testStatus + "</div>';";
        try { ((JavascriptExecutor) driver).executeScript(script); } catch (Exception e) { }
    }
    // @Then("^I wait for the page (.*?) to be displayed within (\\d+) seconds$")
	public void waitForPageToLoad(String url, int time) {
		url = resolveValue(url);
		long startMillis = System.currentTimeMillis();
		WebDriverWait wait = new WebDriverWait(driver, time);
		try {
			wait.until(ExpectedConditions.urlMatches(url));
            updateTestStatus();
        } catch (org.openqa.selenium.TimeoutException e) {
			throw new RuntimeException (url + " did not open in " + time + " seconds. Exception occurred: ", e);
		}

		log.info ("Page " + url + " loaded in " + (System.currentTimeMillis() - startMillis) + "ms");
	}
	// waits for button containing the given buttonText to appear within time seconds
	// @Then("^I wait for button (.*?) to be displayed within (\\d+) seconds$")
	public void waitForButton(String buttonText, int time) {
		buttonText = resolveValue(buttonText);
		
		long startMillis = System.currentTimeMillis();
		WebDriverWait wait = new WebDriverWait(driver, time);
		try {
			buttonText = buttonText.toLowerCase();
			String xpath = "//*[contains(translate(text(),  'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + buttonText + "')]";

			driver.findElement(By.xpath(xpath)).getText();
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))); // case insensitive match! see
		} catch (org.openqa.selenium.TimeoutException e) {
			throw new RuntimeException ("Button text" + buttonText + " was not found in " + time + " seconds. Exception occured: ", e);
		}

		log.info ("Button " + buttonText + " clickable in " + (System.currentTimeMillis() - startMillis) + "ms");
	}
	// @Given("I switch to the \"(.*)\" tab$")
	public void switchToTab(String title) throws InterruptedException {
        title = title.toLowerCase();
		String parentWindow = driver.getWindowHandle();
		Set<String> handles = driver.getWindowHandles();
		for (String windowHandle : handles) {
			if (!windowHandle.equals(parentWindow)) {
				driver.switchTo().window(windowHandle);
                String tabTitle = driver.getTitle();
                if (tabTitle == null)
                    continue;
                tabTitle = tabTitle.toLowerCase();
				if (title.equals(tabTitle)) {
					tabStack.push(parentWindow);
					return;
				}
			}
		}
		log.warn ("Error: tab with title " + title + " not found!");
		// title not found? return to parentWindow
		driver.switchTo().window(parentWindow);
	}
    // @Given("I switch to the \"(.*)\" tab$")
    public void switchToNextTab() throws InterruptedException {

        String parentWindow = driver.getWindowHandle();
        Set<String> handles = driver.getWindowHandles();
        for (String windowHandle : handles) {
            if (!windowHandle.equals(parentWindow)) {
                driver.switchTo().window(windowHandle);
                tabStack.push(parentWindow);
                log.info("switching to next tab!");

                return;
            }
        }

        log.warn ("Error: no other tab found!");
        // title not found? return to parentWindow
        driver.switchTo().window(parentWindow);
    }
	// @Given("I close tab")
	public void closeTab() throws InterruptedException {
		driver.close();
		// need to explicitly switch to last window, otherwise driver will stop working

        log.info("Size of tabstack:"+tabStack.size());
        Set handles=driver.getWindowHandles();
        Iterator<String> iter=handles.iterator();
        while(iter.hasNext()) {
            String window=iter.next();
            tabStack.push(window);
        }
		if (tabStack.size() > 0) {
			String s = tabStack.pop();
			log.info("Switching to:"+s);
			driver.switchTo().window(s);
		}
	}
	// @Given("I switch to the previous tab")
	public void switchTabBack() throws InterruptedException {
		if (tabStack.size() < 1) {
			log.warn ("Warning: trying to pop tab stack when it is empty!");
			return;
		}

		String lastWindow = tabStack.pop();
		switchToTab (lastWindow);
	}
	// @Given("I mark all messages \"Do not transfer\"")
	public void markDNT() throws InterruptedException {
		WebElement e = driver.findElement(By.id("doNotTransfer"));

		if (!e.getAttribute("class").contains("flag-enabled")) {
			driver.findElement(By.id("doNotTransfer")).click();
			waitFor(1);
		}

		driver.findElement(By.id("applyToAll")).click();
	}
	// @Given("I set dropdown \"(.*?)\" to \"(.*?)\"$")
	public void dropDownSelection(String cssSelector, String value) throws InterruptedException {
        WebElement element = driver.findElement(By.cssSelector(cssSelector));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].style.border = '2px solid red';", element);
		Select select = new Select(element);
        waitFor (2);
        select.selectByVisibleText(value);
	}
	// @Then("I confirm the alert$")
	public void confirmAlert() throws InterruptedException {
		Thread.sleep(5000);
		Alert alert = driver.switchTo().alert();
		alert.accept();
	}
	// @Then("I verify the folder (.*) does not exist$")
	public void checkFolderDoesNotExist(String folderName) throws InterruptedException, IOException {
		folderName = resolveValue(folderName);
		if (new File(folderName).exists()) {
			throw new RuntimeException ("Folder " + folderName + " is not expected to exist, but it does!");
		}
		log.info ("Good, folder " + folderName + " does not exist");
	}
	// @Then("I verify the folder (.*) exists$")
	public void checkFolderExists(String folderName) throws InterruptedException, IOException {
		folderName = resolveValue(folderName);
		if (!new File(folderName).exists()) {
			throw new RuntimeException ("Folder " + folderName + " is expected to exist, but it does not!");
		}
		log.info ("Good, folder " + folderName + " exists");
	}
	// if the value is <abc> then we read the value of property abc in the hook. otherwise we use it as is.
	public String resolveValue(String s) {
		if (s == null)
			return null;
		s = s.trim(); // strip spaces before and after
		if (s.startsWith("<") && s.endsWith(">"))
			s = TestReport.VARS.getProperty(s.substring(1, s.length()-1));
		if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) // strip quotes -- if "abc", simply make it abc
			s = s.substring(1, s.length()-1);
		return s;
	}
	public void isNotVisible(String selector){
        if( driver.findElement(By.cssSelector(selector)).isDisplayed()){
            throw new RuntimeException ("Element is Visible");
        }
           log.info("Element is Invisible");
    }
    public void isVisible(String selector){
        if( !driver.findElement(By.cssSelector(selector)).isDisplayed()){
            throw new RuntimeException ("Element is Invisible");
        }
        log.info("Element is Visible");
    }
    public void checkAttributeContains(String selector, String attribute, String attribute_value, int index) {
        List<WebElement> clues = driver.findElements(By.cssSelector(selector));
        WebElement e = clues.get(index);
        String value = e.getAttribute(attribute);
        if (!(value.contains(attribute_value))){
            throw new RuntimeException (selector+" does not have "+attribute_value+" as its "+attribute+" but has "+value);
        }
        log.info(selector+" has the expected attribute value.");
    }
    public void checkAttributeDoesNotContain(String selector, String attribute, String attribute_value, int index) {
        List<WebElement> clues = driver.findElements(By.cssSelector(selector));
        WebElement e = clues.get(index);
        String value = e.getAttribute(attribute);
        if (value.contains(attribute_value)){
            throw new RuntimeException (selector+" has the expected attribute value.");
        }
        log.info(selector+" "+attribute+" does not contain "+attribute_value);
    }
    public void refreshPage() {
        driver.navigate().refresh();
    }
    public void isElementPresent(String selector){
        if(driver.findElements(By.cssSelector(selector)).size()==0)
            throw new RuntimeException("Element not present");
        log.info("Element is present.");
    }
    public void isElementNotPresent(String selector){
        if(driver.findElements(By.cssSelector(selector)).size()!=0)
            throw new RuntimeException("Element is present");
        log.info("Element not present.");
    }
    public void isSelected(String selector){
        if(!driver.findElement(By.cssSelector(selector)).isSelected())
            throw new RuntimeException(selector+" is not selected.");
        else
            log.info(selector+" is selected.");
    }
}
