package com.example.payndrink

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.payndrink.data.Globals.Companion.ActiveOrderID
import com.example.payndrink.data.ShoppingcartItem
import com.example.payndrink.data.ShoppingcartItemAdapter
import com.example.payndrink.database.DatabaseAccess
import com.example.payndrink.database.OrderHasItems
import com.example.payndrink.databinding.ActivityMenuBinding
import kotlinx.android.synthetic.main.activity_shopping_cart.*
import java.sql.Connection

class ShoppingCartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    private val dbAccess = DatabaseAccess()
    private var connection: Connection? = null
    private lateinit var itemList: List<ShoppingcartItem>
    private lateinit var items: MutableList<OrderHasItems>
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: ShoppingcartItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)
        updateView()
    }

    private fun updateView() {
        connection = dbAccess.connectToDatabase()
        itemList = ArrayList()
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        itemList = emptyList()
        items = ActiveOrderID?.let { connection?.let { it1 -> dbAccess.getItemsInOrder(it1, it) } }!!
        for(item in items){
            val price = connection?.let { dbAccess.getItemPrice(it, item.itemID) }
            itemList = itemList + ShoppingcartItem(item.id, item.itemName, item.quantity, price)
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
