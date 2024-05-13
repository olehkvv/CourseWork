package ua.olehkv.coursework

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ua.olehkv.coursework.adapters.AdvertisementsAdapter
import ua.olehkv.coursework.databinding.ActivityMainBinding
import ua.olehkv.coursework.dialogs.DialogConstants
import ua.olehkv.coursework.dialogs.DialogAuthHelper
import ua.olehkv.coursework.firebase.AccountHelper
import ua.olehkv.coursework.model.Advertisement
import ua.olehkv.coursework.viewmodel.FirebaseViewModel

class MainActivity : AppCompatActivity(), AdvertisementsAdapter.Listener{
    private lateinit var binding: ActivityMainBinding
    private val dialogHelper = DialogAuthHelper(this)
    val mAuth = Firebase.auth
    private lateinit var tvAccountEmail: TextView
    private val adapter = AdvertisementsAdapter(this)
    private val firebaseViewModel: FirebaseViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initRcView()
        initViewModel()
        initBottomNavView()
        firebaseViewModel.loadAllAds()
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    override fun onResume() {
        super.onResume()
        binding.included.btNavView.selectedItemId = R.id.id_home
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


    private fun initRcView() = with(binding.included){
        rcView.layoutManager = LinearLayoutManager(this@MainActivity)
        rcView.adapter = adapter
//        adapter.updateAdList()

    }

    private fun initViewModel(){
        firebaseViewModel.liveAdsData.observe(this){
            adapter.updateAdList(it)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.id_new_ads -> {

            }
        }
        return super.onOptionsItemSelected(item)

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

    private fun initBottomNavView() = with(binding.included){
        btNavView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.id_home -> {
                    firebaseViewModel.loadAllAds()
                    toolbar.title = getString(R.string.all_ads)
                }
                R.id.id_favs->{
                    Toast.makeText(this@MainActivity, "Favs", Toast.LENGTH_SHORT).show()
                    toolbar.title = getString(R.string.fav_ads)
                }
                R.id.id_my_ads->{
                    firebaseViewModel.loadMyAds()
                    toolbar.title = getString(R.string.ad_my_ads)
                }
                R.id.id_new_ad->{
                    val i = Intent(this@MainActivity, EditAdvertisementActivity::class.java)
                    startActivity(i)
                }
            }
            true
        }
    }

    fun uiUpdate(user: FirebaseUser?) {
        tvAccountEmail.text = if (user == null) "Sign Up or Sign In" else user.email
    }


    companion object{
        const val EDIT_STATE = "edit_state"
        const val ADS_DATA = "ads_data"
    }

    override fun onDeleteClick(ad: Advertisement) {
        firebaseViewModel.deleteAd(ad)
    }


}