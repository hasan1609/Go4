package com.go4sumbergedang.go4.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.ActivityDetailProdukBinding
import com.go4sumbergedang.go4.model.CartModel
import com.go4sumbergedang.go4.model.ProdukModel
import com.go4sumbergedang.go4.utils.CartUtils
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

class DetailProdukActivity : AppCompatActivity() , AnkoLogger{
    private lateinit var binding: ActivityDetailProdukBinding
    lateinit var detailProduk: ProdukModel
    var jumlah = 1
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()


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
        binding.btnKeranjang.setOnClickListener {
            addToCart()
        }
    }

    private fun addToCart() {
        val newData = CartModel(
            idProduk = detailProduk.idProduk.toString(),
            namaProduk = detailProduk.namaProduk.toString(),
            harga = detailProduk.harga.toString(),
            kategori = detailProduk.kategori.toString(),
            fotoProduk = detailProduk.fotoProduk.toString(),
            jumlah = binding.edtJumlah.text.toString().toInt(),
            catatan = binding.edtCatatan.text.toString()
        )

        CartUtils.addToCart(newData, "id_user",
            onSuccess = {
                val message = "Produk ditambahkan ke keranjang"
                toast(message)

                // Panggil fungsi setCountDataListener() dengan listener di DetailProdukActivity
                CartUtils.setCountDataListener(object : CartUtils.CountDataListener {
                    override fun onCountUpdated(count: Long) {
                        updateCountValue(count)
                    }

                    override fun onError(error: DatabaseError) {
                        // Tangani kesalahan jika ada
                    }
                })
            },
            onFailure = {
                toast("Gagal menambahkan produk ke keranjang")
            }
        )
    }

    override fun onStart() {
        super.onStart()

        CartUtils.startCountDataListener("id_user")
        CartUtils.setCountDataListener(object : CartUtils.CountDataListener {
            override fun onCountUpdated(count: Long) {
                updateCountValue(count)
            }

            override fun onError(error: DatabaseError) {
                // Tangani kesalahan jika ada
            }
        })
    }

    override fun onStop() {
        super.onStop()
        CartUtils.stopCountDataListener("id_user")
    }

    private fun updateCountValue(count: Long) {
        if (count > 0) {
            binding.appBar.divAngka.visibility = View.VISIBLE
            binding.appBar.tvAngka.text = count.toString()
        } else {
            binding.appBar.divAngka.visibility = View.GONE
        }
    }
}