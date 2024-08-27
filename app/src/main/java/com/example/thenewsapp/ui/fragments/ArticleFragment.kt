package com.example.thenewsapp.ui.fragments

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.thenewsapp.R
import com.example.thenewsapp.databinding.FragmentArticleBinding
import com.example.thenewsapp.models.Article
import com.example.thenewsapp.ui.NewsActivity
import com.example.thenewsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlin.properties.Delegates

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private lateinit var newsViewModel : NewsViewModel
    val args : ArticleFragmentArgs by navArgs()
    lateinit var binding : FragmentArticleBinding
    lateinit var progressBar: ProgressBar
    lateinit var article: Article
    private var isLoading = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)
        newsViewModel = ViewModelProvider(requireActivity())[NewsViewModel::class.java]
        newsViewModel = (activity as NewsActivity).newsViewModel
        article = args.article
        progressBar = binding.progressBar
        isLoading = false

        setUpWebView()

        binding.fab.setOnClickListener {
            newsViewModel.addToFavourites(article)
            Snackbar.make(view,"Article added to favourites",Snackbar.LENGTH_SHORT).show()
        }
    }
    private fun setUpWebView() {
        binding.webView.apply {
            webViewClient = object :WebViewClient(){
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    if (!isLoading) {
                        progressBar.visibility = View.VISIBLE
                        isLoading = true
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    if (isLoading) {
                        progressBar.visibility = View.GONE
                        isLoading = false
                    }
                }
            }


            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true // Enable DOM storage API
                loadWithOverviewMode = true
                useWideViewPort = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
            }
            article.url?.let { url ->
                loadUrl(url)
            }
        }
    }
}