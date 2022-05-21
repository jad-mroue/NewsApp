package com.example.newsapp.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.newsapp.NewsActivity
import com.example.newsapp.R
import com.example.newsapp.viewmodels.NewsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*
import kotlin.reflect.KProperty

class ArticleFragment : Fragment(R.layout.fragment_article){

    lateinit var viewModel: NewsViewModel
    var args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel

        val article = args.article
        webView.apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }
        fab.setOnClickListener {
            var myArticle = article
            myArticle.userId = viewModel.auth.uid
            viewModel.saveArticle(myArticle)
            Snackbar.make(view, "Article saved successfully", Snackbar.LENGTH_SHORT).show()
        }
    }
}
private operator fun Any.setValue(articleFragment: ArticleFragment, property: KProperty<*>, articleFragmentArgs: ArticleFragmentArgs) {

}
