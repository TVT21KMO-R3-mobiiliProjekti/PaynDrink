package com.example.payndrink.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.payndrink.R
import kotlinx.android.synthetic.main.quick_item.view.*

class QuickItemAdapter (
    private val quickItemList: List<GridViewMenuItem>
): RecyclerView.Adapter<QuickItemAdapter.ViewHolder>() {
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
            .inflate(R.layout.quick_item, parent, false)
        return ViewHolder(v, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = quickItemList[position].itemName
        holder.tvPrice.text = quickItemList[position].itemPrice.toString()
        //holder.ivItem.setImageBitmap(BitmapFactory.decodeStream(URL(quickItemList[position].itemUrl).openConnection().getInputStream()))
        holder.ivItem.setImageBitmap(quickItemList[position].image)
        items.add(holder.card)
    }
    override fun getItemCount(): Int {
        return quickItemList.size
    }

    inner class ViewHolder
    internal constructor(
        itemView: View,
        listener: onItemClickListener
    ):RecyclerView.ViewHolder(itemView){
        val tvName: TextView = itemView.tv_name_quick
        val tvPrice: TextView = itemView.tv_price_quick
        val ivItem: ImageView = itemView.icons_quick
        val card: CardView = itemView.cv_quick_item

        init{
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

}
