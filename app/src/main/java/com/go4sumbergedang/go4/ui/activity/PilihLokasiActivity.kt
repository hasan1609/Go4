package com.go4sumbergedang.go4.ui.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.adapter.LokasiAdapter
import com.go4sumbergedang.go4.databinding.ActivityPilihLokasiBinding
import com.go4sumbergedang.go4.session.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.android.synthetic.main.custom_alert_search_driver.*
import org.jetbrains.anko.*

class PilihLokasiActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var binding: ActivityPilihLokasiBinding
    lateinit var sessionManager: SessionManager
    lateinit var progressDialog: ProgressDialog
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private val locationSuggestions = ArrayList<String>()
    private val placeIds = ArrayList<String>()
    private lateinit var lokasiAdapter: LokasiAdapter
    private var activeEditText: EditText? = null  // Menyimpan referensi ke EditText yang aktif
    private val COMPOUND_DRAWABLE_RIGHT_INDEX = 2
    var southwest: LatLng? = null
    var northeast: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pilih_lokasi)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)

        Places.initialize(this, getString(R.string.google_maps_key))
        placesClient = Places.createClient(this)
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
        binding.edtDari.makeClearableEditText(null, null)
        binding.edtTujuan.makeClearableEditText(null, null)

        // Inisialisasi RecyclerView
        lokasiAdapter = LokasiAdapter(this, locationSuggestions, placeIds, object : LokasiAdapter.ItemClickListener {
            override fun onItemClick(location: String, placeId: String) {
                activeEditText?.setText(location) // Misalnya, set lokasi terpilih ke EditText yang aktif
                var type = ""
                if (activeEditText == binding.edtDari){
                    type = "lokasiJemput"
                }else if (activeEditText == binding.edtTujuan){
                    type = "lokasiTujuan"
                }

                val intent = intentFor<ActivityMaps>()
                    .putExtra("type", type)
                    .putExtra("placeId", placeId) // Gantilah placeId dengan nilai yang sesuai
                startActivity(intent)
            }
        })

        binding.rvLokasi.layoutManager = LinearLayoutManager(this@PilihLokasiActivity)
        binding.rvLokasi.adapter = lokasiAdapter

        // Menghubungkan TextWatcher ke edtDari
        binding.edtDari.addTextChangedListener(getTextWatcher(binding.edtDari))
        binding.edtTujuan.addTextChangedListener(getTextWatcher(binding.edtTujuan))
    }

    private fun getTextWatcher(editText: EditText) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            activeEditText = editText
            updateRecyclerViewWithPredictions(s.toString(), editText)
        }
    }

    // Fungsi untuk mengupdate RecyclerView dengan prediksi lokasi
    private fun updateRecyclerViewWithPredictions(query: String, editText: EditText) {
//        // Membuat AutocompleteFilter yang memungkinkan tempat kecil
        val token = AutocompleteSessionToken.newInstance()
//        // Membuat FindAutocompletePredictionsRequest dengan token
        val request = FindAutocompletePredictionsRequest.builder()
            .setTypeFilter(TypeFilter.ESTABLISHMENT)
//            .setLocationBias(
//                RectangularBounds.newInstance(
//                    latlong1,
//                    latlong2
//                )
//            )
            .setSessionToken(token)
            .setCountry("ID")
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            val predictions = response.autocompletePredictions
            // Perbarui RecyclerView sesuai dengan EditText yang sedang aktif
            if (activeEditText == editText) {
//                info(predictions.map { it.getFullText(null).toString() })
//                locationSuggestions.clear()
//                locationSuggestions.addAll(predictions.map { it.getFullText(null).toString() })
//                lokasiAdapter.notifyDataSetChanged()
//                info(predictions.map { it.placeId })
                if (activeEditText == editText) {
                    info(predictions.map { it.getFullText(null).toString() })
                    locationSuggestions.clear()
                    locationSuggestions.addAll(predictions.map { it.getFullText(null).toString() })
                    placeIds.clear()
                    placeIds.addAll(predictions.map { it.placeId.toString() })
                    lokasiAdapter.notifyDataSetChanged()
                    info(predictions.map { it.placeId })
                }
            }
        }.addOnFailureListener { exception ->
            // Tangani kesalahan saat mengambil prediksi.
            exception.printStackTrace()
        }
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
    }

    private fun setupEditTextFocus(editText: EditText, type: String) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editText.setBackgroundResource(R.drawable.bg_edt_text)
                binding.btnMaps.setOnClickListener {
                    startActivity<ActivityMaps>("type" to type)
                }
                if (editText == binding.edtDari){
                    binding.lyLokasiSekarang.visibility = View.VISIBLE
                    binding.div2.visibility = View.VISIBLE
                }else if(editText == binding.edtTujuan){
                    binding.lyLokasiSekarang.visibility = View.GONE
                    binding.div2.visibility = View.GONE
                }
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

    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

    fun EditText.makeClearableEditText(
        onIsNotEmpty: (() -> Unit)?,
        onClear: (() -> Unit)?,
        clearDrawable: Drawable
    ) {
        val updateRightDrawable = {
            this.setCompoundDrawables(null, null,
                if (text.isNotEmpty()) clearDrawable else null,
                null)
        }
        updateRightDrawable()

        this.afterTextChanged {
            if (it.isNotEmpty()) {
                onIsNotEmpty?.invoke()
            }
            updateRightDrawable()
        }
        this.onRightDrawableClicked {
            this.text.clear()
            this.setCompoundDrawables(null, null, null, null)
            onClear?.invoke()
            this.requestFocus()
        }
    }

    fun EditText.makeClearableEditText(onIsNotEmpty: (() -> Unit)?, onCleared: (() -> Unit)?) {
        compoundDrawables[COMPOUND_DRAWABLE_RIGHT_INDEX]?.let { clearDrawable ->
            makeClearableEditText(onIsNotEmpty, onCleared, clearDrawable)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
        this.setOnTouchListener { v, event ->
            var hasConsumed = false
            if (v is EditText) {
                if (event.x >= v.width - v.totalPaddingRight) {
                    if (event.action == MotionEvent.ACTION_UP) {
                        onClicked(this)
                    }
                    hasConsumed = true
                }
            }
            hasConsumed
        }
    }

    private fun getLastLocation() {
        loading(true)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    loading(false)
                    val latitude = location.latitude
                    val longitude = location.longitude
                    // Mengatur RectangularBounds dengan LatLng dari lokasi terkini
                    southwest = LatLng(latitude - 0.01, longitude - 0.01)
                    northeast = LatLng(latitude + 0.01, longitude + 0.01)
                    getAddressFromLocation(latitude, longitude)
                }
            }
            .addOnFailureListener { e ->
                loading(false)
                // Penanganan kesalahan jika gagal mendapatkan lokasi terkini
                e.printStackTrace()
                info(e.printStackTrace())
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
                    sessionManager.setLatitudeDari(latitude.toString())
                    sessionManager.setLongitudeDari(longitude.toString())
                }
            } else {
                toast("Tidak dapat menemukan alamat")
            }
        }.addOnFailureListener { exception ->
            // Penanganan kesalahan jika gagal mendapatkan alamat
            exception.printStackTrace()
        }
    }
    private fun getLatLngFromPlaceId(placeId: String): LatLng? {
        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        try {
            val placeResponse = placesClient.fetchPlace(request).getResult()
            val latLng = placeResponse?.place?.latLng
            return latLng
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun loading(status : Boolean){
        if (status){
            progressDialog.setTitle("Loading...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
        }else{
            progressDialog.dismiss()
        }
    }
}
