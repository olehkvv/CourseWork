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
import ua.olehkv.coursework.databinding.ActivityEditAdvertisementBinding
import ua.olehkv.coursework.dialogs.DialogSpinnerHelper
import ua.olehkv.coursework.fragments.ImageListFragment
import ua.olehkv.coursework.utils.CityHelper
import ua.olehkv.coursework.utils.ImagePicker

class EditAdvertisementActivity: AppCompatActivity() {
    private lateinit var binding: ActivityEditAdvertisementBinding
    private val dialog = DialogSpinnerHelper()
    private var isImagesPermissionGranted = false
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

            ImagePicker.getImages(this@EditAdvertisementActivity, 3)

//            binding.scrollViewMain.visibility = View.GONE
//            supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.placeHolder, ImageListFragment(){
//                    binding.scrollViewMain.visibility = View.VISIBLE
//                })
//                .commit()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_GET_IMAGES) {
            if (data != null) {
                val returnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                if (returnValues?.size!! > 1) {
                    binding.scrollViewMain.visibility = View.GONE
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.placeHolder, ImageListFragment(returnValues) {
                            binding.scrollViewMain.visibility = View.VISIBLE
                        })
                        .commit()
                }
            }
        }
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
                    ImagePicker.getImages(this, 3)
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