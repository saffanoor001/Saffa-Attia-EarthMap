package com.example.earthapp.ui

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.earthapp.databinding.ActivityWebviewBinding

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val webView: WebView = binding.webView
        val url = intent.getStringExtra("webcam_url")

        webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            mediaPlaybackRequiresUserGesture = false
            pluginState = WebSettings.PluginState.ON
        }

        webView.webViewClient = MyWebViewClient()
        webView.webChromeClient = WebChromeClient()

        url?.let {
            binding.webViewLayout.visibility = View.VISIBLE
            webView.loadUrl(it)
        }
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            // Hide loading layout after page is loaded
            binding.webViewLayout.visibility = View.GONE
            // Optional: auto-play video if page has play button with id="play"
            view?.evaluateJavascript("document.getElementById('play')?.click();", null)
        }
    }
}