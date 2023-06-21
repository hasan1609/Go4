package com.go4sumbergedang.go4.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.ActivityLoginBinding
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity

class LoginActivity : AppCompatActivity() , AnkoLogger{
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this

        binding.txtDaftar.setOnClickListener {
            startActivity<RegisterActivity>()
        }
        binding.btnlogin.setOnClickListener {
            startActivity<RootActivity>()
        }
    }
}