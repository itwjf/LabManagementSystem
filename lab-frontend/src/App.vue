
<template>
  <div id="app">
    <!-- 路由出口：所有页面都会在这里显示 -->
    <!-- 根据当前路由是否需要认证，决定渲染哪个布局 -->
    <router-view />
  </div>
</template>

<script setup>
// 这里可以留空，因为我们不需要在 App.vue 里写逻辑
</script>

<style>
/* 可选：全局样式重置 */
#app {
  font-family: 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  min-height: 100vh;
  margin: 0;
  padding: 0;
}
</style>