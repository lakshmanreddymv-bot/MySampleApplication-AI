package com.example.mysampleapplication.data.api

import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GeminiApiImplTest {

    private lateinit var server: MockWebServer
    private lateinit var api: GeminiApiImpl

    /** Valid single-candidate response body matching the Gemini v1beta schema. */
    private fun validResponse(text: String) = """
        {
          "candidates": [
            {
              "content": {
                "parts": [{ "text": "$text" }]
              }
            }
          ]
        }
    """.trimIndent()

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        // Point GeminiApiImpl at the local mock server.
        // The apiKey is embedded in the URL; we override the client with a
        // non-redirecting OkHttpClient so tests stay fast and deterministic.
        api = GeminiApiImpl(
            apiKey = "test-key",
            client = OkHttpClient(),
            baseUrl = server.url("/").toString()
        )
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `generateContent returns text from valid response`() = runTest {
        server.enqueue(MockResponse().setBody(validResponse("Paris is the capital of France.")))

        val result = api.generateContent("Tell me about Paris")

        assertEquals("Paris is the capital of France.", result)
    }

    @Test
    fun `generateContent throws when response contains error object`() = runTest {
        val errorBody = """{ "error": { "message": "API key invalid" } }"""
        server.enqueue(MockResponse().setBody(errorBody))

        var thrown: Exception? = null
        try {
            api.generateContent("any prompt")
        } catch (e: Exception) {
            thrown = e
        }

        assertTrue(thrown?.message?.contains("API key invalid") == true)
    }

    @Test
    fun `generateContent throws when candidates array is missing`() = runTest {
        server.enqueue(MockResponse().setBody("""{ "someOtherField": [] }"""))

        var thrown: Exception? = null
        try {
            api.generateContent("any prompt")
        } catch (e: Exception) {
            thrown = e
        }

        assertTrue(thrown != null)
        assertTrue(thrown?.message?.contains("No candidates") == true)
    }

    @Test
    fun `generateContent throws when response body is empty`() = runTest {
        server.enqueue(MockResponse().setBody(""))

        var thrown: Exception? = null
        try {
            api.generateContent("any prompt")
        } catch (e: Exception) {
            thrown = e
        }

        assertTrue(thrown != null)
    }

    @Test
    fun `generateContent sends POST request with prompt in body`() = runTest {
        server.enqueue(MockResponse().setBody(validResponse("ok")))

        api.generateContent("search for sushi")

        val request = server.takeRequest()
        val body = request.body.readUtf8()
        assertTrue(body.contains("search for sushi"))
        assertEquals("POST", request.method)
    }
}
