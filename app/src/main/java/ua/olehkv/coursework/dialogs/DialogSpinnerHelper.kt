package ua.olehkv.coursework.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import ua.olehkv.coursework.R
import ua.olehkv.coursework.adapters.SpinnerDialogRecyclerAdapter
import ua.olehkv.coursework.databinding.SpinnerDialogLayoutBinding
import ua.olehkv.coursework.utils.CityHelper

class DialogSpinnerHelper {
    private lateinit var dialog: AlertDialog
    private lateinit var context: Context
    fun showSpinnerDialog(context: Context, countryList: ArrayList<String>, onItemClicked: (String) -> Unit){
        this.context = context
        val binding = SpinnerDialogLayoutBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context)
        val adapter = SpinnerDialogRecyclerAdapter(onItemClicked)
        binding.rcView.layoutManager = LinearLayoutManager(context)
        binding.rcView.adapter = adapter
        adapter.updateAdapter(countryList)
        setSearchViewListener(adapter, countryList, binding.svSpinner)
        dialog = builder.setView(binding.root)
            .create()
        dialog.show()


    }
    fun dismiss(){
        dialog.dismiss()
    }

    private fun setSearchViewListener(adapter: SpinnerDialogRecyclerAdapter, countryList: ArrayList<String>, sv: SearchView) {
        sv.setOnQueryTextListener(object: OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val tempList = CityHelper.filterListData(context, countryList, newText!!)
                adapter.updateAdapter(tempList)
                return true
            }

        })
    }


}