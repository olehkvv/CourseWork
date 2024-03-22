package ua.olehkv.coursework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import ua.olehkv.coursework.databinding.ActivityEditAdvertisementBinding
import ua.olehkv.coursework.dialogs.DialogSpinnerHelper
import ua.olehkv.coursework.utils.CityHelper

class EditAdvertisementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAdvertisementBinding
    private val dialog = DialogSpinnerHelper()
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