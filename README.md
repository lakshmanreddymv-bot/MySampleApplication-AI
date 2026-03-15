# MySampleApplication AI

## Description
AI-powered Android app with Gemini API natural language search. This app demonstrates how to integrate Google's Gemini 2.0 Flash API into a modern Android application to enable intelligent, natural language-driven search experiences.

## Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture Pattern:** MVVM
- **Architecture Style:** Clean Architecture
- **Networking:** Retrofit
- **Async:** Coroutines & StateFlow

## Features
- 🤖 Natural language search powered by Gemini 2.0 Flash API
- ⚡ Real-time results with Kotlin Coroutines and StateFlow
- 🎨 Modern UI built with Jetpack Compose
- 🏗️ Clean, maintainable codebase following Clean Architecture principles

## Architecture
The project follows Clean Architecture with clearly separated layers:

```
com.example.mysampleapplication/
├── data/
│   ├── api/            # Gemini API interface & implementation
│   └── repository/     # Repository implementations
├── domain/
│   ├── model/          # Domain models
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Business logic use cases
└── ui/
    ├── list/           # List screen & ViewModel
    ├── detail/         # Detail screen & ViewModel
    └── theme/          # App theme (Color, Typography, Theme)
```

- **Domain Layer** – Models, repository interfaces, and use cases (pure Kotlin, no Android dependencies)
- **Data Layer** – Repository implementations and Gemini API integration via Retrofit
- **UI Layer** – Jetpack Compose screens and ViewModels following MVVM

## Author
**Lakshmana Reddy**
