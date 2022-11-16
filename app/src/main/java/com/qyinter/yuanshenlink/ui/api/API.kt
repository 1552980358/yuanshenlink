package com.qyinter.yuanshenlink.ui.api

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.qyinter.yuanshenlink.R
import com.qyinter.yuanshenlink.databinding.FragmentApiBinding

class API: Fragment(), OnClickListener {

    companion object {
        private val PHONE_NUMBER_REGEX = "1[1-9][0-9]{9}".toRegex()
        private val EMAIL_NUMBER_REGEX = "([0-9a-z].)*[0-9a-z]+@[0-9a-z]{1,}.[a-z]+".toRegex()
    }

    private var _binding: FragmentApiBinding? = null
    private val binding: FragmentApiBinding
        get() = _binding!!

    private val textInputLayoutTool: TextInputLayout
        get() = binding.textInputLayoutTool
    private val editTextTool: EditText
        get() = textInputLayoutTool.editText!!
    private val verifyCode: TextInputLayout
        get() = binding.textInputLayoutVerifyCode
    private val login: MaterialButton
        get() = binding.materialButtonLogin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Fix MaterialToolbar blinking
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.root
        }
        sharedElementReturnTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.material_button_api
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentApiBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        editTextTool.doOnTextChanged { text, _, _, _ ->
            text?.let {
                when {
                    text.matches(PHONE_NUMBER_REGEX) -> updateStatus(true, R.drawable.ic_round_phone_android_24)
                    text.matches(EMAIL_NUMBER_REGEX) -> updateStatus(false, R.drawable.ic_round_email_24)
                    else -> updateStatus(false, null)
                }
            }
        }
        verifyCode.setEndIconOnClickListener(this)
        login.setOnClickListener(this)
    }

    private fun updateStatus(verifyCodeState: Boolean, resId: Int) {
        updateStatus(verifyCodeState, ResourcesCompat.getDrawable(resources, resId, null))
    }

    private fun updateStatus(verifyCodeState: Boolean, drawable: Drawable?) {
        if (verifyCode.isEnabled != verifyCodeState) {
            verifyCode.isEnabled = verifyCodeState
        }
        textInputLayoutTool.startIconDrawable = drawable
    }

    override fun onClick(v: View?) {
        when (v) {
            verifyCode -> {  }   // Implement later
            login -> {  }        // Implement later
        }
    }

}