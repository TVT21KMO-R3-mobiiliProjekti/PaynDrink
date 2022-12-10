package com.example.payndrink

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.example.payndrink.data.Globals
import com.example.payndrink.database.DatabaseAccess
import com.example.payndrink.databinding.ActivityMenuBinding
import com.example.payndrink.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {
    private val dbAccess = DatabaseAccess()
    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        var bundle :Bundle ?=intent.extras
        var totalPrice : Double = bundle!!.getDouble("totalPrice")
        binding.tvTotalPrice.text = String.format("TOTAL PRICE: %.2f %s", totalPrice, "€")

        binding.btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED, Intent())
            finish()
        }

        binding.btnPay.setOnClickListener {
            //Emulate payment
            Toast.makeText(this@PaymentActivity, "Sending payment...", Toast.LENGTH_SHORT).show()
            Thread.sleep(1000)
            setResult(Activity.RESULT_OK, Intent())
            finish()
        }
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