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
import kotlin.math.sqrt

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
            preCalculedVects.add(MainActivity.MyVector(list,-1))
            line = buffer.readLine()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

      val labelsStream = context.assets.open("rps_labels.tsv")
      val labelsBuffer = BufferedReader(InputStreamReader(labelsStream))
      var label :String? =""
      var i = 0
      try {
           label =labelsBuffer.readLine()
          while ( label != null) {

              preCalculedVects[i].label = label.toInt()
              label = labelsBuffer.readLine()
              i++
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



 fun ecludienDist(vect1 :MainActivity.MyVector , vect2 : MainActivity.MyVector):Double{

     var dist:Double = 0.0

     for (i in 0..vect1.value.size-1){

         Log.e("dist","value 1 : "+vect1.value[i].toString())
         Log.e("dist","value 2 : "+vect2.value[i].toString())

         dist += (vect1.value[i] - vect2.value[i])*(vect1.value[i] - vect2.value[i])

         Log.e("dist","new dist : "+dist.toString())
         Log.e("dist","----------------------")
     }
     Log.e("dist","*************"+ sqrt(dist) +"*************")
     return sqrt(dist)
 }