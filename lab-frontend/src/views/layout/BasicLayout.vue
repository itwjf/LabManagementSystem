<!--
  <template> 标签内写 HTML 结构（但用的是 Vue 的语法）
-->
<template>
  <!-- el-container 是 Element Plus 的布局容器 -->
  <el-container style="height: 100vh"> <!-- 100vh = 占满整个浏览器高度 -->

    <!-- 顶部导航栏 -->
    <el-header height="60px" style="background-color: #409EFF; color: white; display: flex; align-items: center; padding-left: 20px">
      <h2 style="margin: 0; font-weight: bold">实验室设备管理系统</h2>
    </el-header>

    <!-- 主体区域（包含左侧菜单 + 中间内容） -->
    <el-container>
      <!-- 左侧菜单 -->
      <el-aside width="200px" style="background-color: #f5f7fa; border-right: 1px solid #e6e6e6">
        <el-menu
          :default-active="$route.path"
          router
          background-color="#f5f7fa"
          text-color="#333"
          active-text-color="#409EFF"
          style="border: none"
        >
          <el-menu-item index="/devices">
            <template #title>
              <el-icon><Device /></el-icon>
              <span>设备管理</span>
            </template>
          </el-menu-item>

          <el-menu-item index="/users">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
          </el-menu-item>

          <el-menu-item index="/settings">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统设置</span>
            </template>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 中间内容区：会动态显示不同的页面（如设备列表） -->
      <el-main>
        <router-view /> <!-- ★ 关键！这里会显示当前路由对应的页面 -->
      </el-main>
    </el-container>
  </el-container>
</template>

<!--
  <script setup> 是 Vue 3 的新语法，用来写 JavaScript 逻辑
-->
<script setup>
// 引入图标组件（Element Plus 提供）
import { Monitor } from '@element-plus/icons-vue'
</script>

<!--
  <style scoped> 写 CSS 样式，scoped 表示只作用于本组件
-->
<style scoped>
.el-header {
  box-shadow: 0 2px 4px rgba(0,0,0,0.1); /* 添加阴影，更美观 */
}
.el-aside {
  overflow-y: auto; /* 如果菜单很多，可以滚动 */
}
</style>