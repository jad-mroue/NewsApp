package com.example.newsapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.repository.NewsRepository
import com.google.firebase.auth.FirebaseAuth

class NewsViewModelProviderFactory(
    val newsRepository: NewsRepository,
    val auth: FirebaseAuth
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepository, auth) as T
    }
}