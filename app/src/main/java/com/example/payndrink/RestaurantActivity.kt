package com.example.payndrink

import android.os.Bundle
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.payndrink.data.GridViewModal
import com.example.payndrink.database.DatabaseAccess
import com.example.payndrink.database.Item
import com.example.payndrink.database.Restaurant
import java.sql.Connection

class RestaurantActivity : AppCompatActivity() {
    private var connection: Connection? = null
    private var restaurant: Restaurant? = null
    private var seat: Int? = null
    lateinit var itemGRV: GridView
    lateinit var itemList: List<GridViewModal>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        itemGRV = findViewById(R.id.my_grid_view)
        itemList = ArrayList()

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            restaurant = Restaurant(bundle.getInt("id"), bundle.getString("name"),
                bundle.getString("address"), bundle.getString("description"),
                bundle.getString("picture"), bundle.getInt("type"))
            seat = bundle.getInt("seat")
        }
        addMenuItemsToGrid()
    }

    private fun addMenuItemsToGrid() {
        val dbAccess = DatabaseAccess()
        connection = dbAccess.connectToDatabase()
        val items: MutableList<Item>? = connection?.let { restaurant?.id?.let { it1 -> dbAccess.getItems(it, it1) } }
        if (items != null) {
            for(item in items){
                itemList = itemList + GridViewModal(item.id, item.name, item.pictureUrl, item.description)
            }
        }
        val itemAdapter = GridRVAdapter(itemList = itemList,this@RestaurantActivity)
        itemGRV.adapter = itemAdapter
        itemGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Toast.makeText(applicationContext, itemList[position].itemDescription, Toast.LENGTH_SHORT).show()
        }
    }

}