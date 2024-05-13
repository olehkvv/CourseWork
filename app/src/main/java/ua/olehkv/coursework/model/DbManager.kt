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
import ua.olehkv.coursework.EditAdvertisementActivity
import ua.olehkv.coursework.model.DbManager.Companion.INFO_NODE

class DbManager {
    val db = Firebase.database.getReference(MAIN_NODE)
    val auth = Firebase.auth

    fun publishAd(ad: Advertisement, finishWorkListener: FinishWorkListener){
        if(auth.uid != null){
            db.child(ad.key ?: "empty").child(auth.uid!!).child(AD_NODE).setValue(ad)
                .addOnCompleteListener { 
                    finishWorkListener.onLoadingFinish()
                }
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

    fun getMyAds(readDataCallback: ReadDataCallback?){
        val query = db.orderByChild(auth.uid + "/ad/uid").equalTo(auth.uid)
        readDataFromDb(query, readDataCallback)
    }

    fun getAllAds(readDataCallback: ReadDataCallback?){
        val query = db.orderByChild(auth.uid + "/ad/price")
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
    }

    interface ReadDataCallback {
        fun readData(list: ArrayList<Advertisement>)
    }

    fun interface FinishWorkListener{
        fun onLoadingFinish()
    }

}