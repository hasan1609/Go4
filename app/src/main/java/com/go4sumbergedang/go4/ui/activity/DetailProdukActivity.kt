package com.go4sumbergedang.go4.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.ActivityDetailProdukBinding
import com.go4sumbergedang.go4.model.CartModel
import com.go4sumbergedang.go4.model.ProdukModel
import com.go4sumbergedang.go4.model.TokoItemModel
import com.go4sumbergedang.go4.utils.CartUtils
import com.google.firebase.database.*
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
            onBackPressed()
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
        val dataProduk = CartModel()
        dataProduk.idProduk = detailProduk.idProduk.toString()
        dataProduk.namaProduk = detailProduk.namaProduk.toString()
        dataProduk.harga = detailProduk.harga.toString()
        dataProduk.kategori = detailProduk.kategori.toString()
        dataProduk.fotoProduk = detailProduk.fotoProduk.toString()
        dataProduk.jumlah = binding.edtJumlah.text.toString().toInt()
        dataProduk.catatan = binding.edtCatatan.text.toString()

        // Ambil data toko dari Firebase
        val cartReference = firebaseDatabase.reference.child("cart")
        val userIdReference = cartReference.child("id_user")
        val tokoReference = userIdReference.child("id_toko2")

        // Ambil data toko dari Firebase
        tokoReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var dataToko = dataSnapshot.getValue(TokoItemModel::class.java)

                if (dataToko != null) {
                    // Jika data toko sudah ada
                    val cartItems = dataToko.cartItems

                    if (cartItems != null && cartItems.containsKey(dataProduk.idProduk)) {
                        // Jika item cart dengan key yang sama sudah ada, tambahkan jumlahnya
                        val existingProduk = cartItems[dataProduk.idProduk]
                        val existingJumlah = existingProduk?.jumlah ?: 0
                        existingProduk?.jumlah = existingJumlah + dataProduk.jumlah!!
                    } else {
                        // Jika item cart belum ada, tambahkan ke dalam list cartItems
                        cartItems?.put(dataProduk.idProduk.toString(), dataProduk)
                    }
                } else {
                    // Jika data toko belum ada, buat data toko baru dan tambahkan item cart ke dalam list cartItems
                    val cartItems = mutableMapOf<String, CartModel>()
                    cartItems[dataProduk.idProduk.toString()] = dataProduk

                    dataToko = TokoItemModel(
                        idToko = "id_toko",
                        nama_toko = "ddsfgsjfgj",
                        foto = "gjfjdhfjdf",
                        cartItems = cartItems
                    )
                }

                // Simpan data toko ke Firebase
                tokoReference.setValue(dataToko)
                    .addOnSuccessListener {
                        // Data produk berhasil ditambahkan
                        toast("Produk berhasil ditambahkan")
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        toast("Produk gagal ditambahkan")
                    }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Tangani kesalahan jika terjadi
                toast("Terjadi kesalahan")
            }
        })
    }


    override fun onStart() {
        super.onStart()
        val countDataListener = object : CartUtils.CountDataListener {
            override fun onCountUpdated(count: Long) {
                if (count > 0){
                    binding.appBar.divBadge.visibility = View.VISIBLE
                }else{
                    binding.appBar.divBadge.visibility = View.GONE
                }
            }

            override fun onError(error: DatabaseError) {
                // Tangani kesalahan jika terjadi
            }
        }
        CartUtils.startCountDataListener("id_user", countDataListener)
    }
}