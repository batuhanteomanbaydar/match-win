package com.example.matchwin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_card.view.*

class LeaderAdapter(val data : List<User>) : RecyclerView.Adapter<LeaderAdapter.LeaderViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderViewHolder {
        return LeaderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.user_card, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: LeaderViewHolder, position: Int) {
        val user = data[position]
        holder.view.leaderName.text = user.nickname
        holder.view.leaderScore.text = user.highscore.toString()
        Picasso.get().load(user.avatar).into(holder.view.imageView2);
    }


    inner class LeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}