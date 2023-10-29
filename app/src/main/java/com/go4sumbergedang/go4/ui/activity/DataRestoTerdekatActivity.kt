package com.go4sumbergedang.go4.ui.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.adapter.RestoTerdekatAdapter
import com.go4sumbergedang.go4.databinding.ActivityDataRestoTerdekatBinding
import com.go4sumbergedang.go4.model.ResponseResto
import com.go4sumbergedang.go4.model.RestoNearModel
import com.go4sumbergedang.go4.session.SessionManager
import com.go4sumbergedang.go4.utils.CartItemCountEvent
import com.go4sumbergedang.go4.utils.CartUtils
import com.go4sumbergedang.go4.webservices.ApiClient
import com.google.firebase.database.DatabaseError
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.intentFor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataRestoTerdekatActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var binding: ActivityDataRestoTerdekatBinding
    lateinit var mAdapter: RestoTerdekatAdapter
    lateinit var sessionManager: SessionManager
    var api = ApiClient.instance()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_data_resto_terdekat)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)
        EventBus.getDefault().register(this)
        CartUtils.getCartItemCount(sessionManager.getId().toString())
        binding.appBar.titleTextView.text = "Resto Terdekat"
        binding.appBar.backButton.setOnClickListener{
            finish()
        }
        binding.appBar.btnToKeranjang.setOnClickListener {
            startActivity<KeranjangActivity>()
        }

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
                        val notesList = mutableListOf<RestoNearModel>()
                        val data = response.body()
                        if (data!!.status == true) {
                            loading(false)
                            if (data.data!!.isEmpty()){
                                loading(false)
                                binding.rvRestoTerdekat.visibility = View.GONE
                                binding.txtKosong.visibility = View.VISIBLE
                            }else{
                                loading(false)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCartItemCountEvent(event: CartItemCountEvent) {
        val cartItemCount = event.itemCount
        if (cartItemCount != 0) {
            binding.appBar.divBadge.visibility = View.VISIBLE
        } else {
            binding.appBar.divBadge.visibility = View.GONE
        }
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

    override fun onStart() {
        super.onStart()
        if(sessionManager.getLatitude().toString() == "" && sessionManager.getLongitude().toString() == ""){
            val intent = intentFor<ActivityMaps>()
                .putExtra("type", "alamatku")
            startActivity(intent)
        }else{
            getData(sessionManager.getLatitude().toString(), sessionManager.getLongitude().toString())
        }
    }
}