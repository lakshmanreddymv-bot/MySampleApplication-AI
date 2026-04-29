package com.example.mysampleapplication.data.api

// S: Single Responsibility — defines the contract for Gemini content generation
// D: Dependency Inversion — callers depend on this interface, not the HTTP impl

/**
 * Contract for communicating with the Google Gemini generative AI API.
 *
 * Defined in the data layer as an interface so that [GeminiApiImpl] can be swapped
 * for a fake or mock in tests without touching any domain or UI code.
 */
interface GeminiApi {

    /**
     * Sends [prompt] to the Gemini API and returns the model's text response.
     *
     * @param prompt The instruction or question to send to the model.
     * @return The raw text content from the first candidate in the response.
     * @throws Exception if the network call fails, the response is empty,
     *   the API returns an error object, or the response cannot be parsed.
     */
    suspend fun generateContent(prompt: String): String
}
