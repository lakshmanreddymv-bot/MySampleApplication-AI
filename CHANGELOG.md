# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

---

## [1.1.0] — 2026-04-29

### Added
- **38 unit tests** across 6 test classes (was 0 real tests)
  - `SearchItemsUseCaseTest` (5 tests)
  - `GetItemDetailUseCaseTest` (3 tests)
  - `ListViewModelTest` (9 tests)
  - `DetailViewModelTest` (5 tests)
  - `ItemRepositoryImplTest` (10 tests)
  - `GeminiApiImplTest` (5 tests — via MockWebServer)
- **KDoc** on all 14 Kotlin source files — every class, interface, function, and property
- **SOLID + UDF documentation** in KDoc and README for all ViewModels and use cases
- **GitHub Actions CI** (`.github/workflows/ci.yml`) — runs unit tests and debug build on every push and PR to `main`
- **README rewritten** to portfolio standard: problem statement, Mermaid flowchart, Mermaid architecture diagram, tech stack table, setup instructions, unit tests table, bugs-fixed table, portfolio table (all 4 projects), author section
- **CHANGELOG.md** (this file)
- Test dependencies: `mockk 1.13.10`, `kotlinx-coroutines-test 1.9.0`, `mockwebserver 4.12.0`, `org.json 20231013`

### Changed
- `GeminiApiImpl`: `OkHttpClient` is now injectable (default parameter) to enable unit testing
- `GeminiApiImpl`: Added `baseUrl` parameter (default: Gemini production URL) so tests can redirect to MockWebServer
- `GeminiApiImpl`: Removed `android.util.Log.d` — debug logging in a data class violates single responsibility and breaks unit tests
- URL construction fixed: missing `/` between base URL and path segment

### Fixed
- `org.json.JSONArray`/`JSONObject` stubs throwing in unit tests (added real `org.json` test dependency)
- URL malformed as `"65301models"` port in MockWebServer tests

---

## [1.0.0] — 2026-03-15

### Added
- AI-powered natural language search using **Gemini 2.0 Flash** REST API
- Clean Architecture with three layers: `domain/`, `data/`, `ui/`
- `SearchItemsUseCase` and `GetItemDetailUseCase` for domain-layer business logic
- `ItemRepositoryImpl` with a 90-item catalogue spanning food, tech, sports, nature, cities, art, science, and wellness
- `GeminiApiImpl` — OkHttp-based REST client with JSON request/response parsing and error handling
- `ListViewModel` and `DetailViewModel` following MVVM + Unidirectional Data Flow with sealed `UiState` classes and `StateFlow`
- `ListScreen` — searchable catalogue with AI loading overlay and error/retry states
- `DetailScreen` — AI-generated item description with loading overlay and error/retry
- Navigation Compose — `list` → `detail/{itemId}` route
- PR #1: Loading spinner overlay with semi-transparent background and AI status labels
