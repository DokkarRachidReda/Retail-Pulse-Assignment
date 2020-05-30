package com.reda_dk.retailpulse

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileDescriptor
import java.io.IOException



class MainActivity : AppCompatActivity() {
    val outputs = TensorBuffer.createFixedSize( intArrayOf( 1 , 16 ) , DataType.FLOAT32 )
   // val input = TensorBuffer.createFixedSize( intArrayOf( 1 , 300 , 300 , 3 ) , DataType.UINT8 )

    val imageProcessor = ImageProcessor.Builder()
        // Resize using Bilinear and Nearest Neighbor methods
        .add( ResizeOp( 300 , 300 , ResizeOp.ResizeMethod.BILINEAR ) )
        .add( ResizeWithCropOrPadOp( 300 , 300 ) )
        .build()

    val tensorProcessor = TensorProcessor.Builder()
        // Normalize the tensor by dividing each pixel by 255 ,the formula is (pixel-0)/255
        .add( NormalizeOp( 0f , 255f ) )
        // Cast the tensor to datatype FLOAT32
        .add( CastOp( DataType.FLOAT32 ) )
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        upload.setOnClickListener(View.OnClickListener {
            Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        Log.d("permission","permission granted")

                        val photoPickerIntent = Intent(Intent.ACTION_PICK)
                        photoPickerIntent.type = "image/*"
                        startActivityForResult(photoPickerIntent, 100)

                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        Log.e("permission","permission denied")
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {}
                }).check()
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==100 && data != null){
            Log.d("image upload","Image received in Activity Result")
            image.setImageURI(data.data)

            var bitmap = uriToBitmap(data.data!!)
            Bitmap.createScaledBitmap(
                bitmap, 300, 300, false);
            // Build a TensorImage object
            var tensorImage = TensorImage( DataType.UINT8 )
            // Load the Bitmap
            tensorImage.load( bitmap )

            // Process and normalize  the image
            tensorImage = imageProcessor.process( tensorImage )
            var imageBuffer = tensorProcessor.process(tensorImage.tensorBuffer)

            //Loading the model
            val model = FileUtil.loadMappedFile( this , "rock_paper_sci_model.tflite" )
            val interpreter = Interpreter( model )


            //run the model

            interpreter.run( imageBuffer.buffer , outputs.buffer )


        }

    }


    private fun uriToBitmap(selectedFileUri: Uri):Bitmap {
        lateinit var image : Bitmap
        try {
            val parcelFileDescriptor =
                contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
             image = BitmapFactory.decodeFileDescriptor(fileDescriptor)

            parcelFileDescriptor.close()

            return image

        } catch (e: IOException) {
            Log.e("image upload"," uri to bitmap error : "+e.toString())
        }

       return image
    }




}
