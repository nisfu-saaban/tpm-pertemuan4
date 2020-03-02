package com.example.kebunsehati.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kebunsehati.R
import com.example.kebunsehati.model.Kategori_Member
import com.example.kebunsehati.model.ListCategory
import com.example.kebunsehati.model.ProdukItem
import com.example.kebunsehati.model.ProdukResponse
import kotlinx.android.synthetic.main.category_list.view.*

class CategoryAdapter(private val category: ArrayList<ProdukResponse>,
                      private val listener: (ProdukResponse,Int)-> Unit): RecyclerView
                .Adapter<CategoryAdapter.CategoryHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): CategoryHolder = CategoryHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.category_list,parent,false))

    override fun getItemCount(): Int = category.size

    override fun onBindViewHolder(holder: CategoryAdapter.CategoryHolder, position: Int) {
        holder.bind(category[position],listener)
    }

    inner class CategoryHolder(view: View): RecyclerView.ViewHolder(view){
        fun bind(produkResponse: ProdukResponse,onListener:(ProdukResponse,Int)->Unit){
            itemView.tv_category.text = produkResponse.namaKategori
            itemView.setOnClickListener { onListener(produkResponse,adapterPosition) }
        }
    }

}