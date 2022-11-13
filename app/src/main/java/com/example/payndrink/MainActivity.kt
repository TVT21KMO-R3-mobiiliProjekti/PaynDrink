package com.example.payndrink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
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
                    R.id.itemQR -> {
                        val intent = Intent(applicationContext, ScannerSubActivity::class.java)
                        resultLauncher.launch(intent)
                        if(drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START)
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