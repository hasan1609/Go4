package com.go4sumbergedang.go4.ui.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.ActivityDetailProdukBinding
import com.go4sumbergedang.go4.model.ProdukModel
import com.go4sumbergedang.go4.model.ResponsePostData
import com.go4sumbergedang.go4.session.SessionManager
import com.go4sumbergedang.go4.utils.AddCartSuccessEvent
import com.go4sumbergedang.go4.utils.CartItemCountEvent
import com.go4sumbergedang.go4.utils.CartUtils
import com.go4sumbergedang.go4.webservices.ApiClient
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailProdukActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var binding: ActivityDetailProdukBinding
    var api = ApiClient.instance()
    lateinit var sessionManager: SessionManager
    private lateinit var progressDialog: ProgressDialog
    lateinit var produk: ProdukModel
    private var clickCounter: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_produk)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)
        val gson = Gson()
        produk =
            gson.fromJson(intent.getStringExtra("detailProduk"), ProdukModel::class.java)
        val jumlahIntent = intent.getStringExtra("jumlah")
        val catatanIntent = intent.getStringExtra("catatan")

        binding.appBar.titleTextView.text = "Detail Produk"
        binding.appBar.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.appBar.btnToKeranjang.setOnClickListener {
            startActivity<KeranjangActivity>()
        }
        val urlImage = this.getString(R.string.urlImage)
        val foto = produk.fotoProduk.toString()
        var def = "/public/images/no_image.png"
        if (produk.fotoProduk != null){
            Picasso.get()
                .load(urlImage+foto)
                .into(binding.fotoProduk)
        }else{
            Picasso.get()
                .load(urlImage+def)
                .into(binding.fotoProduk)
        }
        binding.txtNamaProduk.text = produk.namaProduk
        binding.txtHargaProduk.text = produk.harga
        if (produk.keterangan != null) {
            binding.txtKetProduk.visibility = View.VISIBLE
            binding.txtKetProduk.text = produk.keterangan
        } else {
            binding.txtKetProduk.visibility = View.GONE
        }

        if (catatanIntent != "null"){
            binding.edtCatatan.setText(catatanIntent)
        }else{
            binding.edtCatatan.setText("")
        }
        if (jumlahIntent != null ){
            binding.edtJumlah.setText(jumlahIntent)
            clickCounter = jumlahIntent.toInt()
        }else{
            binding.edtJumlah.setText("1")
        }

        binding.icPlus.setOnClickListener {
            clickCounter++
            binding.edtJumlah.setText(clickCounter.toString())
        }

        binding.icMin.setOnClickListener {
            if (clickCounter > 1) {
                clickCounter--
                binding.edtJumlah.setText(clickCounter.toString())
            }
        }

        binding.btnKeranjang.setOnClickListener {
            addCart(
                sessionManager.getId().toString(),
                produk.idProduk.toString(),
                produk.userId.toString(),
                produk.harga.toString()
            )
        }

        EventBus.getDefault().register(this)
        CartUtils.getCartItemCount(sessionManager.getId().toString())
    }

    private fun addCart(userId: String, produkId: String, tokoId: String, harga: String) {
        val catatan = binding.edtCatatan.text.toString()
        val jumlah = binding.edtJumlah.text.toString()

        loading(true)
        api.addToCart(
            userId,
            produkId,
            tokoId,
            jumlah,
            catatan,
            harga
        ).enqueue(object : Callback<ResponsePostData> {
            override fun onResponse(
                call: Call<ResponsePostData>,
                response: Response<ResponsePostData>
            ) {
                if (response.isSuccessful) {
                    loading(false)
                    toast("Produk berhasil ditambahkan")
                    EventBus.getDefault().post(AddCartSuccessEvent()) // Post event untuk sukses menambahkan item ke keranjang
                } else {
                    loading(false)
                    toast("Gagal menambahkan produk")
                    info(response.body())
                }
            }

            override fun onFailure(call: Call<ResponsePostData>, t: Throwable) {
                loading(false)
                toast("Terjadi kesalahan")
                Log.e("AddProdukActivity", "Error: ${t.localizedMessage}")
            }
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCartItemCountEvent(event: CartItemCountEvent) {
        val cartItemCount = event.itemCount
        if (cartItemCount != 0) {
            binding.appBar.divBadge.visibility = View.VISIBLE
        } else {
            binding.appBar.divBadge.visibility = View.GONE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddCartSuccessEvent(event: AddCartSuccessEvent) {
        // Panggil CartUtils untuk mendapatkan data cart setelah item berhasil ditambahkan
        CartUtils.getCartItemCount(sessionManager.getId().toString())
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

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}