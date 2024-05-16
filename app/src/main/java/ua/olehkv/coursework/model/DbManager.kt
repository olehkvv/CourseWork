package ua.olehkv.coursework.model


import android.util.Log
import com.google.android.play.integrity.internal.ad
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import ua.olehkv.coursework.EditAdvertisementActivity
import ua.olehkv.coursework.model.DbManager.Companion.INFO_NODE

class DbManager {
    val db = Firebase.database.getReference(MAIN_NODE)
    val dbStorage = Firebase.storage.getReference(MAIN_NODE)
    val auth = Firebase.auth

    fun publishAd(ad: Advertisement, finishWorkListener: FinishWorkListener){
        if(auth.uid != null){
            db.child(ad.key ?: "empty").child(auth.uid!!).child(AD_NODE).setValue(ad)
                .addOnCompleteListener {
                    // public filter
                    val adFilter = AdFilter(ad.time, "${ad.category}_${ad.time}")
                    db.child(ad.key ?: "empty").child(FILTER_NODE)
                        .setValue(adFilter).addOnCompleteListener {
                            finishWorkListener.onLoadingFinish()
                        }
                }
        }
    }

    fun getMyAds(readDataCallback: ReadDataCallback?){
        val query = db.orderByChild(auth.uid + "/ad/uid").equalTo(auth.uid)
        readDataFromDb(query, readDataCallback)
    }

    fun getMyFavs(readDataCallback: ReadDataCallback?){
        val query = db.orderByChild("/$FAVS_NODE/${auth.uid}").equalTo(auth.currentUser?.displayName)
        readDataFromDb(query, readDataCallback)
    }

    fun getAllAds(lastTime: String, readDataCallback: ReadDataCallback?){
        val query = db.orderByChild("/adFilter/time")
            .startAfter(lastTime)
            .limitToFirst(ADS_LIMIT)
        readDataFromDb(query, readDataCallback)
    }
    fun getAllAdsFromCat(lastCatTime: String, readDataCallback: ReadDataCallback?){
        val query = db.orderByChild("/adFilter/catTime")
            .startAfter(lastCatTime)
            .limitToFirst(ADS_LIMIT)
        readDataFromDb(query, readDataCallback)
    }

    fun deleteAd(ad: Advertisement, listener: FinishWorkListener){
        if (ad.key == null || ad.uid == null)
            return
        else
            db.child(ad.key).child(ad.uid).removeValue()
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        listener.onLoadingFinish()
                }
    }

    fun adViewed(ad: Advertisement) {
        var counter = ad.viewsCount.toInt()
        counter++
        if(auth.uid != null){
            db.child(ad.key ?: "empty")
                .child(INFO_NODE).setValue(AdMetaData(counter.toString(), ad.emailsCount, ad.callsCount))
        }
    }

    fun onFavClicked(ad: Advertisement, listener: FinishWorkListener){
        if (ad.isFav)
            removeFromFavourites(ad, listener)
        else addToFavourites(ad, listener)
    }
    private fun addToFavourites(ad: Advertisement, listener: FinishWorkListener){
        ad.key?.let {
            auth.uid?.let { uid -> db.child(it).child(FAVS_NODE).child(uid).setValue(auth.currentUser?.displayName)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        listener.onLoadingFinish()
                }
            }
        }
    }

    private fun removeFromFavourites(ad: Advertisement, listener: FinishWorkListener){
        ad.key?.let {
            auth.uid?.let { uid -> db.child(it).child(FAVS_NODE).child(uid).removeValue()
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        listener.onLoadingFinish()
                }
            }
        }
    }


    private fun readDataFromDb(query: Query,readDataCallback: ReadDataCallback?){
        query.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val adList = ArrayList<Advertisement>()
                for (item in snapshot.children) {
                    var ad: Advertisement? = null
                    item.children.forEach {
                        if (ad == null )
                            ad = it.child(AD_NODE).getValue(Advertisement::class.java)
                    }
                    val metaData = item.child(INFO_NODE).getValue(AdMetaData::class.java)
                    val favCount = item.child(FAVS_NODE).childrenCount
                    val isFav = auth.uid?.let {
                        item.child(FAVS_NODE).child(it).getValue(String::class.java)
                    } // check if current user liked this ad

                    ad?.isFav = isFav != null
                    ad?.favCount = favCount.toString()
                    ad?.viewsCount = metaData?.viewsCount ?: "0"
                    ad?.emailsCount = metaData?.emailsCount ?: "0"
                    ad?.callsCount = metaData?.callsCount ?: "0"

                    if (ad != null) adList.add(ad!!)

                    Log.d("AAA", "Data: $ad")
                }
                readDataCallback?.readData(adList)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    companion object{
        const val MAIN_NODE = "main"
        const val AD_NODE = "ad"
        const val INFO_NODE = "info"
        const val FILTER_NODE = "adFilter"
        const val FAVS_NODE = "favs"
        const val ADS_LIMIT = 2
    }

    interface ReadDataCallback {
        fun readData(list: ArrayList<Advertisement>)
    }

    fun interface FinishWorkListener{
        fun onLoadingFinish()
    }

}