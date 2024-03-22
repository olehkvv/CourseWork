package ua.olehkv.coursework

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import ua.olehkv.coursework.databinding.ActivityMainBinding
import ua.olehkv.coursework.dialogs.DialogConstants
import ua.olehkv.coursework.dialogs.DialogHelper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    val mAuth = FirebaseAuth.getInstance()
    private lateinit var tvAccountEmail: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    private fun init() = with(binding) {
        setSupportActionBar(included.toolbar)
        val toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, included.toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        tvAccountEmail = navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)

        navView.setNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.id_my_ads -> {
                    Toast.makeText(this@MainActivity, "my ads", Toast.LENGTH_SHORT).show()
                }
                R.id.id_car -> {
                    Toast.makeText(this@MainActivity, "car", Toast.LENGTH_SHORT).show()
                }
                R.id.id_pc -> {
                    Toast.makeText(this@MainActivity, "pc", Toast.LENGTH_SHORT).show()
                }
                R.id.id_smartphone -> {

                }
                R.id.id_dm -> {

                }
                R.id.id_sign_up -> {
                    dialogHelper.showSignDialog(DialogConstants.SIGN_UP_STATE)
                }
                R.id.id_sign_in -> {
                    dialogHelper.showSignDialog(DialogConstants.SIGN_IN_STATE)
                }
                R.id.id_sign_out -> {
                    uiUpdate(null)
                    mAuth.signOut()
                    dialogHelper.accHelper.signOutWithGoogle()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AccountHelper.SIGN_IN_REQUEST_CODE){
            Log.d("AAA", "Sign In result ")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null){
                    dialogHelper.accHelper.signInFirebaseWithGoogle(account.idToken!!)
                }
            }
            catch (ex: ApiException){
                Log.d("AAA", "Api error: ${ex.message} ")
            }
        }
    }

    fun uiUpdate(user: FirebaseUser?) {
        tvAccountEmail.text = if (user == null) "Sign Up or Sign In" else user.email
    }

}