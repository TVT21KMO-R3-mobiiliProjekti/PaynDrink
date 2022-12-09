package com.example.payndrink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.payndrink.data.Globals
import com.example.payndrink.data.Globals.Companion.ActiveOrderID
import com.example.payndrink.data.Globals.Companion.PaymentOK
import com.example.payndrink.data.ShoppingcartItem
import com.example.payndrink.data.ShoppingcartItemAdapter
import com.example.payndrink.database.DatabaseAccess
import com.example.payndrink.database.OrderHasItems
import kotlinx.android.synthetic.main.activity_shopping_cart.*
import java.sql.Connection

class ShoppingCartActivity : AppCompatActivity() {
    private val dbAccess = DatabaseAccess()
    private var connection: Connection? = null
    private var totalPrice: Double? = 0.0
    private lateinit var itemList: List<ShoppingcartItem>
    private lateinit var items: MutableList<OrderHasItems>
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: ShoppingcartItemAdapter
    private lateinit var bUpdate: Button
    private lateinit var bPay: Button
    private lateinit var bClear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        bUpdate = findViewById(R.id.btnUpdate)
        bPay = findViewById(R.id.btnPay)
        bClear = findViewById(R.id.btnClear)
        updateView()
        bUpdate.setOnClickListener{
            updateOrder()
        }
        if (PaymentOK) bPay.text = "Send Order"
        bPay.setOnClickListener{
            if (!PaymentOK) {
                val intent = Intent(applicationContext, PaymentActivity::class.java)
                intent.putExtra("totalPrice", totalPrice)
                paymentLauncher.launch(intent)
            }
            else sendOrder()
        }
        bClear.setOnClickListener{
            deleteOrder()
            finish()
        }
    }

    private fun updateView() {
        connection = dbAccess.connectToDatabase()
        itemList = ArrayList()
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        itemList = emptyList()
        items = ActiveOrderID?.let { connection?.let { it1 -> dbAccess.getItemsInOrder(it1, it) } }!!
        for(item in items){
            val price = connection?.let { dbAccess.getItemPrice(it, item.itemID) }
            itemList = itemList + ShoppingcartItem(item.itemID, item.itemName, item.quantity, price)
        }
        totalPrice = connection?.let { dbAccess.getOrderPrice(it, ActiveOrderID!!) }
        bPay.text = String.format("PAY %.2f %s", totalPrice, "€")
        adapter = ShoppingcartItemAdapter(itemList)
        rv_shoppingcart_items.layoutManager = layoutManager
        rv_shoppingcart_items.setHasFixedSize(true)
        rv_shoppingcart_items.adapter = adapter
        adapter.setOnItemClickListener(object: ShoppingcartItemAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
            }
        })
    }

    /** Update shopping cart changes to db */
    private fun updateOrder() {
        connection = dbAccess.connectToDatabase()
        // Update quantities (currently no matter if changed)
        for (item in itemList) {
            if (connection?.let {dbAccess.updateItemInOrder(it, item.itemQty, item.id, ActiveOrderID!!) } == null) {
                Toast.makeText(this@ShoppingCartActivity, "Updating shopping cart failed! Please try again later", Toast.LENGTH_LONG).show()
                return
            }
        }
        // Update total price to view
        totalPrice = connection?.let { dbAccess.getOrderPrice(it, ActiveOrderID!!) }
        bPay.text = String.format("PAY %.2f %s", totalPrice, "€")
        Toast.makeText(this@ShoppingCartActivity, "Shopping cart is now up to date", Toast.LENGTH_SHORT).show()
    }

    /** Delete all items from shopping cart */
    private fun deleteOrder() {
        var ret : Int = 0
        //Delete items (will finally also delete order)
        for (item in itemList) {
            ret = connection?.let { dbAccess.deleteItemInOrder(it, item.id, ActiveOrderID!!) }!!
        }
        if (ret < 0) {
            ActiveOrderID = null
            Toast.makeText(this@ShoppingCartActivity, "Shopping cart is now empty", Toast.LENGTH_SHORT).show()
        }
        else Toast.makeText(this@ShoppingCartActivity, "Clearing shopping cart failed!", Toast.LENGTH_LONG).show()
    }

    /** Start activity and handle payment activity results **/
    private var paymentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(this@ShoppingCartActivity, "Payment OK, Sending order...", Toast.LENGTH_SHORT).show()
            if (!sendOrder()) {
                //Payment OK, but sending failed -> Next time button will allow to try order directly
                PaymentOK = true
                bPay.text = "Send Order"
            }
        }
        else {
            Toast.makeText(this@ShoppingCartActivity, "Payment failed or canceled!", Toast.LENGTH_LONG).show()
        }
    }

    /** Set send order flag to DB, start status activity and finish this one */
    private fun sendOrder(): Boolean {
        val connection = dbAccess.connectToDatabase()
        var ret : Int = connection?.let {dbAccess.sendOrder(it, Globals.ActiveOrderID!!) }!!
        if (ret >= 0) {
            Globals.PendingOrderID = ActiveOrderID
            ActiveOrderID = null
            Toast.makeText(this@ShoppingCartActivity, "Order sent OK", Toast.LENGTH_SHORT).show()
            // Launch status polling
            val intent = Intent(applicationContext, StatusActivity::class.java)
            finish() //Kill this activity
            startActivity(intent)
        }
        else Toast.makeText(this@ShoppingCartActivity, "Sending order failed!", Toast.LENGTH_LONG).show()
        return ret >= 0
    }

    /** Handle navigate back button */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED, Intent())
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
