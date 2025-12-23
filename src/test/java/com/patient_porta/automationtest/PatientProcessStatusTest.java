package com.patient_porta.automation;

import org.junit.*;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.time.Duration;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatientProcessStatusTest {

    private static final String FE_URL = "http://localhost:3000";
    private static final String PATIENT_USER = "vanhai12@gmail.com";
    private static final String PASSWORD = "vanhai1108";

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeClass
    public static void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @AfterClass
    public static void teardown() {
        if (driver != null) driver.quit();
    }

    /* ================= HELPERS ================= */

    private WebElement visible(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    private void click(By by) {
        wait.until(ExpectedConditions.elementToBeClickable(by)).click();
    }

    private void loginPatient() {
        driver.get(FE_URL + "/login");

        visible(By.name("username")).sendKeys(PATIENT_USER);
        visible(By.name("password")).sendKeys(PASSWORD);
        click(By.cssSelector("button[type='submit']"));

        wait.until(d ->
                d.findElements(By.xpath("//*[contains(text(),'Trạng thái quy trình khám')]")).size() > 0
        );
    }

    /* ================= TEST ================= */

    @Test
    public void test_Patient_See_Waiting_Lab() {
        loginPatient();
        driver.get(FE_URL + "/process");

        WebElement status = visible(
                By.xpath("//*[contains(text(),'Chờ xét nghiệm')]")
        );

        assertTrue(status.isDisplayed());
    }

    @Test
    public void test_Patient_See_Completed() {
        loginPatient();
        driver.get(FE_URL + "/process");

        WebElement done = visible(
                By.xpath("//*[contains(text(),'Hoàn thành')]")
        );

        assertTrue(done.isDisplayed());
    }
}
