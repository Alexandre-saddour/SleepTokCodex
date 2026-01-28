# AGENTS.md — Codex CLI Guidelines (Kotlin Multiplatform + Compose)

This repository is a **Kotlin Multiplatform (KMP)** application using **Compose Multiplatform** with targets **Android** and **iOS**.

Agents working with Codex CLI must follow the rules below when generating, editing, or reviewing code.

---

## 1) Tech Stack (Required)

- **Kotlin Multiplatform** (Android + iOS)
- **Compose Multiplatform** for UI
- **Clean Architecture** modules:
    - `composeApp` (presentation/UI)
    - `domain` (business logic)
    - `data` (data sources + repositories implementations)
- **MVVM** in the presentation layer
- **Room** database **multiplatform**
- **Koin** dependency injection **multiplatform**

Prefer **multiplatform dependencies** over platform-specific alternatives whenever feasible.

---

## 2) Project Structure (Required)

Use (or keep) this high-level structure:

- `composeApp/`
    - `ui/` (Compose screens, components)
    - `navigation/`
    - `viewmodel/` (MVVM ViewModels)
    - `resources/` (all strings, plurals, etc.)
- `domain/`
    - `model/`
    - `repository/` (interfaces only)
    - `usecase/` (business use cases)
- `data/`
    - `repository/` (implementations)
    - `datasource/` (local/remote)
    - `local/` (Room db, DAO, entities)
    - `remote/` (HTTP clients, DTOs)
    - `mapper/` (DTO <-> domain, entity <-> domain)

Agents must not introduce new modules unless clearly justified and consistent with Clean Architecture.

---

## 3) Clean Architecture Rules

### Domain module
- Contains **pure business logic**.
- Must not depend on Android/iOS frameworks.
- Defines:
    - Domain models (immutable data classes)
    - Repository interfaces
    - Use cases

### Data module
- Implements repositories and data sources.
- Owns DTOs/entities and mapping logic.
- Handles errors and converts them to domain-friendly results.

### composeApp module
- UI + ViewModels only.
- ViewModels call **use cases** from `domain`.
- No direct Room / network code in UI layer.

---

## 4) MVVM Rules (Presentation)

- Each screen has a ViewModel managing:
    - `UiState` (immutable)
    - `UiEvent` (one-off events)
    - intents/actions from UI
- State must be updated predictably.
- Prefer **unidirectional data flow**.

### No Duplicate Business Logic in ViewModels
ViewModels must not reimplement logic that already exists in domain models.
Example:
- Use `SleepPlan.durationMinutes` instead of recalculating sleep duration
- Use `SleepPlan.computeActiveDaysMask()` instead of reimplementing bitmask logic
- Use `SleepPlan.activeDays` instead of parsing the mask manually

### Single State Update Per Action
Avoid multiple consecutive `_state.update {}` calls for the same user action.
This can cause race conditions or logic errors where updates cancel each other out.

```kotlin
// BAD - double update causes bug
fun onToggle(item: Item) {
    _state.update { it.copy(items = it.items + item) }
    _state.update { it.copy(items = it.items - item) } // reverses the first!
}

// GOOD - single atomic update
fun onToggle(item: Item) {
    _state.update { current ->
        val updated = when (item) {
            in current.items -> current.items - item
            else -> current.items + item
        }
        current.copy(items = updated)
    }
}
```

### Avoid Redundant Use Case Calls
Do not call multiple use cases that return overlapping data. If `GetHomeSummaryUseCase` already returns `activeNight`, do not also call `GetActiveNightUseCase`.

---

## 5) Use Case Contract (Strict)

Every use case must:
- Expose **exactly one public method** named: `suspend fun execute(...)`

It may contain as many private methods as needed.
It must not expose additional public methods, properties, or operators.
Use cases should testable, and focused on a single business purpose.

6) Strings and Localization (Strict)

No hardcoded strings anywhere in code (UI, domain, data, logs shown to users).

All user-visible text must come from resources.

Build the codebase to be translation-ready:

Use resource keys

Avoid concatenating raw text; use formatted resources when needed

Agents must reject PRs that introduce hardcoded strings.

7) Multiplatform-First Dependency Policy

Prefer multiplatform libraries.

Avoid platform-specific dependencies unless there is no viable KMP alternative.

If platform-specific code is required, isolate it behind expect/actual or clearly separated platform source sets.

7.1) Serialization (Strict)

Use **kotlinx.serialization** for JSON across all modules.

Do **not** use Gson or other JVM-only serializers.

Avoid manual JSON building/parsing when a serializable model can be used.

8) Dependency Versions (Strict)

All dependencies introduced must be:
- Modern
- Actively maintained
- Using the latest stable versions available at the time of change

