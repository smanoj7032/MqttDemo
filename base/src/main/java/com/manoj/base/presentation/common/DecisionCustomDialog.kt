package com.manoj.base.presentation.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import com.manoj.base.R

class DecisionCustomDialog(context: Context) {
    val dialog: Dialog = Dialog(context)
    var okBtn: TextView
    var messageTVDialog: TextView
    var headingDialog: TextView
    var cancelBtn: TextView
    var etMachine: EditText
    var etReference: EditText
    var etLowLimitVolt: EditText
    var etUpLimitVolt: EditText
    var etLowLimitCurrent: EditText
    var etUpLimitCurrent: EditText

    init {
        dialog.setContentView(R.layout.dialog_add)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width: Int = WindowManager.LayoutParams.MATCH_PARENT
        val height: Int = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.setLayout(width, height)
        dialog.window?.setGravity(Gravity.CENTER)
        okBtn = dialog.findViewById(R.id.btn_ok)
        cancelBtn = dialog.findViewById(R.id.btn_cancel)
        etMachine = dialog.findViewById(R.id.et_machine)
        etReference = dialog.findViewById(R.id.et_reference)
        etLowLimitVolt = dialog.findViewById(R.id.et_lower_limit_volt)
        etUpLimitVolt = dialog.findViewById(R.id.et_up_limit_volt)
        etLowLimitCurrent = dialog.findViewById(R.id.et_low_limit_current)
        etUpLimitCurrent = dialog.findViewById(R.id.et_up_limit_current)
        messageTVDialog = dialog.findViewById(R.id.tv_message)
        headingDialog = dialog.findViewById(R.id.tv_heading)
    }
}

