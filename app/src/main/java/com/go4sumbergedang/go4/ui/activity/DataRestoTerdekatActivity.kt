package com.go4sumbergedang.go4.ui.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.adapter.RestoTerdekatAdapter
import com.go4sumbergedang.go4.databinding.ActivityDataRestoTerdekatBinding
import com.go4sumbergedang.go4.model.DetailRestoTerdekatModel
import com.go4sumbergedang.go4.model.ResponseRestoTerdekat
import com.go4sumbergedang.go4.webservices.ApiClient
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataRestoTerdekatActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var binding: ActivityDataRestoTerdekatBinding
    lateinit var mAdapter: RestoTerdekatAdapter
    var api = ApiClient.instance()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_data_resto_terdekat)
        binding.lifecycleOwner = this
        progressDialog = ProgressDialog(this)
        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setTitle("Resto Terdekat")
        binding.toolbar.setNavigationIcon(R.drawable.back)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        getData("-7.649166","112.682555")
    }

    private fun getData(latitude: String, longitude : String) {
        binding.rvRestoTerdekat.layoutManager = LinearLayoutManager(this)
        binding.rvRestoTerdekat.setHasFixedSize(true)
        (binding.rvRestoTerdekat.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        loading(true)
        api.getRestoTerdekat(latitude, longitude).enqueue(object : Callback<ResponseRestoTerdekat> {
            override fun onResponse(
                call: Call<ResponseRestoTerdekat>,
                response: Response<ResponseRestoTerdekat>
            ) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                        val notesList = mutableListOf<DetailRestoTerdekatModel>()
                        val data = response.body()
                        if (data!!.status == true) {
                            loading(false)
                            for (hasil in data.data!!) {
                                notesList.add(hasil!!)
                                mAdapter = RestoTerdekatAdapter(notesList, this@DataRestoTerdekatActivity)
                                binding.rvRestoTerdekat.adapter = mAdapter
                                mAdapter.setDialog(object : RestoTerdekatAdapter.Dialog{
                                    override fun onClick(position: Int, note: DetailRestoTerdekatModel) {
                                        val gson = Gson()
                                        val noteJson = gson.toJson(note)
                                        startActivity<DetailRestoActivity>("detailResto" to noteJson)
                                    }
                                })
                                mAdapter.notifyDataSetChanged()
                            }
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
            override fun onFailure(call: Call<ResponseRestoTerdekat>, t: Throwable) {
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