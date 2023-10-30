package com.go4sumbergedang.go4.ui.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager.TAG
import androidx.recyclerview.widget.LinearLayoutManager
import com.go4sumbergedang.go4.R
import com.go4sumbergedang.go4.adapter.ChatAdapter
import com.go4sumbergedang.go4.model.ChatModel
import com.go4sumbergedang.go4.model.OrderLogModel
import com.go4sumbergedang.go4.ui.activity.ChatActivity.Companion.TAG
import com.go4sumbergedang.go4.webservices.ApiService
import com.google.android.gms.auth.api.phone.SmsRetriever.getClient
import com.google.android.gms.common.api.Api
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.auth.Token
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    companion object {
        internal val TAG = ChatActivity::class.java.simpleName
        //image pick code
        private const val IMAGE_PICK_CODE = 1000
    }

    private var driverId: String? = null
    private var mUser: String? = null
    private lateinit var reference: DatabaseReference
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatList: List<ChatModel>
    var order: OrderLogModel? = null

    private var notify = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val gson = Gson()
        order = gson.fromJson(intent.getStringExtra("order"), OrderLogModel::class.java)
        driverId = order!!.driverId
        mUser = order!!.customerId
        with(rv_chat) {
            setHasFixedSize(true)
            val linearLayoutManager = LinearLayoutManager(this@ChatActivity)
            linearLayoutManager.stackFromEnd = true
            layoutManager = linearLayoutManager
        }
        reference = FirebaseDatabase.getInstance().reference.child("users").child(driverId!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, p0.message)
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                retrieveMessage(mUser, driverId.toString())
            }
        })

        img_send_message.setOnClickListener {
            val message = edt_text_message.text.toString()
            notify = true
            if (TextUtils.isEmpty(message)) {
                Toast.makeText(
                    this,
                    "Please write message, first ...",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                sendMessageToUser(mUser, driverId, message)
            }
            edt_text_message.setText("")
        }
        img_attach_image_file.setOnClickListener {
            notify = true
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), IMAGE_PICK_CODE)
        }
        seenMessage(driverId!!)
    }
    private fun retrieveMessage(senderId: String?, receiverId: String) {
        chatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("Chats")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, p0.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (chatList as ArrayList<ChatModel>).clear()
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(ChatModel::class.java)
                    if (chat?.receiver.equals(senderId) && chat?.sender.equals(receiverId)
                        || chat?.receiver.equals(receiverId) && chat?.sender.equals(senderId)
                    ) {
                        (chatList as ArrayList<ChatModel>).add(chat!!)
                    }
                    chatAdapter = ChatAdapter(order!!)
                    chatAdapter.setData(chatList as ArrayList<ChatModel>)
                    rv_chat.adapter = chatAdapter
                }
            }

        })
    }
    private fun sendMessageToUser(
        senderId: String?,
        receiveId: String?,
        message: String
    ) {
        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiveId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey
        reference.child("Chats").child(messageKey!!).setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val chatListReference =
                        FirebaseDatabase.getInstance().reference.child("ChatList")
                            .child(senderId.toString())
                            .child(driverId!!)

                    chatListReference.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Log.d(TAG, p0.message)
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (!p0.exists()) {
                                chatListReference.child("id").setValue(driverId)
                            }

                            val chatListReceiverReference =
                                FirebaseDatabase.getInstance().reference.child("ChatList")
                                    .child(driverId!!)
                                    .child(senderId.toString())
                            chatListReceiverReference.child("id").setValue(senderId)
                        }

                    })

                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val loadingBar = ProgressDialog(this)
            loadingBar.setMessage("Please wait, image is sending ...")
            loadingBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")
            filePath.putFile(fileUri!!).addOnSuccessListener { it ->
//                Log.d(RegisterActivity.TAG, "Successfully uploaded image : ${it.metadata?.path}")
                filePath.downloadUrl.addOnSuccessListener {
                    val url = it.toString()
                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = mUser
                    messageHashMap["message"] = "sent you an image."
                    messageHashMap["receiver"] = driverId
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                loadingBar.dismiss()
//                                Implement the push notification
                                val reference =
                                    FirebaseDatabase.getInstance().reference.child("users")
                                        .child(mUser.toString())

                                reference.addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                        Log.d(TAG, p0.message)
                                    }

                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val user = dataSnapshot.getValue(User::class.java)
                                        if (notify) {

                                        }
                                        notify = false
                                    }

                                })
                            }
                        }
                }
            }

        }
    }
    private var seenListener: ValueEventListener? = null
    private fun seenMessage(userId: String) {
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        seenListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, p0.message)
            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                for (snapshot in datasnapshot.children) {
                    val chat = snapshot.getValue(ChatModel::class.java)
                    if (chat?.receiver.equals(mUser) && chat?.sender.equals(userId)) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        snapshot.ref.updateChildren(hashMap)
                    }
                }

            }

        })
    }
    override fun onPause() {
        super.onPause()
        reference.removeEventListener(seenListener!!)
    }
}