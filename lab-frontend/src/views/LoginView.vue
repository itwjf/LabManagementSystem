<!-- src/views/LoginView.vue -->
<template>
  <div class="login-container">
    <el-card class="login-card" shadow="never">
      <template #header>
        <h2 style="text-align: center; margin: 0">实验室设备管理系统</h2>
      </template>
      <LoginForm @login-success="handleLoginSuccess" />
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import LoginForm from '@/components/LoginForm.vue'

const router = useRouter()

const handleLoginSuccess = () => {
  // 登录成功后跳转到主系统
  router.push('/')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.login-card {
  width: 400px;
  border-radius: 8px;
}
</style>