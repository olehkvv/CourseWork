package ua.olehkv.coursework.model


import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ua.olehkv.coursework.EditAdvertisementActivity

class DbManager {
    val db = Firebase.database.getReference("main")
    val auth = Firebase.auth

    fun publishAd(ad: Advertisement, finishWorkListener: FinishWorkListener){
        if(auth.uid != null){
            db.child(ad.key ?: "empty").child(auth.uid!!).child("ad").setValue(ad)
                .addOnCompleteListener { 
                    finishWorkListener.onLoadingFinish()
                }
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

    private fun readDataFromDb(query: Query,readDataCallback: ReadDataCallback?){
        query.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val adList = ArrayList<Advertisement>()
                for (item in snapshot.children) {
                    val ad = item.children.iterator().next().child("ad").getValue(Advertisement::class.java)
                    if (ad != null) adList.add(ad)
                    Log.d("AAA", "Data: ${ad}")
                }
                readDataCallback?.readData(adList)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    interface ReadDataCallback {
        fun readData(list: ArrayList<Advertisement>)
    }

    fun interface FinishWorkListener{
        fun onLoadingFinish()
    }

}