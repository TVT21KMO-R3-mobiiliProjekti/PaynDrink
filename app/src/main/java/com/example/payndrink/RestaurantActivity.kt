package com.example.payndrink

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.payndrink.data.GridRVAdapter
import com.example.payndrink.data.GridViewMenuItem
import com.example.payndrink.data.QuickItemAdapter
import com.example.payndrink.database.DatabaseAccess
import com.example.payndrink.database.Item
import com.example.payndrink.database.Restaurant
import kotlinx.android.synthetic.main.activity_menu.*
import java.net.URL
import java.sql.Connection

class RestaurantActivity : AppCompatActivity() {
    private val dbAccess = DatabaseAccess()
    private var connection: Connection? = null
    private lateinit var restaurant: Restaurant
    private var seat: Int? = null
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
                itemList = itemList + GridViewMenuItem(item.id, item.name, item.pictureUrl,
                    item.description, item.quick, item.price)
                if(item.quick != null && item.quick > 0 && item.pictureUrl != null){
                    quickList += GridViewMenuItem(
                        item.id, item.name, item.pictureUrl,
                        item.description, item.quick, item.price
                    )
                }
            }
        }
        val itemAdapter = GridRVAdapter(itemList = itemList,this@RestaurantActivity)
        itemGRV.adapter = itemAdapter
        itemGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Toast.makeText(applicationContext, itemList[position].itemDescription,
                Toast.LENGTH_SHORT).show()
        }
        adapter = QuickItemAdapter(quickList)
        rv_quick_items.layoutManager = layoutManager
        rv_quick_items.setHasFixedSize(true)
        rv_quick_items.adapter = adapter
        adapter.setOnItemClickListener(object: QuickItemAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                Toast.makeText(this@RestaurantActivity, quickList[position].itemDescription, Toast.LENGTH_SHORT).show()
            }

        })

    }
    private fun addRestaurantInfo(){
        val bitmap = BitmapFactory.decodeStream(URL(restaurant.pictureUrl).openConnection().getInputStream())
        val restaurantIv: ImageView = findViewById(R.id.iv_rest_pic)
        val restaurantTv: TextView = findViewById(R.id.tv_rest_name)
        restaurantIv.setImageBitmap(bitmap)
        restaurantTv.text = restaurant.name
    }
}

