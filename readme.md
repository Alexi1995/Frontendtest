# 🎭 Playwright Java Automation Framework (Onice Platform)

Automatizovaný testovací framework postavený na **Java 17**, **Playwright** a **JUnit 5** pro validaci přihlašovacího workflow a správy dokumentů na platformě Onice (`https://onice.io`).

Projekt plně využívá návrhový vzor **Page Object Model (POM)** pro maximální udržitelnost a čitelnost kódu.

---

## 🛠️ Architektura projektu

* `src/main/java/com/webproject/StartPage.java` – Page Object třída obsahující robustní a stabilizované metody pro interakci s prvky pro vstup na webovou stránku, klikání na tlačítka, ověřeni, že formulář se otevřel správně
* `src/main/java/com/webproject/TakeScreenshot.java` - Page Object třída obsahující metody pro tvorbu screenshotů, která se volá z více míst
* `src/main/java/com/webproject/FillForm.java` - Page Object třída obsahující metody pro práci s formulářem (vyplnění dat, poslání dat, kontrola úspěšného odeslání dat)
* `src/main/java/com/webproject/FillForm.java` - Třída obsahující metody pro práci s databází, kontroluje správné odeslání dat
* `src/test/java/com/webproject/AppTest.java` – Samotný testovací scénář validující kompletní workflow od vstupu na webovou stránku až po odeslání a ověření dat poslaných na server.

---

## 🚀 Lokální spuštění testů

Pro spuštění testu u vás na Macu s otevřeným grafickým oknem prohlížeče zadejte do terminálu:

```bash
mvn clean test -Dtest=AppTest -Dheadless=false surefire-report:report
mvn - nastartuje Maven
clean - smaže složku target, aby v následující exekuci nezůstala data z předchozího běhu testu
test - spouštěč testu
-Dtest=AppTest - spustí se jen AppTest.java, -D - dynamický parametr
-Dheadless=false - prohlížeč se nespustí na pozadí, ale budeme ho vidět na obrazovce
surefire-report:report = vezme XML soubory a vytvoří surface-report.html ve složce target/site/
```

---

## 📊 Automatické reporty a screenshoty

Projekt je nakonfigurován tak, že **každý běh testu** automaticky vygeneruje detailní výstupy do složky `target/site/`:

1. **`surefire-report.html`** – Kompletní grafický HTML report s výsledky a časy testu.
2. **`TEST-com.frontendtest.AppTest.xml`** – Kompletní grafický HTML report s textovými výstupy a výsledky z databáze.
3. **Screenshoty (`.png`)** – Vizuální ověření klíčových kroků testu zachycené přímo v průběhu přihlašování a mazání.

### Jak otevřít report na Macu:
```bash
open target/site/surefire-report.html
```
---

---

## Použité knihovny

1. **`com.microsoft.playwright.*`** – Hlavní knihovna pro automatizaci prohlížečů. Zajišťuje interakci s prvky (klikání, psaní), navigaci, zachytávání síťových požadavků (waitForResponse) a spolehlivou synchronizaci prvků pomocí Locator a AriaRole (testování přístupnosti).
2. **`org.junit.jupiter.api.*`** – Testovací framework, který definuje životní cyklus testů (anotace jako @Test, @BeforeEach). Poskytuje aserce (assertEquals, assertTrue, assertNotNull) pro ověření očekávaných výsledků v testech.
3. **`java.sql.*`** – Standardní rozhraní pro komunikaci s databázemi (Java Database Connectivity). Používá se pro "Blind part" testování – připojení k databázi Customer, spouštění SQL dotazů (PreparedStatement) a validaci dat v tabulce PotentialCustomers oproti datům odeslaným přes formulář.
4. **`java.time.LocalDate`** – Správa a validace dat v databázi (např. cust_contact_date).
5. **`java.nio.file.Paths`** – Práce se souborovým systémem, nezbytná pro správu cest k testovacím datům nebo souborům prohlížeče.
6. **`java.util.Arrays`** – Pomocné utility pro práci s poli v testovacích scénářích.

### Použité datové typy pro tvorbu proměnných

1. double
2. String
3. int
```
---