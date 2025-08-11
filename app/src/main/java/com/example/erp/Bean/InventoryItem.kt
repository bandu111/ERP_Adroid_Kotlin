package com.example.erp.data

data class InventoryItem(
    val id: Long? = null,
    val name: String,          // 商品名称
    val sku: String,           // 商品编号/条码
    val category: String?,     // 分类（电子设备/办公家具等）
    val quantity: Int,         // 当前库存
    val unit: String?,         // 单位（台/件/箱…）
    val location: String?,     // 库位（A-01-03…）
    val minStock: Int = 0,     // 低库存阈值
    val note: String? = null,  // 备注
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
