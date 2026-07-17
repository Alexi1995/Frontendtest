package com.frontendtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.microsoft.playwright.Locator;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;

import java.sql.Connection;

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

    public void verifyFormSubmitted(String expectedText) {
        Locator thankYouMessage = page.getByText(expectedText, new Page.GetByTextOptions().setExact(false));
        
        // Explicitně počkáme, až se element objeví v DOMu a bude viditelný
        thankYouMessage.waitFor();
        
        // Poté zkontrolujeme stav
        assertTrue(thankYouMessage.isVisible(), "Chyba: Děkovná zpráva se neobjevila!");
    }

    public void verifyDatabaseAndGetCustomerId(String expectedEmail, String expectedFirstName, int expectedUsers) {
        // 1. Nastavení připojení (ujisti se, že adresa a port jsou správně)
        String dbUrl = "jdbc:postgresql://localhost:5432/tvoje_databaze"; 
        String dbUser = "db_user";
        String dbPassword = "db_password";

        // SQL dotaz: Najdeme vyplněná data a zároveň vytáhneme 'cust_id'
        String sqlQuery = "SELECT cust_id, first_name, number_of_users FROM contact_forms WHERE email = ?";

        System.out.println("Připojuji se k databázi...");

        // OBRANA PROTI TIMEOUTU: Nastavíme maximální čas na připojení na 5 sekund.
        // Pokud se nepodaří připojit do 5s, test okamžitě spadne s jasnou chybou, nebudeme čekat minutu.
        DriverManager.setLoginTimeout(5); 

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            // Dosadíme unikátní email do dotazu
            preparedStatement.setString(1, expectedEmail);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                
                // 2. Ověříme, že záznam v DB vůbec existuje
                if (!resultSet.next()) {
                    throw new AssertionError("Chyba: V databázi nebyl nalezen žádný záznam pro email: " + expectedEmail);
                }

                // Vytáhneme hodnoty z DB
                String dbFirstName = resultSet.getString("first_name");
                int dbUsers = resultSet.getInt("number_of_users");
                
                // KROK 3: Získání hodnoty 'cust_id'
                String custId = resultSet.getString("cust_id"); // nebo .getInt("cust_id") podle typu v DB

                // Ověříme správnost vyplněných dat
                assertEquals(expectedFirstName, dbFirstName, "Křestní jméno v DB neodpovídá tomu z formuláře!");
                assertEquals(expectedUsers, dbUsers, "Počet uživatelů v DB neodpovídá tomu z formuláře!");
                assertNotNull(custId, "Chyba: Sloupec 'cust_id' je v databázi prázdný (null)!");

                System.out.println("✓ KROK 1 & 2: Data v databázi úspěšně ověřena.");
                System.out.println("✓ KROK 3: Získané ID zákazníka (cust_id) je: " + custId);

            }

        } catch (SQLException e) {
            System.err.println("!!! Nepodařilo se připojit k databázi. Zkontroluj:");
            System.err.println("1. Jsi připojený na firemní VPN?");
            System.err.println("2. Běží databáze na správné adrese/portu: " + dbUrl);
            System.err.println("3. Jsou přihlašovací údaje správné?");
            
            throw new RuntimeException("Selhalo připojení k DB: " + e.getMessage(), e);
        }
    }
}
