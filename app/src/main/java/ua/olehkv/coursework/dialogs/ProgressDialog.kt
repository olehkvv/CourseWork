package ua.olehkv.coursework.dialogs

import android.app.Activity
import android.app.AlertDialog
import ua.olehkv.coursework.databinding.ProgressDialogLayoutBinding

object ProgressDialog {
    fun showDialog(act: Activity): AlertDialog{
        val builder = AlertDialog.Builder(act)
        val binding = ProgressDialogLayoutBinding.inflate(act.layoutInflater)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }
}