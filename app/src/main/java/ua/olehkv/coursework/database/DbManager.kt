package ua.olehkv.coursework.database


import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ua.olehkv.coursework.models.Advertisement

class DbManager(private val readDataCallback: ReadDataCallback? = null) {
    val db = Firebase.database.getReference("main")
    val auth = Firebase.auth

    fun publishAd(ad: Advertisement){
        if(auth.uid != null){
            db.child(ad.key ?: "empty").child(auth.uid!!).child("ad").setValue(ad)
        }
    }

    fun readDataFromDb(){
        db.addListenerForSingleValueEvent(object: ValueEventListener{
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
}