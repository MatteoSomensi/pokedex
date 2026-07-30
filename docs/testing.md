# Testing strategy

The project uses JUnit 4/5, MockK, Coroutines Test, Turbine, Robolectric, Compose UI Test,
Roborazzi, Compose Preview Screenshot Testing, Room testing, MockWebServer, UI Automator,
Macrobenchmark, and Jacoco.

## Test layers

- JVM logic tests cover ViewModels, repositories, Paging/Worker orchestration, and HTTP contracts.
- Robolectric Compose tests cover screen behavior without a device.
- Device tests validate Room against Android SQLite, every database migration, and the demo
  authentication journey.
- Screenshot tests cover nine canonical window sizes plus dark theme and 150% font scale.
- Macrobenchmarks and Baseline Profiles measure startup and scrolling separately from correctness.

## Commands

```bash
./gradlew testDebugUnitTest
./gradlew :features:pokemon_list:verifyRoborazziDebug
./gradlew :features:pokemon_list:validateDebugScreenshotTest
./gradlew :core:data:pixel2Api35DebugAndroidTest
./gradlew :app:pixel2Api35DebugAndroidTest
./gradlew verifyModuleBoundaries :features:favorite:api:verifyApiContract
./gradlew ktlintCheck detekt lintDebug
./gradlew :app:cyclonedxDirectBom
```

Roborazzi references live in `features/pokemon_list/src/test/screenshots`. Preview screenshot
references live in `features/pokemon_list/src/screenshotTestDebug/reference`. Managed-device tests
download an API 35 AOSP image on first use.

Firebase must remain disabled during deterministic tests. Real-adapter smoke tests can be run
separately with a valid local configuration and `-PFIREBASE_ENABLED=true`.
