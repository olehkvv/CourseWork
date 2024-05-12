package ua.olehkv.coursework.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ua.olehkv.coursework.databinding.AdListItemBinding
import ua.olehkv.coursework.models.Advertisement

class AdvertisementsAdapter: RecyclerView.Adapter<AdvertisementsAdapter.AdvertisementHolder>() {

    val adList = ArrayList<Advertisement>()
    class AdvertisementHolder(private val binding: AdListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(ad: Advertisement) = with(binding){
            tvDescription.text = ad.description
            tvPrice.text = ad.price

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