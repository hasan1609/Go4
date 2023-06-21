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
        return binding.root
    }
}