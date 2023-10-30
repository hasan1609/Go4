package com.go4sumbergedang.go4.ui.activity

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.databinding.ActivityDetailAkunBinding
import com.go4sumbergedang.go4.model.*
import com.go4sumbergedang.go4.session.SessionManager
import com.go4sumbergedang.go4.ui.fragment.BottomSheetFilePickerFragment
import com.go4sumbergedang.go4.webservices.ApiClient
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class DetailAkunActivity : AppCompatActivity(), AnkoLogger, BottomSheetFilePickerFragment.FilePickerListener {
    private lateinit var binding: ActivityDetailAkunBinding
    private var api = ApiClient.instance()
    private lateinit var sessionManager: SessionManager
    private lateinit var progressDialog: ProgressDialog
    private var selectedImageFile: File? = null
    private val PERMISSION_REQUEST_CODE = 100
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICKER = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_akun)
        binding.lifecycleOwner = this
        progressDialog = ProgressDialog(this)
        sessionManager = SessionManager(this)

        binding.appBar.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.appBar.titleTextView.text = "Detail Profil"
        binding.appBar.btnToKeranjang.visibility = View.GONE

        getData(sessionManager.getId().toString())
        binding.addFoto.setOnClickListener {
            openImagePicker()
        }
        binding.btnSimpan.setOnClickListener {
            selectedImageFile?.let { file ->
                uploadDataWithfoto(file, sessionManager.getId().toString())
            }?: uploadData(sessionManager.getId().toString())
        }
    }

    private fun getData(id: String) {
        loading(true)
        api.getDetailCustomer(id).enqueue(object : Callback<ResponseDetailCustomer> {
            override fun onResponse(
                call: Call<ResponseDetailCustomer>,
                response: Response<ResponseDetailCustomer>
            ) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                        val data = response.body()
                        if (data!!.status == true) {
                            binding.edtNama.setText(data.data!!.nama)
                            binding.edtEmail.text = data.data.email
                            binding.edtTlp.text = data.data.tlp
                            binding.edtAlamat.setText(data.data.detailCustomer!!.alamat)
                            val urlImage = getString(R.string.urlImage)
                            val def = "/public/images/no_image.png"
                            val foto= data.data.detailCustomer.foto.toString()
                            if (foto != null) {
                                Picasso.get()
                                    .load(urlImage + foto)
                                    .into(binding.foto)
                            }else{
                                Picasso.get()
                                    .load(urlImage + def)
                                    .into(binding.foto)
                            }
                        } else {
                            toast("gagal mendapatkan respon")
                        }
                    } else {
                        loading(false)
                        toast("gagal mendapatkan response")
                    }
                } catch (e: Exception) {
                    loading(false)
                    info { "hasan ${e.message}" }
                    toast(e.message.toString())
                }
            }

            override fun onFailure(call: Call<ResponseDetailCustomer>, t: Throwable) {
                info { "hasan ${t.message}" }
                toast(t.message.toString())
            }
        })
    }

    private fun uploadDataWithfoto(file: File, idUser: String) {
        val nama = binding.edtNama.text.toString()
        val alamat = binding.edtAlamat.text.toString()

        if (nama.isEmpty()) {
            Toast.makeText(this, "Nama Wajib Diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val imagePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "foto",
            file.name,
            requestBody
        )
        val namaBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), nama.toString())
        val alamatBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), alamat)

        loading(true)
        api.updateCustomerWithFoto(
            idUser,
            namaBody,
            alamatBody,
            imagePart
        ).enqueue(object : Callback<ResponseUpdateProfil> {
            override fun onResponse(
                call: Call<ResponseUpdateProfil>,
                response: Response<ResponseUpdateProfil>
            ) {
                if (response.isSuccessful) {
                    loading(false)
                    toast("Berhasil diubah")
                    sessionManager.setNama(response.body()!!.data!!.nama.toString())
                    sessionManager.setFoto(response.body()!!.data!!.detailCustomer!!.foto.toString())
                    finish()
                } else {
                    loading(false)
                    toast("Gagal mengubah data")
                }
            }

            override fun onFailure(call: Call<ResponseUpdateProfil>, t: Throwable) {
                loading(false)
                toast("Terjadi kesalahan")
            }
        })
    }

    private fun uploadData(idUser: String) {
        val nama = binding.edtNama.text.toString()
        val alamat = binding.edtAlamat.text.toString()

        loading(true)
        api.updateCustomerNofoto(
            idUser,
            nama,
            alamat
        ).enqueue(object : Callback<ResponseUpdateProfil> {
            override fun onResponse(
                call: Call<ResponseUpdateProfil>,
                response: Response<ResponseUpdateProfil>
            ) {
                if (response.isSuccessful) {
                    loading(false)
                    toast("Berhasil diubah")
                    sessionManager.setNama(response.body()!!.data!!.nama.toString())
                    sessionManager.setFoto(response.body()!!.data!!.detailCustomer!!.foto.toString())
                    finish()
                } else {
                    loading(false)
                    toast("Gagal mengubah data")
                    info(response)
                }
            }

            override fun onFailure(call: Call<ResponseUpdateProfil>, t: Throwable) {
                loading(false)
                toast("Terjadi kesalahan")
            }
        })
    }

    private fun openImagePicker() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                PERMISSION_REQUEST_CODE
            )
        } else {
            val bottomSheetFragment = BottomSheetFilePickerFragment()
            bottomSheetFragment.show(supportFragmentManager, "filePicker")
        }
    }

    override fun onFilePickerOptionSelected(option: Int) {
        when (option) {
            BottomSheetFilePickerFragment.OPTION_CAMERA -> {
                openCamera()
            }
            BottomSheetFilePickerFragment.OPTION_GALLERY -> {
                openGallery()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICKER)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICKER -> {
                    val imageUri = data?.data
                    binding.foto.setImageURI(imageUri)
                    selectedImageFile = File(getRealPathFromURI(imageUri))
                }
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    binding.foto.setImageBitmap(imageBitmap)
                    selectedImageFile = saveImageToInternalStorage(imageBitmap)
                }
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri?): String {
        var realPath = ""
        uri?.let {
            val cursor = contentResolver.query(it, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val idx = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    realPath = it.getString(idx)
                }
            }
        }
        return realPath
    }

    private fun saveImageToInternalStorage(image: Bitmap): File {
        val fileDir = filesDir
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val file = File(fileDir, fileName)
        FileOutputStream(file).use { outputStream ->
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
        }
        return file
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