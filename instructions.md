# Kurs i Software Security Engineering

I denna kurs kommer du progressivt att lägga till säkerhetverktyg till ditt GitHub Actions-arbetsflöde. Ditt mål är att förstå implementering av säkerhetsverktyg, identifiera och förstå säkerhetssårbarheter i Todo-applikationen.

---

## Steg 1: Static Application Security Testing (SAST) med Semgrep

### 📚 Verktygsöversikt: Semgrep

**Vad är Semgrep?**

Semgrep är ett statisk analysverktyg som genomsöker din källkod för säkerhetssårbarheter, kodkvalitetsproblem och efterlevnadskränkningar **utan att köra koden**. Det använder mönsterbaserade regler för att upptäcka problem i din kodbas.

### 🔗 Dokumentation & Resurser

- **Semgrep officiell dokumentation:** https://semgrep.dev/docs/
- **Semgreps regelbibliotek:** https://semgrep.dev/r
- **Community vs Pro:** https://semgrep.dev/pricing

### ✅ Din uppgift: Steg 1

1. **Lägg till Semgrep i din `security.yml`** workflow fil
   - Använd Semgrep GitHub Action för community-versionen
   - Konfigurera den för att genomsöka katalogen `todo-app`
   - Ställ in den för att använda community-regeluppsättningen (gratis, öppen källkod)

2. **Kör GitHub Actions-arbetsflödet**
   - Navigera till fliken **Actions** i din GitHub-databas
   - Vänta på att Semgrep-jobbet är klart och blir godkänt

3. **Granska resultaten**
   - Gå till fliken **Security** → **Code scanning alerts**
   - Undersök sårbarheten som hittades av Semgrep
   - Notera allvarlighetsgraden och beskrivningar av några fynd

4. **Reflektionsfråga:**

   > **Har du identifierat något konstigt eller oväntat i Semgreps resultat?**
   > 
   > - Finns det falska positiva?
   > - Hittade det säkerhetsproblem du förvände dig?
   > - Finns det några ovanliga mönster?
   > 

### ⏱️ Om du har tid över...

**Utmaning:** Försök att åtgärda en eller två av sårbarheter som identifieras av Semgrep i `todo-app`-källkoden.

---

## Step 2: Dependency Vulnerability Scanning med Dependabot

### 📚 Verktygsöversikt: Dependabot

**Vad är Dependabot?**

Dependabot är GitHubs inbyggda beroendehanteringsverktyg som automatiskt genomsöker ditt projekts beroenden för kända säkerhetssårbarheter. Det övervakar biblioteken som används i din applikation och varnar dig när sårbarheter upptäcks.

### 🔗 Dokumentation & Resurser

- **Dependabot-dokumentation:** https://docs.github.com/en/code-security/dependabot
- **Dependabot-konfiguration:** https://docs.github.com/en/code-security/dependabot/dependabot-version-updates/about-dependabot-version-updates
- **GitHub-säkerhetsbulletiner:** https://github.blog/security/

### ✅ Din uppgift: Steg 2

1. **Aktivera Dependabot via GitHub**
   - Navigera till repot
   - Gå till **Settings** → **Code security and analysis**
   - Aktivera **Dependabot alerts**
   - Aktivera **Dependabot security updates** (valfritt, tillåter auto-PR-skapande)

2. **Kontrollera Dependabot-resultaten**
   - Navigera till fliken **Security** → **Dependabot alerts** → **verifiera** att det står _Dependency files checked x min ago_
   - Granska alla beroendesårbarheter som identifieras
   - Kontrollera allvarlighetsgrad och påverkade versioner av bibliotek
   - Notera vilka beroenden i din `pom.xml` som har säkerhetsproblem

3. **Exandera Dependabot**
   - Slå på PR skapande för **säkerhetsuppdateringar** och **vanliga uppdateringar**

4. **Reflektionsfråga:**

   > **Vilka beroenden rapporterar Dependabot som sårbara?**
   > 
   > - Påverkar något av dessa beroenden direkt ditt Todo app-funktioner?
   > - Finns det nyare versioner tillgängliga?
   > 
### ⏱️ Om du har tid över...

**Utmaning:** Uppdatera ett eller flera sårbara beroenden till deras patchade versioner.

- Identifiera ett sårbart beroende med en tillgänglig patch
- Uppdatera versionen i din `pom.xml`
- Kör `mvn clean verify` lokalt för att säkerställa ingen brytande ändringar
- Arkivera uppdateringen och push för att utlösa Dependabot att genomsöka igen
- Verifiera att aviseringen är löst

---

## Steg 3: Containerbildöversökning med Trivy

### 📚 Verktygsöversikt: Trivy

**Vad är Trivy?**

Trivy är en omfattande sårbarhetsöversöksverktyg utvecklat av Aqua Security som genomsöker containeravbildningar, filsystem och källkod för säkerhetsproblem. Det är lätt, snabbt och kräver ingen tidigare konfiguration eller externa databaser.

### 🔗 Dokumentation & Resurser

- **Trivys officiella dokumentation:** https://aquasecurity.github.io/trivy/
- **Trivys GitHub-databas:** https://github.com/aquasecurity/trivy
- **Trivys säkerhetsbästa praxis:** https://aquasecurity.github.io/trivy/latest/

