package ua.olehkv.coursework.dialogs

import android.app.AlertDialog
import android.content.Context
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import ua.olehkv.coursework.MainActivity
import ua.olehkv.coursework.R
import ua.olehkv.coursework.databinding.SignDialogBinding
import ua.olehkv.coursework.firebase.AccountHelper

class DialogAuthHelper(private val activity: MainActivity) {
    val accHelper = AccountHelper(activity)
    fun showSignDialog(index: Int){
        val builder = AlertDialog.Builder(activity)

        val binding = SignDialogBinding.inflate(activity.layoutInflater)
        val dialog = builder.setView(binding.root)
            .create()

        if (index == DialogConstants.SIGN_UP_STATE){
            binding.tvSignTitle.text = activity.resources.getString(R.string.ac_sign_up)
            binding.btSignUpIn.text = activity.resources.getString(R.string.ac_sign_up)
            binding.btSignUpIn.setOnClickListener {
                val email = binding.edSignEmail.text.toString()
                val password = binding.edPassword.text.toString()
                if (email.isNotBlank() && password.isNotBlank()) {
                    accHelper.signUpWithEmail(email, password)
                }
                else Toast.makeText(activity, "Input email and password correctly", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        else {
            binding.tvSignTitle.text = activity.resources.getString(R.string.ac_sign_in)
            binding.btSignUpIn.text = activity.resources.getString(R.string.ac_sign_in)
            binding.btSignUpIn.setOnClickListener {
                val email = binding.edSignEmail.text.toString()
                val password = binding.edPassword.text.toString()
                if (email.isNotBlank() && password.isNotBlank()) {
                    accHelper.signInWithEmail(email, password)
                }
                else Toast.makeText(activity, "Input email and password correctly", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            val mSpannableString = SpannableString("Forget password?")
            mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)
            binding.btForgetPassword.text = mSpannableString
            binding.btForgetPassword.visibility = View.VISIBLE
            binding.btForgetPassword.setOnClickListener {
                if (binding.edSignEmail.text.isNotBlank()) {
                    activity.mAuth.sendPasswordResetEmail(binding.edSignEmail.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(activity, "Reset info was sent to your email", Toast.LENGTH_SHORT).show()
                            } else Toast.makeText(activity,"Reset info was not sent to your email", Toast.LENGTH_SHORT).show()
                        }
                }
                else {
                    binding.tvResetPassword.visibility = View.VISIBLE
                    binding.edSignEmail.requestFocus()

                    (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
                }
            }
        }


        binding.btSignInWithGoogle.setOnClickListener {
            accHelper.signInWithGoogle()
            dialog.dismiss()

        }

        dialog.show()
    }



}