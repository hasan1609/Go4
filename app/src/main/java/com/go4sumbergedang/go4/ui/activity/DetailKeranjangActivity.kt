package com.go4sumbergedang.go4.ui.activity

import android.content.Context
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
import com.go4sumbergedang.go4.model.ProdukModel
import com.go4sumbergedang.go4.model.TokoItemModel
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import java.text.DecimalFormat

class DetailKeranjangActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var binding: ActivityDetailKeranjangBinding
    lateinit var detailCart: TokoItemModel
    private lateinit var cartAdapter: CartAdapter
//    private val cartList: MutableList<TokoItemModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_keranjang)
        binding.lifecycleOwner = this
        val gson = Gson()
        detailCart =
            gson.fromJson(intent.getStringExtra("detailCart"), TokoItemModel::class.java)
        binding.appBar.btnToKeranjang.visibility = View.GONE
        binding.appBar.titleTextView.text = detailCart.nama_toko
        binding.appBar.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.rvCart.layoutManager = LinearLayoutManager(this)
        binding.rvCart.setHasFixedSize(true)
        (binding.rvCart.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        cartAdapter = CartAdapter(detailCart.cartItems ?: mutableMapOf(), this)
        binding.rvCart.adapter = cartAdapter
        setSwipe()
        cartAdapter.notifyDataSetChanged()
        val formatter = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val symbols = formatter.decimalFormatSymbols
        symbols.currencySymbol = "Rp. "
        formatter.decimalFormatSymbols = symbols
        val totals = formatter.format(cartAdapter.getTotalJumlah())

        binding.txtSubTotalProduk.text = totals
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
}