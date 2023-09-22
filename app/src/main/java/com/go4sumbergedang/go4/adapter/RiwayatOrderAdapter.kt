package com.go4sumbergedang.go4.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.model.DataLogOrder
import com.go4sumbergedang.go4.model.OrderLogModel
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class RiwayatOrderAdapter (
    private val listData :MutableList<DataLogOrder>,
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val VIEW_TYPE_DRIVER = 1
    private val VIEW_TYPE_RESTO = 2

    private var dialog: Dialog? = null
    interface Dialog {
        fun onClick(position: Int, idOrder : String)
    }

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    override fun getItemViewType(position: Int): Int {
        val order = listData[position]
        return if (order.order!!.kategori == "resto") VIEW_TYPE_RESTO else VIEW_TYPE_DRIVER
    }

    override fun getItemCount(): Int {
        return listData.size
    }


    inner class DiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Implement binding and view setup here for motor order
        fun bind(order: OrderLogModel) {
            val headerText = itemView.findViewById<TextView>(R.id.txt_header)
            val headerIc = itemView.findViewById<ImageView>(R.id.ic_header)
            val fotoDriver = itemView.findViewById<ImageView>(R.id.foto_driver)
            val status = itemView.findViewById<TextView>(R.id.txt_status)
            val review = itemView.findViewById<Button>(R.id.btn_review)
            val urlImage = context.getString(R.string.urlImage)
            val foto= order.detailDriver!!.foto.toString()
            var def = "/public/images/no_image.png"
            if(order.kategori == "motor"){
                headerText.text = "MOTOR"
                headerIc.setImageDrawable(context.getDrawable(R.drawable.ic_motor))
            }else{
                headerText.text = "MOBIL"
                headerIc.setImageDrawable(context.getDrawable(R.drawable.ic_car))
            }
            if (foto != null) {
                Picasso.get()
                    .load(urlImage+foto)
                    .into(fotoDriver)
            }else{
                Picasso.get()
                    .load(urlImage+def)
                    .into(fotoDriver)
            }
            itemView.findViewById<TextView>(R.id.nama_driver).text = order.driver!!.nama
            itemView.findViewById<TextView>(R.id.txt_tujuan).text = order.alamatTujuan
            val totalx = order.total!!.toDoubleOrNull() ?: 0.0
            val formatter = DecimalFormat.getCurrencyInstance() as DecimalFormat
            val symbols = formatter.decimalFormatSymbols
            symbols.currencySymbol = "Rp. "
            formatter.decimalFormatSymbols = symbols

            val totals = formatter.format(totalx)
            itemView.findViewById<TextView>(R.id.txt_harga).text = totals

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = dateFormat.parse(order.createdAt!!)
            val formattedDate = SimpleDateFormat("dd MMM yyyy, HH:mm:ss").format(date!!)
            itemView.findViewById<TextView>(R.id.txt_tgl).text = formattedDate

            when (order.status) {
                "0" -> {
                    status.text = "Driver menuju ke lokasi penjemputan"
                    status.setTextColor(context.getColor(R.color.primary_color))
                }
                "1" -> {
                    status.text = "Driver sampai di lokasi penjemputan"
                    status.setTextColor(context.getColor(R.color.primary_color))
                }
                "2" -> {
                    status.text = "Driver sedang menuju lokasi tujuan"
                    status.setTextColor(context.getColor(R.color.primary_color))
                }
                "3" -> {
                    status.text = "Sampai Pada tujuan"
                    status.setTextColor(context.getColor(R.color.primary_color))
                }
                "4" -> {
                    status.text = "Selesai"
                    status.setTextColor(context.getColor(R.color.teal_700))
                }
                else -> {
                    status.text = "Dibatalkan"
                    status.setTextColor(context.getColor(R.color.red))
                }
            }
            if(order.status == "4" && order.reviewId != null){
                review.visibility = View.VISIBLE
            }
        }
    }

    inner class RestoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Implement binding and view setup here for resto order
        fun bind(order: DataLogOrder) {
            val fotoResto = itemView.findViewById<ImageView>(R.id.foto_resto)
            val status = itemView.findViewById<TextView>(R.id.txt_status)
            val review = itemView.findViewById<Button>(R.id.btn_review)
            val urlImage = context.getString(R.string.urlImage)
            val foto= order.order!!.detailResto!!.foto.toString()
            var def = "/public/images/no_image.png"

            itemView.findViewById<ImageView>(R.id.ic_header).setImageDrawable(context.getDrawable(R.drawable.ic_food))
            if (foto != null) {
                Picasso.get()
                    .load(urlImage+foto)
                    .into(fotoResto)
            }else{
                Picasso.get()
                    .load(urlImage+def)
                    .into(fotoResto)
            }

            itemView.findViewById<TextView>(R.id.txt_header).text = "RESTO"
            itemView.findViewById<ImageView>(R.id.ic_header).setImageDrawable(context.getDrawable(R.drawable.ic_food))
            itemView.findViewById<TextView>(R.id.nama_resto).text = order.order.resto!!.nama
            itemView.findViewById<TextView>(R.id.jml_produk).text = order.count.toString() + " Produk"

            val totalx = order.order.total!!.toDoubleOrNull() ?: 0.0
            val formatter = DecimalFormat.getCurrencyInstance() as DecimalFormat
            val symbols = formatter.decimalFormatSymbols
            symbols.currencySymbol = "Rp. "
            formatter.decimalFormatSymbols = symbols

            val totals = formatter.format(totalx)
            itemView.findViewById<TextView>(R.id.txt_total).text = totals

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = dateFormat.parse(order.order.createdAt!!)
            val formattedDate = SimpleDateFormat("dd MMM yyyy, HH:mm:ss").format(date!!)
            itemView.findViewById<TextView>(R.id.txt_tgl).text = formattedDate
            // 0 = driver ke toko
            // 1 = driver sampai toko
            // 2 = driver mengantar
            // 3 = driver sampai
            // 4 = selesai
            // 5 = batal
            when (order.order.status) {
                "0" -> {
                    status.text = "Driver menuju ke lokasi resto"
                    status.setTextColor(context.getColor(R.color.primary_color))
                }
                "1" -> {
                    status.text = "Driver sampai di lokasi resto"
                    status.setTextColor(context.getColor(R.color.primary_color))
                }
                "2" -> {
                    status.text = "Driver sedang menuju lokasi pengantaan"
                    status.setTextColor(context.getColor(R.color.primary_color))
                }
                "3" -> {
                    status.text = "Driver telah sampai"
                    status.setTextColor(context.getColor(R.color.primary_color))
                }
                "4" -> {
                    status.text = "Selesai"
                    status.setTextColor(context.getColor(R.color.teal_700))
                }
                else -> {
                    status.text = "Dibatalkan"
                    status.setTextColor(context.getColor(R.color.red))
                }
            }

            if(order.order.status == "4" && order.order.reviewId != null){
                review.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DRIVER -> DiverViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.list_riwayat_order_driver,
                    parent,
                    false
                )
            )
            VIEW_TYPE_RESTO -> RestoViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.list_riwayat_order_resto,
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val list = listData[position]
        when (holder) {
            is DiverViewHolder -> {
                // Bind data for motor order
                holder.bind(list.order!!)
            }
            is RestoViewHolder -> {
                // Bind data for resto order
                holder.bind(list)
            }
        }

        holder.itemView.setOnClickListener {
            if (dialog!=null){
                dialog!!.onClick(position, list.order!!.idOrder.toString())
            }
        }
    }

}