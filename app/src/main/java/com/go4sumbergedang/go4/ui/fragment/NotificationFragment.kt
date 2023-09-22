package com.go4sumbergedang.go4.ui.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.adapter.NotifikasiAdapter
import com.go4sumbergedang.go4.adapter.TokoCartAdapter
import com.go4sumbergedang.go4.databinding.FragmentNotificationBinding
import com.go4sumbergedang.go4.model.NotifikasiLogModel
import com.go4sumbergedang.go4.model.ResponseNotifikasiLog
import com.go4sumbergedang.go4.model.ResponsePostData
import com.go4sumbergedang.go4.ui.activity.DetailRiwayatOrderActivity
import com.go4sumbergedang.go4.webservices.ApiClient
import com.google.firebase.database.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NotificationFragment : Fragment(), AnkoLogger {
    private lateinit var binding: FragmentNotificationBinding
    private lateinit var mAdapter: NotifikasiAdapter
    private lateinit var progressDialog: ProgressDialog
    var api = ApiClient.instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false)
        binding.lifecycleOwner = this
        progressDialog = ProgressDialog(requireActivity())
        return binding.root
    }

    private fun getNotifikasi(reciveId: String) {
        binding.rvNotif.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvNotif.setHasFixedSize(true)
        (binding.rvNotif.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.VERTICAL
        loading(true)
        api.getNotifikasiLog(reciveId).enqueue(object :
            Callback<ResponseNotifikasiLog> {
            override fun onResponse(call: Call<ResponseNotifikasiLog>, response: Response<ResponseNotifikasiLog>) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                        if (response.body()!!.data!!.isEmpty()){
                            binding.txtKosong.visibility = View.VISIBLE
                            binding.rvNotif.visibility = View.GONE
                        }else{
                            binding.txtKosong.visibility = View.GONE
                            binding.rvNotif.visibility = View.VISIBLE
                            val noteList = mutableListOf<NotifikasiLogModel>()
                            val data = response.body()
                            if (data!!.status == true) {
                                for (hasil in data.data!!) {
                                    noteList.add(hasil!!)
                                }
                                mAdapter = NotifikasiAdapter(noteList, requireActivity())
                                binding.rvNotif.adapter = mAdapter
                                mAdapter.notifyDataSetChanged()
                                mAdapter.setDialog(object : NotifikasiAdapter.Dialog {
                                    override fun onClick(position: Int, idOrder: String, status: String, idNotifikasi: String) {
                                        val intent = intentFor<DetailRiwayatOrderActivity>()
                                            .putExtra("idOrder", idOrder)

                                        if (status == "0"){
                                            updateStatus(idNotifikasi)
                                        }
                                        startActivity(intent)
                                    }
                                })
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

            override fun onFailure(call: Call<ResponseNotifikasiLog>, t: Throwable) {
                info { "hasan ${t.message}" }
                toast(t.message.toString())
            }
        })
    }

    private fun updateStatus(notifikasiId: String){
        api.updateNotifikasiStatusLog(notifikasiId).enqueue(object :
            Callback<ResponsePostData> {
            override fun onResponse(call: Call<ResponsePostData>, response: Response<ResponsePostData>) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                    } else {
                        loading(false)
                        toast("Gagal mengupdate")
                    }
                } catch (e: Exception) {
                    loading(false)
                    info { "hasan ${e.message}" }
                    toast(e.message.toString())
                }
            }

            override fun onFailure(call: Call<ResponsePostData>, t: Throwable) {
                info { "hasan ${t.message}" }
                toast(t.message.toString())
            }
        })
    }

    override fun onStart() {
        super.onStart()
        getNotifikasi("f3ece8ed-6353-4268-bdce-06ba4c6049fe")
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