When adding or updating dependencies:
- Update versions consistently (BOMs or version catalogs if used).
- Avoid pinning outdated versions without a clear reason.
- Include migration notes in the PR description if an update impacts code patterns.

9) Room (Multiplatform)

Use Room in a KMP-compatible way.

DAOs and entities live in data/local.

Keep schema changes explicit and documented.

Ensure iOS integration is supported (driver setup, initialization, etc.) using KMP-friendly configuration.

10) Koin (Multiplatform)
Define DI modules per layer:
- domain bindings (use cases)
- data bindings (repositories, data sources, db)
- composeApp bindings (ViewModels)
Avoid service locator patterns outside Koin modules.
Keep module wiring explicit and testable.
For Android ViewModels, always inject using `viewModelOf`.

11) Error Handling and Result Types

Prefer explicit domain-level error models (sealed classes) or result wrappers.

Do not leak platform exceptions directly to UI.

Map data-layer failures into domain-friendly outcomes.

### Use Case Error Handling Pattern (Strict)

In use cases, use the `getOrThrow()` pattern with `try/catch DomainException` for consistent and readable error handling:

```kotlin
suspend fun execute(): AppResult<MyResult> {
    return try {
        val user = userRepository.getActiveUser().getOrThrow()
        val data = dataRepository.getData(user.id).getOrThrow()

        // Business logic here

        AppResult.Success(result)
    } catch (e: DomainException) {
        AppResult.Error(e.error)
    }
}
```

**Do NOT** use the verbose `if/as` pattern:
```kotlin
// ❌ Avoid this pattern
val userResult = userRepository.getActiveUser()
if (userResult is AppResult.Error) {
    return userResult
}
val user = (userResult as AppResult.Success).value
```

The `getOrThrow()` extension and `DomainException` are defined in `domain/result/AppResult.kt`.

12) Testing Expectations

Domain use cases should be unit-test friendly.

Repository interfaces should be mockable.

Avoid static/global state that makes tests flaky.

13) Coding Standards

Idiomatic Kotlin (immutability, sealed hierarchies, extension functions when appropriate).

Keep functions small; favor clarity over cleverness.

Avoid unnecessary abstractions.

All new code must be formatted and lint-clean according to project tooling.

### Kotlin Style Rules
- **Enum iteration**: Use `EnumClass.entries` instead of `EnumClass.values()` (deprecated in Kotlin 1.9+).
- **Imports over FQN**: Prefer imports over fully qualified names. Avoid inline FQN like `kotlinx.datetime.LocalTime`; instead, add an import and use `LocalTime`.
- **Prefer `when` over `if/else`**: For binary conditions, prefer `when` expressions over `if/else`:
  ```kotlin
  // Preferred
  val result = when (item) {
      in collection -> doA()
      else -> doB()
  }

  // Avoid
  val result = if (item in collection) doA() else doB()
  ```

14) Coroutines (Strict)

If a dispatcher is required, use the `DispatcherProvider` abstraction instead of hardcoding a dispatcher.

## 14.1) Date and Time Handling (Strict)

### Use kotlin.time APIs
Always use `kotlin.time.Instant` and `kotlin.time.Clock` instead of the deprecated `kotlinx.datetime.Instant` and `kotlinx.datetime.Clock`.

```kotlin
// ✅ GOOD - Use kotlin.time
import kotlin.time.Instant
import kotlin.time.Clock

val now = Clock.System.now()
val timestamp: Instant = Clock.System.now()

// ❌ BAD - Don't use kotlinx.datetime (deprecated)
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
```

### Use DateTimeUtils for conversions
For timezone-aware operations and conversions, use the utilities in `domain/util/DateTimeUtils.kt`:

```kotlin
import com.example.domain.util.toLocalDateTime
import com.example.domain.util.toKotlinxInstant
import com.example.domain.util.toKotlinTimeInstant

// Convert kotlin.time.Instant to LocalDateTime with timezone
val localDateTime = instant.toLocalDateTime(timeZone)

// Convert between kotlin.time.Instant and kotlinx.datetime.Instant
val kotlinxInstant = kotlinTimeInstant.toKotlinxInstant()
val kotlinTimeInstant = kotlinxInstant.toKotlinTimeInstant()
```

### Rationale
- `kotlin.time.Instant` is the standard library replacement for `kotlinx.datetime.Instant`
- `kotlinx.datetime.Instant` is being deprecated in favor of the stdlib version
- `DateTimeUtils.kt` provides bridge functions for timezone-aware operations which are not available on `kotlin.time.Instant` directly

15) Pull Request / Change Requirements for Agents
When generating changes, agents must include:
- What was changed and why
- Any architectural impact
- Any dependency added/updated with version and rationale
- Notes about resource strings added/updated
Agents must not:
- Introduce hardcoded strings
- Bypass Clean Architecture boundaries
- Add extra public methods to use cases (only execute())
