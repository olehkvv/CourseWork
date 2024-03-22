package ua.olehkv.coursework.utils

import android.content.Context
import org.json.JSONObject
import ua.olehkv.coursework.R
import java.io.IOException

object CityHelper {
    fun getAllCountries(context: Context): ArrayList<String> {
        val tempArray = ArrayList<String>()
        try {
            val inputStream = context.assets.open("countriesToCities.json")
            val size = inputStream.available()
            val byteArray = ByteArray(size)
            inputStream.read(byteArray)
            val jsonFile = String(byteArray)

            val jsonObject = JSONObject(jsonFile)
            val countryNames = jsonObject.names()
            countryNames?.let {
                for (i in 0 until  it.length())
                    tempArray.add(it[i].toString())
            }


        }
        catch (ex: IOException){

        }
        return tempArray
    }


    fun getAllCities(context: Context, country: String): ArrayList<String> {
        val tempArray = ArrayList<String>()
        try {
            val inputStream = context.assets.open("countriesToCities.json")
            val size = inputStream.available()
            val byteArray = ByteArray(size)
            inputStream.read(byteArray)
            val jsonFile = String(byteArray)

            val jsonObject = JSONObject(jsonFile)
            val cityNames = jsonObject.getJSONArray(country)

            for (i in 0 until cityNames.length())
                tempArray.add(cityNames[i].toString())
        }
        catch (ex: IOException){

        }
        return tempArray
    }

    fun filterListData(context: Context, list: ArrayList<String>, searchText: String?): ArrayList<String> {
        val tempList = ArrayList<String>()
        for(selection in list) {
            if (searchText != null) {
                if (selection.lowercase().startsWith(searchText.lowercase())){
                    tempList.add(selection)
                }
            }
        }
        if (tempList.size == 0) tempList.add(context.getString(R.string.no_result))
        return tempList
    }
}