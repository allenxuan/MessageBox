package com.allenxuan.xuanyihuang.messagebox.demo.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.allenxuan.xuanyihuang.messagebox.R
import com.allenxuan.xuanyihuang.messagebox.annotation.MessageReceive
import com.allenxuan.xuanyihuang.messagebox.core.MessageBox
import com.allenxuan.xuanyihuang.messagebox.demo.messages.*

class DemoMainFragmentB : Fragment() {
    private var textReceiver: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MessageBox.INSTANCE().subscribe(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_fragment_demo_main_b, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
    }

    private fun initViews(rootView: View) {
        rootView.findViewById<View>(R.id.sendMessage3)?.setOnClickListener {
            MessageBox.INSTANCE().sendMessage(Message3("Message3 received"))
        }
        textReceiver = rootView.findViewById<TextView>(R.id.fragment_b_receive_text)
    }

    override fun onDestroy() {
        super.onDestroy()
        MessageBox.INSTANCE().unSubscribe(this)
    }

    @MessageReceive
    fun onReceiveMessage1(message1: Message1) {
        textReceiver?.text = message1.text
    }

    @MessageReceive
    fun onReceiveMessage2(message2: Message2) {
        textReceiver?.text = message2.text
    }

    @MessageReceive
    fun onReceiveMessage4(message4: Message4) {
        textReceiver?.text = message4.text
    }

    @MessageReceive
    fun onReceiveMessage5(message5: Message5) {
        textReceiver?.text = message5.text
    }
}