package com.go4sumbergedang.go4.ui.fragment

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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.toast

class KeranjangFragment : Fragment(), AnkoLogger {
    private lateinit var binding: FragmentKeranjangBinding
    private lateinit var cartAdapter: TokoCartAdapter
    private val cartList: MutableList<TokoItemModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater ,R.layout.fragment_keranjang, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    fun getData(){
        binding.rvProduk.layoutManager = LinearLayoutManager(requireActivity())
        cartAdapter = TokoCartAdapter(cartList, requireActivity())
        binding.rvProduk.adapter = cartAdapter

        val userId = "id_user"
        val cartReference = FirebaseDatabase.getInstance().reference
            .child("cart")
            .child(userId)

        val cartListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                cartList.clear()
                for (tokoSnapshot in dataSnapshot.children) {
                    val tokoCart = tokoSnapshot.getValue(TokoItemModel::class.java)
                    if (tokoCart != null) {
                        // Tambahkan data toko ke list
                        cartList.add(tokoCart)
                    }
                }
                cartAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                toast("Gagal mendapatkan data")
            }
        }

        cartReference.addValueEventListener(cartListener)
    }

    override fun onStart() {
        super.onStart()
        getData()
    }
}