import { createRouter, createWebHashHistory } from 'vue-router'
// 布局组件
import BasicLayout from '@/views/layout/BasicLayout.vue'
import LoginView from '@/views/LoginView.vue'

// 业务页面
import DeviceList from '@/views/device/DeviceList.vue'

// 定义路由规则
const routes = [
  // 登录页：不使用 BasicLayout
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
    meta: { requiresAuth: false }
  },

  // 主系统：使用 BasicLayout 作为父布局
  {
    path: '/',
    component: BasicLayout,
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/devices' },
      { path: 'devices', name: 'Devices', component: DeviceList }
    ]
  },

  // 重定向未匹配路径到登录页
  { path: '/:pathMatch(.*)*', redirect: '/login' }
]

// 创建路由器实例
const router = createRouter({
    history: createWebHashHistory(), // 使用 # 模式（简单，适合实训）
    routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('authToken')

  if (to.meta.requiresAuth && !token) {
    // 需要登录但未登录 → 跳转登录页
    next('/login')
  } else if (to.path === '/login' && token) {
    // 已登录却访问登录页 → 跳回首页
    next('/')
  } else {
    next()
  }
})

export default router