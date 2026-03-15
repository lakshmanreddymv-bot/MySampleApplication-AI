package com.example.mysampleapplication.data.api

interface GeminiApi {
    suspend fun generateContent(prompt: String): String
}