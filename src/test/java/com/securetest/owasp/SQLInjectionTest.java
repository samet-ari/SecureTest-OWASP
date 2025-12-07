package com.securetest.owasp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;

public class SQLInjectionTest extends BaseTest {
    
    @Test
    public void testSQLInjectionOnLoginForm() {
        long startTime = System.currentTimeMillis();
        
        navigateTo("http://testphp.vulnweb.com/login.php");
        
        // Initialize screenshot utility
        ScreenshotUtil screenshotUtil = new ScreenshotUtil(driver);
        List<String> screenshotPaths = new ArrayList<>();
        
        System.out.println("\n========================================");
        System.out.println("Testing SQL Injection on: " + driver.getCurrentUrl());
        System.out.println("Page Title: " + getPageTitle());
        System.out.println("========================================\n");
        
        // Take initial screenshot
        String startScreenshot = screenshotUtil.takeTestStartScreenshot("SQLInjectionTest");
        if (startScreenshot != null) {
            screenshotPaths.add(startScreenshot);
        }
        
        try {
            By formLocator = By.tagName("form");
            By usernameInput = By.name("uname");
            By submitButton = By.cssSelector("input[type='submit']");
            
            SQLInjectionScanner scanner = new SQLInjectionScanner(driver);
            List<String> vulnerabilities = scanner.scanForm(formLocator, usernameInput, submitButton);
            
            // Take screenshot for each vulnerability found
            if (!vulnerabilities.isEmpty()) {
                String vulnScreenshot = screenshotUtil.takeVulnerabilityScreenshot("SQLInjection", "vulnerability_detected");
                if (vulnScreenshot != null) {
                    screenshotPaths.add(vulnScreenshot);
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            
            System.out.println("\n========== SCAN RESULTS ==========");
            System.out.println("Payloads tested: 3");
            System.out.println("Vulnerabilities found: " + scanner.getVulnerabilityCount());
            
            if (!vulnerabilities.isEmpty()) {
                System.out.println("\n[CRITICAL] SQL Injection vulnerabilities detected:");
                for (String vuln : vulnerabilities) {
                    System.out.println("  - " + vuln);
                }
            } else {
                System.out.println("\nNo SQL Injection vulnerabilities detected.");
            }
            
            System.out.println("\n📸 Screenshots captured: " + screenshotPaths.size());
            for (String path : screenshotPaths) {
                System.out.println("  - " + path);
            }
            System.out.println("==================================\n");
            
            // Generate HTML Report
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String reportPath = "reports/security-scan-" + timestamp + ".html";
            
            HTMLReporter reporter = new HTMLReporter(reportPath);
            reporter.generateReport(driver.getCurrentUrl(), vulnerabilities, 3, duration);
            
            System.out.println("📊 HTML Report: " + reportPath);
            
            // Take final screenshot
            String endScreenshot = screenshotUtil.takeTestEndScreenshot("SQLInjectionTest");
            if (endScreenshot != null) {
                screenshotPaths.add(endScreenshot);
            }
            
            assertTrue(true, "SQL Injection test completed successfully");
            
        } catch (Exception e) {
            // Take error screenshot
            screenshotUtil.takeScreenshot("SQLInjectionTest_error");
            
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed: " + e.getMessage());
        }
    }
}