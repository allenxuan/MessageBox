package com.allenxuan.xuanyihuang.messagebox.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.allenxuan.xuanyihuang.messagebox.R
import com.allenxuan.xuanyihuang.messagebox.annotation.MessageReceive
import com.allenxuan.xuanyihuang.messagebox.annotation.SchedulerType
import com.allenxuan.xuanyihuang.messagebox.others.MessageScheduler

class DemoMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_main)
        func1(100)
    }

    private fun func1(@SchedulerType para: Int) {

    }

    @MessageReceive(executeThread = MessageScheduler.sync, executeDelay = 0)
    fun func2(event1: Event1) {

    }

    @MessageReceive(executeThread = MessageScheduler.mainThread, executeDelay = 500)
    fun func3(event2: Event2) {

    }

//    @MessageReceive(executeThread = MessageScheduler.mainThread, executeDelay = 0)
//    fun func4(event2: Event2) {
//
//    }
}
