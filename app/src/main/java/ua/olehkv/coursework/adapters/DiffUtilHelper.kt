package ua.olehkv.coursework.adapters

import androidx.recyclerview.widget.DiffUtil
import ua.olehkv.coursework.model.Advertisement

class DiffUtilHelper(private val oldList: List<Advertisement>,private val newList: List<Advertisement>): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].key == newList[newItemPosition].key
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}