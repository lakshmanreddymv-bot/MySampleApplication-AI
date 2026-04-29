package com.example.mysampleapplication.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

// S: Single Responsibility — handles only HTTP communication with the Gemini REST API
// D: Dependency Inversion — implements GeminiApi; callers depend on the interface

/**
 * Concrete implementation of [GeminiApi] using OkHttp to call the Gemini v1beta REST endpoint.
 *
 * Constructs the JSON request body, executes the call on [Dispatchers.IO], and parses the
 * response. All error cases (API error object, missing candidates, empty body) surface as
 * exceptions with descriptive messages.
 *
 * @property apiKey Google AI Studio API key injected at construction time.
 * @property client OkHttp client used for HTTP calls. Injectable for testing.
 * @param baseUrl Base URL of the Gemini API. Overridable in tests to point at a
 *   [okhttp3.mockwebserver.MockWebServer].
 */
class GeminiApiImpl(
    private val apiKey: String,
    private val client: OkHttpClient = OkHttpClient(),
    baseUrl: String = "https://generativelanguage.googleapis.com/v1beta/"
) : GeminiApi {

    private val url = "${baseUrl.trimEnd('/')}/models/gemini-2.5-flash-preview-04-17:generateContent?key=$apiKey"

    /**
     * Sends [prompt] to Gemini 2.5 Flash and returns the first candidate's text.
     *
     * Runs the blocking HTTP call on [Dispatchers.IO] to avoid blocking the calling coroutine's
     * dispatcher.
     *
     * @param prompt The text instruction to send to the model.
     * @return The model's text response.
     * @throws Exception with a descriptive message on any failure path.
     */
    override suspend fun generateContent(prompt: String): String {
        val requestBody = JSONObject()
            .put("contents", JSONArray().put(
                JSONObject().put("parts", JSONArray().put(
                    JSONObject().put("text", prompt)
                ))
            ))
            .toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val raw = withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { it.body?.string() }
        } ?: throw Exception("Empty response from API")

        val json = JSONObject(raw)
        if (json.has("error")) {
            throw Exception("API error: ${json.getJSONObject("error").optString("message", raw)}")
        }
        if (!json.has("candidates")) {
            throw Exception("No candidates in response: $raw")
        }

        return json.getJSONArray("candidates")
            .optJSONObject(0)
            ?.optJSONObject("content")
            ?.optJSONArray("parts")
            ?.optJSONObject(0)
            ?.optString("text")
            ?: throw Exception("Could not parse response: $raw")
    }
}
