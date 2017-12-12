package com.example.chris.canvasdraw

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.ViewTreeObserver
import android.widget.Button
import com.example.chris.canvasdraw.view.ClockView

class MainActivity : AppCompatActivity() {
    private lateinit var clockView: ClockView
    private lateinit var startButton: Button
    private lateinit var resetButton: Button
    private var seconds = 0
    private val timer by lazy {
        object: CountDownTimer(600 * 1000L, 1000L) {
            override fun onFinish() {
            }

            override fun onTick(p0: Long) {
                print(p0)
                seconds++
                clockView.tick(seconds)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clockView = findViewById(R.id.clockView)
        startButton = findViewById(R.id.btn_start)
        resetButton = findViewById(R.id.btn_reset)

        resetButton.setOnClickListener {
            timer.cancel()
            clockView.reset()
        }
        startButton.setOnClickListener {
            timer.start()
        }
    }

    override fun onStop() {
        timer.cancel()
        super.onStop()
    }
}
