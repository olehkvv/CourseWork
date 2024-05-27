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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
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
import ua.olehkv.coursework.utils.AppMainState
import ua.olehkv.coursework.utils.FilterManager
import ua.olehkv.coursework.viewmodel.FirebaseViewModel

class MainActivity : AppCompatActivity(), AdvertisementsAdapter.Listener{
    private lateinit var binding: ActivityMainBinding
    private val dialogHelper = DialogAuthHelper(this)
    val mAuth = Firebase.auth
    private lateinit var tvAccountEmail: TextView
    private lateinit var imAccount: ImageView
    private val adapter = AdvertisementsAdapter(this)
    lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    lateinit var filterLauncher: ActivityResultLauncher<Intent>
    private val firebaseViewModel: FirebaseViewModel by viewModels()
    private var clearUpdate: Boolean = true
    private lateinit var currentCategory: String
    private var filter: String = "empty"
    private var filterDb: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as AppMainState).showAdIfAvailable(this) { }
        init()
        initAds()
        initRcView()
        initViewModel()
        initBottomNavView()
        initLaunchers()
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
        binding.included.adView.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.included.adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.included.adView.destroy()
    }

    private fun init() = with(binding) {
        currentCategory = getString(R.string.all_ads)
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

    private fun initAds(){
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.included.adView.loadAd(adRequest)

    }

    private fun initRcView() = with(binding.included){
        rcView.layoutManager = LinearLayoutManager(this@MainActivity)
        rcView.adapter = adapter
//        adapter.updateAdList()

    }

    private fun initViewModel(){
        firebaseViewModel.liveAdsData.observe(this){
            val list = getAdsByCategory(it)
            if(!clearUpdate) {
                adapter.updateAdList(list)
            } else adapter.updateAdapterWithClear(list)
            binding.included.tvEmpty.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
        }
    }

    private fun getAdsByCategory(list: ArrayList<Advertisement>): ArrayList<Advertisement>{
        val tempList = ArrayList<Advertisement>()
        tempList.addAll(list)
        if (currentCategory != getString(R.string.all_ads)){
            tempList.clear()
            list.forEach {
                if (currentCategory == it.category)
                    tempList.add(it)
            }
        }
        tempList.reverse()
        return tempList
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.id_filter -> {
                val i = Intent(this@MainActivity, FilterActivity::class.java)
                i.putExtra(FILTER_KEY, filter) // for saving filter state
                filterLauncher.launch(i)
            }
        }
        return super.onOptionsItemSelected(item)

    }

     private fun initLaunchers() {
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

         filterLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
             if (it.resultCode == RESULT_OK) {
                 filter = it.data?.getStringExtra(FILTER_KEY)!!
                 filterDb = FilterManager.getFilter(filter)
                 Toast.makeText(this, "Got filter: $filter", Toast.LENGTH_SHORT).show()
                 Log.d("filt", "Got filter: $filter")
                 Log.d("filt", "filterDb = : $filterDb")
             }
             else if (it.resultCode == RESULT_CANCELED) {
                 filterDb = ""
                 filter = "empty"
             }
         }
    }

    private fun initBottomNavView() = with(binding.included){
        btNavView.setOnItemSelectedListener {
            clearUpdate = true
            when(it.itemId) {
                R.id.id_home -> {
                    currentCategory = getString(R.string.all_ads)
                    toolbar.title = getString(R.string.all_ads)
                    firebaseViewModel.loadAllAdsFirstPage(filterDb)
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

    private fun getAdsFromCat(category: String) {
        currentCategory = category
        firebaseViewModel.loadAllAdsFromCat(category, filterDb)
    }

    fun uiUpdate(user: FirebaseUser?) {
//        tvAccountEmail.text = if (user == null) "Sign Up or Sign In" else user.email
        if (user == null) {
            dialogHelper.accHelper.signInAnonymously(object : AccountHelper.Listener {
                override fun onComplete() {
                    tvAccountEmail.text = getString(R.string.guest)
                    imAccount.setImageResource(R.drawable.avatar)
                }
            })
        } else if (user.isAnonymous) {
            tvAccountEmail.text = getString(R.string.guest)
            imAccount.setImageResource(R.drawable.avatar)
        } else if (!user.isAnonymous) {
            tvAccountEmail.text = "${user.displayName}\n${user.email}"
            Picasso.get().load(user.photoUrl).into(imAccount)
        }
    }


    companion object{
        const val EDIT_STATE = "edit_state"
        const val ADS_DATA = "ads_data"
        const val SCROLL_DOWN = -1
        const val FILTER_KEY = "filter_key"
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
        adsList[0].let {
            if (currentCategory == getString(R.string.all_ads)) {
                firebaseViewModel.loadAllAdsNextPage(it.time, filterDb)
            } else {
                firebaseViewModel.loadAllAdsFromCatNextPage(it.category!!, it.time, filterDb)
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