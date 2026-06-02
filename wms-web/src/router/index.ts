import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { setupRouterGuard } from './guard'

const staticRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled', affix: true }
      },
      {
        path: 'warehouse/visual',
        name: 'WarehouseVisual',
        component: () => import('@/views/warehouse/visual/index.vue'),
        meta: { title: '库房可视化', hidden: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', hidden: true }
      },
      {
        path: 'system/message',
        name: 'SystemMessage',
        component: () => import('@/views/system/message/index.vue'),
        meta: { title: '消息中心', hidden: true }
      },
      {
        path: 'report/inbound',
        name: 'ReportInbound',
        component: () => import('@/views/report/inbound/index.vue'),
        meta: { title: '入库统计', hidden: true }
      },
      {
        path: 'report/outbound',
        name: 'ReportOutbound',
        component: () => import('@/views/report/outbound/index.vue'),
        meta: { title: '出库统计', hidden: true }
      },
      {
        path: 'report/stock',
        name: 'ReportStock',
        component: () => import('@/views/report/stock/index.vue'),
        meta: { title: '库存统计', hidden: true }
      },
      {
        path: 'report/borrow-return',
        name: 'ReportBorrowReturn',
        component: () => import('@/views/report/borrow-return/index.vue'),
        meta: { title: '借还统计', hidden: true }
      },
      {
        path: 'report/scrap',
        name: 'ReportScrap',
        component: () => import('@/views/report/scrap/index.vue'),
        meta: { title: '报废报表', hidden: true }
      },
      {
        path: 'report/transfer',
        name: 'ReportTransfer',
        component: () => import('@/views/report/transfer/index.vue'),
        meta: { title: '调拨报表', hidden: true }
      },
      {
        path: 'report/alert',
        name: 'ReportAlert',
        component: () => import('@/views/report/alert/index.vue'),
        meta: { title: '预警报表', hidden: true }
      },
      {
        path: 'report/cost',
        name: 'ReportCost',
        component: () => import('@/views/report/cost/index.vue'),
        meta: { title: '费用核算', hidden: true }
      },
      {
        path: 'monitor/stock-alert',
        name: 'MonitorStockAlert',
        component: () => import('@/views/monitor/stock-alert/index.vue'),
        meta: { title: '库存预警', hidden: true }
      },
      {
        path: 'monitor/overdue-return',
        name: 'MonitorOverdueReturn',
        component: () => import('@/views/monitor/overdue-return/index.vue'),
        meta: { title: '逾期归还', hidden: true }
      },
      {
        path: 'log/operation',
        name: 'LogOperation',
        component: () => import('@/views/log/operation/index.vue'),
        meta: { title: '操作日志', hidden: true }
      },
      {
        path: 'log/login',
        name: 'LogLogin',
        component: () => import('@/views/log/login/index.vue'),
        meta: { title: '登录日志', hidden: true }
      }
    ]
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404', hidden: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: staticRoutes
})

setupRouterGuard(router)

export default router
