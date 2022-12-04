package com.example.payndrink

import android.os.Bundle
import android.widget.*
import android.app.Activity
import android.content.Intent
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.payndrink.data.*
import com.example.payndrink.data.Globals.Companion.ActiveOrderID
import com.example.payndrink.data.GridRVAdapter
import com.example.payndrink.database.DatabaseAccess
import com.example.payndrink.database.Item
import com.example.payndrink.database.OrderHasItems
import com.example.payndrink.database.Restaurant
import com.example.payndrink.databinding.ActivityMainBinding
import com.example.payndrink.databinding.ActivityMenuBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.activity_shoppingcart.*
import java.sql.Connection

class ShoppingcartActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMenuBinding

    private val dbAccess = DatabaseAccess()
    private var connection: Connection? = null
    private var shoppingCart: OrderHasItems? = null
    private var orderID: Int? = null
    private lateinit var itemList: List<ShoppingcartItem>
    private lateinit var items: MutableList<ShoppingcartItem>
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: ShoppingcartItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateView()
        addOrderItems()
    }

    private fun updateView() {
        connection = dbAccess.connectToDatabase()
        itemList = ArrayList()
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


    }


    private fun addOrderItems() {
        itemList = emptyList()
        for(item in items){
            itemList = itemList + ShoppingcartItem(item.id, item.itemName, item.itemQty, item.itemPrice)

        }
        adapter = ShoppingcartItemAdapter(itemList)
        rv_shoppingcart_items.layoutManager = layoutManager
        rv_shoppingcart_items.setHasFixedSize(true)
        rv_shoppingcart_items.adapter = adapter
        adapter.setOnItemClickListener(object: ShoppingcartItemAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
            }
        })
    }

}

