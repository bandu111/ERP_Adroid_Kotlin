package com.example.erp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.permissionx.guolindev.PermissionX

class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // 仅在需要旧式写存储时请求；Android 11+ 建议走 SAF，见下
        PermissionX.init(this)
            .permissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "需要文件权限以导出数据库备份",
                    "允许", "拒绝"
                )
            }
            .request { allGranted, _, _ ->
                if (allGranted) {
                  //  exportDbToDownloads() // TODO: 你实现拷贝数据库的方法
                } else {
                    Toast.makeText(this, "未授予文件权限，无法导出备份", Toast.LENGTH_SHORT).show()
                }
            }


        val navHost =  supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navController=navHost.navController
         val bottomNav=findViewById<BottomNavigationView>(R.id.bottom_nav);
        // 绑定 Navigation
        bottomNav.setupWithNavController(navController)
        // 默认选中“客户”标签（如果你想启动时就是这个）
        bottomNav.selectedItemId = R.id.nav_customer
    }
}