<!-- src/components/LoginForm.vue -->
<template>
  <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
    <el-form-item label="账号" prop="username">
      <el-input v-model="form.username" placeholder="学号/工号" clearable />
    </el-form-item>
    <el-form-item label="密码" prop="password">
      <el-input v-model="form.password" type="password" placeholder="密码" show-password />
    </el-form-item>
    <el-form-item>
      <el-button
        type="primary"
        @click="handleLogin"
        :loading="loading"
        style="width: 100%"
      >
        登录
      </el-button>
    </el-form-item>
  </el-form>
  <p v-if="error" style="color: #f56c6c; text-align: center; margin-top: 10px">{{ error }}</p>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const form = ref({ username: '', password: '' })
const formRef = ref()
const loading = ref(false)
const error = ref('')

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const emit = defineEmits(['login-success'])

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  error.value = ''

  try {
    const res = await axios.post('/api/auth/login', form.value)
    localStorage.setItem('authToken', res.data.token)

    // 可选：获取用户信息存入全局状态（如 Pinia/Vuex）
    // 这里简单处理，只通知父组件登录成功
    emit('login-success')
  } catch (err) {
    error.value = err.response?.data?.message || '用户名或密码错误'
    console.error('登录失败:', err)
  } finally {
    loading.value = false
  }
}
</script>