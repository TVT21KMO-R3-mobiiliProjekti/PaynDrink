package com.example.payndrink

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
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
                }
                true
            }
        }

        /** Start scanner activity */
        val btnScanner = findViewById<Button>(R.id.btnScan)
        btnScanner.setOnClickListener {
            val intent = Intent(applicationContext, ScannerSubActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
          return true
        }
        return super.onOptionsItemSelected(item)
    }

    /** Get result from activity **/
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val str = data?.getStringExtra("barcode")
            Toast.makeText(this@MainActivity, str.toString(), Toast.LENGTH_SHORT).show()
        }
        else if (result.resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this@MainActivity, "Unknown QR-code scanned!", Toast.LENGTH_SHORT).show()
        }
    }



}