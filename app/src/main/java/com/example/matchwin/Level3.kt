package com.example.matchwin

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_level3.*
import kotlinx.android.synthetic.main.fragment_level3.view.*

class Level3 : Fragment() {
    private lateinit var gametimer: CountDownTimer

    private lateinit var auth: FirebaseAuth

    private lateinit var user: FirebaseUser

    private lateinit var database: DatabaseReference

    private var userScore: Int = 0

    var numOpen = 0
    var found = 0
    var selectedItem1 = -1;
    var selectedItem2 = -1;
    val data = ArrayList<Int>()
    val cards = ArrayList<Int>()
    var timeleft: Long = 180000

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_level3, container, true)
        var min = 3
        var second = 0

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        view.timerText3.text = "Time: 0".plus(min.toString()).plus(".00")
        gametimer = object: CountDownTimer(180000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeleft = millisUntilFinished
                if (second == 0){
                    min = min - 1
                    second = 60
                }else{
                    second = second - 1
                }
                if(second == 60){
                    view.timerText3.text = "Time: 0".plus((min + 1).toString()).plus(":00")
                }else if(second < 10){
                    view.timerText3.text = "Time: 0".plus(min.toString()).plus(":0").plus(second.toString())
                }else{
                    view.timerText3.text = "Time: 0".plus(min.toString()).plus(":").plus(second.toString())
                }
            }
            override fun onFinish() {
                if (found != 6){
                    activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.root_layout, GameOver())
                        .commit()
                }
            }
        }
        gametimer.start()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fetchData()
    }


    private fun fetchData(){
        data.add(R.drawable.picture_1)
        data.add(R.drawable.picture_2)
        data.add(R.drawable.picture_3)
        data.add(R.drawable.picture_4)
        data.add(R.drawable.picture_5)
        data.add(R.drawable.picture_6)
        data.add(R.drawable.picture_1)
        data.add(R.drawable.picture_2)
        data.add(R.drawable.picture_3)
        data.add(R.drawable.picture_4)
        data.add(R.drawable.picture_5)
        data.add(R.drawable.picture_6)
        data.shuffle()
        cards.add(R.color.colorPrimaryDark)
        cards.add(R.color.colorPrimaryDark)
        cards.add(R.color.colorPrimaryDark)
        cards.add(R.color.colorPrimaryDark)
        cards.add(R.color.colorPrimaryDark)
        cards.add(R.color.colorPrimaryDark)
        cards.add(R.color.colorPrimaryDark)
        cards.add(R.color.colorPrimaryDark)
        cards.add(R.color.colorPrimaryDark)
        cards.add(R.color.colorPrimaryDark)
        cards.add(R.color.colorPrimaryDark)
        cards.add(R.color.colorPrimaryDark)
        showCards(cards)
    }

    private fun showCards(card: ArrayList<Int>){
        var gameAdapter = GameAdapter(card)
        recyclerView3.layoutManager = GridLayoutManager(activity, 4, GridLayoutManager.VERTICAL, true)
        recyclerView3.adapter = gameAdapter

        gameAdapter.onItemClick = { item ->
            numOpen = numOpen + 1
            if(numOpen == 1){
                selectedItem1 = item
            }else{
                selectedItem2 = item
            }
            card.set(item,data.get(item))
            gameAdapter.notifyItemChanged(item)
            if (numOpen == 2){
                if (selectedItem1 != selectedItem2) {
                    val timer = object: CountDownTimer(500, 1000) {
                        override fun onTick(millisUntilFinished: Long) {

                        }

                        override fun onFinish() {
                            if (card.get(selectedItem1) != card.get(selectedItem2)){
                                card.set(selectedItem1,R.color.colorPrimaryDark)
                                card.set(selectedItem2,R.color.colorPrimaryDark)
                            }else{
                                found = found + 1
                                userScore = userScore + 20
                                scoreText3.text = "Score: ".plus(userScore.toString())
                            }
                            selectedItem1 = -1
                            selectedItem2 = -1
                            numOpen = 0
                            gameAdapter.notifyDataSetChanged();
                            if (found == 6){
                                //gametimer.cancel()
                                userScore = (userScore + (3*timeleft/1000)).toInt()
                                scoreText3.text = "Score: ".plus(userScore.toString())

                                database = FirebaseDatabase.getInstance().getReference("users")

                                val userListener = object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        for (snapshot in dataSnapshot.children) {
                                            if (snapshot.child("nickname").value == user.email!!.split("@")[0]){
                                                val userId = snapshot.key
                                                val highscore = snapshot.child("highscore").value.toString().toInt()
                                                val score = snapshot.child("score").value.toString().toInt()
                                                database.child(userId!!).child("score").setValue(userScore + score).addOnSuccessListener {
                                                    if (userScore > highscore){
                                                        database.child(userId!!).child("highscore").setValue(userScore)
                                                    }
                                                    updateUI()
                                                }
                                            }
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                    }
                                }
                                database.addValueEventListener(userListener)
                            }
                        }
                    }
                    timer.start()
                }else{
                    numOpen = 1
                    selectedItem2 = -1
                }
            }
        }
    }

    private fun updateUI(){
        if (activity != null){
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.root_layout,  Level4()).commit()
        }
    }
}
