package com.go4sumbergedang.go4.ui.activity

import android.app.ProgressDialog
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.adapter.RestoTerdekatAdapter
import com.go4sumbergedang.go4.databinding.ActivityDataRestoTerdekatBinding
import com.go4sumbergedang.go4.model.ResponseResto
import com.go4sumbergedang.go4.model.RestoModel
import com.go4sumbergedang.go4.utils.CartUtils
import com.go4sumbergedang.go4.webservices.ApiClient
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseError
import com.google.gson.Gson
import org.jetbrains.anko.*
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
        binding.appBar.titleTextView.text = "Resto Terdekat"
        binding.appBar.backButton.setOnClickListener{
            finish()
        }
        getData("-7.649166","112.682555")

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


    private fun getData(latitude: String, longitude : String) {
        binding.rvRestoTerdekat.layoutManager = LinearLayoutManager(this)
        binding.rvRestoTerdekat.setHasFixedSize(true)
        (binding.rvRestoTerdekat.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        loading(true)
        api.getRestoTerdekat(latitude, longitude).enqueue(object : Callback<ResponseResto> {
            override fun onResponse(
                call: Call<ResponseResto>,
                response: Response<ResponseResto>
            ) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                        val notesList = mutableListOf<RestoModel>()
                        val data = response.body()
                        if (data!!.status == true) {
                            loading(false)
                            if (data.data!!.isEmpty()){
                                binding.rvRestoTerdekat.visibility = View.GONE
                                binding.txtKosong.visibility = View.VISIBLE
                            }else{
                                for (hasil in data.data) {
                                    notesList.add(hasil!!)
                                    mAdapter = RestoTerdekatAdapter(notesList, this@DataRestoTerdekatActivity)
                                    binding.rvRestoTerdekat.visibility = View.VISIBLE
                                    binding.txtKosong.visibility = View.GONE
                                    binding.rvRestoTerdekat.adapter = mAdapter
                                    mAdapter.setDialog(object : RestoTerdekatAdapter.Dialog{
                                        override fun onClick(position: Int, idToko: String) {
                                            val intent = intentFor<DetailRestoActivity>()
                                                .putExtra("detailToko", idToko)
                                            startActivity(intent)
                                        }
                                    })
                                    mAdapter.notifyDataSetChanged()
                                }
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
            override fun onFailure(call: Call<ResponseResto>, t: Throwable) {
                loading(false)
                info { "hasan ${t.message}" }
                toast(t.message.toString())
            }
        })
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        CartUtils.stopCountDataListener()
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