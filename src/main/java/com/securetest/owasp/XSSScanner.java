package com.securetest.owasp;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import java.util.List;

/**
 * XSS (Cross-Site Scripting) Scanner
 * Tests web forms for XSS vulnerabilities
 */
public class XSSScanner {
    
    private WebDriver driver;
    private List<String> vulnerabilities;
    private ScreenshotUtil screenshotUtil;
    
    // XSS test payloads
    private static final String[] XSS_PAYLOADS = {
        "<script>alert('XSS')</script>",
        "<img src=x onerror=alert('XSS')>",
        "<svg/onload=alert('XSS')>",
        "javascript:alert('XSS')",
        "<iframe src=javascript:alert('XSS')>",
        "'\"><script>alert('XSS')</script>"
    };
    
    public XSSScanner(WebDriver driver) {
        this.driver = driver;
        this.vulnerabilities = new ArrayList<>();
        this.screenshotUtil = new ScreenshotUtil(driver);
    }
    
    /**
 * Scan a form for XSS vulnerabilities
 */
public List<String> scanForm(By formLocator, By inputLocator, By submitButtonLocator) {
    
    for (String payload : XSS_PAYLOADS) {
        try {
            // Find form elements
            WebElement input = driver.findElement(inputLocator);
            
            System.out.println("Testing XSS payload: " + payload);
            
            // Clear and enter payload
            input.clear();
            Thread.sleep(300);
            input.sendKeys(payload);
            
            // Submit form
            WebElement submitButton = driver.findElement(submitButtonLocator);
            submitButton.click();
            
            Thread.sleep(1000);
            
            // Check for XSS vulnerability
            if (detectXSS(payload)) {
                String vulnerability = "XSS vulnerability detected with payload: " + payload;
                vulnerabilities.add(vulnerability);
                System.out.println("[VULNERABILITY FOUND] " + vulnerability);
                
                // Take screenshot of vulnerability
                try {
                    screenshotUtil.takeVulnerabilityScreenshot("XSS", sanitizePayload(payload));
                } catch (Exception e) {
                    System.out.println("Could not take screenshot: " + e.getMessage());
                }
            }
            
            // Handle any remaining alerts before navigating back
            try {
                driver.switchTo().alert().dismiss();
                Thread.sleep(500);
            } catch (Exception e) {
                // No alert present, continue
            }
            
            // Navigate back
            try {
                driver.navigate().back();
                Thread.sleep(1000);
            } catch (Exception e) {
                // Browser might have crashed, try to recover
                System.out.println("Warning: Could not navigate back, continuing to next payload");
                return vulnerabilities; // Stop testing if browser is unstable
            }
            
        } catch (Exception e) {
            System.out.println("Error testing XSS payload '" + payload + "': " + e.getMessage());
            
            // Try to dismiss any alerts
            try {
                driver.switchTo().alert().dismiss();
            } catch (Exception ignored) {}
        }
    }
    
    return vulnerabilities;
}
    
    /**
     * Detect XSS in page source or alerts
     */
    private boolean detectXSS(String payload) {
        try {
            String pageSource = driver.getPageSource();
            
            // Check if payload is reflected in page source without proper encoding
            if (pageSource.contains(payload)) {
                return true;
            }
            
            // Check for common XSS patterns in page source
            if (containsXSSPattern(pageSource)) {
                return true;
            }
            
            // Check for JavaScript alert (indicates successful XSS)
            try {
                if (driver.switchTo().alert() != null) {
                    driver.switchTo().alert().dismiss();
                    return true;
                }
            } catch (Exception e) {
                // No alert present
            }
            
            return false;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if page contains XSS patterns
     */
    private boolean containsXSSPattern(String pageSource) {
        String[] xssPatterns = {
            "<script>",
            "javascript:",
            "onerror=",
            "onload=",
            "<iframe",
            "<svg"
        };
        
        for (String pattern : xssPatterns) {
            if (pageSource.toLowerCase().contains(pattern.toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Sanitize payload for filename
     */
    private String sanitizePayload(String payload) {
        return payload.replaceAll("[^a-zA-Z0-9]", "_").substring(0, Math.min(payload.length(), 30));
    }
    
    /**
     * Get all found vulnerabilities
     */
    public List<String> getVulnerabilities() {
        return vulnerabilities;
    }
    
    /**
     * Get vulnerability count
     */
    public int getVulnerabilityCount() {
        return vulnerabilities.size();
    }
}