# Validering

Kontrolleret 7. november 2026 med Java 21, Maven 3.9.11 og Node/npm.

| Version | Backendtests | Frontendtests | Backend-JAR | React-produktionsbuild |
| --- | ---: | ---: | --- | --- |
| before-feature-flag | 1 bestået | 1 bestået | Bygget | Bestået |
| after-feature-flag / main | 6 bestået | 5 bestået | Bygget | Bestået |
| after-cleanup | 3 bestået | 2 bestået | Bygget | Bestået |

Den pakkede JAR er desuden startet med flag=false og flag=true. HTTP-kald kontrollerede health, konfiguration, afvisning med 403 og korrekt rabat på 180 kr. Den samme JAR blev anvendt i begge tilstande.

Docker er ikke tilgængelig i kontrolmiljøet. Docker-images, Compose og nginx-proxy er derfor ikke afprøvet her. CI-workflowet er ikke kørt på GitHub. Ingen ekstern CD/deployment er konfigureret. Frontend er verificeret gennem komponenttests og produktionsbuild; en manuel browsergennemgang af Docker-versionen bør udføres før undervisning.

## Gentag lokalt

Backend: `cd backend` efterfulgt af `mvn clean verify`.
Frontend: `cd frontend` efterfulgt af `npm ci`, `npm test` og `npm run build`.

Testene bruger en eksplicit flagkonfiguration og afhænger ikke af terminalens miljøvariabel. API-testene starter Javalin på en ledig port og stopper den igen. Frontendtestene simulerer API-svar; de erstatter ikke en samlet browsertest.
