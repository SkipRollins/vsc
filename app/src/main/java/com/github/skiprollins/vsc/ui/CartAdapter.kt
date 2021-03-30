package com.github.skiprollins.vsc.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.skiprollins.vsc.R
import com.github.skiprollins.vsc.network.Product

class CartAdapter(
    private val context: Context
): RecyclerView.Adapter<CartItemViewHolder>() {

    private val items = ArrayList<Product>()

    fun setItems(products: List<Product>) {
        items.clear()
        items.addAll(products)
        notifyDataSetChanged()
    }

    fun addItem(product: Product): Int {
        items.add(product)
        notifyItemInserted(items.size - 1)
        return items.size - 1
    }

    private fun removeItem(position: Int) {
        if (position >= 0 || position < items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        return CartItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false))
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        holder.bind(context, items[position]) {
            val index = items.indexOfFirst { it === holder.product }
            if (index >= 0) {
                items.removeAt(index)
                notifyItemRemoved(index)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}

class CartItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    lateinit var product: Product
    val thumbnail: ImageView = itemView.findViewById(R.id.cart_thumbnail)
    val name: TextView = itemView.findViewById(R.id.cart_item_name)
    val price: TextView = itemView.findViewById(R.id.cart_item_price)
    val remove: ImageView = itemView.findViewById(R.id.cart_item_remove)

    fun bind(context: Context, product: Product, closeAction: View.OnClickListener) {
        this.product = product
        name.text = product.name
        price.text = product.price

        Glide
            .with(context)
            .load(product.thumbnail)
            .placeholder(android.R.drawable.screen_background_light_transparent)
            .error(android.R.drawable.screen_background_light_transparent)
            .centerCrop()
            .into(thumbnail)


        remove.setOnClickListener(closeAction)
    }
}