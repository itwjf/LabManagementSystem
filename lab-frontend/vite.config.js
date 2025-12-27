import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path' // ★ 新增：用于处理路径

import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      // ★ 关键配置：让 @ 指向 src 目录
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端地址
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api')
      }
    }
  }
})
