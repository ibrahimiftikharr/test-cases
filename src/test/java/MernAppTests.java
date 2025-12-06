import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.Alert;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MERN Application Selenium Tests")
public class MernAppTests {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://13.61.134.227:8082";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); 
        options.addArguments("--no-sandbox"); 
        options.addArguments("--disable-dev-shm-usage"); 
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--user-data-dir=/tmp/unique-dir");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, TIMEOUT);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }


  @Test
@DisplayName("Signup with Valid Information shows success alert")
public void testSignupWithValidInformation() {
    driver.get(BASE_URL);

    WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
    String uniqueEmail = "user" + System.currentTimeMillis() + "@example.com";
    emailInput.sendKeys(uniqueEmail);

    WebElement passwordInput = driver.findElement(By.id("password"));
    passwordInput.sendKeys("SecurePass123");

    WebElement jobseekerRadio = driver.findElement(By.id("jobseeker"));
    jobseekerRadio.click();

    WebElement signupButton = driver.findElement(By.cssSelector("button.btn-color"));
    signupButton.click();

    // Handle alert
    Alert alert = wait.until(ExpectedConditions.alertIsPresent());
    String alertText = alert.getText();
    assertEquals("Signup successful! PRESS SIGN IN NOW!", alertText);
    alert.accept(); // close the alert
}


@Test
@DisplayName("Signup with missing email shows error alert")
public void testSignupWithMissingEmail() {
    driver.get(BASE_URL);

    WebElement passwordInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("password")));
    passwordInput.sendKeys("SecurePass123");

    WebElement jobseekerRadio = driver.findElement(By.id("jobseeker"));
    jobseekerRadio.click();

    WebElement signupButton = driver.findElement(By.cssSelector("button.btn-color"));
    signupButton.click();

    Alert alert = wait.until(ExpectedConditions.alertIsPresent());
    String alertText = alert.getText();
    assertEquals("One or more fields are missing!", alertText);
    alert.accept();
}

@Test
@DisplayName("Signin with valid jobseeker credentials shows success alert")
public void testSigninValidJobseeker() {
    driver.get("http://13.61.134.227:8082/signin");

    WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
    emailInput.sendKeys("ibrahim@gmail.com");

    WebElement passwordInput = driver.findElement(By.id("password"));
    passwordInput.sendKeys("123456");

    WebElement signinButton = driver.findElement(By.id("signin"));
    signinButton.click();

    // Wait for alert
    Alert alert = wait.until(ExpectedConditions.alertIsPresent());
    String alertText = alert.getText();
    assertEquals("Logged in successfully!", alertText);
    alert.accept();
}


@Test
@DisplayName("Signin with invalid email shows alert")
public void testSigninInvalidEmail() {
    driver.get("http://13.61.134.227:8082/signin");

    WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
    emailInput.sendKeys("nonexistent@example.com");

    WebElement passwordInput = driver.findElement(By.id("password"));
    passwordInput.sendKeys("123456");

    WebElement signinButton = driver.findElement(By.id("signin"));
    signinButton.click();

    Alert alert = wait.until(ExpectedConditions.alertIsPresent());
    String alertText = alert.getText();
    assertEquals("Error: jobseeker with this email not found", alertText);
    alert.accept();
}


@Test
@DisplayName("Signin with invalid password shows alert")
public void testSigninInvalidPassword() {
    driver.get("http://13.61.134.227:8082/signin");

    WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
    emailInput.sendKeys("ibrahim@gmail.com");

    WebElement passwordInput = driver.findElement(By.id("password"));
    passwordInput.sendKeys("wrongpassword");

    WebElement signinButton = driver.findElement(By.id("signin"));
    signinButton.click();

    Alert alert = wait.until(ExpectedConditions.alertIsPresent());
    String alertText = alert.getText();
    assertEquals("Error: Invalid password", alertText);
    alert.accept();
}

    @Test
    @DisplayName("Click 'View Job' opens popup with full job description")
    public void testViewJobOpensPopup() {
        loginAsJobseeker();
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(text(),'View Job')]"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("popup-overlay")));
        WebElement popup = driver.findElement(By.className("popup"));

        assertEquals("Teacher", popup.findElement(By.tagName("h2")).getText());
        String text = popup.getText();
        assertTrue(text.contains("APSACS"));
        assertTrue(text.contains("Mathematics teacher"));
        assertTrue(text.contains("Islamabad"));
        assertTrue(text.contains("60000"));
        assertTrue(text.contains("Part Time"));
    }

    @Test
    @DisplayName("My CV page shows 'No CV found' initially")
    public void testMyCVPageShowsNoCVMessage() {
        loginAsJobseeker();
        driver.findElement(By.xpath("//button[text()='My CV']")).click();
        String msg = driver.findElement(By.xpath("//div[contains(text(),'No CV found')]")).getText();
        assertEquals("No CV found. Please create one.", msg.trim());
    }

    @Test
    @DisplayName("All Jobs page shows Teacher job")
    public void testAllJobsPageDisplaysJobListing() {
        loginAsJobseeker();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("jobsList")));
        String text = driver.findElement(By.cssSelector("ul.jobsList > li")).getText();
        assertTrue(text.contains("Teacher"));
        assertTrue(text.contains("APSACS"));
        assertTrue(text.contains("Islamabad, Pakistan"));
        assertTrue(text.contains("60000"));
    }

    @Test
    @DisplayName("New user cannot apply without CV → 'Create a CV first!'")
    public void testNewUserCannotApplyWithoutCV() {
        String email = "user" + System.currentTimeMillis() + "@test.com";

        driver.get(BASE_URL + "/signup");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email"))).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.id("jobseeker")).click();
        driver.findElement(By.cssSelector("button.btn-color")).click();

        Alert a = wait.until(ExpectedConditions.alertIsPresent());
        assertTrue(a.getText().contains("success"));
        a.accept();

        driver.findElement(By.id("signin")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email"))).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.id("signin")).click();

        a = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Logged in successfully!", a.getText());
        a.accept();

        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(text(),'View Job')]"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("popup-overlay")));
        driver.findElement(By.xpath("//button[text()='Apply']")).click();

        a = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Create a CV first!", a.getText());
        a.accept();
    }

    @Test
    @DisplayName("Logout works correctly")
    public void testLogoutSuccessfully() {
        loginAsJobseeker();
        driver.findElement(By.xpath("//button[text()='Log Out']")).click();

        Alert a = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Logout Successful!", a.getText());
        a.accept();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("signin")));
        assertTrue(driver.getCurrentUrl().contains("/signin"));
    }

    // —————————————————————— HELPER ——————————————————————
    private void loginAsJobseeker() {
        driver.get(BASE_URL + "/signin");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")))
            .sendKeys("ibrahim@gmail.com");
        driver.findElement(By.id("password")).sendKeys("123456");
        driver.findElement(By.id("signin")).click();

        Alert a = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Logged in successfully!", a.getText());
        a.accept();
    }


    
}
