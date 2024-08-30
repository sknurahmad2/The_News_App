package com.example.thenewsapp.models

data class Source(
    val id: String,
    val name: String
){
    override fun hashCode(): Int {
        return id?.hashCode() ?: 0 // If id is null, return 0 as hash code
    }
}