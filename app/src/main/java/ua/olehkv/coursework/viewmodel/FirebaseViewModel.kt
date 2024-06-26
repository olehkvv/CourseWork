package ua.olehkv.coursework.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ua.olehkv.coursework.model.Advertisement
import ua.olehkv.coursework.model.DbManager

class FirebaseViewModel: ViewModel() {
    private val dbManager = DbManager()
    val liveAdsData = MutableLiveData<ArrayList<Advertisement>>()

    fun loadAllAdsFirstPage(filter: String) {
        dbManager.getAllAdsFirstPage(filter, object: DbManager.ReadDataCallback{
            override fun readData(newList: ArrayList<Advertisement>) {
                liveAdsData.value = newList
            }
        })
    }

    fun loadAllAdsNextPage(time: String, filter: String) {
        dbManager.getAllAdsNextPage(time, filter, object: DbManager.ReadDataCallback{
            override fun readData(newList: ArrayList<Advertisement>) {
                liveAdsData.value = newList
            }
        })
    }

    fun loadAllAdsFromCat(category: String, filter: String) {
        dbManager.getAllAdsFromCatFirstPage(category, filter, object: DbManager.ReadDataCallback{
            override fun readData(newList: ArrayList<Advertisement>) {
                liveAdsData.value = newList
            }
        })
    }

    fun loadAllAdsFromCatNextPage(cat: String, time: String, filter: String) {
        dbManager.getAllAdsFromCatNextPage(cat, time, filter, object: DbManager.ReadDataCallback{
            override fun readData(newList: ArrayList<Advertisement>) {
                liveAdsData.value = newList
            }
        })
    }

    fun loadMyAds() {
        dbManager.getMyAds(object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Advertisement>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadMyFavs(){
        dbManager.getMyFavs(object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Advertisement>) {
                liveAdsData.value = list
            }
        })
    }

    fun deleteAd(ad: Advertisement) {
        dbManager.deleteAd(ad, object: DbManager.FinishWorkListener{
            override fun onLoadingFinish(isDone: Boolean) {
                val updatedList = liveAdsData.value
                updatedList?.remove(ad)
                liveAdsData.postValue(updatedList!!)
            }
        })
    }

    fun onFavClicked(ad: Advertisement){
        dbManager.onFavClicked(ad, object : DbManager.FinishWorkListener{
            override fun onLoadingFinish(isDone: Boolean) {
                val updatedList = liveAdsData.value!!
                val pos = updatedList.indexOf(ad)
                if (pos != -1) {
                    val favCount = if(ad.isFav) ad.favCount.toInt() - 1 else ad.favCount.toInt() + 1
                    updatedList[pos] = updatedList[pos].copy(isFav = !ad.isFav, favCount = favCount.toString())
                }
                liveAdsData.postValue(updatedList)
            }
        })
    }

    fun adViewed(ad: Advertisement){
        dbManager.adViewed(ad)
    }
}