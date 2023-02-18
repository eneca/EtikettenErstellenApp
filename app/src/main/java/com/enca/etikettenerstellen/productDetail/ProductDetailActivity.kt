package com.enca.etikettenerstellen.productDetail

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.enca.etikettenerstellen.R
import com.enca.etikettenerstellen.addProduct.PRODUCT_BCODE
import com.enca.etikettenerstellen.data.Product

class ProductDetailActivity : AppCompatActivity() {

    private val productDetailViewModel by viewModels<ProductDetailViewModel> {
        ProductDetailViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_detail_activity)

        var currentProductBcode: String? = null

        /* Connect variables to UI elements. */
        val productName: TextView = findViewById(R.id.product_detail_name)
        val productPrice: TextView = findViewById(R.id.product_detail_price)
        val productPfand: TextView = findViewById(R.id.product_detail_pfand)
        val productBcode: TextView = findViewById(R.id.product_detail_bcode)
        val removeProductButton: Button = findViewById(R.id.remove_button_detail)
        val editProductButton: Button = findViewById(R.id.edit_button_detail)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            currentProductBcode = bundle.getString(PRODUCT_BCODE)
        }

        /* If currentProductBcode is not null, get corresponding product and set bcode, name, price and
        pfand */
        currentProductBcode?.let {
            var currentProduct = productDetailViewModel.getProductForBcode(it)
            productName.text = currentProduct?.name
            productPrice.text = currentProduct?.price
            productPfand.text = currentProduct?.pfand
            productBcode.text = currentProduct?.bcode

            removeProductButton.setOnClickListener {
                if (currentProduct != null) {
                    productDetailViewModel.removeProduct(currentProduct)
                }
                finish()
            }
            editProductButton.setOnClickListener {
                if (currentProduct != null) {
                    if(productName.text.isNullOrEmpty() || productPrice.text.isNullOrEmpty()|| productBcode.text.isNullOrEmpty()){
                        finish()
                    }
                    val p= Product(productName.text.toString(), productPrice.text.toString(),productPfand.text.toString() ,productBcode.text.toString())
                    productDetailViewModel.editProduct(p)
                }
                finish()
            }
        }

    }


}