package com.example.matchwin

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_leaders.*
import kotlinx.android.synthetic.main.fragment_leaders.view.*
import kotlinx.android.synthetic.main.fragment_level1.*
import androidx.recyclerview.widget.DividerItemDecoration as DividerItemDecoration1


class Leaders : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var database: DatabaseReference

    private lateinit var leaderRecyclerView: RecyclerView
    val data = ArrayList<User>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_leaders, container, false)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        database = FirebaseDatabase.getInstance().getReference("users")

        var layoutManager = LinearLayoutManager(activity)
        leaderRecyclerView = view.leaderRecyclerView
        leaderRecyclerView.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration1(
            leaderRecyclerView.getContext(),
            layoutManager.getOrientation()
        )
        leaderRecyclerView.addItemDecoration(dividerItemDecoration)

        view.leaderBackBtn.setOnClickListener(){ view ->
            getFragmentManager()!!.beginTransaction()
                .replace(R.id.root_layout, Game())
                .addToBackStack(tag)
                .commit()
        }

        fetchData()

        return view
    }

    private fun fetchData(){
        val myTopUsersQuery = database.orderByKey()
        myTopUsersQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val u = snapshot.getValue(User::class.java)!!
                    data.add(u)
                }
                showUsers(data)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun showUsers(card: ArrayList<User>){
        var leaderAdapter = LeaderAdapter(card)
        leaderRecyclerView.adapter = leaderAdapter
    }

}
