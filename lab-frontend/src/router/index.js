import { createRouter, createWebHashHistory } from 'vue-router'
// 引入我们刚写的两个页面
import BasicLayout from '@/views/layout/BasicLayout.vue'
import DeviceList from '@/views/device/DeviceList.vue'

// 定义路由规则
const routes = [
    {
        path: '/',           // 根路径
        redirect: '/devices' // 自动跳转到设备管理页
    },
    {
        path: '/',           // 主布局的路径
        component: BasicLayout,
        children: [          // 子路由（显示在 <router-view> 里）
            {
                path: 'devices',
                component: DeviceList
            }
        ]
    }
]

// 创建路由器实例
const router = createRouter({
    history: createWebHashHistory(), // 使用 # 模式（简单，适合实训）
    routes
})

export default router