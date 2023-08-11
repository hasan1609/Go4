package com.go4sumbergedang.go4.ui.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.adapter.TokoCartAdapter
import com.go4sumbergedang.go4.databinding.FragmentNotificationBinding
import com.google.firebase.database.*
import org.jetbrains.anko.AnkoLogger


class NotificationFragment : Fragment(), AnkoLogger {
    private lateinit var binding: FragmentNotificationBinding
    private lateinit var cartAdapter: TokoCartAdapter
    private lateinit var progressDialog: ProgressDialog
    private lateinit var cartListener: ValueEventListener
    var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false)
        binding.lifecycleOwner = this
        progressDialog = ProgressDialog(requireActivity())
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
}