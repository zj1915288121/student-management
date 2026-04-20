<template>
  <div class="error-page">
    <div class="error-content">
      <h1 class="error-code">404</h1>
      <p class="error-message">抱歉，您访问的页面不存在</p>
      <el-button type="primary" size="large" class="back-btn" @click="$router.push('/dashboard')">
        <SvgIcon name="home" :size="16" :stroke-width="2" />
        <span>返回首页</span>
      </el-button>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.error-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: var(--color-background);
}

.error-content {
  text-align: center;
  padding: 60px;
}

.error-code {
  font-size: 120px;
  font-weight: 800;
  color: var(--color-primary);
  line-height: 1;
  margin-bottom: 16px;
  letter-spacing: -4px;
}

.error-message {
  font-size: 18px;
  color: var(--color-text-secondary);
  margin-bottom: 32px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  border-radius: var(--radius-md);
  font-weight: 500;
  font-size: 15px;
  padding: 12px 28px;
  transition: all var(--transition-fast);
}

.back-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}
</style>
