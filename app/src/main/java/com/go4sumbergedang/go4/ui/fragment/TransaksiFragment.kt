package com.go4sumbergedang.go4.ui.fragment

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.adapter.RiwayatOrderAdapter
import com.go4sumbergedang.go4.databinding.FragmentTransaksiBinding
import com.go4sumbergedang.go4.model.DataLogOrder
import com.go4sumbergedang.go4.model.OrderLogModel
import com.go4sumbergedang.go4.model.ResponseOrderLog
import com.go4sumbergedang.go4.session.SessionManager
import com.go4sumbergedang.go4.ui.activity.DetailRiwayatOrderActivity
import com.go4sumbergedang.go4.ui.activity.TrackingOrderActivity
import com.go4sumbergedang.go4.utils.NetworkUtils
import com.go4sumbergedang.go4.webservices.ApiClient
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_transaksi.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransaksiFragment : Fragment(), AnkoLogger {
    lateinit var binding: FragmentTransaksiBinding
    var api = ApiClient.instance()
    private lateinit var mAdapter: RiwayatOrderAdapter
    lateinit var sessionManager: SessionManager
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaksi, container, false)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(requireActivity())
        progressDialog = ProgressDialog(requireActivity())
        binding.lyKoneksi.btn_refresh.setOnClickListener {
            getData(sessionManager.getId().toString())
        }
        return binding.root
    }

    private fun getData(userId: String) {
        binding.lyKoneksi.visibility = View.GONE
        binding.rvTransaksi.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvTransaksi.setHasFixedSize(true)
        (binding.rvTransaksi.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        loading(true)
        api.getOrderLog(userId).enqueue(object :
            Callback<ResponseOrderLog> {
            override fun onResponse(call: Call<ResponseOrderLog>, response: Response<ResponseOrderLog>) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                        if (response.body()!!.data!!.isEmpty()){
                            binding.txtKosong.visibility = View.VISIBLE
                            binding.rvTransaksi.visibility = View.GONE
                        }else{
                            binding.txtKosong.visibility = View.GONE
                            binding.rvTransaksi.visibility = View.VISIBLE
                            val notesList = mutableListOf<DataLogOrder>()
                            val data = response.body()
                            if (data!!.status == true) {
                                for (hasil in data.data!!) {
                                    notesList.add(hasil!!)
                                }
                                mAdapter = RiwayatOrderAdapter(notesList, requireActivity())
                                binding.rvTransaksi.adapter = mAdapter
                                mAdapter.setDialog(object : RiwayatOrderAdapter.Dialog {
                                    override fun onClick(position: Int, order: OrderLogModel, status: String) {
                                        when (status) {
                                            "0", "1", "2", "3","7" -> {
                                                val gson = Gson()
                                                val noteJoson = gson.toJson(order)
                                                startActivity<TrackingOrderActivity>("order" to noteJoson)
                                            }
                                            else -> {
                                                val intent = intentFor<DetailRiwayatOrderActivity>()
                                                    .putExtra("idOrder", order.idOrder)
                                                startActivity(intent)
                                            }
                                        }
                                    }
                                })
                                mAdapter.notifyDataSetChanged()
                            }
                        }
                    } else {
                        loading(false)
                        toast("gagal mendapatkan response")
                        binding.lyKoneksi.visibility = View.VISIBLE
                    }

                } catch (e: Exception) {
                    if (isAdded) {
                        loading(false)
                        info { "hasan ${e.message}" }
                        toast(e.message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<ResponseOrderLog>, t: Throwable) {
                if (isAdded) {
                    loading(false)
                    info { "hasan ${t.message}" }
//                    toast(t.message.toString())
                    binding.lyKoneksi.visibility = View.VISIBLE
                }
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

    override fun onStart() {
        super.onStart()
        if (NetworkUtils.isInternetAvailable(requireContext())) {
            // Tampilkan konten
            getData(sessionManager.getId().toString())
        } else {
            // Tampilkan logo koneksi tidak ada
            binding.lyKoneksi.visibility = View.VISIBLE
        }
    }
}