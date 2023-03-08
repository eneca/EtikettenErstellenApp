package com.enca.etikettenerstellen.productList

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.enca.etikettenerstellen.data.DataSource
import com.enca.etikettenerstellen.data.Product

class ProductsListViewModel(val dataSource: DataSource) : ViewModel() {

    val productsLiveData = dataSource.getProductList()

    /* If the name and description are present, create new product and add it to the datasource */
    fun insertProduct(productName: String?, productPrice : String?, productPfand : String?, productBcode : String?) {
        if (productName == null || productPrice == null|| productPfand==null|| productBcode == null) {
            return
        }
        val newProduct = Product(
            productName,
            productPrice,
            productPfand,
            productBcode
        )

        dataSource.addProduct(newProduct)
    }

    /* Delete list in datasource */
    fun deleteList() {
        dataSource.deleteList()
    }
}

class ProductsListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductsListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductsListViewModel(
                dataSource = DataSource.getDataSource(context.resources)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}