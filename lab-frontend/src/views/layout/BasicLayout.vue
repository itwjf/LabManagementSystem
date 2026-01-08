<!--
  <template> 标签内写 HTML 结构（但用的是 Vue 的语法）
-->
<template>
  <!-- el-container 是 Element Plus 的布局容器 -->
  <el-container style="height: 100vh"> <!-- 100vh = 占满整个浏览器高度 -->

    <!-- 顶部导航栏 -->
    <el-header height="60px" style="background-color: #409EFF; color: white; display: flex; align-items: center; padding-left: 20px">
      <h2 style="margin: 0; font-weight: bold">实验室设备管理系统</h2>

      <!-- 已登录时显示用户信息和退出按钮 -->
    <div style="color: white; display: flex; align-items: center; gap: 10px">
      欢迎，{{ user?.realName || user?.username || '访客' }}
      <el-button size="small" type="info" @click="logout">退出</el-button>
    </div>
    
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

          <el-menu-item index="/borrows">
            <el-icon><DocumentCopy /></el-icon>
            <span>借用记录</span>
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
import { onMounted, ref } from 'vue'
import { Monitor as Device, DocumentCopy } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const user = ref(null)

const fetchCurrentUser = async () => {
    // ✅ 第一步：从 localStorage 获取 token
  const token = localStorage.getItem('authToken')
 // 如果没有 token，直接返回（未登录状态）
  if (!token) {
    return
  }

  try {
    // ✅ 第二步：带上 Authorization 头
    const res = await axios.get('/api/auth/me', {
      headers: {
        Authorization: `Bearer ${token}` // ← 现在 token 已定义！
      }
    })
    user.value = res
  } catch (err) {
    console.error('获取当前用户失败:', err)
    // 可选：如果 token 无效，自动登出
    if (err.response?.status === 401) {
      logout()
    }
  }
}

const logout = () => {
  localStorage.removeItem('authToken')
  user.value = null
  router.push('/login')
}

onMounted(() => {
  fetchCurrentUser()
})
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