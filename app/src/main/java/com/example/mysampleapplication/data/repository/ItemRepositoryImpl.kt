package com.example.mysampleapplication.data.repository

import com.example.mysampleapplication.data.api.GeminiApi
import com.example.mysampleapplication.domain.model.MyListItem
import com.example.mysampleapplication.domain.repository.ItemRepository
import org.json.JSONArray

class ItemRepositoryImpl(private val api: GeminiApi) : ItemRepository {

    override fun getAllItems(): List<MyListItem> = listOf(
        MyListItem(1, "Pizza Margherita"),
        MyListItem(2, "Sushi Roll"),
        MyListItem(3, "Burger and Fries"),
        MyListItem(4, "Pasta Carbonara"),
        MyListItem(5, "Tacos al Pastor"),
        MyListItem(6, "Pad Thai"),
        MyListItem(7, "Croissant"),
        MyListItem(8, "Ramen Noodles"),
        MyListItem(9, "Caesar Salad"),
        MyListItem(10, "Chocolate Cake"),
        MyListItem(11, "iPhone 16"),
        MyListItem(12, "Samsung Galaxy S25"),
        MyListItem(13, "MacBook Pro M4"),
        MyListItem(14, "Sony WH-1000XM6 Headphones"),
        MyListItem(15, "4K OLED Monitor"),
        MyListItem(16, "Mechanical Keyboard"),
        MyListItem(17, "Raspberry Pi 5"),
        MyListItem(18, "Drone with Camera"),
        MyListItem(19, "Smart Watch"),
        MyListItem(20, "Portable SSD 2TB"),
        MyListItem(21, "Soccer Ball"),
        MyListItem(22, "Basketball"),
        MyListItem(23, "Tennis Racket"),
        MyListItem(24, "Swimming Goggles"),
        MyListItem(25, "Running Shoes"),
        MyListItem(26, "Yoga Mat"),
        MyListItem(27, "Baseball Glove"),
        MyListItem(28, "Cycling Helmet"),
        MyListItem(29, "Badminton Set"),
        MyListItem(30, "Golf Club Set"),
        MyListItem(31, "Amazon Rainforest"),
        MyListItem(32, "Mount Everest"),
        MyListItem(33, "Grand Canyon"),
        MyListItem(34, "Northern Lights"),
        MyListItem(35, "Niagara Falls"),
        MyListItem(36, "Sahara Desert"),
        MyListItem(37, "Great Barrier Reef"),
        MyListItem(38, "Yellowstone Geyser"),
        MyListItem(39, "Cherry Blossom Forest"),
        MyListItem(40, "Victoria Falls"),
        MyListItem(41, "African Elephant"),
        MyListItem(42, "Bengal Tiger"),
        MyListItem(43, "Blue Whale"),
        MyListItem(44, "Golden Eagle"),
        MyListItem(45, "Red Panda"),
        MyListItem(46, "Snow Leopard"),
        MyListItem(47, "Hummingbird"),
        MyListItem(48, "Great White Shark"),
        MyListItem(49, "Chimpanzee"),
        MyListItem(50, "Komodo Dragon"),
        MyListItem(51, "Paris, France"),
        MyListItem(52, "Tokyo, Japan"),
        MyListItem(53, "New York City, USA"),
        MyListItem(54, "Rome, Italy"),
        MyListItem(55, "Sydney, Australia"),
        MyListItem(56, "Barcelona, Spain"),
        MyListItem(57, "Dubai, UAE"),
        MyListItem(58, "London, UK"),
        MyListItem(59, "Cape Town, South Africa"),
        MyListItem(60, "Rio de Janeiro, Brazil"),
        MyListItem(61, "Mona Lisa – Leonardo da Vinci"),
        MyListItem(62, "Starry Night – Van Gogh"),
        MyListItem(63, "The Persistence of Memory – Dalí"),
        MyListItem(64, "Beethoven's 9th Symphony"),
        MyListItem(65, "Swan Lake Ballet"),
        MyListItem(66, "Hamilton Musical"),
        MyListItem(67, "The Thinker – Rodin"),
        MyListItem(68, "Guernica – Picasso"),
        MyListItem(69, "Jazz Improvisation"),
        MyListItem(70, "Origami Art"),
        MyListItem(71, "Theory of Relativity"),
        MyListItem(72, "DNA Double Helix"),
        MyListItem(73, "Quantum Computing"),
        MyListItem(74, "Black Hole Imaging"),
        MyListItem(75, "CRISPR Gene Editing"),
        MyListItem(76, "James Webb Space Telescope"),
        MyListItem(77, "Periodic Table of Elements"),
        MyListItem(78, "Higgs Boson Discovery"),
        MyListItem(79, "Artificial Intelligence"),
        MyListItem(80, "Mars Rover Perseverance"),
        MyListItem(81, "Meditation Practice"),
        MyListItem(82, "Cardiovascular Exercise"),
        MyListItem(83, "Mediterranean Diet"),
        MyListItem(84, "Sleep Hygiene"),
        MyListItem(85, "Strength Training"),
        MyListItem(86, "Mindfulness Therapy"),
        MyListItem(87, "Vitamin D Supplement"),
        MyListItem(88, "Cold Water Therapy"),
        MyListItem(89, "Intermittent Fasting"),
        MyListItem(90, "Hydration Tracking")
    )

    override suspend fun searchItems(query: String): List<MyListItem> {
        val items = getAllItems()
        val itemsText = items.joinToString("\n") { "${it.id}:${it.text}" }
        val prompt = """
            You are a search assistant. Given a list of items and a user query, return the IDs of items that match the query.

            Items (format is id:text):
            $itemsText

            User query: "$query"

            Return ONLY a JSON array of matching item IDs (integers), nothing else.
            Example: [1, 5, 12, 23]
            If no items match, return an empty array: []
        """.trimIndent()

        val text = api.generateContent(prompt)
        val jsonText = text.replace("```json", "").replace("```", "").trim()
        val startIndex = jsonText.indexOf('[')
        val endIndex = jsonText.lastIndexOf(']')
        if (startIndex == -1 || endIndex == -1) return emptyList()

        val jsonArray = JSONArray(jsonText.substring(startIndex, endIndex + 1))
        val matchingIds = (0 until jsonArray.length()).map { jsonArray.getInt(it) }.toSet()
        return items.filter { it.id in matchingIds }
    }

    override suspend fun getItemDetail(item: MyListItem): String {
        val prompt = "Give a brief 2-3 sentence description of \"${item.text}\". Be informative and engaging."
        return api.generateContent(prompt)
    }
}