package com.go4sumbergedang.go4.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.go4sumbergedang.go4.databinding.BottomSheetFilePickBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFilePickerFragment : BottomSheetDialogFragment() {

    private var listener: FilePickerListener? = null
    private var _binding: BottomSheetFilePickBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFilePickBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.iconKamera.setOnClickListener {
            listener?.onFilePickerOptionSelected(OPTION_CAMERA)
            dismiss()
        }
        binding.iconFile.setOnClickListener {
            listener?.onFilePickerOptionSelected(OPTION_GALLERY)
            dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FilePickerListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement FilePickerListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface FilePickerListener {
        fun onFilePickerOptionSelected(option: Int)
    }

    companion object {
        const val OPTION_CAMERA = 0
        const val OPTION_GALLERY = 1
    }
}