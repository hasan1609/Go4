package com.go4sumbergedang.go4.ui.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.adapter.DriverManualAdapter
import com.go4sumbergedang.go4.adapter.RiwayatOrderAdapter
import com.go4sumbergedang.go4.databinding.ActivityMotorManualBinding
import com.go4sumbergedang.go4.model.*
import com.go4sumbergedang.go4.session.SessionManager
import com.go4sumbergedang.go4.webservices.ApiClient
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MotorManualActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var binding: ActivityMotorManualBinding
    lateinit var mAdapter: DriverManualAdapter
    lateinit var sessionManager: SessionManager
    var api = ApiClient.instance()
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_motor_manual)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)

        binding.appBar.titleTextView.text = "Data Driver Terdekat"
        binding.appBar.backButton.setOnClickListener{
            finish()
        }
        binding.appBar.btnToKeranjang.visibility = View.GONE

        getData(sessionManager.getLatitudeDari().toString(), sessionManager.getLongitudeDari().toString())
    }

    private fun getData(latitude: String, longitude: String)
    {
        binding.lyKoneksi.visibility = View.GONE
        binding.rvDriver.layoutManager = LinearLayoutManager(this)
        binding.rvDriver.setHasFixedSize(true)
        (binding.rvDriver.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        loading(true)
        api.getDriverManual(latitude, longitude).enqueue(object :
            Callback<ResponseDriverManual> {
            override fun onResponse(call: Call<ResponseDriverManual>, response: Response<ResponseDriverManual>) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                        if (response.body()!!.data!!.isNullOrEmpty()){
                            binding.txtKosong.visibility = View.VISIBLE
                            binding.rvDriver.visibility = View.GONE
                        }else{
                            binding.txtKosong.visibility = View.GONE
                            binding.rvDriver.visibility = View.VISIBLE
                            val notesList = mutableListOf<DataDriverManual>()
                            val data = response.body()
                            if (data!!.data != null) {
                                for (hasil in data.data!!) {
                                    notesList.add(hasil!!)
                                }
                                mAdapter = DriverManualAdapter(notesList,this@MotorManualActivity)
                                binding.rvDriver.adapter = mAdapter
                                mAdapter.setDialog(object : DriverManualAdapter.Dialog {
                                    override fun onClick(position: Int, idToko: String) {
                                        TODO("Not yet implemented")
                                    }
                                })
                                mAdapter.notifyDataSetChanged()
                            }
                        }
                    } else {
                        loading(false)
                        info(response)
                        toast("gagal mendapatkan response")
                        binding.lyKoneksi.visibility = View.VISIBLE
                    }

                } catch (e: Exception) {
                    loading(false)
                    info { "hasan ${e.message}" }
                    toast(e.message.toString())
                }
            }

            override fun onFailure(call: Call<ResponseDriverManual>, t: Throwable) {
                loading(false)
                info { "hasan ${t.message}" }
//                    toast(t.message.toString())
                binding.lyKoneksi.visibility = View.VISIBLE
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