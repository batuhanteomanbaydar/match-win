package com.example.matchwin

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_leaders.*
import kotlinx.android.synthetic.main.fragment_leaders.view.*
import androidx.recyclerview.widget.DividerItemDecoration as DividerItemDecoration1


class Leaders : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var database: DatabaseReference

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

        view.leaderBackBtn.setOnClickListener(){ view ->
            getFragmentManager()?.popBackStack()
        }

        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fetchData()
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
        var layoutManager = LinearLayoutManager(activity)
        leaderRecyclerView.layoutManager = layoutManager
        leaderRecyclerView.adapter = leaderAdapter
        val dividerItemDecoration = DividerItemDecoration1(
            leaderRecyclerView.getContext(),
            layoutManager.getOrientation()
        )
        leaderRecyclerView.addItemDecoration(dividerItemDecoration)
    }

}
