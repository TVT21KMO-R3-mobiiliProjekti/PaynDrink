package com.example.payndrink

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.payndrink.data.Globals
import com.example.payndrink.data.Globals.Companion.TrackedOrderIDs
import com.example.payndrink.data.StatusItem
import com.example.payndrink.data.StatusItemAdapter
import com.example.payndrink.database.*
import com.example.payndrink.databinding.ActivityStatusBinding
import kotlinx.android.synthetic.main.activity_status.*
import kotlinx.coroutines.*
import java.sql.Connection

class StatusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatusBinding
    private val scope =  CoroutineScope(newSingleThreadContext("Polling"))
    private lateinit var job: Job
    private val dbAccess = DatabaseAccess()
    private var connection: Connection? = null
    private var activeOrderIdx: Int = 0
    private lateinit var itemList: List<StatusItem>
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
            runBlocking {
                if(activeOrderIdx > 0) {
                    if (job.isActive) job.cancelAndJoin()
                    activeOrderIdx--
                    tvOrderID.text = String.format("%s", (activeOrderIdx + 1).toString())
                    updateView()
                }
            }
        }
        btnPlus.setOnClickListener {
            runBlocking {
                if(activeOrderIdx < TrackedOrderIDs.count() - 1) {
                    if (job.isActive) job.cancelAndJoin()
                    activeOrderIdx++
                    tvOrderID.text = String.format("%s", (activeOrderIdx + 1).toString())
                    updateView()
                }
            }
        }
        btnDelete.setOnClickListener {
            runBlocking {
                if (job.isActive) job.cancelAndJoin()
                TrackedOrderIDs.removeAt(activeOrderIdx)
                Globals().savePreferences()
                if(TrackedOrderIDs.count() == 0) {
                    Toast.makeText(this@StatusActivity, "No pending orders! Returning...", Toast.LENGTH_LONG).show()
                    finish()
                }
                else {
                    activeOrderIdx = TrackedOrderIDs.count() - 1
                    tvOrderID.text = String.format("%s", (activeOrderIdx + 1).toString())
                    updateView()
                }
            }
        }

        if (TrackedOrderIDs.isEmpty()) {
            Toast.makeText(this@StatusActivity, "No pending orders! Returning...", Toast.LENGTH_LONG).show()
            finish()
        }
        else {
            activeOrderIdx = TrackedOrderIDs.count() - 1
            tvOrderID.text = String.format("%s", (activeOrderIdx + 1).toString())
            updateView()
        }
    }

    /** Start coroutine for database polling and UI updating */
    private fun updateView() {
        job = scope.launch(Dispatchers.IO) {
            var restaurant:Restaurant? = null
            var order: Order?
            var waiter: Waiter? = null
            while(job.isActive) {
                //Get data from DB
                if (connection == null) connection = dbAccess.connectToDatabase()
                order = TrackedOrderIDs[activeOrderIdx].let { connection?.let { it1 -> dbAccess.getPlacedOrder(it1, it)}}
                itemList = emptyList()
                if(order != null) {
                    val items: MutableList<OrderHasItems> = TrackedOrderIDs[activeOrderIdx].let { connection?.let { it1 -> dbAccess.getItemsInOrder(it1, it)}}!!
                    restaurant = order.restaurantID.let { connection?.let { it1 -> dbAccess.getRestaurant(it1, it)}}
                    if(order.waiterID != null) waiter = order.waiterID.let { connection?.let { it1 -> dbAccess.getWaiter(it1, it!!)}}
                    for(item in items){
                        val statusItem = StatusItem(item.itemID, item.itemName, item.quantity, item.refunded, item.delivered, order.rejected!! > 0)
                        if(!itemList.contains(statusItem)) {
                            itemList = itemList + statusItem
                        }
                    }
                }
                //Set adapter
                adapter = StatusItemAdapter(itemList)
                //Dispatch changes to UI
                withContext(Dispatchers.Main) {
                    if (order != null) {
                        val simpleDateFormat = SimpleDateFormat("E d-M-y  H:mm")
                        tvRestaurant.text = restaurant!!.name
                        tvOrderPlaced.text = simpleDateFormat.format(order.placed)
                        tvSeatID.text = "(seat id: ${order.seatingID.toString()})"
                        tvPrice.text = String.format("Paid: %.2f%s", order.price, "€")
                        if (order.rejected!! > 0) {
                            val simpleDateFormat = SimpleDateFormat("H:mm")
                            val timeString = simpleDateFormat.format(order.rejected)
                            tvOrderStatus.text = getString(R.string.status_rejected) + " at $timeString!"
                            tvOrderStatus.setTextColor(Color.RED)
                            tvOrderMessage.text = order.rejectReason
                        }
                        else if (order.refund!! > 0) {
                            tvPrice.text = tvPrice.text.toString() + String.format(" / Refunded: %.2f%s", order.refund, "€")
                            tvOrderStatus.text = getString(R.string.status_refunded)
                            tvOrderStatus.setTextColor(Color.MAGENTA)
                            tvOrderMessage.text = String.format("%s %s", "Restaurant refunded item(s): ", order.refundReason)
                        }
                        else if (order.fulfilled!! > 0) {
                            val simpleDateFormat = SimpleDateFormat("H:mm")
                            val timeString = simpleDateFormat.format(order.fulfilled)
                            tvOrderStatus.text = getString(R.string.status_delivered) + " at $timeString"
                            tvOrderStatus.setTextColor(Color.parseColor("#00B000"))
                            tvOrderMessage.text = getString(R.string.order_delivered)
                        }
                        else if (order.accepted!! > 0) {
                            tvOrderStatus.text = getString(R.string.status_accepted)
                            tvOrderStatus.setTextColor(Color.parseColor("#00800C"))
                            val simpleDateFormat = SimpleDateFormat("H:mm")
                            val timeString = simpleDateFormat.format(order.exceptedDelivery)
                            var waiterName: String = waiter!!.firstName!!
                            tvOrderMessage.text = "Waiter $waiterName accepted your order. Please wait (estimated delivery time: $timeString)"
                        }
                        else{
                            tvOrderStatus.text = getString(R.string.status_waiting)
                            tvOrderStatus.setTextColor(Color.parseColor("#800080"))
                            tvOrderMessage.text = getString(R.string.waiting_waiter)
                        }
                    }
                    rv_status_items.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
                //Polling delay
                delay(STATUS_POLLING_INTERVAL)
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