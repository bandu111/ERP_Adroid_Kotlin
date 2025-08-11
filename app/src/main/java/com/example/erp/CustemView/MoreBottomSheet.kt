package com.example.erp.CustemView

import AddCustomerBottomSheet
import EdCustomerBottomSheet
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.erp.Bean.More_item_Bean
import com.example.erp.Fragments.Adapter.ItemsAdapter
import com.example.erp.R
import com.example.erp.data.CustomerDao
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.mutableListOf

class MoreBottomSheet() : BottomSheetDialogFragment(R.layout.more_bottomsheep_layout){
    private var customerId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customerId = arguments?.getLong(ARG_ID) ?: -1L

    }
    // ✅ 关键：这里把容器背景换成“只有上边两个圆角”的 drawable
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val sheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            sheet?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bottom_sheet_bg)
            // 可选：如果你想阴影更自然，也可以把 sheet 的 elevation 调一调：
            // ViewCompat.setElevation(sheet!!, 0f)
        }
        return dialog
    }
    private lateinit var dao: CustomerDao

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var recyclerView=view.findViewById<RecyclerView>(R.id.recyclerview_item)
        var _list= mutableListOf<More_item_Bean>()
        _list.add(More_item_Bean(R.drawable.tianjia,"编辑信息","修改用户信息"))
        _list.add(More_item_Bean(R.drawable.tianjia,"删除信息","删除用户信息"))

        recyclerView.adapter = ItemsAdapter(_list, requireContext()) { position, bean ->
            when (position) {
                0 -> { // 编辑信息
//
                    // TODO: 打开编辑界面，比如：
                    EdCustomerBottomSheet.newInstance(customerId)
                        .show(parentFragmentManager, "EditCustomer")
                    dismiss() // 先关闭底部弹窗
                }
                1 -> { // 删除信息
//
                    // IO 线程写库
                    if (customerId <= 0) { dismiss(); return@ItemsAdapter }
                    dao = CustomerDao(requireContext())
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        val rows = dao.delete(customerId) // ← 建议用按 id 删除的方法
                        withContext(Dispatchers.Main) {
                            if (rows > 0) {
                                parentFragmentManager.setFragmentResult(
                                    RESULT_KEY, Bundle().apply { putLong("deletedId", customerId) }
                                )
                            }
                            dismiss()
                        }
                    }
                }
            }
        }
        recyclerView.layoutManager= LinearLayoutManager(requireContext())

    }
    companion object {
        private const val ARG_ID = "arg_customer_id"
        const val RESULT_KEY = "customer_action"

        fun newInstance(customerId: Long): MoreBottomSheet {
            return MoreBottomSheet().apply {
                arguments = Bundle().apply { putLong(ARG_ID, customerId) }
            }
        }
    }

}