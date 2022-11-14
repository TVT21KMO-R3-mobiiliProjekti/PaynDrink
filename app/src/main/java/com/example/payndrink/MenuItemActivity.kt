package com.example.payndrink

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.payndrink.database.Item
import com.example.payndrink.databinding.ActivityMenuItemBinding
import java.net.URL

class MenuItemActivity : AppCompatActivity() {

    private var itemID : Int = -1
    private lateinit var binding : ActivityMenuItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            itemID = bundle.getInt("id")
            binding.txtName.text = bundle.getString("name")
            binding.txtDescription.text = bundle.getString("description")
            binding.txtPrice.text = bundle.getDouble("price").toString().format(2) + "€"
            val bitmap : Bitmap = BitmapFactory.decodeStream(URL(bundle.getString("pictureUrl")).openConnection().getInputStream())
            binding.imgItem.setImageBitmap(bitmap)
        }

        binding.btnOrder.setOnClickListener {
            val intent = Intent()
            var result : Int = Activity.RESULT_OK
            val qty = binding.edtQTY.text.toString().toInt()
            intent.putExtra("id", itemID)
            intent.putExtra("qty", qty)
            setResult(result, intent)
            finish()
        }

        binding.btnCancel.setOnClickListener {
            val intent = Intent()
            var result : Int = Activity.RESULT_CANCELED
            intent.putExtra("id", itemID)
            intent.putExtra("qty", 0)
            setResult(result, intent)
            finish()
        }
    }


}
