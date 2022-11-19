package com.example.payndrink

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScannerSubActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var scannerView : ZXingScannerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZXingScannerView(this)
        setContentView(scannerView)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Check permissions
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        if(permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1)
        }
    }

    /** Result received -> Return and pass data to requested activity */
    override fun handleResult(p0: Result?) {
        val data = p0.toString()
        val intent = Intent()
        val result : Int = Activity.RESULT_OK
        intent.putExtra("barcode", data)
        setResult(result, intent)
        finish()
    }

    /** On resume -> Start camera */
    override fun onResume() {
        super.onResume()
        scannerView?.setResultHandler(this)
        //scannerView?.setFormats(listOf(BarcodeFormat.QR_CODE)) // Scan only QR-Codes - TESTAUKSEN AJAKSI OHITETTU
        scannerView?.startCamera()
    }

    /** On stop -> Stop camera */
    override fun onStop() {
        super.onStop()
        scannerView?.stopCamera()
        onBackPressed()
    }

    /** Handle camera permission request result */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            1 -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(applicationContext, "You need camera permission!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}