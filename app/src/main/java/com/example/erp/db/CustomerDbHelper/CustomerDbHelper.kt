package com.example.erp.db.CustomerDbHelper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
private const val DB_NAME = "erp.db"
private const val DB_VERSION = 1
class CustomerDbHelper(context: Context): SQLiteOpenHelper(context,DB_NAME,null,DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("""
            CREATE TABLE customers (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              name TEXT NOT NULL,
              company TEXT,
              phone TEXT,
              email TEXT,
              status TEXT NOT NULL DEFAULT '潜在客户',
              last_follow TEXT,
              created_at INTEGER NOT NULL,
              updated_at INTEGER NOT NULL
            );
        """.trimIndent())

        db.execSQL("CREATE INDEX idx_customers_name ON customers(name);")
        db.execSQL("CREATE INDEX idx_customers_status ON customers(status);")


        db.execSQL("""
            CREATE TABLE IF NOT EXISTS inventory_items (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              name TEXT NOT NULL,
              sku TEXT NOT NULL UNIQUE,
              category TEXT,
              quantity INTEGER NOT NULL DEFAULT 0,
              unit TEXT,
              location TEXT,
              min_stock INTEGER NOT NULL DEFAULT 0,
              note TEXT,
              created_at INTEGER NOT NULL,
              updated_at INTEGER NOT NULL
            );
        """.trimIndent())
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_inv_name ON inventory_items(name);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_inv_category ON inventory_items(category);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_inv_quantity ON inventory_items(quantity);")
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        TODO("Not yet implemented")
    }

}