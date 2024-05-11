package ua.olehkv.coursework.fragments

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ua.olehkv.coursework.R
import ua.olehkv.coursework.adapters.SelectImageRecyclerAdapter
import ua.olehkv.coursework.databinding.FragmentImageListBinding
import ua.olehkv.coursework.dialogs.ProgressDialog
import ua.olehkv.coursework.utils.ImageManager
import ua.olehkv.coursework.utils.ImagePicker
import ua.olehkv.coursework.utils.ItemTouchMoveCallback

class ImageListFragment(private val newList: ArrayList<String>?, val onFragmentClose: (list: ArrayList<Bitmap>) -> Unit) : BaseAdsFragment(),
    InterstitialAdListener,
    SelectImageRecyclerAdapter.Listener {
    lateinit var binding: FragmentImageListBinding
    private var adapter = SelectImageRecyclerAdapter(this)
    private val dragCallback = ItemTouchMoveCallback(adapter)
    val touchHelper = ItemTouchHelper(dragCallback)
    private var job: Job? = null
    var addImageItem: MenuItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageListBinding.inflate(layoutInflater)
        adView = binding.adView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        touchHelper.attachToRecyclerView(binding.rcView)
        binding.rcView.layoutManager = LinearLayoutManager(activity)
        binding.rcView.adapter = adapter
        if (newList != null) {
            resizeSelectedImages(newList, true)
        }


    }

    fun updateAdapterFromEdit(bitmapList: ArrayList<Bitmap>){
        adapter.updateAdapter(bitmapList, true)
    }

    private fun setupToolbar() = with(binding){
        toolbar.inflateMenu(R.menu.menu_choose_image)
        addImageItem = toolbar.menu.findItem(R.id.id_add_image)
        val deleteImageItem = toolbar.menu.findItem(R.id.id_delete_image)

        toolbar.setNavigationOnClickListener {
            showInterstitialAd()
        }
        addImageItem?.setOnMenuItemClickListener {
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
            addImageItem?.isVisible = true
            true
        }
    }

    fun updateAdapter(newList: ArrayList<String>){
        resizeSelectedImages(newList, false)
    }

    fun setSingleImage(uri: String, position: Int){
        val pBar = binding.rcView[position].findViewById<ProgressBar>(R.id.pBar)
        job = CoroutineScope(Dispatchers.Main).launch {
            pBar.visibility = View.VISIBLE
            val bitmapList = ImageManager.imageResize(listOf(uri))
            pBar.visibility = View.GONE
            adapter.mainList[position] = bitmapList[0]
            adapter.notifyItemChanged(position)
        }


    }

    private fun resizeSelectedImages(newList: ArrayList<String>, sholdClear: Boolean){
        job = CoroutineScope(Dispatchers.Main).launch {
            val dialog = ProgressDialog.showDialog(activity as Activity)
            val bitmapList = ImageManager.imageResize(newList)
            dialog.dismiss()
            adapter.updateAdapter(bitmapList, sholdClear)
            if (adapter.mainList.size == 3)
                addImageItem?.isVisible = false
        }
    }

    override fun onDetach() {
        super.onDetach()
        onFragmentClose(adapter.mainList)
        job?.cancel()
    }

    override fun onItemDelete() {
        addImageItem?.isVisible = true
    }

    override fun onClose() {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.remove(this@ImageListFragment)
            ?.commit()
    }


}