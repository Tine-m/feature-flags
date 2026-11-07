# Feature flags · React og Javalin

Et lille undervisningsrepo til systemudvikling på 4. semester. Samme bestillingsside demonstrerer integration, deployment, release og oprydning.

| Branch | Indhold |
| --- | --- |
| `before-feature-flag` | Startkode: bestillingsside uden rabatkoder |
| `after-feature-flag` | Løsning: rabatkoder styres af backendflag |
| `after-cleanup` | Rabatkoder er permanent frigivet; flaget er fjernet |
| `main` | Samme løsning som `after-feature-flag` |

Branches er undervisningsøjebliksbilleder. De er ikke et forslag om langlivede udviklingsbranches. Git-historikken viser flere små integrationer i main.

## Hurtig start med Docker

```bash
git switch after-feature-flag
docker compose up --build -d
```

Åbn http://localhost:8080. Rabatfunktionen er som standard slået fra.
Kopiér `.env.example` til `.env`, og sæt `FEATURE_DISCOUNT_CODES=true`.

```bash
docker compose up -d --no-deps --force-recreate backend
```

Genindlæs siden. Brug `STUDENT10`: 200 kr. bliver til 180 kr. Der bygges ikke et nyt image ved aktivering. Sæt flaget til false og genopret backend igen for at deaktivere.

Stop: `docker compose down`.

## Lokal udvikling uden Docker

Java 21, Maven 3.9+ og Node 22.12+ med npm. Versionerne i projektet er fastlåst som undervisningsbaseline, ikke en påstand om nyeste versioner. Backend anvender Javalin 6.7.0.

Terminal 1:

```bash
cd backend
mvn verify
java -jar target/app.jar
```

Slå flaget til med miljøvariablen i din IDE eller terminal **før** Java startes.

PowerShell:

```powershell
$env:FEATURE_DISCOUNT_CODES="true"
java -jar target/app.jar
```

Git Bash / macOS / Linux:

```bash
FEATURE_DISCOUNT_CODES=true java -jar target/app.jar
```

Terminal 2:

```bash
cd frontend
npm ci
npm test
npm run dev
```

Åbn http://localhost:5173. Vite videresender `/api` til Javalin på port 7070; i Docker gør nginx det samme. Der er derfor ikke behov for CORS-konfiguration.

## Materialer

- [Lærervejledning og løsningsforklaring](docs/teacher-guide.md)
- [Opgave til studerende](docs/exercise.md)
- [Test og validering](docs/validation.md)

Ingen database, login, faktisk ordreafgivelse eller betaling. Priser fastlægges på serveren. Endpointet beregner alene en forhåndsvisning.

## Læg repoet på din GitHub-konto

Opret et **tomt** repository uden README, licens og .gitignore. Kør fra den udpakkede repo-mappe, og erstat URL'en med din egen:

```bash
git remote add origin https://github.com/DIT-BRUGERNAVN/feature-flags-demo.git
git push -u origin main
git push origin before-feature-flag after-feature-flag after-cleanup
```

Brug eventuelt et privat repo til underviserløsningen og et separat elevrepo baseret på startversionen. Alle løsninger er tilgængelige for studerende, hvis de har læseadgang til dette repo, også selvom de kun starter på før-branchen.
