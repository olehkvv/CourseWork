package ua.olehkv.coursework.utils

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams

class BillingManager(private val act: AppCompatActivity) {
    private var billingClient: BillingClient? = null

    init {
        setupBillingClient()
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(act).setListener(getPurchaseListener())
            .enablePendingPurchases().build()
    }

    private fun savePurchase(isPurchased: Boolean) {
        val prefs = act.getSharedPreferences(MAIN_PREF, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(REMOVE_ADS_PREF, isPurchased)
        editor.apply()
    }

    fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {

            }

            override fun onBillingSetupFinished(result: BillingResult) {
                getItem()
            }
        })
    }

    private fun getItem(){
        val skuList = ArrayList<String>()
        skuList.add(REMOVE_ADS)
        val skuDetails = SkuDetailsParams.newBuilder()
        skuDetails.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient?.querySkuDetailsAsync(skuDetails.build()){
                billingResult, list ->
                run {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                        if (!list.isNullOrEmpty()){
                            val billingFlowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(list[0])
                                .build()
                            billingClient?.launchBillingFlow(act, billingFlowParams)
                        }
                    }
                }

        }
    }

    private fun nonConsumableItem(purchase: Purchase){
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient?.acknowledgePurchase(acParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                        savePurchase(true)
                        Toast.makeText(act, "Purchase COMPLETED!", Toast.LENGTH_SHORT).show()
                    } else {
                        savePurchase(false)
                        Toast.makeText(act, "Purchase NOT COMPLETED!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun getPurchaseListener(): PurchasesUpdatedListener{
        return PurchasesUpdatedListener{
            billingResult, purchases ->
            run {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                    purchases?.get(0)?.let { nonConsumableItem(it) }
                }
            }
        }
    }

    fun closeConnection() {
        billingClient?.endConnection()
    }

    companion object{
        const val REMOVE_ADS = "remove_ads"
        const val MAIN_PREF = "main_pref"
        const val REMOVE_ADS_PREF = "remove_ads_pref"
    }
}