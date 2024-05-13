package ua.olehkv.coursework.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import ua.olehkv.coursework.databinding.AdListItemBinding
import ua.olehkv.coursework.model.Advertisement

class AdvertisementsAdapter(private val auth: FirebaseAuth): RecyclerView.Adapter<AdvertisementsAdapter.AdvertisementHolder>() {

    private val adList = ArrayList<Advertisement>()
    inner class AdvertisementHolder(private val binding: AdListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(ad: Advertisement) = with(binding){
            tvTitle.text = ad.title
            tvDescription.text = ad.description
            tvPrice.text = ad.price
            showEditPanel(isOwner(ad))
        }

        private fun isOwner(ad: Advertisement): Boolean{
            return ad.uid == auth.uid
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
        adList.clear()
        adList.addAll(newList)
        notifyDataSetChanged()
    }
}