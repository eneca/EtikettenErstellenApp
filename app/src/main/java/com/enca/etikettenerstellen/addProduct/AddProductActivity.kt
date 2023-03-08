package com.enca.etikettenerstellen.addProduct

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.enca.etikettenerstellen.R
import com.google.android.material.textfield.TextInputEditText


const val PRODUCT_NAME = "Name"
const val PRODUCT_PRICE = "Preis"
const val PRODUCT_PFAND = "Pfand"
const val PRODUCT_BCODE = "Barcode"

class AddProductActivity : AppCompatActivity() {
    private val request_Code = 5
    private lateinit var addProductName: TextInputEditText
    private lateinit var addProductPrice: TextInputEditText
    private lateinit var addProductPfand: TextInputEditText
    private lateinit var addProductBcode: TextInputEditText
    private val your_addition = charArrayOf('€')




    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_product_layout)

        findViewById<Button>(R.id.bcode_scan_button).setOnClickListener {
            addBcode()
        }

        findViewById<Button>(R.id.done_button).setOnClickListener {
            addProduct()
        }

        addProductName = findViewById(R.id.add_product_name)
        addProductPrice = findViewById(R.id.add_product_price)
        addProductPfand = findViewById(R.id.add_product_pfand)
        addProductBcode = findViewById(R.id.add_product_bcode)

    }
    @Deprecated("k")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        /* Inserts product into viewModel. */
        if (requestCode == request_Code && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                addProductBcode.setText(data.getStringExtra("Bcode"))

            }
        }
    }

    //Kommt bestimmt bald ;D
    private fun addName(){
    }
    private fun setName(text: TextInputEditText){
        text.setText("")
    }

    private fun addBcode(){
        val i = Intent(this, BcodeScanner::class.java)
        startActivityForResult(i,request_Code)
    }
    private fun setBcode(text: TextInputEditText){
        text.setText("")
    }

    private fun addProduct() {
        val resultIntent = Intent()

        if (addProductName.text.isNullOrEmpty() || addProductPrice.text.isNullOrEmpty()|| addProductBcode.text.isNullOrEmpty()) {
            setResult(Activity.RESULT_CANCELED, resultIntent)
        } else {
            val name = addProductName.text.toString()
            val price = addProductPrice.text.toString().replace('.',',').let{it+'€'}
            val pfand = calcPfand(addProductPfand.text.toString()).replace('.',',')
            val bcode = addProductBcode.text.toString()

            resultIntent.putExtra(PRODUCT_BCODE, bcode)
            resultIntent.putExtra(PRODUCT_NAME, name)
            resultIntent.putExtra(PRODUCT_PRICE, price)
            resultIntent.putExtra(PRODUCT_PFAND, pfand)

            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }
    private fun calcPfand(s:String):String{
        if(s.isNullOrEmpty()||s=="0")
            return ""
        val n=s.toInt()*0.25
        return "+Pfand "+n+"€"

    }

}