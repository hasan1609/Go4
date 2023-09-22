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
import com.go4sumbergedang.go4.model.ResponseOrderLog
import com.go4sumbergedang.go4.ui.activity.DetailRiwayatOrderActivity
import com.go4sumbergedang.go4.webservices.ApiClient
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransaksiFragment : Fragment(), AnkoLogger {
    lateinit var binding: FragmentTransaksiBinding
    var api = ApiClient.instance()
    private lateinit var mAdapter: RiwayatOrderAdapter
//    lateinit var sessionManager: SessionManager
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaksi, container, false)
        binding.lifecycleOwner = this
//        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(requireActivity())

        return binding.root
    }

    private fun getData(userId: String) {
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
                                    override fun onClick(position: Int, idOrder: String) {
                                        val intent = intentFor<DetailRiwayatOrderActivity>()
                                            .putExtra("idOrder", idOrder)
                                        startActivity(intent)
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
                    if (isAdded) {
                        loading(false)
                        info { "hasan ${e.message}" }
                        toast(e.message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<ResponseOrderLog>, t: Throwable) {
                if (isAdded) {
                    info { "hasan ${t.message}" }
                    toast(t.message.toString())
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
        getData("f3ece8ed-6353-4268-bdce-06ba4c6049fe")
    }
}