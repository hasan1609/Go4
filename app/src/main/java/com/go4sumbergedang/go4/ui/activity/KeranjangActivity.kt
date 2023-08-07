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
import com.go4sumbergedang.go4.adapter.TokoCartAdapter
import com.go4sumbergedang.go4.databinding.ActivityKeranjangBinding
import com.go4sumbergedang.go4.model.TokoCartModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class KeranjangActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var binding: ActivityKeranjangBinding
    private lateinit var cartAdapter: TokoCartAdapter
    private val cartList: MutableList<TokoCartModel> = mutableListOf()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var cartListener: ValueEventListener
    var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keranjang)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_keranjang)
        binding.lifecycleOwner = this
        progressDialog = ProgressDialog(this)
        binding.appBar.btnToKeranjang.visibility = View.GONE
        binding.appBar.titleTextView.text = "Keranjang"
        binding.appBar.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.rvProduk.layoutManager = LinearLayoutManager(this)
        binding.rvProduk.setHasFixedSize(true)
        (binding.rvProduk.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        cartAdapter = TokoCartAdapter(cartList, this)
        binding.rvProduk.adapter = cartAdapter
    }

    fun getData(id: String) {
        loading(true)
        val cartReference = FirebaseDatabase.getInstance().reference
            .child("cart")
            .child(id)
        cartListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                loading(false)
                cartList.clear()
                for (tokoSnapshot in dataSnapshot.children) {
                    val tokoCart = tokoSnapshot.getValue(TokoCartModel::class.java)
                    if (tokoCart != null) {
                        // Tambahkan data toko ke list
                        cartList.add(tokoCart)
                    }
                }
                if (cartList.isEmpty()) {
                    // Jika data kosong, tampilkan teks kosong
                    binding.txtKosong.visibility = View.VISIBLE
                    binding.rvProduk.visibility = View.GONE
                } else {
                    // Jika ada data, tampilkan RecyclerView
                    binding.txtKosong.visibility = View.GONE
                    binding.rvProduk.visibility = View.VISIBLE
                    // Update adapter
                    cartAdapter.notifyDataSetChanged()
                    cartAdapter.setDialog(object : TokoCartAdapter.Dialog {
                        override fun onClick(position: Int, list: TokoCartModel) {
                            val gson = Gson()
                            val noteJson = gson.toJson(list)
                            startActivity<DetailKeranjangActivity>("detailCart" to noteJson)
                        }
                    })
                    cartAdapter.setOnDeleteClickListener(object : TokoCartAdapter.OnDeleteClickListener {
                        override fun onDeleteClick(position: Int, note: TokoCartModel) {
                            val builder: AlertDialog.Builder = AlertDialog.Builder(this@KeranjangActivity)
                            builder.setTitle("Hapus Keranjang")
                            builder.setPositiveButton("Ya",
                                DialogInterface.OnClickListener { _, _ ->
                                    deleteData(note.idToko.toString())
                                })
                            builder.setNegativeButton("Batal",
                                DialogInterface.OnClickListener { dialogInterface, _ ->
                                    dialogInterface.cancel()
                                })
                            builder.show()
                        }
                    })
                    setSwipe()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                loading(false)
                toast("Gagal mendapatkan data")
            }
        }
        cartReference.addValueEventListener(cartListener)
    }

    fun deleteData(idToko: String) {
        FirebaseDatabase.getInstance().reference
            .child("cart")
            .child(userId.toString())
            .child(idToko)
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toast("Berhasil dihapus")
                } else {
                    toast("Gagal dihapus")
                }
            }
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
        userId = "id_user"
        getData(userId.toString())
    }

    override fun onPause() {
        super.onPause()
        val cartReference = FirebaseDatabase.getInstance().reference
            .child("cart")
            .child(userId.toString())
        cartReference.removeEventListener(cartListener)
    }
}