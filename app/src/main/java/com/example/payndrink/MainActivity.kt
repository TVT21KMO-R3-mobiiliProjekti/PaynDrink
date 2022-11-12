package com.example.payndrink

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.payndrink.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open, R.string.closed)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

                supportActionBar?.setDisplayHomeAsUpEnabled(true)

                navView.setNavigationItemSelectedListener {
                    when (it.itemId){
                        R.id.Item1 -> {
                            Toast.makeText(this@MainActivity, "first Item clicked", Toast.LENGTH_SHORT).show()
                        }
                        R.id.Item2 -> {
                            Toast.makeText(this@MainActivity, "Second Item clicked", Toast.LENGTH_SHORT).show()
                        }
                        R.id.Item3 -> {
                            Toast.makeText(this@MainActivity, "Third Item clicked", Toast.LENGTH_SHORT).show()
                        }
                    }
                    true
                }
            }
        }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
          return true
        }
        return super.onOptionsItemSelected(item)
    }
}