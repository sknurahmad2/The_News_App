package com.example.thenewsapp.repository

import androidx.lifecycle.LiveData
import com.example.thenewsapp.api.RetrofitInstance
import com.example.thenewsapp.db.ArticleDatabase
import com.example.thenewsapp.models.Article
import com.example.thenewsapp.util.Constants
import retrofit2.http.Query

class NewsRepository(val db:ArticleDatabase) {
    suspend fun getHeadlines(countryCode:String , pageNumber:Int) =
        RetrofitInstance.api.getHeadlines(countryCode,pageNumber)

    suspend fun searchForNews(searchQuery : String , pageNumber : Int ) =
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)

    suspend fun upsert(article: Article) =
        db.getArticleDao().upsert(article)

    fun getAllArticles() =
        db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) =
        db.getArticleDao().deleteArticle(article)
}