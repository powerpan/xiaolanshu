<script setup>
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
      <span class="brand-logo">XL</span>
      <div>
        <strong>小蓝书</strong>
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
  position: sticky;
  top: 0;
  height: 100vh;
  padding: 24px;
  background:
    linear-gradient(150deg, rgba(16, 42, 40, 0.96), rgba(21, 77, 67, 0.96)),
    repeating-linear-gradient(135deg, rgba(255, 255, 255, 0.08) 0 1px, transparent 1px 18px);
  color: #fff;
  display: flex;
  flex-direction: column;
  gap: 26px;
  box-shadow: 20px 0 55px rgba(21, 42, 36, 0.14);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-logo {
  width: 44px;
  height: 44px;
  display: grid;
  place-items: center;
  border-radius: 8px;
  background: linear-gradient(135deg, #ffe29a, #f2c35d 50%, #85d4ff);
  color: #17211c;
  font-weight: 900;
  box-shadow: 0 12px 28px rgba(242, 195, 93, 0.28);
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
  overflow-y: auto;
  display: grid;
  gap: 20px;
}

.nav-list p {
  margin: 0 0 8px;
  color: rgba(255, 255, 255, 0.48);
  font-size: 12px;
  font-weight: 800;
}

.nav-list button {
  position: relative;
  width: 100%;
  min-height: 42px;
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
    z-index: 20;
    height: auto;
    padding: 10px 12px;
    gap: 10px;
    box-shadow: 0 10px 30px rgba(21, 42, 36, 0.16);
  }

  .brand {
    gap: 10px;
  }

  .brand-logo {
    width: 36px;
    height: 36px;
  }

  .brand small,
  .nav-list p,
  .sidebar-user {
    display: none;
  }

  .nav-list {
    display: flex;
    overflow-x: auto;
    gap: 8px;
    padding-bottom: 2px;
    scrollbar-width: none;
  }

  .nav-list section {
    min-width: 0;
    display: flex;
    gap: 8px;
  }

  .nav-list button {
    width: auto;
    min-width: 94px;
    min-height: 36px;
    flex: 0 0 auto;
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
}
</style>
