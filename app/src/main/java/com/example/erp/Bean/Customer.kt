package com.example.erp.Bean

data class Customer(
   val id: Long?,          // 数据库自增主键，对应 INTEGER PRIMARY KEY AUTOINCREMENT
   val name: String,
   val company: String?,   // 可空
   val phone: String?,
   val email: String?,
   val status: String,     // 潜在/跟进中/合作中/已签约
   val lastFollow: String  // "2025-08-08" 这种日期
)
