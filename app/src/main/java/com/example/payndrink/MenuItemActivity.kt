package com.example.payndrink

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.payndrink.data.Utilities
import com.example.payndrink.databinding.ActivityMenuItemBinding
import kotlinx.android.synthetic.main.activity_menu_item.*

const val MAX_QTY : Int = 100
const val STATUS_POLLING_INTERVAL : Long = 5000      //in milliseconds

class MenuItemActivity : AppCompatActivity() {

    private var itemID : Int = -1
    private var quantity : Int = 1
    private lateinit var binding : ActivityMenuItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        /** Get values from the calling activity */
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            itemID = bundle.getInt("id", -1)
            quantity = bundle.getInt("qty", 1)
            binding.txtName.text = bundle.getString("name")
            binding.txtDescription.text = bundle.getString("description")
            binding.txtPrice.text = String.format("%.2f%s", bundle.getDouble("price"), "€")
            binding.imgItem.setImageBitmap(Utilities().getImageBitmapFromURL(bundle.getString("pictureUrl")))
        }

        //Set default quantity
        edtQTY.setText("$quantity")

        /** Button Minus clicked -> Decrease quantity */
        binding.btnMinus.setOnClickListener{
            if (quantity >= 1) {
                quantity -= 1
                edtQTY.setText("$quantity")
            }
        }

        /** Button Plus clicked -> Increase quantity */
        binding.btnPlus.setOnClickListener{
            if (quantity <= MAX_QTY) {
                quantity += 1
                edtQTY.setText("$quantity")
            }
        }

        /** Edit qty text changed -> Validate user entry */
        binding.edtQTY.doOnTextChanged { text, _, _, count ->
            if (count > 0) {
                if(!Utilities().isNumeric(text.toString()) || text.toString().toInt() < 0 || text.toString().toInt() > MAX_QTY) {
                    quantity = 1
                    edtQTY.setText("$quantity")
                    Toast.makeText(this@MenuItemActivity, "Please enter quantity between 0 and $MAX_QTY", Toast.LENGTH_SHORT).show()
                }
                else quantity = text.toString().toInt()
            }
        }

        /** Button Order clicked -> Finnish activity and send values to the calling activity */
        binding.btnOrder.setOnClickListener {
            val intent = Intent()
            val result : Int = Activity.RESULT_OK
            intent.putExtra("id", itemID)
            intent.putExtra("qty", quantity)
            intent.putExtra("name", binding.txtName.text)
            setResult(result, intent)
            finish()
        }

        /** Button Cancel clicked -> Finish activity */
        binding.btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED, Intent())
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
