package com.example.erp.Fragments.Adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.erp.Fragments.bar.HezuoFragment
import com.example.erp.Fragments.bar.JinxingFragment
import com.example.erp.Fragments.bar.QianzaiFragment
import com.example.erp.Fragments.bar.QuanbuFragment
import com.example.erp.Fragments.bar.YuYueFragment

class MyPageViewAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
       return  when(position){
           0->QuanbuFragment()
           1 -> QianzaiFragment()
           2 -> JinxingFragment()
           3 -> HezuoFragment()
           else -> YuYueFragment()
       }
    }

    override fun getItemCount()=5
}