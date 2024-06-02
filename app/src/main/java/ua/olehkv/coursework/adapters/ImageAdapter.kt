package ua.olehkv.coursework.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ua.olehkv.coursework.R
import ua.olehkv.coursework.databinding.ImageAdapterItemBinding

class ImageAdapter: RecyclerView.Adapter<ImageAdapter.ImageHolder>() {
    val imageList = ArrayList<Bitmap>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_adapter_item, parent, false)
        return ImageHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bind(imageList[position])
    }

    class ImageHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = ImageAdapterItemBinding.bind(view)
        fun bind(bitmap: Bitmap) = with(binding){
            imItem.setImageBitmap(bitmap)
        }
    }

    fun updateList(newList: ArrayList<Bitmap>){
        imageList.clear()
        imageList.addAll(newList)
        notifyDataSetChanged()
    }
}