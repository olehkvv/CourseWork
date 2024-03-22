package ua.olehkv.coursework.utils

import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix


object ImagePicker {
    const val REQUEST_CODE_GET_IMAGES = 122
    fun getImages(context: AppCompatActivity) {
        val options = Options.init()
            .setRequestCode(REQUEST_CODE_GET_IMAGES)
            .setCount(3)
            .setFrontfacing(false)
            .setMode(Options.Mode.Picture)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath("pix/images")

        Pix.start(context, options)

    }
}