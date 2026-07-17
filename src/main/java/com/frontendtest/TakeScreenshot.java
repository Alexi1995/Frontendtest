package com.frontendtest;

import java.nio.file.Paths;
import com.microsoft.playwright.Page;

/**
 * Tato třída řeší tvorbu a ukládání screenshotů během testování webové aplikace.
 * Obrázky se ukládají do složky target/site/ pro snadnou integraci s testovacími reporty.
 * @author Alexandr
 * @version 1.0
 */
public class TakeScreenshot {
    private final Page page;

    /**
     * Konstruktor pro inicializaci stránky.
     * @param page Instance Playwright stránky, na které se budou provádět akce
     */
    public TakeScreenshot(Page page) {
        this.page = page;
    }

    /**
     * Počká 2 sekundy na stabilizaci prvků a vytvoří snímek obrazovky.
     * Obrázek se automaticky ukládá do složky target/site/ k test reportům.
     * @param filename Název souboru obrázku (např. login_step.png)
     */
    public void takeScreenshot(String filename) {
        page.waitForTimeout(2000); // Počkáme 2 sekundy, aby se stránka stabilizovala
        System.out.println("Dělám screenshot: " + filename);
        
        String path = filename.startsWith("target/") ? filename : "target/site/" + filename;
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)));
    }
}
