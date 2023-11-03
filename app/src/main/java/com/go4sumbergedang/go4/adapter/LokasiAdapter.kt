package com.go4sumbergedang.go4.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.go4.R

class LokasiAdapter(private val context: Context, private val items: List<String>, private val placeIds: List<String>, private val itemClickListener: ItemClickListener) : RecyclerView.Adapter<LokasiAdapter.ViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(location: String, placeId: String)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val locationName: TextView = itemView.findViewById(R.id.txt_lokasi)

        init {
            itemView.setOnClickListener {
//                itemClickListener.onItemClick(items[adapterPosition])
                itemClickListener.onItemClick(items[adapterPosition], placeIds[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_lokasi_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.locationName.text = items[position]
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
