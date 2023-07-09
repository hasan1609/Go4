package com.go4sumbergedang.go4.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.ActivityDetailKeranjangBinding
import com.go4sumbergedang.go4.model.ProdukModel
import com.go4sumbergedang.go4.model.TokoItemModel
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger

class DetailKeranjangActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var binding: ActivityDetailKeranjangBinding
    lateinit var detailCart: TokoItemModel

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
    }
}