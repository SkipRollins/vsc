package com.github.skiprollins.vsc.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

    fun addItem(product: Product) {
        items.add(product)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        if (position >= 0 || position < items.size) {
            items.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        return CartItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false))
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

class CartItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    val name: TextView = itemView.findViewById(R.id.cart_item_name)
    val price: TextView = itemView.findViewById(R.id.cart_item_price)
    val id: TextView = itemView.findViewById(R.id.cart_item_id)
    val qrCode: TextView = itemView.findViewById(R.id.cart_item_code)
    val image: TextView = itemView.findViewById(R.id.cart_item_image)

    fun bind(product: Product) {
        name.text = product.name
        price.text = product.price
        id.text = product.id
        qrCode.text = product.qrUrl
        image.text = product.thumbnail
    }
}