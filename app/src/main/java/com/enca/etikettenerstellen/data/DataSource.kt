package com.enca.etikettenerstellen.data

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/* Handles operations on productsLiveData and holds details about it. */
class DataSource(resources: Resources) {
    private val initialProductList = productList(resources)
    private val productsLiveData = MutableLiveData(initialProductList)

    /* Adds product to liveData and posts value. */
    fun addProduct(product: Product) {
        val currentList = productsLiveData.value
        if (currentList == null) {
            productsLiveData.postValue(listOf(product))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(0, product)
            productsLiveData.postValue(updatedList)
        }
    }
    /* Edits existing product from liveData and posts value. */
    fun editProduct(product: Product) {
        val currentList = productsLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(updatedList.find { it.bcode==product.bcode})
            updatedList.add(0, product)
            productsLiveData.postValue(updatedList)
        }

    }

    /* Removes product from liveData and posts value. */
    fun removeProduct(product: Product) {
        val currentList = productsLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(product)
            productsLiveData.postValue(updatedList)
        }
    }

    /* Returns product given a Barcode. */
    fun getProductForBcode(bcode: String): Product? {
        productsLiveData.value?.let { products ->
            return products.firstOrNull{ it.bcode == bcode}
        }
        return null
    }

    fun getProductList(): LiveData<List<Product>> {
        return productsLiveData
    }

    fun deleteList() {
        productsLiveData.postValue(null)
    }

    companion object {
        private var INSTANCE: DataSource? = null

        fun getDataSource(resources: Resources): DataSource {
            return synchronized(DataSource::class) {
                val newInstance = INSTANCE ?: DataSource(resources)
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}