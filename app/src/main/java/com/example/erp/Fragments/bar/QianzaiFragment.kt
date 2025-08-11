package com.example.erp.Fragments.bar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.erp.Fragments.Adapter.MyCustomerAdapter

import com.example.erp.R
import com.example.erp.data.CustomerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QianzaiFragment: Fragment() {
    private var tagName: String = ""
    private lateinit var dao: CustomerDao
    private lateinit var rv: RecyclerView
    private val data = mutableListOf<com.example.erp.data.Customer>()
    private lateinit var adapter: MyCustomerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tagName = arguments?.getString(ARG_ID) ?: "潜在客户"
        print(tagName.toString())

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.qinzai_fragment,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dao = CustomerDao(requireContext())
        rv = view.findViewById(R.id.rRecyclerView_item1)

        // RecyclerView
        rv.layoutManager = LinearLayoutManager(requireContext())

        adapter = MyCustomerAdapter(data,childFragmentManager, requireContext())
        loadTagsCustomers()
        rv.adapter = adapter
        parentFragmentManager.setFragmentResultListener(RESULT_KEY, viewLifecycleOwner) { _, bundle ->
            // 先更新 tagName
            tagName = bundle.getString("arg_TagName", "潜在客户")
            // 再查库
            loadTagsCustomers()
        }

    }

    private fun loadTagsCustomers() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val list = dao.getTagsAll(tagName)
            Log.d("TAG", "loadNamesCustomers: ${tagName.trim()} ")
            withContext(Dispatchers.Main) {
                data.clear()
                data.addAll(list)
                adapter.notifyDataSetChanged()
            }
        }
    }

    companion object{
        private const val ARG_ID="arg_TagName"
        const val RESULT_KEY = "update_action"
    }
}