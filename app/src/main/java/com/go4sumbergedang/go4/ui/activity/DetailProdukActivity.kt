package com.go4sumbergedang.go4.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.ActivityDetailProdukBinding
import com.go4sumbergedang.go4.model.ProdukModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

class DetailProdukActivity : AppCompatActivity() , AnkoLogger{
    private lateinit var binding: ActivityDetailProdukBinding
    lateinit var detailProduk: ProdukModel
    var jumlah = 1
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

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
        val newData = ProdukModel(
            idProduk = detailProduk.idProduk.toString(),
            namaProduk = detailProduk.namaProduk.toString(),
            keterangan = detailProduk.keterangan.toString(),
            terjual = detailProduk.terjual.toString(),
            harga = detailProduk.harga.toString(),
            updatedAt = detailProduk.updatedAt.toString(),
            userId = detailProduk.userId.toString(),
            createdAt = detailProduk.createdAt.toString(),
            kategori = detailProduk.kategori.toString(),
            fotoProduk = detailProduk.fotoProduk.toString(),
            status = detailProduk.status.toString()
        )

        val collectionReference = firestore.collection("cart")
        collectionReference.document(newData.userId.toString())
            .set(newData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Tampilkan pesan
                    val message = "Produk ditambahkan ke keranjang"
                    toast(message)
                } else {
                    val exception = task.exception
                    toast("Gagal menambahkan produk ke keranjang: ${exception?.message}")
                    info { "hasan ${exception?.message}" }
                }
            }
            .addOnFailureListener { exception ->
                toast("Gagal menambahkan produk ke keranjang")
                info { "hasan ${exception.message}" }
            }
    }
}