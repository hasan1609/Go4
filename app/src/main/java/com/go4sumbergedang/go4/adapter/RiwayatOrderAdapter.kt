package com.go4sumbergedang.go4.adapter

import android.content.Context
import android.content.Intent
import android.util.TypedValue
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
import com.go4sumbergedang.go4.ui.activity.DetailRiwayatOrderActivity
import com.squareup.picasso.Picasso
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.w3c.dom.Text
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class RiwayatOrderAdapter (
    private val listData :MutableList<DataLogOrder>,
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), AnkoLogger{

    private val VIEW_TYPE_HEADER = 1


    private val VIEW_TYPE_DRIVER = 3
    private val VIEW_TYPE_RESTO = 4

    private var dialog: Dialog? = null
    interface Dialog {
        fun onClick(position: Int, order: OrderLogModel, status: String)
    }

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    // Data class for header
    data class OrderHeader(val status: String)

    // Combined list of headers and items
    private val items: MutableList<Any> = mutableListOf()
    init {
        // Separate orders by status and add headers
        val ordersByStatus = listData.groupBy { it.order?.status }

        // Menambahkan header "Sedang Proses" jika ada pesanan dengan status 0, 1, 2, 3
        val sedangProsesOrders = ordersByStatus.filterKeys { it in listOf("0", "1", "2", "3") }.values.flatten()
        if (sedangProsesOrders.isNotEmpty()) {
            val sedangProsesHeader = OrderHeader("Sedang Proses")
            items.add(sedangProsesHeader)
            items.addAll(sedangProsesOrders)
        }

        // Menambahkan header "Selesai" jika ada pesanan dengan status 4, 5
        val selesaiOrders = ordersByStatus.filterKeys { it in listOf("4", "5") }.values.flatten()
        if (selesaiOrders.isNotEmpty()) {
            val selesaiHeader = OrderHeader("Selesai")
            items.add(selesaiHeader)
            items.addAll(selesaiOrders)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        if (item is OrderHeader) {
            return VIEW_TYPE_HEADER
        } else if (item is DataLogOrder) {
            // Tentukan viewType berdasarkan kategori pesanan (motor atau mobil)
            return if (item.order!!.kategori == "resto") {
                VIEW_TYPE_RESTO
            } else {
                VIEW_TYPE_DRIVER
            }
        }
        // Jika jenis lainnya atau kesalahan, kembalikan viewType default
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(header: OrderHeader) {
            val headerText = itemView.findViewById<TextView>(R.id.header_textview)
            val layoutParams = headerText.layoutParams as ViewGroup.MarginLayoutParams
            headerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            layoutParams.setMargins(30, 10, 30, 10)
            headerText.layoutParams = layoutParams
            // You can customize the header view here
            headerText.text = header.status
        }
    }

    inner class DiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Implement binding and view setup here for motor order
        fun bind(order: DataLogOrder) {
            val headerText = itemView.findViewById<TextView>(R.id.txt_header)
            val headerIc = itemView.findViewById<ImageView>(R.id.ic_header)
            val fotoDriver = itemView.findViewById<ImageView>(R.id.foto_driver)
            val status = itemView.findViewById<TextView>(R.id.txt_status)
            val review = itemView.findViewById<Button>(R.id.btn_review)
            val urlImage = context.getString(R.string.urlImage)
            val foto= order.order!!.detailDriver!!.foto.toString()
            var def = "/public/images/no_image.png"
            if(order.order.kategori == "mobil"){
                headerText.text = "MOBIL"
                headerIc.setImageDrawable(context.getDrawable(R.drawable.ic_car))
            }else {
                headerText.text = "MOTOR"
                headerIc.setImageDrawable(context.getDrawable(R.drawable.ic_motor))
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
            itemView.findViewById<TextView>(R.id.nama_driver).text = order.order.driver!!.nama
            itemView.findViewById<TextView>(R.id.txt_tujuan).text = order.order.alamatTujuan
            val totalx = order.order.total!!.toDoubleOrNull() ?: 0.0
            val formatter = DecimalFormat.getCurrencyInstance() as DecimalFormat
            val symbols = formatter.decimalFormatSymbols
            symbols.currencySymbol = "Rp. "
            formatter.decimalFormatSymbols = symbols

            val totals = formatter.format(totalx)
            itemView.findViewById<TextView>(R.id.txt_harga).text = totals

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = dateFormat.parse(order.order.createdAt!!)
            val formattedDate = SimpleDateFormat("dd MMM yyyy, HH:mm:ss").format(date!!)
            itemView.findViewById<TextView>(R.id.txt_tgl).text = formattedDate

            when (order.order.status) {
                "0" -> {
                    status.text = "Menunngu Konfirmasi Driver"
                }
                "1" -> {
                    status.text = "Driver menuju lokasi penjemputan"
                }
                "2" -> {
                    status.text = "Driver sedang menuju lokasi tujuan"
                }
                "3" -> {
                    status.text = "Driver menuju lokasi pengantaran"
                }
                "4" -> {
                    status.text = "Driver telah sampai pada tujuan"
                }
                "5" -> {
                    status.text = "Selesai"
                }
                else -> {
                    status.text = "Dibatalkan"
                }
            }
            if(order.order.status == "4" && order.order.reviewId != null){
                review.visibility = View.VISIBLE
            }
            itemView.setOnClickListener {
                if (dialog != null) {
                    dialog!!.onClick(adapterPosition, order.order, order.order.status.toString())
                }
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
            itemView.setOnClickListener {
                if (dialog != null) {
                    dialog!!.onClick(adapterPosition, order.order, order.order.status.toString())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_header_rv_produk,
                    parent,
                    false
                )
            )
            VIEW_TYPE_DRIVER -> {
                // Gunakan layout untuk tampilan driver
                val itemView = LayoutInflater.from(parent.context).inflate(
                    R.layout.list_riwayat_order_driver,
                    parent,
                    false
                )
                DiverViewHolder(itemView)
            }
            VIEW_TYPE_RESTO -> {
                // Gunakan layout untuk tampilan resto
                val itemView = LayoutInflater.from(parent.context).inflate(
                    R.layout.list_riwayat_order_resto,
                    parent,
                    false
                )
                RestoViewHolder(itemView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is HeaderViewHolder -> {
                // Bind data for header view holder
                val header = item as OrderHeader
                holder.bind(header)
            }
            is RestoViewHolder -> {
                // Bind data for resto item view holder
                val order = item as DataLogOrder
                holder.bind(order)
            }
            is DiverViewHolder -> {
                // Bind data for motor item view holder
                val order = item as DataLogOrder
                holder.bind(order)
            }
        }
    }

}