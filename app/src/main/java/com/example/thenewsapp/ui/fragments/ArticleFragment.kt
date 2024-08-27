package com.example.thenewsapp.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.thenewsapp.R
import com.example.thenewsapp.databinding.FragmentArticleBinding
import com.example.thenewsapp.ui.NewsActivity
import com.example.thenewsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private lateinit var newsViewModel : NewsViewModel
    val args : ArticleFragmentArgs by navArgs()
    lateinit var binding : FragmentArticleBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)
        newsViewModel = ViewModelProvider(requireActivity())[NewsViewModel::class.java]
        newsViewModel = (activity as NewsActivity).newsViewModel
        val article = args.article

        binding.webView.apply {
            webViewClient = WebViewClient()
            article.url?.let {url ->
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true // Enable DOM storage API
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                    cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                }
                loadUrl(url)
            }
        }

        binding.fab.setOnClickListener {
            newsViewModel.addToFavourites(article)
            Snackbar.make(view,"Article added to favourites",Snackbar.LENGTH_SHORT).show()
        }
    }
}