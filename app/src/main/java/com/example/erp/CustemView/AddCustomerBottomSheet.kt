import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.erp.R
import com.example.erp.data.Customer
import com.example.erp.data.CustomerDao
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddCustomerBottomSheet : BottomSheetDialogFragment() {

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 注意：布局根节点（ScrollView）上就不要再设置 bottom_sheet_bg 了
        return inflater.inflate(R.layout.bottom_sheet_add_customer, container, false)
    }

    override fun onStart() {
        super.onStart()
        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }
    lateinit var dao: CustomerDao
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dao = CustomerDao(requireContext())
        val etName    = view.findViewById<TextInputEditText>(R.id.etName)
        val etCompany = view.findViewById<TextInputEditText>(R.id.etCompany)
        val etPhone   = view.findViewById<TextInputEditText>(R.id.etPhone)
        val etEmail   = view.findViewById<TextInputEditText>(R.id.etEmail)
        val chips     = view.findViewById<ChipGroup>(R.id.chipGroup)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btnCancel)
        val btnOk     = view.findViewById<MaterialButton>(R.id.btnConfirm)

        btnCancel.setOnClickListener { dismiss() }
        /**
         * 确认添加
         */
        btnOk.setOnClickListener {
            val name = etName.text?.toString()?.trim().orEmpty()
            val company = etCompany.text?.toString()?.trim().orEmpty()
            val phone = etPhone.text?.toString()?.trim().orEmpty()
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val status = when (chips.checkedChipId) {
                R.id.chipPotential -> "潜在客户"
                R.id.chipFollowing -> "跟进中"
                R.id.chipCooperate -> "合作中"
                R.id.chipSigned    -> "已签约"
                else -> ""
            }

            if (name.isBlank()) {
                Toast.makeText(requireContext(), "请填写客户姓名", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 增
            // 调用
//            val today = getCurrentDate()
//            println(today) // 输出: 2025-08-08
//            val id = dao.insert(
//                Customer(
//                    name = "$name",
//                    company = "$company",
//                    phone = "$phone",
//                    email = "$email",
//                    status = "$status",
//                    lastFollow = "$today"
//                )
//            )
//            print(id)

            setFragmentResult(REQ_ADD_CUSTOMER, bundleOf(
                "name" to name,
                "company" to company,
                "phone" to phone,
                "email" to email,
                "status" to status
            ))
            dismiss()
        }
    }

    companion object {
        const val REQ_ADD_CUSTOMER = "req_add_customer"
    }
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
