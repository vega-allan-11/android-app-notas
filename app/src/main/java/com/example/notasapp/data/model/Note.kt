package com.example.notasapp.data.model

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val created_at: String,
    val updated_at: String,
    val is_favorite: Boolean

)