package com.go4sumbergedang.go4.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.FragmentHomeBinding
import com.go4sumbergedang.go4.ui.activity.DataRestoTerdekatActivity
import com.go4sumbergedang.go4.ui.activity.KeranjangActivity
import com.go4sumbergedang.go4.utils.CartUtils
import com.google.firebase.database.DatabaseError
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.startActivity


class HomeFragment : Fragment(), AnkoLogger {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater ,R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this

        binding.cardMakanan.setOnClickListener {
            startActivity<DataRestoTerdekatActivity>()
        }

        binding.btnToKeranjang.setOnClickListener{
            startActivity<KeranjangActivity>()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val countDataListener = object : CartUtils.CountDataListener {
            override fun onCountUpdated(count: Long) {
                if (count > 0){
                    binding.divBadge.visibility = View.VISIBLE
                }else{
                    binding.divBadge.visibility = View.GONE
                }
            }

            override fun onError(error: DatabaseError) {
                // Tangani kesalahan jika terjadi
            }
        }
        CartUtils.startCountDataListener("id_user", countDataListener)
    }
}