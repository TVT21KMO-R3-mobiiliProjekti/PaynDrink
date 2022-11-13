package com.example.payndrink

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.payndrink.database.DatabaseAccess
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScannerSubActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var scannerView : ZXingScannerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZXingScannerView(this)
        setContentView(scannerView)

        // Check permissions
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        if(permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1)
        }
    }

    /** Result received -> Return and pass data to requested activity */
    override fun handleResult(p0: Result?) {
        val data = p0.toString()
        val intent = Intent(this, RestaurantActivity::class.java)

        //var result : Int = Activity.RESULT_OK
        if(!isNumeric(data)){
            Toast.makeText(this, "Invalid bar code", Toast.LENGTH_SHORT).show()
            finish()
            return
        } //Validate data - TESTAUKSEN AJAKSI OHITETTU
        //intent.putExtra("barcode", data)
        val dbAccess = DatabaseAccess()
        val connection = dbAccess.connectToDatabase()
        val restaurant = connection?.let { dbAccess.getRestaurantBySeating(it, data.toInt()) }
        if(restaurant != null){
            startActivity(intent.apply {
                putExtra("id", restaurant.id)
                putExtra("seat", data.toInt())
                putExtra("name", restaurant.name)
                putExtra("address", restaurant.address)
                putExtra("description", restaurant.description)
                putExtra("picture", restaurant.pictureUrl)
                putExtra("type", restaurant.typeID)
            })
        }
        else{
            Toast.makeText(this, "Invalid bar code", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    /** Check is string numeric */
    fun isNumeric(toCheck: String): Boolean {
        return toCheck.all { char -> char.isDigit() }
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
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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