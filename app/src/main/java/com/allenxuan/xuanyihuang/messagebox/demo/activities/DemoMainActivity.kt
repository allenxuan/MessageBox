package com.allenxuan.xuanyihuang.messagebox.demo.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.allenxuan.xuanyihuang.messagebox.R
import com.allenxuan.xuanyihuang.messagebox.annotation.MessageReceive
import com.allenxuan.xuanyihuang.messagebox.core.MessageBox
import com.allenxuan.xuanyihuang.messagebox.demo.messages.*

class DemoMainActivity : AppCompatActivity() {
    private val textReceiver by lazy {
        findViewById<TextView>(R.id.main_activity_receive_text)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MessageBox.INSTANCE().subscribe(this)

        setContentView(R.layout.activity_demo_main)
        initViews()
    }

    private fun initViews() {
        supportActionBar?.title = "Demo Main Activity"
        findViewById<View>(R.id.activity_jump_button)?.setOnClickListener {
            startActivity(Intent(this, DemoSubordinateActivity::class.java))
        }
        findViewById<View>(R.id.sendMessage1)?.setOnClickListener {
            MessageBox.INSTANCE().sendMessage(Message1("Message1 received"))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MessageBox.INSTANCE().unSubscribe(this)
    }

    @MessageReceive
    fun onReceiveMessage2(message2: Message2) {
        textReceiver.text = message2.text
    }

    @MessageReceive
    fun onReceiveMessage3(message3: Message3) {
        textReceiver.text = message3.text
    }

    @MessageReceive
    fun onReceiveMessage4(message4: Message4) {
        textReceiver.text = message4.text
    }

    @MessageReceive
    fun onReceiveMessage5(message5: Message5) {
        textReceiver.text = message5.text
    }
}
