package com.allenxuan.xuanyihuang.messagebox.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.allenxuan.xuanyihuang.messagebox.R
import com.allenxuan.xuanyihuang.messagebox.core.MessageBox

class DemoMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_main)

        MessageBox.INSTANCE().subscribe(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        MessageBox.INSTANCE().unSubscribe(this)
    }
}
