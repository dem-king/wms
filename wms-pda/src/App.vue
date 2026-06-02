<script setup lang="ts">
/**
 * WMS-PDA 应用根组件
 * 监听应用生命周期，初始化全局状态
 */
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
import { useAppStore } from '@/store/app'
import { useOfflineStore } from '@/store/offline'

onLaunch(() => {
  console.log('WMS-PDA App Launch')

  // 初始化应用全局状态
  const appStore = useAppStore()
  appStore.initNetworkListener()

  // 初始化离线存储和网络监听
  const offlineStore = useOfflineStore()
  offlineStore.init().catch((err) => {
    console.error('离线存储初始化失败:', err)
  })
})

onShow(() => {
  console.log('WMS-PDA App Show')
})

onHide(() => {
  console.log('WMS-PDA App Hide')
})
</script>

<style>
/* 全局样式 */
page {
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 28rpx;
  color: #333333;
}
</style>