package com.example.matchwin

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*


class Signup : Fragment() {

    private lateinit var auth: FirebaseAuth

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater!!.inflate(R.layout.fragment_login, container, false)

        auth = FirebaseAuth.getInstance()

        view.select_image.setOnClickListener { view ->
            pickImageFromGallery()
        }

        view.signupbtn.setOnClickListener { view ->
            signupClicked()
        }

        view.signinbtn.setOnClickListener { view ->
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.root_layout, Signin())
                .addToBackStack(tag)
                .commit()
        }
        // Return the fragment view/layout
        return view
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun signupClicked() {

        auth.createUserWithEmailAndPassword(username.text.toString().plus("@matchwin.com"), password.text.toString())
            .addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser!!
                    database = FirebaseDatabase.getInstance().getReference("users")
                    val userId = database.push().key
                    val myuser = User(nickname = auth.currentUser!!.email!!.split("@")[0],score = 0,highscore = 0,avatar = "test")
                    database.child(userId!!).setValue(myuser).addOnCompleteListener {
                        Toast.makeText(activity!!.applicationContext,"User saved successfully.", Toast.LENGTH_LONG).show()
                        updateUI(user)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                }

                // ...
            }

    }

    private fun updateUI(user: FirebaseUser){
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.root_layout, Game())
            .addToBackStack(tag)
            .commit()
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this.context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            imageView.setImageURI(data?.data)
        }
    }
}

@IgnoreExtraProperties
data class User(
    var nickname: String? = "",
    var score: Int? = 0,
    var highscore: Int? = 0,
    var avatar: String? = "default"
)