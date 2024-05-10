package ua.olehkv.coursework.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log

import android.view.Surface
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import com.fxn.pix.Pix
import com.google.android.play.integrity.internal.i
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


object ImageManager { // for large bitmaps
    const val MAX_IMAGE_SIZE = 1000
    data class ImageDimension(
        val width: Int,
        val height: Int,
    )

    fun getImageSize(uri: String): ImageDimension {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(uri, options)
        return if(imageRotation(uri) == 90)
            ImageDimension(options.outHeight, options.outWidth)
        else ImageDimension(options.outWidth, options.outHeight)
    }

    private fun imageRotation(uri: String): Int{
        val rotation: Int
        val imageFile = File(uri)
        val exif = ExifInterface(imageFile.absolutePath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        rotation = if (orientation == ExifInterface.ORIENTATION_ROTATE_90
            || orientation == ExifInterface.ORIENTATION_ROTATE_270)
            90
        else 0


        return rotation
    }

    fun chooseScaleType(imView: ImageView, bitmap: Bitmap){
        if (bitmap.width > bitmap.height)
            imView.scaleType = ImageView.ScaleType.CENTER_CROP
        else imView.scaleType = ImageView.ScaleType.CENTER_INSIDE
    }

    suspend fun imageResize(uris: List<String>): ArrayList<Bitmap> = withContext(Dispatchers.IO){
        val tempList = ArrayList<ImageDimension>()
        val bitmapList = ArrayList<Bitmap>()

        for (n in uris.indices){
            val size = getImageSize(uris[n])
            Log.d("AAA", "size: width = ${size.width}, height = ${size.height}")
            val imageRatio = size.width.toDouble() / size.height.toDouble()

            if (imageRatio > 1){ // if width > height
                if(size.width > MAX_IMAGE_SIZE)
                    tempList.add(ImageDimension(MAX_IMAGE_SIZE, (MAX_IMAGE_SIZE / imageRatio).toInt() ))
                else tempList.add(ImageDimension(size.width, size.height))
            }
            else {
                if(size.height > MAX_IMAGE_SIZE)
                    tempList.add(ImageDimension((MAX_IMAGE_SIZE * imageRatio).toInt(), MAX_IMAGE_SIZE))
                else tempList.add(ImageDimension(size.width, size.height))
            }

            Log.d("AAA", "new size: width = ${tempList[n].width}, height = ${tempList[n].height}")

        }

        for (i in uris.indices) {
            val e = runCatching {
                val requestCreator = Picasso.get().load(File(uris[i]))
                val resized = requestCreator.resize(tempList[i].width, tempList[i].height).get()
                bitmapList.add(resized)
            }
//            Log.d("AAA", "Bitmap load done: ${e.isSuccess} ")
        }

        return@withContext bitmapList
    }


}