package com.go4sumbergedang.go4.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.model.OngkirModel
import com.go4sumbergedang.go4.ui.activity.MotorManualActivity
import com.google.gson.Gson
import org.jetbrains.anko.startActivity
import java.text.DecimalFormat

class OngkirAdapter (
    private val listData :MutableList<OngkirModel>,
    private val context: Context
) : RecyclerView.Adapter<OngkirAdapter.ViewHolder>(){

    private var selectedPosition = -1
    private var selectedOngkirModel: OngkirModel? = null
    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var foto: ImageView
        var type: TextView
        var keterangan: TextView
        var harga: TextView
        var jarak: TextView

        init {
            foto = view.findViewById(R.id.logo)
            type = view.findViewById(R.id.txt_type)
            keterangan = view.findViewById(R.id.keterangan)
            harga = view.findViewById(R.id.harga)
            jarak = view.findViewById(R.id.jarak)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_ongkir, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val list = listData[position]
        val formatter = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val symbols = formatter.decimalFormatSymbols
        symbols.currencySymbol = "Rp. "
        formatter.decimalFormatSymbols = symbols
        val harga = formatter.format(list.harga)
        holder.harga.text = harga
        val jarak = list.jarak.toString()
        holder.jarak.text = "$jarak KM"
        if (list.jenisKendaraan == "mobil" ){
            holder.foto.setImageResource(R.drawable.mobil_big)
            holder.type.text = "Mobil"
        }else if(list.jenisKendaraan == "motor_otomatis"){
            holder.foto.setImageResource(R.drawable.motor_big)
            holder.type.text = "Motor Otomatis"
        }else{
            holder.foto.setImageResource(R.drawable.motor_big)
            holder.type.text = "Motor Manual"
            holder.keterangan.visibility = View.VISIBLE
        }

        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.color.primary_color) // Ganti dengan drawable latar belakang yang sesuai
        }else{
            holder.itemView.setBackgroundResource(R.color.white)
        }


        holder.itemView.setOnClickListener {
            // Mengubah latar belakang item saat diklik
            if (selectedPosition != position) {
                val previousSelected = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousSelected)
                notifyItemChanged(position)

                val selectedOngkirModel = getSelectedOngkirModel()
                if (selectedOngkirModel!!.jenisKendaraan == "motor_manual") {
                    val gson = Gson()
                    val noteJson = gson.toJson(selectedOngkirModel)
                    context.startActivity<MotorManualActivity>("ongkir" to noteJson)
                }
            }
        }
    }

    fun getSelectedOngkirModel(): OngkirModel? {
        return if (selectedPosition != -1) {
            listData[selectedPosition]
        } else {
            null
        }
    }

}