package nl.revolution.webdriver;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.Optional;

public class WebDriverFlashHelper {

    private WebDriver driver;
    private JavascriptExecutor jsDriver;

    public WebDriverFlashHelper() {
        new WebDriverFlashHelper(new FirefoxDriver());
    }

    public WebDriverFlashHelper(WebDriver webDriver) {
        this.driver = webDriver;
        jsDriver = (JavascriptExecutor)driver;

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    if (driver != null) {
                        driver.quit();
                    }
                } catch (Exception e) {
                    System.out.println("Stopping WebDriver failed: " + e.getStackTrace());
                }
            }
        });
    }

    public void closeWebDriver() {
        driver.quit();
    }

    public void clickButtonWithLabel(String label) {
        driver.findElements(By.tagName("input")).stream()
                .filter(button -> label.equals(button.getAttribute("value")))
                .findFirst().get().click();
    }

    public void openUrl(String url) {
        driver.get(url);
    }

    public void clickButtonWithId(String id) {
        driver.findElement(By.id(id)).click();
    }
    public void typeText(String id, String text) {
        driver.findElement(By.id(id)).sendKeys(text);
    }

    public WebElement waitForFlashAppToLoad(String flashAppId, String flashItemId) {
        WebElement flashApp = driver.findElement(By.id(flashAppId));
        for (int i=0; i<50; i++) {
            String flashObjectId = String.valueOf(jsDriver.executeScript("return arguments[0].findID('" + flashItemId + "');", flashApp));
            if (!"0".equals(flashObjectId)) {
                // Wait a bit more to finish loading.
                sleep(500);
                return flashApp;
            }

            sleep(100);
        }
        throw new NoSuchElementException("Flash item '" + flashItemId + "' not found.");
    }

    public void clickFlashItem(WebElement flashApp, String flashItemId) {
        String flashObjectId = String.valueOf(jsDriver.executeScript("return arguments[0].findID('" + flashItemId + "');", flashApp));
        if ("0".equals(flashObjectId)) {
            throw new NoSuchElementException("Flash item '" + flashItemId + "' not found.");
        }
        jsDriver.executeScript("arguments[0].click(arguments[1]);", flashApp, flashObjectId);
    }

    public void clickLinkWithText(String linkText) {
        findByLinkText(linkText).click();
    }

    public void selectDropdown(String id, String text) {
        for (int i=0;i<10;i++) {
            try {
                new Select(driver.findElement(By.id(id))).selectByVisibleText(text);
            } catch (NoSuchElementException e) {
                sleep(100);
            }
        }
    }

    public WebElement findByLinkText(String linkText) {
        Optional<WebElement> element = driver.findElements(By.tagName("a")).stream()
                .filter(elem -> elem.getText() != null)
                .filter(elem -> elem.getText().equals(linkText))
                .findFirst();
        if (element.isPresent()) {
            return element.get();
        }

        // didn't find anything..
        throw new NoSuchElementException("Link with text '" + linkText + "' not found.");
    }

    protected void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
