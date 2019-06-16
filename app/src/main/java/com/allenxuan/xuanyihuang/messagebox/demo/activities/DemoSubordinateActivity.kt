package com.allenxuan.xuanyihuang.messagebox.demo.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.allenxuan.xuanyihuang.messagebox.R
import com.allenxuan.xuanyihuang.messagebox.annotation.MessageReceive
import com.allenxuan.xuanyihuang.messagebox.core.MessageBox
import com.allenxuan.xuanyihuang.messagebox.demo.messages.*

class DemoSubordinateActivity : AppCompatActivity() {
    private val textReceiver by lazy {
        findViewById<TextView>(R.id.subordinate_activity_receive_text)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MessageBox.subscribe(this)

        setContentView(R.layout.activity_demo_subordinate)
        initViews()
    }

    private fun initViews() {
        supportActionBar?.title = "Demo Subordinate Activity"
        findViewById<View>(R.id.sendMessage4)?.setOnClickListener {
            MessageBox.sendMessage(Message4("Message4 received"))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MessageBox.unSubscribe(this)
    }

    @MessageReceive
    fun onReceiveMessage1(message1: Message1) {
        textReceiver.text = message1.text
    }

    @MessageReceive
    fun onReceiveMessage4(message2: Message2) {
        textReceiver.text = message2.text
    }

    @MessageReceive
    fun onReceiveMessage3(message3: Message3) {
        textReceiver.text = message3.text
    }

    @MessageReceive
    fun onReceiveMessage5(message5: Message5) {
        textReceiver.text = message5.text
    }
}