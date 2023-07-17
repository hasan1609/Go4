package com.go4sumbergedang.go4.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.ActivityDetailProdukBinding
import com.go4sumbergedang.go4.model.CartModel
import com.go4sumbergedang.go4.model.TokoItemModel
import com.go4sumbergedang.go4.utils.CartUtils
import com.google.firebase.database.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class DetailProdukActivity : AppCompatActivity() , AnkoLogger{
    private lateinit var binding: ActivityDetailProdukBinding
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    var namaTokoE: String? = null
    var fotoTokoE: String? = null
    var idProdukE: String? = null
    var namaProdukE: String? = null
    var hargaProdukE: String? = null
    var fotoProdukE: String? = null
    var keteranganProdukE: String? = null
    var idRestoE: String? = null
    var kategoriProdukE: String? = null
    var jumlahProdukE: Int? = null
    var catatanE: String? = null

    companion object {
        const val namaToko = "namaToko"
        const val fotoToko = "foto"
        const val idProduk = "idProduk"
        const val namaProduk = "namaProduk"
        const val hargaProduk = "hargaProduk"
        const val fotoProduk = "fotoProduk"
        const val keteranganProduk = "keteranganProduk"
        const val kategoriProduk = "kategoriProduk"
        const val idResto = "idResto"
        const val jumlahProduk = "jumlahProduk"
        const val catatan = "catatan"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_produk)
        binding.lifecycleOwner = this

        namaTokoE = intent.getStringExtra(namaToko)
        fotoTokoE = intent.getStringExtra(fotoToko)
        idProdukE = intent.getStringExtra(idProduk)
        namaProdukE = intent.getStringExtra(namaProduk)
        hargaProdukE = intent.getStringExtra(hargaProduk)
        fotoProdukE = intent.getStringExtra(fotoProduk)
        keteranganProdukE = intent.getStringExtra(keteranganProduk)
        idRestoE = intent.getStringExtra(idResto)
        kategoriProdukE = intent.getStringExtra(kategoriProduk)
        jumlahProdukE = intent.getStringExtra(jumlahProduk)?.toIntOrNull() ?: 1
        catatanE = intent.getStringExtra(catatan)

        binding.appBar.titleTextView.text = "Detail Produk"
        binding.appBar.backButton.setOnClickListener{
            onBackPressed()
        }
        binding.appBar.btnToKeranjang.setOnClickListener {
            startActivity<KeranjangActivity>()
        }
        binding.txtNamaProduk.text = namaProdukE
        binding.txtHargaProduk.text = hargaProdukE
        if(keteranganProdukE != null) {
            binding.txtKetProduk.visibility = View.VISIBLE
            binding.txtKetProduk.text = keteranganProdukE
        }else{
            binding.txtKetProduk.visibility = View.GONE
        }

        if (catatanE != null){
            binding.edtCatatan.setText(catatanE)
        }

        binding.edtJumlah.setText(jumlahProdukE.toString())

        binding.icPlus.setOnClickListener {
            jumlahProdukE?.let {
                jumlahProdukE = it + 1
                binding.edtJumlah.setText(jumlahProdukE.toString())
            }
        }

        binding.icMin.setOnClickListener {
            jumlahProdukE?.let {
                if (it > 1) {
                    jumlahProdukE = it - 1
                    binding.edtJumlah.setText(jumlahProdukE.toString())
                }
            }
        }

        binding.btnKeranjang.setOnClickListener {
            if (namaTokoE == null && fotoTokoE == null){
                toast("kosong")
            }else{
                addToCart(namaTokoE.toString(), fotoTokoE.toString())
            }
        }
    }

    private fun addToCart(namaCart: String, fotoCart: String) {
        val dataProduk = CartModel()
        dataProduk.idProduk = idProdukE
        dataProduk.namaProduk = namaProdukE
        dataProduk.harga = hargaProdukE
        dataProduk.kategori = kategoriProdukE
        dataProduk.keterangan = keteranganProdukE
        dataProduk.fotoProduk = fotoProdukE
        dataProduk.jumlah = binding.edtJumlah.text.toString().toInt()
        dataProduk.catatan = binding.edtCatatan.text.toString()

        // Ambil data toko dari Firebase
        val cartReference = firebaseDatabase.reference.child("cart")
        val userIdReference = cartReference.child("id_user")
        val tokoReference = userIdReference.child(idRestoE.toString())

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
                        existingProduk?.catatan = dataProduk.catatan
                        if (jumlahProdukE != 1){
                            existingProduk?.jumlah = dataProduk.jumlah
                        }else{
                            existingProduk?.jumlah =  dataProduk.jumlah!! + existingJumlah
                        }
                    } else {
                        // Jika item cart belum ada, tambahkan ke dalam list cartItems
                        cartItems?.put(dataProduk.idProduk.toString(), dataProduk)
                    }
                } else {
                    // Jika data toko belum ada, buat data toko baru dan tambahkan item cart ke dalam list cartItems
                    val cartItems = mutableMapOf<String, CartModel>()
                    cartItems[dataProduk.idProduk.toString()] = dataProduk

                    dataToko = TokoItemModel(
                        idToko = idRestoE.toString(),
                        nama_toko = namaCart,
                        foto = fotoCart,
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