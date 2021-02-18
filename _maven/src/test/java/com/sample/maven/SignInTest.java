package com.sample.maven;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Listeners(customlisteners.class)
public class SignInTest {

    WebDriver driver;
    WebDriverWait wait;
    SoftAssert a = new SoftAssert();

    Properties prop = new Properties();
    FileInputStream fs = new FileInputStream("./data.properties");

    ExtentReports extent;

    private static final Logger log = LogManager.getLogger(SignInTest.class.getName());

    public SignInTest() throws FileNotFoundException {
    }

    @BeforeSuite
    public WebDriver driverInitialize() throws IOException {

        prop.load(fs);
        String driverType = prop.getProperty("browser");
        String website = prop.getProperty("url");

        //Extent Reports
        String path = System.getProperty("user.dir")+"\\testReport\\index.html";
        ExtentSparkReporter reporter = new ExtentSparkReporter(path);
        reporter.config().setReportName("Web Automation");
        reporter.config().setDocumentTitle("Test Results");

        extent = new ExtentReports();
        extent.attachReporter(reporter);
        extent.setSystemInfo("Tester","Shazeen Mahmood");

        //Lauaching browser depending on data.properties input
        if(driverType.equalsIgnoreCase("chrome"))
        {
            //setting chrome driver path
            System.setProperty("webdriver.chrome.driver","./Driver/chromedriver.exe");
            //initializing driver with Chrome Driver
            driver = new ChromeDriver();
        }

        else if(driverType.equalsIgnoreCase("firefox"))
        {
            //setting gecko driver path
            System.setProperty("webdriver.firefox.driver","./Driver/geckodriver.exe");
            //initializing driver with Firefox Driver
            driver = new FirefoxDriver();
        }

        //initializing wait
        wait = new WebDriverWait(driver, 5);

        driver.manage().window().maximize();
        driver.get(website);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username-input")));

        //disabling notifications
        ChromeOptions co = new ChromeOptions();
        co.addArguments("--disable-notifications");

        return driver;
    }

    @Test(dataProvider = "getData")
    public void signIn(String username, String password) throws IOException {

        extent.createTest("Login");
        //input username and password
        if(driver.findElement(By.id("username-input")).isDisplayed())
        {
            driver.findElement(By.id("username-input")).sendKeys(username);
            log.debug("Username is added in field");
        }
        else
        {
            log.error("Username field is not visible");
        }

        if(driver.findElement(By.id("password-input")).isDisplayed())
        {
            driver.findElement(By.id("password-input")).sendKeys(password);
            log.debug("Password is added in field");
        }
        else
        {
            log.error("Password field is not visible");
        }

        if(driver.findElement(By.xpath("//div[@class = 'tb-action-button']/button")).isDisplayed())
        {
            driver.findElement(By.xpath("//div[@class = 'tb-action-button']/button")).click();
            log.debug("Sign In button is clicked");
        }
        else
        {
            log.error("Sign In button is not visible");
        }

        //scrolling for error message
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.querySelector('mat-card.mat-card.mat-focus-indicator').scrollTop = 50");
        log.debug("Page scrolling");

        //taking screenshot of error message
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(src,new File("C:\\Shazeen\\authFail.png"));
        log.debug("Screenshot of page taken");

        //Verify error message
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("tb-snack-bar-component[class *= 'ng-tns-c165-']")));
        if(driver.findElement(By.cssSelector("tb-snack-bar-component[class *= 'ng-tns-c165-']")).isDisplayed())
        {
            a.assertEquals(driver.findElement(By.cssSelector("tb-snack-bar-component[class *= 'ng-tns-c165-']  div div")).getText(),"Authentication failed");
            log.debug("Error message displayed - Authentication failed ");
            driver.findElement(By.cssSelector("tb-snack-bar-component[class *= 'ng-tns-c165-'] button")).click();
        }
        else
        {
            log.error("Error message not displayed");
        }


        //clearing input fields
        driver.findElement(By.id("username-input")).clear();
        driver.findElement(By.id("password-input")).clear();
        log.debug("Fields cleared");

        //displaying assert
        a.assertAll();
    }

    @Test(priority=1)
    public void forgotPassword() {

        extent.createTest("Password");

        //navigate to Forgot Password screen
        driver.findElement(By.cssSelector("button[class*='tb-reset-password']")).click();
        log.debug("Navigating to Forgot Password screen");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("mat-headline")));
        a.assertEquals(driver.findElement(By.className("mat-headline")).getText(), "Request Password Reset");

        //sending input to email field
        if(driver.findElement(By.cssSelector("input[id*='mat-input']")).isDisplayed())
            {
                driver.findElement(By.cssSelector("input[id*='mat-input']")).sendKeys("abs");
                log.debug("Email is entered");
            }
        else
            {
                log.error("Email not entered");
            }

        driver.findElement(By.cssSelector("button[type ='submit']")).click();
        log.debug("Button clicked");

        //verifying error message
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-error[id*='mat-error']")));
        if(driver.findElement(By.cssSelector("mat-error[id*='mat-error']")).isDisplayed())
        {
            a.assertEquals(driver.findElement(By.cssSelector("mat-error[id*='mat-error']")).getText(), "Invalid email format.");
            log.debug("Error message displayed - Invalid email format");
        }
        else
        {
            log.error("Error message not displayed");
        }

    /*    //Taking screenshot
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(src, new File("C://Shazeen//forgotPasswordError.png"));
        test.addScreenCaptureFromBase64String("C://Shazeen//forgotPasswordError.png");*/

        //navigate back to sign in and verifying Login text
        driver.findElement(By.cssSelector("button[class *= 'mat-primary']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span[class='tb-login-message']")));
        if(driver.findElement(By.cssSelector("span[class='tb-login-message']")).isDisplayed())
        {
            a.assertEquals(driver.findElement(By.cssSelector("span[class='tb-login-message']")).getText(),"Log in to see ThingsBoard in action.");
            log.debug("Page text - Log in to see ThingsBoard in action");
        }
        else
        {
            log.error("Not navigated to page");
        }
        a.assertAll();
    }

    //adding sample test for failure to execute listeners method
    @Test(priority = 2)
    public void failureTest()
    {
        extent.createTest("Failure Test");
        a.assertTrue(false);
        log.error("Test error message");
    }

    //adding a skip test to execute listeners method
    @Test (priority = 3)
    public void skipTest()
    {
        extent.createTest("Skip Test");
        throw new SkipException("This is a skip exception...");
    }

    @AfterTest
    public void driverQuit()
    {
        //deleting cookies
        driver.manage().deleteAllCookies();

        //closing driver
        driver.quit();
    }

    @DataProvider
    public Object[][] getData()
    {
        Object[][] data = new Object[3][2];
        //first iteration
        data[0][0] = "shaz@dfsd.com";
        data[0][1] = "mah";

        //second iteration
        data[1][0] = "asb@sdfs.com";
        data[1][1] = "fss";

        //third iteration
        data[2][0] = "the@te.com";
        data[2][1]  = "wer";

        return data;
    }


}
