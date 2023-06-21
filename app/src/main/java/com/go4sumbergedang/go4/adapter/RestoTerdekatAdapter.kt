package com.go4sumbergedang.go4.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.model.DetailRestoTerdekatModel
import de.hdodenhof.circleimageview.CircleImageView

class RestoTerdekatAdapter (
    private val listData :MutableList<DetailRestoTerdekatModel>,
    private val context: Context
) : RecyclerView.Adapter<RestoTerdekatAdapter.ViewHolder>(){

    private var dialog: Dialog? = null
    interface Dialog {
        fun onClick(position: Int, list : DetailRestoTerdekatModel)
    }

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }
    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var foto: CircleImageView
        var nama: TextView
        var jarak: TextView
        var rating: TextView

        init {
            foto = view.findViewById(R.id.foto)
            nama = view.findViewById(R.id.txt_nama)
            jarak = view.findViewById(R.id.txt_jarak)
            rating = view.findViewById(R.id.txt_star)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_resto_terdekat, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val list = listData[position]
        holder.nama.text = list.namaResto.toString().toUpperCase()
        val jarak = list.distance.toString()
        holder.jarak.text = "$jarak + KM"
        holder.itemView.setOnClickListener {
            if (dialog!=null){
                dialog!!.onClick(position,list)
            }
        }
    }

}