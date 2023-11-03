package com.go4sumbergedang.go4.ui.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.FragmentProfilBinding
import com.go4sumbergedang.go4.session.SessionManager
import com.go4sumbergedang.go4.ui.activity.DetailAkunActivity
import com.go4sumbergedang.go4.ui.activity.LoginActivity
import com.go4sumbergedang.go4.ui.activity.UpdatePasswordActivity
import com.squareup.picasso.Picasso
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

class ProfilFragment : Fragment(), AnkoLogger {
    private lateinit var binding: FragmentProfilBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var sessionManager: SessionManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profil, container, false)
        binding.lifecycleOwner = this
        progressDialog = ProgressDialog(requireActivity())
        sessionManager = SessionManager(requireActivity())
        val urlImage = requireActivity().getString(R.string.urlImage)
        val foto= sessionManager.getFoto()
        var def = "/public/images/no_image.png"
        if (foto != "") {
            Picasso.get()
                .load(urlImage+foto)
                .into(binding.foto)
        }else{
            Picasso.get()
                .load(urlImage+def)
                .into(binding.foto)
        }
        binding.tvNamaUser.text = sessionManager.getNama().toString()
        binding.lySetAkun.setOnClickListener {
            startActivity<DetailAkunActivity>()
        }
        binding.lySetPwd.setOnClickListener {
            startActivity<UpdatePasswordActivity>()
        }
        binding.txtLogout.setOnClickListener {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setMessage("Logout ? ")
            builder.setPositiveButton("Ok") { dialog, which ->
                sessionManager.clearSession()
                startActivity<LoginActivity>()
                toast("Berhasil Logout")
                requireActivity().finish()
            }
            builder.setNegativeButton("Batal") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }
        return binding.root
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

    override fun onStart() {
        super.onStart()
        val urlImage = requireActivity().getString(R.string.urlImage)
        val foto= sessionManager.getFoto()
        var def = "/public/images/no_image.png"
        if (foto != "") {
            Picasso.get()
                .load(urlImage+foto)
                .into(binding.foto)
        }else{
            Picasso.get()
                .load(urlImage+def)
                .into(binding.foto)
        }
        binding.tvNamaUser.text = sessionManager.getNama().toString()
    }
}