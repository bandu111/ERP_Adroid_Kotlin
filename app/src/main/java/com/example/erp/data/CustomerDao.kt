package com.example.erp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.erp.db.CustomerDbHelper.CustomerDbHelper

data class Customer(
    val id: Long? = null,
    val name: String,
    val company: String?,
    val phone: String?,
    val email: String?,
    val status: String,
    val lastFollow: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
){
    constructor(name: String,company: String,phone: String,email: String,status: String) : this(
        id = null,
        name = name,
        company = company,
        phone = phone,
        email = email,
        status = status,
        lastFollow = null
    )
}



class CustomerDao(context: Context) {
    private val helper = CustomerDbHelper(context.applicationContext)

    fun insert(customer: Customer): Long {
        val db = helper.writableDatabase
        val cv = ContentValues().apply {
            put("name", customer.name)
            put("company", customer.company)
            put("phone", customer.phone)
            put("email", customer.email)
            put("status", customer.status)
            put("last_follow", customer.lastFollow)
            put("created_at", customer.createdAt)
            put("updated_at", customer.updatedAt)
        }
        return db.insert("customers", null, cv)
    }

    fun update(customer: Customer): Int {
        requireNotNull(customer.id) { "update 需要 id" }
        val db = helper.writableDatabase
        val cv = ContentValues().apply {
            put("name", customer.name)
            put("company", customer.company)
            put("phone", customer.phone)
            put("email", customer.email)
            put("status", customer.status)
            put("last_follow", customer.lastFollow)
            put("updated_at", System.currentTimeMillis())
        }
        return db.update("customers", cv, "id=?", arrayOf(customer.id.toString()))
    }

    fun delete(id: Long): Int {
        val db = helper.writableDatabase
        return db.delete("customers", "id=?", arrayOf(id.toString()))
    }
    fun delete(name: String): Int {
        val db = helper.writableDatabase
        return db.delete("customers", "name=?", arrayOf(name.toString()))
    }

    fun getById(id: Long): Customer? {
        val db = helper.readableDatabase
        db.rawQuery("SELECT * FROM customers WHERE id=?", arrayOf(id.toString())).use { c ->
            return if (c.moveToFirst()) c.toCustomer() else null
        }
    }


    fun getAll(): List<Customer> {
        val db = helper.readableDatabase
        db.rawQuery("SELECT * FROM customers ORDER BY updated_at DESC", null).use { c ->
            val list = mutableListOf<Customer>()
            while (c.moveToNext()) list += c.toCustomer()
            return list
        }
    }
    fun getTagsAll(tag: String): List<Customer> {
        val db = helper.readableDatabase
        db.rawQuery("SELECT * FROM customers WHERE status = ?", arrayOf(tag)).use { c ->
            val list = mutableListOf<Customer>()
            while (c.moveToNext()) list += c.toCustomer()
            return list
        }
    }
    fun searchByName(keyword: String): List<Customer> {
        val db = helper.readableDatabase
        db.rawQuery(
            "SELECT * FROM customers WHERE name LIKE ? ORDER BY updated_at DESC",
            arrayOf("%$keyword%")
        ).use { c ->
            val list = mutableListOf<Customer>()
            while (c.moveToNext()) list += c.toCustomer()
            return list
        }
    }

    private fun Cursor.toCustomer(): Customer {
        return Customer(
            id = getLong(getColumnIndexOrThrow("id")),
            name = getString(getColumnIndexOrThrow("name")),
            company = getString(getColumnIndexOrThrow("company")),
            phone = getString(getColumnIndexOrThrow("phone")),
            email = getString(getColumnIndexOrThrow("email")),
            status = getString(getColumnIndexOrThrow("status")),
            lastFollow = getString(getColumnIndexOrThrow("last_follow")),
            createdAt = getLong(getColumnIndexOrThrow("created_at")),
            updatedAt = getLong(getColumnIndexOrThrow("updated_at"))
        )
    }
}
