package com.go4sumbergedang.go4.utils

import androidx.appcompat.app.AppCompatActivity
import com.go4sumbergedang.go4.model.CartModel
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot

object CartUtils {
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val valueEventListenerMap: MutableMap<String, ValueEventListener> = mutableMapOf()
    private var countDataListener: CountDataListener? = null

    interface CountDataListener {
        fun onCountUpdated(count: Long)
        fun onError(error: DatabaseError)
    }

    fun startCountDataListener(userId: String) {
        val cartReference = firebaseDatabase.reference.child("cart")
        val userIdReference = cartReference.child(userId)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val count = dataSnapshot.childrenCount
                countDataListener?.onCountUpdated(count)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                countDataListener?.onError(databaseError)
            }
        }

        userIdReference.addValueEventListener(valueEventListener)

        // Menyimpan valueEventListener dalam map menggunakan userId sebagai kunci
        valueEventListenerMap[userId] = valueEventListener
    }

    fun setCountDataListener(listener: CountDataListener) {
        countDataListener = listener
    }

    fun stopCountDataListener(userId: String) {
        val cartReference = firebaseDatabase.reference.child("cart")
        val userIdReference = cartReference.child(userId)

        // Mendapatkan valueEventListener dari map menggunakan userId sebagai kunci
        val valueEventListener = valueEventListenerMap[userId]

        if (valueEventListener != null) {
            userIdReference.removeEventListener(valueEventListener)
            // Menghapus valueEventListener dari map setelah dihapus dari referensi
            valueEventListenerMap.remove(userId)
        }
    }

    fun addToCart(
        newData: CartModel,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val cartReference = firebaseDatabase.reference.child("cart")
        val userIdReference = cartReference.child(userId).child(newData.idProduk.toString())

        userIdReference.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val existingData = currentData.getValue(CartModel::class.java)
                if (existingData != null) {
                    val currentJumlah = existingData.jumlah ?: 0
                    val newJumlah = currentJumlah + newData.jumlah!!
                    newData.jumlah = newJumlah
                }
                currentData.value = newData
                return Transaction.success(currentData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (databaseError == null && committed) {
                    onSuccess()
                } else {
                    onFailure()
                }
            }
        })
    }

    fun getCurrentCount(userId: String): Long {
        val cartReference = firebaseDatabase.reference.child("cart")
        val userIdReference = cartReference.child(userId)

        var count = 0L

        userIdReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                count = dataSnapshot.childrenCount
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Tangani kesalahan jika ada
            }
        })

        return count
    }
}
