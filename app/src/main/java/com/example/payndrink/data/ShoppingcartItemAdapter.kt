package com.example.payndrink.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.payndrink.MAX_QTY
import com.example.payndrink.R
import kotlinx.android.synthetic.main.shoppingcart_grid_item.view.*

class ShoppingcartItemAdapter (
    private val shoppingcartItemList: List<ShoppingcartItem>
): RecyclerView.Adapter<ShoppingcartItemAdapter.ViewHolder>() {
    private lateinit var mListener: OnItemClickListener
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
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
        var rec : Boolean = false
        holder.tvName.text = shoppingcartItemList[position].itemName
        var price: Double? = shoppingcartItemList[position].itemQty.let {
            shoppingcartItemList[position].itemPrice?.times(it)
        }
        holder.tvPrice.text = String.format("%.2f %s", price, "€")
        holder.tvQty.setText(shoppingcartItemList[position].itemQty.toString())
        holder.bPlus.setOnClickListener{
            if(shoppingcartItemList[position].itemQty < MAX_QTY) {
                shoppingcartItemList[position].itemQty =
                    shoppingcartItemList[position].itemQty.plus(1)
                rec = true
                holder.tvQty.setText(shoppingcartItemList[position].itemQty.toString())
                rec = false
                price = price?.plus(shoppingcartItemList[position].itemPrice!!)
                holder.tvPrice.text = String.format("%.2f %s", price, "€")
            }
        }
        holder.bMinus.setOnClickListener{
            if(shoppingcartItemList[position].itemQty > 0) {
                shoppingcartItemList[position].itemQty =
                    shoppingcartItemList[position].itemQty.minus(1)
                rec = true
                holder.tvQty.setText(shoppingcartItemList[position].itemQty.toString())
                rec = false
                price = price?.minus(shoppingcartItemList[position].itemPrice!!)
                holder.tvPrice.text = String.format("%.2f %s", price, "€")
            }
        }
        holder.tvQty.doOnTextChanged { text, _, _, count ->
            if (!rec) {
                if (count > 0 && Utilities().isNumeric(text.toString()) && text.toString().toInt() >= 0 && text.toString().toInt() <= MAX_QTY) {
                    shoppingcartItemList[position].itemQty = text.toString().toInt()
                    price = shoppingcartItemList[position].itemPrice?.times(text.toString().toInt())
                    holder.tvPrice.text = String.format("%.2f %s", price, "€")
                }
                else {
                    rec = true
                    holder.tvQty.setText(shoppingcartItemList[position].itemQty.toString())     //not a valid value
                    rec = false
                }
            }
        }
        items.add(holder.card)
    }
    override fun getItemCount(): Int {
        return shoppingcartItemList.size
    }

    inner class ViewHolder
    internal constructor(
        itemView: View,
        listener: OnItemClickListener
    ):RecyclerView.ViewHolder(itemView){
        val tvName: TextView = itemView.name_text_view
        val tvPrice: TextView = itemView.tv_price
        val tvQty: EditText = itemView.edtQTY
        val bPlus: Button = itemView.btnPlus
        val bMinus: Button = itemView.btnMinus
        val card: CardView = itemView.cv_menu_item

        init{
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

}