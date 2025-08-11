import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.example.erp.R
import com.example.erp.data.Customer
import com.example.erp.data.CustomerDao
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EdCustomerBottomSheet : BottomSheetDialogFragment() {

    private lateinit var dao: CustomerDao

    private lateinit var etName: TextInputEditText
    private lateinit var etCompany: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var chips: ChipGroup
    private lateinit var btnCancel: MaterialButton
    private lateinit var btnOk: MaterialButton

    private var customerId: Long = -1L
    private var loadedCustomer: Customer? = null   // 保存已加载的客户，用于保留 createdAt 等

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customerId = arguments?.getLong(ARG_ID) ?: -1L
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val sheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            sheet?.background = ContextCompat.getDrawable(requireContext(), R.drawable.bottom_sheet_bg)
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_edit_customer, container, false)
    }

    override fun onStart() {
        super.onStart()
        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dao = CustomerDao(requireContext())

        etName    = view.findViewById(R.id.edName)
        etCompany = view.findViewById(R.id.edCompany)
        etPhone   = view.findViewById(R.id.edPhone)
        etEmail   = view.findViewById(R.id.edEmail)
        chips     = view.findViewById(R.id.edGroup)
        btnCancel = view.findViewById(R.id.edCancel)
        btnOk     = view.findViewById(R.id.edConfirm)

        btnCancel.setOnClickListener { dismiss() }

        if (customerId <= 0L) {
            Toast.makeText(requireContext(), "参数缺失：customerId", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }

        // 异步查询并回显
        loadCustomerAndFill(customerId)

        btnOk.setOnClickListener {
            val name = etName.text?.toString()?.trim().orEmpty()
            if (name.isBlank()) {
                Toast.makeText(requireContext(), "请填写客户姓名", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val company = etCompany.text?.toString()?.trim().orEmpty()
            val phone   = etPhone.text?.toString()?.trim().orEmpty()
            val email   = etEmail.text?.toString()?.trim().orEmpty()
            val status  = when (chips.checkedChipId) {
                R.id.chipPotential -> "潜在客户"
                R.id.chipFollowing -> "跟进中"
                R.id.chipCooperate -> "合作中"
                R.id.chipSigned    -> "已签约"
                else               -> "潜在客户"
            }
            val today = currentDate()

            // 以库为准：保留原 createdAt
            val createdAtKeep = loadedCustomer?.createdAt ?: System.currentTimeMillis()

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val rows = dao.update(
                    Customer(
                        id = customerId,                 // ★ 必须带 id：按 id 更新
                        name = name,
                        company = company.ifBlank { null },
                        phone = phone.ifBlank { null },
                        email = email.ifBlank { null },
                        status = status,
                        lastFollow = today,
                        createdAt = createdAtKeep,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                withContext(Dispatchers.Main) {
                    if (rows > 0) {
                        parentFragmentManager.setFragmentResult(
                            RESULT_KEY, bundleOf("updatedId" to customerId)
                        )
                    }
                    dismiss()
                }
            }
        }
    }

    private fun loadCustomerAndFill(id: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            val c = withContext(Dispatchers.IO) { dao.getById(id) }
            if (c == null) {
                Toast.makeText(requireContext(), "未找到该客户", Toast.LENGTH_SHORT).show()
                dismiss()
                return@launch
            }
            loadedCustomer = c
            // 回显到 UI（主线程）
            etName.setText(c.name)
            etCompany.setText(c.company.orEmpty())
            etPhone.setText(c.phone.orEmpty())
            etEmail.setText(c.email.orEmpty())
            when (c.status) {
                "潜在客户" -> chips.check(R.id.chipPotential)
                "跟进中"   -> chips.check(R.id.chipFollowing)
                "合作中"   -> chips.check(R.id.chipCooperate)
                "已签约"   -> chips.check(R.id.chipSigned)
                else       -> chips.clearCheck()
            }
        }
    }

    private fun currentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    companion object {
        private const val ARG_ID = "arg_customer_id"
        const val RESULT_KEY = "update_action"

        fun newInstance(customerId: Long) = EdCustomerBottomSheet().apply {
            arguments = Bundle().apply { putLong(ARG_ID, customerId) }
        }
    }
}
