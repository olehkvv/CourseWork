package ua.olehkv.coursework

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.fxn.utility.PermUtil
import ua.olehkv.coursework.MainActivity.Companion.ADS_DATA
import ua.olehkv.coursework.MainActivity.Companion.EDIT_STATE
import ua.olehkv.coursework.adapters.ImageAdapter
import ua.olehkv.coursework.model.DbManager
import ua.olehkv.coursework.databinding.ActivityEditAdvertisementBinding
import ua.olehkv.coursework.dialogs.DialogSpinnerHelper
import ua.olehkv.coursework.fragments.ImageListFragment
import ua.olehkv.coursework.model.Advertisement
import ua.olehkv.coursework.utils.CityHelper
import ua.olehkv.coursework.utils.ImagePicker

class EditAdvertisementActivity: AppCompatActivity() {
    lateinit var binding: ActivityEditAdvertisementBinding
    val dialog = DialogSpinnerHelper()
    var isImagesPermissionGranted = false
    lateinit var imageAdapter: ImageAdapter
    private val dbManager = DbManager()
    var chooseImageFrag: ImageListFragment? = null
    var editImagePos = 0
    var launcherMultiSelectImages: ActivityResultLauncher<Intent>? = null
    var launcherSingleSelectImages: ActivityResultLauncher<Intent>? = null
    private var isEditState = false
    private var ad: Advertisement? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdvertisementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        checkEditState()
    }

    private fun init() = with(binding){
        launcherMultiSelectImages = ImagePicker.getLauncherForMultiSelectImages(this@EditAdvertisementActivity)
        launcherSingleSelectImages = ImagePicker.getLauncherForSingleImage(this@EditAdvertisementActivity)

        tvChooseCountry.setOnClickListener{
            val listCountry = CityHelper.getAllCountries(this@EditAdvertisementActivity)
            dialog.showSpinnerDialog(this@EditAdvertisementActivity, listCountry) {
                tvChooseCountry.text = if(it == getString(R.string.no_result)) getString(R.string.choose_country) else it
                tvChooseCity.text = getString(R.string.choose_city)
                dialog.dismiss()
            }
        }
        tvChooseCity.setOnClickListener {
            val selectedCountry = tvChooseCountry.text.toString()
            if (selectedCountry != getString(R.string.choose_country)){
                val listCity = CityHelper.getAllCities(
                    this@EditAdvertisementActivity,
                    selectedCountry)

                dialog.showSpinnerDialog(this@EditAdvertisementActivity, listCity){
                    tvChooseCity.text = if(it == getString(R.string.no_result)) getString(R.string.choose_city) else it
                    dialog.dismiss()
                }
            }
            else Toast.makeText(this@EditAdvertisementActivity, "No country selected", Toast.LENGTH_SHORT).show()
        }

        ibOpenPicker.setOnClickListener {
            if(imageAdapter.imageList.size == 0)
                ImagePicker.launcher(this@EditAdvertisementActivity,
                    launcherMultiSelectImages,
                    ImagePicker.MAX_IMAGE_COUNT)
            else {
                openChooseImageFragment(null)
                chooseImageFrag?.updateAdapterFromEdit(imageAdapter.imageList)
            }

        }
        imageAdapter = ImageAdapter()
        viewPagerImages.adapter = imageAdapter

        tvSelectCategory.setOnClickListener {
            val categoryList = resources.getStringArray(R.array.category).toMutableList() as ArrayList
            dialog.showSpinnerDialog(this@EditAdvertisementActivity, categoryList){
                tvSelectCategory.text = if(it == getString(R.string.no_result)) getString(R.string.select_category) else it
                dialog.dismiss()
            }
        }

        btPublish.setOnClickListener {
            val tempAd = fillAd()
            if(isEditState)
                //  add a callback to avoid the case when we switch to MainActivity,
                // and the data has not yet had time to load on FireBase
                dbManager.publishAd(tempAd .copy(key = ad?.key)) // don't overwrite key for edited ads otherwise it creates a new ad
                { finish() } // when ad loaded on Firebase
            else {
                dbManager.publishAd(tempAd)
                { finish() }
            }
        }

    }

    private fun fillAd(): Advertisement = with(binding){
         Advertisement(
            country = tvChooseCountry.text.toString(),
            city = tvChooseCity.text.toString(),
            tel = edTelNumber.text.toString(),
            index = edIndex.text.toString(),
            withSend = checkBoxWithSend.isChecked.toString(),
            category = tvSelectCategory.text.toString(),
            title = edTitle.text.toString(),
            price = edPrice.text.toString(),
            description = edDescription.text.toString(),
            key = dbManager.db.push().key, // generates unique key
            uid = dbManager.auth.uid
        )
    }

    private fun checkEditState(){
        isEditState = isEditState()
        if (isEditState) {
            ad = intent.getSerializableExtra(ADS_DATA) as Advertisement
            fillViews(ad!!)
        }
    }

    private fun isEditState(): Boolean {
        return intent.getBooleanExtra(EDIT_STATE, false)
    }
    private fun fillViews(ad: Advertisement) = with(binding){
        tvChooseCountry.text = ad.country
        tvChooseCity.text = ad.city
        edTelNumber.setText(ad.tel)
        edIndex.setText(ad.index)
        checkBoxWithSend.isChecked = ad.withSend.toBoolean()
        tvSelectCategory.text = ad.category
        edTitle.setText(ad.title)
        edPrice.setText(ad.price)
        edDescription.setText(ad.description)
    }


    fun openChooseImageFragment(newList: ArrayList<String>?){
        chooseImageFrag = ImageListFragment(newList) { list ->
            binding.scrollViewMain.visibility = View.VISIBLE
            binding.viewPagerImages.setCurrentItem(0, false)
            imageAdapter.updateList(list)
            chooseImageFrag = null
        }
        binding.scrollViewMain.visibility = View.GONE
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.placeHolder, chooseImageFrag!!)
            .commit()
    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    isImagesPermissionGranted = true
                    ImagePicker.launcher(this@EditAdvertisementActivity,
                        launcherMultiSelectImages,
                        ImagePicker.MAX_IMAGE_COUNT)
                }
                else {
                    isImagesPermissionGranted = false
                    Toast.makeText(this, "Approve permissions to open image picker", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



}