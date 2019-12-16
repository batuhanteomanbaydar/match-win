package com.example.matchwin


import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_level4.*
import kotlinx.android.synthetic.main.fragment_level4.view.*
import kotlin.collections.ArrayList

class Level4: Fragment() {

    companion object {

        fun newInstance(): Level4 {
            return Level4()
        }
    }

    private lateinit var gametimer: CountDownTimer

    private lateinit var auth: FirebaseAuth

    private lateinit var user: FirebaseUser

    private lateinit var database: DatabaseReference

    private lateinit var recyclerView4: RecyclerView

    private var userScore: Int = 0

    var numOpen = 0
    var found = 0
    var selectedItem1 = -1;
    var selectedItem2 = -1;
    val data4 = ArrayList<Int>()
    val cards4 = ArrayList<Int>()
    var timeleft: Long = 180000

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_level4, container, false)
        var layoutManager = GridLayoutManager(activity, 4, GridLayoutManager.VERTICAL, true)
        recyclerView4 = view.findViewById(R.id.recyclerView4)
        recyclerView4.layoutManager = layoutManager
        var min = 3
        var second = 0

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        view.timerText4.text = "Time: 0".plus(min.toString()).plus(".00")
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
                    view.timerText4.text = "Time: 0".plus((min + 1).toString()).plus(":00")
                }else if(second < 10){
                    view.timerText4.text = "Time: 0".plus(min.toString()).plus(":0").plus(second.toString())
                }else{
                    view.timerText4.text = "Time: 0".plus(min.toString()).plus(":").plus(second.toString())
                }
            }
            override fun onFinish() {
                if (found != 8){
                    activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.root_layout, GameOver())
                        .commit()
                }
            }
        }
        gametimer.start()
        fetchData()
        return view
    }


    private fun fetchData(){
        data4.add(R.drawable.picture_1)
        data4.add(R.drawable.picture_2)
        data4.add(R.drawable.picture_3)
        data4.add(R.drawable.picture_4)
        data4.add(R.drawable.picture_5)
        data4.add(R.drawable.picture_6)
        data4.add(R.drawable.picture_7)
        data4.add(R.drawable.picture_8)
        data4.add(R.drawable.picture_1)
        data4.add(R.drawable.picture_2)
        data4.add(R.drawable.picture_3)
        data4.add(R.drawable.picture_4)
        data4.add(R.drawable.picture_5)
        data4.add(R.drawable.picture_6)
        data4.add(R.drawable.picture_7)
        data4.add(R.drawable.picture_8)
        data4.shuffle()
        cards4.add(R.color.colorPrimaryDark)
        cards4.add(R.color.colorPrimaryDark)
        cards4.add(R.color.colorPrimaryDark)
        cards4.add(R.color.colorPrimaryDark)
        cards4.add(R.color.colorPrimaryDark)
        cards4.add(R.color.colorPrimaryDark)
        cards4.add(R.color.colorPrimaryDark)
        cards4.add(R.color.colorPrimaryDark)
        cards4.add(R.color.colorPrimaryDark)
        cards4.add(R.color.colorPrimaryDark)
        cards4.add(R.color.colorPrimaryDark)
        cards4.add(R.color.colorPrimaryDark)
        cards4.add(R.color.colorPrimaryDark)
        cards4.add(R.color.colorPrimaryDark)
        cards4.add(R.color.colorPrimaryDark)
        cards4.add(R.color.colorPrimaryDark)
        showCards(cards4)
    }

    private fun showCards(card: ArrayList<Int>){
        var gameAdapter = GameAdapter(card)
        recyclerView4.adapter = gameAdapter

        gameAdapter.onItemClick = { item ->
            numOpen = numOpen + 1
            if(numOpen == 1){
                selectedItem1 = item
            }else{
                selectedItem2 = item
            }
            card.set(item,data4.get(item))
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
                                userScore = userScore + 40
                                scoreText4.text = "Score: ".plus(userScore.toString())
                            }
                            selectedItem1 = -1
                            selectedItem2 = -1
                            numOpen = 0
                            gameAdapter.notifyDataSetChanged();
                            if (found == 8){
                                //gametimer.cancel()
                                userScore = (userScore + (3*timeleft/1000)).toInt()
                                scoreText4.text = "Score: ".plus(userScore.toString())

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
                                                    if (activity != null) {
                                                        // 2
                                                        activity!!.supportFragmentManager.beginTransaction()
                                                            .remove(this@Level4)
                                                            // 4
                                                            .add(R.id.root_layout, Finish.newInstance(), "finish")
                                                            // 5
                                                            .commit()
                                                    }
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


}
