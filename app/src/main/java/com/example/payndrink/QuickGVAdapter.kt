package com.example.payndrink

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.payndrink.data.GridViewModal
import java.net.URL


internal class QuickGVAdapter (
    private val quickList: List<GridViewModal>,
    private val context: Context

) :
    BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var itemTV: TextView
    private lateinit var priceTV: TextView
    private lateinit var itemIV: ImageView

    override fun getCount(): Int {
        return quickList.size
    }

    override fun getItem(position: Int): Any {
        return quickList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        if(layoutInflater == null){
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if(convertView == null){
            convertView = layoutInflater!!.inflate(R.layout.grid_item_list, null)
        }
        itemIV = convertView!!.findViewById((R.id.icons))
        itemTV = convertView!!.findViewById(R.id.name_text_view)
        priceTV = convertView!!.findViewById(R.id.tv_price)
        val bitmap = BitmapFactory.decodeStream(URL(quickList[position].itemUrl).openConnection().getInputStream())
        itemIV.setImageBitmap(bitmap)
        itemTV.text = quickList[position].itemName
        priceTV.text = quickList[position].itemPrice.toString()
        return convertView
    }
}