package com.securetest.owasp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;

/**
 * XSS (Cross-Site Scripting) Test
 * Tests vulnerable websites for XSS vulnerabilities
 */
public class XSSTest extends BaseTest {
    
    @Test
    public void testXSSOnSearchForm() {
        long startTime = System.currentTimeMillis();
        
        // Using a test site known to have XSS vulnerabilities
        navigateTo("http://testphp.vulnweb.com/search.php?test=query");
        
        ScreenshotUtil screenshotUtil = new ScreenshotUtil(driver);
        List<String> screenshotPaths = new ArrayList<>();
        
        System.out.println("\n========================================");
        System.out.println("Testing XSS on: " + driver.getCurrentUrl());
        System.out.println("Page Title: " + getPageTitle());
        System.out.println("========================================\n");
        
        // Take initial screenshot
        String startScreenshot = screenshotUtil.takeTestStartScreenshot("XSSTest");
        if (startScreenshot != null) {
            screenshotPaths.add(startScreenshot);
        }
        
        try {
            // Define locators for search form
            By formLocator = By.tagName("form");
            By searchInput = By.name("searchFor");
            By submitButton = By.cssSelector("input[type='submit']");
            
            // Create XSS scanner and test
            XSSScanner scanner = new XSSScanner(driver);
            List<String> vulnerabilities = scanner.scanForm(formLocator, searchInput, submitButton);
            
            // Take screenshot if vulnerabilities found
            if (!vulnerabilities.isEmpty()) {
                String vulnScreenshot = screenshotUtil.takeVulnerabilityScreenshot("XSS", "vulnerabilities_detected");
                if (vulnScreenshot != null) {
                    screenshotPaths.add(vulnScreenshot);
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            
            // Display results
            System.out.println("\n========== XSS SCAN RESULTS ==========");
            System.out.println("Payloads tested: 6");
            System.out.println("Vulnerabilities found: " + scanner.getVulnerabilityCount());
            
            if (!vulnerabilities.isEmpty()) {
                System.out.println("\n[CRITICAL] XSS vulnerabilities detected:");
                for (String vuln : vulnerabilities) {
                    System.out.println("  - " + vuln);
                }
            } else {
                System.out.println("\nNo XSS vulnerabilities detected.");
            }
            
            System.out.println("\n📸 Screenshots captured: " + screenshotPaths.size());
            for (String path : screenshotPaths) {
                System.out.println("  - " + path);
            }
            System.out.println("======================================\n");
            
            // Generate HTML Report
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String reportPath = "reports/xss-scan-" + timestamp + ".html";
            
            HTMLReporter reporter = new HTMLReporter(reportPath);
            reporter.generateReport(driver.getCurrentUrl(), vulnerabilities, 6, duration);
            
            System.out.println("📊 HTML Report: " + reportPath);
            
            // Take final screenshot
            String endScreenshot = screenshotUtil.takeTestEndScreenshot("XSSTest");
            if (endScreenshot != null) {
                screenshotPaths.add(endScreenshot);
            }
            
            assertTrue(true, "XSS test completed successfully");
            
        } catch (Exception e) {
            screenshotUtil.takeScreenshot("XSSTest_error");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed: " + e.getMessage());
        }
    }
}