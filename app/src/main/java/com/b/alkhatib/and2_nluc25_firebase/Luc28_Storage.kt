package com.b.alkhatib.and2_nluc25_firebase

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.b.alkhatib.and2_nluc25_firebase.databinding.ActivityLuc28StorageBinding
import com.b.alkhatib.and2_nluc25_firebase.databinding.ActivityMainBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_luc28_storage.*
import java.io.ByteArrayOutputStream

class Luc28_Storage : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1111
    private var fileURI: Uri? = null //5.1
    var imageURI: Uri? = null //3.2.b
    val TAG = "hzm"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_luc28_storage)

        /**
                                            ( Cloud Storage )
        خدمة من الفايربيز تسمح ب upload and share مثل الصور والفيديوهات
        من ال console الى الstorage #
        عندما تذهب للRules ستجد انها محمية عبر الAuthentication بالتالي الذي يتعامل مع الstorage هو من سجل الدخول فقط لذلك نعمل #
        allow read, write: if true;

        1.
        نعمل اتصال عبر الStorage كما عملنا مسبقا من tool -> Cloud Storage -> add cloud storage to your app
        2.
        نضيف الimplemintation الخاص بالكوتلين بدلا من الذي يضيفه افتراضيا افضل
        3.
        عملية اختيار الصورة
        4.
        عملية الStorage
           # يمكن رفع الصور من القنسل ويمكن ايضا عمل مجلدات , لايمكن رفع صورة بنفس الاسم مرتين
           لذلك يتعين عليك تغيير الاسم ليكون فريد
                4.1
                    عمل انستنس من اFirebaseStorage
                4.2
                    تعريف ريفيرنس _المرجع الخاص بك
                4.3
                    تعريف ريفيرنس للصورة بزيادة child ومنها تحدد المجلد الذي تريد الرفع عليه
                4.4
                     جزئية الرفع من كود الdoc جزئية uploadFile اخترنا منها الطريقة الاولى bitsArray
         5.
         عملية قراءة الصورة
            # لما تريد الوصول لصورة من الانترنت تحتاج لرابط ,لذا عند نجاح عملية الرفع انت تحتاج لرابط
                5.1
                    في ليسنر النجاح الخاص بالرفع : عبر الصورة المرفوعة ناخذ منها الurl ونعمل ليسنر في حال اخذه بنجاح ونخزنه بمتغير
                5.2
                    بما ان الرابط جاهز اقرأه عبر اي من المكتبات التي اخذناها سابقا مثل الpicasso

        */
//**************************************************************  3  ***************************************************************

        // 3.1
        btnChooseImage.setOnClickListener {
            getContent.launch("image/*")

            //val intent = Intent()
            //intent.action = Intent.ACTION_PICK //لالتقاط صورة من الاستديو
            //intent.type = "image/*" // يعني اي امتداد
            //startActivityForResult(intent,PICK_IMAGE_REQUEST) // تاخدintent وrequestCode انت تحدده كرقم عادي
            // لماذا startActivityForResult لاننا سنعمل اوفرايد لدالة onActivityResult بحيث نحقق مين اللي شغل image pick
        }

//**************************************************************  4  ***************************************************************

        val storage = Firebase.storage //4.1
        val storageRef = storage.reference //4.2
        val imageRef = storageRef.child("images") //4.3

        btnUploadImage.setOnClickListener {  //4.4
            // Get the data from an ImageView as bytes
            // التقاط الصورة وتحويلها لبايت اريي

            val bitmap = (imageViewChoose.drawable as BitmapDrawable).bitmap //نعطيه مكان عرض الصورة ونحولها لبت ماب
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val data = baos.toByteArray()

            /**
            ملاحظة :- ***
            imageRef عندما ترفع الصورة نفسها فانت ترفعه على نفس الريفيرنس الخاص بالا
            وتكون بنفس الاسم الذي حددته في الchield وهو images وهذا خطا لذلك لكل صورة ترفعها لازم تعمل child
            */

            val childRef = imageRef.child(System.currentTimeMillis().toString()+"_bilalimages.png") //***
            var uploadTask = childRef.putBytes(data) // المكان الذي سترفع عليه

            uploadTask.addOnFailureListener {exception ->
                Log.e(TAG, exception.message.toString())
            }.addOnSuccessListener { taskSnapshot ->
                Log.e(TAG,"Image Uploaded Successfully")
                Toast.makeText(this,"Image Uploaded Successfully",Toast.LENGTH_LONG).show()

                childRef.downloadUrl.addOnSuccessListener { uri -> //5.1
                    Log.e(TAG, uri.toString())
                    fileURI = uri
                }
            }
        }

//**************************************************************  5  ***************************************************************
        btnReadImage.setOnClickListener { //5.2
            Picasso.get().load(fileURI).into(imageViewRead);
        }
//**********************************************************************************************************************************


    }
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageViewChoose.setImageURI(uri)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // a.  ونجاح العمليةrequestQode التحقق من ال
        // b.  dataوهي موجودة ضمن الchooseالخاصة بimageView اخذ مسار الصورة المختارة لعرضها في ال
        // c.  imageView على الuri وضع ال

        if(resultCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){ // a
            imageURI = data!!.data // b
            Log.e(TAG, imageURI.toString())
            imageViewChoose.setImageURI(imageURI) // c
        }

    }
}