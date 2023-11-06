package com.go4sumbergedang.go4.ui.activity

import android.app.ProgressDialog
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.adapter.OngkirAdapter
import com.go4sumbergedang.go4.databinding.ActivityCekOngkirOrderBinding
import com.go4sumbergedang.go4.model.*
import com.go4sumbergedang.go4.session.SessionManager
import com.go4sumbergedang.go4.utils.LoadingDialogSearch
import com.go4sumbergedang.go4.webservices.ApiClient
import com.go4sumbergedang.go4.webservices.MapsService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.PolyUtil
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CekOngkirOrderActivity : AppCompatActivity(), AnkoLogger, OnMapReadyCallback {

    private lateinit var binding: ActivityCekOngkirOrderBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private val api = ApiClient.instance()
    private lateinit var mAdapter: OngkirAdapter
    private var loadingDialog: LoadingDialogSearch? = null
    private lateinit var sessionManager: SessionManager
    private lateinit var mMap: GoogleMap
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cek_ongkir_order)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetLayout)
        // Set peekHeight agar hanya 1/4 bagian
        bottomSheetBehavior.peekHeight = resources.displayMetrics.heightPixels / 9
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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

        binding.contentBottom.btnPilh.setOnClickListener {
            val selectedOngkirModel = mAdapter.getSelectedOngkirModel()
            if (selectedOngkirModel != null) {
                showLoadingDialog()
                api.addBookingDriver(
                    sessionManager.getLokasiTujuan().toString(),
                    sessionManager.getLatitudeTujuan().toString(),
                    sessionManager.getLongitudeTujuan().toString(),
                    sessionManager.getLongitudeDari().toString(),
                    sessionManager.getLatitudeDari().toString(),
                    sessionManager.getLokasiDari().toString(),
                    selectedOngkirModel.jenisKendaraan.toString(),
                    selectedOngkirModel.harga.toString(),
                    sessionManager.getId().toString()
                ).enqueue(object : Callback<ResponseSearchDriver> {
                    override fun onResponse(
                        call: Call<ResponseSearchDriver>,
                        response: Response<ResponseSearchDriver>
                    ) {
                        if (response.isSuccessful) {
                            if (response.body()!!.status != false){
                                dismissLoadingDialog()
                                toast("Driver Ditemukan")
                                sessionManager.setisOrderTransport(true)
                                val gson = Gson()
                                val noteJoson = gson.toJson(response.body()!!.data)
                                startActivity<TrackingOrderActivity>("order" to noteJoson)
                                setSession()
                                finish()
                            }else{
                                dismissLoadingDialog()
                                toast("Driver Kosong")
                                info(response.body())
                            }
                        } else {
                            dismissLoadingDialog()
                            toast("Kesalahan Response")
                            info(response.body())
                        }
                    }

                    override fun onFailure(call: Call<ResponseSearchDriver>, t: Throwable) {
                        toast("Terjadi kesalahan")
                        Log.e("AddProdukActivity", "Error: ${t.localizedMessage}")
                        dismissLoadingDialog()
                    }
                })
                selectedOngkirModel.harga.toString()
            } else {
                toast("Pilih Kendaraan dahulu")
            }
        }
    }

    private fun getOngkir(lat1: String, long1: String, lat2: String, long2: String) {
        binding.contentBottom.rvOngkir.layoutManager = LinearLayoutManager(this)
        binding.contentBottom.rvOngkir.setHasFixedSize(true)
        (binding.contentBottom.rvOngkir.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        loading(true)
        api.getOngkir(lat1, long1, lat2, long2).enqueue(object : Callback<ResponseOngkir> {
            override fun onResponse(
                call: Call<ResponseOngkir>,
                response: Response<ResponseOngkir>
            ) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                        val notesList = mutableListOf<OngkirModel>()
                        val data = response.body()
                        if (data!!.status == true) {
                            for (hasil in data.data!!) {
                                notesList.add(hasil!!)
                                mAdapter = OngkirAdapter(notesList, this@CekOngkirOrderActivity)
                                binding.contentBottom.rvOngkir.adapter = mAdapter
                                mAdapter.notifyDataSetChanged()
                            }
                        } else {
                            toast("gagal mendapatkan respon")
                        }
                    } else {
                        loading(false)
                        toast("gagal mendapatkan response")
                    }
                } catch (e: Exception) {
                    loading(false)
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
            sessionManager.getLongitudeTujuan().toString()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        setSession()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setSession()
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

        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(lat1, long1))
                .title("Lokasi Jemput").icon(markerDari)
        )

        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(lat2, long2))
                .title("Lokasi Tujuan").icon(markerTujuan)
        )

        // Set tampilan peta agar fokus pada rute
        val padding = 400 // Atur padding sesuai kebutuhan
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mMap.moveCamera(cameraUpdate)
    }

    private fun showLoadingDialog() {
        loadingDialog = LoadingDialogSearch.create(this)
        loadingDialog?.show()
    }

    private fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
    }
    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            progressDialog.setMessage("Tunggu sebentar...")
            progressDialog.setCancelable(false)
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }

    fun setSession(){
        sessionManager.setLongitudeDari("")
        sessionManager.setLatitudeDari("")
        sessionManager.setLongitudeTujuan("")
        sessionManager.setLatitudeTujuan("")
        sessionManager.setLokasiDari("")
        sessionManager.setLokasiTujuan("")
    }
}