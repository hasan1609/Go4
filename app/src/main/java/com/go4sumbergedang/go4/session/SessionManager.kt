package com.go4sumbergedang.go4.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(private val context: Context?) {
    val privateMode = 0
    val privateName = "login"
    var Pref: SharedPreferences? = context?.getSharedPreferences(privateName, privateMode)
    var editor: SharedPreferences.Editor? = Pref?.edit()

    private val islogin = "login"
    fun setLogin(check: Boolean) {
        editor?.putBoolean(islogin, check)
        editor?.commit()
    }

    fun getLogin(): Boolean? {
        return Pref?.getBoolean(islogin, false)
    }

    private val isLokasiSekarang = "isLokasiSekarang"
    fun setLokasiSekarang(check: String?) {
        editor?.putString(isLokasiSekarang, check)
        editor?.commit()
    }

    fun getLokasiSekarang(): String? {
        return Pref?.getString(isLokasiSekarang, "")
    }

    private val isLatitude = "isLatitude"
    fun setLatitude(check: String?) {
        editor?.putString(isLatitude, check)
        editor?.commit()
    }

    fun getLatitude(): String? {
        return Pref?.getString(isLatitude, "")
    }

    private val isLongitude = "isLongitude"
    fun setLongitude(check: String?) {
        editor?.putString(isLongitude, check)
        editor?.commit()
    }

    fun getLongitude(): String? {
        return Pref?.getString(isLongitude, "")
    }

    private val isLokasiDari = "isLokasiDari"
    fun setLokasiDari(check: String?) {
        editor?.putString(isLokasiDari, check)
        editor?.commit()
    }

    fun getLokasiDari(): String? {
        return Pref?.getString(isLokasiDari, "")
    }

    private val isLatitudeDari = "isLatitudeDari"
    fun setLatitudeDari(check: String?) {
        editor?.putString(isLatitudeDari, check)
        editor?.commit()
    }

    fun getLatitudeDari(): String? {
        return Pref?.getString(isLatitudeDari, "")
    }

    private val isLongitudeDari = "isLongitudeDari"
    fun setLongitudeDari(check: String?) {
        editor?.putString(isLongitudeDari, check)
        editor?.commit()
    }

    fun getLongitudeDari(): String? {
        return Pref?.getString(isLongitudeDari, "")
    }

    private val isLokasiTujuan = "isLokasiTujuan"
    fun setLokasiTujuan(check: String?) {
        editor?.putString(isLokasiTujuan, check)
        editor?.commit()
    }

    fun getLokasiTujuan(): String? {
        return Pref?.getString(isLokasiTujuan, "")
    }

    private val isLatitudeTujuan = "isLatitudeTujuan"
    fun setLatitudeTujuan(check: String?) {
        editor?.putString(isLatitudeTujuan, check)
        editor?.commit()
    }

    fun getLatitudeTujuan(): String? {
        return Pref?.getString(isLatitudeTujuan, "")
    }

    private val isLongitudeTujuan = "isLongitudeTujuan"
    fun setLongitudeTujuan(check: String?) {
        editor?.putString(isLongitudeTujuan, check)
        editor?.commit()
    }

    fun getLongitudeTujuan(): String? {
        return Pref?.getString(isLongitudeTujuan, "")
    }
}