package ua.olehkv.coursework.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import ua.olehkv.coursework.R
import ua.olehkv.coursework.adapters.SelectImageRecyclerAdapter
import ua.olehkv.coursework.databinding.FragmentImageListBinding
import ua.olehkv.coursework.models.SelectImageItem

class ImageListFragment(private val newList: ArrayList<String> ,private val onFragmentClose: () -> Unit) : Fragment() {
    private lateinit var binding: FragmentImageListBinding
    private lateinit var adapter: SelectImageRecyclerAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentImageListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btBack.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.remove(this)
                ?.commit()
        }


        adapter = SelectImageRecyclerAdapter()
        binding.rcView.layoutManager = LinearLayoutManager(activity)
        binding.rcView.adapter = adapter
        val listForUpdate = ArrayList<SelectImageItem>()
        newList.forEachIndexed { i, el ->
            listForUpdate.add(SelectImageItem(i.toString(), el))
        }

        adapter.updateAdapter(listForUpdate)


    }

    override fun onDetach() {
        super.onDetach()
        onFragmentClose()
    }


}