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
import com.go4sumbergedang.go4.adapter.TokoCartAdapter
import com.go4sumbergedang.go4.databinding.ActivityDetailKeranjangBinding
import com.go4sumbergedang.go4.model.CartModel
import com.go4sumbergedang.go4.model.TokoItemModel
import com.go4sumbergedang.go4.utils.LoadingDialogSearch
import com.google.firebase.database.*
import com.google.gson.Gson
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import java.text.DecimalFormat

class DetailKeranjangActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var binding: ActivityDetailKeranjangBinding
    private lateinit var detailCart: TokoItemModel
    private lateinit var cartAdapter: CartAdapter
    private lateinit var progressDialog: ProgressDialog
    private var loadingDialog: LoadingDialogSearch? = null
    private lateinit var cartListener: ValueEventListener
    private val cartItems = mutableListOf<CartModel>()
    var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_keranjang)
        binding.lifecycleOwner = this
        progressDialog = ProgressDialog(this)
        userId = "id_user"
        val gson = Gson()
        detailCart =
            gson.fromJson(intent.getStringExtra("detailCart"), TokoItemModel::class.java)
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
            showLoadingDialog()
            main()
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
                cartItems.clear()
                for (cartItemSnapshot in dataSnapshot.children) {
                    val cartItem = cartItemSnapshot.getValue(CartModel::class.java)
                    if (cartItem != null) {
                        // Tambahkan data toko ke list
                        cartItems.add(cartItem)
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

    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
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

    fun main() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val driverRef = firebaseDatabase.getReference("driver_active")

        // Lokasi Anda
        val yourLatitude = -7.655570936875087
        val yourLongitude = 112.68822961870752

        // Variabel untuk menyimpan driver terdekat
        var nearestDriverId: String? = null
        var nearestDistance = Double.MAX_VALUE

        // Filter dan ambil driver aktif dengan jarak kurang dari 3 km
        driverRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (driverSnapshot in snapshot.children) {
                    val latitude = driverSnapshot.child("latitude").getValue(Double::class.java) ?: 0.0
                    val longitude = driverSnapshot.child("longitude").getValue(Double::class.java) ?: 0.0
                    val status = driverSnapshot.child("status").getValue(String::class.java) ?: ""

                    val dist = distance(yourLatitude, yourLongitude, latitude, longitude)
                    if (status == "aktif" && dist < 3.0 && dist < nearestDistance) {
                        nearestDriverId = driverSnapshot.key ?: ""
                        nearestDistance = dist
                    }
                }

                // Lakukan apa pun dengan driver terdekat (nearestDriverId)
                info("Driver aktif terdekat: $nearestDriverId")
                // Misalnya, menampilkan atau menyimpan hasilnya.
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error (if any)
            }
        })
    }
}