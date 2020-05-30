package com.reda_dk.retailpulse.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.reda_dk.retailpulse.MainActivity
import java.io.BufferedReader
import java.io.FileDescriptor
import java.io.IOException
import java.io.InputStreamReader

  fun loadPreCalculedVects(context: Context) : ArrayList<MainActivity.MyVector> {
    val preCalculedVects = ArrayList<MainActivity.MyVector>()
    val inputStream = context.assets.open("rps_vecs.tsv")
    val buffer = BufferedReader(InputStreamReader(inputStream))


    try {
        var line =buffer.readLine()
        while ( line != null) {

            val list = ArrayList<Double>()
            val values = line.split("\t")

            for( st in values){list.add(st.trim().toDouble())}
            preCalculedVects.add(MainActivity.MyVector(list))
            line = buffer.readLine()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return preCalculedVects
}


  fun uriToBitmap(selectedFileUri: Uri,context: Context): Bitmap {
    lateinit var image : Bitmap
    try {
        val parcelFileDescriptor =
            context.contentResolver.openFileDescriptor(selectedFileUri, "r")

        val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
        image = BitmapFactory.decodeFileDescriptor(fileDescriptor)

        parcelFileDescriptor.close()

        return image

    } catch (e: IOException) {
        Log.e("image upload"," uri to bitmap error : "+e.toString())
    }

    return image
}
