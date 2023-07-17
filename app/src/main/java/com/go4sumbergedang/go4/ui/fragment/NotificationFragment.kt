package com.go4sumbergedang.go4.ui.fragment

import android.app.ProgressDialog
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.adapter.TokoCartAdapter
import com.go4sumbergedang.go4.databinding.FragmentNotificationBinding
import com.go4sumbergedang.go4.model.TokoItemModel
import com.go4sumbergedang.go4.ui.activity.DetailKeranjangActivity
import com.google.firebase.database.*
import com.google.gson.Gson
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast


class NotificationFragment : Fragment(), AnkoLogger {
    private lateinit var binding: FragmentNotificationBinding
    private lateinit var cartAdapter: TokoCartAdapter
    private val cartList: MutableList<TokoItemModel> = mutableListOf()
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

}