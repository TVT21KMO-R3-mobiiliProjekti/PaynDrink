package com.example.payndrink

import android.os.Bundle
import android.widget.*
import android.app.Activity
import android.content.ClipDescription
import android.content.Intent
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.payndrink.data.GridRVAdapter
import com.example.payndrink.data.GridViewMenuItem
import com.example.payndrink.data.QuickItemAdapter
import com.example.payndrink.data.Utilities
import com.example.payndrink.database.DatabaseAccess
import com.example.payndrink.database.Item
import com.example.payndrink.database.Restaurant
import kotlinx.android.synthetic.main.activity_menu.*
import java.sql.Connection

class RestaurantActivity : AppCompatActivity() {
    private val dbAccess = DatabaseAccess()
    private var connection: Connection? = null
    private lateinit var restaurant: Restaurant
    private var seat: Int? = null
    private var activeOrderID : Int? = null
    lateinit var itemGRV: GridView
    lateinit var itemList: List<GridViewMenuItem>
    private lateinit var items: MutableList<Item>
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var quickList: MutableList<GridViewMenuItem>
    private lateinit var adapter: QuickItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        connection = dbAccess.connectToDatabase()
        itemGRV = findViewById(R.id.my_grid_view)
        itemList = ArrayList()
        quickList = ArrayList()
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            restaurant = Restaurant(bundle.getInt("id"), bundle.getString("name"),
                bundle.getString("address"), bundle.getString("description"),
                bundle.getString("picture"), bundle.getInt("type"))
            seat = bundle.getInt("seat")
            items = connection?.let { restaurant.id?.let { it1 -> dbAccess.getItems(it, it1) } }!!
            addRestaurantInfo()
            addMenuItemsToGrid()
        }
    }

    private fun addMenuItemsToGrid() {
        if (items != null) {
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
        }
        val itemAdapter = GridRVAdapter(itemList = itemList,this@RestaurantActivity)
        itemGRV.adapter = itemAdapter

        itemGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            //Launch MenuItemActivity
            val intent = Intent(applicationContext, MenuItemActivity::class.java)
            intent.apply {
                var qty : Int = 1
                if (activeOrderID != null) {
                    //Get quantity from existing order
                    qty = connection?.let { dbAccess.getOrderItemQty(it, activeOrderID!!, items?.get(position)?.id!! )} ?: 0
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
        val restaurantIv: ImageView = findViewById(R.id.iv_rest_pic)
        val restaurantTv: TextView = findViewById(R.id.tv_rest_name)
        restaurantIv.setImageBitmap(Utilities().getImageBitmapFromURL(restaurant.pictureUrl))
        restaurantTv.text = restaurant.name

        adapter = QuickItemAdapter(quickList)
        rv_quick_items.layoutManager = layoutManager
        rv_quick_items.setHasFixedSize(true)
        rv_quick_items.adapter = adapter
        adapter.setOnItemClickListener(object: QuickItemAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                var qty : Int = 0
                if (activeOrderID != null) {
                    //Get quantity from existing order
                    qty = connection?.let { dbAccess.getOrderItemQty(it, activeOrderID!!, quickList[position].id!! )} ?: 0
                }
                qty += 1
                addItemToOrder(quickList[position].id!!, qty, quickList[position].itemName!!)
            }

        })
    }

    /** Start activity and handle results **/
    private var menuItemLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val itemID: Int? = data?.getIntExtra("id", -1)
            val qty: Int? = data?.getIntExtra("qty", 0)
            val name: String? = data?.getStringExtra("name")
            if (itemID ?: 0 >= 0) {
                addItemToOrder(itemID!!, qty!!, name!!)
            }
        }
    }

    /** Add item to the order (create if needed) or update quantity if item already exists in order */
    private fun addItemToOrder(itemID : Int, qty : Int, itemName: String) {
        if (activeOrderID == null) {
            //Create a new order if none exists
            activeOrderID = connection?.let { dbAccess.createOrder(it, restaurant.id!! , seat!!) }
        }
        else {
            //Check is item already entered
            if (connection?.let { dbAccess.getOrderItemQty(it, activeOrderID!!, itemID )} ?: 0 >= 1) {
                // Update quantity
                if (connection?.let {dbAccess.updateItemInOrder(it, qty, itemID, activeOrderID!!) } != null)
                    Toast.makeText(this@RestaurantActivity, "$itemName quantity updated to $qty", Toast.LENGTH_SHORT).show()
                else Toast.makeText(this@RestaurantActivity, "Updating '$itemName' quantity failed!", Toast.LENGTH_LONG).show()
                return
            }
        }

        if (activeOrderID == null) {
            Toast.makeText(this@RestaurantActivity, "Adding order to the database failed!", Toast.LENGTH_LONG).show()
            return
        }
        else {
            //Add item to order
            if (connection?.let { dbAccess.addItemToOrder(it, qty, itemID, activeOrderID!!) } != null)
                Toast.makeText(this@RestaurantActivity, "$qty x $itemName added to the order", Toast.LENGTH_SHORT).show()
            else Toast.makeText(this@RestaurantActivity, "Adding '$itemName' to order failed!", Toast.LENGTH_LONG).show()
        }
    }
}

