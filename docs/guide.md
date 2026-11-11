# Deploy før release

Java 21, React og Javalin. Ingen database.

## Acceptkriterier

- Den eksisterende bestilling på 200 kr. virker fortsat.
- Backend læser FEATURE_DISCOUNT_CODES ved opstart; standard=false.
- GET /api/features returnerer `{ "discountCodes": false }` eller true.
- React viser kun rabatfeltet, når konfigurationen udtrykkeligt er true. Konfigurationsfejl skal ikke blokere bestillingen.
- POST /api/orders/discount-preview modtager `{ "code": "STUDENT10" }`.
- Flag=false: 403, også ved direkte API-kald.
- Flag=true og korrekt kode: 200 med originalTotal=200, discount=20, total=180.
- Forkert eller manglende kode: 400.
- Serveren fastlægger prisen og beregner rabatten. Brug BigDecimal til beløb.

## Rækkefølge 
Start med branchen `before-feature-flag`. 

```bash
git switch before-feature-flag
docker compose up --build -d
```

Vis bestillingen på http://localhost:8080. Den viser to menuer til 200 kr. Rabatkoder findes endnu ikke.

Prøv så branch `after-feature-flag` med flag=false og kontroller den almindelige bestilling. 

```bash
git switch after-feature-flag
docker compose up --build -d
```

Sørg for `FEATURE_DISCOUNT_CODES=false` i `.env`, hvis filen findes. Genindlæs siden. Den eksisterende funktion virker stadig.

Åbn http://localhost:8080/api/features: `discountCodes` er false.
Vis i Postman: POST http://localhost:8080/api/orders/discount-preview, Content-Type application/json, body `{"code":"STUDENT10"}`. Svaret er 403.

### Release uden nyt build

Notér backend-image-ID med `docker compose images -q backend`.
Sæt flaget til true i `.env`:

```bash
docker compose up -d --no-deps --force-recreate backend
```

Sammenlign image-ID: det er samme image. Genindlæs siden; brug STUDENT10. Prisen bliver 180 kr. En forkert kode giver en fejl og ændrer ikke prisen.

Sæt flag=false, og genopret backend igen. Genindlæs siden. Funktionen er igen utilgængelig. En allerede åben side kan stadig vise feltet, indtil den genindlæses, men backend afviser nye rabatkald. Den lokale visning af en tidligere beregnet rabat nulstilles ved genindlæsning. Dette er en preview-demo, ikke ordreafgivelse.

Løsningen kræver genstart/genoprettelse ved flagændringer. Den viser ikke dynamisk ændring af flag i en kørende proces. `docker compose restart` indlæser ikke en ændret miljøvariabel fra Compose.

### Oprydning af feature flag

```bash
git switch after-cleanup
docker compose up --build -d
```

Funktionen er permanent tilgængelig. Flagklasse, konfigurationsendpoint, betinget React-visning, miljøvariabel og tests af flagtilstande er fjernet. Testene af rabatfunktionen er bevaret. Byg igen ved hvert branchskift; ellers kører tidligere kode stadig i containerne.

## Hvad sker der i koden

| Fil | Ansvar |
| --- | --- |
| backend/.../FeatureFlags.java | Læser flaget; manglende/ukendt værdi bliver false |
| backend/.../Main.java | Læser miljøet ved opstart |
| backend/.../App.java | Routes og kontrol af flag før rabatkald |
| backend/.../DiscountService.java | Validering og beregning med BigDecimal |
| frontend/src/App.jsx | Henter pris og offentlig featurekonfiguration hver for sig |
| frontend/src/DiscountCodeForm.jsx | Sender kode, viser fejl og opdaterer pris |

Backend er autoritativ. Frontend henter konfiguration ved indlæsning; flaget kopieres ikke ind i Reacts build. Konfigurationsfejl skjuler rabatfunktionen, men forhindrer ikke den eksisterende bestillingsvisning.

Der returneres 403, når funktionen er deaktiveret. 


## Opsamling

1. **Deployment/release:** Deployment sker, når den nye applikationsversion installeres; release sker, når flaget aktiveres og funktionen gøres tilgængelig.
2. **Hyppig integration:** Delene kan komme i main, før hele brugerfunktionen er klar. Eksisterende adfærd bevares.
3. **Backendkontrol:** En bruger kan kalde API'et uden React. En skjult knap er ikke en backendkontrol.
4. **Fjernelse:** Når funktionen er stabilt og permanent frigivet, og behovet for midlertidig deaktivering er afklaret. Fjern flag og gammel kode med tests.
5. **Uden flag:** Små færdige funktioner eller bagudkompatible forberedelser kan integreres uden flags. Tilføj eventuelt brugeradgangen sidst.

## Kilder

- https://martinfowler.com/articles/feature-toggles.html
- https://trunkbaseddevelopment.com/feature-flags/
