package com.qyinter.yuanshenlink.ui.webview

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.qyinter.yuanshenlink.util.UserService

class SelectAccountDialog(private val userServiceList: List<UserService>,
                          block: SelectAccountDialog.() -> Unit): DialogFragment() {
    
    private var positive: ((UserService) -> Unit)? = null
    private var negative: (() -> Unit)? = null
    
    init {
        block.invoke(this)
    }
    
    fun onPositive(positive: (UserService) -> Unit) {
        this.positive = positive
    }
    
    fun onNegative(negative: () -> Unit) {
        this.negative = negative
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setSingleChoiceItems(
                userServiceList.map { it.gameUid }.toTypedArray(),
                0,
                null
            )
            .setPositiveButton(android.R.string.ok) { _, which -> positive?.invoke(userServiceList[which]) }
            .setNegativeButton(android.R.string.cancel) { _, _ -> negative?.invoke() }
            .create()
    }

}