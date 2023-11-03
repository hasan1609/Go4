package com.go4sumbergedang.go4.ui.fragment

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.FragmentHomeBinding
import com.go4sumbergedang.go4.session.SessionManager
import com.go4sumbergedang.go4.ui.activity.*
import com.go4sumbergedang.go4.utils.CartItemCountEvent
import com.go4sumbergedang.go4.utils.CartUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.custom_alert_search_driver.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import java.io.IOException
import java.util.*


class HomeFragment : Fragment(), AnkoLogger {
    private lateinit var binding: FragmentHomeBinding
    lateinit var sessionManager: SessionManager
    private lateinit var progressDialog: ProgressDialog
    private val locationPermissionCode = 123
    private lateinit var geocoder: Geocoder
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater ,R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(requireActivity())
        progressDialog = ProgressDialog(requireActivity())
        EventBus.getDefault().register(this)
        CartUtils.getCartItemCount(sessionManager.getId().toString())
        // Inisialisasi Geocoder
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        binding.lyLokasi.setOnClickListener {
            val intent = intentFor<ActivityMaps>()
                .putExtra("type", "alamatku")
            startActivity(intent)
        }
        binding.cardMakanan.setOnClickListener {
            startActivity<DataRestoTerdekatActivity>()
        }
        binding.btnToKeranjang.setOnClickListener{
            startActivity<KeranjangActivity>()
        }
        binding.cardMotor.setOnClickListener {
            startActivity<PilihLokasiActivity>()
        }
        binding.cardWifi.setOnClickListener {
            toast("Segera hadir")
        }
        binding.pokmas1.setOnClickListener {
            toast("Segera hadir")
        }
        return binding.root
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCartItemCountEvent(event: CartItemCountEvent) {
        val cartItemCount = event.itemCount
        // Update tampilan jumlah item keranjang di aktivitas ini
        if (cartItemCount != 0) {
            binding.divBadge.visibility = View.VISIBLE
        } else {
            binding.divBadge.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        if (sessionManager.getLokasiSekarang()!!.isNotEmpty()) {
            binding.txtLokasi.text = sessionManager.getLokasiSekarang()
        }else{
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            // Memeriksa izin lokasi
            if (checkLocationPermission()) {
                // Jika sudah memiliki izin lokasi, dapatkan lokasi terkini
                getLastLocation()
            } else {
                // Jika belum memiliki izin lokasi, minta izin kepada pengguna
                requestLocationPermission()
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            locationPermissionCode
        )
    }

    private fun getLastLocation() {
        loading(true)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    loading(false)
                    val latitude = location.latitude
                    val longitude = location.longitude
//                    val address = getAddressFromLocation(location)
                    // Mendapatkan alamat dari latitude dan longitude
                    val addressList: List<Address>?
                    try {
                        addressList = geocoder.getFromLocation(latitude, longitude, 1)
                        if (addressList != null && addressList.isNotEmpty()) {
                            val address = addressList[0].getAddressLine(0) // Alamat lengkap
                            // Set TextView dengan alamat
                            sessionManager.setLokasiSekarang(address)
                            binding.txtLokasi.text = address
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    sessionManager.setLatitude(latitude.toString())
                    sessionManager.setLongitude(longitude.toString())
                    info(location)
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Jika pengguna memberikan izin lokasi, dapatkan lokasi terkini
                getLastLocation()
            }
        }
    }

    private fun getAddressFromLocation(location: Location): String {
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses.isNotEmpty()) {
                return addresses[0].getAddressLine(0) ?: "Alamat tidak ditemukan"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "Alamat tidak ditemukan"
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

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this) // Batalkan pendaftaran saat aktivitas dihancurkan
    }
}