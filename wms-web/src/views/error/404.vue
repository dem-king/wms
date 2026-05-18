<template>
  <div class="error-page">
    <h1>404</h1>
    <p>页面未找到</p>
    <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
  </div>
</template>

<style scoped>
.error-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
h1 { font-size: 72px; color: #409eff; margin-bottom: 8px; }
p { font-size: 18px; color: #909399; margin-bottom: 20px; }
</style>
