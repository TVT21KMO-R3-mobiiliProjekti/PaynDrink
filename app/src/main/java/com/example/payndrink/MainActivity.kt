package com.example.payndrink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.payndrink.data.Globals.Companion.ActiveSeatID
import com.example.payndrink.data.Utilities

import com.example.payndrink.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding

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
                        val intent = Intent(applicationContext, ScannerSubActivity::class.java)
                        resultLauncher.launch(intent)
                    }
                    R.id.itemMenu -> {
                        if (ActiveSeatID == null) ActiveSeatID = 1  //Design time! Muista poistaa!!!
                        if (ActiveSeatID != null) {
                            val intent = Intent(applicationContext, RestaurantActivity::class.java)
                            startActivity(intent.apply { putExtra("seatID", ActiveSeatID) })
                        }
                        else Toast.makeText(this@MainActivity, "Seat id must be scanned first", Toast.LENGTH_SHORT).show()

                        //drawerLayout.closeDrawers()
                    }
                    R.id.itemChart -> {
                        Toast.makeText(this@MainActivity, "Third Item clicked", Toast.LENGTH_SHORT).show()
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
                val intent = Intent(applicationContext, RestaurantActivity::class.java)
                startActivity(intent.apply { putExtra("seatID", ActiveSeatID) })    //Not really needed since ActiveSeatID is global
            } else Toast.makeText(this@MainActivity, "Unknown QR-code scanned!", Toast.LENGTH_SHORT)
                .show()
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this@MainActivity, "QR-Scanning Canceled", Toast.LENGTH_SHORT).show()
        }
    }
}