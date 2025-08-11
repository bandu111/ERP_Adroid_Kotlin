ERP Android Kotlin（ERP_Android_Kotlin）
一个以 Kotlin 编写的轻量级 ERP 移动端系统，搭建于 Android 平台，涵盖了客户管理、库存管理、人事管理三个核心功能模块，适合新手练习，中小企业或团队进行 ERP 流程演示与二次开发。

⭐️功能模块概览
模块	功能简介
客户管理	添加 / 查询 / 编辑客户档案，提升销售协作效率
库存管理	实时查看库存趋势、库存分类占比，直观反映库存状态
人事管理	员工档案录入、员工搜索筛选、职位与状态管理

⭐️技术栈 & 架构亮点
语言：Kotlin - 简洁高效、协程支持，构建现代化 Android 应用。
UI 控件：WilliamChart 3.x - 展示库存趋势折线图及分类占比环形图。
数据库：sqlite 支持持久化存储员工、库存等数据。
动态筛选：实现多重过滤与搜索功能，包括关键字搜索、Spinner 根据部门与在职状态筛选。
底部弹出页面：使用 BottomSheetDialogFragment 展示员工或库存详情，提升用户体验。
依赖注入（可选）：可集成 Dagger/Hilt 构建更加模块化的架构。

⭐️如何运行
git clone https://github.com/bandu111/ERP_Adroid_Kotlin.git
Android Studio 打开项目；
添加必要依赖（包括 WilliamChart），确保 build.gradle 指向正确的库版本；
构建运示例功能模块；
根据业务需求自行调整分页、网络或 CRUD 接口逻辑。
