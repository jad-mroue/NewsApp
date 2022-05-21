package com.example.newsapp.repository

import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.db.ArticleDatabase
import com.example.newsapp.models.Article

class NewsRepository (
    val db: ArticleDatabase
){
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews(uid: String) = db.getArticleDao().getAllArticles(uid)

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

    fun checkArticleIfSaved(articleUrl: String) = db.getArticleDao().checkArticleIfSaved(articleUrl)
}