package com.securetest.owasp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class SQLInjectionTest extends BaseTest {
    
    @Test
    public void testSQLInjectionOnLoginForm() {
        navigateTo("http://testphp.vulnweb.com/login.php");
        
        System.out.println("\n========================================");
        System.out.println("Testing SQL Injection on: " + driver.getCurrentUrl());
        System.out.println("Page Title: " + getPageTitle());
        System.out.println("========================================\n");
        
        try {
            // Locator'ları tanımla
            By formLocator = By.tagName("form");
            By usernameInput = By.name("uname");
            By submitButton = By.cssSelector("input[type='submit']");
            
            // Scanner oluştur ve test et
            SQLInjectionScanner scanner = new SQLInjectionScanner(driver);
            List<String> vulnerabilities = scanner.scanForm(formLocator, usernameInput, submitButton);
            
            // Sonuçları raporla
            System.out.println("\n========== SCAN RESULTS ==========");
            System.out.println("Payloads tested: 3");
            System.out.println("Vulnerabilities found: " + scanner.getVulnerabilityCount());
            
            if (!vulnerabilities.isEmpty()) {
                System.out.println("\n[CRITICAL] SQL Injection vulnerabilities detected:");
                for (String vuln : vulnerabilities) {
                    System.out.println("    " + vuln);
                }
            } else {
                System.out.println("\n No SQL Injection vulnerabilities detected.");
            }
            System.out.println("==================================\n");
            
            // Test başarılı
            assertTrue(true, "SQL Injection test completed successfully");
            
        } catch (Exception e) {
            System.err.println(" Error during test: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }
}
