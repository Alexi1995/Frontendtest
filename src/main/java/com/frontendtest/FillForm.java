package com.frontendtest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;

/**
 * Tato třída řeší vyplňování formuláře na webové stránce.
 * Obsahuje metody pro vyplnění textových polí, výběr z rozbalovacích seznamů a odeslání formuláře.
 * Po odeslání formuláře je možné ověřit, zda se zobrazila očekávaná děkovná zpráva.
 * @author Alexandr
 * @version 1.0 
 * FillForm
 */
public class FillForm {
    private final Page page;

    /**
     * Konstruktor pro inicializaci stránky.
     * @param page Instance Playwright stránky, na které se budou provádět akce
     */
    public FillForm(Page page) {
        this.page = page;
    }

    /**
     * Vyhledá textové pole podle jeho štítku (labelu) a vyplní do něj zadanou hodnotu.
     * @param info Text, který se má do pole zapsat (např. jméno, e-mail, telefonní číslo)
     * @param name Název štítku textového pole
     */
    public void fillInfo(String info, String name) {
        System.out.println("Vyplňuji " + name + ": " + info);
        // Hledáme s vypnutou striktní shodou (setExact false) pro větší stabilitu
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(name)).fill(info);
    }

    /**
     * Vyhledá textové pole podle jeho štítku (labelu) a vyplní do něj zadanou hodnotu.
     * @param numberOfUsers Počet uživatelů
     * @param name Název štítku textového pole
     */
    public void fillUsersInfo(int numberOfUsers, String name) {
        System.out.println("Vyplňuji " + name + ": " + numberOfUsers);
        // 1. Najdeme lokátor přímo jako typ Locator (bez jakéhokoliv přetypování)
        // Používáme parametr 'name' místo natvrdo zapsaného textu
        Locator numUsersInput = page.getByPlaceholder(
            name, 
            new Page.GetByPlaceholderOptions().setExact(false)
        );
        
        // 2. Klikneme a vyplníme hodnotu převedenou na String
        numUsersInput.click();
        numUsersInput.fill(String.valueOf(numberOfUsers));
    }

    /**
     * Vybere z rozbalovacího seznamu zadanou zemi podle jejího názvu.
     * @param name Název položky v seznamu (např. "Czech Republic")
     */
    public void selectFromList(String name, String locatorName) {
        // 1. Zacílíme na náš select element
        Locator countryDropdown = page.locator(locatorName);

        // 2. Počkáme, až bude na stránce viditelný a připravený k interakci
        countryDropdown.waitFor();

        // 3. Zjistíme, jaká hodnota je aktuálně vybraná (abychom neklikali zbytečně)
        // evaluate nám vrátí text momentálně vybrané <option>
        String currentSelection = (String) countryDropdown.evaluate("el => el.options[el.selectedIndex].text");

        if (currentSelection != null && currentSelection.trim().equalsIgnoreCase(name)) {
            System.out.println("Položka '" + name + "' je již vybrána. Přeskočeno.");
        } else {
            System.out.println("Položka '" + name + "' není vybrána (aktuálně: " + currentSelection + "). Vybírám...");
            
            // Playwright sám najde možnost podle textu a vybere ji
            countryDropdown.selectOption(new SelectOption().setLabel(name));
        }
    }

    /**
     * Klikne na tlačítko pro odeslání formuláře.
     * Před kliknutím provede explicitní scroll, aby bylo tlačítko viditelné a klikatelné.
     * @param buttonName Název tlačítka, na které se má kliknout (např. "Submit" nebo "Send")
     */
    public void submitForm(String buttonName) {
        // 1. Najdeme tlačítko (uprav selektor/text podle tvého reálného tlačítka, např. "Submit" nebo "Send")
        Locator submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(buttonName));
        
        System.out.println("Scrolluji k odesílacímu tlačítku...");
        
        // 2. EXPLICITNÍ SCROLL: Vynutí posun posuvníku tak, aby bylo tlačítko plně vidět
        submitButton.scrollIntoViewIfNeeded();
        
        // 3. Klikneme
        submitButton.click();
    }

    /**
     * Ověří, že se po odeslání formuláře zobrazí děkovná zpráva s očekávaným textem.
     * @param expectedText
     */
    public void verifyFormSubmitted(String expectedText) {
        Locator thankYouMessage = page.getByText(expectedText, new Page.GetByTextOptions().setExact(false));
        
        // Explicitně počkáme, až se element objeví v DOMu a bude viditelný
        thankYouMessage.waitFor();
        
        // Poté zkontrolujeme stav
        assertTrue(thankYouMessage.isVisible(), "Chyba: Děkovná zpráva se neobjevila!");
    }
    
}
