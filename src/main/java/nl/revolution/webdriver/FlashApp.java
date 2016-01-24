package nl.revolution.webdriver;

import org.openqa.selenium.WebElement;

public class FlashApp {

    public WebDriverFlashHelper browser;
    public WebElement flashApp;

    public FlashApp(WebDriverFlashHelper browser, WebElement flashApp) {
        this.browser = browser;
        this.flashApp = flashApp;
    }

    public FlashApp click(String objId) {
        browser.clickFlashItem(flashApp, objId);
        return this;
    }

}
