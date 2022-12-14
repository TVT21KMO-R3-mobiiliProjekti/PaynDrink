package com.example.payndrink

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.payndrink.data.Globals
import com.example.payndrink.data.Globals.Companion.ActiveOrderID
import com.example.payndrink.data.Globals.Companion.ActiveSeatID
import com.example.payndrink.data.Globals.Companion.TrackedOrderIDs
import com.example.payndrink.data.Globals.Companion.sharedPreferences
import com.example.payndrink.data.Utilities
import com.example.payndrink.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding
    private val globals = Globals()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectNetwork().permitAll().penaltyLog().build()) //DEBUGGING
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open, R.string.closed)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            navView.setNavigationItemSelectedListener {
                when (it.itemId){
                    R.id.itemQR -> {
                        drawerLayout.closeDrawers()
                        if (ActiveOrderID < 0) {
                            val intent = Intent(applicationContext, ScannerSubActivity::class.java)
                            resultLauncher.launch(intent)
                        }
                        else Toast.makeText(this@MainActivity, "You must complete or cancel your active order to scan again!", Toast.LENGTH_LONG).show()
                    }
                    R.id.itemMenu -> {
                        drawerLayout.closeDrawers()
                        ActiveSeatID=3  //DEMONSTRATION!!!! Muista poistaa!!!
                        if (ActiveSeatID >= 0) {
                            Toast.makeText(this@MainActivity, "Please wait a moment...", Toast.LENGTH_LONG).show()
                            startActivity(Intent(applicationContext, RestaurantActivity::class.java))
                        }
                        else Toast.makeText(this@MainActivity, "Seat id must be scanned first", Toast.LENGTH_SHORT).show()
                    }
                    R.id.itemChart -> {
                        drawerLayout.closeDrawers()
                        if(ActiveOrderID >= 0){
                            Toast.makeText(this@MainActivity, "Please wait a moment...", Toast.LENGTH_LONG).show()
                            startActivity(Intent(applicationContext, ShoppingCartActivity::class.java))
                        }
                        else Toast.makeText(this@MainActivity, "No active order", Toast.LENGTH_SHORT).show()
                    }
                    R.id.itemStatus -> {
                        drawerLayout.closeDrawers()
                        if(TrackedOrderIDs.isEmpty()){
                            Toast.makeText(this@MainActivity, "No pending orders", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(this@MainActivity,"Please wait a moment...", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@MainActivity, StatusActivity::class.java))
                        }
                    }
                }
                true
            }
        }

        /** Button scan clicked -> Start scanner activity */
        binding.btnScan.setOnClickListener {
            val intent = Intent(applicationContext, ScannerSubActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        sharedPreferences = this@MainActivity.getPreferences(Context.MODE_PRIVATE)
        globals.loadPreferences()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /** Get result from Scanner activity and launch restaurant activity**/
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val seatID: String? = data?.getStringExtra("barcode")
            val utilities = Utilities()
            if (seatID?.let { utilities.isNumeric(it) } == true) {
                ActiveSeatID = seatID.toInt()
                globals.savePreferences();
                startActivity(Intent(applicationContext, RestaurantActivity::class.java))
            } else Toast.makeText(this@MainActivity, "Unknown QR-code scanned!", Toast.LENGTH_SHORT)
                .show()
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this@MainActivity, "QR-Scanning Canceled", Toast.LENGTH_SHORT).show()
        }
    }
}