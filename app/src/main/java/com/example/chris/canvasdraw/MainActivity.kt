package com.example.chris.canvasdraw

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewTreeObserver
import com.example.chris.canvasdraw.view.ClockView

class MainActivity : AppCompatActivity() {
    private lateinit var clockView: ClockView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clockView = findViewById(R.id.clockView)
    }
}
