package ua.olehkv.coursework.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ua.olehkv.coursework.EditAdvertisementActivity
import ua.olehkv.coursework.R
import ua.olehkv.coursework.databinding.SelectImageListItemBinding
import ua.olehkv.coursework.utils.ImagePicker
import ua.olehkv.coursework.utils.ItemTouchMoveCallback


class SelectImageRecyclerAdapter: RecyclerView.Adapter<SelectImageRecyclerAdapter.ImageHolder>(), ItemTouchMoveCallback.ItemTouchAdapter {
    val mainList = ArrayList<String>()
    inner class ImageHolder(view: View, private val context: Context): RecyclerView.ViewHolder(view) {
        private val binding = SelectImageListItemBinding.bind(view)
        fun bind(selectImageItem: String) = with(binding){
            tvTitle.text = context.resources.getStringArray(R.array.title_array)[adapterPosition]
            imContent.setImageURI(Uri.parse(selectImageItem))
            ibEdit.setOnClickListener {
                ImagePicker.getImages(context as EditAdvertisementActivity, 1, ImagePicker.REQUEST_CODE_GET_SINGLE_IMAGE)
                context.editImagePos = adapterPosition
            }
            ibDelete.setOnClickListener {
                mainList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                for(i in 0 until mainList.size){  // for saving delete animation in recycler with deleting
                    notifyItemChanged(i)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_image_list_item, parent, false)
        return ImageHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return mainList.size
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bind(mainList[position])
    }

    fun updateAdapter(newList: ArrayList<String>, shouldClear: Boolean){
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

    override fun onClear() {
        notifyDataSetChanged()
    }
}