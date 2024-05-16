package ua.olehkv.coursework

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
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
    private lateinit var imAccount: ImageView
    private val adapter = AdvertisementsAdapter(this)
    lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private val firebaseViewModel: FirebaseViewModel by viewModels()
    private var clearUpdate: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initRcView()
        initViewModel()
        initBottomNavView()
        initGoogleSignInLauncher()
        scrollListener()
//        firebaseViewModel.loadAllAds("0")
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
        navViewSettings()
        val toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, included.toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        tvAccountEmail = navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
        imAccount = navView.getHeaderView(0).findViewById(R.id.imAccountImage)

        navView.setNavigationItemSelectedListener { item ->
            clearUpdate = true
            when(item.itemId){
                R.id.id_my_ads -> {
                    Toast.makeText(this@MainActivity, "my ads", Toast.LENGTH_SHORT).show()
                }
                R.id.id_car -> {
                    getAdsFromCat(getString(R.string.ad_car))
                }
                R.id.id_pc -> {
                    getAdsFromCat(getString(R.string.ad_pc))
                }
                R.id.id_smartphone -> {
                    getAdsFromCat(getString(R.string.ad_smartphone))
                }
                R.id.id_dm -> {
                    getAdsFromCat(getString(R.string.ad_dm))
                }
                R.id.id_sign_up -> {
                    dialogHelper.showSignDialog(DialogConstants.SIGN_UP_STATE)
                }
                R.id.id_sign_in -> {
                    dialogHelper.showSignDialog(DialogConstants.SIGN_IN_STATE)
                }
                R.id.id_sign_out -> {
                    if (mAuth.currentUser?.isAnonymous == true) {
                        drawerLayout.closeDrawer(GravityCompat.START)
                        return@setNavigationItemSelectedListener true
                    }
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
            if(!clearUpdate) {
                adapter.updateAdList(it)
            } else adapter.updateAdapterWithClear(it)
            binding.included.tvEmpty.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
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

     private fun initGoogleSignInLauncher() {
         googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
             Log.d("AAA", "Sign In result ")
             val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
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
            clearUpdate = true
            when(it.itemId) {
                R.id.id_home -> {
                    toolbar.title = getString(R.string.all_ads)
                    firebaseViewModel.loadAllAds("0")
                }
                R.id.id_favs-> {
                    toolbar.title = getString(R.string.fav_ads)
                    firebaseViewModel.loadMyFavs()
                }
                R.id.id_my_ads-> {
                    toolbar.title = getString(R.string.ad_my_ads)
                    firebaseViewModel.loadMyAds()
                }
                R.id.id_new_ad-> {
                    val i = Intent(this@MainActivity, EditAdvertisementActivity::class.java)
                    startActivity(i)
                }
            }
            true
        }
    }

    private fun getAdsFromCat(category: String){
        val catTime = "${category}_0"
        firebaseViewModel.loadAllAdsFromCat(catTime)
    }

    fun uiUpdate(user: FirebaseUser?) {
//        tvAccountEmail.text = if (user == null) "Sign Up or Sign In" else user.email
         if(user == null){
             dialogHelper.accHelper.signInAnonymously(object: AccountHelper.Listener{
                 override fun onComplete() {
                     tvAccountEmail.text = getString(R.string.guest)
                     imAccount.setImageResource(R.drawable.avatar)
                 }
             })
         }
        else if(user.isAnonymous) {
             tvAccountEmail.text = getString(R.string.guest)
             imAccount.setImageResource(R.drawable.avatar)
         }
        else if(!user.isAnonymous) {
             tvAccountEmail.text = "${user.displayName}\n${user.email}"
             Picasso.get().load(user.photoUrl).into(imAccount)
         }
    }


    companion object{
        const val EDIT_STATE = "edit_state"
        const val ADS_DATA = "ads_data"
        const val SCROLL_DOWN = -1
    }

    override fun onDeleteClick(ad: Advertisement) {
        firebaseViewModel.deleteAd(ad)
    }

    override fun onAdViewed(ad: Advertisement) {
        firebaseViewModel.adViewed(ad)
        val i = Intent(this, DescriptionActivity::class.java).apply {
            putExtra("AD", ad)
        }
        startActivity(i)
    }

    override fun onFavClicked(ad: Advertisement) {
        firebaseViewModel.onFavClicked(ad)
    }

    private fun scrollListener() = with(binding.included) {
        rcView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.canScrollVertically(SCROLL_DOWN) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    clearUpdate = false
                    val adsList = firebaseViewModel.liveAdsData.value!!
                    if (adsList.isNotEmpty()) {
                        getAllAdsFromCat(adsList)
                    }
                }
            }
        })
    }

    private fun getAllAdsFromCat(adsList: ArrayList<Advertisement>){
        adsList[adsList.size - 1].let {
            if (it.category == getString(R.string.all_ads)) {
                firebaseViewModel.loadAllAds(it.time)
            } else {
                val catTime = "${it.category}_${it.time}"
                firebaseViewModel.loadAllAdsFromCat(catTime)
            }
        }
    }

    private fun navViewSettings() = with(binding){
        val menu = navView.menu
        val menuIds = listOf(R.id.adsCategory, R.id.accCategory)
        for(id in menuIds) {
            val item: MenuItem = menu.findItem(id)
            val spanAdsCat = SpannableString(item.title)
            spanAdsCat.setSpan(ForegroundColorSpan(
                ContextCompat.getColor(this@MainActivity, R.color.black)),
                0, item.title!!.length, 0)
            item.title = spanAdsCat
        }
    }

}