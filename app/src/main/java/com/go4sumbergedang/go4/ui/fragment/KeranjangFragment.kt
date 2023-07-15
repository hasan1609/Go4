package com.go4sumbergedang.go4.ui.fragment

import android.app.ProgressDialog
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.adapter.TokoCartAdapter
import com.go4sumbergedang.go4.databinding.FragmentKeranjangBinding
import com.go4sumbergedang.go4.model.TokoItemModel
import com.go4sumbergedang.go4.ui.activity.DetailKeranjangActivity
import com.google.firebase.database.*
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast


class KeranjangFragment : Fragment(), AnkoLogger {
    private lateinit var binding: FragmentKeranjangBinding
    private lateinit var cartAdapter: TokoCartAdapter
    private val cartList: MutableList<TokoItemModel> = mutableListOf()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var cartListener: ValueEventListener
    var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_keranjang, container, false)
        binding.lifecycleOwner = this
        progressDialog = ProgressDialog(requireActivity())
        binding.rvProduk.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvProduk.setHasFixedSize(true)
        (binding.rvProduk.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        cartAdapter = TokoCartAdapter(cartList, requireActivity())
        binding.rvProduk.adapter = cartAdapter
        return binding.root
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
                    val tokoCart = tokoSnapshot.getValue(TokoItemModel::class.java)
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
                        override fun onClick(position: Int, list: TokoItemModel) {
                            val gson = Gson()
                            val noteJson = gson.toJson(list)
                            startActivity<DetailKeranjangActivity>("detailCart" to noteJson)
                        }
                    })
                    cartAdapter.setOnDeleteClickListener(object : TokoCartAdapter.OnDeleteClickListener {
                        override fun onDeleteClick(position: Int, note: TokoItemModel) {
                            deleteData(note.idToko.toString())
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

            private val limitScrollX = dipToPx(60f, requireActivity())
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