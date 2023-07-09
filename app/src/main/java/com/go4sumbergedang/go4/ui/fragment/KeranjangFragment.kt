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
import com.go4sumbergedang.go4.adapter.TokoCartAdapter
import com.go4sumbergedang.go4.databinding.FragmentKeranjangBinding
import com.go4sumbergedang.go4.model.TokoItemModel
import com.go4sumbergedang.go4.ui.activity.DetailKeranjangActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

class KeranjangFragment : Fragment(), AnkoLogger {
    private lateinit var binding: FragmentKeranjangBinding
    private lateinit var cartAdapter: TokoCartAdapter
    private val cartList: MutableList<TokoItemModel> = mutableListOf()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater ,R.layout.fragment_keranjang, container, false)
        binding.lifecycleOwner = this
        progressDialog = ProgressDialog(requireActivity())
        getData("id_user")
        return binding.root
    }

    fun getData(userId: String) {
        loading(true)
        binding.rvProduk.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvProduk.setHasFixedSize(true)
        (binding.rvProduk.layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.VERTICAL
        cartAdapter = TokoCartAdapter(cartList, requireActivity())
        binding.rvProduk.adapter = cartAdapter

        val cartReference = FirebaseDatabase.getInstance().reference
            .child("cart")
            .child(userId)

        val cartListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                loading(false)
                cartList.clear()
                for (tokoSnapshot in dataSnapshot.children) {
                    val tokoCart = tokoSnapshot.getValue(TokoItemModel::class.java)
                    if (tokoCart != null) {
                        // Tambahkan data toko ke list
                        cartList.add(tokoCart)
                    }
                }
                if (cartList.isEmpty()) {
                    // Jika data kosong, tampilkan teks kosong
                    binding.txtKosong.visibility = View.VISIBLE
                    binding.rvProduk.visibility = View.GONE
                } else {
                    // Jika ada data, tampilkan RecyclerView
                    binding.txtKosong.visibility = View.GONE
                    binding.rvProduk.visibility = View.VISIBLE

                    // Update adapter
                    cartAdapter.notifyDataSetChanged()
                    cartAdapter.setDialog(object : TokoCartAdapter.Dialog {
                        override fun onClick(position: Int, note: TokoItemModel) {
                            val gson = Gson()
                            val noteJson = gson.toJson(note)
                            startActivity<DetailKeranjangActivity>("detailCart" to noteJson)
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                loading(false)
                toast("Gagal mendapatkan data")
            }
        }

        cartReference.addValueEventListener(cartListener)
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