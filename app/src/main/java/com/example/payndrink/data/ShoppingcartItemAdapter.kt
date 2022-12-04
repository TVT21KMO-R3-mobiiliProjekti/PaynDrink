package com.example.payndrink.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.payndrink.R
import kotlinx.android.synthetic.main.quick_item.view.*
import kotlinx.android.synthetic.main.shoppingcart_grid_item.view.*

class ShoppingcartItemAdapter (
    private val shoppingcartItemList: List<ShoppingcartItem>
): RecyclerView.Adapter<ShoppingcartItemAdapter.ViewHolder>() {
    private lateinit var mListener: onItemClickListener
    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    private val items: MutableList<CardView>
    init{
        this.items = ArrayList()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.shoppingcart_grid_item, parent, false)
        return ViewHolder(v, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = shoppingcartItemList[position].itemName
        holder.tvPrice.text = String.format("%.2f %s", shoppingcartItemList[position].itemPrice, "€")
        holder.tvQty.setText(String.format("%i",shoppingcartItemList[position].itemQty))
        items.add(holder.card)
    }
    override fun getItemCount(): Int {
        return shoppingcartItemList.size
    }

    inner class ViewHolder
    internal constructor(
        itemView: View,
        listener: onItemClickListener
    ):RecyclerView.ViewHolder(itemView){
        val tvName: TextView = itemView.name_text_view
        val tvPrice: TextView = itemView.tv_price
        val tvQty: EditText = itemView.edtQTY
        val card: CardView = itemView.cv_menu_item

        init{
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

}