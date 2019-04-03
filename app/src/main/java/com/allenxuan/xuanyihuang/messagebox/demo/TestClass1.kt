package com.allenxuan.xuanyihuang.messagebox.demo

import com.allenxuan.xuanyihuang.messagebox.annotation.MessageReceive
import com.allenxuan.xuanyihuang.messagebox.others.MessageScheduler

class TestClass1 {
    @MessageReceive(executeThread = MessageScheduler.sync, executeDelay = 200)
    fun go(event1: Event1) {
    }
}