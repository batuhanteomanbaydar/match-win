package com.example.matchwin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadRegister(Register())
    }

    private fun loadRegister(frag1: Register) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.root_layout, frag1)
        ft.commit()
    }
}
