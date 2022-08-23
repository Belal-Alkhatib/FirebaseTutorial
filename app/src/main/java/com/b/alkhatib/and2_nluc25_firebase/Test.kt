package com.b.alkhatib.and2_nluc25_firebase

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_test.*
import java.io.ByteArrayOutputStream

class Test : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val storage = Firebase.storage //4.1
        val storageRef = storage.reference //4.2
        val imageRef = storageRef.child("images") //4.3

        btn.setOnClickListener {
            // Get the data from an ImageView as bytes
            img.isDrawingCacheEnabled = true
            img.buildDrawingCache()
            val bitmap = (img.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val childRef = imageRef.child(System.currentTimeMillis().toString()+"_bilalimages.png") //***
            var uploadTask = childRef.putBytes(data)
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
            }.addOnSuccessListener { taskSnapshot ->
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                // ...
            }
        }
    }
}