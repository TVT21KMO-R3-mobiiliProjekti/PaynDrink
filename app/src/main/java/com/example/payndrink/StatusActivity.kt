package com.example.payndrink

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.payndrink.data.*
import com.example.payndrink.data.Globals.Companion.TrackedOrderIDs
import com.example.payndrink.database.DatabaseAccess
import com.example.payndrink.database.Order
import com.example.payndrink.database.OrderHasItems
import com.example.payndrink.databinding.ActivityStatusBinding
import kotlinx.android.synthetic.main.activity_status.*
import kotlinx.coroutines.*
import java.sql.Connection

class StatusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatusBinding
    private val scope = CoroutineScope(newSingleThreadContext("Polling"))
    private val dbAccess = DatabaseAccess()
    private var connection: Connection? = null
    private var order: Order? = null
    private var activeOrderIdx: Int = 0
    private var coroutineOrderIdx: Int = 0
    private lateinit var itemList: List<StatusItem>
    private lateinit var items: MutableList<OrderHasItems>
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: StatusItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_status_items.layoutManager = layoutManager
        rv_status_items.setHasFixedSize(true)

        btnMinus.setOnClickListener {
            //Is this actually coroutine / thread-safe??
            if(activeOrderIdx > 0) {
                activeOrderIdx--
                tvOrderID.text = (activeOrderIdx + 1).toString()
            }
        }
        btnPlus.setOnClickListener {
            //Is this actually coroutine / thread-safe??
            if(activeOrderIdx < TrackedOrderIDs.count() - 1) {
                activeOrderIdx++
                tvOrderID.text = (activeOrderIdx + 1).toString()
            }
        }

        if (TrackedOrderIDs.isEmpty()) {
            Toast.makeText(this@StatusActivity, "No pending orders!", Toast.LENGTH_SHORT).show()
        }
        else {
            activeOrderIdx = TrackedOrderIDs.count() - 1
            coroutineOrderIdx = activeOrderIdx
            tvOrderID.text = (activeOrderIdx + 1).toString()
            updateView()
        }
    }

    /** Start coroutine for database polling and UI updating */
    private fun updateView() {
        scope.launch(Dispatchers.IO) {
            var noDelay: Boolean = false
            while(scope.isActive) {
                //Get data from DB
                if (connection == null) connection = dbAccess.connectToDatabase()
                order = TrackedOrderIDs[coroutineOrderIdx].let { connection?.let { it1 -> dbAccess.getPlacedOrder(it1, it)}}
                itemList = emptyList()
                if (order != null) {
                    items = TrackedOrderIDs[coroutineOrderIdx].let { connection?.let { it1 -> dbAccess.getItemsInOrder(it1, it)}}!!
                    for(item in items){
                        itemList = itemList + StatusItem(item.itemID, item.itemName, item.quantity, item.refunded, item.delivered, order!!.rejected!! > 0)
                    }
                }
                //Set adapter
                adapter = StatusItemAdapter(itemList)
                //Dispatch changes to UI
                withContext(Dispatchers.Main) {
                   noDelay = coroutineOrderIdx != activeOrderIdx
                    coroutineOrderIdx = activeOrderIdx

                    if (order != null) {
                        if (order!!.fulfilled!! > 0) {
                            tvOrderStatus.text = "General status: Deliveried"
                            tvOrderStatus.setTextColor(Color.CYAN)
                            tvOrderMessage.text = "This order is deliveried"
                        }
                        else if (order!!.rejected!! > 0) {
                            tvOrderStatus.text = "General status: REJECTED!"
                            tvOrderStatus.setTextColor(Color.RED)
                            tvOrderMessage.text = order!!.rejectReason
                        }
                        else if (order!!.refund!! > 0) {
                            tvOrderStatus.text = "General status: Refunded!"
                            tvOrderStatus.setTextColor(Color.MAGENTA)
                            tvOrderMessage.text = "Restaurant refunded some of order! " + order!!.refundReason
                        }
                        else if (order!!.accepted!! > 0) {
                            tvOrderStatus.text = "General status: Accepted"
                            tvOrderStatus.setTextColor(Color.GREEN)
                            val simpleDateFormat = SimpleDateFormat("H:mm")
                            val timeString = simpleDateFormat.format(order!!.exceptedDelivery)
                            tvOrderMessage.text = "Waiter accepted order. Please wait (estimated delivery time: $timeString)"
                        }
                    }
                    adapter.notifyDataSetChanged()
                    rv_status_items.adapter = adapter
                }
                //Polling delay
                if(!noDelay) delay(STATUS_POLLING_INTERVAL)
            }
        }
    }

    /** On activity destroy stop the coroutine */
    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
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