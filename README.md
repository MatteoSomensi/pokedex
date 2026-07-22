# Pokedex (Multimodule Android App)

This is a modern Android application showcasing a Pokedex, built with Jetpack Compose, Kotlin, and following Clean Architecture principles. The project is fully modularized to ensure separation of concerns and scalable development.

## Architecture & Tech Stack

The app follows **Clean Architecture** combined with the **MVI (Model-View-Intent)** presentation pattern without the use of third-party MVI libraries. The architecture is divided into the following modules:

*   **`:app`**: The application entry point. Handles Navigation 3 setup and connects feature modules.
*   **`:core`**: Contains base MVI components, design system (Compose Theme, Type, Shapes), shared resources, and utility functions.
*   **`:domain`**: Pure Kotlin module. Contains business logic, use cases, models, and repository interfaces.
*   **`:data`**: Implements domain repositories, handles network calls (Retrofit), local database (if any), and dependency injection (Hilt) for data sources.
*   **`:feature_pokemon_list`**: The Pokemon list screen, implementing a custom infinite scroll pagination, search, and type filtering.
*   **`:feature_pokemon_detail`**: The Pokemon detail screen displaying stats and characteristics.

### Key Libraries & Technologies
*   **Jetpack Compose**: Declarative UI framework.
*   **Jetpack Navigation 3**: For type-safe routing between screens.
*   **Hilt**: Dependency injection.
*   **Coroutines & Flow**: For asynchronous programming and reactive streams.
*   **Retrofit & Kotlinx Serialization**: For network requests and JSON parsing.
*   **Coil**: For image loading.
*   **JUnit 4, MockK, Turbine, Robolectric**: For Unit and UI testing.
*   **Ktlint & Detekt**: For static code analysis and formatting.

## Features

1.  **Infinite Scrolling**: Custom pagination implementation leveraging Jetpack Compose `LazyGridState` to load more Pokemon as the user scrolls.
2.  **Search & Filter**: Real-time search and filtering by Pokemon type.
3.  **Adaptive UI**: Supports both light and dark themes, leveraging `CompositionLocal` for dynamic dimension scaling.
4.  **Localization**: Supports English and Italian.

## Getting Started

1.  Clone the repository.
2.  Open the project in Android Studio (Ladybug or newer recommended, utilizing AGP 9.0+).
3.  Build and run the `:app` configuration.

## Testing

The project includes setup for unit testing and UI testing. Run tests via Gradle:
```bash
./gradlew test
```

For linting and code analysis:
```bash
./gradlew ktlintCheck detekt
```
