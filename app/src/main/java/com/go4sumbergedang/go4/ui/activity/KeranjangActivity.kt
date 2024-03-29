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
import com.go4sumbergedang.go4.adapter.RestoTerdekatAdapter
import com.go4sumbergedang.go4.adapter.TokoCartAdapter
import com.go4sumbergedang.go4.databinding.ActivityKeranjangBinding
import com.go4sumbergedang.go4.model.*
import com.go4sumbergedang.go4.session.SessionManager
import com.go4sumbergedang.go4.webservices.ApiClient
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KeranjangActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var binding: ActivityKeranjangBinding
    var api = ApiClient.instance()
    lateinit var sessionManager: SessionManager
    private lateinit var cartAdapter: TokoCartAdapter
    private val cartList: MutableList<RestoCartModel> = mutableListOf()
    private lateinit var progressDialog: ProgressDialog
    var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keranjang)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_keranjang)
        binding.lifecycleOwner = this
        progressDialog = ProgressDialog(this)
        sessionManager = SessionManager(this)
        binding.appBar.btnToKeranjang.visibility = View.GONE
        binding.appBar.titleTextView.text = "Keranjang"
        binding.appBar.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    fun getData(id: String, latitude: String, longitude: String) {
        loading(true)
        binding.rvProduk.layoutManager = LinearLayoutManager(this)
        binding.rvProduk.setHasFixedSize(true)
        (binding.rvProduk.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL

        api.getCart(id, latitude, longitude).enqueue(object : Callback<ResponseCart>{
            override fun onResponse(
                call: Call<ResponseCart>,
                response: Response<ResponseCart>
            ) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                        val data = response.body()
                        if (data!!.data!!.isEmpty()) {
                            // Jika data kosong, tampilkan teks kosong
                            binding.txtKosong.visibility = View.VISIBLE
                            binding.rvProduk.visibility = View.GONE
                        } else {
                            binding.txtKosong.visibility = View.GONE
                            binding.rvProduk.visibility = View.VISIBLE
                            cartList.clear()
                            for (hasil in data.data!!) {
                                cartList.add(hasil!!)
                                cartAdapter = TokoCartAdapter(cartList, this@KeranjangActivity)
                                binding.rvProduk.adapter = cartAdapter
                                cartAdapter.setDialog(object : TokoCartAdapter.Dialog {
                                    override fun onClick(position: Int, list: RestoCartModel) {
                                        val gson = Gson()
                                        val noteJson = gson.toJson(list)
                                        startActivity<DetailKeranjangActivity>("detailCart" to noteJson)
                                    }
                                })
                                cartAdapter.setOnDeleteClickListener(object :
                                    TokoCartAdapter.OnDeleteClickListener {
                                    override fun onDeleteClick(
                                        position: Int,
                                        note: RestoCartModel
                                    ) {
                                        val builder: AlertDialog.Builder =
                                            AlertDialog.Builder(this@KeranjangActivity)
                                        builder.setTitle("Hapus Keranjang")
                                        builder.setPositiveButton("Ya",
                                            DialogInterface.OnClickListener { _, _ ->
                                                api.hapusCart(note.tokoId.toString(), note.userId.toString()).enqueue(object :
                                                    Callback<ResponsePostData> {
                                                    override fun onResponse(
                                                        call: Call<ResponsePostData>,
                                                        response: Response<ResponsePostData>
                                                    ) {
                                                        try {
                                                            if (response.body()!!.status == true) {
                                                                cartList.removeAt(position) // Hapus dari dataset di adapter
                                                                cartAdapter.notifyItemRemoved(position)
                                                                cartAdapter.notifyDataSetChanged()
                                                                binding.rvProduk.requestLayout();
                                                                if (cartList.size < 1) {
                                                                    binding.txtKosong.visibility = View.VISIBLE
                                                                    binding.rvProduk.visibility = View.GONE // Jika sisa item hanya 1, selesaikan Activity
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
                                })
                                setSwipe()
                                cartAdapter.notifyDataSetChanged()
                            }
                        }
                    } else {
                        loading(false)
                        info(response.toString())
                        toast("gagal mendapatkan response")
                    }
                } catch (e: Exception) {
                    loading(false)
                    info { "hasan ${e.message}" }
                    toast(e.message.toString())
                }
            }
            override fun onFailure(call: Call<ResponseCart>, t: Throwable) {
                loading(false)
                info { "hasan ${t.message}" }
                toast(t.message.toString())
            }
        })
    }

    private fun setSwipe() {
        ItemTouchHelper(object : ItemTouchHelper.Callback() {

            private val limitScrollX = dipToPx(60f, this@KeranjangActivity)
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
            attachToRecyclerView(binding.rvProduk)
        }
    }

    private fun dipToPx(dipValue: Float, context: Context): Int{
        return (dipValue * context.resources.displayMetrics.density).toInt()
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

    override fun onStart() {
        super.onStart()
        userId = sessionManager.getId().toString()
        getData(userId.toString(), sessionManager.getLatitude().toString(), sessionManager.getLongitude().toString())
    }

    override fun onPause() {
        super.onPause()
    }
}