package com.example.payndrink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.payndrink.data.*
import com.example.payndrink.data.Globals.Companion.ActiveOrderID
import com.example.payndrink.database.DatabaseAccess
import com.example.payndrink.database.Item
import com.example.payndrink.database.Restaurant
import com.example.payndrink.databinding.ActivityMenuBinding
import kotlinx.android.synthetic.main.activity_menu.*
import java.sql.Connection

class RestaurantActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMenuBinding

    private val dbAccess = DatabaseAccess()
    private var connection: Connection? = null
    private var restaurant: Restaurant? = null
    //private var seatID: Int? = null
    private lateinit var itemGRV: GridView
    private lateinit var itemList: List<GridViewMenuItem>
    private lateinit var items: MutableList<Item>
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var quickList: MutableList<GridViewMenuItem>
    private lateinit var adapter: QuickItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            toggle = ActionBarDrawerToggle(this@RestaurantActivity, drawerLayout, R.string.open, R.string.closed)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            navView.menu.findItem(R.id.itemMenu).isVisible = false
            navView.setNavigationItemSelectedListener {
                when (it.itemId){
                    R.id.itemQR -> {
                        drawerLayout.closeDrawers()
                        val intent = Intent(applicationContext, ScannerSubActivity::class.java)
                        scannerLauncher.launch(intent)
                    }
                    R.id.itemChart -> {
                        drawerLayout.closeDrawers()
                        if(ActiveOrderID == null){
                            Toast.makeText(this@RestaurantActivity, "No active order", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(this@RestaurantActivity, "Please wait a moment...", Toast.LENGTH_LONG).show()
                            Thread.sleep(500)
                            startActivity(Intent(applicationContext, ShoppingCartActivity::class.java))
                        }
                    }
                }
                true
            }
        }

        updateView()
    }

    fun updateView() {
        connection = dbAccess.connectToDatabase()
        itemGRV = binding.myGridView //   findViewById(R.id.my_grid_view)
        itemList = ArrayList()
        quickList = ArrayList()
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        restaurant = connection?.let { Globals.ActiveSeatID?.let { it1 -> dbAccess.getRestaurantBySeating(it, it1) } }
        if (restaurant != null) {
            items = connection?.let { restaurant!!.id?.let { it1 -> dbAccess.getItems(it, it1) } }!!
        }
        addRestaurantInfo()
        addMenuItemsToGrid()
    }


    private fun addMenuItemsToGrid() {
        itemList = emptyList()
        quickList.clear()
        for(item in items){
            itemList = itemList + GridViewMenuItem(item.id, item.name, item.pictureUrl, Utilities().getImageBitmapFromURL(item.pictureUrl),
                item.description, item.quick, item.price)
            if(item.quick != null && item.quick > 0 && item.pictureUrl != null){
                quickList += GridViewMenuItem(
                    item.id, item.name, item.pictureUrl, Utilities().getImageBitmapFromURL(item.pictureUrl),
                    item.description, item.quick, item.price
                )
            }
        }
        val itemAdapter = GridRVAdapter(itemList = itemList,this@RestaurantActivity)
        itemGRV.adapter = itemAdapter

        itemGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            //Launch MenuItemActivity
            val intent = Intent(applicationContext, MenuItemActivity::class.java)
            intent.apply {
                var qty = 1
                if (ActiveOrderID != null) {
                    //Get quantity from existing order
                    qty = connection?.let { dbAccess.getOrderItemQty(it, ActiveOrderID!!, items[position].id!! )} ?: 0
                }
                if (qty < 1) qty = 1
                putExtra("id", itemList[position].id)
                putExtra("qty", qty)
                putExtra("name", itemList[position].itemName)
                putExtra("description", itemList[position].itemDescription)
                putExtra("pictureUrl", itemList[position].pictureUrl)
                putExtra("price", itemList[position].itemPrice)
            }
            menuItemLauncher.launch(intent)
        }
    }

    private fun addRestaurantInfo(){
        val restaurantIv: ImageView =  binding.ivRestPic  //findViewById(R.id.iv_rest_pic)
        val restaurantTv: TextView = binding.tvRestName   //findViewById(R.id.tv_rest_name)
        restaurantIv.setImageBitmap(Utilities().getImageBitmapFromURL(restaurant!!.pictureUrl))
        restaurantTv.text = restaurant!!.name

        adapter = QuickItemAdapter(quickList)
        rv_quick_items.layoutManager = layoutManager
        rv_quick_items.setHasFixedSize(true)
        rv_quick_items.adapter = adapter
        adapter.setOnItemClickListener(object: QuickItemAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                var qty = 0
                if (ActiveOrderID != null) {
                    //Get quantity from existing order
                    qty = connection?.let { dbAccess.getOrderItemQty(it, ActiveOrderID!!, quickList[position].id!! )} ?: 0
                }
                if (qty < MAX_QTY) {
                    qty += 1
                    addItemToOrder(quickList[position].id!!, qty, quickList[position].itemName!!)
                }
                else Toast.makeText(this@RestaurantActivity, "Maximum quantity is $MAX_QTY", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /** Start activity and handle menuItem activity results **/
    private var menuItemLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val itemID: Int? = data?.getIntExtra("id", -1)
            val qty: Int? = data?.getIntExtra("qty", 0)
            val name: String? = data?.getStringExtra("name")
            if ((itemID ?: 0) >= 0) {
                addItemToOrder(itemID!!, qty!!, name!!)
            }
        }
    }

    /** Add item to order (create order if needed), update quantity or delete item */
    private fun addItemToOrder(itemID : Int, qty : Int, itemName: String) {
        if (ActiveOrderID == null) {
            if (qty < 1) return     //Zero qty -> No need to add
            //Create a new order if none exists
            ActiveOrderID = connection?.let { dbAccess.createOrder(it, restaurant!!.id!! , Globals.ActiveSeatID!!) }
        }
        else {
            //Check if item already exists in active order
            if ((connection?.let { dbAccess.getOrderItemQty(it, ActiveOrderID!!, itemID) } ?: 0) >= 1) {
                if (qty > 0) {
                    // Update quantity
                    if (connection?.let {dbAccess.updateItemInOrder(it, qty, itemID, ActiveOrderID!!) } != null)
                        Toast.makeText(this@RestaurantActivity, "$itemName quantity updated to $qty", Toast.LENGTH_SHORT).show()
                    else Toast.makeText(this@RestaurantActivity, "Updating '$itemName' quantity failed!", Toast.LENGTH_LONG).show()
                    return
                }
                else {
                    //Delete item (also order if its empty)
                    val ret : Int = connection?.let {dbAccess.deleteItemInOrder(it, itemID, ActiveOrderID!!) }!!
                    if ( ret != 0) {
                        Toast.makeText(this@RestaurantActivity, "$itemName deleted from shopping cart", Toast.LENGTH_SHORT).show()
                        if (ret < 0) {
                            ActiveOrderID = null
                            Toast.makeText(this@RestaurantActivity, "Shopping cart is empty", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else Toast.makeText(this@RestaurantActivity, "Deleting '$itemName' from shopping cart failed!", Toast.LENGTH_LONG).show()
                }
            }
        }

        if (qty < 1) return    //Quantity is 0 -> No need to add new item
        if (ActiveOrderID == null) {
            Toast.makeText(this@RestaurantActivity, "Adding order to the database failed!", Toast.LENGTH_LONG).show()
            return
        }
        else {
            //Add item to order
            if (connection?.let { dbAccess.addItemToOrder(it, qty, itemID, ActiveOrderID!!) } != null)
                Toast.makeText(this@RestaurantActivity, "$qty x $itemName added to shopping cart", Toast.LENGTH_SHORT).show()
            else Toast.makeText(this@RestaurantActivity, "Adding '$itemName' to shopping cart failed!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /** Get result from Scanner activity and update view **/
    private var scannerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val seatID: String? = data?.getStringExtra("barcode")
            val utilities = Utilities()
            if (seatID?.let { utilities.isNumeric(it) } == true) {
                Globals.ActiveSeatID = seatID.toInt()
                updateView()
            } else Toast.makeText(this@RestaurantActivity, "Unknown QR-code scanned!", Toast.LENGTH_SHORT)
                .show()
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this@RestaurantActivity, "QR-Scanning Canceled", Toast.LENGTH_SHORT).show()
        }
    }
}

