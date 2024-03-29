package com.go4sumbergedang.go4.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(private val context: Context?) {
    val privateMode = 0
    val privateName = "login"
    var Pref: SharedPreferences? = context?.getSharedPreferences(privateName, privateMode)
    var editor: SharedPreferences.Editor? = Pref?.edit()

    private val islogin = "login"
    fun setLogin(check: Boolean){
        editor?.putBoolean(islogin,check)
        editor?.commit()
    }

    fun getLogin():Boolean?
    {
        return Pref?.getBoolean(islogin,false)
    }

    private val isToken = "isToken"
    fun setToken(check: String){
        editor?.putString(isToken,check)
        editor?.commit()
    }

    fun getToken():String?
    {
        return Pref?.getString(isToken,"")
    }

    private val isId = "isId"
    fun setId(check: String){
        editor?.putString(isId,check)
        editor?.commit()
    }

    fun getId():String?
    {
        return Pref?.getString(isId,"")
    }

    private val isNama = "isNama"
    fun setNama(check: String){
        editor?.putString(isNama,check)
        editor?.commit()
    }

    fun getNama():String?
    {
        return Pref?.getString(isNama,"")
    }

    private val isFoto = "isFoto"
    fun setFoto(check: String){
        editor?.putString(isFoto,check)
        editor?.commit()
    }

    fun getFoto():String?
    {
        return Pref?.getString(isFoto,"")
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

    private val isCekRouteResto = "isCekRouteResto"
    fun setCekRouteResto(check: String?) {
        editor?.putString(isCekRouteResto, check)
        editor?.commit()
    }

    fun getCekRouteResto(): String? {
        return Pref?.getString(isCekRouteResto, "")
    }

    private val isRoutesResto = "isRoutesResto"
    fun setRoutesResto(check: String?) {
        editor?.putString(isRoutesResto, check)
        editor?.commit()
    }

    fun getRoutesResto(): String? {
        return Pref?.getString(isRoutesResto, "")
    }

    private val isCekRoutesTransport = "isCekRouteTransport"
    fun setCekRouteTransport(check: String?) {
        editor?.putString(isCekRoutesTransport, check)
        editor?.commit()
    }

    fun getCekRouteTransport(): String? {
        return Pref?.getString(isCekRoutesTransport, "")
    }

    private val isRoutesTranport = "isRoutesTranport"
    fun setRoutesTranport(check: String?) {
        editor?.putString(isRoutesTranport, check)
        editor?.commit()
    }

    fun getRoutesTranport(): String? {
        return Pref?.getString(isRoutesTranport, "")
    }

    private val isOrderResto = "isOrderResto"
    fun setisOrderResto(check: Boolean){
        editor?.putBoolean(isOrderResto,check)
        editor?.commit()
    }

    fun getisOrderResto():Boolean?
    {
        return Pref?.getBoolean(isOrderResto,false)
    }

    private val isOrderTransport = "isOrderTransport"
    fun setisOrderTransport(check: Boolean){
        editor?.putBoolean(isOrderTransport,check)
        editor?.commit()
    }

    fun getisOrderTransport():Boolean?
    {
        return Pref?.getBoolean(isOrderTransport,false)
    }

    fun clearSession() {
        val editor = Pref?.edit()
        editor?.clear()
        editor?.apply()
    }
}