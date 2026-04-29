package com.example.mysampleapplication.domain.model

/**
 * Represents a single item in the searchable catalogue.
 *
 * Lives in the domain layer — no Android or framework dependencies.
 *
 * @property id Stable unique identifier used for navigation and lookup.
 * @property text Human-readable label displayed in the list and used as the
 *   Gemini search corpus.
 */
data class MyListItem(val id: Int, val text: String)
