package com.frontendtest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.microsoft.playwright.*;
import com.microsoft.playwright.Locator;


/**
 * Tato třída řeší interakci s úvodní stránkou webové aplikace.
 * Obsahuje metody pro otevření webové adresy, kliknutí na tlačítko a ověření, zda se otevřel formulář na pravé straně stránky.
 * @author Alexandr
 * @version 1.0
 * StartPage
 */
public class StartPage {
    private final Page page;

    /**
     * Konstruktor pro inicializaci stránky.
     * @param page Instance Playwright stránky, na které se budou provádět akce
     */
    public StartPage(Page page) {
        this.page = page;
    }

    /**
     * Otevře zadanou webovou adresu a počká na vykreslení formuláře.
     * @param hostname Kompletní URL adresa (např. https://onice.io)
     */
    public void navigateTo(String hostname) {
        System.out.println("Otevírám Chromium a přicházím na: " + hostname);
        page.navigate(hostname);
    }

    /**
     * Najde na stránce tlačítko s odpovídajícím textem a klikne na něj.
     * @param ButtonToClick Text zobrazený na tlačítku
     * @param index Index tlačítka, pokud se na stránce vyskytuje více tlačítek se stejným textem (0 = první, 1 = druhé, ...)
     */
    public void clickButton(String ButtonToClick, int index) {
        System.out.println("Klikám na tlačítko: " + ButtonToClick);
        // Hledá element, který má roli tlačítka a obsahuje daný text
        page.getByText(ButtonToClick).nth(index).click();
    }

    /**
     * Zkontroluje, zda se po kliknutí na tlačítko otevře formulář na pravé straně stránky.
     * @param hasText Text, který by měl být přítomen v otevřeném formuláři (např. "Contact sales")
     * @param index Index tlačítka, pokud se na stránce vyskytuje více tlačítek se stejným text
     */
    public void checkFormOpenedRight(String hasText, int index) {
        // 1. Definujeme si lokátor pro element
        Locator rightPanel = page.locator("div")
            .filter(new Locator.FilterOptions().setHasText(hasText))
            .nth(index);

        // 2. Ověříme, že je element na stránce vůbec viditelný
        rightPanel.waitFor(); // Počkáme, až se vykreslí
        assertTrue(rightPanel.isVisible(), "Element se na stránce vůbec nezobrazil!");

        // 3. Bezpečně zjistíme celkovou šířku okna prohlížeče přes JavaScript
        // (Tím se vyhneme chybě s null u page.viewportSize())
        int windowWidth = (int) page.evaluate("window.innerWidth");

        // 4. Získáme souřadnice (polohu) našeho elementu
        // boundingBox().x nám říká, jak daleko od levého okraje obrazovky (v pixelech) element začíná
        double elementXPosition = rightPanel.boundingBox().x;

        System.out.println("Šířka okna: " + windowWidth + "px");
        System.out.println("Element začíná na X souřadnici: " + elementXPosition + "px");

        // 5. Ověříme, že element začíná v pravé polovině obrazovky
        // (Pokud je jeho X pozice větší než polovina šířky okna, je zaručeně vpravo)
        assertTrue(
            elementXPosition > (windowWidth / 2), 
            "Chyba: Element se nezobrazil vpravo! Začíná na pozici " + elementXPosition + "px při šířce okna " + windowWidth + "px."
        );

        System.out.println("Ověřeno: Element se úspěšně zobrazil v pravé části obrazovky.");
    }
}
