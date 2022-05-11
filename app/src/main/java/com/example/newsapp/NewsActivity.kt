package com.example.newsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsapp.db.ArticleDatabase
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.viewmodels.NewsViewModel
import com.example.newsapp.viewmodels.NewsViewModelProviderFactory
import com.example.newsapp.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel
    lateinit var auth: FirebaseAuth
    lateinit var userViewModel: UserViewModel
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        auth.signOut()
        startActivity(Intent(this, NewsActivity::class.java))
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        auth = FirebaseAuth.getInstance()
        var toast = Toast.makeText(this, "neither", Toast.LENGTH_LONG)
        if(auth.currentUser == null){
            toast = Toast.makeText(this, "null", Toast.LENGTH_LONG)
            setContentView(R.layout.activity_auth)
        }
        else{
            toast = Toast.makeText(this, "not null", Toast.LENGTH_LONG)
            setContentView(R.layout.activity_main)
            bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())
        }
        toast.show();
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = UserViewModel()
        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)
    }
}