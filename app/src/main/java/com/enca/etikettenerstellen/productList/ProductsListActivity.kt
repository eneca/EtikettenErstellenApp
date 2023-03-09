package com.enca.etikettenerstellen.productList

import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.enca.etikettenerstellen.R
import com.enca.etikettenerstellen.data.Product
import com.enca.etikettenerstellen.productDetail.ProductDetailActivity
import com.enca.etikettenerstellen.addProduct.*
import com.google.android.material.snackbar.Snackbar

import java.io.*


const val PRODUCT_ID = "product id"

class ProductsListActivity : AppCompatActivity(),ActivityCompat.OnRequestPermissionsResultCallback  {
    private val newProductActivityRequestCode = 1
    private val productsListViewModel by viewModels<ProductsListViewModel> {
        ProductsListViewModelFactory(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        //WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val productsAdapter = ProductsAdapter { product -> adapterOnClick(product) }
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = productsAdapter

        productsListViewModel.productsLiveData.observe(this, { it?.let { productsAdapter.submitList(it as MutableList<Product>) }})

        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener { fabOnClick() }

        if (!allRuntimePermissionsGranted()) {
            getRuntimePermissions()
        }
    }

    /* Opens ProductDetailActivity when RecyclerView item is clicked. */
    private fun adapterOnClick(product: Product) {
        val intent = Intent(this, ProductDetailActivity()::class.java)
        intent.putExtra(PRODUCT_BCODE, product.bcode)
        startActivity(intent)
    }

    /* Adds product to productList when FAB is clicked. */
    private fun fabOnClick() {
        val intent = Intent(this, AddProductActivity::class.java)
        startActivityForResult(intent, newProductActivityRequestCode)
    }

    /******     CSV    *******/
    fun writeCsv(productList: List<Product>) {
        var root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        root = File(root, "Produkte")
        root.mkdir()
        root = File(root, "Produkt_Liste.txt")

        val writer = BufferedWriter(FileWriter(root))
        writer.write("Barcode; Name; Preis; Pfand")
        writer.newLine()
        productList.forEach {
            writer.write("${it.bcode};${it.name};${it.price};${it.pfand}")
            writer.newLine()
        }
        writer.flush()
    }
    /* Print a PDF from Elements in RecyclerView TODO */
    private fun printButtonOnClick(){
        if(productsListViewModel.productsLiveData.value.isNullOrEmpty())
            return
        Snackbar.make(getWindow().getDecorView().getRootView(),"Speichert Datei!",Snackbar.LENGTH_SHORT).show()
        writeCsv(productsListViewModel.productsLiveData.value!!)
    }

    /******     Delete List    *******/
    fun deleteList(){
        productsListViewModel.deleteList()
    }

    /* Click event to delete all elements in list */
    private fun deleteButtonOnClick(){
        if(productsListViewModel.productsLiveData.value.isNullOrEmpty()){
            Snackbar.make(getWindow().getDecorView().getRootView(),"Liste bereits gelöscht!",Snackbar.LENGTH_SHORT).show()
            return
        }

        val deleteDialog = AlertDialog.Builder(this)
        deleteDialog.setTitle("Liste Löschen")
        deleteDialog.setMessage("Bei Bestätigung wird die Liste in der App gelöscht")

        deleteDialog.setPositiveButton(android.R.string.yes) { dialog, which ->
            Snackbar.make(getWindow().getDecorView().getRootView(),"Löscht Liste!",Snackbar.LENGTH_SHORT).show()
            deleteList()
        }

        deleteDialog.setNegativeButton(android.R.string.no) { dialog, which ->
            Snackbar.make(getWindow().getDecorView().getRootView(),"Liste nicht gelöscht!",Snackbar.LENGTH_SHORT).show()
        }
        deleteDialog.show()

        return
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings_print -> {
                printButtonOnClick()
                true
            }
            R.id.action_settings_delete -> {
                deleteButtonOnClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Deprecated("k")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        /* Inserts product into viewModel. */
        if (requestCode == newProductActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val productName = data.getStringExtra(PRODUCT_NAME)
                val productPrice = data.getStringExtra(PRODUCT_PRICE)
                val productPfand = data.getStringExtra(PRODUCT_PFAND)
                val productBcode = data.getStringExtra(PRODUCT_BCODE)

                productsListViewModel.insertProduct(productBcode, productName, productPrice,productPfand)
            }
        }
    }

    /******     Permissions     *******/
    private fun allRuntimePermissionsGranted(): Boolean {
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission?.let {
                if (!isPermissionGranted(this, it)) {
                    return false
                }
            }
        }
        return true
    }

    private fun getRuntimePermissions() {
        val permissionsToRequest = ArrayList<String>()
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission?.let {
                if (!isPermissionGranted(this, it)) {
                    permissionsToRequest.add(permission)
                }
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUESTS
            )
        }
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(TAG, "Permission granted: $permission")
            return true
        }
        Log.i(TAG, "Permission NOT granted: $permission")
        return false
    }
    companion object {
        private const val TAG = "EntryChoiceActivity"
        private const val PERMISSION_REQUESTS = 1

        private val REQUIRED_RUNTIME_PERMISSIONS =
            arrayOf(CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
    }

}