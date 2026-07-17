package com.frontendtest;
import com.microsoft.playwright.*;

import java.util.Arrays;

import org.junit.jupiter.api.*;

/**
 * Hlavní třída pro automatizované testování webové aplikace Onice.
 * Obsahuje workflow pro přihlášení uživatele pomocí Playwright.
 */
public class AppTest {
    /**
     * Výchozí konstruktor pro testovací třídu AppTest.
     */
    public AppTest() {
        // Inicializace testu
    }
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
    
    Browser browser = playwright.chromium().launch(
        new BrowserType.LaunchOptions()
            .setHeadless(false)
            .setArgs(Arrays.asList(
                "--disable-gpu", 
                "--disable-dev-shm-usage",
                "--no-sandbox",
                "--start-maximized" // Přidáno pro maximalizaci okna
            ))
    );
        BrowserContext context = browser.newContext(
            new Browser.NewContextOptions().setViewportSize(null)
        );
        page = context.newPage();
        page.setDefaultNavigationTimeout(90000); // 90 sekund na načtení úvodní stránky webu
        page.setDefaultTimeout(60000);           // 60 sekund na objevení jakéhokoliv tlačítka/pole
    }


    /**
     * Komplexní testovací scénář pro ověření funkčnosti přihlašovacího formuláře.
     * Krok za krokem prochází zadáním e-mailu, hesla a kontroluje stavy pomocí screenshotů.
     */
    @Test
    @DisplayName("Ověření úspěšného přihlášení uživatele")
    void testLoginWorkflow() {
        String webName = "https://icewarp.com";
        String contact = "Contact sales";
        String hasText = "Our Sales team will contact"; // Text, který by měl být přítomen v otevřeném formuláři
        int index = 1; // Index tlačítka, pokud se na stránce vyskytuje více tlačítek se stejným textem

        StartPage startPage = new StartPage(page);
        //ApplicationTesting applicationTesting = new ApplicationTesting(page);
        TakeScreenshot takeScreenshot = new TakeScreenshot(page);
        
        /** Krok 1: Otevření přihlašovací stránky*/
        startPage.navigateTo(webName);
        takeScreenshot.takeScreenshot("target/site/start.png");

        /** Krok 2: Kliknutí na tlačítko "Contact sales" a kontrola, zda se otevře na pravé straně*/
        startPage.clickButton(contact, index);
        startPage.checkFormOpenedRight(hasText, index);
        takeScreenshot.takeScreenshot("target/site/button_clicked.png");

        // Poznámka: page.pause() je pro automatický report vynechán, 
        // aby se test v CI/CD terminálu nezasekl. Pokud ho chcete, odkomentujte:
        page.pause();
    }

    @AfterEach
    void tearDown() {
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
