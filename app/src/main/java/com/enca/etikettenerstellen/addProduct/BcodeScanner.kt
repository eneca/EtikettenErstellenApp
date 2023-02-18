package com.enca.etikettenerstellen.addProduct

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.enca.etikettenerstellen.databinding.ViewFinderBinding
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


private typealias BarcodeListener = (barcode: String?) -> Unit

/*Activity starting camera and passing on results of the barcode scan*/
class BcodeScanner:AppCompatActivity() {


    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeBoxView: BarcodeBoxView
    private lateinit var binding: ViewFinderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ViewFinderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        barcodeBoxView = BarcodeBoxView(this)
        addContentView(barcodeBoxView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        startCamera()

    }

    override fun onDestroy() {
        super.onDestroy()

        cameraExecutor.shutdown()
    }


    fun bcodeResults(bcode: String){
        val resultIntent = Intent()
        if(bcode.isNullOrEmpty()){
            setResult(Activity.RESULT_CANCELED,resultIntent)
            finish()
        }
        resultIntent.putExtra("Bcode",bcode)
        setResult(Activity.RESULT_OK,resultIntent)
        finish()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            // Image analyzer
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BCodeAnalyzer(this,barcodeBoxView,
                        binding.viewFinder.width.toFloat(),
                        binding.viewFinder.height.toFloat()){barcode: String? -> bcodeResults(barcode!!) })
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    /* Analyzer for given images */
    private class BCodeAnalyzer(private val context: Context,
                                private val barcodeBoxView: BarcodeBoxView,
                                private val previewViewWidth: Float,
                                private val previewViewHeight: Float,
                                private val listener: BarcodeListener
    ): ImageAnalysis.Analyzer {

        private var scaleX = 1f
        private var scaleY = 1f

        private fun translateX(x: Float) = x * scaleX
        private fun translateY(y: Float) = y * scaleY
        private val listeners = ArrayList<BarcodeListener>().apply { listener?.let { add(it) } }

        private fun adjustBoundingRect(rect: Rect) = RectF(
            translateX(rect.left.toFloat()),
            translateY(rect.top.toFloat()),
            translateX(rect.right.toFloat()),
            translateY(rect.bottom.toFloat())
        )

        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(image: ImageProxy) {
            val img = image.image
            if (img != null) {
                scaleX = previewViewWidth / img.height.toFloat()
                scaleY = previewViewHeight / img.width.toFloat()

                val inputImage = InputImage.fromMediaImage(img, image.imageInfo.rotationDegrees)

                // Process image searching for barcodes
                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_EAN_13)
                    .build()

                val scanner = BarcodeScanning.getClient(options)
                scanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            var b = barcodes.get(0)
                            Toast.makeText(context, "Value: " + b.rawValue, Toast.LENGTH_SHORT).show()
                            b.boundingBox?.let { rect -> barcodeBoxView.setRect(adjustBoundingRect(rect))}
                            listeners.forEach { it(b.rawValue) }
                        }
                        image.close()
                    }
                    .addOnFailureListener { e ->
                        image.close()
                    }
            }
        }

    }

}