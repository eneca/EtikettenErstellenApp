package com.enca.etikettenerstellen.data

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import java.util.*


/*Creates a bitmap of a given EAN-13 barcode*/
@Throws(WriterException::class)
fun createBarcodeBitmap(data: String, width: Int, height: Int): Bitmap? {
    val writer = MultiFormatWriter()
    val finalData: String = Uri.encode(check(data))

    // Use 1 as the height of the matrix as this is a 1D Barcode.
    val bm = writer.encode(finalData, BarcodeFormat.EAN_13, width, 1)
    val bmWidth = bm.width
    val imageBitmap = Bitmap.createBitmap(bmWidth, height, Bitmap.Config.ARGB_8888)
    for (i in 0 until bmWidth) {
        // Paint columns of width 1
        val column = IntArray(height)
        Arrays.fill(column, if (bm[i, 0]) Color.BLACK else Color.WHITE)
        imageBitmap.setPixels(column, 0, 1, i, 0, 1, height)
    }
    return imageBitmap
}
private fun check(s:String): String{
    if(s.length<13||s.length>13){
        return "012345678012"
    }
    return s
}