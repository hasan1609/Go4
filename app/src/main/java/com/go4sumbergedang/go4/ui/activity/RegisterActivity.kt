package com.go4sumbergedang.go4.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.ActivityRegisterBinding
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity

class RegisterActivity : AppCompatActivity() , AnkoLogger{
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.lifecycleOwner = this

        binding.btnRegister.setOnClickListener {
            startActivity<RootActivity>()
        }

        binding.txtLogin.setOnClickListener {
            startActivity<LoginActivity>()
        }

    }
}