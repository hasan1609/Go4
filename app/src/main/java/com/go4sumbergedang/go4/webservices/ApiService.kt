package com.go4sumbergedang.go4.webservices

import com.go4sumbergedang.go4.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // TODO: AUTH
    // register
    @FormUrlEncoded
    @POST("customer")
    fun register(
        @Field("nama") nama: String,
        @Field("tlp") tlp: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<ResponseLogin>

    @FormUrlEncoded
    @POST("login/customer")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("fcm") fcm: String
    ): Call<ResponseLogin>
    // get detail
    @GET("customer/{id}")
    fun getDetailCustomer(
        @Path("id") id: String
    ): Call<ResponseDetailCustomer>
    @FormUrlEncoded
    // update password
    @POST("update-password")
    fun updatePassword(
        @Field("id") id: String,
        @Field("current_password") current_password: String,
        @Field("password") password: String,
    ): Call<ResponsePostData>

    @Multipart
    @POST("customer/{id}")
    fun updateCustomerWithFoto(
        @Path("id") id: String,
        @Part("nama") nama: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part foto: MultipartBody.Part?
    ): Call<ResponseUpdateProfil>

    @FormUrlEncoded
    @POST("customer/{id}")
    fun updateCustomerNofoto(
        @Path("id") id: String,
        @Field("nama") nama: String,
        @Field("alamat") alamat: String,
        ): Call<ResponseUpdateProfil>

    // TODO: RESTO 
// cari resto terderkat
    @GET("nearby/resto/{latitude}&{longitude}")
    fun getRestoTerdekat(
        @Path("latitude") latitude: String,
        @Path("longitude") longitude: String
    ): Call<ResponseResto>
// get data resto by id
    @GET("resto/{id}")
    fun getRestoById(
        @Path("id") id: String,
    ): Call<ResponseRestoSingle>
// get produk by id resto
    @GET("resto/produk/{id}/{lat}&{long}")
    fun getProdukByIdResto(
        @Path("id") id: String,
        @Path("lat") lat: String,
        @Path("long") long: String,
    ): Call<ResponseResto>

    // TODO: CART 
//    add to cart
    @FormUrlEncoded
    @POST("cart")
    fun addToCart(
        @Field("user_id") user_id: String,
        @Field("produk_id") produk_id: String,
        @Field("toko_id") toko_id: String,
        @Field("jumlah") jumlah: String,
        @Field("catatan") catatan: String,
        @Field("harga") harga: String,
    ): Call<ResponsePostData>
//    get cart Count
    @GET("cart/count/{id}")
    fun getCountCart(
        @Path("id") id: String,
    ): Call<ResponseCountCart>
//    get cart
    @GET("cart/{id}/{lat}&{long}")
    fun getCart(
        @Path("id") id: String,
        @Path("lat") lat: String,
        @Path("long") long: String,
    ): Call<ResponseCart>
//    get item cart
    @GET("cart/item/{id}/{user}/{lat}/{long}")
    fun getItemCart(
        @Path("id") id: String,
        @Path("user") user: String,
        @Path("lat") lat: String,
        @Path("long") long: String
    ): Call<ResponseItemCart>
//    hapus cart by id resto
    @POST("cart/{id}/{user}")
    fun hapusCart(
        @Path("id") id: String,
        @Path("user") user: String
    ): Call<ResponsePostData>
//    hapus cart by id cart
    @POST("item/cart/{id}")
    fun hapusByIdCart(
        @Path("id") id: String
    ): Call<ResponsePostData>

    // TODO: ORDER
    // BOOKING RESTO
    @FormUrlEncoded
    @POST("booking")
    fun addBooking(
        @Field("id_order") id_order: String,
        @Field("id") id: String,
        @Field("alamat_tujuan") alamat_tujuan: String,
        @Field("latitude_tujuan") latitude_tujuan: String,
        @Field("longitude_tujuan") longitude_tujuan: String,
        @Field("longitude_dari") longitude_dari: String,
        @Field("latitude_dari") latitude_dari: String,
        @Field("alamat_dari") alamat_dari: String,
        @Field("kategori") kategori: String,
        @Field("ongkir") ongkir: String,
        @Field("user_id") user_id: String
    ): Call<ResponseSearchDriver>
    // BOOKING DRIVER
    @FormUrlEncoded
    @POST("booking")
    fun addBookingDriver(
        @Field("id_order") id_order: String,
        @Field("alamat_tujuan") alamat_tujuan: String,
        @Field("latitude_tujuan") latitude_tujuan: String,
        @Field("longitude_tujuan") longitude_tujuan: String,
        @Field("longitude_dari") longitude_dari: String,
        @Field("latitude_dari") latitude_dari: String,
        @Field("alamat_dari") alamat_dari: String,
        @Field("kategori") kategori: String,
        @Field("ongkir") ongkir: String,
        @Field("user_id") user_id: String
    ): Call<ResponseSearchDriver>

    @FormUrlEncoded
    @POST("order/manual")
    fun addBookingDriverManual(
        @Field("id_order") id_order: String,
        @Field("alamat_tujuan") alamat_tujuan: String,
        @Field("latitude_tujuan") latitude_tujuan: String,
        @Field("longitude_tujuan") longitude_tujuan: String,
        @Field("longitude_dari") longitude_dari: String,
        @Field("latitude_dari") latitude_dari: String,
        @Field("alamat_dari") alamat_dari: String,
        @Field("latitude_driver") latitude_driver: String,
        @Field("longitude_driver") longitude_driver: String,
        @Field("kategori") kategori: String,
        @Field("ongkir") ongkir: String,
        @Field("user_id") user_id: String,
        @Field("driver_id") driver_id: String
    ): Call<ResponseSearchDriver>
    // GET ORDER LOG
    @GET("order/customer/{id}")
    fun getOrderLog(
        @Path("id") id: String
    ): Call<ResponseOrderLog>
    // GET ORDER LOG BY ID ORDER
    @GET("order/detail/{id}")
    fun getDetailOrderLog(
        @Path("id") id: String
    ): Call<ResponseDetailLogOrder>
    // GET DRIVER MANUAL
    @GET("driver/manual/{latitude}&{longitude}")
    fun getDriverManual(
        @Path("latitude") latitude: String,
        @Path("longitude") longitude: String
    ): Call<ResponseDriverManual>

    // TODO: NOTIFIKASI 
    @GET("notifikasi/{id}")
    fun getNotifikasiLog(
        @Path("id") id: String
    ): Call<ResponseNotifikasiLog>

    @POST("notifikasi/{id}")
    fun updateNotifikasiStatusLog(
        @Path("id") id: String
    ): Call<ResponsePostData>

    // TODO: ONGKIR
    @FormUrlEncoded
    @POST("ongkir")
    fun getOngkir(
        @Field("lat1") lat1: String,
        @Field("long1") long1: String,
        @Field("lat2") lat2: String,
        @Field("long2") long2: String,
    ): Call<ResponseOngkir>

    // TODO: GETROUTE

}