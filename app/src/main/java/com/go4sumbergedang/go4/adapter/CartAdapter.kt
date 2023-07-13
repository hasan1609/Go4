package com.go4sumbergedang.go4.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.model.CartModel
import com.go4sumbergedang.go4.model.DetailRestoTerdekatModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.ref.WeakReference
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class CartAdapter (
    private val listData :MutableMap<String, CartModel>,
    private val context: Context
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private var dialog: Dialog? = null
    private var subTotal: Int = 0

    init {
        calculateSubTotal() // Hitung subtotal saat adapter dibuat
    }

    interface Dialog {
        fun onDelete(position: Int, list: CartModel)
    }

    fun setDelete(dialog: Dialog) {
        this.dialog = dialog
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    private fun calculateSubTotal() {
        subTotal = 0
        for (cartModel in listData.values) {
            val hargax = cartModel.harga?.toDoubleOrNull() ?: 0.0
            val jmlx = cartModel.jumlah ?: 0
            subTotal += (hargax * jmlx).toInt()
        }
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
        val cartModelList = ArrayList(listData.values) // Mengubah map menjadi list
        val cartModel = cartModelList[position]
        val urlImage = context.getString(R.string.urlImage)
        val foto = cartModel.fotoProduk.toString()
        var def = "/public/images/no_image.png"
        val jml = cartModel.jumlah.toString()

        val hargax = cartModel.harga?.toDoubleOrNull() ?: 0.0
        val jmlx = cartModel.jumlah ?: 0
        val total = hargax * jmlx
        val formatter = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val symbols = formatter.decimalFormatSymbols
        symbols.currencySymbol = "Rp. "
        formatter.decimalFormatSymbols = symbols
        val totals = formatter.format(total)
        val hargas = formatter.format(hargax)

        holder.nama.text = cartModel.namaProduk.toString().toUpperCase()
        holder.harga.text = "$hargas x $jml"
        holder.total.text = totals

        subTotal += total.toInt()

        if (cartModel.catatan == "") {
            holder.keterangan.visibility = View.GONE
        } else {
            holder.keterangan.visibility = View.VISIBLE
            holder.keterangan.text = cartModel.catatan
        }
        if (cartModel.fotoProduk != null) {
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
    }

    fun getTotalJumlah(): Int {
        return subTotal
    }
}