<script setup>
import { SwitchButton } from '@element-plus/icons-vue'

defineProps({
  isAdmin: {
    type: Boolean,
    default: false,
  },
  greetingName: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['logout', 'open-profile'])
</script>

<template>
  <header class="topbar">
    <div>
      <p>{{ isAdmin ? '管理端' : '欢迎回来' }}</p>
      <h1>{{ isAdmin ? `${greetingName}，管理内容、动作库和账号` : `${greetingName}，今天也稳一点进步` }}</h1>
    </div>
    <div class="topbar-actions">
      <button class="role-badge" type="button" :title="isAdmin ? '进入用户管理' : '进入个人资料'" @click="emit('open-profile')">
        {{ isAdmin ? '管理员模式' : '普通用户' }}
      </button>
      <el-button :icon="SwitchButton" @click="emit('logout')">退出</el-button>
    </div>
  </header>
</template>

<style scoped>
.topbar {
  min-height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 16px;
}

.topbar > div:first-child {
  min-width: 0;
}

.topbar p {
  margin: 0 0 6px;
  color: #d96b1f;
  font-size: 12px;
  font-weight: 900;
  text-transform: uppercase;
}

.topbar h1 {
  margin: 0;
  max-width: 100%;
  letter-spacing: 0;
  font-size: clamp(22px, 2.4vw, 28px);
  line-height: 1.22;
  overflow-wrap: anywhere;
}

.topbar-actions {
  min-width: 0;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex-wrap: wrap;
  gap: 10px;
}

.role-badge {
  appearance: none;
  border: 1px solid #eadfd4;
  border-radius: 8px;
  padding: 8px 12px;
  background: #fff;
  color: #4e4038;
  font-weight: 800;
  cursor: pointer;
  transition: border-color 180ms ease, color 180ms ease, transform 180ms ease, box-shadow 180ms ease;
}

.role-badge:hover {
  border-color: #efb07b;
  color: #7b371b;
  transform: translateY(-1px);
  box-shadow: 0 10px 22px rgba(217, 111, 31, 0.14);
}

@media (max-width: 820px) {
  .topbar {
    grid-template-columns: 1fr;
    flex-direction: column;
    align-items: stretch;
  }

  .topbar h1 {
    font-size: 26px;
  }

  .topbar-actions {
    width: 100%;
    display: grid;
    grid-template-columns: 1fr;
  }

  .topbar-actions .el-button,
  .role-badge {
    width: 100%;
    margin-left: 0 !important;
  }
}

@media (max-width: 520px) {
  .topbar h1 {
    font-size: 24px;
  }
}
</style>
