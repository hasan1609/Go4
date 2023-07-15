package com.go4sumbergedang.go4.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.model.ProdukModel
import com.go4sumbergedang.go4.model.RestoModel
import com.squareup.picasso.Picasso

class ProdukAdapter(
    private val dataList: List<Any>,
    private val restoModel: RestoModel,
    private val context: Context
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_TYPE_HEADER = 0
    private val ITEM_TYPE_PRODUK = 1

    private var dialog: Dialog? = null
    interface Dialog {
        fun onClick(position: Int, namaToko: String, foto: String, produkModel: ProdukModel)
    }

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtKategori: TextView = itemView.findViewById(R.id.header_textview)

        fun bind(kategori: String) {
            txtKategori.text = kategori.toUpperCase()
        }
    }

    inner class ProdukViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtNamaProduk: TextView = itemView.findViewById(R.id.txt_nama)
        private val txtHarga: TextView = itemView.findViewById(R.id.txt_harga)
        private val foto: ImageView = itemView.findViewById(R.id.foto)

        fun bind(produk: ProdukModel) {
            txtNamaProduk.text = produk.namaProduk
            txtHarga.text = produk.harga
            val urlImage = context.getString(R.string.urlImage)
            val fotoResto = produk.fotoProduk.toString()
            var def = "/public/images/no_image.png"
            if (produk.fotoProduk != null) {
                Picasso.get()
                    .load(urlImage+fotoResto)
                    .into(foto)
            }else{
                Picasso.get()
                    .load(urlImage+def)
                    .into(foto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_header_rv_produk, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_produk, parent, false)
            ProdukViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]
        if (holder is HeaderViewHolder && item is String) {
            holder.bind(item)
        } else if (holder is ProdukViewHolder && item is ProdukModel) {
            holder.bind(item)
            holder.itemView.setOnClickListener {
                if (dialog!=null){
                    dialog!!.onClick(position, restoModel.namaResto.toString() , restoModel.foto.toString() ,item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = dataList[position]
        return if (item is String) {
            ITEM_TYPE_HEADER
        } else {
            ITEM_TYPE_PRODUK
        }
    }
}
