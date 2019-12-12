package com.example.matchwin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var database: DatabaseReference

    lateinit var btn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main)

        loadSignup(Signup())

    }


    private fun loadSignin(frag1: Signin) {
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.root_layout, frag1)
        ft.commit()
    }

    private fun loadSignup(frag1: Signup) {
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.root_layout, frag1)
        ft.commit()
    }
}
