package com.go4sumbergedang.go4.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.ActivityRouteOrderBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class RouteOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRouteOrderBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route_order)
        binding.lifecycleOwner = this
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetLayout)

        // Atur callback untuk mengelola perubahan state
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Di sini Anda dapat menangani perubahan slideOffset
                // untuk mengatur perilaku bottom sheet saat digeser.
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Tangani perubahan state
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        // State setengah
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // State penuh
                    }
                    // Lainnya sesuai kebutuhan Anda
                }
            }
        })

        // Set initial state (setengah atau penuh)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }
}