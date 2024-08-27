package com.example.thenewsapp.ui.fragments

import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.thenewsapp.R
import com.example.thenewsapp.databinding.FragmentArticleBinding
import com.example.thenewsapp.models.Article
import com.example.thenewsapp.ui.NewsActivity
import com.example.thenewsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private lateinit var newsViewModel : NewsViewModel
    val args : ArticleFragmentArgs by navArgs()
    lateinit var binding : FragmentArticleBinding
    lateinit var progressBar: ProgressBar
    lateinit var article: Article

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)
        newsViewModel = ViewModelProvider(requireActivity())[NewsViewModel::class.java]
        newsViewModel = (activity as NewsActivity).newsViewModel
        article = args.article
        progressBar = binding.progressBar

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
                    binding.progressBar.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.progressBar.visibility = View.GONE
                }
            }
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
    }
}