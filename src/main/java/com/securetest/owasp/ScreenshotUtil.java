package com.securetest.owasp;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Screenshot Utility
 * Captures and saves screenshots during security testing
 */
public class ScreenshotUtil {
    
    private WebDriver driver;
    private String screenshotDir;
    
    public ScreenshotUtil(WebDriver driver) {
        this.driver = driver;
        this.screenshotDir = "screenshots";
        createScreenshotDirectory();
    }
    
    /**
     * Create screenshots directory if it doesn't exist
     */
    private void createScreenshotDirectory() {
        File dir = new File(screenshotDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    /**
     * Take screenshot with custom name
     */
    public String takeScreenshot(String testName) {
        try {
            // Generate filename with timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = testName + "_" + timestamp + ".png";
            String filePath = screenshotDir + "/" + fileName;
            
            // Take screenshot
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File source = screenshot.getScreenshotAs(OutputType.FILE);
            File destination = new File(filePath);
            
            // Save screenshot
            FileUtils.copyFile(source, destination);
            
            System.out.println("📸 Screenshot saved: " + filePath);
            return filePath;
            
        } catch (IOException e) {
            System.err.println("❌ Failed to take screenshot: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Take screenshot when vulnerability is found
     */
    public String takeVulnerabilityScreenshot(String vulnerabilityType, String payload) {
        String testName = vulnerabilityType + "_" + sanitizeFileName(payload);
        return takeScreenshot(testName);
    }
    
    /**
     * Take screenshot at test start
     */
    public String takeTestStartScreenshot(String testName) {
        return takeScreenshot(testName + "_start");
    }
    
    /**
     * Take screenshot at test end
     */
    public String takeTestEndScreenshot(String testName) {
        return takeScreenshot(testName + "_end");
    }
    
    /**
     * Sanitize filename by removing special characters
     */
    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9-_]", "_").substring(0, Math.min(fileName.length(), 50));
    }
}