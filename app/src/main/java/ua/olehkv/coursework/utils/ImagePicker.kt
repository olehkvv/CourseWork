package ua.olehkv.coursework.utils

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.olehkv.coursework.EditAdvertisementActivity


object ImagePicker {
    const val REQUEST_CODE_GET_IMAGES = 122
    const val REQUEST_CODE_GET_SINGLE_IMAGE = 121
    const val MAX_IMAGE_COUNT = 3
    fun getImages(context: AppCompatActivity, imageCount: Int, rCode: Int) {
        val options = Options.init()
            .setRequestCode(rCode)
            .setCount(imageCount)
            .setFrontfacing(false)
            .setMode(Options.Mode.Picture)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath("pix/images")

        Pix.start(context, options)

    }

    fun showSelectedImages(resultCode: Int, requestCode: Int, data: Intent?,edAct: EditAdvertisementActivity ){
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_CODE_GET_IMAGES) {
            if (data != null) {
                val returnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                if (returnValues?.size!! > 1 && edAct.chooseImageFrag == null) {
                    edAct.openChooseImageFragment(returnValues)
                }
                else if (returnValues.size ==  1 && edAct.chooseImageFrag == null){
//                    imageAdapter.updateList(returnValues)

                    CoroutineScope(Dispatchers.Main).launch {
                        edAct.binding.pBarLoad.visibility = View.VISIBLE
                        val bitmap = ImageManager.imageResize(returnValues)
                        edAct.binding.pBarLoad.visibility = View.GONE
                        edAct.imageAdapter.updateList(bitmap)
                    }
                    val imgDimension = ImageManager.getImageSize(returnValues[0])
                }
                else if(edAct.chooseImageFrag != null){
                    edAct.chooseImageFrag!!.updateAdapter(returnValues)
                }
            }

        }
        else if(resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_CODE_GET_SINGLE_IMAGE){
            if (data != null) {
                val uris = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                edAct.chooseImageFrag?.setSingleImage(uris!![0]!!, edAct.editImagePos)
            }
        }
    }

}