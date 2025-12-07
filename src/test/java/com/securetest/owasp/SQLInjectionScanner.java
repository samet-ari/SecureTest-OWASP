package com.securetest.owasp;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import java.util.List;

public class SQLInjectionScanner {
    
    private WebDriver driver;
    private List<String> vulnerabilities;
    
    private static final String[] SQL_PAYLOADS = {
        "' OR '1'='1",
        "' OR '1'='1' --",
        "admin' --"
    };
    
    public SQLInjectionScanner(WebDriver driver) {
        this.driver = driver;
        this.vulnerabilities = new ArrayList<>();
    }
    
    public List<String> scanForm(By formLocator, By inputLocator, By submitButtonLocator) {
        
        for (String payload : SQL_PAYLOADS) {
            try {
                // Her iterasyonda elementleri yeniden bul
                WebElement form = driver.findElement(formLocator);
                WebElement input = driver.findElement(inputLocator);
                
                System.out.println("Testing payload: " + payload);
                
                // Input'u temizle ve payload gir
                input.clear();
                Thread.sleep(500); // Kısa bekleme
                input.sendKeys(payload);
                
                // Submit butonunu bul ve tıkla
                WebElement submitButton = driver.findElement(submitButtonLocator);
                submitButton.click();
                
                // Sayfa yüklenmesini bekle
                Thread.sleep(1000);
                
                // SQL error kontrolü
                String pageSource = driver.getPageSource().toLowerCase();
                if (containsSQLError(pageSource)) {
                    String vulnerability = "SQL Injection detected with payload: " + payload;
                    vulnerabilities.add(vulnerability);
                    System.out.println("[VULNERABILITY FOUND] " + vulnerability);
                }
                
                // Geri dön
                driver.navigate().back();
                Thread.sleep(1000); // Sayfanın yüklenmesini bekle
                
            } catch (Exception e) {
                System.out.println("Error testing payload '" + payload + "': " + e.getMessage());
            }
        }
        
        return vulnerabilities;
    }
    
    private boolean containsSQLError(String pageSource) {
        String[] errorPatterns = {
            "sql syntax",
            "mysql_fetch",
            "mysql_num_rows",
            "syntax error",
            "warning: mysql"
        };
        
        for (String pattern : errorPatterns) {
            if (pageSource.contains(pattern)) {
                return true;
            }
        }
        
        return false;
    }
    
    public List<String> getVulnerabilities() {
        return vulnerabilities;
    }
    
    public int getVulnerabilityCount() {
        return vulnerabilities.size();
    }
}