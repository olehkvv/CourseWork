package ua.olehkv.coursework.firebase

import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import ua.olehkv.coursework.MainActivity
import ua.olehkv.coursework.R

class AccountHelper(private val activity: MainActivity) {
    private lateinit var signInClient: GoogleSignInClient

    fun signUpWithEmail(email: String, password: String) {
        if (activity.mAuth.currentUser?.isAnonymous == true)
            activity.mAuth.currentUser?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        activity.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task->
                            if (task.isSuccessful){
                                signUpWithEmailSuccessful(task.result.user!!)
                            }
                            else{
                                signUpWithEmailException(task, email, password)
                            }
                        }
                    }
                }
    }

    private fun signUpWithEmailException(task: Task<AuthResult>, email: String, password: String) {
        Toast.makeText(activity, "Sign up error", Toast.LENGTH_SHORT).show()
        Log.d("AAA", "signUpWithEmail error: ${task.exception}")
        if (task.exception is FirebaseAuthUserCollisionException) {
            val ex = task.exception as FirebaseAuthUserCollisionException
            //Log.d("AAA", "Exception: ${ex.errorCode}")
            if (ex.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                Toast.makeText(
                    activity,
                    FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE, Toast.LENGTH_SHORT
                ).show()
                linkEmailToGoogleAccount(email, password)
            }
        } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
            val ex = task.exception as FirebaseAuthInvalidCredentialsException
            if (ex.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                Toast.makeText(
                    activity,
                    FirebaseAuthConstants.ERROR_INVALID_EMAIL,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        if (task.exception is FirebaseAuthWeakPasswordException) {
            val ex = task.exception as FirebaseAuthWeakPasswordException
            if (ex.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {
                Toast.makeText(
                    activity,
                    FirebaseAuthConstants.ERROR_WEAK_PASSWORD,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun signUpWithEmailSuccessful(user: FirebaseUser) {
        Toast.makeText(activity, "Sign Up successfully as ${user.email}", Toast.LENGTH_SHORT).show()
        sendEmailVerification(user)
        activity.uiUpdate(user)
    }

    fun signInWithEmail(email: String, password: String) {
        if (activity.mAuth.currentUser?.isAnonymous == true)
            activity.mAuth.currentUser?.delete()
                ?.addOnCompleteListener {
                activity.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task->
                    if (task.isSuccessful){
                        Toast.makeText(activity, "Sign In successfully as ${task.result.user?.email}", Toast.LENGTH_SHORT).show()
                        activity.uiUpdate(task.result.user)
                    }
                    else{
                        signInWithEmailException(task)
                    }
                }
            }

    }

    private fun signInWithEmailException(task: Task<AuthResult>) {
        Log.d("AAA", "signInWithEmail error: ${task.exception}")
        if (task.exception is FirebaseAuthInvalidCredentialsException) {
            val ex = task.exception as FirebaseAuthInvalidCredentialsException
            Log.d("AAA", "signInWithEmail errorCode: ${ex.errorCode}")
            if (ex.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                Toast.makeText(
                    activity,
                    FirebaseAuthConstants.ERROR_INVALID_EMAIL,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (ex.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                Toast.makeText(
                    activity,
                    FirebaseAuthConstants.ERROR_WRONG_PASSWORD,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (ex.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                Toast.makeText(
                    activity,
                    FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE,
                    Toast.LENGTH_SHORT
                ).show()
            } else Toast.makeText(activity, ex.errorCode, Toast.LENGTH_SHORT).show()
        }
        if (task.exception is FirebaseAuthInvalidUserException) {
            val ex = task.exception as FirebaseAuthInvalidUserException
            Toast.makeText(activity, ex.errorCode, Toast.LENGTH_SHORT).show()
        }
        //                Toast.makeText(activity, "Sign in error", Toast.LENGTH_SHORT).show()
    }

    private fun linkEmailToGoogleAccount(email: String, password: String){
        val credential = EmailAuthProvider.getCredential(email, password)
        if (activity.mAuth.currentUser != null) {
            activity.mAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener { task ->
                    Log.d("AAA", "linkEmailToGoogleAccount ")
                    if (task.isSuccessful) {
                        Toast.makeText(activity, "Email was linked to Google account", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        else{
            Toast.makeText(activity, "The account with this email already exists. Sign in your google account with this email. Then sign up with this email again", Toast.LENGTH_LONG).show()
        }

    }


    fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activity, gso)
    }

    fun signInWithGoogle(){
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        activity.googleSignInLauncher.launch(intent)
    }

    fun signOutWithGoogle(){
        getSignInClient().signOut()
    }

    fun signInFirebaseWithGoogle(token: String){
        val credential = GoogleAuthProvider.getCredential(token, null)
        activity.mAuth.currentUser?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    activity.mAuth.signInWithCredential(credential).addOnCompleteListener {task->
                        if (task.isSuccessful){
                            Toast.makeText(activity, "Sign in with google is successful", Toast.LENGTH_SHORT).show()
                            activity.uiUpdate(task.result?.user)
                        }
                        else{
                            Log.d("AAA", "signInFirebaseWithGoogle error: ${task.exception}")
                        }
                    }
                }
            }

    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener {task ->
            if (task.isSuccessful){
                Toast.makeText(activity, "Verification info sent to your email address", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(activity, "Can't send email info for verification", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun signInAnonymously(listener: Listener){
        activity.mAuth.signInAnonymously()
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    listener.onComplete()
                    Toast.makeText(activity, "You entered as anonymous", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Can not log in as anonymous", Toast.LENGTH_SHORT).show()
                }
            }
    }

    interface Listener{
        fun onComplete()
    }

    companion object {
        const val SIGN_IN_REQUEST_CODE = 445
    }
}