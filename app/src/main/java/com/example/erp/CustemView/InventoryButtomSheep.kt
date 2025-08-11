package com.example.erp.CustemView

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

class InventoryButtomSheep: BottomSheetDialogFragment() {
    lateinit var dao: InventoryDao
    var list=mutableListOf<InventoryItem>()
    private lateinit var chips: ChipGroup
    private lateinit var chips2: ChipGroup
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
        return inflater.inflate(R.layout.inventory_bottomsheep_layout,container,false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        val inputName = itemView.findViewById<TextInputEditText>(R.id.inputName) // 产品名称输入框
        val inputCode = itemView.findViewById<TextInputEditText>(R.id.inputcode) // 产品编号输入框
        val inputSize = itemView.findViewById<TextInputEditText>(R.id.inputsize) // 库存数量输入框
        dao=InventoryDao(requireContext())
        chips = itemView.findViewById<ChipGroup>(R.id.chipGroup) // 产品标签

        chips2 = itemView.findViewById<ChipGroup>(R.id.group2_)


        val btnCancel = itemView.findViewById<MaterialButton>(R.id.Cancel) // 取消按钮
        val btnConfirm = itemView.findViewById<MaterialButton>(R.id.Confirm) // 确认按钮
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
            if (inputName.text.toString().isNullOrEmpty() ||
                inputCode.text.toString().isNullOrEmpty() ||
                inputSize.text.toString().isNullOrEmpty()) {
                Toast.makeText(context, "请检查输入框是否为空", Toast.LENGTH_LONG).show()
            } else {
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
                    R.id.chipPotential_1 -> "电子设备"
                    R.id.chipFollowing_1 -> "办公家具"
                    R.id.chipCooperate_1 -> "办公用品"
                    else -> "电子设备"
                }

                // 创建新的 InventoryItem
                val inventoryItem = InventoryItem(
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
                  val newId = dao.insert(inventoryItem)
                    // 通知上一个页面更新
                    val resultBundle = Bundle().apply {
                        putLong("itemId", newId) // 将插入的 ID 返回给父页面
                    }
                    parentFragmentManager.setFragmentResult(InventoryButtomSheep.RESULT_KEY, resultBundle)
                    dismiss()// 假设你的 DAO 有一个插入单个项的方法
                }

            }
        }

    }
    companion object {
        private const val ARG_ID = "arg_customer_id"
        const val RESULT_KEY = "tag"

    }
}