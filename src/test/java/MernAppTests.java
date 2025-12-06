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
    private static final String BASE_URL = "http://13.61.134.227:8082/signup";
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
@DisplayName("View Job Button shows job popup")
public void testViewJobPopup() {
    // Sign in first
    driver.get("http://13.61.134.227:8082/signin");

    WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
    emailInput.sendKeys("ibrahim@gmail.com");

    WebElement passwordInput = driver.findElement(By.id("password"));
    passwordInput.sendKeys("123456");

    driver.findElement(By.id("signin")).click();
    wait.until(ExpectedConditions.alertIsPresent()).accept(); // close login alert

    // Click "View Job" button
    WebElement viewJobButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".job-button")));
    viewJobButton.click();

    // Check if popup appears
    WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".popup")));
    assertTrue(popup.isDisplayed(), "Job popup should be visible");
}

@Test
@DisplayName("Close Job Popup hides the popup")
public void testCloseJobPopup() {
    // Sign in and open popup first
    driver.get("http://13.61.134.227:8082/signin");

    driver.findElement(By.id("email")).sendKeys("ibrahim@gmail.com");
    driver.findElement(By.id("password")).sendKeys("123456");
    driver.findElement(By.id("signin")).click();
    wait.until(ExpectedConditions.alertIsPresent()).accept();

    driver.findElement(By.cssSelector(".job-button")).click();
    WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='popup']//button[text()='Close']")));
    closeButton.click();

    // Verify popup is hidden
    assertTrue(driver.findElements(By.cssSelector(".popup")).isEmpty() ||
               !driver.findElement(By.cssSelector(".popup")).isDisplayed(),
               "Job popup should be hidden after clicking Close");
}


    
}
