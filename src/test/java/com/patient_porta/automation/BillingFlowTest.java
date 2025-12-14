package com.patient_porta.automation;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.*;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class BillingFlowTest {

    /* ===== CẤU HÌNH ===== */
    private static final String FE_URL = "http://localhost:3000";
    private static final String BE_URL = "http://localhost:8080";
    private static final String USERNAME = "hiepcc22";
    private static final String PASSWORD = "anhhiepdz";

    private static final boolean USE_STRIPE_REAL = true;

    // Stripe test cards
    private static final String CARD_OK   = "4242 4242 4242 4242";
    private static final String CARD_3DS  = "4000 0027 6000 3184";
    private static final String CARD_DECL = "4000 0000 0000 0002"; // always declined

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeClass
    public static void setup() {
        ChromeOptions opts = new ChromeOptions();
        // opts.addArguments("--headless=new");
        opts.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(opts);
        driver.manage().window().setSize(new Dimension(1366, 900));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        wait = new WebDriverWait(driver, Duration.ofSeconds(45));
    }

    @AfterClass
    public static void teardown() {
        if (driver != null) driver.quit();
    }

    /* ===== Helpers UI chung ===== */

    private WebElement visible(By by) {
        for (int i = 0; i < 2; i++) {                // 1 retry nếu dính alert
            try {
                return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            } catch (UnhandledAlertException e) {
                clearAnyAlertIfPresent(3);
            }
        }
        // lần cuối: để exception gốc nếu còn lỗi
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    private List<WebElement> listInvoiceRows() {
        // dọn alert trước khi chuyển trang/đọc DOM
        clearAnyAlertIfPresent(1);
        driver.get(FE_URL + "/billing");
        clearAnyAlertIfPresent(1);
        visible(By.xpath("//*[contains(.,'Hóa đơn viện phí')]"));
        visible(By.cssSelector("table.visit-table"));
        return driver.findElements(By.cssSelector("table.visit-table tbody tr"));
    }


    private void click(By by) {
        wait.until(ExpectedConditions.elementToBeClickable(by)).click();
    }

    private void sleep(long ms) { try { Thread.sleep(ms); } catch (InterruptedException ignored) {} }

    private void loginUI() {
        driver.get(FE_URL + "/login");
        WebElement user = visible(By.cssSelector(".auth-card input[autocomplete='username']"));
        user.clear(); user.sendKeys(USERNAME);
        WebElement pass = visible(By.cssSelector(".auth-card input[type='password']"));
        pass.clear(); pass.sendKeys(PASSWORD);
        click(By.cssSelector(".auth-card button.btn[type='submit']"));
        wait.until(d ->
                d.findElements(By.xpath("//*[contains(.,'Patient Dashboard')]")).size() > 0
                        || d.findElements(By.cssSelector("a[href='/billing'], .menu a[href='/billing']")).size() > 0
        );
    }


    private WebElement findFirstUnpaidRow() {
        for (WebElement r : listInvoiceRows()) {
            WebElement st = null;
            try { st = r.findElement(By.cssSelector("td:nth-child(4)")); } catch (Exception ignored) {}
            if (st == null) try { st = r.findElement(By.cssSelector("td:nth-child(5)")); } catch (Exception ignored) {}
            if (st == null) continue;
            String t = st.getText().trim().toUpperCase();
            if (t.equals("UNPAID") || t.equals("INPROGRESS")) return r;
        }
        return null;
    }


    private boolean clickInAnyFrameRecursive(java.util.function.BooleanSupplier tryHere, int depth, int maxDepth) {
        if (depth > maxDepth) return false;
        List<WebElement> frames = driver.findElements(By.cssSelector(
                "iframe, iframe[name^='__privateStripeFrame'], iframe[src*='3d_secure'], iframe[title*='3D Secure'], iframe[src*='challenge']"));
        for (WebElement f : frames) {
            try {
                if (!f.isDisplayed()) continue;
                driver.switchTo().frame(f);
                if (tryHere.getAsBoolean()) return true;
                // thử vào các frame con
                if (clickInAnyFrameRecursive(tryHere, depth + 1, maxDepth)) return true;
            } catch (StaleElementReferenceException ignored) {
            } finally {
                driver.switchTo().parentFrame();
            }
        }
        return false;
    }



    private WebElement findAnotherUnpaidRow(String excludeInvoiceNo) {
        for (WebElement r : listInvoiceRows()) {
            String inv = getInvoiceNoFromRow(r);
            if (inv.equals(excludeInvoiceNo)) continue;
            WebElement st = null;
            try { st = r.findElement(By.cssSelector("td:nth-child(4)")); } catch (Exception ignored) {}
            if (st == null) try { st = r.findElement(By.cssSelector("td:nth-child(5)")); } catch (Exception ignored) {}
            if (st == null) continue;
            String t = st.getText().trim().toUpperCase();
            if (t.equals("UNPAID") || t.equals("INPROGRESS")) return r;
        }
        return null;
    }

    private String getInvoiceNoFromRow(WebElement row) {
        return row.findElement(By.cssSelector("td:nth-child(1)")).getText().trim();
    }

    private void openInvoiceDetail(WebElement row) {
        row.findElement(By.xpath(".//button[contains(.,'Xem chi tiết')]")).click();
        visible(By.xpath("//div[contains(@class,'modal')]//h3[contains(.,'Hóa đơn')]"));
        visible(By.xpath("//div[contains(@class,'detail-row')]//*[contains(.,'Tổng cộng')]"));
    }

    /** Đóng modal chi tiết nếu còn mở (an toàn, không fail test nếu không đóng được). */
    private void closeDetailModalIfOpen() {
        driver.switchTo().defaultContent();
        long deadline = System.currentTimeMillis() + 8000; // tối đa 8s cố gắng
        RuntimeException last = null;

        while (System.currentTimeMillis() < deadline) {
            try {
                // Lấy modal ĐANG HIỂN THỊ (không phải mọi .modal trong DOM)
                List<WebElement> all = driver.findElements(By.cssSelector(".modal"));
                WebElement visibleModal = null;
                for (WebElement m : all) {
                    try {
                        if (m.isDisplayed() && m.getRect().getHeight() > 0 && m.getRect().getWidth() > 0) {
                            visibleModal = m; break;
                        }
                    } catch (StaleElementReferenceException ignored) {}
                }
                if (visibleModal == null) {
                    // Không có modal hiển thị -> coi như đã đóng xong
                    return;
                }

                // 1) click nút close tiêu chuẩn
                List<By> closeTries = List.of(
                        By.cssSelector(".close-btn"),
                        By.cssSelector("button[aria-label='Close']"),
                        By.xpath(".//button[contains(.,'Đóng')]"),
                        By.xpath(".//button[contains(.,'Close')]"),
                        By.xpath(".//button[contains(.,'Hủy')]"),
                        By.cssSelector("[data-dismiss='modal']")
                );
                boolean clicked = false;
                for (By by : closeTries) {
                    List<WebElement> cs = visibleModal.findElements(by);
                    if (!cs.isEmpty() && cs.get(0).isDisplayed()) {
                        try { cs.get(0).click(); clicked = true; break; } catch (Exception ignored) {}
                    }
                }

                // 2) nếu chưa đóng được: thử click backdrop
                if (!clicked) {
                    List<WebElement> backs = driver.findElements(By.cssSelector(".modal-backdrop, .overlay, .backdrop"));
                    for (WebElement b : backs) {
                        try { if (b.isDisplayed()) { b.click(); clicked = true; break; } } catch (Exception ignored) {}
                    }
                }

                // 3) nếu vẫn chưa: gửi ESC
                if (!clicked) {
                    try {
                        WebElement body = driver.findElement(By.tagName("body"));
                        new Actions(driver).sendKeys(Keys.ESCAPE).perform();
                    } catch (Exception ignored) {}
                }

                // 4) Chờ modal vừa tìm được trở nên INVISIBLE (không bắt buộc remove khỏi DOM)
                try {
                    new WebDriverWait(driver, Duration.ofSeconds(3))
                            .until(ExpectedConditions.invisibilityOf(visibleModal));
                } catch (TimeoutException te) {
                    // vòng sau thử lại
                }

            } catch (RuntimeException e) {
                last = e;
                sleep(400);
            }
        }
        // Không throw để tránh làm hỏng test 3DS – chỉ log
        if (last != null) System.out.println("closeDetailModalIfOpen: couldn't fully close modal, continue. Reason: " + last.getMessage());
    }


    /* ===== Stripe helpers ===== */

    /** Bấm “Thanh toán viện phí” trong modal */
    private void openStripeFromModalOrFailIfNoKey() {
        WebElement payBtn = visible(By.xpath("//div[contains(@class,'modal')]//button[contains(.,'Thanh toán viện phí')]"));
        payBtn.click();
        if (!driver.findElements(By.xpath("//*[contains(.,'Thiếu REACT_APP_STRIPE_PUBLISHABLE_KEY')]")).isEmpty()) {
            Assert.fail("Thiếu REACT_APP_STRIPE_PUBLISHABLE_KEY ở FE → không thể test Stripe thật.");
        }
    }

    /** Chọn iframe Payment Element đã render (height đủ lớn) và switch vào. */
    private WebElement waitStripeIframeReady() {
        wait.until(d -> !d.findElements(By.cssSelector(
                "iframe[name^='__privateStripeFrame'], iframe[src*='stripe'], iframe[title*='payment'], iframe[title*='Secure']"
        )).isEmpty());

        WebElement good = new WebDriverWait(driver, Duration.ofSeconds(25)).until(d -> {
            List<WebElement> frames = d.findElements(By.cssSelector(
                    "iframe[name^='__privateStripeFrame'], iframe[src*='stripe'], iframe[title*='payment'], iframe[title*='Secure']"
            ));
            for (WebElement f : frames) {
                try {
                    if (!f.isDisplayed()) continue;
                    if (f.getRect().getHeight() >= 200) return f;
                } catch (Exception ignore) {}
            }
            return null;
        });

        driver.switchTo().defaultContent();
        driver.switchTo().frame(good);

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> !d.findElements(By.cssSelector("input, body")).isEmpty());

        return good;
    }
    /** Đọc trạng thái của 1 invoice chính xác từ API; trả về UPPERCASE hoặc "NOT_FOUND"/"ERR:..." */
    private String getInvoiceStatusAPI(String invoiceNo) {
        Object rs = ((JavascriptExecutor) driver).executeAsyncScript("""
        var done = arguments[arguments.length - 1];
        (async () => {
          try {
            const base = '%s';
            const token = localStorage.getItem('token');
            if (!token) return 'ERR:NO_TOKEN';
            const r = await fetch(base + '/api/billing/invoices', { headers:{ Authorization:'Bearer '+token }});
            const arr = await r.json();
            const it = arr.find(x => String(x.invoiceNo) === '%s');
            if (!it) return 'NOT_FOUND';
            return String(it.status || '').trim().toUpperCase();
          } catch(e){ return 'ERR:'+e.message; }
        })().then(done).catch(e => done('ERR:'+e.message));
    """.formatted(BE_URL, invoiceNo));
        return String.valueOf(rs);
    }
    /** Chờ tối đa durationSec, KHÔNG để invoice chuyển thành PAID; true = chưa từng là PAID */
    private boolean waitInvoiceNeverPaidAPI(String invoiceNo, int durationSec) {
        int loops = Math.max(1, durationSec / 2);
        for (int i = 0; i < loops; i++) {
            String st = getInvoiceStatusAPI(invoiceNo);
            if ("PAID".equals(st)) return false; // phát hiện PAID -> fail ngay
            sleep(2000);
        }
        return true;
    }
    private String getRowStatusUI(String invoiceNo) {
        for (WebElement r : listInvoiceRows()) {
            String id = "";
            try { id = r.findElement(By.cssSelector("td:nth-child(1)")).getText().trim(); } catch (Exception ignored) {}
            if (!invoiceNo.equals(id)) continue;
            WebElement st;
            try { st = r.findElement(By.cssSelector("td:nth-child(4)")); }
            catch (Exception e) { st = r.findElement(By.cssSelector("td:nth-child(5)")); }
            return st.getText().trim().toUpperCase();
        }
        return "NOT_FOUND";
    }

    /** NHẬP THẺ (robust) – ưu tiên field cụ thể, fallback gõ qua body. */
    private void fillStripeCardWith(String card, String expMMYY, String cvc) {
        RuntimeException last = null;

        List<By> cardSelectors = List.of(
                By.cssSelector("input[name='cardnumber']"),
                By.cssSelector("input[autocomplete='cc-number']"),
                By.cssSelector("input[data-elements-stable-field-name='cardNumber']")
        );
        List<By> expSelectors = List.of(
                By.cssSelector("input[name='exp-date']"),
                By.cssSelector("input[autocomplete='cc-exp']"),
                By.cssSelector("input[data-elements-stable-field-name='cardExpiry']")
        );
        List<By> cvcSelectors = List.of(
                By.cssSelector("input[name='cvc']"),
                By.cssSelector("input[autocomplete='cc-csc']"),
                By.cssSelector("input[data-elements-stable-field-name='cardCvc']")
        );

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                boolean typed = false;

                for (By by : cardSelectors) {
                    List<WebElement> els = driver.findElements(by);
                    if (!els.isEmpty()) { els.get(0).sendKeys(card); typed = true; break; }
                }
                for (By by : expSelectors) {
                    List<WebElement> els = driver.findElements(by);
                    if (!els.isEmpty()) els.get(0).sendKeys(expMMYY);
                }
                for (By by : cvcSelectors) {
                    List<WebElement> els = driver.findElements(by);
                    if (!els.isEmpty()) els.get(0).sendKeys(cvc);
                }

                if (!typed) {
                    WebElement body = new WebDriverWait(driver, Duration.ofSeconds(10))
                            .until(d -> d.findElement(By.cssSelector("body")));
                    body.click();
                    body.sendKeys(card.replace(" ", ""));
                    body.sendKeys(expMMYY);
                    body.sendKeys(cvc);
                }
                return;
            } catch (RuntimeException e) {
                last = e;
                sleep(1200);
                driver.switchTo().defaultContent();
                waitStripeIframeReady();
            }
        }
        if (last != null) throw last;
    }

    private void clickConfirmInModal() {
        driver.switchTo().defaultContent();
        WebElement confirm = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[contains(@class,'modal')]//button[contains(.,'Xác nhận thanh toán')]")
                ));
        confirm.click();
    }

    private void handle3DSIfAny() {
        driver.switchTo().defaultContent();
        try {
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(10));
            w.until(d -> d.findElements(By.cssSelector(
                    "iframe[name*='challenge'], iframe[src*='3ds'], iframe[title*='challenge']"
            )).size() > 0);
        } catch (TimeoutException ignore) {
            return;
        }
        List<WebElement> frames = driver.findElements(By.cssSelector(
                "iframe[name*='challenge'], iframe[src*='3ds'], iframe[title*='challenge']"
        ));
        if (frames.isEmpty()) return;

        driver.switchTo().frame(frames.get(0));
        List<By> btns = List.of(
                By.xpath("//button[contains(.,'Complete') or contains(.,'Authorize') or contains(.,'Submit') or contains(.,'Tiếp tục')]"),
                By.cssSelector("button[type='submit']")
        );
        for (By by : btns) {
            List<WebElement> cand = driver.findElements(by);
            if (!cand.isEmpty()) { cand.get(0).click(); break; }
        }
        driver.switchTo().defaultContent();
        sleep(2000);
    }

    private void acceptSuccessAlertIfAny() {
        try {
            Alert alert = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.alertIsPresent());
            System.out.println("Alert text: " + alert.getText());
            alert.accept();
        } catch (TimeoutException ignored) {}
    }

    /** Poll API /invoices tới khi status = PAID (tối đa ~20s). */
    private boolean pollInvoicePaid(String invoiceNo) {
        Object rs = ((JavascriptExecutor) driver).executeAsyncScript("""
            var done = arguments[arguments.length - 1];
            (async () => {
              try {
                const base = '%s';
                const token = localStorage.getItem('token');
                if (!token) return 'ERR:NO_TOKEN';
                const limit = 10;
                for (let i=0;i<limit;i++){
                  const r = await fetch(base + '/api/billing/invoices', { headers:{ Authorization:'Bearer '+token }});
                  const arr = await r.json();
                  const it = arr.find(x => (''+x.invoiceNo).includes('%s'));
                  if (it && String(it.status).trim().toUpperCase()==='PAID') return 'PAID';
                  await new Promise(r => setTimeout(r, 2000));
                }
                return 'TIMEOUT';
              } catch(e){ return 'ERR:'+e.message; }
            })().then(done).catch(e => done('ERR:'+e.message));
        """.formatted(BE_URL, invoiceNo));
        return "PAID".equals(String.valueOf(rs));
    }
    /** Dismiss/accept bất kỳ alert nào nếu đang mở. Trả về true nếu có alert. */
    private boolean clearAnyAlertIfPresent(int timeoutSec) {
        try {
            Alert al = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                    .until(ExpectedConditions.alertIsPresent());
            System.out.println("Auto-handle alert: " + al.getText());
            // tuỳ trường hợp: accept cho nhanh
            al.accept();
            return true;
        } catch (TimeoutException ignore) {
            return false;
        }
    }

    /** Sau khi PAID qua API, refresh bảng và xác minh badge của đúng dòng. */
    private boolean waitRowStatusPaidUI(String invoiceNo) {
        for (int i = 0; i < 10; i++) {
            clearAnyAlertIfPresent(1);
            for (WebElement r : listInvoiceRows()) {
                String id = "";
                try { id = r.findElement(By.cssSelector("td:nth-child(1)")).getText().trim(); } catch (Exception ignored) {}
                if (!Objects.equals(id, invoiceNo)) continue;
                WebElement st;
                try { st = r.findElement(By.cssSelector("td:nth-child(4)")); }
                catch (Exception e) { st = r.findElement(By.cssSelector("td:nth-child(5)")); }
                String txt = st.getText().trim().toUpperCase();
                if (txt.contains("PAID") || txt.contains("COMPLETED")) return true;
            }
            sleep(3000);
        }
        return false;
    }


    /* ===== TEST: Thành công với 4242 (giữ nguyên) ===== */
    @Test
    public void test_Billing_Pay_First_Unpaid_StripeReal() {
        assertTrue("Cấu hình USE_STRIPE_REAL phải = true khi test thật", USE_STRIPE_REAL);
        loginUI();

        WebElement unpaidRow = findFirstUnpaidRow();
        assertNotNull("Không tìm thấy hóa đơn UNPAID (hãy seed 1 hóa đơn chưa thanh toán).", unpaidRow);
        String invoiceNo = getInvoiceNoFromRow(unpaidRow);
        openInvoiceDetail(unpaidRow);

        openStripeFromModalOrFailIfNoKey();
        waitStripeIframeReady();
        fillStripeCardWith(CARD_OK, "0127", "123");
        clickConfirmInModal();

        acceptSuccessAlertIfAny();
        closeDetailModalIfOpen();

        boolean paid = pollInvoicePaid(invoiceNo);
        assertTrue("Sau khi xác nhận, trạng thái chưa về PAID.", paid);
        assertTrue("UI chưa hiển thị PAID sau khi thanh toán thật.", waitRowStatusPaidUI(invoiceNo));
    }

    /* ===== TEST: Cancel flow — không đổi trạng thái ===== */
    @Test
    public void test_CancelPayment_KeepsStatusUnchanged() {
        loginUI();
        WebElement unpaidRow = findFirstUnpaidRow();
        assumeTrue("Cần ít nhất 1 UNPAID để test cancel.", unpaidRow != null);
        String invoiceNo = getInvoiceNoFromRow(unpaidRow);
        openInvoiceDetail(unpaidRow);

        // mở Stripe -> rồi đóng modal ngay
        openStripeFromModalOrFailIfNoKey();
        closeDetailModalIfOpen();
        clearAnyAlertIfPresent(2);  // << xử lý alert "Failed to fetch" nếu có

        // reload và đọc lại đúng dòng đó
        List<WebElement> rows = listInvoiceRows();
        WebElement same = rows.stream()
                .filter(r -> {
                    try { return r.findElement(By.cssSelector("td:nth-child(1)"))
                            .getText().trim().equals(invoiceNo); }
                    catch (Exception e) { return false; }
                }).findFirst().orElse(null);
        assertNotNull("Không tìm thấy lại hóa đơn sau khi cancel.", same);

        WebElement st;
        try { st = same.findElement(By.cssSelector("td:nth-child(4)")); }
        catch (Exception e) { st = same.findElement(By.cssSelector("td:nth-child(5)")); }
        String txt = st.getText().trim().toUpperCase();

        assertTrue("Cancel mà trạng thái đổi khác UNPAID/INPROGRESS: " + txt,
                txt.equals("UNPAID") || txt.equals("INPROGRESS"));
    }


    /* ===== TEST: Thẻ bị từ chối — không về PAID ===== */
    @Test
    public void test_DeclinedCard_DoesNotBecomePaid() {
        loginUI();
        WebElement unpaidRow = findFirstUnpaidRow();
        assumeTrue("Cần ít nhất 1 UNPAID để test declined.", unpaidRow != null);

        String invoiceNo = getInvoiceNoFromRow(unpaidRow);
        String initialStatus = getInvoiceStatusAPI(invoiceNo);
        assertTrue("Invoice ban đầu không phải UNPAID/INPROGRESS mà là: " + initialStatus,
                "UNPAID".equals(initialStatus) || "INPROGRESS".equals(initialStatus));

        openInvoiceDetail(unpaidRow);
        openStripeFromModalOrFailIfNoKey();
        waitStripeIframeReady();
        fillStripeCardWith(CARD_DECL, "0127", "123");  // thẻ bị từ chối
        clickConfirmInModal();

        // Có thể có alert/inline error → chỉ cần đảm bảo KHÔNG trở thành PAID trong ~20s
        acceptSuccessAlertIfAny(); // nếu có alert thì đóng
        closeDetailModalIfOpen();

        boolean neverPaid = waitInvoiceNeverPaidAPI(invoiceNo, 20);
        assertTrue("Dùng thẻ bị từ chối mà invoice lại thành PAID.", neverPaid);

        // Xác minh UI cũng không hiển thị PAID
        driver.get(FE_URL + "/billing");
        String uiStatus = getRowStatusUI(invoiceNo);
        assertTrue("UI hiển thị sai trạng thái sau declined: " + uiStatus,
                "UNPAID".equals(uiStatus) || "INPROGRESS".equals(uiStatus));
    }

    /* ===== TEST: Dòng đã PAID không có nút thanh toán ===== */
    @Test
    public void test_AlreadyPaidRow_HasNoPayButtonInModal() {
        loginUI();

        // Tìm bất kỳ dòng PAID
        WebElement paidRow = null;
        for (WebElement r : listInvoiceRows()) {
            WebElement st = null;
            try { st = r.findElement(By.cssSelector("td:nth-child(4)")); } catch (Exception ignored) {}
            if (st == null) try { st = r.findElement(By.cssSelector("td:nth-child(5)")); } catch (Exception ignored) {}
            if (st == null) continue;
            if (st.getText().trim().equalsIgnoreCase("PAID")) { paidRow = r; break; }
        }
        assumeTrue("Không có dòng PAID để kiểm tra.", paidRow != null);

        // Mở chi tiết và xác minh không thấy nút "Thanh toán viện phí"
        openInvoiceDetail(paidRow);
        boolean hasPayBtn = !driver.findElements(
                By.xpath("//div[contains(@class,'modal')]//button[contains(.,'Thanh toán viện phí')]")
        ).isEmpty();
        closeDetailModalIfOpen();
        assertFalse("Hóa đơn PAID nhưng vẫn hiện nút thanh toán.", hasPayBtn);
    }
}
