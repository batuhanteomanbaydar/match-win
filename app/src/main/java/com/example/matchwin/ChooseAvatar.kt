package com.example.matchwin


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_signup.*


class ChooseAvatar : Fragment() {

    private lateinit var avatarList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_choose_avatar, container, false)
        avatarList = view.findViewById(R.id.avatarList)
        var layoutManager = GridLayoutManager(activity, 3, GridLayoutManager.VERTICAL, true)
        avatarList.layoutManager = layoutManager
        val avatars = ArrayList<Int>()
        val urls = ArrayList<String>()
        avatars.add(R.drawable.avatar_1)
        avatars.add(R.drawable.avatar_2)
        avatars.add(R.drawable.avatar_3)
        avatars.add(R.drawable.avatar_4)
        avatars.add(R.drawable.avatar_5)
        avatars.add(R.drawable.avatar_6)
        urls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcSrdYcBbcTUNyC3M7MuYwmpnaVlb6Xtr9wJtapw8zdaaNTdigGc")
        urls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcR7a68VelObSQtgLPuoK1pV1sYiBqjbELNiPtG2MJf7omw4-c_M")
        urls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcS8jhnphS1sVsi-e_xbXaRhJq7KCqr4bt301HpmB5_Bp8RpMH8t")
        urls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcRP9pOW71bZX7N_KpN494Ni5-wBNwIfAx1DXiKKx5LQXcFAwdHI")
        urls.add("https://media.istockphoto.com/vectors/pretty-girl-avatar-flat-cartoon-style-vector-illustration-vector-id1140166223?k=6&m=1140166223&s=170667a&w=0&h=1wFvyz9gPA-VT1ZdSVLt4CoiXDkl-l0R0PQWwReVRPQ=")
        urls.add("https://i.etsystatic.com/6251240/r/il/773165/805375969/il_570xN.805375969_gp6s.jpg")
        var gameAdapter = GameAdapter(avatars)
        avatarList.adapter = gameAdapter
        gameAdapter.onItemClick = { item ->
            val ldf = Signup()
            val args = Bundle()
            args.putString("avatarUrl",urls.get(item))
            ldf.setArguments(args)
            activity!!.supportFragmentManager.beginTransaction().add(R.id.root_layout, ldf).commit()
        }
        return view
    }
}
