package com.example.erp.CustemView

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.db.williamchart.view.DonutChartView
import com.db.williamchart.view.LineChartView
import com.example.erp.R
import com.example.erp.data.InventoryDao
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.YearMonth
import java.time.ZoneId
import kotlin.math.max

class InvShowButtonSheep : BottomSheetDialogFragment() {

    private lateinit var dao: InventoryDao

    private lateinit var tvTotalStock: TextView
    private lateinit var tvLowStock: TextView
    private lateinit var tvCategoryCount: TextView
    private lateinit var chartLiner: LineChartView
    private lateinit var donChart: DonutChartView

    private lateinit var legend1: TextView
    private lateinit var legend2: TextView
    private lateinit var legend3: TextView
    private lateinit var legend4: TextView
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
        return inflater.inflate(R.layout.fragment_inventory2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ==== 绑定控件 ====
        tvTotalStock = view.findViewById(R.id.tvTotalStock)
        tvLowStock = view.findViewById(R.id.tvLowStock)
        tvCategoryCount = view.findViewById(R.id.tvCategoryCount)

        chartLiner = view.findViewById(R.id.chartliner)
        donChart = view.findViewById(R.id.donchart)

        legend1 = view.findViewById(R.id.legend1)
        legend2 = view.findViewById(R.id.legend2)
        legend3 = view.findViewById(R.id.legend3)
        legend4 = view.findViewById(R.id.legend4)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initData()
        } else {
            // 可选：低版本提示或降级处理
            tvTotalStock.text = "-"
            tvLowStock.text = "-"
            tvCategoryCount.text = "-"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initData() {
        dao = InventoryDao(requireContext())
        val all = dao.getAll()           // 全量
        val low = dao.lowStock()         // 低库存

        // 顶部 3 指标
        val totalQty = all.sumOf { it.quantity }  // Int
        val categoryCount = all.map { it.category }.distinct().size
        tvTotalStock.text = totalQty.toString()
        tvLowStock.text = low.size.toString()
        tvCategoryCount.text = categoryCount.toString()

        // 折线趋势：最近 6 个月（基于 updatedAt）
        val months = lastMonths(6) // 从旧到新
        val monthPairs: List<Pair<String, Float>> = months.map { ym ->
            val start = ym.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val end = ym.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val sum = all.filter { it.updatedAt in start until end }
                .sumOf { it.quantity } // Int
                .toFloat()
            (ym.monthValue.toString() + "月") to sum
        }
        chartLiner.animation.duration = 900
        chartLiner.animate(monthPairs)

        // 环形占比：按分类汇总数量，取 Top3 + 其他
        // 环形占比数据：把可空 category 统一成非空
        val grouped: List<Pair<String, Float>> = all
            .groupBy { it.category ?: "未分类" }
            .map { (cat, items) -> cat to items.sumOf { it.quantity }.toFloat() }
            .sortedByDescending { it.second }

        val top3 = grouped.take(3)
        val othersValue = grouped.drop(3).sumOf { it.second.toDouble() }.toFloat()
        val sectors: List<Pair<String, Float>> = top3 + listOf("其他" to othersValue)


        // 4 色（与图例同步）
        val colors = listOf(
            Color.parseColor("#3F8CFF"), // 蓝
            Color.parseColor("#FFA726"), // 橙
            Color.parseColor("#43A047"), // 绿
            Color.parseColor("#9E9E9E")  // 灰
        )

        // 本版本 DonutChartView 传入 values 即可；颜色用 donutColors
        val values: List<Float> = sectors.map { it.second }
// 让 draw 时颜色与 sectors 顺序一致：由于内部会 reversed()，这里先反转一次
        donChart.donutColors = colors.take(values.size).reversed().toIntArray()

        // donutTotal 设定（避免 0），然后开动画
        val total = max(1f, values.sum()) // List<Float>.sum() -> Float
        donChart.donutTotal = total
        donChart.animation.duration = 900
        donChart.animate(values)



        // 右侧图例（名称 + 数值，与颜色同步）
        setLegend(legend1, sectors.getOrNull(0)?.first ?: "—", sectors.getOrNull(0)?.second ?: 0f, colors[0])
        setLegend(legend2, sectors.getOrNull(1)?.first ?: "—", sectors.getOrNull(1)?.second ?: 0f, colors[1])
        setLegend(legend3, sectors.getOrNull(2)?.first ?: "—", sectors.getOrNull(2)?.second ?: 0f, colors[2])
        setLegend(legend4, sectors.getOrNull(3)?.first ?: "—", sectors.getOrNull(3)?.second ?: 0f, colors[3])
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun lastMonths(n: Int): List<YearMonth> {
        val now = YearMonth.now()
        return (n - 1 downTo 0).map { now.minusMonths(it.toLong()) }
    }

    // 只把 “•” 点着色，保持和环形图一致
    private fun setLegend(tv: TextView, label: String, value: Float, color: Int) {
        val text = "•  $label  ${value.toInt()}"
        val span = SpannableString(text).apply {
            setSpan(ForegroundColorSpan(color), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        tv.text = span
    }
}
