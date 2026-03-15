package com.example.mysampleapplication.data.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class GeminiApiImpl(private val apiKey: String) : GeminiApi {
    private val client = OkHttpClient()
    private val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"

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

        Log.d("GeminiApi", "Raw response: $raw")

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