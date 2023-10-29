package com.go4sumbergedang.go4.ui.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.adapter.OngkirAdapter
import com.go4sumbergedang.go4.databinding.ActivityDetailAkunBinding
import com.go4sumbergedang.go4.model.OngkirModel
import com.go4sumbergedang.go4.model.ResponseDetailCustomer
import com.go4sumbergedang.go4.model.ResponseOngkir
import com.go4sumbergedang.go4.session.SessionManager
import com.go4sumbergedang.go4.webservices.ApiClient
import com.squareup.picasso.Picasso
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailAkunActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var binding: ActivityDetailAkunBinding
    private var api = ApiClient.instance()
    private lateinit var sessionManager: SessionManager
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_akun)
        binding.lifecycleOwner = this
        progressDialog = ProgressDialog(this)
        sessionManager = SessionManager(this)

        binding.appBar.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.appBar.titleTextView.text = "Detail Profil"
        binding.appBar.btnToKeranjang.visibility = View.GONE

        getData(sessionManager.getId().toString())
    }

    private fun getData(id: String) {
        loading(true)
        api.getDetailCustomer(id).enqueue(object : Callback<ResponseDetailCustomer> {
            override fun onResponse(
                call: Call<ResponseDetailCustomer>,
                response: Response<ResponseDetailCustomer>
            ) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                        val data = response.body()
                        if (data!!.status == true) {
                            binding.edtNama.setText(data.data!!.nama)
                            binding.edtEmail.text = data.data.email
                            binding.edtTlp.text = data.data.tlp
                            binding.edtAlamat.setText(data.data.detailCustomer!!.alamat)
                            val urlImage = getString(R.string.urlImage)
                            val foto= data.data.detailCustomer.foto.toString()
                            if (foto != null) {
                                Picasso.get()
                                    .load(urlImage + foto)
                                    .into(binding.foto)
                            }else
                        } else {
                            toast("gagal mendapatkan respon")
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

            override fun onFailure(call: Call<ResponseDetailCustomer>, t: Throwable) {
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