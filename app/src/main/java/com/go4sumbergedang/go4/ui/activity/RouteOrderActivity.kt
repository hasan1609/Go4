package com.go4sumbergedang.go4.ui.activity

import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.adapter.OngkirAdapter
import com.go4sumbergedang.go4.adapter.RestoTerdekatAdapter
import com.go4sumbergedang.go4.databinding.ActivityRouteOrderBinding
import com.go4sumbergedang.go4.model.OngkirModel
import com.go4sumbergedang.go4.model.ResponseOngkir
import com.go4sumbergedang.go4.session.SessionManager
import com.go4sumbergedang.go4.webservices.ApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class RouteOrderActivity : AppCompatActivity(), AnkoLogger, OnMapReadyCallback {

    private lateinit var binding: ActivityRouteOrderBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    var api = ApiClient.instance()
    private lateinit var mAdapter: OngkirAdapter
    lateinit var sessionManager: SessionManager
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private lateinit var placesClient: PlacesClient
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_order)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(this)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetLayout)

        // Atur callback untuk mengelola perubahan state
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Di sini Anda dapat menangani perubahan slideOffset
                // untuk mengatur perilaku bottom sheet saat digeser.
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Tangani perubahan state
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        // State setengah
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // State penuh
                    }
                    // Lainnya sesuai kebutuhan Anda
                }
            }
        })
        // Set initial state (setengah atau penuh)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        val supportMapFragment =
            (supportFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment?)!!
        supportMapFragment.getMapAsync(this)
    }

    fun getOngkir(lat1: String, long1: String, lat2: String, long2: String){
        binding.contentBottom.rvOngkir.layoutManager = LinearLayoutManager(this)
        binding.contentBottom.rvOngkir.setHasFixedSize(true)
        (binding.contentBottom.rvOngkir.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        api.getOngkir(lat1, long1, lat2, long2).enqueue(object : Callback<ResponseOngkir> {
            override fun onResponse(
                call: Call<ResponseOngkir>,
                response: Response<ResponseOngkir>
            ) {
                try {
                    if (response.isSuccessful) {
                        val notesList = mutableListOf<OngkirModel>()
                        val data = response.body()
                        if (data!!.status == true) {
                            for (hasil in data.data!!) {
                                notesList.add(hasil!!)
                                mAdapter = OngkirAdapter(notesList, this@RouteOrderActivity)
                                binding.contentBottom.rvOngkir.adapter = mAdapter
                                mAdapter.notifyDataSetChanged()
                            }
                        }else{
                            toast("gagal mendapatkan respon")
                        }
                    } else {
                        toast("gagal mendapatkan response")
                    }
                } catch (e: Exception) {
                    info { "hasan ${e.message}" }
                    toast(e.message.toString())
                }
            }
            override fun onFailure(call: Call<ResponseOngkir>, t: Throwable) {
                info { "hasan ${t.message}" }
                toast(t.message.toString())
            }
        })
    }

    override fun onStart() {
        super.onStart()
        getOngkir(
            sessionManager.getLatitudeDari().toString(),
            sessionManager.getLongitudeDari().toString(),
            sessionManager.getLatitudeTujuan().toString(),
            sessionManager.getLongitudeTujuan().toString())
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val markerTujuan = BitmapDescriptorFactory.fromResource(R.drawable.ic_pinmap)
        val markerDari = BitmapDescriptorFactory.fromResource(R.drawable.ic_start_order)

        val lat1 = sessionManager.getLatitudeDari()!!.toDouble()
        val lat2 = sessionManager.getLatitudeTujuan()!!.toDouble()
        val long1 = sessionManager.getLongitudeDari()!!.toDouble()
        val long2 = sessionManager.getLongitudeTujuan()!!.toDouble()

        val builder = LatLngBounds.Builder()
        builder.include(LatLng(lat1, long1))
        builder.include(LatLng(lat2, long2))
        val bounds = builder.build()

        mMap.addMarker(MarkerOptions()
            .position(LatLng(lat1, long1))
            .title("Lokasi Jemput").icon(markerDari))

        mMap.addMarker(MarkerOptions()
            .position(LatLng(lat2, long2))
            .title("Lokasi Tujuan").icon(markerTujuan))

        val padding = 50 // Atur padding sesuai kebutuhan
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mMap.moveCamera(cu)
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat1, long1), 15f))
    }
}