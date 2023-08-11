package com.go4sumbergedang.go4.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.model.ItemCartModel
import com.squareup.picasso.Picasso
import java.lang.ref.WeakReference
import java.text.DecimalFormat
import java.util.*

class ItemCartAdapter (
    private val listData :MutableList<ItemCartModel>,
    private val context: Context
) : RecyclerView.Adapter<ItemCartAdapter.ViewHolder>() {

    private var dialog: Dialog? = null

    interface Dialog {
        fun onDelete(position: Int, list: ItemCartModel)
        fun onClick(position: Int, list: ItemCartModel)
    }

    fun setClick(dialog: Dialog) {
        this.dialog = dialog
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var foto: ImageView
        var nama: TextView
        var harga: TextView
        var keterangan: TextView
        var total: TextView
        var hapus: ImageView
        private val vieww = WeakReference(view)

        init {
            foto = view.findViewById(R.id.foto)
            nama = view.findViewById(R.id.txt_nama)
            harga = view.findViewById(R.id.txt_harga)
            total = view.findViewById(R.id.txt_total)
            keterangan = view.findViewById(R.id.txt_ket)
            hapus = view.findViewById(R.id.ic_trash)
            vieww.get()?.let {
                it.setOnClickListener {
//                    click to reset swip
                    if (vieww.get()?.scrollX != 0) {
                        vieww.get()?.scrollTo(0, 0)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_cart, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartModel = listData[position]
        val urlImage = context.getString(R.string.urlImage)
        val foto = cartModel.resto!!.foto.toString()
        var def = "/public/images/no_image.png"
        val jml = cartModel.jumlah.toString()

        val hargax = cartModel.produk!!.harga!!.toDoubleOrNull() ?: 0.0
        val totalx = cartModel.total!!.toDoubleOrNull() ?: 0.0
        val formatter = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val symbols = formatter.decimalFormatSymbols
        symbols.currencySymbol = "Rp. "
        formatter.decimalFormatSymbols = symbols

        val totals = formatter.format(totalx)
        val hargas = formatter.format(hargax)

        holder.nama.text = cartModel.produk.namaProduk.toString().toUpperCase()
        holder.harga.text = "$hargas x $jml"
        holder.total.text = totals


        if (cartModel.catatan == null) {
            holder.keterangan.visibility = View.GONE
        } else {
            holder.keterangan.visibility = View.VISIBLE
            holder.keterangan.text = cartModel.catatan.toString()
        }
        if (cartModel.produk.fotoProduk  != null) {
            Picasso.get()
                .load(urlImage + foto)
                .into(holder.foto)
        } else {
            Picasso.get()
                .load(urlImage + def)
                .into(holder.foto)
        }
        holder.hapus.setOnClickListener {
            if (dialog != null) {
                dialog!!.onDelete(position, cartModel)
            }
        }

        holder.itemView.setOnClickListener {
            if (dialog != null) {
                dialog!!.onClick(position, cartModel)
            }
        }
    }
}