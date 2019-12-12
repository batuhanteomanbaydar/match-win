package com.example.matchwin

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.android.synthetic.main.fragment_signup.view.*
import java.io.File
import java.io.IOException


class Signup : Fragment() {

    private lateinit var auth: FirebaseAuth

    private  lateinit var storageRef: StorageReference

    var avatarUrl: String = ""

    private lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater!!.inflate(R.layout.fragment_signup, container, false)

        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().reference

        view.select_image.setOnClickListener { view ->
            //pickImageFromGallery()
            val dialogBuilder = AlertDialog.Builder(activity!!)
            dialogBuilder.setMessage("Choose an avatar or upload a picture")
                // if the dialog is cancelable
                .setCancelable(true)
                .setNegativeButton("Select Avatar", DialogInterface.OnClickListener {
                        dialog, id ->
                    dialog.dismiss()
                })
                .setPositiveButton("Open Gallery", DialogInterface.OnClickListener {
                        dialog, id ->
                    dialog.dismiss()
                    pickImageFromGallery()

                })

            val alert = dialogBuilder.create()
            alert.setTitle("Select Avatar")
            alert.show()
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

        if(usernameText.text.toString().length == 0){
            Toast.makeText(activity!!.applicationContext,"Username required!", Toast.LENGTH_LONG).show()
        }else if(password.text.toString().length == 0){
            Toast.makeText(activity!!.applicationContext,"Password required!", Toast.LENGTH_LONG).show()
        }else{
            auth.createUserWithEmailAndPassword(usernameText.text.toString().plus("@matchwin.com"), password.text.toString())
                .addOnCompleteListener(activity!!) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser!!
                        database = FirebaseDatabase.getInstance().getReference("users")
                        val userId = database.push().key
                        var myuser = User()
                        if (avatarUrl.length == 0){
                            myuser = User(nickname = auth.currentUser!!.email!!.split("@")[0],score = 0,highscore = 0,avatar = "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcS4RHDL6Bs9E6cpHDChkzD48PJdBPUVDq6P9kjJ8HfBSTPm6W7W")
                        }else{
                            myuser = User(nickname = auth.currentUser!!.email!!.split("@")[0],score = 0,highscore = 0,avatar = avatarUrl)
                        }

                        database.child(userId!!).setValue(myuser).addOnCompleteListener {
                            Toast.makeText(activity!!.applicationContext,"User saved successfully.", Toast.LENGTH_LONG).show()
                            updateUI(user)
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(activity!!.applicationContext,task.exception?.message?.replace("email address","username",ignoreCase = true), Toast.LENGTH_LONG).show()
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    }
                }
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
            var file = data!!.data
            val riversRef = storageRef.child("images/${file!!.lastPathSegment}")
            var uploadTask = storageRef.putFile(file!!)
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                storageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    avatarUrl = downloadUri.toString()
                } else {
                    // Handle failures
                    // ...
                }
            }
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