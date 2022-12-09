package com.example.payndrink.data

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.payndrink.R
import kotlinx.android.synthetic.main.status_grid_item.view.*

class StatusItemAdapter ( private val statusItemList: List<StatusItem>): RecyclerView.Adapter<StatusItemAdapter.ViewHolder>() {
    inner class ViewHolder internal constructor( itemView: View):RecyclerView.ViewHolder(itemView){
        val tvName: TextView = itemView.tvName
        val tvOrderedQty: TextView = itemView.tvOrderedQty
        val tvRefundedQty: TextView = itemView.tvRefundedQty
        val tvDeliveriedQty: TextView = itemView.tvDeliveriedQty
        val card: CardView = itemView.cv_menu_item

    }

    private val items: MutableList<CardView>
    init{
        this.items = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.status_grid_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tvName.text = statusItemList[position].itemName
        holder.tvOrderedQty.text = statusItemList[position].orderedQty.toString()
        holder.tvRefundedQty.text = statusItemList[position].refundedQty.toString()
        holder.tvDeliveriedQty.text = statusItemList[position].deliveriedQty.toString()
        if (statusItemList[position].rejected) holder.card.setCardBackgroundColor(Color.RED)
        else if (statusItemList[position].deliveriedQty == statusItemList[position].orderedQty) holder.card.setCardBackgroundColor(Color.GREEN)
        else if (statusItemList[position].refundedQty > 0) holder.card.setCardBackgroundColor(Color.MAGENTA)
        else holder.card.setCardBackgroundColor(Color.WHITE)

        items.add(holder.card)
    }
    override fun getItemCount(): Int {
        return statusItemList.size
    }

}