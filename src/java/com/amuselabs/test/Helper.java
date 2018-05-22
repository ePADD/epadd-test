package com.amuselabs.test;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
//import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

//import java.io.File;
//import java.io.IOException;
//import java.util.Properties;
import java.sql.Driver;
import java.util.Set;

public class Helper
{
   /* private WebDriver driver;
    static Properties VARS;
    public static String BASE_DIR;
    public static String testStatus = "1...2...3", testStatusColor = "rgba(10,140,10,0.8)";
    private static String EPADD_TEST_PROPS_FILE = System.getProperty("user.home") + File.separator + "epadd.test.properties";
    private static Log log = LogFactory.getLog(StepDefs.class);
    private Process epaddProcess = null;*/

    /* public void open_Epadd(String mode) throws IOException, InterruptedException {
         // we'll always launch using epadd-standalone.jar
         updateTestStatus("Starting ePADD");

         String errFile = System.getProperty("java.io.tmpdir") + File.separator + "epadd-test.err.txt";
         String outFile = System.getProperty("java.io.tmpdir") + File.separator + "epadd-test.out.txt";
         String cmd = VARS.getProperty ("cmd");
         if (cmd == null) {
             log.warn ("Please confirm cmd in " + EPADD_TEST_PROPS_FILE);
             throw new RuntimeException ("no command to start epadd");
         }

         cmd = "java -Depadd.mode=" + mode +  " -Depadd.base.dir=" + BASE_DIR + " " + cmd;
         cmd = cmd + " --no-browser-open"; // we'll open our own browser
         ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));

 //		ProcessBuilder pb = new ProcessBuilder("java", "-Xmx2g", "-jar", "epadd-standalone.jar", "--no-browser-open");
         pb.redirectError(new File(errFile));
         pb.redirectOutput(new File(outFile));
         log.info ("Sending epadd output to: " + outFile);
         epaddProcess = pb.start();
         log.info ("Started ePADD");
     }*/
   /* void updateTestStatus(String status) {
        testStatus = status;
        String script = "var e123 = document.getElementById('test-status'); if (e123 != null) { e123.remove(); }";
        script +=  "document.body.innerHTML += '<div id=\"test-status\" style=\"font-family:sans-serif,serif;position:fixed;bottom:0px; width:100%; text-align:center; font-size:18px; background-color:" + testStatusColor + ";color:white;border-top: solid 2px black; padding: 5px;\">Test status: " + testStatus + "</div>';";
        try { ((JavascriptExecutor) driver).executeScript(script); } catch (Exception e) { }
    }*/
    public String strings_From(WebDriver driver)
    {
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
        StepDefs sf = new StepDefs();
        try {
            sf.waitFor(3);
        } catch (InterruptedException e) {

        }
        /*driver.navigate().refresh();
        StepDefs sf = new StepDefs();
        try {
            sf.waitFor(3);
        } catch (InterruptedException e) {

        }*/
        WebElement e=driver.findElement(By.xpath("//*[@id=\"jog_contents\"]/div[2]/div[1]/table/tbody/tr[2]/td[2]/a"));
        String onClickValue=e.getText();
        return onClickValue;
    }

}