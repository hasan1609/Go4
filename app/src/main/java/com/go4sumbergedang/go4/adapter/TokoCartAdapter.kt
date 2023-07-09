package com.go4sumbergedang.go4.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.model.TokoItemModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class TokoCartAdapter(
    private val listData: MutableList<TokoItemModel>,
    private val context: Context
) : RecyclerView.Adapter<TokoCartAdapter.ViewHolder>(){

    private var dialog: Dialog? = null
    interface Dialog {
        fun onClick(position: Int, list : TokoItemModel)
    }

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }
    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var foto: ImageView
        var nama_toko: TextView
        var jml_produk: TextView

        init {
            foto = view.findViewById(R.id.foto_toko)
            nama_toko = view.findViewById(R.id.nama_toko)
            jml_produk = view.findViewById(R.id.jml_produk)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_cart_toko, parent, false)

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = listData[position]
        val urlImage = context.getString(R.string.urlImage)
        val fotoResto = list.foto.toString()
        var def = "/public/images/no_image.png"

        holder.nama_toko.text = list.nama_toko.toString().toUpperCase()
        holder.jml_produk.text = list.cartItems!!.size.toString() + "  Produk"
        if (list.foto != null) {
            Picasso.get()
                .load(urlImage+fotoResto)
                .into(holder.foto)
        }else{
            Picasso.get()
                .load(urlImage+def)
                .into(holder.foto)
        }
        holder.itemView.setOnClickListener {
            if (dialog!=null){
                dialog!!.onClick(position,list)
            }
        }
    }
}