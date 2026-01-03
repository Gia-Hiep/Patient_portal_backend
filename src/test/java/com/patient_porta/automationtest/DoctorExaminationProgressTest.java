package com.patient_porta.automationtest;

import org.junit.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

public class DoctorExaminationProgressTest {

    private static final String FE_URL = "http://localhost:3000";
    private static final String DOCTOR_USER = "doctor01";
    private static final String PASSWORD = "12345678";

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    static void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        // options.addArguments("--headless=new"); // nếu cần

        driver = new ChromeDriver(options);
        driver.manage().window().setSize(new Dimension(1366, 900));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /* ================= HELPERS ================= */

    private WebElement visible(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    private void click(By by) {
        wait.until(ExpectedConditions.elementToBeClickable(by)).click();
    }

    private void loginDoctor() {
        driver.get(FE_URL + "/login");

        visible(By.name("username")).sendKeys(DOCTOR_USER);
        visible(By.name("password")).sendKeys(PASSWORD);
        click(By.cssSelector("button[type='submit']"));

        wait.until(d ->
                d.findElements(By.xpath("//*[contains(text(),'Cập nhật trạng thái khám')]")).size() > 0
        );
    }

    private void selectPatientById(String patientId) {
        WebElement card = visible(
                By.xpath("//*[contains(text(),'Mã hồ sơ: " + patientId + "')]")
        );
        card.click();
    }

    private void updateStage(String stageValue) {
        Select select = new Select(visible(By.tagName("select")));
        select.selectByValue(stageValue);

        click(By.xpath("//button[contains(text(),'Cập nhật')]"));
    }

    /* ================= TEST ================= */

    @Test
    public void test_Doctor_Update_To_Waiting_Lab() {
        loginDoctor();

        driver.get(FE_URL + "/doctor/examination-progress");

        selectPatientById("24");
        updateStage("2"); // Chờ xét nghiệm

        assertTrue(driver.getPageSource().contains("Cập nhật trạng thái"));
    }

    @Test
    public void test_Doctor_Update_To_Completed() {
        loginDoctor();

        driver.get(FE_URL + "/doctor/examination-progress");

        selectPatientById("24");
        updateStage("3"); // Hoàn tất

        assertTrue(driver.getPageSource().contains("Cập nhật trạng thái"));
    }
}
