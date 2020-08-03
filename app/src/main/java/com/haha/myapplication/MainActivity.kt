package com.haha.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigInteger

class MainActivity : AppCompatActivity() {

    var mLeftNum = 0
    var mRightNUm = 0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnAddLeft.setOnClickListener {
            pKProgressView.addLeft(mLeftNum++)
        }
        btnAddRight.setOnClickListener {
            pKProgressView.addRight(mRightNUm++)
        }
        btnSubtractLeft.setOnClickListener {
            if (mLeftNum == 0) return@setOnClickListener
            pKProgressView.subtractLeft(mLeftNum--)
        }
        btnSubtractRight.setOnClickListener {
            if (mRightNUm == 0) return@setOnClickListener
            pKProgressView.subtractRight(mRightNUm--)
        }


    }
}