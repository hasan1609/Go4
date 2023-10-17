package com.go4sumbergedang.go4.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.ActivityMapsBinding
import com.go4sumbergedang.go4.session.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity
import java.util.*

class ActivityMaps : AppCompatActivity(), AnkoLogger, OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    lateinit var binding: ActivityMapsBinding
    lateinit var sessionManager: SessionManager
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private lateinit var placesClient: PlacesClient
    private lateinit var geocoder: Geocoder
    private val permissionCode = 101
    var dataType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(this)
        dataType = intent.getStringExtra("type")
        setupUI()
    }

    private fun setupUI() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        checkMyLocationPermission()
        binding.back.setOnClickListener { finish() }
        binding.btnPilihLokasi.text = when (dataType) {
            "alamatku" -> "Pilih Lokasi Anda"
            "lokasiJemput" -> "Pilih Lokasi Jemput"
            else -> "Pilih Lokasi Tujuan"
        }
        if (dataType != "alamatku" && dataType != "lokasiJemput") {
            binding.tvTujuan.visibility = View.VISIBLE
            binding.lytujuan.visibility = View.VISIBLE
            binding.txtAlamat.text = sessionManager.getLokasiDari()
            binding.icPin.setImageResource(R.drawable.ic_pinmap)
        }
    }

    private fun checkMyLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
        } else {
            val task = mFusedLocationProviderClient.lastLocation
            task.addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                    val supportMapFragment =
                        (supportFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment?)!!
                    supportMapFragment.getMapAsync(this)
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Places.initialize(this, getString(R.string.google_maps_key))
        mMap = googleMap
        placesClient = Places.createClient(this)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLocation.latitude, currentLocation.longitude), 20f))
        mMap.isMyLocationEnabled = true
        mMap.setOnCameraIdleListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkMyLocationPermission()
        }
    }

    override fun onCameraIdle() {
        val cameraPosition = mMap.cameraPosition.target
        val location = Location("")
        location.latitude = cameraPosition.latitude
        location.longitude = cameraPosition.longitude
        val address = getAddressFromLocation(location)
        if (dataType != "alamatku" && dataType != "lokasiJemput"){
            binding.txtAlamatTujuan .text = address
        }else{
            binding.txtAlamat.text = address
        }
        binding.btnPilihLokasi.setOnClickListener {
            if (dataType == "alamatku") {
                sessionManager.setLatitude(location.latitude.toString())
                sessionManager.setLongitude(location.longitude.toString())
                sessionManager.setLokasiSekarang(address)
                finish()
            }else if(dataType == "lokasiJemput") {
                sessionManager.setLatitudeDari(location.latitude.toString())
                sessionManager.setLongitudeDari(location.longitude.toString())
                sessionManager.setLokasiDari(address)
                finish()
            } else{
                sessionManager.setLatitudeTujuan(location.latitude.toString())
                sessionManager.setLongitudeTujuan(location.longitude.toString())
                sessionManager.setLokasiTujuan(address)
                finish()
                startActivity<CekOngkirOrderActivity>()
            }
        }
    }

    private fun getAddressFromLocation(location: Location): String {
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses.isNotEmpty()) {
                return addresses[0].getAddressLine(0) ?: "Alamat tidak ditemukan"
                binding.btnPilihLokasi.isEnabled = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "Alamat tidak ditemukan"
    }
}
