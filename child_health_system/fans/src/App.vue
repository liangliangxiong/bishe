<script setup>
</script>

<template>
  <router-view></router-view>
</template>

<style lang="scss">
/* 移除全局滚动条 */
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow: hidden;
}

/* 如果需要在某些容器内滚动，可以给特定容器添加以下样式 */
.scrollable {
  overflow-y: auto;
  
  &::-webkit-scrollbar {
    width: 6px;
    height: 6px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: #ccc;
    border-radius: 3px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
}
</style>
