package com.example.erp.Fragments.bar

import AddCustomerBottomSheet
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.erp.Bean.Customer
import com.example.erp.CustemView.MoreBottomSheet
import com.example.erp.Fragments.Adapter.MyCustomerAdapter
import com.example.erp.Fragments.CustomerFragment
import com.example.erp.R
import com.example.erp.data.CustomerDao
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuanbuFragment : Fragment(R.layout.quanbu_fragment) {
    private var searchName: String = ""
    private var tagName: String = ""
    private lateinit var dao: CustomerDao
    private lateinit var rv: RecyclerView
    private lateinit var fab: FloatingActionButton

    // 列表数据放为成员，适配器持有它的引用
    private val data = mutableListOf<com.example.erp.data.Customer>()
    private lateinit var adapter: MyCustomerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchName = arguments?.getString(ARG_ID) ?: ""
        tagName = arguments?.getString(ARG_ID2) ?: ""
        print(searchName.toString())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dao = CustomerDao(requireContext())
        rv = view.findViewById(R.id.rRecyclerView_item0)
        fab = view.findViewById(R.id.fabAdd)

        // RecyclerView
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = MyCustomerAdapter(data,childFragmentManager, requireContext())
        rv.adapter = adapter

        // 初始化加载数据库
        loadAllCustomers()

        // FAB：弹出新增
        fab.setOnClickListener {
            AddCustomerBottomSheet().show(childFragmentManager, "addCustomer")
        }

        parentFragmentManager.setFragmentResultListener(RESULT_KEY, viewLifecycleOwner) { _, bundle ->
//            val q = bundle.getString("arg_SearchName", "")
//            // 先更新搜索词
            loadTagsCustomers()                // 再按新词查询
        }
        parentFragmentManager.setFragmentResultListener("arg_Name", viewLifecycleOwner) { _, bundle ->
//            val q = bundle.getString("arg_SearchName", "")
//            // 先更新搜索词
            loadAllCustomers()                // 再按新词查询
        }
        parentFragmentManager.setFragmentResultListener(RESULT_KEY, viewLifecycleOwner) { _, bundle ->
            val q = bundle.getString("arg_SearchName", "")
            searchName = q                      // 先更新搜索词
            loadNamesCustomers()                // 再按新词查询
        }
        childFragmentManager.setFragmentResultListener (MoreBottomSheet.RESULT_KEY,viewLifecycleOwner){_,bundle ->
            val deletedId = bundle.getLong("deletedId", -1L)
            if (deletedId != -1L) {
                // 方式 A：最稳，直接重查数据库
                loadAllCustomers()
            }
        }
        childFragmentManager.setFragmentResultListener (EdCustomerBottomSheet.RESULT_KEY,viewLifecycleOwner){_,bundle ->
            val id=  bundle.getLong("id")
            val edId = bundle.getLong("edId", id)
            if (edId != -1L) {
                // 方式 A：最稳，直接重查数据库
                loadAllCustomers()
            }
        }

        // 接收 BottomSheet 回传结果
        childFragmentManager.setFragmentResultListener(
            AddCustomerBottomSheet.REQ_ADD_CUSTOMER, viewLifecycleOwner
        ) { _: String, bundle: Bundle ->
            val name = bundle.getString("name").orEmpty()
            val company = bundle.getString("company")
            val phone = bundle.getString("phone")
            val email = bundle.getString("email")
            val status = bundle.getString("status").orEmpty()
            val today = currentDate()

            // IO 线程写库
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val newId = dao.insert(
                    com.example.erp.data.Customer( // ← 注意用你 DAO 里定义的 data class
                        id = null,
                        name = name,
                        company = company,
                        phone = phone,
                        email = email,
                        status = status.ifBlank { "潜在客户" },
                        lastFollow = today
                    )
                )
                // 回主线程更新 UI
                withContext(Dispatchers.Main) {
                    data.add(0, com.example.erp.data.Customer(newId, name, company, phone, email, status, today))
                    adapter.notifyItemInserted(0)
                    rv.scrollToPosition(0)
                }
            }
        }
    }

    /** 初始化加载全部客户 */
    private fun loadAllCustomers() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val list = dao.getAll()
            withContext(Dispatchers.Main) {
                data.clear()
                data.addAll(list)
                adapter.notifyDataSetChanged()
            }
        }
    }
    private fun loadTagsCustomers() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val list = dao.getTagsAll(searchName)
            Log.d("TAG", "loadNamesCustomers: ${searchName} ")
            withContext(Dispatchers.Main) {
                data.clear()
                data.addAll(list)
                adapter.notifyDataSetChanged()
            }
        }
    }
    /** 加载模糊查询客户 */
    private fun loadNamesCustomers() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val list = dao.searchByName(searchName.trim())

            Log.d("TAG", "loadNamesCustomers: $searchName ")
            print(searchName)

            withContext(Dispatchers.Main) {
                data.clear()
                data.addAll(list)
                adapter.notifyDataSetChanged()
//                adapter.notifyItemInserted(0)
//                rv.scrollToPosition(0)
            }
        }
    }
    private fun currentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
    companion object{
        private const val ARG_ID = "arg_SearchName"
        private const val ARG_ID2 = "arg_TagName"
        private const val ARG_BARID = "arg_Name"
        const val RESULT_KEY = "update_action"
        fun newInstance(searchName: String) = QuanbuFragment().apply {
            arguments = Bundle().apply { putString(ARG_ID, searchName) }
        }
    }

}
