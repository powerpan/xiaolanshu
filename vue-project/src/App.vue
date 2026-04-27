<script setup>

</script>

<template>
    <router-view>

    </router-view>
</template>

<style>
body {
  margin: 0;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f0e7 0%, #e8e0d5 100%);
  position: relative;
}

/* 点状图案叠加层 */
body::before {
  content: "";
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: radial-gradient(#c0a080 1px, transparent 1px);
  background-size: 24px 24px;
  opacity: 0.08;
  pointer-events: none;
}
</style>

