package com.b.alkhatib.and2_nluc25_firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.b.alkhatib.and2_nluc25_firebase.databinding.ActivityMainBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.core.FieldFilter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var db:FirebaseFirestore // 1. تعريف الداتا بيز
    val TAG = "hzm"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

         db = Firebase.firestore // 1. تابع

        // 3. تجهيز الواجهة لاضافة مستخدم
        // 4. استخدام دالة الاضافة
        binding.btnAddUser.setOnClickListener {
            addUserToDB(binding.etUsername.text.toString(), binding.etEmail.text.toString(), binding.etLevel.text.toString().toInt())
        }

        //5
        binding.btnGetUsers.setOnClickListener {
            getAllUsers()
        }

        //6
        binding.btnGetAllUserByLimitAndOrder.setOnClickListener {
            getAllUserByLimit()
        }

        //7
        binding.btnGetAllUserByWhere.setOnClickListener {
            getAllUserByWhere()
        }

        //8
        binding.btnDeleteUser.setOnClickListener {
            DeleteUser("A3cN97CrG8tFk2JBLmAB")
        }

        //9
        binding.btnDeleteUserEmail.setOnClickListener {
            DeleteUserEmailField("pjr1pUnKd8Q0MPAT57GI")
        }

    }
//***************** *********************** ********************** ******************** ******************** ******************

    // 2. انشاء دالة اضافة مستخدم
    private fun addUserToDB(name:String, email:String, level:Int){
        val user = hashMapOf("name" to name, "email" to email, "level" to level)
        db.collection("users") //دلة تضيفلك الكوليكشن في حال مش موجود
            .add(user)// دالة لاضافة دوكيومنت على الكوليكشن
            .addOnSuccessListener { documentReference ->      //تعمل كمستمع في حال نجاح الاضافة
                Log.e(TAG, "User Add Successfully with user id ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.message.toString())
            }
    }

    // 5. دالة لعرض المستخدمين
    /*      a. تحدد الكوليكشن
            b. تاتي بالبيانات عبر دالة get
            c. تعمل ليسنر على نجاح او فشل العملية
    */
    private fun getAllUsers(){
        db.collection("users")
            .get()
            .addOnSuccessListener { querySnapshot -> // يمثل البيانات الراجعة وهي عبارة عن مصفوفة بالتاي نحتاج للوب
                for( document in querySnapshot){
                    Log.e(TAG, "${document.id} => ${document.data}") //  بهذه الطريقة تاتي بال اي دي والمعلومات الكاملة عن هذا الدكيومنت الفيلدز الخاصة به
                    //Log.e(TAG, "${document.id} => ${document.get("name")}") //  بهذه الطريقة تاتي بال اي دي والمعلومات الكاملة عن هذا الدكيومنت ,وبالاخص الفيلد الذي حددته

                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.message.toString())
            }
    }

    // 6.دالة لعرض المستخدمين بليمت وترتيب معين
    // نفس 5 ولكن باضافة الlimit وال orderby
    private fun getAllUserByLimit(){
       // db.collection("users").limit(2).orderBy("name") // للترتيب التصاعدي asc
        db.collection("users").limit(2).orderBy("name", Query.Direction.DESCENDING) // للترتيب التنازلي des
            .get()
            .addOnSuccessListener { querySnapshot -> // يمثل البيانات الراجعة وهي عبارة عن مصفوفة بالتاي نحتاج للوب
                for( document in querySnapshot){
                    Log.e(TAG, "${document.id} => ${document.data}") //
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.message.toString())
            }
    }

    // 7. دالة لعرض المستخدمين بناء على شرط معين وذلك باستخدام where بانواعها
    private fun getAllUserByWhere(){

        //db.collection("users").whereGreaterThan("level",3) // لشرط الاكبر من تعطيها اسم الفيلد والقيمة المقارن حسبها
        db.collection("users").whereEqualTo("level",3) // اكبر من او يساوي
            .get()
            .addOnSuccessListener { querySnapshot ->
                for( document in querySnapshot){
                    Log.e(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.message.toString())
            }
    }

    //8. للحذف تحتاج لid _ حذف مستخدم كامل
    /* a. تحدد الكوليكشن
       b. documentعبر  idترسل ال
       c. delete امر الحذف عبر دالة
       d. تعمل ليسنر على نجاح او فشل العملية
    */
    private fun DeleteUser(id:String){
        db.collection("users").document(id)
            .delete()
            .addOnSuccessListener {
                Log.e(TAG, "User Deleted Successfully")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.message.toString())
            }

    }

    /* 9.
       a. تحدد الكوليكشن
       b. document عبر  id ترسل ال
       c.  : لانك تحدث قيمة عمود بالتالي update معين عبر عمل field لحذف
           c1_ المراد حذفه field تحدد ال
           c2_ FieldValue.delete() القيمة اللتي تتكفل بالحذف وهي
    */
    private  fun  DeleteUserEmailField(id:String){
        db.collection("users").document(id)
            .update("email",FieldValue.delete())

            .addOnSuccessListener {
                Log.e(TAG, "User Email Deleted Successfully")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.message.toString())
            }
    }

    //******************** ******************* ***********  Luc 28  ********* ********************* *************** ********************* ***********

    /* 10. UPDATE
        # عملية التعديل مثل الاضافة ولكن بزيادة ال id
        # يمكن الارسال الdata عبر obj of model او hashmap ولكن الهاش ماب مش حتزبط لذلك سنلجأ لل obj
        a.  key & value ونحدد ال hashmap من ال obj نعرف
        b. تحدد الكوليكشن
        c. document المراد تعديله عبر id ترسل ال _row ال_
        d. الي عملته obj ترسل ال update عبر دالة ال
        e. تعمل ليسنر على نجاح او فشل العملية
    */

    private fun updateuserById(oldId:String, name:String, email:String, level:Int){
        val user = HashMap<String,Any>()
            user["name"] = name
            user["email"] = email
            user["level"] = level

        db.collection("users").document(oldId)
            .update(user)
            .addOnSuccessListener {
                Log.e(TAG, "User Updated Successfully")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.message.toString())
            }
    }

    /*
    ملاحظة :-
        حتى تاتي بrow معين عبر عمل نفس دالة الgetAll بخلاف انك ترسل الid للrow الذي تريده عبر document مثل ما عملنا بالupdate
    */
}