package ua.olehkv.coursework.utils

import android.net.Uri
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.olehkv.coursework.EditAdvertisementActivity
import ua.olehkv.coursework.R
import ua.olehkv.coursework.fragments.ImageListFragment


object ImagePicker {
    const val MAX_IMAGE_COUNT = 3
    private fun getOptions(imageCount: Int): Options {
        return Options().apply {
            count = imageCount
            isFrontFacing = false
            mode = Mode.Picture
            path = "pix/images"
        }
    }

    fun getMultiImages(edAct: EditAdvertisementActivity, imageCount: Int) {
        edAct.addPixToActivity(R.id.placeHolder, getOptions(imageCount)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {  //use results as it.data
                    getMultiSelectImages(edAct, result.data)
                    closePixFragment(edAct)
                }
                PixEventCallback.Status.BACK_PRESSED -> { // back pressed called

                }
            }
        }
    }

    fun addImages(edAct: EditAdvertisementActivity, imageCount: Int) {
        edAct.addPixToActivity(R.id.placeHolder, getOptions(imageCount)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {  //use results as it.data
                    openChooseImageFrag(edAct)
                    edAct.chooseImageFrag!!.updateAdapter(result.data as ArrayList<Uri>, edAct)
                }

                PixEventCallback.Status.BACK_PRESSED -> { // back pressed called

                }
            }
        }
    }

    fun getSingleImage(edAct: EditAdvertisementActivity) {
        edAct.addPixToActivity(R.id.placeHolder, getOptions(1)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {  //use results as it.data
                    edAct.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    edAct.window.statusBarColor = ContextCompat.getColor(edAct as AppCompatActivity, R.color.white)
                    edAct.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    openChooseImageFrag(edAct)
                    singleImage(edAct, result.data[0])
                }
                PixEventCallback.Status.BACK_PRESSED -> { // back pressed called

                }
            }
        }
    }

    private fun openChooseImageFrag(edAct:EditAdvertisementActivity){
        edAct.supportFragmentManager
            .beginTransaction()
            .replace(R.id.placeHolder, edAct.chooseImageFrag!!)
            .commit()
        edAct.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        edAct.window.statusBarColor = ContextCompat.getColor(edAct as AppCompatActivity, R.color.white)
        edAct.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun closePixFragment(edAct: EditAdvertisementActivity) {
        edAct.runOnUiThread {
            val decorView: View = edAct.getWindow().getDecorView()
            val uiOptions = View.SYSTEM_UI_FLAG_VISIBLE
            decorView.systemUiVisibility = uiOptions
            edAct.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            edAct.window.statusBarColor = ContextCompat.getColor(edAct as AppCompatActivity, R.color.white)
            edAct.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        val fragList = edAct.supportFragmentManager.fragments
        fragList.forEach { f ->
            if (f.isVisible && f::class.java != ImageListFragment::class.java) {
                edAct.supportFragmentManager
                    .beginTransaction()
                    .remove(f)
                    .commit()

                edAct.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                edAct.window.statusBarColor = ContextCompat.getColor(edAct as AppCompatActivity, R.color.white)
                edAct.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


    private fun getMultiSelectImages(edAct: EditAdvertisementActivity, uris: List<Uri>) {
        if (uris.size > 1 && edAct.chooseImageFrag == null) {
            edAct.openChooseImageFragment(uris as ArrayList<Uri>)
        }
        else if (uris.size == 1 && edAct.chooseImageFrag == null) {
            CoroutineScope(Dispatchers.Main).launch {
                edAct.binding.pBarLoad.visibility = View.VISIBLE
                val bitmap = ImageManager.imageResize(uris, edAct)
                edAct.binding.pBarLoad.visibility = View.GONE
                edAct.imageAdapter.updateList(bitmap)
                closePixFragment(edAct)

            }
        }
    }

    private fun singleImage(edAct: EditAdvertisementActivity, uri: Uri) {
        edAct.chooseImageFrag?.setSingleImage(uri, edAct.editImagePos)
    }
}
