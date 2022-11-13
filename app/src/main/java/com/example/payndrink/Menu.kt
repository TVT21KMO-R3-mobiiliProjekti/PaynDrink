package com.example.payndrink

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView

class Menu : AppCompatActivity() {

    private var gridView:GridView ? = null
    private var arrayList:ArrayList<MenuItem> ? = null
    private var menuAdapter:MenuAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        gridView = findViewById(R.id.my_grid_view)
        arrayList = ArrayList()
        menuAdapter = MenuAdapter(applicationContext, arrayList!!)
        gridView?.adapter = menuAdapter

        }

        private fun setDataList() :ArrayList<MenuItem>{
            var arrayList:ArrayList<MenuItem> = ArrayList()

            arrayList.add(MenuItem(R.drawable.qr_scanner, "Kalja"))
            arrayList.add(MenuItem(R.drawable.qr_scanner, "Siideri"))
            arrayList.add(MenuItem(R.drawable.qr_scanner, "Lonkero"))
            arrayList.add(MenuItem(R.drawable.qr_scanner, "Viini"))

            return arrayList
        }

    }

