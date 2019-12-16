package com.example.matchwin

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.android.synthetic.main.fragment_game.view.*


class Game : Fragment() {

    private lateinit var auth: FirebaseAuth

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater!!.inflate(R.layout.fragment_game, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")
        val currentUser = auth.currentUser
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    if (snapshot.child("nickname").value == currentUser?.email!!.split("@")[0]){
                        val user = snapshot.getValue(User::class.java)
                        view.score.text =  "Score: ".plus(user?.score.toString())
                        view.username.text = "Nickname: ".plus(currentUser?.email!!.split("@")[0])
                        view.highscore.text =  "Highscore: ".plus(user?.highscore.toString())
                        Picasso.get().load(user?.avatar).into(view.imageView3)
                        view.progressBar.visibility = View.INVISIBLE
                        view.playbutton.visibility = View.VISIBLE
                        view.leadersbutton.visibility = View.VISIBLE
                        break
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException())
                // ...
            }
        }
        database.addValueEventListener(postListener)


        view.playbutton.setOnClickListener {
            playClicked()
        }

        view.leadersbutton.setOnClickListener {
            leadersClicked()
        }

        return view
    }

    private fun playClicked(){
        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.root_layout, Level1.newInstance(), "level1")
            .addToBackStack(tag)
            .commit()
    }

    private fun leadersClicked(){
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.root_layout, Leaders(), "leaders")
            .addToBackStack(tag)
            .commit()
    }

}
