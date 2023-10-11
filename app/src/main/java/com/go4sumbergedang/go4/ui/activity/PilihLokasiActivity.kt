package com.go4sumbergedang.go4.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.ActivityPilihLokasiBinding
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity

class PilihLokasiActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var binding: ActivityPilihLokasiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pilih_lokasi)
        binding.back.setOnClickListener {
            onBackPressed()
        }
        binding.btnMaps.setOnClickListener {
            startActivity<ActivityMaps>()
        }
    }
}