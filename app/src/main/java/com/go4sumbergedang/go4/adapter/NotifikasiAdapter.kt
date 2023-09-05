package com.go4sumbergedang.go4.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.model.NotifikasiLogModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class NotifikasiAdapter (
    private val listData :MutableList<NotifikasiLogModel>,
    private val context: Context
) : RecyclerView.Adapter<NotifikasiAdapter.ViewHolder>(){

    private var dialog: Dialog? = null
    interface Dialog {
        fun onClick(position: Int, idOrder: String, status: String, idNotifikasi: String)
    }

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var judul: TextView
        var tgl: TextView
        var body: TextView
        var ly: LinearLayout

        init {
            judul = view.findViewById(R.id.txt_judul)
            tgl = view.findViewById(R.id.txt_tgl)
            body = view.findViewById(R.id.txt_body)
            ly = view.findViewById(R.id.ly)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_notifikasi, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = listData[position]
        holder.judul.text = list.judul

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = dateFormat.parse(list.createdAt.toString())
        val formattedDate = SimpleDateFormat("dd MMM yyyy, HH:mm:ss").format(date!!)
        holder.tgl.text = formattedDate
        holder.body.text = list.body

        if (list.status == "0"){
            holder.ly.setBackgroundColor(context.getColor(R.color.grey2))
        }

        holder.itemView.setOnClickListener {
            if (dialog!=null){
                dialog!!.onClick(position, list.data.toString(), list.status.toString(), list.idNotificationLog.toString())
            }
        }
    }

}