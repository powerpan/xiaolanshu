<script setup>
import yueluLogo from '@/assets/yuelu-logo.png'

defineProps({
  navGroups: {
    type: Array,
    required: true,
  },
  activeView: {
    type: String,
    required: true,
  },
  isAdmin: {
    type: Boolean,
    default: false,
  },
  displayName: {
    type: String,
    default: '',
  },
  goalText: {
    type: String,
    default: '',
  },
  weeklyFrequency: {
    type: Number,
    default: 0,
  },
})

const emit = defineEmits(['open-view'])
</script>

<template>
  <aside class="sidebar">
    <div class="brand">
      <img class="brand-logo" :src="yueluLogo" alt="跃鹿运动标识">
      <div>
        <strong>跃鹿运动</strong>
        <small>{{ isAdmin ? '管理控制台' : '训练、饮食与内容' }}</small>
      </div>
    </div>

    <nav class="nav-list">
      <section v-for="group in navGroups" :key="group.title">
        <p>{{ group.title }}</p>
        <button
          v-for="item in group.items"
          :key="item.key"
          :class="{ active: activeView === item.key }"
          @click="emit('open-view', item.key)"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </button>
      </section>
    </nav>

    <div class="sidebar-user">
      <span>{{ isAdmin ? 'ADMIN' : 'USER' }}</span>
      <strong>{{ displayName }}</strong>
      <small>{{ isAdmin ? '内容、动作库与账号管理' : `${goalText} · 每周 ${weeklyFrequency || 0} 次` }}</small>
    </div>
  </aside>
</template>

<style scoped>
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 30;
  width: 280px;
  height: 100vh;
  padding: 24px;
  background:
    linear-gradient(160deg, rgba(58, 33, 23, 0.98) 0%, rgba(114, 49, 24, 0.98) 54%, rgba(169, 77, 26, 0.96) 100%),
    linear-gradient(90deg, rgba(255, 255, 255, 0.06), transparent 34%);
  color: #fff;
  display: flex;
  flex-direction: column;
  gap: clamp(14px, 3vh, 26px);
  overflow: hidden;
  box-shadow: 18px 0 46px rgba(78, 44, 26, 0.16);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-logo {
  width: 48px;
  height: 48px;
  display: block;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.94);
  object-fit: cover;
  object-position: center 54%;
  box-shadow: 0 12px 28px rgba(233, 121, 26, 0.28);
}

.brand strong,
.brand small {
  display: block;
}

.brand small {
  color: rgba(255, 255, 255, 0.64);
  margin-top: 2px;
}

.nav-list {
  min-height: 0;
  overflow: hidden;
  display: grid;
  align-content: start;
  gap: clamp(10px, 2.2vh, 20px);
}

.nav-list p {
  margin: 0 0 clamp(4px, 0.8vh, 8px);
  color: rgba(255, 255, 255, 0.48);
  font-size: 12px;
  font-weight: 800;
}

.nav-list button {
  position: relative;
  width: 100%;
  min-height: clamp(34px, 5vh, 42px);
  padding: 0 12px;
  border: 0;
  border-radius: 8px;
  background: transparent;
  color: rgba(255, 255, 255, 0.8);
  text-align: left;
  font-weight: 800;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  overflow: hidden;
  transition: transform 180ms ease, background 180ms ease, color 180ms ease;
}

.nav-list button::after {
  content: "";
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.18), transparent);
  transform: translateX(-120%);
  transition: transform 420ms ease;
}

.nav-list button.active,
.nav-list button:hover {
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
  transform: translateX(4px);
}

.nav-list button.active::after,
.nav-list button:hover::after {
  transform: translateX(120%);
}

.sidebar-user {
  margin-top: auto;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 8px;
  padding: 14px;
  display: grid;
  gap: 4px;
  background: rgba(255, 255, 255, 0.06);
}

.sidebar-user span,
.sidebar-user small {
  color: rgba(255, 255, 255, 0.64);
  font-size: 12px;
}

@media (max-width: 820px) {
  .sidebar {
    position: sticky;
    left: auto;
    z-index: 20;
    height: auto;
    width: 100%;
    min-width: 0;
    padding: 10px 12px;
    gap: 10px;
    overflow: visible;
    box-shadow: 0 10px 30px rgba(43, 33, 28, 0.16);
  }

  .brand {
    gap: 10px;
  }

  .brand-logo {
    width: 38px;
    height: 38px;
  }

  .brand small,
  .nav-list p,
  .sidebar-user {
    display: none;
  }

  .nav-list {
    display: flex;
    flex-wrap: wrap;
    width: 100%;
    max-width: 100%;
    min-width: 0;
    overflow: visible;
    gap: 8px;
    padding-bottom: 0;
  }

  .nav-list section {
    min-width: 0;
    display: contents;
  }

  .nav-list button {
    width: auto;
    min-width: max(96px, calc((100% - 16px) / 3));
    min-height: 36px;
    flex: 1 1 96px;
    justify-content: center;
    padding: 0 10px;
  }

  .nav-list button.active,
  .nav-list button:hover {
    transform: none;
  }
}

@media (max-width: 520px) {
  .sidebar {
    padding: 10px 12px;
  }

  .nav-list button {
    min-width: calc((100% - 8px) / 2);
    flex-basis: calc((100% - 8px) / 2);
  }
}

@media (max-height: 680px) and (min-width: 821px) {
  .sidebar {
    padding: 16px;
    gap: 12px;
  }

  .brand-logo {
    width: 40px;
    height: 40px;
  }

  .brand small,
  .sidebar-user small {
    display: none;
  }

  .nav-list {
    gap: 8px;
  }

  .nav-list p {
    margin-bottom: 4px;
  }

  .nav-list button {
    min-height: 32px;
  }

  .sidebar-user {
    padding: 10px;
  }
}
</style>
