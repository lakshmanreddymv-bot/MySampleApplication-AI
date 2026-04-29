package com.example.mysampleapplication.domain.repository

import com.example.mysampleapplication.domain.model.MyListItem

// S: Single Responsibility — defines the contract for item data access
// D: Dependency Inversion — UI and use cases depend on this abstraction, not the concrete impl

/**
 * Contract for accessing and querying the item catalogue.
 *
 * Defined in the domain layer so that higher layers (use cases, ViewModels) depend on
 * an abstraction rather than a concrete data-layer class.
 */
interface ItemRepository {

    /**
     * Returns the full catalogue of all 90 items.
     *
     * This is a synchronous, in-memory operation — no suspend needed.
     */
    fun getAllItems(): List<MyListItem>

    /**
     * Uses the Gemini API to find items semantically matching [query].
     *
     * @param query Natural language search string entered by the user.
     * @return Subset of [getAllItems] whose content matches the query, as
     *   determined by the AI model.
     * @throws Exception if the Gemini API call fails or returns an error.
     */
    suspend fun searchItems(query: String): List<MyListItem>

    /**
     * Asks the Gemini API to generate a short description for [item].
     *
     * @param item The item to describe.
     * @return A 2–3 sentence AI-generated description.
     * @throws Exception if the Gemini API call fails or returns an error.
     */
    suspend fun getItemDetail(item: MyListItem): String
}
