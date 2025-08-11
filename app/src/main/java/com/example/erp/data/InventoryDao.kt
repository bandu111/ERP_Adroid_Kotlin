package com.example.erp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.erp.db.CustomerDbHelper.CustomerDbHelper

class InventoryDao(context: Context) {
    private val helper = CustomerDbHelper(context)

    // 新增：返回新 id
    fun insert(item: InventoryItem): Long {
        val db = helper.writableDatabase
        val cv = toCV(item.copy(updatedAt = System.currentTimeMillis()))
        cv.remove("id")
        return db.insertOrThrow("inventory_items", null, cv)
    }

    // 批量新增（事务）
    fun insertAll(items: List<InventoryItem>): Int {
        val db = helper.writableDatabase
        var count = 0
        db.beginTransaction()
        try {
            items.forEach {
                val cv = toCV(it.copy(updatedAt = System.currentTimeMillis()))
                cv.remove("id")
                db.insertOrThrow("inventory_items", null, cv)
                count++
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return count
    }

    // 按 id 更新
    fun update(item: InventoryItem): Int {
        requireNotNull(item.id) { "update 需要 id" }
        val db = helper.writableDatabase
        val cv = toCV(item.copy(updatedAt = System.currentTimeMillis()))
        cv.remove("id")
        return db.update("inventory_items", cv, "id=?", arrayOf(item.id.toString()))
    }

    // 删除
    fun delete(id: Long): Int {
        val db = helper.writableDatabase
        return db.delete("inventory_items", "id=?", arrayOf(id.toString()))
    }

    // 按 id 查询
    fun getById(id: Long): InventoryItem? {
        val db = helper.readableDatabase
        db.rawQuery("SELECT * FROM inventory_items WHERE id=?", arrayOf(id.toString())).use { c ->
            return if (c.moveToFirst()) c.toInventoryItem() else null
        }
    }

    // 全部
    fun getAll(): List<InventoryItem> {
        val db = helper.readableDatabase
        db.rawQuery("SELECT * FROM inventory_items ORDER BY updated_at DESC", emptyArray()).use { c ->
            val list = mutableListOf<InventoryItem>()
            while (c.moveToNext()) list += c.toInventoryItem()
            return list
        }
    }

    // 按名称或 SKU 搜索（忽略大小写）
    fun searchByNameOrSku(keyword: String): List<InventoryItem> {
        val q = keyword.trim()
        val db = helper.readableDatabase
        if (q.isEmpty()) return getAll()
        db.rawQuery(
            "SELECT * FROM inventory_items " +
                    "WHERE name LIKE ? COLLATE NOCASE OR sku LIKE ? COLLATE NOCASE " +
                    "ORDER BY updated_at DESC",
            arrayOf("%$q%", "%$q%")
        ).use { c ->
            val list = mutableListOf<InventoryItem>()
            while (c.moveToNext()) list += c.toInventoryItem()
            return list
        }
    }

    // 按名称或 SKU 搜索（忽略大小写）
    fun searchBystate(keyword: String): List<InventoryItem> {
        val q = keyword.trim()
        val db = helper.readableDatabase
        if (q.isEmpty()) return getAll()
        db.rawQuery(
            "SELECT * FROM inventory_items " +
                    "WHERE category =?" +
                    "ORDER BY updated_at DESC",
            arrayOf("$q")
        ).use { c ->
            val list = mutableListOf<InventoryItem>()
            while (c.moveToNext()) list += c.toInventoryItem()
            return list
        }
    }

    // 按分类
    fun getByCategory(category: String): List<InventoryItem> {
        val db = helper.readableDatabase
        db.rawQuery(
            "SELECT * FROM inventory_items WHERE category = ? ORDER BY updated_at DESC",
            arrayOf(category)
        ).use { c ->
            val list = mutableListOf<InventoryItem>()
            while (c.moveToNext()) list += c.toInventoryItem()
            return list
        }
    }

    // 低库存（quantity < min_stock）
    fun lowStock(): List<InventoryItem> {
        val db = helper.readableDatabase
        db.rawQuery(
            "SELECT * FROM inventory_items WHERE quantity < min_stock ORDER BY quantity ASC",
            emptyArray()
        ).use { c ->
            val list = mutableListOf<InventoryItem>()
            while (c.moveToNext()) list += c.toInventoryItem()
            return list
        }
    }

    // 库存调整（正数入库，负数出库）— 事务保证并发安全
    fun adjustStock(id: Long, delta: Int): Boolean {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            val cur = db.rawQuery("SELECT quantity FROM inventory_items WHERE id=?", arrayOf(id.toString()))
            val currentQty = cur.use { if (it.moveToFirst()) it.getInt(0) else return false }
            val newQty = (currentQty + delta).coerceAtLeast(0) // 不允许负库存，可按需放开
            val cv = ContentValues().apply {
                put("quantity", newQty)
                put("updated_at", System.currentTimeMillis())
            }
            val rows = db.update("inventory_items", cv, "id=?", arrayOf(id.toString()))
            if (rows > 0) {
                db.setTransactionSuccessful()
                return true
            }
        } finally {
            db.endTransaction()
        }
        return false
    }

    fun count(): Long {
        val db = helper.readableDatabase
        db.rawQuery("SELECT COUNT(*) FROM inventory_items", emptyArray()).use { c ->
            return if (c.moveToFirst()) c.getLong(0) else 0L
        }
    }

    fun deleteAll(): Int {
        val db = helper.writableDatabase
        return db.delete("inventory_items", null, null)
    }

    // --- 内部工具 ---
    private fun toCV(i: InventoryItem): ContentValues = ContentValues().apply {
        i.id?.let { put("id", it) }
        put("name", i.name)
        put("sku", i.sku)
        put("category", i.category)
        put("quantity", i.quantity)
        put("unit", i.unit)
        put("location", i.location)
        put("min_stock", i.minStock)
        put("note", i.note)
        put("created_at", i.createdAt)
        put("updated_at", i.updatedAt)
    }

    private fun Cursor.toInventoryItem(): InventoryItem = InventoryItem(
        id = getLong(getColumnIndexOrThrow("id")),
        name = getString(getColumnIndexOrThrow("name")),
        sku = getString(getColumnIndexOrThrow("sku")),
        category = getString(getColumnIndexOrThrow("category")),
        quantity = getInt(getColumnIndexOrThrow("quantity")),
        unit = getString(getColumnIndexOrThrow("unit")),
        location = getString(getColumnIndexOrThrow("location")),
        minStock = getInt(getColumnIndexOrThrow("min_stock")),
        note = getString(getColumnIndexOrThrow("note")),
        createdAt = getLong(getColumnIndexOrThrow("created_at")),
        updatedAt = getLong(getColumnIndexOrThrow("updated_at"))
    )
}
