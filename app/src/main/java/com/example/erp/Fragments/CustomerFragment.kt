package com.example.erp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.erp.Fragments.Adapter.MyPageViewAdapter
import com.example.erp.Fragments.bar.QuanbuFragment
import com.example.erp.R

class CustomerFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customer,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 这里写 UI 逻辑，比如初始化 RecyclerView / Button 点击事件
        var quanbu =view.findViewById<TextView>(R.id.quanbu)
        var pageview2 =view.findViewById<ViewPager2>(R.id.pageview2)
        var qianzai =view.findViewById<TextView>(R.id.qianzai)
        var genjing =view.findViewById<TextView>(R.id.genjing)
        var hezuo =view.findViewById<TextView>(R.id.hezuo)
        var qianyue =view.findViewById<TextView>(R.id.qianyue)
        var starchBar =view.findViewById<SearchView>(R.id.starchBar)
        starchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val q = query?.trim().orEmpty()
                // ✅ 把查询词发给子Fragment们（包括 QuanbuFragment）
                childFragmentManager.setFragmentResult(
                    com.example.erp.Fragments.bar.QuanbuFragment.RESULT_KEY,
                    Bundle().apply { putString("arg_SearchName", q) }
                )
                starchBar.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String?) = false
        })

        val adapter = MyPageViewAdapter(this)
        pageview2.isUserInputEnabled=false
        pageview2.adapter= adapter
        quanbu.setOnClickListener {
            quanbu.setBackgroundResource(R.drawable.is_background_contina)

            qianzai.setBackgroundResource(R.drawable.bg_tab_normal)
            genjing.setBackgroundResource(R.drawable.bg_tab_normal)
            hezuo.setBackgroundResource(R.drawable.bg_tab_normal)
            qianyue.setBackgroundResource(R.drawable.bg_tab_normal)
            pageview2.currentItem = 0
            childFragmentManager.setFragmentResult(
                com.example.erp.Fragments.bar.QuanbuFragment.RESULT_KEY,
                Bundle().apply { putString("arg_Name", "全部") }
            )
        }

        qianzai.setOnClickListener {
            qianzai.setBackgroundResource(R.drawable.is_background_contina)
            quanbu.setBackgroundResource(R.drawable.bg_tab_normal)
            genjing.setBackgroundResource(R.drawable.bg_tab_normal)
            hezuo.setBackgroundResource(R.drawable.bg_tab_normal)
            qianyue.setBackgroundResource(R.drawable.bg_tab_normal)
            pageview2.currentItem = 1
//            pageview2.currentItem = 0
            childFragmentManager.setFragmentResult(
                com.example.erp.Fragments.bar.QianzaiFragment.RESULT_KEY,
                Bundle().apply { putString("arg_Name", "潜在客户") }
            )
        }
        genjing.setOnClickListener {
            genjing.setBackgroundResource(R.drawable.is_background_contina)

            qianzai.setBackgroundResource(R.drawable.bg_tab_normal)
            quanbu.setBackgroundResource(R.drawable.bg_tab_normal)
            hezuo.setBackgroundResource(R.drawable.bg_tab_normal)
            qianyue.setBackgroundResource(R.drawable.bg_tab_normal)
            pageview2.currentItem = 2
            childFragmentManager.setFragmentResult(
                com.example.erp.Fragments.bar.JinxingFragment.RESULT_KEY,
                Bundle().apply { putString("arg_Name", "更进中") }
            )
        }

        hezuo.setOnClickListener {
            hezuo.setBackgroundResource(R.drawable.is_background_contina)

            qianzai.setBackgroundResource(R.drawable.bg_tab_normal)
            quanbu.setBackgroundResource(R.drawable.bg_tab_normal)
            genjing.setBackgroundResource(R.drawable.bg_tab_normal)
            qianyue.setBackgroundResource(R.drawable.bg_tab_normal)
            pageview2.currentItem = 3
        }

        qianyue.setOnClickListener {
            qianyue.setBackgroundResource(R.drawable.is_background_contina)

            hezuo.setBackgroundResource(R.drawable.bg_tab_normal)
            qianzai.setBackgroundResource(R.drawable.bg_tab_normal)
            quanbu.setBackgroundResource(R.drawable.bg_tab_normal)
            genjing.setBackgroundResource(R.drawable.bg_tab_normal)

            pageview2.currentItem = 4
        }

    }
}