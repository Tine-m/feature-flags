# Opgave: Deploy før release

Start med `before-feature-flag`. Java 21, React og Javalin. Ingen database.

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

## Arbejdsgang

1. Opret et issue og en kort branch til flagkonfiguration og endpoint. Test og integrér i main.
2. Implementér rabatberegning og API bag flaget. Test og integrér igen.
3. Implementér Reactformularen. Test til/fra og fejl. Integrér igen.
4. Deploy lokalt med flag=false. Kald API direkte og kontroller afvisningen.
5. Aktivér flaget ved at ændre konfiguration og genoprette backend. Brug samme image. Kontroller rabatten.
6. Deaktivér igen. Diskutér, hvad flaget kan og ikke kan beskytte mod.

På jeres eget elevrepo er main jeres integrationsbranch. De færdige branches i lærerepoet er kun snapshots.

## Demonstration og refleksion

Vis begge tilstande og direkte API-kald. Svar kort i issuet: Hvornår deployede og frigav I? Hvordan støttede flaget små integrationer? Hvorfor backendkontrol? Hvornår fjernes flaget? Kunne funktionen være opdelt uden et flag?

Link til relevante PRs. Ingen særskilt rapport.
