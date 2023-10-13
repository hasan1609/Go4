package com.go4sumbergedang.go4.ui.activity

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.ActivityPilihLokasiBinding
import com.go4sumbergedang.go4.session.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class PilihLokasiActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var binding: ActivityPilihLokasiBinding
    lateinit var sessionManager: SessionManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pilih_lokasi)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(this)

        Places.initialize(this, getString(R.string.google_maps_key))
        // Inisialisasi FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Cek izin lokasi
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Mendapatkan lokasi terkini
            getLastLocation()
        } else {
            // Handle jika izin lokasi tidak diberikan
            // Anda bisa meminta izin di sini
        }

        binding.back.setOnClickListener { onBackPressed() }
        setupEditTextFocus(binding.edtDari, "lokasiJemput")
        setupEditTextFocus(binding.edtTujuan, "lokasiTujuan")
    }

    override fun onStart() {
        super.onStart()
        if (sessionManager.getLokasiDari() != null) {
            binding.edtDari.setText(sessionManager.getLokasiDari())
        }
        if (sessionManager.getLokasiTujuan() != null) {
            binding.edtTujuan.setText(sessionManager.getLokasiTujuan())
        }
        setFocusOnEmptyEditText()
        // Set visibilitas awal lokasiSekarang berdasarkan fokus edtDari
        binding.lokasiSekarang.visibility = if (binding.edtDari.hasFocus()) View.VISIBLE else View.GONE

    }

    private fun setupEditTextFocus(editText: EditText, type: String) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editText.setBackgroundResource(R.drawable.bg_edt_text)
                binding.btnMaps.setOnClickListener {
                    startActivity<ActivityMaps>("type" to type)
                }
                binding.lokasiSekarang.visibility = if (editText == binding.edtDari) View.VISIBLE else View.GONE
            } else {
                editText.setBackgroundResource(R.drawable.bg_edt_text_transparent)
            }
        }
    }

    private fun setFocusOnEmptyEditText() {
        if (binding.edtDari.text.isEmpty()) {
            requestFocusAndCursorToStart(binding.edtDari)
            binding.lokasiSekarang.visibility = View.VISIBLE
        } else if (binding.edtTujuan.text.isEmpty()) {
            requestFocusAndCursorToStart(binding.edtTujuan)
        } else {
            clearFocusFromEditTexts()
        }
    }

    private fun requestFocusAndCursorToStart(editText: EditText) {
        editText.requestFocus()
        editText.setSelection(0)
    }

    private fun clearFocusFromEditTexts() {
        binding.edtDari.clearFocus()
        binding.edtTujuan.clearFocus()
    }

    private fun getLastLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    getAddressFromLocation(latitude, longitude)
                }
            }
            .addOnFailureListener { e ->
                // Penanganan kesalahan jika gagal mendapatkan lokasi terkini
                e.printStackTrace()
            }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val placesClient: PlacesClient = Places.createClient(this)
        val placeFields = listOf(Place.Field.ADDRESS)
        val request = FindCurrentPlaceRequest.newInstance(placeFields)
        val placeResponse = placesClient.findCurrentPlace(request)

        placeResponse.addOnSuccessListener { response ->
            val placeLikelihood = response.placeLikelihoods.firstOrNull()
            val address = placeLikelihood?.place?.address
            if (address != null) {
                binding.alamatSekarang.text = address
                binding.lokasiSekarang.setOnClickListener {
                    binding.edtDari.setText(address)
                    sessionManager.setLokasiDari(address)
                }
            } else {
                toast("Tidak dapat menemukan alamat")
            }
        }.addOnFailureListener { exception ->
            // Penanganan kesalahan jika gagal mendapatkan alamat
            exception.printStackTrace()
        }
    }
}
