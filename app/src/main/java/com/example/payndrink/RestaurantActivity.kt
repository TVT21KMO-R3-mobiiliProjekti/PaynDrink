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
import kotlinx.coroutines.*
import java.sql.Connection

class RestaurantActivity : AppCompatActivity() {
    private val scope =  CoroutineScope(newSingleThreadContext("Polling"))
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMenuBinding
    private var totalPrice: Double? = 0.0
    private val dbAccess = DatabaseAccess()
    private val globals = Globals()
    private var connection: Connection? = null
    private var restaurant: Restaurant? = null
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
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

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
                        if (ActiveOrderID < 0) {
                            val intent = Intent(applicationContext, ScannerSubActivity::class.java)
                            scannerLauncher.launch(intent)
                        }
                        else Toast.makeText(this@RestaurantActivity, "You must complete or cancel your active order to scan again!", Toast.LENGTH_LONG).show()
                    }
                    R.id.itemChart -> {
                        drawerLayout.closeDrawers()
                        if(ActiveOrderID < 0) {
                            Toast.makeText(this@RestaurantActivity, "No active order", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(this@RestaurantActivity, "Please wait a moment...", Toast.LENGTH_LONG).show()
                            startActivity(Intent(applicationContext, ShoppingCartActivity::class.java))
                        }
                    }
                    R.id.itemStatus -> {
                        drawerLayout.closeDrawers()
                        if(Globals.TrackedOrderIDs.isEmpty()){
                            Toast.makeText(this@RestaurantActivity, "No pending orders", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(this@RestaurantActivity,"Please wait a moment...", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@RestaurantActivity, StatusActivity::class.java))
                        }
                    }
                }
                true
            }
        }

        updateView()
    }

    /** On activity destroy stop the coroutine */
    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun updateView() {
        scope.launch(Dispatchers.IO) {
            connection = dbAccess.connectToDatabase()
            itemGRV = binding.myGridView //   findViewById(R.id.my_grid_view)
            itemList = ArrayList()
            quickList = ArrayList()

            restaurant = connection?.let {
                Globals.ActiveSeatID.let { it1 ->
                    dbAccess.getRestaurantBySeating(it, it1)
                }
            }
            if (restaurant != null) {
                items = connection?.let { restaurant!!.id?.let { it1 -> dbAccess.getItems(it, it1) } }!!
            }

            withContext(Dispatchers.Main) {
                addRestaurantInfo()
                addMenuItemsToGrid()
            }
        }
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
                if (ActiveOrderID >= 0) {
                    //Get quantity from existing order
                    qty = connection?.let { dbAccess.getOrderItemQty(it, ActiveOrderID, items[position].id!! )} ?: 0
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
        tvTitleMenu.visibility = TextView.VISIBLE
        tvTitleQuick.visibility = TextView.VISIBLE
        val restaurantIv: ImageView =  binding.ivRestPic
        val restaurantTv: TextView = binding.tvRestName
        restaurantIv.setImageBitmap(Utilities().getImageBitmapFromURL(restaurant!!.pictureUrl))
        restaurantTv.text = restaurant!!.name

        adapter = QuickItemAdapter(quickList)
        rv_quick_items.layoutManager = layoutManager
        rv_quick_items.setHasFixedSize(true)
        rv_quick_items.adapter = adapter
        adapter.setOnItemClickListener(object: QuickItemAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                if (ActiveOrderID >= 0)  {
                    Toast.makeText(this@RestaurantActivity, "Can't make quick order while there is active order pending", Toast.LENGTH_SHORT).show()
                }
                else{
                    addItemToOrder(quickList[position].id!!, 1, quickList[position].itemName!!)
                    totalPrice = connection?.let { dbAccess.getItemPrice(it, quickList[position].id!!) }
                    val intent = Intent(applicationContext, PaymentActivity::class.java)
                    intent.putExtra("totalPrice", totalPrice)
                    paymentLauncher.launch(intent)
                }
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
        Globals.PaymentOK = false
        if (ActiveOrderID < 0) {
            if (qty < 1) return     //Zero qty -> No need to add
            //Create a new order if none exists
            ActiveOrderID = connection?.let { dbAccess.createOrder(it, restaurant!!.id!!, Globals.ActiveSeatID) }!!
        }
        else {
            //Check if item already exists in active order
            if ((connection?.let { dbAccess.getOrderItemQty(it, ActiveOrderID, itemID) } ?: 0) >= 1) {
                if (qty > 0) {
                    // Update quantity
                    if (connection?.let {dbAccess.updateItemInOrder(it, qty, itemID, ActiveOrderID) } != null)
                        Toast.makeText(this@RestaurantActivity, "$itemName quantity updated to $qty", Toast.LENGTH_SHORT).show()
                    else Toast.makeText(this@RestaurantActivity, "Updating '$itemName' quantity failed!", Toast.LENGTH_LONG).show()
                    return
                }
                else {
                    //Delete item (also order if its empty)
                    val ret : Int = connection?.let {dbAccess.deleteItemInOrder(it, itemID, ActiveOrderID) }!!
                    if ( ret != 0) {
                        Toast.makeText(this@RestaurantActivity, "$itemName deleted from shopping cart", Toast.LENGTH_SHORT).show()
                        if (ret < 0) {
                            ActiveOrderID = -1
                            globals.savePreferences()
                            Toast.makeText(this@RestaurantActivity, "Shopping cart is empty", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else Toast.makeText(this@RestaurantActivity, "Deleting '$itemName' from shopping cart failed!", Toast.LENGTH_LONG).show()
                }
            }
        }
        globals.savePreferences()
        if (qty < 1) return    //Quantity is 0 -> No need to add new item
        if (ActiveOrderID < 0) {
            Toast.makeText(this@RestaurantActivity, "Adding order to the database failed!", Toast.LENGTH_LONG).show()
            return
        }
        else {
            //Add item to order
            if (connection?.let { dbAccess.addItemToOrder(it, qty, itemID, ActiveOrderID) } != null)
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
                globals.savePreferences()
                updateView()
            } else Toast.makeText(this@RestaurantActivity, "Unknown QR-code scanned!", Toast.LENGTH_SHORT)
                .show()
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this@RestaurantActivity, "QR-Scanning Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    /** Start activity and handle payment activity results **/
    private var paymentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(this@RestaurantActivity, "Payment OK, Sending order...", Toast.LENGTH_SHORT).show()
            if (!sendOrder()) {
                //Payment OK, but sending failed -> Next time button will allow to try order directly
                Globals.PaymentOK = true
                globals.savePreferences()
                Toast.makeText(this@RestaurantActivity, "Order failed, go to shopping cart", Toast.LENGTH_LONG).show()
            }
        }
        else {
            Toast.makeText(this@RestaurantActivity, "Payment failed or canceled!", Toast.LENGTH_LONG).show()
        }
    }

    /** Set send order flag to DB, start status activity and finish this one */
    private fun sendOrder(): Boolean {
        val connection = dbAccess.connectToDatabase()
        val ret : Int = connection?.let {dbAccess.sendOrder(it, ActiveOrderID) }!!
        if (ret >= 0) {
            if(!Globals.TrackedOrderIDs.contains(ActiveOrderID)) {
                Globals.TrackedOrderIDs.add(ActiveOrderID)
            }
            ActiveOrderID = -1
            globals.savePreferences()
            Toast.makeText(this@RestaurantActivity, "Order sent OK", Toast.LENGTH_SHORT).show()
            // Launch status polling
            val intent = Intent(applicationContext, StatusActivity::class.java)
            finish() //Kill this activity
            startActivity(intent)
        }
        else Toast.makeText(this@RestaurantActivity, "Sending order failed!", Toast.LENGTH_LONG).show()
        return ret >= 0
    }
}

