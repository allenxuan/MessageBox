package com.allenxuan.xuanyihuang.messagebox.demo.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.allenxuan.xuanyihuang.messagebox.R
import com.allenxuan.xuanyihuang.messagebox.core.MessageBox

class DemoMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MessageBox.INSTANCE().subscribe(this)

        setContentView(R.layout.activity_demo_main)
        initViews()
    }

    private fun initViews() {
        supportActionBar?.title = "Demo Main Activity"
        findViewById<View>(R.id.button)?.setOnClickListener {
            startActivity(Intent(this, DemoSubordinateActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MessageBox.INSTANCE().unSubscribe(this)
    }
}
