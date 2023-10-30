package com.go4sumbergedang.go4.ui.activity

import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.LinearInterpolator
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
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.squareup.picasso.Picasso
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity

class TrackingOrderActivity : AppCompatActivity(), AnkoLogger, OnMapReadyCallback {
    private lateinit var binding: ActivityTrackingOrderBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private val api = ApiClient.instance()
    private lateinit var sessionManager: SessionManager
    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null
    var order: OrderLogModel? = null
    private lateinit var databaseReference: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener

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
        binding.contentBottom.chat.setOnClickListener {
            startActivity<ChatActivity>("order" to intent.getStringExtra("order"))
        }
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

        databaseReference = FirebaseDatabase.getInstance().getReference("perjalanan_pengemudi").child(order!!.driverId.toString())// Ganti "nama_referensi" dengan nama referensi yang sesuai di Firebase Anda.
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val latitude = dataSnapshot.child("latitude").getValue(Double::class.java)
                    val longitude = dataSnapshot.child("longitude").getValue(Double::class.java)

                    if (latitude != null && longitude != null) {
                        updateMapWithLocation(latitude, longitude)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error (optional)
            }
        }
        // Tambahkan pendengar ke referensi Firebase
        databaseReference.addValueEventListener(valueEventListener)
    }

    private fun updateMapWithLocation(latitude: Double, longitude: Double) {
        if (marker != null) {
            val newLocation = LatLng(latitude, longitude)
            val startPosition = marker!!.position // Posisi awal marker
            val endPosition = newLocation // Posisi akhir marker (lokasi baru)

            // Hitung arah dan rotasi motor (misalnya, sejajar dengan rute)
            val bearing = SphericalUtil.computeHeading(startPosition, endPosition)

            // Menghitung jarak antara posisi awal dan akhir
            val distance = SphericalUtil.computeDistanceBetween(startPosition, endPosition)

            // Animasi motor berjalan dari posisi awal ke posisi akhir dengan interval waktu tertentu
            val duration = 10000L // Durasi animasi dalam milidetik (2 detik)

            val interpolator = LinearInterpolator()
            val animator = ObjectAnimator.ofObject(marker, "position",
                TypeEvaluator<LatLng> { fraction, startValue, endValue ->
                    return@TypeEvaluator SphericalUtil.interpolate(startValue, endValue,
                        fraction.toDouble()
                    )
                },
                startPosition, endPosition
            )
            animator.duration = duration
            animator.interpolator = interpolator
            animator.start()

            // Rotasi motor (menghadap ke arah rute)
            marker!!.rotation = bearing.toFloat()

            // Anda juga dapat mengatur kamera agar mengikuti perjalanan motor:
            mMap.animateCamera(CameraUpdateFactory.newLatLng(newLocation))
        }
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
            marker = mMap.addMarker(
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
            marker = mMap.addMarker(
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
            marker = mMap.addMarker(
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

    override fun onDestroy() {
        super.onDestroy()
        // Hentikan pemanggilan data saat aktivitas di-destroy
        databaseReference.removeEventListener(valueEventListener)
    }
}