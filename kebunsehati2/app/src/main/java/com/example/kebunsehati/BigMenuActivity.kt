package com.example.kebunsehati

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_big_menu.*

class BigMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_big_menu)
        bt_preorder.setOnClickListener { intent(MenuActivity::class.java)
        finish()}
    }
}
