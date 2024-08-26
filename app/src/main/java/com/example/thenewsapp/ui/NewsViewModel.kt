package com.example.thenewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.thenewsapp.models.Article
import com.example.thenewsapp.models.NewsResponse
import com.example.thenewsapp.repository.NewsRepository
import com.example.thenewsapp.util.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel(app:Application,val newsRepository: NewsRepository):AndroidViewModel(app) {
    val headlines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headlinesPage = 1
    var headlinesResponce : NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponce : NewsResponse ? = null
    var oldSearchQuery:String ? = null
    var newSearchQuery:String ? = null

    init {
        getHeadlines("us")
    }
    fun getHeadlines(countryCode: String) = viewModelScope.launch {
        headlinesInternet(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNewsInternet(searchQuery)
    }

    private fun handleHeadlinesResponce(responce : Response<NewsResponse>):Resource<NewsResponse>{
        if(responce.isSuccessful){
            responce.body()?.let { resultResponse ->
                headlinesPage++
                if(headlinesResponce == null ){
                    headlinesResponce = resultResponse
                }else{
                    val oldArticles = headlinesResponce?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Sucess(headlinesResponce ?: resultResponse)
            }
        }
        return Resource.Error(responce.message())
    }

    private fun handleSearchNewsResponce(responce : Response<NewsResponse>):Resource<NewsResponse>{
        if(responce.isSuccessful){
            responce.body()?.let { resultResponse ->
                if(searchNewsResponce == null || newSearchQuery != oldSearchQuery ){
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponce = resultResponse
                }else{
                    searchNewsPage++
                    val oldArticles = searchNewsResponce?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Sucess(searchNewsResponce ?: resultResponse)
            }
        }
        return Resource.Error(responce.message())
    }

    fun addToFavourites(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getFavouriteNews() = newsRepository.getAllArticles()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun internetConnection(context : Context):Boolean{
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when{
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }
    }

    private suspend fun headlinesInternet(countryCode : String){
        headlines.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())){
                val response = newsRepository.getHeadlines(countryCode , headlinesPage)
                headlines.postValue(handleHeadlinesResponce(response))
            }else{
                headlines.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t : Throwable){
            when(t){
                is IOException -> headlines.postValue(Resource.Error("Unable to connect"))
                else -> headlines.postValue(Resource.Error("No Signal"))
            }
        }
    }

    private suspend fun searchNewsInternet(searchQuery : String){
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())){
                val response = newsRepository.searchForNews(searchQuery,searchNewsPage)
                searchNews.postValue(handleSearchNewsResponce(response))
            }else{
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t : Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Unable to connect"))
                else -> searchNews.postValue(Resource.Error("No Signal"))
            }
        }
    }
}