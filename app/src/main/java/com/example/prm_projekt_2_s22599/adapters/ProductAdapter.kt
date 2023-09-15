package com.example.prm_projekt_2_s22599.adapters

import android.content.ContentValues.TAG
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.HandlerCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.prm_projekt_2_s22599.databinding.ListItemBinding
import com.example.prm_projekt_2_s22599.model.Product


class ProductViewHolder(val binding: ListItemBinding)
    : RecyclerView.ViewHolder(binding.root){

    fun bind(product: Product){
        binding.name.text = product.name
        binding.address.text = product.address
        val bitmap = BitmapFactory.decodeFile(product.path)
        binding.imageProduct.setImageBitmap(bitmap)
    }
}

class ProductAdapter : RecyclerView.Adapter<ProductViewHolder>() {

    private val data = mutableListOf<Product>()
    private val handler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())
    var onItemClick: (Long) -> Unit = { }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding).also {vh ->
            binding.root.setOnClickListener {
                onItemClick(data[vh.layoutPosition].id)
            }
        }
    }

    override fun getItemCount(): Int = data.size


    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun replace(newData: List<Product>){
        data.clear()
        data.addAll(newData)

        handler.post{
            notifyDataSetChanged()
        }

    }

    fun removeItem(layoutPosition: Int): Product {
        val product = data.removeAt(layoutPosition)
        notifyDataSetChanged()
        return product
    }

}