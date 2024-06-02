package ua.olehkv.coursework.fragments

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import ua.olehkv.coursework.R
import ua.olehkv.coursework.utils.BillingManager


abstract class BaseAdsFragment(): Fragment(), InterstitialAdListener {
    lateinit var adView: AdView
    var interstitialAd: InterstitialAd? = null
    private var prefs: SharedPreferences? = null
    private var isPremiumUser = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = activity?.getSharedPreferences(BillingManager.MAIN_PREF, AppCompatActivity.MODE_PRIVATE)
//        isPremiumUser = prefs?.getBoolean(BillingManager.REMOVE_ADS_PREF, false)!!
        if (!isPremiumUser) {
            initAds()
            loadInterstitialAd()
           // adView.visibility = View.VISIBLE
        } else {
            adView.visibility = View.GONE
        }
    }

    private fun loadInterstitialAd(){
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context as Activity, getString(R.string.ad_interstitial_id), adRequest, object : InterstitialAdLoadCallback(){
            override fun onAdLoaded(ad: InterstitialAd) {
                super.onAdLoaded(ad)
                interstitialAd = ad
            }
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
            }
        })
    }

    fun showInterstitialAd(){
        if(interstitialAd != null){
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    onClose()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    onClose()
                }
            }
        }
        else onClose()
        interstitialAd?.show(activity as Activity)
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onPause() {
        super.onPause()
        adView.pause()
    }

    private fun initAds(){
        MobileAds.initialize(activity as Activity)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    override fun onDestroy() {
        super.onDestroy()
        adView.destroy()
    }
}