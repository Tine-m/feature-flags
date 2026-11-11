# Validering

| Version | Backendtests | Frontendtests | Backend-JAR | React-produktionsbuild |
| --- | ---: | ---: | --- | --- |
| before-feature-flag | 1 bestået | 1 bestået | Bygget | Bestået |
| after-feature-flag / main | 6 bestået | 5 bestået | Bygget | Bestået |
| after-cleanup | 3 bestået | 2 bestået | Bygget | Bestået |

Testene bruger en eksplicit flagkonfiguration og afhænger ikke af terminalens miljøvariabel. API-testene starter Javalin på en ledig port og stopper den igen. Frontendtestene simulerer API-svar.
