package com.example.payndrink

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView


class MenuAdapter(var context: Context, var arrayList: ArrayList<com.example.payndrink.MenuItem>): BaseAdapter(){


    override fun getItem(p0: Int): Any {
       return arrayList.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }
    override fun getCount(): Int {
        return arrayList.size
    }
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var view:View = View.inflate(context, R.layout.grid_item_list, null)

        var icons:ImageView = view.findViewById(R.id.icons)
        var names:TextView = view.findViewById(R.id.name_text_view)

        var menuItem: com.example.payndrink.MenuItem = arrayList.get(p0)

        icons.setImageResource(menuItem.icons!!)
        names.text = menuItem.name

        return view
    }


}