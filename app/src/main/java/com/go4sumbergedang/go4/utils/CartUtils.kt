package com.go4sumbergedang.go4.utils

import com.google.firebase.database.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot

object CartUtils {
    private lateinit var cartReference: DatabaseReference
    private lateinit var cartListener: ValueEventListener
    private var countDataListener: CountDataListener? = null

    interface CountDataListener {
        fun onCountUpdated(count: Long)
        fun onError(error: DatabaseError)
    }

    fun startCountDataListener(userId: String, listener: CountDataListener) {
        val cartReference = FirebaseDatabase.getInstance().reference.child("cart")
            .child(userId)

        cartListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val count = dataSnapshot.childrenCount
                listener.onCountUpdated(count)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                listener.onError(databaseError)
            }
        }

        cartReference.addValueEventListener(cartListener)
        countDataListener = listener
    }

    fun stopCountDataListener() {
        if (::cartReference.isInitialized && ::cartListener.isInitialized) {
            cartReference.removeEventListener(cartListener)
            countDataListener = null
        }
    }
}
