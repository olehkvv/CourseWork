package ua.olehkv.coursework

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.google.android.play.core.integrity.z
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.olehkv.coursework.adapters.ImageAdapter
import ua.olehkv.coursework.database.DbManager
import ua.olehkv.coursework.databinding.ActivityEditAdvertisementBinding
import ua.olehkv.coursework.dialogs.DialogSpinnerHelper
import ua.olehkv.coursework.fragments.ImageListFragment
import ua.olehkv.coursework.models.Advertisement
import ua.olehkv.coursework.utils.CityHelper
import ua.olehkv.coursework.utils.ImageManager.ImageDimension
import ua.olehkv.coursework.utils.ImageManager
import ua.olehkv.coursework.utils.ImagePicker

class EditAdvertisementActivity: AppCompatActivity() {
    lateinit var binding: ActivityEditAdvertisementBinding
    val dialog = DialogSpinnerHelper()
    var isImagesPermissionGranted = false
    lateinit var imageAdapter: ImageAdapter
    private val dbManager = DbManager()
    var chooseImageFrag: ImageListFragment? = null
    var editImagePos = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdvertisementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

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
                ImagePicker.getImages(this@EditAdvertisementActivity, 3, ImagePicker.REQUEST_CODE_GET_IMAGES)
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

            dbManager.publishAd(fillAd())
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
            price = edPrice.text.toString(),
            description = edDescription.text.toString(),
            key = dbManager.db.push().key // generates unique key

        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        ImagePicker.showSelectedImages(resultCode,requestCode, data, this)
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
                    ImagePicker.getImages(this, 3, ImagePicker.REQUEST_CODE_GET_IMAGES)
                }
                else {
                    isImagesPermissionGranted = false
                    Toast.makeText(this, "Approve permissions to open image picker", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



//    private fun init() = with(binding) {
//        val adapter = ArrayAdapter(
//            this@EditAdvertisementActivity,
//            R.layout.simple_spinner_item,
//            CityHelper.getAllCountries(this@EditAdvertisementActivity)
//        )
//
//        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
//        spinnerCountry.adapter = adapter
//    }
}