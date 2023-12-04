package com.go4sumbergedang.go4.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.model.DataDriverManual
import com.go4sumbergedang.go4.model.RestoNearModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class DriverManualAdapter (
    private val listData :MutableList<DataDriverManual>,
    private val context: Context
) : RecyclerView.Adapter<DriverManualAdapter.ViewHolder>() {

    private var dialog: Dialog? = null

    interface Dialog {
        fun onClick(position: Int, idDriver: String)
    }

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var foto: ImageView
        var nama: TextView
        var jarak: TextView

        init {
            foto = view.findViewById(R.id.foto_driver)
            nama = view.findViewById(R.id.nama_driver)
            jarak = view.findViewById(R.id.txt_jarak)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_driver_manual, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = listData[position]
        val urlImage = context.getString(R.string.urlImage)
        val fotoDriver = list.driver!!.detailDriver!!.foto
        var def = "/public/images/no_image.png"

        holder.nama.text = list.driver.nama.toString().toUpperCase()
        val jarak = list.distance.toString()
        if (list.distance!! <= 1){
            holder.jarak.text = "± 1 KM"
        }else{
            holder.jarak.text = "$jarak KM"
        }
        if (list.driver.detailDriver!!.foto != null) {
            Picasso.get()
                .load(urlImage + fotoDriver)
                .into(holder.foto)
        } else {
            Picasso.get()
                .load(urlImage + def)
                .into(holder.foto)
        }
        holder.itemView.setOnClickListener {
            if (dialog != null) {
                dialog!!.onClick(position, list.driverId.toString())
            }
        }
    }
}