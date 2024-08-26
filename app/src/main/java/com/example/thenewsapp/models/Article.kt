package com.example.thenewsapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val author: String? = null, // Allow null for author
    val content: String? = null, // Allow null for content
    val description: String? = null, // Allow null for description
    val publishedAt: String? = null, // Allow null for publishedAt
    val source: Source? = null, // Allow null for source
    val title: String? = null, // Allow null for title
    val url: String? = null, // Allow null for url
    val urlToImage: String? = null // Allow null for urlToImage
) : Serializable