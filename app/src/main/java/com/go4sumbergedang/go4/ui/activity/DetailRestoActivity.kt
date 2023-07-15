package com.go4sumbergedang.go4.ui.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.adapter.ProdukAdapter
import com.go4sumbergedang.go4.databinding.ActivityDetailRestoBinding
import com.go4sumbergedang.go4.model.*
import com.go4sumbergedang.go4.utils.CartUtils
import com.go4sumbergedang.go4.webservices.ApiClient
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.firebase.database.DatabaseError
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.custom_appbar.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailRestoActivity : AppCompatActivity() , AnkoLogger{
    private lateinit var binding: ActivityDetailRestoBinding
    lateinit var tokoItemModel: TokoItemModel
    lateinit var mAdapter: ProdukAdapter
    private lateinit var progressDialog: ProgressDialog
    var api = ApiClient.instance()

    companion object {
        const val idToko = "detailToko"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_resto)
        binding.lifecycleOwner = this
        progressDialog = ProgressDialog(this)
        val data = intent.getStringExtra(idToko)

        binding.appBar.titleTextView.text = "Data Produk"
        binding.appBar.backButton.setOnClickListener{
            onBackPressed()
        }

        getData(data.toString(), "-7.649166","112.682555")
    }

    private fun getData(id: String, lat: String, long:String) {
        binding.rvProduk.layoutManager = LinearLayoutManager(this)
        binding.rvProduk.setHasFixedSize(true)
        (binding.rvProduk.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        loading(true)
        api.getProdukByIdResto(id, lat, long).enqueue(object : Callback<ResponseResto> {
            override fun onResponse(
                call: Call<ResponseResto>,
                response: Response<ResponseResto>
            ) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                        val data = response.body()
                        val dataResto = data!!.data!!.get(0)!!
                        val urlImage = getString(R.string.urlImage)
                        var def = "/public/images/no_image.png"
                        val  fotoResto = dataResto.foto
                        if (data.data!![0]!!.foto != null) {
                            Picasso.get()
                                .load(urlImage+fotoResto)
                                .into(binding.fotoResto)
                        }else{
                            Picasso.get()
                                .load(urlImage+def)
                                .into(binding.fotoResto)
                        }
                        binding.txtNamaResto.text = dataResto.namaResto
                        binding.txtJarak.text = dataResto.distance.toString() + " KM"

                        if (dataResto.produk!!.isEmpty()){
                            binding.rvProduk.visibility = View.GONE
                            binding.txtKosong.visibility = View.VISIBLE
                        }else{
                            val produkList = dataResto.produk ?: emptyList<ProdukModel>()
                            val groupedProduk = groupProdukByKategori(produkList as List<ProdukModel>)
                            binding.rvProduk.visibility = View.VISIBLE
                            binding.txtKosong.visibility = View.GONE
                                mAdapter = ProdukAdapter(groupedProduk, this@DetailRestoActivity)
                                binding.rvProduk.adapter = mAdapter
                                mAdapter.setDialog(object : ProdukAdapter.Dialog{
                                    override fun onClick(position: Int, note: ProdukModel) {
                                        val gson = Gson()
                                        val noteJson = gson.toJson(note)
                                        startActivity<DetailProdukActivity>("detailProduk" to noteJson)
                                    }
                                })
                        }
                    } else {
                        loading(false)
                        toast("gagal mendapatkan response")
                    }
                } catch (e: Exception) {
                    loading(false)
                    info { "hasan ${e.message}" }
                    toast(e.message.toString())
                }
            }
            override fun onFailure(call: Call<ResponseResto>, t: Throwable) {
                loading(false)
                info { "hasan ${t.message}" }
                toast(t.message.toString())
            }
        })
    }

    private fun groupProdukByKategori(produkList: List<ProdukModel>): List<Any> {
        val groupedProduk = mutableListOf<Any>()
        val kategoriMap = hashMapOf<String, MutableList<ProdukModel>>()

        produkList.forEach { produk ->
            val kategori = produk.kategori
            if (kategori != null) {
                if (kategoriMap.containsKey(kategori)) {
                    kategoriMap[kategori]?.add(produk)
                } else {
                    kategoriMap[kategori] = mutableListOf(produk)
                }
            }
        }

        kategoriMap.forEach { (kategori, produkList) ->
            groupedProduk.add(kategori)
            groupedProduk.addAll(produkList)
        }

        return groupedProduk
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