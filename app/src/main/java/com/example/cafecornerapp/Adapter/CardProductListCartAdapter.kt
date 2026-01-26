package com.example.cafecornerapp.Adapter

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cafecornerapp.Activity.MainActivity
import com.example.cafecornerapp.Domain.ProductModel
import com.example.cafecornerapp.ViewModel.CartViewModel
import com.example.cafecornerapp.databinding.ViewHolderCardproductBinding

class CardProductListCartAdapter(val items: MutableList<ProductModel>):
    RecyclerView.Adapter<CardProductListCartAdapter.Viewholder>() {
    private val viewModel = CartViewModel()
    lateinit var context: Context
    class Viewholder(val binding: ViewHolderCardproductBinding):
        RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardProductListCartAdapter.Viewholder {
        context= parent.context
        val binding = ViewHolderCardproductBinding.
        inflate(LayoutInflater.from(context),parent,false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: CardProductAdapter.Viewholder, position: Int) {
        holder.binding.titleTxt.text= items[position].nama_product
            .replaceFirstChar { it.uppercase() }
        holder.binding.priceTxt.text="$"+items[position].harga_product.toString()
        holder.binding.subtitleTxt.text= items[position].deskripsi_product.toString().take(50)
            .replaceFirstChar { it.uppercase() } + "..."

        Glide.with(context).load(items[position].imgUrl).into(holder.binding.pic)

        holder.binding.deleteBtn.setOnClickListener {
            viewModel.deleteProduct(items[position].documentId)
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(context, MainActivity::class.java)
                ContextCompat.startActivity(context, intent, null)
            }, 500)
        }


        holder.itemView.setOnClickListener {
//            val intent = Intent(context, DetailActivity::class.java)
//            intent.putExtra("object", items[position])
//            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int =items.size
}