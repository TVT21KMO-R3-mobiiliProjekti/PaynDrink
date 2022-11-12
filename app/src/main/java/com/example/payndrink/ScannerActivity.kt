package com.example.payndrink


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.payndrink.databinding.ActivityMainBinding
import com.example.payndrink.databinding.ActivityScannerBinding

class ScannerActivity : AppCompatActivity() {

    companion object {
        const val RESULT = "RESULT"
    }

    private lateinit var binding : ActivityScannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnScan.setOnClickListener {
            val intent = Intent(applicationContext, ScannerSubActivity::class.java)
            startActivity(intent)
        }

        val result = intent.getStringExtra(RESULT)

        if(result != null) {
            binding.result.text = result.toString()
            //Viedään main activityyn koodi
        }



        /** Skip to main activity */
        val btnSkip = findViewById<Button>(R.id.btnSkip)
        btnSkip.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }


}