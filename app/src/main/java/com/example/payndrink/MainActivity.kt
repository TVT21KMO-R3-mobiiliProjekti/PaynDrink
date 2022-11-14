package com.example.payndrink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.payndrink.data.Utilities
import com.example.payndrink.database.DatabaseAccess
import com.example.payndrink.database.Item
import com.example.payndrink.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding

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
                        val intent = Intent(applicationContext, ScannerSubActivity::class.java)
                        resultLauncher.launch(intent)
                        drawerLayout.closeDrawers()
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

        /** Button scan clicked -> Start scanner activity */
        //val btnScanner = findViewById<Button>(R.id.btnScan)
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
            val seatId : String? = data?.getStringExtra("barcode")
            val utilities = Utilities()
            if(seatId?.let { utilities.isNumeric(it) } == true) {
                val intent = Intent(this, RestaurantActivity::class.java)
                val dbAccess = DatabaseAccess()
                val connection = dbAccess.connectToDatabase()
                val restaurant = connection?.let { seatId?.let { it1 -> dbAccess.getRestaurantBySeating(it, it1.toInt()) } }
                if(restaurant != null){
                    startActivity(intent.apply {
                        putExtra("id", restaurant.id)
                        if (seatId != null) {
                            putExtra("seat", seatId.toInt())
                        }
                        putExtra("name", restaurant.name)
                        putExtra("address", restaurant.address)
                        putExtra("description", restaurant.description)
                        putExtra("picture", restaurant.pictureUrl)
                        putExtra("type", restaurant.typeID)
                    })
                }
                else{
                    Toast.makeText(this@MainActivity, "Unknown QR-code scanned!", Toast.LENGTH_SHORT).show()
                }
            }
            else Toast.makeText(this@MainActivity, "Unknown QR-code scanned!", Toast.LENGTH_SHORT).show()
        }
        else if (result.resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this@MainActivity, "QR-Scanning Canceled", Toast.LENGTH_SHORT).show()
        }
    }

}