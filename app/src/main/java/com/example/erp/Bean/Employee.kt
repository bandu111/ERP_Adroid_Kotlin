package com.example.erp.Bean

data  class Employee(
    val id: Long? = null,
    val name: String ,         // 姓名
    val state: String,         //在职状态
    val department: String ,         // 部门
    val position: String ,           // 职位
    val phoneNumber: String ,         // 手机号码
    val monthlySalary: Double ,       // 月薪
    val email: String ,               // 邮箱地址
    val address: String ,             // 家庭住址
    val emergencyContact: String ,    // 紧急联系人
    val emergencyPhone: String ,     // 紧急联系电话
    val bankCardNumber: String ,      // 银行卡号
    val lastFollow: String,  // "2025-08-08" 这种日期
    val image: Int , //头像
)