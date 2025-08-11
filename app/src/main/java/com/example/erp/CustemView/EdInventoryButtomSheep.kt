package com.example.erp.CustemView

import EdCustomerBottomSheet
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.erp.R
import com.example.erp.data.InventoryDao
import com.example.erp.data.InventoryItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EdInventoryButtomSheep: BottomSheetDialogFragment() {
    lateinit var dao: InventoryDao
    var list=mutableListOf<InventoryItem>()
    private lateinit var chips: ChipGroup
    private lateinit var chips2: ChipGroup
    lateinit var inventoryItem: InventoryItem

  lateinit var inputName :TextInputEditText
  lateinit var inputCode :TextInputEditText
  lateinit var inputSize :TextInputEditText
    private var customerId: Long = -1L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customerId = arguments?.getLong(ARG_ID) ?: -1L
    }
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.inventory_edite_bottomsheep_layout,container,false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        inputName = itemView.findViewById<TextInputEditText>(R.id.inputName) // 产品名称输入框
        inputCode = itemView.findViewById<TextInputEditText>(R.id.inputcode) // 产品编号输入框
        inputSize = itemView.findViewById<TextInputEditText>(R.id.inputsize) // 库存数量输入框
        dao=InventoryDao(requireContext())
        chips = itemView.findViewById<ChipGroup>(R.id.chipGroup1) // 产品标签

        chips2 = itemView.findViewById<ChipGroup>(R.id.group22)


        val btnCancel = itemView.findViewById<MaterialButton>(R.id.Cancel) // 取消按钮
        val btnConfirm = itemView.findViewById<MaterialButton>(R.id.Confirm) // 确认按钮
        displayer()
//        val status1  = when (chips.checkedChipId) {
//                    R.id.chip1-> "台"
//                    R.id.chip2-> "张"
//                    R.id.chip3-> "包"
//                    R.id.chip4-> "块"
//                    R.id.chip5-> "个"
//                    R.id.chip6-> "套"
//                    R.id.chip7-> "箱"
//            else               -> "台"
//        }
//        val status2  = when (chips2.checkedChipId) {
//            R.id.chipPotential-> "电子设备"
//            R.id.chipFollowing-> "办公家具"
//            R.id.chipCooperate->" 办公用品"
//            else               -> "电子设备"
//        }
        btnCancel.setOnClickListener {
            dismiss()
        }
        btnConfirm.setOnClickListener {
                // 获取输入框中的库存数量
                val size = inputSize.text.toString().toInt()
                // 根据 chip 的选择来确定单位和分类
                val status1 = when (chips.checkedChipId) {
                    R.id.chip1 -> "台"
                    R.id.chip2 -> "张"
                    R.id.chip3 -> "包"
                    R.id.chip4 -> "块"
                    R.id.chip5 -> "个"
                    R.id.chip6 -> "套"
                    R.id.chip7 -> "箱"
                    else -> "台"
                }
                val status2 = when (chips2.checkedChipId) {
                    R.id.chipPotentials -> "电子设备"
                    R.id.chipFollowings -> "办公家具"
                    R.id.chipCooperates -> "办公用品"
                    else -> "全部"
                }
            print(status2)

                // 创建新的 InventoryItem
                val inventoryItem = InventoryItem(
                    id=customerId,
                    name = inputName.text.toString().trim(),
                    sku = inputCode.text.toString().trim(),
                    category = status2,
                    quantity = size,
                    unit = status1,
                    location = "A-01-03",
                    minStock = 5,
                    note = "库存不足，建议及时补货",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                // 将该项插入数据库
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        dao.update(inventoryItem)
                    // 通知上一个页面更新
                    val resultBundle = Bundle().apply {
                        putString("itemName", inventoryItem.name)  // 返回数据
                        putInt("itemQuantity", inventoryItem.quantity)  // 返回数量等信息
                    }
                    parentFragmentManager.setFragmentResult(RESULT_KEY, resultBundle)
                    dismiss()// 假设你的 DAO 有一个插入单个项的方法
                }

        }

    }

    private fun displayer() {
        viewLifecycleOwner.lifecycleScope.launch {
            val c = withContext(Dispatchers.IO) { dao.getById(customerId) }
            inventoryItem=c!!
            inputName.setText(c.name)
            inputCode.setText(c.sku)
            inputSize.setText(c.quantity.toInt().toString())
            when (c.unit) {
                "台" ->chips.check( R.id.chip1 )
                "张" ->chips.check( R.id.chip2 )
                "包" ->chips.check( R.id.chip3 )
                "块" ->chips.check( R.id.chip4 )
                "个" ->chips.check( R.id.chip5 )
                "套" ->chips.check( R.id.chip6 )
                  else       -> chips.clearCheck()
            }
            when(c.category){
                "电子设备"->chips2.check(R.id.chipPotentials )
                "办公家具"->chips2.check(R.id.chipFollowings )
                "办公用品"->chips2.check(R.id.chipCooperates )
                else       -> chips2.clearCheck()
            }
        }

    }

    companion object {
        private const val ARG_ID = "arg_customer_id"
        const val RESULT_KEY = "tag"
        fun newInstance(customerId: Long) = EdInventoryButtomSheep().apply {
            arguments = Bundle().apply { putLong(ARG_ID, customerId) }
        }
    }
}