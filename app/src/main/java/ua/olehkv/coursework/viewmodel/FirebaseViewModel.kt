package ua.olehkv.coursework.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ua.olehkv.coursework.model.Advertisement
import ua.olehkv.coursework.model.DbManager

class FirebaseViewModel: ViewModel() {
    private val dbManager = DbManager()
    val liveAdsData = MutableLiveData<ArrayList<Advertisement>>()

    fun loadAllAds(){
        dbManager.readDataFromDb(object: DbManager.ReadDataCallback{
            override fun readData(newList: ArrayList<Advertisement>) {
                liveAdsData.value = newList
            }
        })
    }
}