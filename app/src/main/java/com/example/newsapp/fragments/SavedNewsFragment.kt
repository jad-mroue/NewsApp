package com.example.newsapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.NewsActivity
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.viewmodels.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply{
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                val myArticle = article
                myArticle.userId = viewModel.auth.uid
                viewModel.deleteArticle(myArticle)
                Snackbar.make(view, "Successfully deleted article", Snackbar.LENGTH_LONG).apply{
                    setAction("Undo"){
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }
        viewModel.auth.uid?.let {
            viewModel.getSavedNews(it)?.observe(viewLifecycleOwner, Observer { articles ->
                newsAdapter.differ.submitList(articles)
            })
        }
    }
    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(object : NewsAdapter.SaveListener {
            override fun onClick(position: Int) {
                var article = newsAdapter.differ.currentList[position]
                article.url?.let {
                    val check = viewModel.checkArticleIfSaved(it)
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
        rvSavedNews.apply{
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}