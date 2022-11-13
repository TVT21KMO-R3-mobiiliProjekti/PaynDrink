package com.example.payndrink

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.payndrink.database.DatabaseAccess
import com.example.payndrink.database.DatabaseAdapter
import com.example.payndrink.database.Item
import com.example.payndrink.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding

    //Item Array
    var itemList: MutableList<Item>? = null

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

            setUpGridView()
        }

        /** Button scan clicked -> Start scanner activity */
        //Note! Hide button if there is items in grid
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

    private fun setUpGridView() {
        val adapter = ImageListAdapter(this, R.layout.itemlist_item, itemList)
        binding.gridview.adapter = adapter

        binding.gridview.onItemClickListener =
            AdapterView.OnItemClickListener { parent, v, position, id ->
                Toast.makeText(
                    this@MainActivity, " Clicked Position: " + (position + 1),
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    /** Get result from activity **/
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        //TEST
        refreshItems()

        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val seatId = data?.getStringExtra("barcode")
            if(seatId?.isNotEmpty() == true) {
                refreshItems()
                //Toast.makeText(this@MainActivity, seatId.toString(), Toast.LENGTH_SHORT).show()
            }
            else Toast.makeText(this@MainActivity, "Unknown QR-code scanned!", Toast.LENGTH_SHORT).show()
        }
        else if (result.resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this@MainActivity, "QR-Scanning Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshItems() {
        val dba = DatabaseAdapter()
        var test = dba.getItemsByRestaurant(1)
        itemList = test
    }







}