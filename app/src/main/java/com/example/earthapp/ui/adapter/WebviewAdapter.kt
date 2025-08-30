package com.example.earthapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.earthapp.R
import com.example.earthapp.ui.WebViewActivity
import com.example.earthapp.model.Webview

class WebviewAdapter(private val items: List<Webview>) :
    RecyclerView.Adapter<WebviewAdapter.WebviewViewHolder>() {
    inner class WebviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.text)
        val card: CardView = itemView.findViewById(R.id.webViewLayout)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_webviews, parent, false)
        return WebviewViewHolder(view)
    }
    override fun onBindViewHolder(holder: WebviewViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title

        holder.card.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, WebViewActivity::class.java).apply {
                putExtra("link_url", item.url)
            }
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int = items.size
}
