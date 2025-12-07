package com.securetest.owasp;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.time.Duration;

/**
 * Base Test Class
 * Provides WebDriver setup and teardown for all security tests
 */
public class BaseTest {
    
    protected WebDriver driver;
    
    @BeforeEach
    public void setUp() {
        // Setup ChromeDriver using WebDriverManager
        WebDriverManager.chromedriver().setup();
        
        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("detach", true); // TEST BİTİNCE KAPANMAZ
        driver = new ChromeDriver(options);
       
        
        // Initialize WebDriver
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }
    
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    /**
     * Navigate to URL
     */
    protected void navigateTo(String url) {
        driver.get(url);
    }
    
    /**
     * Get current page title
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }
}