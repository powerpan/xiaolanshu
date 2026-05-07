<script setup>
import { computed } from 'vue'
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

const todayLabel = computed(() => {
  const now = new Date()
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return `${now.getMonth() + 1}月${now.getDate()}日 · ${weekDays[now.getDay()]}`
})
</script>

<template>
  <header class="topbar">
    <div class="topbar-headline">
      <p>
        <span class="eyebrow-dot" aria-hidden="true"></span>
        <span>{{ isAdmin ? '管理端' : '欢迎回来' }}</span>
        <span class="eyebrow-divider" aria-hidden="true"></span>
        <span class="eyebrow-meta">{{ todayLabel }}</span>
      </p>
      <h1>
        <span class="greet-name">{{ greetingName }}</span><span class="greet-comma">，</span>
        <span class="greet-rest">{{ isAdmin ? '管理内容、动作库和账号' : '今天也稳一点进步' }}</span>
      </h1>
    </div>
    <div class="topbar-actions">
      <button class="role-badge" type="button" :title="isAdmin ? '进入用户管理' : '进入个人资料'" @click="emit('open-profile')">
        <span class="role-badge-dot" :class="{ admin: isAdmin }" aria-hidden="true"></span>
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

.topbar-headline {
  min-width: 0;
}

.topbar p {
  margin: 0 0 8px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px 6px 8px;
  border-radius: 999px;
  background: linear-gradient(110deg, rgba(255, 244, 227, 0.85), rgba(255, 211, 154, 0.55));
  border: 1px solid rgba(233, 121, 26, 0.18);
  color: #8a3d12;
  font-size: 11px;
  font-weight: 900;
  letter-spacing: 1.4px;
  text-transform: uppercase;
  box-shadow: 0 6px 16px rgba(233, 121, 26, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.6);
}

.eyebrow-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 30%, #ffb454 0%, #e9791a 60%, #7b371b 100%);
  box-shadow: 0 0 0 3px rgba(233, 121, 26, 0.18), 0 0 12px rgba(255, 180, 84, 0.55);
  animation: eyebrow-pulse 2.4s ease-in-out infinite;
}

.eyebrow-divider {
  width: 1px;
  height: 12px;
  background: linear-gradient(180deg, transparent, rgba(122, 55, 27, 0.55), transparent);
}

.eyebrow-meta {
  font-weight: 800;
  letter-spacing: 1px;
  color: #7b371b;
  font-feature-settings: 'tnum';
}

@keyframes eyebrow-pulse {
  0%, 100% { box-shadow: 0 0 0 3px rgba(233, 121, 26, 0.18), 0 0 8px rgba(255, 180, 84, 0.45); }
  50% { box-shadow: 0 0 0 4px rgba(233, 121, 26, 0.28), 0 0 18px rgba(255, 180, 84, 0.85); }
}

.topbar h1 {
  margin: 0;
  max-width: 100%;
  letter-spacing: -0.2px;
  font-size: clamp(22px, 2.6vw, 30px);
  font-weight: 800;
  line-height: 1.2;
  overflow-wrap: anywhere;
}

.greet-name {
  background: linear-gradient(120deg, #3a2117 0%, #7b371b 35%, #e9791a 75%, #ffb454 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.greet-comma {
  color: #c65a1a;
}

.greet-rest {
  color: #2b211c;
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
  display: inline-flex;
  align-items: center;
  gap: 8px;
  border: 1px solid #eadfd4;
  border-radius: 999px;
  padding: 8px 14px;
  background: #fff;
  color: #4e4038;
  font-weight: 800;
  cursor: pointer;
  transition: border-color 220ms ease, color 220ms ease, transform 220ms ease, box-shadow 220ms ease, background 220ms ease;
}

.role-badge-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #6dbc63;
  box-shadow: 0 0 0 3px rgba(109, 188, 99, 0.18);
}

.role-badge-dot.admin {
  background: #e9791a;
  box-shadow: 0 0 0 3px rgba(233, 121, 26, 0.22);
}

.role-badge:hover {
  border-color: #efb07b;
  color: #7b371b;
  transform: translateY(-1px);
  background: linear-gradient(120deg, #fff, #fff4e3);
  box-shadow: 0 12px 24px rgba(217, 111, 31, 0.18);
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
