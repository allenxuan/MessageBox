package com.allenxuan.xuanyihuang.messagebox.demo.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.allenxuan.xuanyihuang.messagebox.R

class DemoSubordinateFragmentA : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_fragment_demo_subordinate_a, container, false)
    }
}