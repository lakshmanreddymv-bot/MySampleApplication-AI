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

## Tech Stack & AI Integration

**AI Layer:**
- **Gemini API** — Google's multimodal LLM used for natural language 
  understanding and intelligent response generation
- **LLM Integration** — REST API calls to large language models 
  using Retrofit + OkHttp, handling streaming responses, 
  error states and rate limiting
- **AI-powered Android features** — on-device intelligence 
  via ML Kit + cloud inference via Gemini API

**Developer Tooling:**
- **MCP (Model Context Protocol)** — open standard that connects 
  AI assistants directly to IDEs, enabling Claude Desktop to 
  read, write and refactor code inside Android Studio in real time

## Author
**Lakshmana Reddy**
