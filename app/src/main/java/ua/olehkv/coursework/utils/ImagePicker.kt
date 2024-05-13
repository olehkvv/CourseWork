package ua.olehkv.coursework.utils

import android.content.Intent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.olehkv.coursework.EditAdvertisementActivity


object ImagePicker {
    const val MAX_IMAGE_COUNT = 3
    private fun getOptions(imageCount: Int): Options {
        return Options.init()
            .setCount(imageCount)
            .setFrontfacing(false)
            .setMode(Options.Mode.Picture)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath("pix/images")
    }

    fun launcher(edAct: EditAdvertisementActivity, launcher: ActivityResultLauncher<Intent>?, imageCount: Int){
        PermUtil.checkForCamaraWritePermissions(edAct){
            val i = Intent(edAct, Pix::class.java).apply {
                putExtra("options", getOptions(imageCount))
            }
            launcher?.launch(i)
        }

    }


    fun getLauncherForMultiSelectImages(edAct: EditAdvertisementActivity): ActivityResultLauncher<Intent>{
        return edAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            it.apply {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    if (data != null) {
                        val returnValues = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                        if (returnValues?.size!! > 1 && edAct.chooseImageFrag == null) {
                            edAct.openChooseImageFragment(returnValues)
                        } else if (returnValues.size == 1 && edAct.chooseImageFrag == null) {
                            CoroutineScope(Dispatchers.Main).launch {
                                edAct.binding.pBarLoad.visibility = View.VISIBLE
                                val bitmap = ImageManager.imageResize(returnValues)
                                edAct.binding.pBarLoad.visibility = View.GONE
                                edAct.imageAdapter.updateList(bitmap)
                            }
                            val imgDimension = ImageManager.getImageSize(returnValues[0])
                        } else if (edAct.chooseImageFrag != null) {
                            edAct.chooseImageFrag!!.updateAdapter(returnValues)
                        }
                    }

                }
            }
        }
    }

    fun getLauncherForSingleImage(edAct: EditAdvertisementActivity): ActivityResultLauncher<Intent>{
        return edAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == AppCompatActivity.RESULT_OK){
                if (it.data != null) {
                    val uris = it.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                    edAct.chooseImageFrag?.setSingleImage(uris!![0]!!, edAct.editImagePos)
                }
            }
        }


    }

}