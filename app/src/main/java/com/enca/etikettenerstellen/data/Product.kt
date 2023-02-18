package com.enca.etikettenerstellen.data

/*Data class Product consisting of

* Product Barcode EAN-13
* Product Name
* Product Price
* Product Pfand

* */
data class Product(
    var bcode: String,
    var name: String,
    var price: String,
    var pfand: String
)