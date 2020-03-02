package com.example.kebunsehati.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kebunsehati.R
import com.example.kebunsehati.model.DataItem
import com.example.kebunsehati.model.ProdukItem
import kotlinx.android.synthetic.main.card_item.view.*

class ItemCategoryAdapter(private val dataItem: List<ProdukItem>,
                          private val onAddListener: (ProdukItem,Int)->Unit): RecyclerView
        .Adapter<ItemCategoryAdapter.ItemViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder = ItemViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.card_item,parent,false)
    )

    override fun getItemCount(): Int = dataItem.size

    override fun onBindViewHolder(holder: ItemCategoryAdapter.ItemViewHolder, position: Int) {
        holder.bind(dataItem[position],onAddListener)
    }

    inner class ItemViewHolder(view: View):RecyclerView.ViewHolder(view){
        fun bind(dataItem: ProdukItem, onAddListener: (ProdukItem, Int) -> Unit){
            itemView.tv_item_name.text = dataItem.namaProduk.toString()
            itemView.tv_item_price.text = dataItem.harga.toString()
            itemView.tv_item_quantity.text = dataItem.qty.toString()
            itemView.ib_chart.setOnClickListener { onAddListener(dataItem,adapterPosition) }
        }
    }
}