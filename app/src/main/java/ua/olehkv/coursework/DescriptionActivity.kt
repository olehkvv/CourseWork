package ua.olehkv.coursework

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.viewpager2.widget.ViewPager2
import ua.olehkv.coursework.adapters.ImageAdapter
import ua.olehkv.coursework.databinding.ActivityDescriptionBinding
import ua.olehkv.coursework.model.Advertisement
import ua.olehkv.coursework.utils.ImageManager

class DescriptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDescriptionBinding
    private lateinit var adapter: ImageAdapter
    private var ad: Advertisement? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() = with(binding){
        adapter = ImageAdapter()
        viewPager.adapter = adapter
        fbTel.setOnClickListener{ call() }
        fbEmail.setOnClickListener { sendEmail() }
        getIntentFromMainAct()
        imageChangeCounter()
    }

    private fun getIntentFromMainAct(){
        ad = intent.getSerializableExtra("AD") as Advertisement
        if (ad != null)
            updateUi(ad!!)
    }

    private fun updateUi(ad: Advertisement){
        ImageManager.fillImageArray(ad, adapter)
        fillTextViews(ad)
    }
    private fun fillTextViews(ad: Advertisement) = with(binding){
        tvTitle.text = ad.title
        tvDescription.text = ad.description
        tvPrice.text = "$ ${ad.price}"
        tvTelNumber.text = ad.tel
        tvEmail.text = ad.email
        tvCountry.text = ad.country
        tvCity.text = ad.city
        tvIndex.text = ad.index
        tvWithSend.text = if(ad.withSend.toBoolean()) "Yes" else "No"
    }

    private fun imageChangeCounter() = with(binding){
        tvImageCounter.text = "0 / ${adapter.itemCount}"
        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val imageCounter = "${position + 1} / ${adapter.itemCount}"
                tvImageCounter.text = imageCounter
            }
        })
    }


    private fun call(){
        val callUri = "tel:${ad?.tel}".toUri()
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = callUri
        startActivity(dialIntent)
    }

    private fun sendEmail(){
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "message/rfc822"
        emailIntent.apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(ad?.email))
            putExtra(Intent.EXTRA_SUBJECT, "Advertisement index #${ad?.index}")
            putExtra(Intent.EXTRA_TEXT, "I am interested in your advertisement ${ad?.title}")
        }

        try {
            startActivity(Intent.createChooser(emailIntent, "Choose mail app"))
        }catch (ex: ActivityNotFoundException){
            Toast.makeText(this, "Exception while sending email", Toast.LENGTH_SHORT).show()
        }
    }


}