package ua.olehkv.coursework.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ua.olehkv.coursework.R
import ua.olehkv.coursework.databinding.SpListItemBinding

class SpinnerDialogRecyclerAdapter(private val onItemClicked: (String) -> Unit): RecyclerView.Adapter<SpinnerDialogRecyclerAdapter.ViewHolder>() {

    val mainList = ArrayList<String>()
    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        private val binding = SpListItemBinding.bind(view)
        fun bind(text: String) = with(binding){
            tvSpItem.text = text
            itemView.setOnClickListener {
                onItemClicked(text)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sp_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mainList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mainList[position])
    }

    fun updateAdapter(newList: ArrayList<String>){
        mainList.clear()
        mainList.addAll(newList)
        notifyDataSetChanged()
    }
}