package ua.olehkv.coursework

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import ua.olehkv.coursework.MainActivity.Companion.FILTER_KEY
import ua.olehkv.coursework.databinding.ActivityFilterBinding
import ua.olehkv.coursework.dialogs.DialogSpinnerHelper
import ua.olehkv.coursework.utils.CityHelper
import java.lang.StringBuilder

class FilterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFilterBinding
    val dialog = DialogSpinnerHelper()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() = with(binding) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tvChooseCountry.setOnClickListener{
            val listCountry = CityHelper.getAllCountries(this@FilterActivity)
            dialog.showSpinnerDialog(this@FilterActivity, listCountry) {
                tvChooseCountry.text = if(it == getString(R.string.no_result)) getString(R.string.choose_country) else it
                tvChooseCity.text = getString(R.string.choose_city)
                dialog.dismiss()
            }
        }
        tvChooseCity.setOnClickListener {
            val selectedCountry = tvChooseCountry.text.toString()
            if (selectedCountry != getString(R.string.choose_country)){
                val listCity = CityHelper.getAllCities(
                    this@FilterActivity,
                    selectedCountry)
                dialog.showSpinnerDialog(this@FilterActivity, listCity){
                    tvChooseCity.text = if(it == getString(R.string.no_result)) getString(R.string.choose_city) else it
                    dialog.dismiss()
                }
            }
            else Toast.makeText(this@FilterActivity, "No country selected", Toast.LENGTH_SHORT).show()
        }

        btApplyFilter.setOnClickListener {
            Toast.makeText(this@FilterActivity, "Filter: ${createFilter()}", Toast.LENGTH_SHORT).show()
            val i = Intent(this@FilterActivity, MainActivity::class.java)
            i.putExtra(FILTER_KEY, createFilter())
            setResult(RESULT_OK, i)
            finish()
        }

        getFilter()
    }

    private fun createFilter(): String = with(binding) {
        val sBuilder = StringBuilder()
        val arrayTempFilter = listOf(tvChooseCountry.text.toString(),
            tvChooseCity.text.toString(),
            edIndex.text.toString(),
            checkBoxWithSend.isChecked.toString()
            )
        for ((idx, str) in arrayTempFilter.withIndex()) {
            if (str != getString(R.string.choose_country) && str != getString(R.string.choose_city) && str.isNotBlank()){
                sBuilder.append(str)
                if (idx != arrayTempFilter.size - 1)
                    sBuilder.append("_")
            } else{
                sBuilder.append("empty")
                if (idx != arrayTempFilter.size - 1)
                    sBuilder.append("_")
            }


        }
        return@with sBuilder.toString()
    }

    private fun getFilter() = with(binding){
        val filter = intent.getStringExtra(FILTER_KEY)
        if (filter != null && filter != "empty"){
            val filterArray = filter.split("_")
            if (filterArray[0] != "empty") tvChooseCountry.text = filterArray[0]
            if (filterArray[1] != "empty") tvChooseCity.text = filterArray[1]
            if (filterArray[2] != "empty") edIndex.setText(filterArray[2])
            checkBoxWithSend.isChecked = filterArray[3].toBoolean()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}