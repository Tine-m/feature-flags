# Lærervejledning

## Formål og afgrænsning

Øvelsen tager cirka 60–90 minutter med startkode og et eksisterende kendskab til React og Javalin. Til opsætning og GitHub bør afsættes ekstra tid.

Det faglige fokus er hyppig integration, automatiseret kvalitetssikring og pålidelig levering. Feature flag er et redskab, ikke en forudsætning for god CI/CD.

## Forberedelse

1. Start `after-feature-flag` med flag=false og kontroller den almindelige bestilling.
2. Hav `.env` og backendkoden åbne.
3. Kør backend- og frontendtestene før undervisningen.
4. Hold startversion og løsning i separate repos, hvis løsningen skal holdes tilbage.

## Demonstration, cirka 15 minutter

### Før: en eksisterende funktion

```bash
git switch before-feature-flag
docker compose up --build -d
```

Vis bestillingen på http://localhost:8080. Den viser to menuer til 200 kr. Rabatkoder findes endnu ikke.

### Efter implementering: funktionen er deployet og deaktiveret

```bash
git switch after-feature-flag
docker compose up --build -d
```

Sørg for `FEATURE_DISCOUNT_CODES=false` i `.env`, hvis filen findes. Genindlæs siden. Den eksisterende funktion virker stadig.

Åbn http://localhost:8080/api/features: `discountCodes` er false.
Vis i Postman: POST http://localhost:8080/api/orders/discount-preview, Content-Type application/json, body `{"code":"STUDENT10"}`. Svaret er 403.

Alternativ fra PowerShell (curl.exe) eller Bash (curl):

```bash
curl -i -X POST http://localhost:7070/api/orders/discount-preview -H 'Content-Type: application/json' -d '{"code":"STUDENT10"}'
```

Brug Postman på Windows, hvis terminalens håndtering af citationstegn giver problemer.

### Release uden nyt build

Notér backend-image-ID med `docker compose images -q backend`.
Sæt flaget til true i `.env`:

```bash
docker compose up -d --no-deps --force-recreate backend
```

Sammenlign image-ID: det er samme image. Genindlæs siden; brug STUDENT10. Prisen bliver 180 kr. En forkert kode giver en fejl og ændrer ikke prisen.

Sæt flag=false, og genopret backend igen. Genindlæs siden. Funktionen er igen utilgængelig. En allerede åben side kan stadig vise feltet, indtil den genindlæses, men backend afviser nye rabatkald. Den lokale visning af en tidligere beregnet rabat nulstilles ved genindlæsning. Dette er en preview-demo, ikke ordreafgivelse.

Løsningen kræver genstart/genoprettelse ved flagændringer. Den viser ikke dynamisk ændring af flag i en kørende proces. `docker compose restart` indlæser ikke en ændret miljøvariabel fra Compose.

### Oprydning

```bash
git switch after-cleanup
docker compose up --build -d
```

Funktionen er permanent tilgængelig. Flagklasse, konfigurationsendpoint, betinget React-visning, miljøvariabel og tests af flagtilstande er fjernet. Testene af rabatfunktionen er bevaret. Byg igen ved hvert branchskift; ellers kører tidligere kode stadig i containerne.

## Løsningsforklaring

| Fil | Ansvar |
| --- | --- |
| backend/.../FeatureFlags.java | Læser flaget; manglende/ukendt værdi bliver false |
| backend/.../Main.java | Læser miljøet ved opstart |
| backend/.../App.java | Routes og kontrol af flag før rabatkald |
| backend/.../DiscountService.java | Validering og beregning med BigDecimal |
| frontend/src/App.jsx | Henter pris og offentlig featurekonfiguration hver for sig |
| frontend/src/DiscountCodeForm.jsx | Sender kode, viser fejl og opdaterer pris |

Backend er autoritativ. Frontend henter konfiguration ved indlæsning; flaget kopieres ikke ind i Reacts build. Konfigurationsfejl skjuler rabatfunktionen, men forhindrer ikke den eksisterende bestillingsvisning.

Der returneres 403 som et bevidst undervisningsvalg, når funktionen er deaktiveret. Dette er ikke brugerautorisation. Et produktionssystem kan have andre API-konventioner og skal stadig have egentlig adgangskontrol, hvor det kræves. Kun offentlig featurekonfiguration må udstilles her, aldrig secrets.

## Sammenhæng med trunk-based udvikling

```bash
git log --oneline --reverse main
git diff before-feature-flag..after-feature-flag -- backend/src/main frontend/src
```

Historikken viser: fungerende start → konfiguration → backend bag flag → React bag flag. Hvert trin kan integreres separat. De navngivne branches fryser undervisningsstadier og simulerer ikke længerevarende featurebranches. I elevernes arbejde bruges korte branches og hyppig integration i main.

Funktionen er implementeret i hele after-versionen. Flag=false simulerer, at man endnu ikke ønsker at frigive den. De mellemste commits viser integration af delene undervejs. Et flag tillader ikke, at man merger kode, som ikke bygger, knækker opstart eller ødelægger databasen.

## Vejledende svar på refleksion

1. **Deployment/release:** Deployment sker, når den nye applikationsversion installeres; release sker, når flaget aktiveres og funktionen gøres tilgængelig.
2. **Hyppig integration:** Delene kan komme i main, før hele brugerfunktionen er klar. Eksisterende adfærd bevares.
3. **Backendkontrol:** En bruger kan kalde API'et uden React. En skjult knap er ikke en backendkontrol.
4. **Begrænsninger:** Flaget beskytter ikke mod fejl i opstart, fælles kode, dependency-opdateringer eller skadelige databasemigrationer. Deaktivering tilbagefører ikke allerede ændrede data.
5. **Fjernelse:** Når funktionen er stabilt og permanent frigivet, og behovet for midlertidig deaktivering er afklaret. Fjern flag og gammel kode med tests.
6. **Uden flag:** Små færdige funktioner eller bagudkompatible forberedelser kan integreres uden flags. Tilføj eventuelt brugeradgangen sidst.

## CI og CD

GitHub Actions udfører CI: Maven verify, frontendtests og frontendbuild. Der er ingen automatisk deployment til en ekstern server og ingen forudkonfigureret branch protection. Docker-demonstrationen udfører deployment lokalt. Kan kobles til jeres eksisterende CD senere. Kræv ikke, at eleverne lærer en ny hostingplatform i denne øvelse.

## Fejlsøgning

- Mangler rabatfeltet? Kontroller `/api/features`, `.env` og at backend er genoprettet. Genindlæs browseren.
- Kører gammel kode? Brug `docker compose up --build -d` efter branchskift.
- Port optaget? Stop den tidligere lokale backend eller Compose. Lokal Java og Compose bruger begge 7070.
- Tests med Maven kræver downloadadgang til Maven Central; npm kræver npm-registret første gang.
- Lokal opstart uden Docker: `.env` indlæses ikke automatisk af Java; angiv miljøvariablen i terminal eller IDE.

## Kilder

- https://martinfowler.com/articles/feature-toggles.html
- https://trunkbaseddevelopment.com/feature-flags/
- https://javalin.io/archive/docs/v6.7.X
- https://vite.dev/guide/
