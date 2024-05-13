package ua.olehkv.coursework.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import ua.olehkv.coursework.EditAdvertisementActivity
import ua.olehkv.coursework.MainActivity
import ua.olehkv.coursework.MainActivity.Companion.ADS_DATA
import ua.olehkv.coursework.MainActivity.Companion.EDIT_STATE
import ua.olehkv.coursework.databinding.AdListItemBinding
import ua.olehkv.coursework.model.Advertisement

class AdvertisementsAdapter(private val mainAct: MainActivity): RecyclerView.Adapter<AdvertisementsAdapter.AdvertisementHolder>() {

    private val adList = ArrayList<Advertisement>()
    inner class AdvertisementHolder(private val binding: AdListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(ad: Advertisement) = with(binding){
            tvTitle.text = ad.title
            tvDescription.text = ad.description
            tvPrice.text = ad.price
            showEditPanel(isOwner(ad))
            tvViewCounter.text = ad.viewsCount
            ibEditAd.setOnClickListener {
                val i = Intent(mainAct, EditAdvertisementActivity::class.java).apply {
                    putExtra(EDIT_STATE, true)
                    putExtra(ADS_DATA, ad)
                }
                mainAct.startActivity(i)
            }

            ibDeleteAd.setOnClickListener {
                mainAct.onDeleteClick(ad)
            }
            itemView.setOnClickListener {
                mainAct.onAdViewed(ad)
            }
        }

        private fun isOwner(ad: Advertisement): Boolean{
            return ad.uid == mainAct.mAuth.uid
        }

        private fun showEditPanel(isOwner: Boolean){
            if (isOwner){
                binding.ibEditAd.visibility = View.VISIBLE
                binding.ibDeleteAd.visibility = View.VISIBLE
            }
            else{
                binding.ibEditAd.visibility = View.GONE
                binding.ibDeleteAd.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertisementHolder {
        val binding = AdListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdvertisementHolder(binding)
    }

    override fun getItemCount() = adList.size

    override fun onBindViewHolder(holder: AdvertisementHolder, position: Int) {
        holder.bind(adList[position])
    }

    fun updateAdList(newList: ArrayList<Advertisement>){
        val diffResult = DiffUtil.calculateDiff(DiffUtilHelper(adList, newList))
        diffResult.dispatchUpdatesTo(this)
        adList.clear()
        adList.addAll(newList)
    }

    interface Listener{
        fun onDeleteClick(ad: Advertisement)
        fun onAdViewed(ad: Advertisement)
    }
}