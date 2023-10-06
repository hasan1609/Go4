package com.go4sumbergedang.go4.ui.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.adapter.ItemCartAdapter
import com.go4sumbergedang.go4.adapter.ItemLogOrderRestoAdapter
import com.go4sumbergedang.go4.databinding.ActivityDetailRiwayatOrderBinding
import com.go4sumbergedang.go4.model.*
import com.go4sumbergedang.go4.webservices.ApiClient
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class DetailRiwayatOrderActivity : AppCompatActivity() , AnkoLogger{
    private lateinit var binding: ActivityDetailRiwayatOrderBinding
    private lateinit var progressDialog: ProgressDialog
    var api = ApiClient.instance()
    private lateinit var mAdapterProduk: ItemLogOrderRestoAdapter
    private val produkItems = mutableListOf<ProdukOrderModel>()
    companion object {
        const val idOrder = "idOrder"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_riwayat_order)
        binding.lifecycleOwner = this
        progressDialog = ProgressDialog(this)
        val data = intent.getStringExtra(idOrder)
        binding.toolbar.titleTextView.text = "Detail Order"
        binding.toolbar.backButton.setOnClickListener{
            finish()
        }
        binding.toolbar.btnToKeranjang.visibility = View.GONE
        getData(data.toString())
    }

    private fun getData(idOrder: String) {
        loading(true)
        binding.rvProduk.layoutManager = LinearLayoutManager(this)
        binding.rvProduk.setHasFixedSize(true)
        (binding.rvProduk.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL

        api.getDetailOrderLog(idOrder).enqueue(object : Callback<ResponseDetailLogOrder> {
            override fun onResponse(
                call: Call<ResponseDetailLogOrder>,
                response: Response<ResponseDetailLogOrder>
            ) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                        val data = response.body()
                        val formatter = DecimalFormat.getCurrencyInstance() as DecimalFormat
                        val symbols = formatter.decimalFormatSymbols
                        symbols.currencySymbol = "Rp. "
                        formatter.decimalFormatSymbols = symbols
                        val totalx = data!!.order!!.total!!.toDoubleOrNull() ?: 0.0
                        val ongkir = data.order!!.ongkosKirim!!.toDoubleOrNull() ?: 0.0

                        if (data.produk == null){
                            binding.tvOngkir.text = "Harga Ojek"
                            binding.rvProduk.visibility = View.GONE
                            binding.lySpace2.visibility = View.GONE
                            binding.lyResto.visibility = View.GONE
                        }else{
                            binding.txtSubtotal.text = formatter.format(totalx)
                            binding.namaResto.text = data.order.detailResto!!.namaResto.toString()
                            for (hasil in data.produk) {
                                produkItems.add(hasil!!)
                                mAdapterProduk = ItemLogOrderRestoAdapter(produkItems, this@DetailRiwayatOrderActivity)
                                binding.rvProduk.adapter = mAdapterProduk
                                mAdapterProduk.notifyDataSetChanged()
                            }
                        }
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
                        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                        val date = dateFormat.parse(data.order.createdAt.toString())
                        val formattedDate = SimpleDateFormat("dd MMM yyyy, HH:mm:ss").format(date!!)
                        binding.txtTgl.text = formattedDate
                        binding.txtLokasiAntar.text = data.order.alamatTujuan.toString()
                        binding.txtLokasiJemput.text = data.order.alamatDari.toString()
                        binding.txtOngkir.text = formatter.format(ongkir)

                        if(data.order.status == "0")
                        {
                            binding.txtStatus.text = ""
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
            override fun onFailure(call: Call<ResponseDetailLogOrder>, t: Throwable) {
                loading(false)
                info { "hasan ${t.message}" }
                toast(t.message.toString())
            }
        })
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
}