### ✅ Din uppgift: Steg 3

1. **Lägg till Trivy i ditt `security.yml` workflow**
   - Integrera Trivy i ditt GitHub Actions workflow
   - Konfigurera den för att genomsöka Docker-avbildningen du bygger från din `Dockerfile`
   - Ställ in den för att misslyckas på kritiska eller höga sårbarheter (konfigurerbar)

2. **Kör GitHub Actions-arbetsflödet**
   - Pusha till main
   - Navigera till fliken **Actions**
   - Vänta på att Trivy-skanningsjobbet är klart

3. **Granska containeröversiktsresultat**
   - Navigera till fliken **Security** → **Code scanning alerts**
   - Leta efter sårbarheter märkta som "Trivy" eller "container"
   - Undersök sårbarheter i basavbildningen

4. **Reflektionsfråga:**

   > **Vilka sårbarheter hittades i din containeravbildning?**
   > 
   > - Finns sårbarheter i basavbildningen (Alpine, Ubuntu) eller din applikation?
   > - Hur jämför detta med kodsårbarheter som hittats av Semgrep?
   > - Finns det felkonfigurationer i Dockerfile?
   > 
### ⏱️ Om du har tid över...

**Utmaning:** Härdade din Dockerfile för att minska sårbarhetskällan.

---

## Steg 4: Hemlighetdetektering med Trufflehog

### 📚 Verktygsöversikt: Trufflehog

**Vad är Trufflehog?**

Trufflehog är ett hemligetsökningsverktyg utvecklat av Truffle Security som detekterar hemligheter, autentiseringsuppgifter och känslig information som kan ha committats till ditt repo. Det använder mönstermatchning och entropianalys för att hitta exponerade autentiseringsuppgifter.

### 🔗 Dokumentation & Resurser

- **Trufflehogg officiell dokumentation:** https://trufflesecurity.com/trufflehog
- **Trufflehogs GitHub-repo:** https://github.com/trufflesecurity/trufflehog
- **Autentiseringsuppgifthantering bästa praxis:** https://docs.github.com/en/code-security/secret-scanning

### ✅ Din uppgift: Steg 4

1. **Lägg till Trufflehog i ditt `security.yml`workflow**
   - Integrera Trufflehog i ditt GitHub Actions-workflow
   - Konfigurera det för att genomsöka hela ditt repo
   - Ställ in det för att också kontrollera repo historiken

2. **Kör GitHub Actions-arbetsflödet**
   - Push ändringarna för att utlösa arbetsflödet
   - Navigera till fliken **Actions**
   - Vänta på att Trufflehog-skanningsjobbet är klart

3. **Granska hemlighetdetekteringsresultaten**
   - Kontrollera workflowloggarna för detekterade hemligheter
   - Leta efter falska positiva (detekterade hemligheter som inte är verkliga)

4. **Reflektionsfråga:**

   > **Hittade Trufflehog några hemligheter eller autentiseringsuppgifter?**
   > 
   > - Är de falska positiva (inte verkliga hemligheter)?
   > - Om verkliga autentiseringsuppgifter hittades, hur skulle du åtgärda det?
   > - Finns det några autentiseringsuppgifter i din exempelkod eller testfiler?
   > 
### ⏱️ Om du har tid över...

**Utmaning:** Implementera säker hantering av autentiseringsuppgifter i din applikation.

- Om hemligheter upptäcktes, ta bort dem från kodbasen
- Lägg till `.env` i din `.gitignore` för att förhindra oavsiktlig arkivering
- Använd GitHub Secrets för CI/CD-autentiseringsuppgifter istället
- Kör Trufflehog igen för att bekräfta att hemligheter är borttagna

---

## Kursslutsammanfattning

### 📊 Vad du har uppnått

Genom att slutföra alla fyra steg har du implementerat en säkerhetstoolningpipeline som täcker majoriteten av riskerna:

1. **SAST (kodlevel)** - Semgrep: Kodsårbarheter och mönster
2. **Beroendeöversökning** - Dependabot: Sårbarheter i bibliotek från tredje part
3. **Containersäkerhet** - Trivy: Sårbarheter i images och infrastruktur
4. **Hemlighetdetektering** - Trufflehog: Exponerade autentiseringsuppgifter och hemligheter

### 🎯 Viktiga lärdomar

- **Skiktad säkerhet:** Flera verktyg fångar olika typer av sårbarheter
- **Kontinuerlig övervakning:** Automatiserad skanning fångar problem tidigt
- **Utvecklarens ansvar:** Att förstå och åtgärda problem är avgörande
- **Gemensamt ansvar:** Säkerhet är en teaminsats över utveckling, ops och säkerhet

### 📚 Nästa steg (valfritt)

Om du vill fördjupa din säkerhetskompetens:

- Utforska **SBOM (Software Bill of Materials)**-generering med verktyg som CycloneDX

### 🔗 Användbara resurser

- **OWASP Top 10:** https://owasp.org/www-project-top-ten/
- **CWE Top 25:** https://cwe.mitre.org/top25/
- **GitHub-säkerhetsdokumentation:** https://docs.github.com/en/code-security
- **DevSecOps-principer:** https://www.devsecops.org/
