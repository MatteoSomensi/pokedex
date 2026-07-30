# Repository guidance

Architecture decisions and test commands are documented in:

- [docs/architecture.md](docs/architecture.md)
- [docs/testing.md](docs/testing.md)

Before handing off a change, run the smallest relevant tests followed by:

```bash
./gradlew verifyModuleBoundaries :features:favorite:api:verifyApiContract
./gradlew ktlintCheck detekt testDebugUnitTest lintDebug
```

Do not update screenshot references unless the visual change is intentional and reviewed. Keep
Firebase disabled for deterministic tests; enable it explicitly with `-PFIREBASE_ENABLED=true`.
