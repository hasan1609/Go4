package com.go4sumbergedang.go4.ui.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.adapter.CartAdapter
import com.go4sumbergedang.go4.databinding.ActivityDetailKeranjangBinding
import com.go4sumbergedang.go4.model.*
import com.go4sumbergedang.go4.utils.LoadingDialogSearch
import com.go4sumbergedang.go4.webservices.ApiClient
import com.go4sumbergedang.go4.webservices.ApiService
import com.google.firebase.database.*
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat

class DetailKeranjangActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var binding: ActivityDetailKeranjangBinding
    var api = ApiClient.instance()
    private lateinit var detailCart: TokoCartModel
    private lateinit var cartAdapter: CartAdapter
    private lateinit var progressDialog: ProgressDialog
    private var loadingDialog: LoadingDialogSearch? = null
    private lateinit var cartListener: ValueEventListener
    private val cartItems = mutableListOf<CartModel>()
    var userId: String? = null
    var addLat: Double? = null
    var addLong: Double? = null
    private var dataAddedAfterBtnBeli = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_keranjang)
        binding.lifecycleOwner = this
        progressDialog = ProgressDialog(this)
        userId = "id_user"
        val gson = Gson()
        detailCart =
            gson.fromJson(intent.getStringExtra("detailCart"), TokoCartModel::class.java)
        binding.appBar.btnToKeranjang.visibility = View.GONE
        binding.appBar.titleTextView.text = detailCart.nama_toko
        binding.appBar.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.txtTambah.setOnClickListener {
            val intent = intentFor<DetailRestoActivity>()
                .putExtra("detailToko", detailCart.idToko.toString())
            startActivity(intent)
        }
        getData(userId.toString(), detailCart.idToko.toString())

        binding.btnBeli.setOnClickListener {
            if (detailCart.idToko != null){
                getDataToko(detailCart.idToko.toString())
                showLoadingDialog()
                addLat?.let { it1 -> addLong?.let { it2 -> cariDriver(it1, it2) } }

                // Set flag to true after clicking btnBeli
                dataAddedAfterBtnBeli = true
            } else {
                toast("idkosong")
            }
            dismissLoadingDialog()
        }
        dismissLoadingDialog()
    }

    private fun deleteData(idProduk: String) {
        val cartReference = FirebaseDatabase.getInstance().reference
            .child("cart")
            .child(userId.toString())
            .child(detailCart.idToko.toString())

        cartReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val cartItemsCount = dataSnapshot.child("cartItems").childrenCount.toInt()
                if (cartItemsCount == 1) {
                    // Hapus data cartItems dan id_toko
                    cartReference.removeValue()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                toast("Berhasil dihapus")
                                finish()
                            } else {
                                toast("Gagal dihapus")
                            }
                        }
                } else {
                    // Hapus data cartItems saja
                    cartReference.child("cartItems")
                        .child(idProduk)
                        .removeValue()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                toast("Berhasil dihapus")
                            } else {
                                toast("Gagal dihapus")
                            }
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                toast("Gagal mendapatkan data")
            }
        })
    }

    private fun setSwipe() {
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            private val limitScrollX = dipToPx(60f, this@DetailKeranjangActivity)
            private var currentScrollX = 0
            private var currentScrollXWhenInActive = 0
            private var initXWhenInActive = 0f
            private var fristInActive = false

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = 0
                val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                return makeMovementFlags(dragFlags, swipeFlags)
            }
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                TODO("Not yet implemented")
            }
            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return Int.MAX_VALUE.toFloat()
            }
            override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
                return Int.MAX_VALUE.toFloat()
            }
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    if (dX == 0f){
                        currentScrollX =viewHolder.itemView.scrollX
                        fristInActive = true
                    }
                    if (isCurrentlyActive){
//                        swipeee with finger
                        var scrollOffset = currentScrollX + (-dX).toInt()
                        if (scrollOffset > limitScrollX){
                            scrollOffset = limitScrollX
                        }else if (scrollOffset < 0){
                            scrollOffset = 0
                        }

                        viewHolder.itemView.scrollTo(scrollOffset, 0)
                    } else {
//                        swipe with animation
                        if (fristInActive){
                            fristInActive = false
                            currentScrollXWhenInActive = viewHolder.itemView.scrollX
                            initXWhenInActive = dX
                        }
                        if (viewHolder.itemView.scrollX < limitScrollX){
                            viewHolder.itemView.scrollTo((currentScrollXWhenInActive * dX / initXWhenInActive).toInt(), 0)
                        }
                    }
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                if (viewHolder.itemView.scrollX > limitScrollX){
                    viewHolder.itemView.scrollTo(limitScrollX, 0)
                } else if(viewHolder.itemView.scrollX < 0){
                    viewHolder.itemView.scrollTo(0, 0)
                }
            }

        }).apply {
            attachToRecyclerView(binding.rvCart)
        }
    }

    private fun dipToPx(dipValue: Float, context: Context): Int{
        return (dipValue * context.resources.displayMetrics.density).toInt()
    }

    private fun getData(idUser: String, idToko: String) {
        loading(true)
        binding.rvCart.layoutManager = LinearLayoutManager(this)
        binding.rvCart.setHasFixedSize(true)
        (binding.rvCart.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL

        val cartReference = FirebaseDatabase.getInstance().reference
            .child("cart")
            .child(idUser).child(idToko).child("cartItems")

        cartListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                loading(false)
                if (!dataAddedAfterBtnBeli) {
                    cartItems.clear()
                    for (cartItemSnapshot in dataSnapshot.children) {
                        val cartItem = cartItemSnapshot.getValue(CartModel::class.java)
                        if (cartItem != null) {
                            // Tambahkan data toko ke list
                            cartItems.add(cartItem)
                        }
                    }
                }
                cartAdapter = CartAdapter(cartItems, this@DetailKeranjangActivity)
                binding.rvCart.adapter = cartAdapter
                setSwipe()

                cartAdapter.setClick(object : CartAdapter.Dialog {
                    override fun onDelete(position: Int, list: CartModel) {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this@DetailKeranjangActivity)
                        builder.setTitle("Hapus Keranjang")
                        builder.setPositiveButton("Ya",
                            DialogInterface.OnClickListener { _, _ ->
                                deleteData(list.idProduk.toString())
                            })
                        builder.setNegativeButton("Batal",
                            DialogInterface.OnClickListener { dialogInterface, _ ->
                                dialogInterface.cancel()
                            })
                        builder.show()
                    }

                    override fun onClick(position: Int, list: CartModel) {
                        val intent = intentFor<DetailProdukActivity>()
                            .putExtra("idProduk", list.idProduk)
                            .putExtra("namaProduk", list.namaProduk)
                            .putExtra("hargaProduk", list.harga)
                            .putExtra("fotoProduk", list.fotoProduk)
                            .putExtra("keteranganProduk", list.keterangan)
                            .putExtra("kategoriProduk", list.kategori)
                            .putExtra("idResto", detailCart.idToko)
                            .putExtra("catatan", list.catatan)
                            .putExtra("namaToko", detailCart.nama_toko)
                            .putExtra("foto", detailCart.foto)
                            .putExtra("keterangan", list.keterangan)
                            .putExtra("jumlahProduk", list.jumlah.toString())
                        startActivity(intent)
                    }
                })
                updateCartItems()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                loading(false)
                toast("Gagal mendapatkan data")
            }
        }
        cartReference.addValueEventListener(cartListener)
    }

    private fun updateCartItems() {
        cartAdapter.notifyDataSetChanged()
        val formatter = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val symbols = formatter.decimalFormatSymbols
        symbols.currencySymbol = "Rp. "
        formatter.decimalFormatSymbols = symbols
        val totals = formatter.format(cartAdapter.getTotalJumlah())
        binding.txtSubTotalProduk.text = totals
        val ongkos = 10000
        val all = cartAdapter.getTotalJumlah() + ongkos
        binding.txtTotalAll.text = formatter.format(all)
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

    private fun showLoadingDialog() {
        loadingDialog = LoadingDialogSearch.create(this)
        loadingDialog?.show()
    }

    private fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist =
            Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(
                Math.toRadians(lat1)
            ) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta))
        dist = Math.acos(dist)
        dist = Math.toDegrees(dist)
        dist *= 60 * 1.1515 // Mengubah menjadi mil
        dist *= 1.609344 // Mengubah menjadi kilometer
        return dist
    }

    private fun cariDriver(lat: Double, long: Double) {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val driverRef = firebaseDatabase.getReference("driver_active")

        // Variabel untuk menyimpan driver terdekat
        var nearestDriverId: String? = null
        var fcmDriverId: String? = null
        var nearestDistance = Double.MAX_VALUE

        // Filter dan ambil driver aktif dengan jarak kurang dari 3 km
        driverRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (driverSnapshot in snapshot.children) {
                    val latitude = driverSnapshot.child("latitude").getValue(Double::class.java) ?: 0.0
                    val longitude = driverSnapshot.child("longitude").getValue(Double::class.java) ?: 0.0
                    val status = driverSnapshot.child("status").getValue(String::class.java) ?: ""
                    val fcm = driverSnapshot.child("kodeFcm").getValue(String::class.java) ?: ""

                    val dist = distance(lat, long, latitude, longitude)
                    if (status == "active" && dist < 3.0 && dist < nearestDistance) {
                        nearestDriverId = driverSnapshot.key ?: ""
                        fcmDriverId = fcm
                        nearestDistance = dist
                    }
                }

                // Lakukan apa pun dengan driver terdekat (nearestDriverId)
                toast("Driver aktif terdekat: $nearestDriverId")
                // Misalnya, menampilkan atau menyimpan hasilnya.
                if (!nearestDriverId.isNullOrEmpty()) {
                    kirimNotifikasiFCM(fcmDriverId.toString())
                    simpanDataKeDatabase(cartItems, nearestDriverId!!)
                }else{
                    simpanDataKeDatabase(cartItems, nearestDriverId!!)
                }
                dismissLoadingDialog()
            }

            override fun onCancelled(error: DatabaseError) {
                dismissLoadingDialog()
            }
        })
    }

    private fun simpanDataKeDatabase(cartItems: List<CartModel>, driverId: String) {
        // Ambil referensi database yang sesuai
        val databaseReference = FirebaseDatabase.getInstance().reference
        val userId = "id_user" // Ganti dengan id user sesuai kebutuhan

        // Buat node baru di database dengan nama "pesanan" atau yang sesuai kebutuhan Anda
        val pesananRef = databaseReference.child("booking").push()

        // Buat objek untuk menyimpan data pesanan
        val cartItemsMap = hashMapOf<String, Any>()
        for ((index, cartItem) in cartItems.withIndex()) {
            cartItemsMap[cartItem.idProduk.toString()] = cartItem
        }

        val pesananData = hashMapOf(
            "userId" to userId,
            "driverId" to driverId,
            "tanggalPesan" to ServerValue.TIMESTAMP,
            "totalHarga" to cartAdapter.getTotalJumlah(),
            "status" to "Sedang Proses",
            "pesanan" to cartItemsMap
        )

        // Simpan data pesanan ke dalam database
        pesananRef.setValue(pesananData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Berhasil menyimpan data
                    toast("Pesanan berhasil disimpan")
                } else {
                    // Gagal menyimpan data
                    toast("Gagal menyimpan pesanan")
                }
            }
    }

    private fun kirimNotifikasiFCM(fcmdriverId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val notification = Notification("Test Notification", "This is a test notification from Retrofit")
        val notificationData = NotificationData(fcmdriverId, notification)

        apiService.sendNotification(notificationData).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Notifikasi berhasil dikirim
                    toast("Notifikasi terkirim ke driver")
                } else {
                    // Gagal mengirim notifikasi
                    toast("Gagal Mengirim Notifikasi")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Tangani kesalahan koneksi atau permintaan
                toast("Gagal mengirim notifikasi ke driver")
            }
        })
    }

    private fun getDataToko(id_Toko: String){
        api.getRestoById(id_Toko).enqueue(object : Callback<ResponseRestoSingle> {
            override fun onResponse(
                call: Call<ResponseRestoSingle>,
                response: Response<ResponseRestoSingle>
            ) {
                try {
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data!!.status == true) {
                            addLat = data.data!!.detailResto!!.latitude!!.toDouble()
                            addLong = data.data.detailResto!!.longitude!!.toDouble()
                        }
                    } else {
                        toast("gagal mendapatkan response")
                    }
                } catch (e: Exception) {
                    info { "hasannne ${e.message}" }
                    toast(e.message.toString())
                }
            }
            override fun onFailure(call: Call<ResponseRestoSingle>, t: Throwable) {
                info { "hasan ${t.message}" }
                toast(t.message.toString())
            }
        })
    }
}