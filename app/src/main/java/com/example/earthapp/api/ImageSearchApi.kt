package com.example.earthapp.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

object ImageSearchApi {
    suspend fun getImageSearch(query: String): List<String> {
        Log.d("TAG_PROMPT", "Searching for: $query")
        val client = OkHttpClient()
        val url =
            "https://www.google.com/search?q=$query&tbm=isch&hl=en&gl=us&ijn=0"
        val request = Request.Builder()
            .url(url)
            .header(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.114 Safari/537.36"
            )
            .build()
        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()
                    if (!response.isSuccessful || responseBody == null) {
                        Log.d(
                            "TAG_PROMPT",
                            "Image return:body= ${response.body} code= ${response.code}"
                        )
                        return@withContext emptyList<String>()
                    }
                    val doc = Jsoup.parse(responseBody)
                    val scriptElements = doc.select("script")
                    val imageUrls = mutableListOf<String>()

                    for (element in scriptElements) {
                        val scriptContent = element.html()
                        val regex = Regex("""https?://[^\\s"]+(\.jpg|\.png)""")
                        val matches = regex.findAll(scriptContent)
                        for (match in matches) {
                            imageUrls.add(match.value)
                            Log.d("TAG_PROMPT", "Image Link: ${match.value}")
                        }
                    }
                    imageUrls
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("TAG_PROMPT", "Image exp: ${e.localizedMessage}")
                emptyList()
            }
        }
    }
}
