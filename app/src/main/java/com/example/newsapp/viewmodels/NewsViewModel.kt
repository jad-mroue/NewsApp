package com.example.newsapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.models.Article
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel (
    val newsRepository : NewsRepository,
    val auth: FirebaseAuth
) : ViewModel() {

    val breakingNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null
    val isSaved: MutableLiveData<Article> = MutableLiveData()
    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if(breakingNewsResponse == null){
                    breakingNewsResponse = resultResponse
                }
                else{
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews(uid: String) = auth.uid?.let { newsRepository.getSavedNews(it) }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

//    private fun handleSaved(response: LiveData<Article>) : Article? {
//        return response.value
//    }
    fun checkArticleIfSaved(articleUrl: String): Article? {
        val response = newsRepository.checkArticleIfSaved(articleUrl)
        Log.d("RESPONSE", response.toString() + " " + response.toString())
        return response.value
    }

}