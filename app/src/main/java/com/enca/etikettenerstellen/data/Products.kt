package com.enca.etikettenerstellen.data

import android.content.res.Resources

/* Returns initial list of Products. */
fun productList(resources: Resources): List<Product> {
    return listOf(
        Product(
            bcode = "4003273044326",
            name = "Reis 1kg",
            price = "1,99€",
            pfand = ""

        ),
        Product(
            bcode = "1",
            name = "Cola 200ml",
            price = "1,29€",
            pfand = "+Pfand 0,25€"
        )
    )
}