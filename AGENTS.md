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

11) Error Handling and Result Types

Prefer explicit domain-level error models (sealed classes) or result wrappers.

Do not leak platform exceptions directly to UI.

Map data-layer failures into domain-friendly outcomes.

12) Testing Expectations

Domain use cases should be unit-test friendly.

Repository interfaces should be mockable.

Avoid static/global state that makes tests flaky.

13) Coding Standards

Idiomatic Kotlin (immutability, sealed hierarchies, extension functions when appropriate).

Keep functions small; favor clarity over cleverness.

Avoid unnecessary abstractions.

All new code must be formatted and lint-clean according to project tooling.

14) Pull Request / Change Requirements for Agents
When generating changes, agents must include:
- What was changed and why
- Any architectural impact
- Any dependency added/updated with version and rationale
- Notes about resource strings added/updated
Agents must not:
- Introduce hardcoded strings
- Bypass Clean Architecture boundaries
- Add extra public methods to use cases (only execute())
