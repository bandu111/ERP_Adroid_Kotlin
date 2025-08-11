package com.example.erp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.erp.CustemView.EdInventoryButtomSheep
import com.example.erp.CustemView.InvShowButtonSheep
import com.example.erp.CustemView.InventoryButtomSheep
import com.example.erp.CustemView.MoreBottomSheet
import com.example.erp.Fragments.Adapter.InventoryAdapter
import com.example.erp.Fragments.bar.QuanbuFragment.Companion.RESULT_KEY
import com.example.erp.R
import com.example.erp.data.CustomerDao
import com.example.erp.data.InventoryDao
import com.example.erp.data.InventoryItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.mutableListOf

class InventoryFragment : Fragment() {
    private lateinit var dao: InventoryDao
    var _list = mutableListOf<InventoryItem>()
    lateinit var recy_inven: RecyclerView
    lateinit var starchBar: SearchView
    lateinit var quanbu: TextView
    lateinit var dianzi: TextView
    lateinit var bangong: TextView
    lateinit var jiaju: TextView
    lateinit var floatingAction: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        initData()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dao = InventoryDao(requireContext())
        quanbu = view.findViewById(R.id.quanbu2)
        dianzi = view.findViewById(R.id.dianzi)
        bangong = view.findViewById(R.id.bangong)
        jiaju = view.findViewById(R.id.jiaju)
        starchBar = view.findViewById(R.id.starchBar2)
        recy_inven = view.findViewById(R.id.recy_inven)
        floatingAction = view.findViewById(R.id.floatingAction)
     var    buttonImageview1 = view.findViewById<ImageButton>(R.id.buttonImageview1)



        recy_inven.layoutManager = LinearLayoutManager(requireContext())
        recy_inven.adapter = InventoryAdapter(_list, requireContext(),childFragmentManager)
        floatingAction.setOnClickListener {
            InventoryButtomSheep().show(childFragmentManager, "tag")
        }

        initData()
        buttonImageview1.setOnClickListener {
            InvShowButtonSheep().show(childFragmentManager,"tag")
        }
        childFragmentManager.setFragmentResultListener(InventoryButtomSheep.RESULT_KEY, viewLifecycleOwner) { _, bundle ->
//            val q = bundle.getString("arg_SearchName", "")
            val newId = bundle.getLong("itemId") // 获取返回的 ID

//            // 先更新搜索词
            initData()             // 再按新词查询
        }

        childFragmentManager.setFragmentResultListener(EdInventoryButtomSheep.RESULT_KEY, viewLifecycleOwner) { _, bundle ->
//            val q = bundle.getString("arg_SearchName", "")
            val newId = bundle.getLong("itemId") // 获取返回的 ID

//            // 先更新搜索词
            initData()             // 再按新词查询
        }
        starchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val q = query.toString().trim()
                filterData(q)  // 根据输入进行过滤
                starchBar.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val q = newText.toString().trim()
//                filterData(q)  // 根据输入进行过滤
                return false
            }
        })

        quanbu.setOnClickListener {
            filterData("")  // 显示全部
        }
        dianzi.setOnClickListener {
            filterData2("电子设备")  // 只显示电子设备
        }
        jiaju.setOnClickListener {
            filterData2("办公家具")  // 只显示办公家具
        }
        bangong.setOnClickListener {
            filterData2("办公用品")  // 只显示办公用品
        }
    }

    private fun filterData(query: String) {
        // 按照关键词过滤数据
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val filteredList = dao.searchByNameOrSku(query)
            withContext(Dispatchers.Main) {
                _list.clear()
                _list.addAll(filteredList)
                recy_inven.adapter?.notifyDataSetChanged()
            }
        }
    }
    private fun filterData2(query: String) {
        // 按照关键词过滤数据
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val filteredList = dao.getByCategory(query.toString().trim())
            withContext(Dispatchers.Main) {
                _list.clear()
                _list.addAll(filteredList)
                recy_inven.adapter?.notifyDataSetChanged()
            }
        }
    }
    // 初始化数据
    private fun initData() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val list = dao.getAll()
            withContext(Dispatchers.Main) {
                _list.clear()
                _list.addAll(list)
                recy_inven.adapter?.notifyDataSetChanged()  // 更新 RecyclerView
            }
        }
    }
}
