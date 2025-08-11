package com.example.erp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.erp.Bean.Employee
import com.example.erp.Fragments.Adapter.RenShiAdapter
import com.example.erp.R

class OrdersFragment : Fragment() {

    private val allList = mutableListOf<Employee>()   // 原始数据
    private lateinit var adapter: RenShiAdapter

    // 当前筛选条件
    private var currentQuery: String = ""
    private var currentDept: String = "全部"
    private var currentState: String = "全部"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.framgnet_orders, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val search = view.findViewById<SearchView>(R.id.eartch_home)
        val spDept = view.findViewById<Spinner>(R.id.sprine1)   // 部门
        val spState = view.findViewById<Spinner>(R.id.sprine2)  // 在职状态
        val rv = view.findViewById<RecyclerView>(R.id.recyview_ord)

        // 初始化 Spinner
        val deptAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.contries, android.R.layout.simple_spinner_item
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spDept.adapter = deptAdapter

        val stateAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.contries2, android.R.layout.simple_spinner_item
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spState.adapter = stateAdapter

        // 初始化列表与适配器
        adapter = RenShiAdapter(mutableListOf(), requireContext())
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        // ====== 造几条测试数据（你已有就保留，这里可替换/追加）======
        allList.addAll(
            listOf(
                Employee(1, "张伟", "在职", "销售部", "销售经理", "13800008888", 12000.0,
                    "zhang.wei@company.com", "北京朝阳区建国门外大街1号",
                    "李女士", "13600006666", "6222000012345678", "2025-08-08", R.drawable.shangwu),
                Employee(2, "王芳", "离职", "人事部", "HR专员", "13900007777", 9000.0,
                    "wang.fang@company.com", "上海浦东新区世纪大道1号",
                    "王先生", "13700005555", "6222000011122233", "2025-07-22",R.drawable.shangwu2),
                Employee(3, "李强", "在职", "技术部", "安卓工程师", "13700006666", 20000.0,
                    "li.qiang@company.com", "深圳南山区科苑路8号",
                    "张女士", "13500003333", "6222000098765432", "2025-08-10",R.drawable.shangwu3),
                Employee(4, "琉璃", "在职", "人事部", "HR专员", "13900007777", 9000.0,
                    "wang.fang@company.com", "上海浦东新区世纪大道1号",
                    "琉女士", "13700005555", "6222000011122233", "2025-07-22",R.drawable.shangwu2),
            )
        )
        applyFilters() // 初次展示

        // ====== 搜索 ======
        // 展开搜索框（可选）
        search.isIconified = false
        search.queryHint = "搜索姓名/部门/职位/手机号"

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentQuery = query.orEmpty()
                applyFilters()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText.orEmpty()
                applyFilters()
                return true
            }
        })

        // ====== 部门筛选 ======
        spDept.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, v: View?, position: Int, id: Long
            ) {
                currentDept = parent?.getItemAtPosition(position)?.toString() ?: "全部"
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // ====== 在职状态筛选（在职/离职/全部）======
        spState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, v: View?, position: Int, id: Long
            ) {
                currentState = parent?.getItemAtPosition(position)?.toString() ?: "全部"
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /** 根据当前条件过滤并刷新列表 */
    private fun applyFilters() {
        val q = currentQuery.trim().lowercase()

        val filtered = allList.filter { e ->
            // 关键字（姓名/部门/职位/手机号/邮箱）模糊匹配
            (q.isEmpty() || listOf(
                e.name, e.department, e.position, e.phoneNumber, e.email
            ).any { it.lowercase().contains(q) })
                    // 部门筛选（"全部" 时不过滤）
                    && (currentDept == "全部" || e.department == currentDept)
                    // 在职状态筛选（"全部" 时不过滤）
                    && (currentState == "全部" || e.state == currentState)
        }

        adapter.updateData(filtered.toMutableList())
    }
}
