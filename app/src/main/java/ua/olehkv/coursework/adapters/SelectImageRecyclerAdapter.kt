package ua.olehkv.coursework.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import ua.olehkv.coursework.R
import ua.olehkv.coursework.databinding.SelectImageListItemBinding
import ua.olehkv.coursework.models.SelectImageItem


class SelectImageRecyclerAdapter: RecyclerView.Adapter<SelectImageRecyclerAdapter.ImageHolder>() {
    private val mainList = ArrayList<SelectImageItem>()
    class ImageHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = SelectImageListItemBinding.bind(view)
        fun bind(selectImageItem: SelectImageItem) = with(binding){
            tvTitle.text = selectImageItem.title
            imContent.setImageURI(selectImageItem.imageUri.toUri())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_image_list_item, parent, false)
        return ImageHolder(view)
    }

    override fun getItemCount(): Int {
        return mainList.size
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bind(mainList[position])
    }

    fun updateAdapter(newList: ArrayList<SelectImageItem>){
        mainList.clear()
        mainList.addAll(newList)
        notifyDataSetChanged()
    }
}