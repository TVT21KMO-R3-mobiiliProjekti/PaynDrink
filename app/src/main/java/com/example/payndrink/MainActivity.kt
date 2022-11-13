package com.example.payndrink

import android.content.Intent
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
                            startActivity(Intent(applicationContext, ScannerActivity::class.java))
                        }
                        R.id.Item2 -> {
                            startActivity(Intent(applicationContext, Menu::class.java))
                        }
                        R.id.Item3 -> {
                            startActivity(Intent(applicationContext, ShoppingCart::class.java))
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