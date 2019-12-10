package com.example.matchwin

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_signin.view.*

class Signin : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()

        val view: View = inflater!!.inflate(R.layout.fragment_signin, container, false)


        view.signinbutton.setOnClickListener {
            loginClicked()
        }

        view.signupbutton.setOnClickListener { view ->
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.root_layout, Signup())
                .addToBackStack(tag)
                .commit()
        }

        return view
    }

    private fun loginClicked() {

        auth.signInWithEmailAndPassword(username.text.toString().plus("@matchwin.com"), password.text.toString())
            .addOnCompleteListener(getActivity()!!) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.root_layout, Game())
                        .addToBackStack(tag)
                        .commit()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                }
            }

    }
}
