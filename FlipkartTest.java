package test;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Set;

public class FlipkartTest {

    public static void main(String[] args) throws IOException {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("https://www.flipkart.com");

        // 🔹 Close Login Popup
        try {
            WebElement closeBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[text()='✕']")));
            closeBtn.click();
            System.out.println("Login popup closed.");
        } catch (Exception e) {
            System.out.println("Login popup not displayed.");
        }

        // 🔹 Search Product
        WebElement searchBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.name("q")));
        searchBox.sendKeys("Bluetooth Speakers");
        searchBox.sendKeys(Keys.ENTER);

        // 🔹 Click First Product
        WebElement firstProduct = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("(//a[contains(@href,'/p/')])[1]")));
        firstProduct.click();

        // 🔹 Switch to new tab
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            driver.switchTo().window(handle);
        }

        boolean productAvailable = false;

        try {
            WebElement addToCart = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//button[contains(text(),'Add to cart')]")));

            if (addToCart.isDisplayed() && addToCart.isEnabled()) {
                productAvailable = true;
            }

        } catch (Exception e) {
            productAvailable = false;
        }

        if (productAvailable) {

            // ✅ SCENARIO 1
            driver.findElement(By.xpath("//button[contains(text(),'Add to cart')]")).click();
            System.out.println("Product added to cart.");

            wait.until(ExpectedConditions.urlContains("viewcart"));

            takeScreenshot(driver, "cart_result.png");

        } else {

            // ✅ SCENARIO 2
            System.out.println("Product unavailable — could not be added to cart.");
            takeScreenshot(driver, "result.png");
        }

        driver.quit();
    }

    // 📸 Screenshot Method
    public static void takeScreenshot(WebDriver driver, String fileName) throws IOException {

        String projectPath = System.getProperty("user.dir");
        String screenshotPath = projectPath + "\\screenshots";

        File folder = new File(screenshotPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File dest = new File(screenshotPath + "\\" + fileName);

        FileUtils.copyFile(src, dest);

        System.out.println("Screenshot saved at: " + dest.getAbsolutePath());
    }
}