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
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MERN Application Selenium Tests")
public class MernAppTests {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://13.61.134.227:8082/";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, TIMEOUT);
        driver.manage().window().maximize();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ============== LOGIN TESTS ==============

    @Test
    @DisplayName("Login with Valid Credentials")
    public void testLoginWithValidCredentials() {
        driver.get(BASE_URL + "/login");
        
        // Wait for login form to load
        WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("loginEmail")));
        assertNotNull(emailInput, "Email input should be present");
        
        // Enter credentials
        emailInput.clear();
        emailInput.sendKeys("testuser@example.com");
        
        WebElement passwordInput = driver.findElement(By.id("loginPassword"));
        assertNotNull(passwordInput, "Password input should be present");
        passwordInput.clear();
        passwordInput.sendKeys("Password123");
        
        // Click login button
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit'].loginButton"));
        assertNotNull(loginButton, "Login button should be present");
        loginButton.click();
        
        // Wait for redirect to dashboard
        WebElement dashboardTitle = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//h1[contains(text(), 'Dashboard')]"))
        );
        assertNotNull(dashboardTitle, "Should be redirected to dashboard");
        
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/dashboard"), "URL should contain /dashboard");
    }

    @Test
    @DisplayName("Login with Invalid Email")
    public void testLoginWithInvalidEmail() {
        driver.get(BASE_URL + "/login");
        
        WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("loginEmail")));
        emailInput.sendKeys("invalidemail@notfound.com");
        
        WebElement passwordInput = driver.findElement(By.id("loginPassword"));
        passwordInput.sendKeys("Password123");
        
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit'].loginButton"));
        loginButton.click();
        
        // Wait for error message
        WebElement errorMessage = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("errorMessage"))
        );
        assertNotNull(errorMessage, "Error message should appear");
        assertTrue(errorMessage.isDisplayed(), "Error message should be visible");
        String errorText = errorMessage.getText();
        assertTrue(errorText.contains("not found") || errorText.contains("Invalid"), 
                   "Error message should indicate invalid credentials");
    }

    @Test
    @DisplayName("Login with Empty Password Field")
    public void testLoginWithEmptyPassword() {
        driver.get(BASE_URL + "/login");
        
        WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("loginEmail")));
        emailInput.sendKeys("testuser@example.com");
        
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit'].loginButton"));
        loginButton.click();
        
        // Check for validation error
        WebElement validationError = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("fieldError"))
        );
        assertNotNull(validationError, "Validation error should appear for empty password");
        assertTrue(validationError.isDisplayed(), "Validation error should be visible");
    }

    // ============== SIGNUP TESTS ==============

    @Test
    @DisplayName("Signup with Valid Information")
    public void testSignupWithValidInformation() {
        driver.get(BASE_URL + "/signup");
        
        // Fill signup form
        WebElement nameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("fullName")));
        nameInput.sendKeys("John Doe");
        
        WebElement emailInput = driver.findElement(By.name("email"));
        String uniqueEmail = "newuser" + System.currentTimeMillis() + "@example.com";
        emailInput.sendKeys(uniqueEmail);
        
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys("SecurePass123");
        
        WebElement confirmPasswordInput = driver.findElement(By.name("confirmPassword"));
        confirmPasswordInput.sendKeys("SecurePass123");
        
        // Select user type
        WebElement jobSeekerRadio = driver.findElement(By.cssSelector("input[value='jobseeker']"));
        jobSeekerRadio.click();
        
        // Submit form
        WebElement signupButton = driver.findElement(By.cssSelector("button[type='submit'].signupButton"));
        signupButton.click();
        
        // Wait for success message or redirect
        WebElement successMessage = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("successMessage"))
        );
        assertNotNull(successMessage, "Success message should appear");
        assertTrue(successMessage.isDisplayed(), "Success message should be visible");
    }

    @Test
    @DisplayName("Signup with Password Mismatch")
    public void testSignupWithPasswordMismatch() {
        driver.get(BASE_URL + "/signup");
        
        WebElement nameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("fullName")));
        nameInput.sendKeys("Jane Doe");
        
        WebElement emailInput = driver.findElement(By.name("email"));
        emailInput.sendKeys("janedoe" + System.currentTimeMillis() + "@example.com");
        
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys("SecurePass123");
        
        WebElement confirmPasswordInput = driver.findElement(By.name("confirmPassword"));
        confirmPasswordInput.sendKeys("DifferentPass456");
        
        WebElement signupButton = driver.findElement(By.cssSelector("button[type='submit'].signupButton"));
        signupButton.click();
        
        // Check for error message
        WebElement errorMessage = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("errorMessage"))
        );
        assertNotNull(errorMessage, "Error message should appear");
        String errorText = errorMessage.getText();
        assertTrue(errorText.contains("match") || errorText.contains("password"), 
                   "Error should mention password mismatch");
    }

    @Test
    @DisplayName("Signup with Existing Email")
    public void testSignupWithExistingEmail() {
        driver.get(BASE_URL + "/signup");
        
        WebElement nameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("fullName")));
        nameInput.sendKeys("Existing User");
        
        WebElement emailInput = driver.findElement(By.name("email"));
        emailInput.sendKeys("testuser@example.com"); // Already exists
        
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys("SecurePass123");
        
        WebElement confirmPasswordInput = driver.findElement(By.name("confirmPassword"));
        confirmPasswordInput.sendKeys("SecurePass123");
        
        WebElement signupButton = driver.findElement(By.cssSelector("button[type='submit'].signupButton"));
        signupButton.click();
        
        // Check for duplicate email error
        WebElement errorMessage = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("errorMessage"))
        );
        assertNotNull(errorMessage, "Error message should appear");
        String errorText = errorMessage.getText();
        assertTrue(errorText.contains("already exists") || errorText.contains("Email"), 
                   "Error should indicate email already exists");
    }

    // ============== FORM SUBMISSION TESTS ==============

    @Test
    @DisplayName("Submit Job Post Form Successfully")
    public void testSubmitJobPostFormSuccessfully() {
        // Assuming user is already logged in, navigate to job posting page
        driver.get(BASE_URL + "/dashboard/post-job");
        
        WebElement jobTitleInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("jobTitle")));
        jobTitleInput.sendKeys("Senior Java Developer");
        
        WebElement jobDescriptionInput = driver.findElement(By.id("jobDescription"));
        jobDescriptionInput.sendKeys("We are looking for an experienced Java developer with 5+ years of experience in building scalable applications.");
        
        WebElement salaryInput = driver.findElement(By.name("salary"));
        salaryInput.sendKeys("100000");
        
        // Select location from dropdown
        WebElement locationSelect = driver.findElement(By.id("jobLocation"));
        locationSelect.click();
        WebElement locationOption = driver.findElement(By.cssSelector("option[value='New York']"));
        locationOption.click();
        
        // Submit form
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit'].submitJobButton"));
        submitButton.click();
        
        // Verify success
        WebElement successNotification = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("successNotification"))
        );
        assertNotNull(successNotification, "Success notification should appear");
        assertTrue(successNotification.isDisplayed(), "Success notification should be visible");
        
        // Verify redirect
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/jobs") || currentUrl.contains("/dashboard"), 
                   "Should redirect after successful submission");
    }

    @Test
    @DisplayName("Submit Job Application Form")
    public void testSubmitJobApplicationForm() {
        driver.get(BASE_URL + "/jobs/12345"); // Job details page
        
        WebElement applyButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("applyJobButton")));
        applyButton.click();
        
        // Fill application form
        WebElement coverLetterInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("coverLetter")));
        coverLetterInput.sendKeys("I am very interested in this position and believe my skills match the requirements.");
        
        WebElement portfolioInput = driver.findElement(By.name("portfolioUrl"));
        portfolioInput.sendKeys("https://myportfolio.com");
        
        // Submit application
        WebElement submitApplicationButton = driver.findElement(By.cssSelector("button[type='submit'].submitApplicationButton"));
        submitApplicationButton.click();
        
        // Verify success
        WebElement confirmationMessage = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("confirmationMessage"))
        );
        assertNotNull(confirmationMessage, "Confirmation message should appear");
        assertTrue(confirmationMessage.getText().contains("applied") || confirmationMessage.getText().contains("success"), 
                   "Should confirm successful application");
    }

    @Test
    @DisplayName("Form Validation - Missing Required Fields")
    public void testFormValidationMissingRequiredFields() {
        driver.get(BASE_URL + "/dashboard/post-job");
        
        // Try to submit without filling any fields
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit'].submitJobButton")));
        submitButton.click();
        
        // Check for validation errors
        WebElement validationError = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("fieldError"))
        );
        assertNotNull(validationError, "Validation error should appear for missing fields");
        assertTrue(validationError.isDisplayed(), "Validation error should be visible");
    }

    @Test
    @DisplayName("Update User Profile Form")
    public void testUpdateUserProfileForm() {
        driver.get(BASE_URL + "/dashboard/profile");
        
        // Find and clear existing values
        WebElement phoneInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("phone")));
        phoneInput.clear();
        phoneInput.sendKeys("555-123-4567");
        
        WebElement bioInput = driver.findElement(By.id("bio"));
        bioInput.clear();
        bioInput.sendKeys("Experienced software engineer with passion for building great applications.");
        
        // Save changes
        WebElement saveButton = driver.findElement(By.cssSelector("button[type='submit'].saveProfileButton"));
        saveButton.click();
        
        // Verify update
        WebElement successMessage = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("successMessage"))
        );
        assertNotNull(successMessage, "Success message should appear");
        assertTrue(successMessage.getText().contains("updated") || successMessage.getText().contains("saved"), 
                   "Should confirm profile update");
    }

    // ============== DASHBOARD VISIBILITY TESTS ==============

    @Test
    @DisplayName("Dashboard Visible for Logged In User")
    public void testDashboardVisibleForLoggedInUser() {
        driver.get(BASE_URL + "/login");
        
        // Login first
        WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("loginEmail")));
        emailInput.sendKeys("testuser@example.com");
        
        WebElement passwordInput = driver.findElement(By.id("loginPassword"));
        passwordInput.sendKeys("Password123");
        
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit'].loginButton"));
        loginButton.click();
        
        // Navigate to dashboard
        driver.get(BASE_URL + "/dashboard");
        
        // Verify dashboard elements are visible
        WebElement welcomeMessage = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("welcomeMessage"))
        );
        assertNotNull(welcomeMessage, "Welcome message should be visible");
        
        WebElement userProfile = driver.findElement(By.className("userProfile"));
        assertTrue(userProfile.isDisplayed(), "User profile section should be visible");
    }

    @Test
    @DisplayName("Dashboard Redirect for Unauthenticated User")
    public void testDashboardRedirectForUnauthenticatedUser() {
        // Try to access dashboard without login
        driver.get(BASE_URL + "/dashboard");
        
        // Should redirect to login
        wait.until(ExpectedConditions.urlContains("/login"));
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/login"), "Unauthenticated user should be redirected to login");
    }

    @Test
    @DisplayName("Employer Dashboard Shows Posted Jobs")
    public void testEmployerDashboardShowsPostedJobs() {
        // Assuming logged in as employer
        driver.get(BASE_URL + "/dashboard/employer");
        
        // Wait for jobs list to load
        WebElement jobsList = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("postedJobsList")));
        assertNotNull(jobsList, "Posted jobs list should be visible");
        
        // Verify job items are displayed
        WebElement jobItem = driver.findElement(By.cssSelector("div.jobItem"));
        assertTrue(jobItem.isDisplayed(), "Job item should be visible");
        
        // Check for job information
        WebElement jobTitle = jobItem.findElement(By.className("jobItemTitle"));
        assertNotNull(jobTitle, "Job title should be present");
        assertFalse(jobTitle.getText().isEmpty(), "Job title should not be empty");
    }

    @Test
    @DisplayName("Job Seeker Dashboard Shows Applied Jobs")
    public void testJobSeekerDashboardShowsAppliedJobs() {
        // Assuming logged in as job seeker
        driver.get(BASE_URL + "/dashboard/jobseeker");
        
        // Wait for applications section
        WebElement applicationsSection = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("applicationsSection")));
        assertNotNull(applicationsSection, "Applications section should be visible");
        
        // Verify applications are displayed
        WebElement applicationCard = driver.findElement(By.cssSelector("div.applicationCard"));
        assertTrue(applicationCard.isDisplayed(), "Application card should be visible");
        
        // Check application status
        WebElement applicationStatus = applicationCard.findElement(By.className("applicationStatus"));
        assertNotNull(applicationStatus, "Application status should be present");
        assertFalse(applicationStatus.getText().isEmpty(), "Application status should not be empty");
    }

    // ============== LOGOUT TESTS ==============

    @Test
    @DisplayName("Logout Successfully")
    public void testLogoutSuccessfully() {
        // First login
        driver.get(BASE_URL + "/login");
        
        WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("loginEmail")));
        emailInput.sendKeys("testuser@example.com");
        
        WebElement passwordInput = driver.findElement(By.id("loginPassword"));
        passwordInput.sendKeys("Password123");
        
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit'].loginButton"));
        loginButton.click();
        
        // Wait for dashboard to load
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        
        // Find and click logout button
        WebElement userMenuButton = driver.findElement(By.id("userMenuButton"));
        userMenuButton.click();
        
        WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("logoutButton")));
        logoutButton.click();
        
        // Verify redirect to login or home
        wait.until(ExpectedConditions.urlMatches(".*(/login|/)$"));
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/login") || currentUrl.equals(BASE_URL + "/"), 
                   "Should redirect to login or home after logout");
        
        // Verify cannot access dashboard
        driver.get(BASE_URL + "/dashboard");
        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"), 
                   "Should not be able to access dashboard after logout");
    }

    @Test
    @DisplayName("Logout from Navigation Menu")
    public void testLogoutFromNavigationMenu() {
        driver.get(BASE_URL + "/dashboard");
        
        // Wait for navigation menu
        WebElement navMenu = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("navigationMenu")));
        assertNotNull(navMenu, "Navigation menu should be present");
        
        // Click user dropdown
        WebElement userDropdown = driver.findElement(By.cssSelector("button.userDropdownToggle"));
        userDropdown.click();
        
        // Click logout option
        WebElement logoutOption = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href*='logout']")));
        logoutOption.click();
        
        // Verify logout
        wait.until(ExpectedConditions.urlContains("/login"));
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/login"), "Should redirect to login after logout");
    }

    @Test
    @DisplayName("Session Expires After Logout")
    public void testSessionExpiresAfterLogout() {
        // Login
        driver.get(BASE_URL + "/login");
        
        WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("loginEmail")));
        emailInput.sendKeys("testuser@example.com");
        
        WebElement passwordInput = driver.findElement(By.id("loginPassword"));
        passwordInput.sendKeys("Password123");
        
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit'].loginButton"));
        loginButton.click();
        
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        
        // Logout
        WebElement userMenuButton = driver.findElement(By.id("userMenuButton"));
        userMenuButton.click();
        
        WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("logoutButton")));
        logoutButton.click();
        
        wait.until(ExpectedConditions.urlContains("/login"));
        
        // Try to access protected page
        driver.get(BASE_URL + "/dashboard");
        
        // Should redirect back to login (session expired)
        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"), 
                   "Session should be expired and user redirected to login");
    }
}
