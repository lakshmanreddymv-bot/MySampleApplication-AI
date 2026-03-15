package com.example.mysampleapplication.domain.repository

import com.example.mysampleapplication.domain.model.MyListItem

interface ItemRepository {
    fun getAllItems(): List<MyListItem>
    suspend fun searchItems(query: String): List<MyListItem>
    suspend fun getItemDetail(item: MyListItem): String
}