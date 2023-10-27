package com.go4sumbergedang.go4.ui.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.adapter.ItemCartAdapter
import com.go4sumbergedang.go4.databinding.ActivityDetailKeranjangBinding
import com.go4sumbergedang.go4.model.*
import com.go4sumbergedang.go4.session.SessionManager
import com.go4sumbergedang.go4.utils.LoadingDialogSearch
import com.go4sumbergedang.go4.webservices.ApiClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.google.maps.android.PolyUtil
import com.squareup.picasso.Picasso
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class DetailKeranjangActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var binding: ActivityDetailKeranjangBinding
    var api = ApiClient.instance()
    lateinit var sessionManager: SessionManager
    private lateinit var detailCart: RestoCartModel
    private lateinit var itemcartAdapter: ItemCartAdapter
    private lateinit var progressDialog: ProgressDialog
    private var loadingDialog: LoadingDialogSearch? = null
    private val cartItems = mutableListOf<ItemCartModel>()
    private var ongkire: Int? = null
    private var total = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_keranjang)
        binding.lifecycleOwner = this
        progressDialog = ProgressDialog(this)
        sessionManager = SessionManager(this)
        val gson = Gson()
        detailCart =
            gson.fromJson(intent.getStringExtra("detailCart"), RestoCartModel::class.java)
        binding.appBar.btnToKeranjang.visibility = View.GONE
        binding.appBar.titleTextView.text = detailCart.resto!!.namaResto
        binding.appBar.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.txtLokasi.text = sessionManager.getLokasiSekarang()
        binding.lokasi.setOnClickListener {
            startActivity<ActivityMaps>("type" to "alamatku")
        }
        binding.txtTambah.setOnClickListener {
            val intent = intentFor<DetailRestoActivity>()
                .putExtra("detailToko", detailCart.tokoId)
            startActivity(intent)
        }

        binding.btnBeli.setOnClickListener {
            if (cartItems.isNotEmpty()) {
                val allItemsInfo = StringBuilder()
                for (item in cartItems) {
                    // Lakukan apa pun yang diperlukan dengan data item
                    allItemsInfo.append(item.idCart).append(",")
                }
                info("Semua item dalam RecyclerView:\n$allItemsInfo")
                showLoadingDialog()
                api.addBooking(
                    allItemsInfo.toString(),
                    sessionManager.getLokasiSekarang().toString(),
                    sessionManager.getLatitude().toString(),
                    sessionManager.getLongitude().toString(),
                    detailCart.resto!!.longitude.toString(),
                    detailCart.resto!!.latitude.toString(),
                    detailCart.resto!!.alamat.toString(),
                    "resto",
                    ongkire.toString(),
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
                                val gson = Gson()
                                val noteJoson = gson.toJson(response.body()!!.data)
                                startActivity<TrackingOrderActivity>("order" to noteJoson)
                                finish()
                            }else{
                                dismissLoadingDialog()
                                toast("Driver Kosong")
                            }
                        } else {
                            dismissLoadingDialog()
                            toast("Kesalahan Response")
                            info(response.body())
                        }
                    }

                    override fun onFailure(call: Call<ResponseSearchDriver>, t: Throwable) {
                        toast("Terjadi kesalahan")
                        Log.e("AddProdukActivity", "Error: ${t.message}")
                        dismissLoadingDialog()
                    }
                })
            } else {
                info("Tidak ada item dalam RecyclerView")
            }

        }
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
        api.getItemCart(idToko, idUser, sessionManager.getLatitude().toString(), sessionManager.getLongitude().toString()).enqueue(object : Callback<ResponseItemCart>{
            override fun onResponse(
                call: Call<ResponseItemCart>,
                response: Response<ResponseItemCart>
            ) {
                try {
                    if (response.isSuccessful) {
                        cartItems.clear()
                        loading(false)
                        val data = response.body()
                        val formatter = DecimalFormat.getCurrencyInstance() as DecimalFormat
                        val symbols = formatter.decimalFormatSymbols
                        symbols.currencySymbol = "Rp. "
                        formatter.decimalFormatSymbols = symbols
                        binding.txtSubTotalProduk.text = formatter.format(data!!.totalJumlah)
                        binding.txtOngkir.text = formatter.format(data.ongkir)
                        ongkire = data.ongkir
                        binding.txtTotalAll.text = formatter.format(data.totalAll)
                        for (hasil in data.data!!) {
                            cartItems.add(hasil!!)
                            itemcartAdapter = ItemCartAdapter(cartItems, this@DetailKeranjangActivity)
                            itemcartAdapter.setClick(object : ItemCartAdapter.Dialog {
                                override fun onDelete(position: Int, list: ItemCartModel) {
                                    val builder: AlertDialog.Builder =
                                        AlertDialog.Builder(this@DetailKeranjangActivity)
                                    builder.setTitle("Hapus Item")
                                    builder.setPositiveButton("Ya",
                                        DialogInterface.OnClickListener { _, _ ->
                                            api.hapusByIdCart(list.idCart.toString()).enqueue(object :
                                                Callback<ResponsePostData> {
                                                override fun onResponse(
                                                    call: Call<ResponsePostData>,
                                                    response: Response<ResponsePostData>
                                                ) {
                                                    try {
                                                        if (response.body()!!.status == true) {
                                                            cartItems.removeAt(position) // Hapus dari dataset di adapter
                                                            itemcartAdapter.notifyItemRemoved(position)
                                                            itemcartAdapter.notifyDataSetChanged()
                                                            binding.rvCart.requestLayout();
                                                            // Hitung ulang total jumlah produk
                                                            val newTotal = calculateTotal(cartItems)

                                                            // Update nilai subtotal
                                                            val formatter = DecimalFormat.getCurrencyInstance() as DecimalFormat
                                                            val symbols = formatter.decimalFormatSymbols
                                                            symbols.currencySymbol = "Rp. "
                                                            formatter.decimalFormatSymbols = symbols
                                                            binding.txtSubTotalProduk.text = formatter.format(newTotal)
                                                            total = newTotal
                                                            if (cartItems.size < 1) {
                                                                finish() // Jika sisa item hanya 1, selesaikan Activity
                                                            }
                                                            toast("Berhasil mengapus")
                                                        } else {
                                                            toast("Gagal mengapus")
                                                        }
                                                    } catch (e: Exception) {
                                                        toast("Kesalahan jaringan")
                                                        info { "hasan ${e.message}${response.code()} " }
                                                    }
                                                }
                                                override fun onFailure(
                                                    call: Call<ResponsePostData>,
                                                    t: Throwable
                                                ) {
                                                    toast("Respon server gagal")
                                                }
                                            })
                                        })
                                    builder.setNegativeButton("Batal",
                                        DialogInterface.OnClickListener { dialogInterface, _ ->
                                            dialogInterface.cancel()
                                        })
                                    builder.show()
                                }
                                override fun onClick(position: Int, list: ItemCartModel) {
                                    val gson = Gson()
                                    val noteJson = gson.toJson(list.produk)
                                    val intent = Intent(this@DetailKeranjangActivity, DetailProdukActivity::class.java)
                                    intent.putExtra("detailProduk", noteJson)
                                    intent.putExtra("jumlah", list.jumlah)
                                    intent.putExtra("catatan", list.catatan.toString())
                                    startActivity(intent)
                                }
                            })
                            binding.rvCart.adapter = itemcartAdapter
                            itemcartAdapter.notifyDataSetChanged()
                            setSwipe()
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
            override fun onFailure(call: Call<ResponseItemCart>, t: Throwable) {
                loading(false)
                info { "hasan ${t.message}" }
                toast(t.message.toString())
            }
        })
    }

    private fun calculateTotal(cartItems: MutableList<ItemCartModel>): Double {
        var totals = 0.0
        for (item in cartItems) {
            totals += item.total!!.toDoubleOrNull()!!
        }
        return totals
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

    override fun onStart() {
        super.onStart()
        getData(detailCart.userId.toString(), detailCart.tokoId.toString())
    }

}