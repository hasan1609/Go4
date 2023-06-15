package com.go4sumbergedang.go4.ui.activity

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.ui.fragment.HomeFragment
import com.go4sumbergedang.go4.ui.fragment.KeranjangFragment
import com.go4sumbergedang.go4.ui.fragment.ProfilFragment
import com.go4sumbergedang.go4.ui.fragment.TransaksiFragment

class RootActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameHome,
                        HomeFragment()
                    ).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_keranjang -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameHome,
                        KeranjangFragment()
                    ).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_transaksi -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameHome,
                        TransaksiFragment()
                    ).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profil -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameHome,
                        ProfilFragment()
                    ).commit()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        val navView: BottomNavigationView = findViewById(R.id.nav_viewhome)
//        sessionManager = SessionManager(this)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        moveToFragment(HomeFragment())
    }

    private fun moveToFragment(fragment: Fragment) {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.frameHome, fragment)
        fragmentTrans.commit()
    }
}