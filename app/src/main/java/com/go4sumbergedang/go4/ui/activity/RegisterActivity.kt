package com.go4sumbergedang.go4.ui.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.ActivityRegisterBinding
import com.go4sumbergedang.go4.model.ResponseLogin
import com.go4sumbergedang.go4.session.SessionManager
import com.go4sumbergedang.go4.webservices.ApiClient
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() , AnkoLogger{
    private lateinit var binding: ActivityRegisterBinding
    var api = ApiClient.instance()
    lateinit var progressDialog: ProgressDialog
    lateinit var sessionManager: SessionManager
    var token : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.lifecycleOwner = this

        progressDialog = ProgressDialog(this)
        sessionManager = SessionManager(this)
        binding.btnRegister.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            val nama = binding.edtNama.text.toString().trim()
            val tlp = binding.edtTlp.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()){
                register(email,password, nama, tlp)
            }else{
                toast("jangan kosongi kolom")
            }
        }

        binding.txtLogin.setOnClickListener {
            startActivity<LoginActivity>()
            finish()
        }

    }

        private fun register(email: String, password: String, nama: String, tlp: String) {
        loading(true)
        api.register(nama,tlp,email,password).enqueue(object : Callback<ResponseLogin> {
            override fun onResponse(
                call: Call<ResponseLogin>,
                response: Response<ResponseLogin>
            ) {
                try {
                    if (response.isSuccessful){
                        if (response.body()!!.status == true) {
                            loading(false)
                            toast("Berhasil, Silahkan Login")
                            startActivity<LoginActivity>()
                            finish()
                        } else {
                            loading(false)
                            toast("Email atau password salah")
                        }

                    }else{
                        loading(false)
                        info(response)
                        toast("Kesalahan Jaringan")
                    }
                }catch (e : Exception){
                    loading(false)
                    info { "hasan ${e.message }${response.code()} " }
                    toast("Kesalahan aplikasi")
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                loading(false)
                info { "erro ${t.message } " }
                toast("Kesalahan Jaringan")

            }

        })
    }

    fun loading(status : Boolean){
        if (status){
            progressDialog.setTitle("Loading...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
        }else{
            progressDialog.dismiss()
        }
    }
}