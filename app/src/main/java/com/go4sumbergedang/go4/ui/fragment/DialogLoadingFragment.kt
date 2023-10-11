package com.go4sumbergedang.go4.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.model.ResponseSearchDriver
import com.go4sumbergedang.go4.webservices.ApiClient
import kotlinx.android.synthetic.main.custom_dialog_loading.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DialogLoadingFragment: DialogFragment(), AnkoLogger {

    var api = ApiClient.instance()
    private var apiCall: Call<ResponseSearchDriver>? = null

    companion object {
        fun newInstance(
            allItemsInfo: String,
            alamat_tujuan: String,
            latitude_tujuan: String,
            longitude_tujuan: String,
            longitude_dari: String,
            latitude_dari: String,
            alamat_dari: String,
            kategori: String
        ): DialogLoadingFragment {
            val fragment = DialogLoadingFragment()
            val args = Bundle()
            args.putString("allItemsInfo", allItemsInfo)
            args.putString("alamat_tujuan", alamat_tujuan)
            args.putString("latitude_tujuan", latitude_tujuan)
            args.putString("longitude_tujuan", longitude_tujuan)
            args.putString("longitude_dari", longitude_dari)
            args.putString("latitude_dari", latitude_dari)
            args.putString("alamat_dari", alamat_dari)
            args.putString("kategori", kategori)

            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.custom_dialog_loading, null)

        val allItemsInfo = arguments?.getString("allItemsInfo")
        val alamat_tujuan = arguments?.getString("alamat_tujuan")
        val latitude_tujuan = arguments?.getString("latitude_tujuan")
        val longitude_tujuan = arguments?.getString("longitude_tujuan")
        val longitude_dari = arguments?.getString("longitude_dari")
        val latitude_dari = arguments?.getString("latitude_dari")
        val alamat_dari = arguments?.getString("alamat_dari")
        val kategori = arguments?.getString("kategori")

        builder.setView(view)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)

        apiCall = api.addBooking(
            allItemsInfo.toString(),
            alamat_tujuan.toString(),
            latitude_tujuan.toString(),
            longitude_tujuan.toString(),
            longitude_dari.toString(),
            latitude_dari.toString(),
            alamat_dari.toString(),
            kategori.toString()
        )

        apiCall?.enqueue(object : Callback<ResponseSearchDriver> {
            override fun onResponse(
                call: Call<ResponseSearchDriver>,
                response: Response<ResponseSearchDriver>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == false) {
                        toast("Driver Ditemukan")
                        dialog.dismiss()
                    } else {
                        toast("Driver kosong") // Post event untuk sukses menambahkan item ke keranjang
                        dialog.dismiss()
                    }
                } else {
                    dialog.dismiss()
                    toast("Gagal menambahkan produk")
                    info("Gak kenek ${response.body()}")
                }
            }

            override fun onFailure(call: Call<ResponseSearchDriver>, t: Throwable) {
                toast("Terjadi kesalahan")
                dialog.dismiss()
                Log.e("AddProdukActivity", "Error: ${t.localizedMessage}")
            }
        })

        return dialog
    }
}