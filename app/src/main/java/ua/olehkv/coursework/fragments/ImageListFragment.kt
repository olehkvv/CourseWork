package ua.olehkv.coursework.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import ua.olehkv.coursework.R
import ua.olehkv.coursework.adapters.SelectImageRecyclerAdapter
import ua.olehkv.coursework.databinding.FragmentImageListBinding
import ua.olehkv.coursework.utils.ImagePicker
import ua.olehkv.coursework.utils.ItemTouchMoveCallback
import java.text.FieldPosition

class ImageListFragment(private val newList: ArrayList<String>, private val onFragmentClose: (list: ArrayList<String>) -> Unit) : Fragment() {
    private lateinit var binding: FragmentImageListBinding
    private var adapter = SelectImageRecyclerAdapter()
    private val dragCallback = ItemTouchMoveCallback(adapter)
    val touchHelper = ItemTouchHelper(dragCallback)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentImageListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
//        binding.btBack.setOnClickListener {
//            activity?.supportFragmentManager
//                ?.beginTransaction()
//                ?.remove(this)
//                ?.commit()
//        }

        touchHelper.attachToRecyclerView(binding.rcView)
        binding.rcView.layoutManager = LinearLayoutManager(activity)
        binding.rcView.adapter = adapter

        adapter.updateAdapter(newList, true)


    }

    private fun setupToolbar() = with(binding){
        toolbar.inflateMenu(R.menu.menu_choose_image)
        val addImageItem = toolbar.menu.findItem(R.id.id_add_image)
        val deleteImageItem = toolbar.menu.findItem(R.id.id_delete_image)

        toolbar.setNavigationOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.remove(this@ImageListFragment)
                ?.commit()
        }
        addImageItem.setOnMenuItemClickListener {
            if (adapter.mainList.size == ImagePicker.MAX_IMAGE_COUNT){
                Toast.makeText(activity, "You can not add more than 3 photos", Toast.LENGTH_SHORT).show()
                return@setOnMenuItemClickListener true
            }
            val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainList.size
            ImagePicker.getImages(activity as AppCompatActivity, imageCount, ImagePicker.REQUEST_CODE_GET_IMAGES)
            true
        }
        deleteImageItem.setOnMenuItemClickListener {
            adapter.updateAdapter(ArrayList(), true)
            true
        }
    }

    fun updateAdapter(newList: ArrayList<String>){
        adapter.updateAdapter(newList, false)
    }

    fun setSingleImage(uri: String, position: Int){
        adapter.mainList[position] = uri
        adapter.notifyDataSetChanged()

    }

    override fun onDetach() {
        super.onDetach()
        onFragmentClose(adapter.mainList)

    }


}