package ua.olehkv.coursework

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.tasks.OnCompleteListener
//import com.fxn.utility.PermUtil
import ua.olehkv.coursework.MainActivity.Companion.ADS_DATA
import ua.olehkv.coursework.MainActivity.Companion.EDIT_STATE
import ua.olehkv.coursework.adapters.ImageAdapter
import ua.olehkv.coursework.model.DbManager
import ua.olehkv.coursework.databinding.ActivityEditAdvertisementBinding
import ua.olehkv.coursework.dialogs.DialogSpinnerHelper
import ua.olehkv.coursework.fragments.ImageListFragment
import ua.olehkv.coursework.model.Advertisement
import ua.olehkv.coursework.utils.CityHelper
import ua.olehkv.coursework.utils.ImageManager
import ua.olehkv.coursework.utils.ImagePicker
import java.io.ByteArrayOutputStream

class EditAdvertisementActivity: AppCompatActivity() {
    lateinit var binding: ActivityEditAdvertisementBinding
    val dialog = DialogSpinnerHelper()
    lateinit var imageAdapter: ImageAdapter
    private val dbManager = DbManager()
    var chooseImageFrag: ImageListFragment? = null
    var editImagePos = 0
    private var imageIndex = 0
    private var isEditState = false
    private var ad: Advertisement? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdvertisementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        checkEditState()
        imageChangeCounter()
    }

    private fun init() = with(binding){
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
                ImagePicker.getMultiImages(this@EditAdvertisementActivity, ImagePicker.MAX_IMAGE_COUNT)
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
            ad = fillAd()
            if(isEditState)
                //  add a callback to avoid the case when we switch to MainActivity,
                // and the data has not yet had time to load on FireBase
                ad?.copy(key = ad?.key)?.let { it1 ->
                    dbManager.publishAd(it1) // don't overwrite key for edited ads otherwise it creates a new ad
                    { finish() }
                } // when ad loaded on Firebase
            else {
//                dbManager.publishAd(tempAd) { finish() }
                uploadAllImages()
            }
        }

    }

    private fun fillAd(): Advertisement = with(binding){
        Advertisement(
            country = tvChooseCountry.text.toString(),
            city = tvChooseCity.text.toString(),
            tel = edTelNumber.text.toString(),
            email = edEmail.text.toString(),
            index = edIndex.text.toString(),
            withSend = checkBoxWithSend.isChecked.toString(),
            category = tvSelectCategory.text.toString(),
            title = edTitle.text.toString(),
            price = edPrice.text.toString(),
            description = edDescription.text.toString(),
            mainImage = "empty",
            image2 = "empty",
            image3 = "empty",
            favCount = "0",
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
        ImageManager.fillImageArray(ad, imageAdapter)
    }


    fun openChooseImageFragment(newList: ArrayList<Uri>?){
        chooseImageFrag = ImageListFragment() { list ->
            binding.scrollViewMain.visibility = View.VISIBLE
            binding.viewPagerImages.setCurrentItem(0, false)
            imageAdapter.updateList(list)
            chooseImageFrag = null
        }
        if (newList != null) {
            chooseImageFrag?.resizeSelectedImages(newList, true, this)
        }
        binding.scrollViewMain.visibility = View.GONE
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.placeHolder, chooseImageFrag!!)
            .commit()
    }

    private fun uploadAllImages() {
        if (imageAdapter.imageList.size == imageIndex){
            dbManager.publishAd(ad!!) { finish() }
            return
        }
        val byteArray = prepareImageByteArray(imageAdapter.imageList[imageIndex])
        uploadImage(byteArray) {
            Log.d("AAA", "image URI = ${it.result }")
//            dbManager.publishAd(ad!!) { finish() }
            nextImage(it.result.toString())
        }
    }

    private fun nextImage(uri: String){
        setImageUriToAd(uri)
        imageIndex++
        uploadAllImages()
    }
    private fun setImageUriToAd(uri: String) {
        when(imageIndex){
            0 -> ad = ad?.copy(mainImage = uri)
            1 -> ad = ad?.copy(image2 = uri)
            2 -> ad = ad?.copy(image3 = uri)
        }
    }

    private fun prepareImageByteArray(bitmap: Bitmap): ByteArray {
        val outStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outStream)
        return outStream.toByteArray()
    }

    private fun uploadImage(byteArray: ByteArray, listener: OnCompleteListener<Uri>) {
        val imStorageRef = dbManager.dbStorage
            .child(dbManager.auth.uid!!)
            .child("image_${System.currentTimeMillis()}")
        val uploadTask = imStorageRef.putBytes(byteArray)
        uploadTask.continueWithTask {
            task -> imStorageRef.downloadUrl
        }.addOnCompleteListener(listener)


    }
    private fun imageChangeCounter() = with(binding){
        tvImageCounter.text = "0 / ${imageAdapter.itemCount}"
        viewPagerImages.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val imageCounter = "${position + 1} / ${imageAdapter.itemCount}"
                tvImageCounter.text = imageCounter
            }
        })
    }
}