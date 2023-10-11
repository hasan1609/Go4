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
import com.go4sumbergedang.go4.ui.activity.KeranjangActivity
import com.go4sumbergedang.go4.ui.activity.PilihLokasiActivity
import com.go4sumbergedang.go4.utils.CartItemCountEvent
import com.go4sumbergedang.go4.utils.CartUtils
import com.google.firebase.database.DatabaseError
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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
        EventBus.getDefault().register(this)
        CartUtils.getCartItemCount("f3ece8ed-6353-4268-bdce-06ba4c6049fe")

        binding.cardMakanan.setOnClickListener {
            startActivity<DataRestoTerdekatActivity>()
        }

        binding.btnToKeranjang.setOnClickListener{
            startActivity<KeranjangActivity>()
        }

        binding.cardMobil.setOnClickListener {
            startActivity<PilihLokasiActivity>()
        }

        binding.cardMotor.setOnClickListener {
            startActivity<PilihLokasiActivity>()
        }
        return binding.root
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCartItemCountEvent(event: CartItemCountEvent) {
        val cartItemCount = event.itemCount
        // Update tampilan jumlah item keranjang di aktivitas ini
        if (cartItemCount != 0) {
            binding.divBadge.visibility = View.VISIBLE
        } else {
            binding.divBadge.visibility = View.GONE
        }
    }

    // ...

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this) // Batalkan pendaftaran saat aktivitas dihancurkan
    }
}