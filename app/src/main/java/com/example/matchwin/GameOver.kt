package com.example.matchwin

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_game_over.*
import kotlinx.android.synthetic.main.fragment_game_over.view.*

class GameOver : Fragment() {

    private lateinit var auth: FirebaseAuth

    private lateinit var user: FirebaseUser

    private lateinit var database: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater!!.inflate(R.layout.fragment_game_over, container, false)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        database = FirebaseDatabase.getInstance().getReference("users")
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    if (snapshot.child("nickname").value == user.email!!.split("@")[0]){
                        val score = snapshot.child("score").value
                        view.goScore.text = "Score: ".plus(score.toString())
                        break
                    }
                }
                view.goBackBtn.setOnClickListener(){
                    activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.root_layout, Game())
                        .commit()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        database.addValueEventListener(userListener)
        return view
    }
}
