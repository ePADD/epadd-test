package com.amuselabs.test;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by charu on 12/20/17.
 */
public class TestMain {
    String reportsDir;
    String timestamp;
    String time;
    ArrayList<TestReport> testReports;
    static boolean mobile = false;
    String email;

    public TestMain() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        Date date = new Date();
        time = dateFormat.format(date);
        timestamp = dateFormat1.format(date);
        reportsDir = System.getProperty("user.home") + File.separator + "pm-test" + File.separator + "reports" + File.separator + "pm-test-report_" + timestamp;
        new File(reportsDir).mkdirs();
        testReports = new ArrayList<>();
    }

    // Uses java.util.zip to create zip file
    private void zipFolder(Path sourceFolderPath, Path zipPath) throws Exception {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
        Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
                Files.copy(file, zos);
                zos.closeEntry();
                return FileVisitResult.CONTINUE;
            }
        });
        zos.close();
    }

    public static void main(String args[]) throws IOException {
        TestMain TestMain = new TestMain();
        if (args[0].equalsIgnoreCase("-help")) {
            System.out.println("Welcome to AmuseLabs Automation Testing Software.");
            System.out.println("To run the software, type java -jar <platform> <email> <options>");
            System.out.println("The options allowed are as follows:");
            System.out.println(String.format("%-20s %s", "-a", "All tests"));
            System.out.println(String.format("%-20s %s", "-wapo", "Washington Post Tests"));
            System.out.println(String.format("%-20s %s", "-nd", "NewsDay tests"));
            System.out.println(String.format("%-20s %s", "-picker", "Picker tests"));
            System.out.println(String.format("%-20s %s", "-init", "Player initialization tests"));
            System.out.println(String.format("%-20s %s", "-player", "Player tests (same as -navbar, -cookies, -settings, -error, -check, -reveal, -keys, -rebus, -circle, -print)"));
            System.out.println(String.format("%-20s %s", "-navbar", "Reset and Info tests"));
            System.out.println(String.format("%-20s %s", "-cookies", "Refresh tests"));
            System.out.println(String.format("%-20s %s", "-settings", "Settings tests"));
            System.out.println(String.format("%-20s %s", "-error", "Error Check Mode tests"));
            System.out.println(String.format("%-20s %s", "-check", "Check tests"));
            System.out.println(String.format("%-20s %s", "-reveal", "Reveal tests"));
            System.out.println(String.format("%-20s %s", "-keys", "Keys tests"));
            System.out.println(String.format("%-20s %s", "-rebus", "Rebus tests"));
            System.out.println(String.format("%-20s %s", "-circle", "Circled letters tests"));
            System.out.println(String.format("%-20s %s", "-print", "Print tests"));

        } else {
            TestMain.testReports.add(new TestReport(TestMain.reportsDir, args[0], args));
            TestMain.email = args[1];
            //TestMain.testReports.add(new TestReport(TestMain.puzzleLink, TestMain.reportsDir, "safari"));

            for (int i = 0; i < TestMain.testReports.size(); ++i) {
                TestMain.testReports.get(i).runTests();
                TestMain.testReports.get(i).printReport();
            }
            //TestMain.sendMail();
        }

    }

    public void sendMail() {
        final String username = "charu@amuselabs.com";
        final String password = "mypassword123"; //working after entering credentials

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("charu@amuselabs.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.setSubject("Test Automation Report " + time);
            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();
            String s = "<h2> Test Report Summary </h2>";
            for (int i = 0; i < testReports.size(); ++i) {
                s += "<b> OS Name: </b>" + StepDefs.opsystem + "<br>";
                s += "<b> Browser Name: </b>" + testReports.get(i).browserName + "<br>";
                s += "<font color=\"green\"> Number of Tests Passed:" + testReports.get(i).nPassed + "/" + (testReports.get(i).nFailed + testReports.get(i).nPassed + testReports.get(i).nUndetermined) + "</font> <br>";
                s += "<font color=\"red\"> Number of Tests Failed:" + testReports.get(i).nFailed + "/" + (testReports.get(i).nFailed + testReports.get(i).nPassed + testReports.get(i).nUndetermined) + "</font> <br>";
                s += "<font color=\"blue\"> Number of Tests requiring manual inspection:" + testReports.get(i).nUndetermined + "/" + (testReports.get(i).nFailed + testReports.get(i).nPassed + testReports.get(i).nUndetermined) + "</font> <br><br><br>";
            }
            s += " Please see attachment for detailed reports.";
            // Fill the message
            messageBodyPart.setContent(s, "text/html");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);
            String folderToZip = reportsDir;
            String zipName = reportsDir + ".zip";
            zipFolder(Paths.get(folderToZip), Paths.get(zipName));

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            String filename = timestamp + ".zip";
            DataSource source = new FileDataSource(zipName);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);


            Transport.send(message);

            System.out.println("Done");
            File f = new File(zipName);
            f.delete();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
