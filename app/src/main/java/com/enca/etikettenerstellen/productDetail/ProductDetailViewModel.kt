package com.enca.etikettenerstellen.productDetail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.enca.etikettenerstellen.data.DataSource
import com.enca.etikettenerstellen.data.Product

class ProductDetailViewModel(private val datasource: DataSource) : ViewModel() {

    /* Queries datasource to returns a product that corresponds to an bcode. */
    fun getProductForBcode(bcode: String) : Product? {
        return datasource.getProductForBcode(bcode)
    }

    /* Queries datasource to edit product */
    fun editProduct(product: Product){
        datasource.editProduct(product)
    }

    /* Queries datasource to remove a product. */
    fun removeProduct(product: Product) {
        datasource.removeProduct(product)
    }
}

class ProductDetailViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductDetailViewModel(
                datasource = DataSource.getDataSource(context.resources)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}