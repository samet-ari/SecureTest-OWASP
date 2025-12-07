package com.securetest.owasp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HTMLReporter {
    
    private String reportPath;
    
    public HTMLReporter(String reportPath) {
        this.reportPath = reportPath;
    }
    
    public void generateReport(String testUrl, List<String> vulnerabilities, 
                               int totalPayloads, long testDuration) {
        try {
            createReportDirectory();
            writeHTMLReport(testUrl, vulnerabilities, totalPayloads, testDuration);
            System.out.println("\n✅ HTML Report generated: " + reportPath);
        } catch (IOException e) {
            System.err.println("❌ Error generating report: " + e.getMessage());
        }
    }
    
    private void createReportDirectory() {
        File reportDir = new File("reports");
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }
    }
    
    private void writeHTMLReport(String testUrl, List<String> vulnerabilities, 
                                 int totalPayloads, long testDuration) throws IOException {
        FileWriter writer = new FileWriter(reportPath);
        writer.write(getHTMLContent(testUrl, vulnerabilities, totalPayloads, testDuration));
        writer.close();
    }
    
    private String getHTMLContent(String testUrl, List<String> vulnerabilities, 
                                  int totalPayloads, long testDuration) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        int vulnerabilityCount = vulnerabilities.size();
        String statusClass = vulnerabilityCount > 0 ? "critical" : "success";
        String statusText = vulnerabilityCount > 0 ? "VULNERABILITIES FOUND" : "SECURE";
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang='en'>\n<head>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>\n");
        html.append("<title>SecureTest-OWASP Security Report</title>\n");
        html.append("<style>\n");
        html.append(getCSS());
        html.append("</style>\n</head>\n<body>\n");
        
        // Header
        html.append("<div class='header'>\n");
        html.append("  <h1>🔒 SecureTest-OWASP</h1>\n");
        html.append("  <p>Automated Security Scan Report</p>\n");
        html.append("</div>\n");
        
        // Container
        html.append("<div class='container'>\n");
        
        // Summary Box
        html.append("  <div class='summary-box'>\n");
        html.append("    <h2>Test Summary</h2>\n");
        html.append("    <div class='status-badge " + statusClass + "'>" + statusText + "</div>\n");
        html.append("    <div class='summary-grid'>\n");
        html.append("      <div class='summary-item'>\n");
        html.append("        <div class='label'>Target URL</div>\n");
        html.append("        <div class='value'>" + testUrl + "</div>\n");
        html.append("      </div>\n");
        html.append("      <div class='summary-item'>\n");
        html.append("        <div class='label'>Scan Date</div>\n");
        html.append("        <div class='value'>" + timestamp + "</div>\n");
        html.append("      </div>\n");
        html.append("      <div class='summary-item'>\n");
        html.append("        <div class='label'>Payloads Tested</div>\n");
        html.append("        <div class='value'>" + totalPayloads + "</div>\n");
        html.append("      </div>\n");
        html.append("      <div class='summary-item'>\n");
        html.append("        <div class='label'>Vulnerabilities Found</div>\n");
        html.append("        <div class='value " + statusClass + "-text'>" + vulnerabilityCount + "</div>\n");
        html.append("      </div>\n");
        html.append("      <div class='summary-item'>\n");
        html.append("        <div class='label'>Test Duration</div>\n");
        html.append("        <div class='value'>" + String.format("%.2f", testDuration / 1000.0) + "s</div>\n");
        html.append("      </div>\n");
        html.append("    </div>\n");
        html.append("  </div>\n");
        
        // Vulnerabilities Section
        if (vulnerabilityCount > 0) {
            html.append("  <div class='vulnerabilities-section'>\n");
            html.append("    <h2>⚠️ Detected Vulnerabilities</h2>\n");
            
            for (int i = 0; i < vulnerabilities.size(); i++) {
                html.append("    <div class='vulnerability-item'>\n");
                html.append("      <div class='vuln-header'>\n");
                html.append("        <span class='vuln-number'>#" + (i + 1) + "</span>\n");
                html.append("        <span class='vuln-type'>SQL Injection</span>\n");
                html.append("        <span class='severity critical'>CRITICAL</span>\n");
                html.append("      </div>\n");
                html.append("      <div class='vuln-details'>\n");
                html.append("        <p><strong>Description:</strong> " + vulnerabilities.get(i) + "</p>\n");
                html.append("        <p><strong>Risk:</strong> Attackers can manipulate SQL queries to access or modify database data</p>\n");
                html.append("        <p><strong>Recommendation:</strong> Use parameterized queries (PreparedStatement) and input validation</p>\n");
                html.append("      </div>\n");
                html.append("    </div>\n");
            }
            
            html.append("  </div>\n");
        } else {
            html.append("  <div class='no-vulnerabilities'>\n");
            html.append("    <h2>✅ No Vulnerabilities Detected</h2>\n");
            html.append("    <p>All SQL Injection tests passed successfully.</p>\n");
            html.append("  </div>\n");
        }
        
        // Footer
        html.append("  <div class='footer'>\n");
        html.append("    <p>Generated by SecureTest-OWASP Framework</p>\n");
        html.append("    <p><a href='https://github.com/samet-ari/SecureTest-OWASP'>GitHub Repository</a></p>\n");
        html.append("  </div>\n");
        html.append("</div>\n");
        
        html.append("</body>\n</html>");
        
        return html.toString();
    }
    
    private String getCSS() {
        return "* { margin: 0; padding: 0; box-sizing: border-box; }\n" +
               "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px; min-height: 100vh; }\n" +
               ".header { text-align: center; color: white; margin-bottom: 30px; }\n" +
               ".header h1 { font-size: 2.5em; margin-bottom: 10px; text-shadow: 2px 2px 4px rgba(0,0,0,0.3); }\n" +
               ".header p { font-size: 1.2em; }\n" +
               ".container { max-width: 1200px; margin: 0 auto; }\n" +
               ".summary-box { background: white; border-radius: 15px; padding: 30px; margin-bottom: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); }\n" +
               ".summary-box h2 { color: #333; margin-bottom: 20px; font-size: 1.8em; }\n" +
               ".status-badge { display: inline-block; padding: 12px 24px; border-radius: 25px; font-weight: bold; font-size: 1.1em; margin-bottom: 25px; }\n" +
               ".status-badge.critical { background: #ff4444; color: white; }\n" +
               ".status-badge.success { background: #00C851; color: white; }\n" +
               ".summary-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 20px; }\n" +
               ".summary-item { padding: 20px; background: #f8f9fa; border-radius: 10px; border-left: 4px solid #667eea; }\n" +
               ".summary-item .label { font-size: 0.9em; color: #666; margin-bottom: 8px; text-transform: uppercase; letter-spacing: 0.5px; }\n" +
               ".summary-item .value { font-size: 1.4em; font-weight: bold; color: #333; word-break: break-word; }\n" +
               ".critical-text { color: #ff4444; }\n" +
               ".success-text { color: #00C851; }\n" +
               ".vulnerabilities-section { background: white; border-radius: 15px; padding: 30px; margin-bottom: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); }\n" +
               ".vulnerabilities-section h2 { color: #333; margin-bottom: 25px; }\n" +
               ".vulnerability-item { background: #fff3cd; border-left: 5px solid #ff4444; padding: 20px; margin-bottom: 20px; border-radius: 8px; transition: transform 0.2s; }\n" +
               ".vulnerability-item:hover { transform: translateX(5px); }\n" +
               ".vuln-header { display: flex; gap: 15px; align-items: center; margin-bottom: 15px; flex-wrap: wrap; }\n" +
               ".vuln-number { background: #333; color: white; padding: 5px 15px; border-radius: 20px; font-weight: bold; font-size: 0.9em; }\n" +
               ".vuln-type { font-weight: bold; color: #333; font-size: 1.1em; }\n" +
               ".severity { padding: 5px 15px; border-radius: 20px; font-weight: bold; font-size: 0.85em; text-transform: uppercase; }\n" +
               ".severity.critical { background: #ff4444; color: white; }\n" +
               ".vuln-details { line-height: 1.8; color: #555; }\n" +
               ".vuln-details p { margin-bottom: 12px; }\n" +
               ".vuln-details strong { color: #333; }\n" +
               ".no-vulnerabilities { background: white; border-radius: 15px; padding: 50px; text-align: center; box-shadow: 0 10px 30px rgba(0,0,0,0.2); }\n" +
               ".no-vulnerabilities h2 { color: #00C851; margin-bottom: 15px; font-size: 2em; }\n" +
               ".no-vulnerabilities p { color: #666; font-size: 1.1em; }\n" +
               ".footer { text-align: center; color: white; margin-top: 30px; padding: 25px; }\n" +
               ".footer p { margin: 5px 0; }\n" +
               ".footer a { color: white; text-decoration: underline; font-weight: bold; }\n" +
               ".footer a:hover { color: #ddd; }\n";
    }
}