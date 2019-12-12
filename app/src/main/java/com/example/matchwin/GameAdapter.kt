package com.example.matchwin

import android.graphics.drawable.Drawable
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.memory_card.view.*

class GameAdapter(val data : List<Int>) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    var onItemClick: ((Int) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        return GameViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.memory_card, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val image = data[position]
        holder.view.cardImage.setImageResource(image)
    }


    inner class GameViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        init {
            view.setOnClickListener {
                onItemClick?.invoke(adapterPosition)
            }
        }
    }
}