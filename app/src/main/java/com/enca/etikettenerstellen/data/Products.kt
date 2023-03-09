package com.enca.etikettenerstellen.data

import android.content.res.Resources

/* Returns initial list of Products. */
fun productList(resources: Resources): List<Product> {
    return listOf(
        Product(
            bcode = "4003273044326",
            name = "Kein Echtes Produkt 5kg",
            price = "1,99€",
            pfand = ""

        )
    )
}