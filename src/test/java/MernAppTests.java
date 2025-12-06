
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MERN Application Selenium Tests")
public class MernAppTests {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://13.61.134.227:8082";
    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();

        // ADD THIS LINE FOR HEADLESS
        options.addArguments("--headless=new");

        // These are REQUIRED to avoid 403 in headless
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");  // Important in headless!
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36");

        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        driver = new ChromeDriver(options);

        // Hide webdriver (critical!)
        ((JavascriptExecutor) driver).executeScript(
            "Object.defineProperty(navigator, 'webdriver', {get: () => undefined});"
        );

        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    // —————————————————————— ALL YOUR WORKING TESTS ——————————————————————


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
    @DisplayName("Cannot apply without CV")
    public void testCannotApplyWithoutCV() {
        loginAsJobseeker(); // Uses ibrahim@gmail.com → we assume this account has NO CV

        // Go directly to My CV page — if we see "No CV found", we are good
        driver.findElement(By.xpath("//button[text()='My CV']")).click();

        // If CV exists → skip this test (we don't want flaky failures)
        // If "No CV found" → proceed to apply
        try {
            WebElement noCvMessage = driver.findElement(By.xpath("//div[contains(text(),'No CV found')]"));
            // CV doesn't exist → perfect, continue
        } catch (NoSuchElementException e) {
            // CV exists → skip the test gracefully (not a failure)
            org.junit.jupiter.api.Assumptions.assumeTrue(false, 
                "Skipping test: CV already exists for ibrahim@gmail.com — cannot test 'Create a CV first!'");
        }

        // Now go back to jobs and try to apply
        driver.findElement(By.xpath("//button[text()='Home']")).click();

        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(text(),'View Job')]")
        )).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("popup-overlay")));
        driver.findElement(By.xpath("//button[text()='Apply']")).click();

        // Expected result
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Create a CV first!", alert.getText());
        alert.accept();
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
            .sendKeys("ibrahim@hotmail.com");
        driver.findElement(By.id("password")).sendKeys("Santa@2242");
        driver.findElement(By.id("signin")).click();

        Alert a = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Logged in successfully!", a.getText());
        a.accept();
    }
}
