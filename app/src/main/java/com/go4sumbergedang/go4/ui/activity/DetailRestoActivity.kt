package com.go4sumbergedang.go4.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.ActivityDetailRestoBinding
import org.jetbrains.anko.AnkoLogger

class DetailRestoActivity : AppCompatActivity() , AnkoLogger{
    private lateinit var binding: ActivityDetailRestoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_resto)
        binding.lifecycleOwner = this
    }
}