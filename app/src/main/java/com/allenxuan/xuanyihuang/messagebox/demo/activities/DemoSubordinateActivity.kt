package com.allenxuan.xuanyihuang.messagebox.demo.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.allenxuan.xuanyihuang.messagebox.R
import com.allenxuan.xuanyihuang.messagebox.core.MessageBox

class DemoSubordinateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MessageBox.INSTANCE().subscribe(this)

        setContentView(R.layout.activity_demo_subordinate)
        initViews()
    }

    private fun initViews(){
        supportActionBar?.title = "Demo Subordinate Activity"
    }

    override fun onDestroy() {
        super.onDestroy()
        MessageBox.INSTANCE().unSubscribe(this)
    }
}