package com.go4sumbergedang.go4.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.ActivityTrackingOrderBinding
import com.go4sumbergedang.go4.model.*
import com.go4sumbergedang.go4.session.SessionManager
import com.go4sumbergedang.go4.webservices.ApiClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.maps.android.PolyUtil
import com.squareup.picasso.Picasso
import org.jetbrains.anko.AnkoLogger

class TrackingOrderActivity : AppCompatActivity(), AnkoLogger, OnMapReadyCallback {
    private lateinit var binding: ActivityTrackingOrderBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private val api = ApiClient.instance()
    private lateinit var sessionManager: SessionManager
    private lateinit var mMap: GoogleMap
    var order: OrderLogModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tracking_order)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(this)
        val gson = Gson()
        order = gson.fromJson(intent.getStringExtra("order"), OrderLogModel::class.java)
        val urlImage = getString(R.string.urlImage)
        when (order!!.status) {
            "0" -> {
                binding.contentBottom.status.text = "Menunngu Konfirmasi Driver"
            }
            "1" -> {
                binding.contentBottom.status.text = "Driver menuju lokasi penjemputan"
            }
            "2" -> {
                binding.contentBottom.status.text = "Driver sedang menuju lokasi tujuan"
            }
            "3" -> {
                binding.contentBottom.status.text = "Driver menuju lokasi pengantaran"
            }
            "4" -> {
                binding.contentBottom.status.text = "Driver telah sampai pada tujuan"
            }
            "5" -> {
                binding.contentBottom.status.text = "Selesai"
            }
            else -> {
                binding.contentBottom.status.text = "Dibatalkan"
            }
        }
        if (order!!.detailDriver!!.foto  != null) {
            Picasso.get()
                .load(urlImage + order!!.detailDriver!!.foto)
                .into(binding.contentBottom.foto)
        }
        binding.contentBottom.txtPlat.text = order!!.detailDriver!!.platNo
        binding.contentBottom.txtKendaraan.text = order!!.detailDriver!!.kendaraan
        binding.contentBottom.nama.text = order!!.driver!!.nama
        when (order!!.kategori) {
            "resto" -> binding.contentBottom.type.setImageResource(R.drawable.makanan)
            "mobil" -> binding.contentBottom.type.setImageResource(R.drawable.mobil)
            else -> binding.contentBottom.type.setImageResource(R.drawable.motor)
        }
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetLayout)
        // Set peekHeight agar hanya 1/4 bagian
        bottomSheetBehavior.peekHeight = resources.displayMetrics.heightPixels / 9
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                }
            }
        })

        val supportMapFragment =
            (supportFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment?)!!
        supportMapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val markerTujuan = BitmapDescriptorFactory.fromResource(R.drawable.ic_pinmap)
        val markerDriverMotor = BitmapDescriptorFactory.fromResource(R.drawable.motor)
        val markerDriverMobil = BitmapDescriptorFactory.fromResource(R.drawable.mobil)
        val markerResto = BitmapDescriptorFactory.fromResource(R.drawable.makanan)
        val markerStart = BitmapDescriptorFactory.fromResource(R.drawable.ic_start_order)
        val lat1 = order!!.detailDriver!!.latitude.toString()
        val long1 = order!!.detailDriver!!.longitude.toString()
        val lat2 = order!!.latitudeDari!!.toString()
        val long2 = order!!.longitudeDari!!.toString()
        val lat3 = order!!.latitudeTujuan!!.toString()
        val long3 = order!!.longitudeTujuan!!.toString()
        if (order!!.kategori == "resto") {
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(lat1.toDouble(), long1.toDouble()))
                    .title("Titik Awal").icon(markerDriverMotor))

            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(lat2.toDouble(), long2.toDouble()))
                    .title("Lokasi Resto").icon(markerResto))
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(lat3.toDouble(), long3.toDouble()))
                    .title("Lokasi Tujuan").icon(markerTujuan))
        } else if(order!!.kategori == "mobil") {
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(lat1.toDouble(), long1.toDouble()))
                    .title("Titik Awal").icon(markerDriverMobil))

            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(lat2.toDouble(), long2.toDouble()))
                    .title("Lokasi Jemput").icon(markerStart))
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(lat3.toDouble(), long3.toDouble()))
                    .title("Lokasi Tujuan").icon(markerTujuan))
        }else if(order!!.kategori == "motor_otomatis" || order!!.kategori == "motor_manual"){
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(lat1.toDouble(), long1.toDouble()))
                    .title("Lokasi Jemput").icon(markerDriverMotor))

            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(lat2.toDouble(), long2.toDouble()))
                    .title("Lokasi Jemput").icon(markerStart))
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(lat3.toDouble(), long3.toDouble()))
                    .title("Lokasi Tujuan").icon(markerTujuan))
        }

        mMap.setOnMapLoadedCallback {
            val gson = Gson()
            val responseRoutes = gson.fromJson(order!!.routes, ResponseRoutes::class.java)
            val polyline = responseRoutes.routes?.firstOrNull()?.overviewPolyline?.points
            setRoute(polyline.toString())
        }

    }

    fun setRoute(polyline: String){
        val routeColor = ContextCompat.getColor(this, R.color.primary_color)
        val decodedPath = PolyUtil.decode(polyline)
        val options = PolylineOptions()
            .color(routeColor)
            .width(20f)
            .addAll(decodedPath)

        mMap.addPolyline(options)

        // Menghitung LatLngBounds yang mencakup seluruh rute
        val builder = LatLngBounds.builder()
        for (point in decodedPath) {
            builder.include(LatLng(point.latitude, point.longitude))
        }
        val bounds = builder.build()

        // Set tampilan peta agar fokus pada rute
        val padding = 300
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mMap.moveCamera(cameraUpdate)
    }

}