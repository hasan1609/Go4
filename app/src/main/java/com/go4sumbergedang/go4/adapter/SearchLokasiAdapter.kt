package com.go4sumbergedang.go4.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.model.ResultLokasiModel
import com.go4sumbergedang.go4.ui.activity.ActivityMaps
import com.go4sumbergedang.go4.utils.HitungJarak.getDistance
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.list_item_lokasi.view.*
import java.text.DecimalFormat
import java.util.*

class SearchLokasiAdapter(private val context: Context?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val modelResults = ArrayList<ResultLokasiModel>()
    lateinit var simpleLocation: SimpleLocation
    var strLatitude = 0.0
    var strLongitude = 0.0

    fun setResultAdapter(items: ArrayList<ResultLokasiModel>) {
        modelResults.clear()
        modelResults.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_lokasi, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = modelResults[position]

        simpleLocation = SimpleLocation(context)

        //current location
        strLatitude = simpleLocation.latitude
        strLongitude = simpleLocation.longitude
        val strPlaceID = modelResults[position].placeId

        //location destination
        val strLat = modelResults[position].modelGeometry?.modelLocation?.lat
        val strLong = modelResults[position].modelGeometry?.modelLocation?.lng
        val strJarak = getDistance(strLat!!, strLong!!, strLatitude, strLongitude)

        holder.itemView.tvNamaLokasi.text = item.name
        holder.itemView.tvAlamat.text = item.vicinity
        holder.itemView.tvJarak.text = DecimalFormat("#.##").format(strJarak) + " KM"

        holder.itemView.cvListLocation.setOnClickListener { view: View? ->
            val intent = Intent(context, ActivityMaps::class.java)
            intent.putExtra("placeId", strPlaceID)
            intent.putExtra("lat", strLat)
            intent.putExtra("lng", strLong)
            context?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return modelResults.size
    }

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvListLocation: CardView
        val tvNamaLokasi: TextView
        val tvAlamat: TextView
        val tvJarak: TextView

        init {
            cvListLocation = itemView.cvListLocation
            tvNamaLokasi = itemView.tvNamaLokasi
            tvAlamat = itemView.tvAlamat
            tvJarak = itemView.tvJarak
        }
    }

}