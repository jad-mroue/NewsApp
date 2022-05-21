package com.example.newsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newsapp.models.Article

@Dao
interface ArticleDao {

    //update or insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Query("SELECT * FROM articles WHERE userId LIKE :uid")
    fun getAllArticles(uid: String): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("SELECT * FROM articles WHERE url LIKE :articleUrl LIMIT 1")
    fun checkArticleIfSaved(articleUrl: String): LiveData<Article>
}