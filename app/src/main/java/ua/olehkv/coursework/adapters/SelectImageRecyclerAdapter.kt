package ua.olehkv.coursework.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ua.olehkv.coursework.EditAdvertisementActivity
import ua.olehkv.coursework.R
import ua.olehkv.coursework.databinding.SelectImageListItemBinding
import ua.olehkv.coursework.utils.ImageManager
import ua.olehkv.coursework.utils.ImagePicker
import ua.olehkv.coursework.utils.ItemTouchMoveCallback


class SelectImageRecyclerAdapter(private val listener: Listener): RecyclerView.Adapter<SelectImageRecyclerAdapter.ImageHolder>(), ItemTouchMoveCallback.ItemTouchAdapter {
    val mainList = ArrayList<Bitmap>()
    inner class ImageHolder(private val binding: SelectImageListItemBinding, private val context: Context): RecyclerView.ViewHolder(binding.root) {
        fun bind(bitmap: Bitmap) = with(binding){
            tvTitle.text = context.resources.getStringArray(R.array.title_array)[adapterPosition]
            imContent.setImageBitmap(bitmap)
            ImageManager.chooseScaleType(imContent, bitmap)
            ibEdit.setOnClickListener {
                ImagePicker.getSingleImage(context as EditAdvertisementActivity)
                context.editImagePos = adapterPosition
            }
            ibDelete.setOnClickListener {
                mainList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                for(i in 0 until mainList.size){  // for saving delete animation in recycler with deleting
                    notifyItemChanged(i)
                }
                listener.onItemDelete()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_image_list_item, parent, false)
        val binding = SelectImageListItemBinding.bind(view)
        return ImageHolder(binding, parent.context)
    }

    override fun getItemCount(): Int {
        return mainList.size
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bind(mainList[position])
    }

    fun updateAdapter(newList: ArrayList<Bitmap>, shouldClear: Boolean){
        if(shouldClear) mainList.clear()
        mainList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onMove(startPosition: Int, targetPosition: Int) {
        val targetItem = mainList[targetPosition]
        mainList[targetPosition] = mainList[startPosition]
//        val titleStart = mainList[targetPosition].title
//        mainList[targetPosition].title = targetItem.title
        mainList[startPosition] = targetItem
//        mainList[startPosition].title = titleStart

        notifyItemMoved(startPosition, targetPosition)
    }

    interface Listener{
        fun onItemDelete()
    }

    override fun onClear() {
        notifyDataSetChanged()
    }
}