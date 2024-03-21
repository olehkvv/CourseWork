package ua.olehkv.coursework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import ua.olehkv.coursework.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() = with(binding) {
        val toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, included.toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.id_my_ads -> {
                    Toast.makeText(this@MainActivity, "my ads", Toast.LENGTH_SHORT).show()
                }
                R.id.id_car -> {
                    Toast.makeText(this@MainActivity, "car", Toast.LENGTH_SHORT).show()
                }
                R.id.id_pc -> {
                    Toast.makeText(this@MainActivity, "pc", Toast.LENGTH_SHORT).show()
                }
                R.id.id_smartphone -> {

                }
                R.id.id_dm -> {

                }
                R.id.id_sign_up -> {
                    Toast.makeText(this@MainActivity, "sign up", Toast.LENGTH_SHORT).show()
                }
                R.id.id_sign_in -> {
                    Toast.makeText(this@MainActivity, "sign in", Toast.LENGTH_SHORT).show()
                }
                R.id.id_sign_out -> {
                    Toast.makeText(this@MainActivity, "sign out", Toast.LENGTH_SHORT).show()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

}