package com.go4sumbergedang.go4.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import com.go4sumbergedang.go4.R

class LoadingDialogSearch private constructor(context: Context) {
    private var dialog: Dialog? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_loading, null)
        dialog = Dialog(context)
        dialog?.setContentView(view)
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
    }

    fun show() {
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    companion object {
        fun create(context: Context): LoadingDialogSearch {
            return LoadingDialogSearch(context)
        }
    }
}