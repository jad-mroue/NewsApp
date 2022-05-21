package com.example.newsapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.NewsActivity
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.viewmodels.NewsViewModel
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.newsapp.util.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.item_article_preview.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    val TAG = "BreakingNewsFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply{
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer{ response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        //add 2 to round the division result (+1) and because the last page is empty (+1)
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if(isLastPage){
                            rvBreakingNews.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

    }
    private fun hideProgressBar(){
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }
    private fun showProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate){
                viewModel.getBreakingNews("us")
                isScrolling = false
            }
        }
    }
    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(object : NewsAdapter.SaveListener{
            override fun onClick(position: Int) {
                var article = newsAdapter.differ.currentList[position]
                article.url?.let {
                    val check = viewModel.checkArticleIfSaved(article.url!!)
                    if(check != null){
                        Log.d("CHECK", check.toString())
                        viewModel.deleteArticle(article)
                        view?.let {
                            Snackbar.make(it, "Successfully deleted article", Snackbar.LENGTH_LONG).apply{
                                setAction("Undo"){
                                    viewModel.saveArticle(article)
                                }
                                show()
                            }
                        }
                    }
                    else{
                        Log.d("ELSE", article.url.toString())
                        viewModel.saveArticle(article)
                        view?.let { Snackbar.make(it, "Article saved successfully", Snackbar.LENGTH_SHORT).show() }
                    }
                }
            }
        })
        rvBreakingNews.apply{
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }
}