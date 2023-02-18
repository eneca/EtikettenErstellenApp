package com.enca.etikettenerstellen.productList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.enca.etikettenerstellen.R
import com.enca.etikettenerstellen.data.Product
import com.enca.etikettenerstellen.data.createBarcodeBitmap

class ProductsAdapter(private val onClick: (Product) -> Unit) :
    ListAdapter<Product, ProductsAdapter.ProductViewHolder>(ProductDiffCallback) {

    /* ViewHolder for products, takes in the inflated view and the onClick behavior. */
    class ProductViewHolder(itemView: View, val onClick: (Product) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val productTextView: TextView = itemView.findViewById(R.id.product_text1)
        private val productTextView2: TextView = itemView.findViewById(R.id.product_text2)
        private val productTextView3: TextView = itemView.findViewById(R.id.product_text3)
        private val productImageView: ImageView= itemView.findViewById(R.id.imageView)
        private val productTextView4: TextView = itemView.findViewById(R.id.product_text4)
        private var currentProduct: Product? = null

        init {
            itemView.setOnClickListener {
                currentProduct?.let {
                    onClick(it)
                }
            }
        }

        /* Bind product informations and bitmap of barcode. */
        fun bind(product: Product) {
            currentProduct = product

            productTextView.text = product.name
            productTextView2.text = product.price
            productTextView3.text = product.pfand
            productImageView.setImageBitmap(createBarcodeBitmap(product.bcode,500,50))
            productTextView4.text = product.bcode
        }
    }

    /* Creates and inflates view and return ProductViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view, onClick)
    }

    /* Gets current product and uses it to bind view. */
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)

    }
}

object ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.bcode == newItem.bcode && oldItem.name == newItem.name
    }
}