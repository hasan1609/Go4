package com.go4sumbergedang.go4.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.ActivityDetailProdukBinding
import com.go4sumbergedang.go4.model.DetailRestoTerdekatModel
import com.go4sumbergedang.go4.model.ProdukModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.custom_appbar.view.*
import org.jetbrains.anko.AnkoLogger

class DetailProdukActivity : AppCompatActivity() , AnkoLogger{
    private lateinit var binding: ActivityDetailProdukBinding
    lateinit var detailProduk: ProdukModel
    var jumlah = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_produk)
        binding.lifecycleOwner = this
        val gson = Gson()
        detailProduk =
            gson.fromJson(intent.getStringExtra("detailProduk"), ProdukModel::class.java)

        binding.appBar.titleTextView.text = "Detail Produk"
        binding.appBar.backButton.setOnClickListener{
            finish()
        }
        binding.txtNamaProduk.text = detailProduk.namaProduk
        binding.txtHargaProduk.text = detailProduk.harga
        binding.edtJumlah.setText(jumlah.toString())
        if(detailProduk.keterangan != null) {
            binding.txtKetProduk.visibility = View.VISIBLE
            binding.txtKetProduk.text = detailProduk.keterangan
        }else{
            binding.txtKetProduk.visibility = View.GONE
            binding.txtKetProduk.text = detailProduk.keterangan
        }

        binding.icPlus.setOnClickListener{
            jumlah++
            binding.edtJumlah.setText(jumlah.toString())
        }
        binding.icMin.setOnClickListener{
            if (jumlah > 1){
                jumlah--
                binding.edtJumlah.setText(jumlah.toString())
            }
        }
    }
}