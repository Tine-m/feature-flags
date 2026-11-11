# Feature flags

En bestillingsside demonstrerer brugen af feature flag til rabatkoder.

| Branch | Indhold |
| --- | --- |
| `before-feature-flag` | Startkode: bestillingsside uden rabatkoder |
| `after-feature-flag` | Løsning: rabatkoder styres af backendflag |
| `after-cleanup` | Rabatkoder er permanent frigivet; flaget er fjernet |
| `main` | Samme løsning som `after-feature-flag` |


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

## Materialer

- [En lidt mere detaljeret vejledning](docs/guide.md)
- [Test og validering](docs/validation.md)

Ingen database, login, faktisk ordreafgivelse eller betaling. Priser fastlægges på serveren. Endpointet beregner alene en forhåndsvisning.
