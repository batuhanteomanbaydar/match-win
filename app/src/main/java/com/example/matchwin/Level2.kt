package com.example.matchwin


import android.content.Context
import android.nfc.Tag
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_level2.*
import kotlinx.android.synthetic.main.fragment_level2.view.*
import kotlin.collections.ArrayList

class Level2 : Fragment() {

    companion object {

        fun newInstance(): Level2 {
            return Level2()
        }
    }

    private lateinit var gametimer: CountDownTimer

    private lateinit var auth: FirebaseAuth

    private lateinit var user: FirebaseUser

    private lateinit var database: DatabaseReference

    private lateinit var recyclerView2: RecyclerView

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

        val view: View = inflater.inflate(R.layout.fragment_level2, container, false)
        var layoutManager = GridLayoutManager(activity, 4, GridLayoutManager.VERTICAL, true)
        recyclerView2 = view.findViewById(R.id.recyclerView2)
        recyclerView2.layoutManager = layoutManager
        var min = 3
        var second = 0

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        view.timerText2.text = "Time: 0".plus(min.toString()).plus(".00")
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
                    view.timerText2.text = "Time: 0".plus((min + 1).toString()).plus(":00")
                }else if(second < 10){
                    view.timerText2.text = "Time: 0".plus(min.toString()).plus(":0").plus(second.toString())
                }else{
                    view.timerText2.text = "Time: 0".plus(min.toString()).plus(":").plus(second.toString())
                }
            }
            override fun onFinish() {
                if (found != 4){
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    private fun fetchData(){
        data.add(R.drawable.picture_1)
        data.add(R.drawable.picture_2)
        data.add(R.drawable.picture_3)
        data.add(R.drawable.picture_4)
        data.add(R.drawable.picture_1)
        data.add(R.drawable.picture_2)
        data.add(R.drawable.picture_3)
        data.add(R.drawable.picture_4)
        data.shuffle()
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
        recyclerView2.adapter = gameAdapter

        gameAdapter.onItemClick = { item ->
            if (card.get(item) == R.color.colorPrimaryDark) {
                numOpen = numOpen + 1
                if (numOpen == 1) {
                    selectedItem1 = item
                } else {
                    selectedItem2 = item
                }
                card.set(item, data.get(item))
                gameAdapter.notifyItemChanged(item)
                if (numOpen == 2) {
                    if (selectedItem1 != selectedItem2) {
                        val timer = object : CountDownTimer(500, 1000) {
                            override fun onTick(millisUntilFinished: Long) {

                            }

                            override fun onFinish() {
                                if (card.get(selectedItem1) != card.get(selectedItem2)) {
                                    card.set(selectedItem1, R.color.colorPrimaryDark)
                                    card.set(selectedItem2, R.color.colorPrimaryDark)
                                } else {
                                    found = found + 1
                                    userScore = userScore + 20
                                    scoreText2.text = "Score: ".plus(userScore.toString())
                                }
                                selectedItem1 = -1
                                selectedItem2 = -1
                                numOpen = 0
                                gameAdapter.notifyDataSetChanged();
                                if (found == 4) {
                                    gametimer.cancel()
                                    userScore = (userScore + (3 * timeleft / 1000)).toInt()
                                    scoreText2.text = "Score: ".plus(userScore.toString())
                                    //saveData()
                                    database = FirebaseDatabase.getInstance().getReference("users")

                                    val userListener = object : ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            for (snapshot in dataSnapshot.children) {
                                                if (snapshot.child("nickname").value == user.email!!.split(
                                                        "@"
                                                    )[0]
                                                ) {

                                                    val userId = snapshot.key
                                                    val highscore =
                                                        snapshot.child("highscore").value.toString()
                                                            .toInt()
                                                    val score =
                                                        snapshot.child("score").value.toString()
                                                            .toInt().plus(userScore)
                                                    database.child(userId!!).child("score")
                                                        .setValue(score).addOnCompleteListener {
                                                            if (userScore > highscore) {
                                                                database.child(userId!!).child("highscore")
                                                                    .setValue(userScore)
                                                                    .addOnCompleteListener {
                                                                        updateUI()
                                                                    }
                                                            }
                                                        }

                                                    break
                                                }
                                            }
                                            return
                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                        }
                                    }
                                    database.addListenerForSingleValueEvent(userListener)

                                }
                            }
                        }
                        timer.start()
                    } else {
                        numOpen = 1
                        selectedItem2 = -1
                    }
                }
            }
        }
    }

    private fun updateUI(){
        if (activity != null) {
            Toast.makeText(context,"update",Toast.LENGTH_SHORT).show()
            // 2
            activity!!.supportFragmentManager.beginTransaction()
                // 4
                .replace(
                    R.id.root_layout,
                    Level3.newInstance(),
                    "level3"
                )
                .commit()
        }
    }

}
