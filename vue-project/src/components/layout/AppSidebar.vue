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
    <div class="sidebar-aurora" aria-hidden="true"></div>
    <div class="sidebar-grid" aria-hidden="true"></div>

    <div class="brand">
      <span class="brand-logo-wrap">
        <span class="brand-logo" role="img" aria-label="小蓝鼠标识">小</span>
        <i class="brand-logo-glow" aria-hidden="true"></i>
      </span>
      <div class="brand-text">
        <strong>小蓝鼠</strong>
        <small>{{ isAdmin ? '管理控制台' : 'Xiaolanshu Fitness OS' }}</small>
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
          <span class="nav-pill" aria-hidden="true"></span>
          <el-icon><component :is="item.icon" /></el-icon>
          <span class="nav-label">{{ item.label }}</span>
        </button>
      </section>
    </nav>

    <div class="sidebar-user">
      <span class="user-tag">{{ isAdmin ? 'ADMIN' : 'USER' }}</span>
      <strong>{{ displayName }}</strong>
      <small>{{ isAdmin ? '内容、动作库与账号管理' : `${goalText} · 每周 ${weeklyFrequency || 0} 次` }}</small>
      <i class="user-spark" aria-hidden="true"></i>
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
  background: #3a2117;
  color: #fff;
  display: flex;
  flex-direction: column;
  gap: clamp(14px, 3vh, 26px);
  overflow: hidden;
  isolation: isolate;
  border-right: 1px solid rgba(255, 211, 154, 0.12);
  box-shadow:
    18px 0 46px rgba(78, 44, 26, 0.22),
    inset -1px 0 0 rgba(255, 246, 234, 0.06);
}

/* —— 纯色品牌侧栏，不再使用图片或光晕背景 —— */
.sidebar-aurora {
  position: absolute;
  inset: -40% -30% auto -30%;
  height: 78%;
  z-index: -2;
  background:
    conic-gradient(from 200deg at 30% 40%,
      rgba(255, 180, 84, 0) 0deg,
      rgba(255, 180, 84, 0.55) 70deg,
      rgba(233, 121, 26, 0.18) 160deg,
      rgba(255, 246, 234, 0.4) 230deg,
      rgba(255, 180, 84, 0) 360deg);
  filter: blur(48px);
  opacity: 0.85;
  mix-blend-mode: screen;
  animation: sidebar-drift 18s linear infinite;
  display: none;
}

/* —— 背景底纹已关闭，保持纯色 —— */
.sidebar-grid {
  position: absolute;
  inset: 0;
  z-index: -1;
  background:
    repeating-linear-gradient(0deg, rgba(255, 255, 255, 0.04) 0 1px, transparent 1px 56px),
    repeating-linear-gradient(90deg, rgba(255, 255, 255, 0.035) 0 1px, transparent 1px 56px),
    radial-gradient(420px 320px at 100% 110%, rgba(255, 211, 154, 0.18), transparent 70%);
  mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.55), rgba(0, 0, 0, 1) 30%, rgba(0, 0, 0, 0.35) 100%);
  pointer-events: none;
  display: none;
}

@keyframes sidebar-drift {
  to { transform: rotate(360deg); }
}

.brand {
  position: relative;
  display: flex;
  align-items: center;
  gap: 14px;
  min-width: 0;
}

.brand-logo-wrap {
  position: relative;
  isolation: isolate;
  flex-shrink: 0;
  display: grid;
  place-items: center;
  width: 62px;
  height: 62px;
  border-radius: 18px;
  background: #fff4e3;
  padding: 4px;
  box-shadow:
    0 14px 30px rgba(233, 121, 26, 0.42),
    inset 0 1px 0 rgba(255, 255, 255, 0.65);
}

.brand-logo {
  width: 100%;
  height: 100%;
  max-width: 100%;
  max-height: 100%;
  display: grid;
  place-items: center;
  border-radius: 14px;
  background: #e9791a;
  color: #fff;
  font-size: 26px;
  font-weight: 900;
  line-height: 1;
}

.brand-logo-glow {
  position: absolute;
  inset: -5px;
  border-radius: 22px;
  z-index: -1;
  background: rgba(233, 121, 26, 0.55);
  filter: blur(8px);
  opacity: 0.42;
  pointer-events: none;
  animation: ring-spin 8s linear infinite;
}

.brand-text {
  min-width: 0;
  overflow: hidden;
}

.brand-text strong,
.brand-text small {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.brand-text strong,
.brand-text small {
  display: block;
}

.brand-text strong {
  font-size: 19px;
  font-weight: 900;
  letter-spacing: 0.6px;
  color: #fff;
}

.brand-text strong em {
  font-style: normal;
  background: linear-gradient(120deg, #ffe3bd 0%, #ffb454 50%, #fff4e3 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  margin-left: 4px;
  letter-spacing: 1px;
}

.brand-text small {
  margin-top: 3px;
  color: rgba(255, 246, 234, 0.6);
  font-size: 11px;
  letter-spacing: 1.4px;
  font-weight: 700;
  text-transform: uppercase;
}

.nav-list {
  position: relative;
  min-height: 0;
  overflow: hidden;
  display: grid;
  align-content: start;
  gap: clamp(10px, 2.2vh, 20px);
}

.nav-list p {
  margin: 0 0 clamp(4px, 0.8vh, 8px);
  color: rgba(255, 246, 234, 0.42);
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 1.6px;
  text-transform: uppercase;
}

.nav-list button {
  position: relative;
  width: 100%;
  min-height: clamp(36px, 5vh, 42px);
  padding: 0 14px;
  border: 0;
  border-radius: 10px;
  background: transparent;
  color: rgba(255, 246, 234, 0.78);
  text-align: left;
  font-weight: 700;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  overflow: hidden;
  transition: transform 220ms cubic-bezier(0.2, 0.8, 0.2, 1), color 220ms ease;
  isolation: isolate;
}

/* —— 滑块 pill：active/hover 时充能 —— */
.nav-pill {
  position: absolute;
  inset: 0;
  z-index: -1;
  border-radius: inherit;
  background: linear-gradient(135deg, rgba(255, 211, 154, 0.18), rgba(255, 180, 84, 0.08));
  border: 1px solid rgba(255, 211, 154, 0);
  opacity: 0;
  transform: translateX(-6px) scale(0.96);
  transition: opacity 220ms ease, transform 220ms ease, border-color 220ms ease, background 220ms ease;
}

/* —— 流光横扫 —— */
.nav-list button::after {
  content: "";
  position: absolute;
  inset: 0;
  background: linear-gradient(100deg, transparent 30%, rgba(255, 255, 255, 0.22), transparent 70%);
  transform: translateX(-130%);
  transition: transform 520ms cubic-bezier(0.2, 0.8, 0.2, 1);
  pointer-events: none;
}

.nav-list button:hover {
  color: #fff;
  transform: translateX(2px);
}

.nav-list button:hover .nav-pill {
  opacity: 1;
  transform: translateX(0) scale(1);
  border-color: rgba(255, 211, 154, 0.35);
}

.nav-list button:hover::after {
  transform: translateX(130%);
}

.nav-list button.active {
  color: #fff;
  transform: translateX(0);
}

.nav-list button.active .nav-pill {
  opacity: 1;
  transform: translateX(0) scale(1);
  background:
    linear-gradient(135deg, rgba(255, 211, 154, 0.32), rgba(233, 121, 26, 0.22)),
    linear-gradient(135deg, rgba(255, 255, 255, 0.06), rgba(255, 255, 255, 0));
  border-color: rgba(255, 211, 154, 0.55);
  box-shadow:
    0 12px 24px rgba(166, 71, 22, 0.32),
    inset 0 1px 0 rgba(255, 246, 234, 0.4);
}

/* —— active 左侧高光指示 —— */
.nav-list button.active::before {
  content: "";
  position: absolute;
  left: 6px;
  top: 50%;
  width: 4px;
  height: 18px;
  border-radius: 999px;
  background: linear-gradient(180deg, #ffe3bd, #ffb454, #e9791a);
  box-shadow: 0 0 12px rgba(255, 211, 154, 0.85);
  transform: translateY(-50%);
}

.nav-list button .el-icon {
  font-size: 17px;
  flex-shrink: 0;
  filter: drop-shadow(0 1px 0 rgba(43, 33, 28, 0.35));
}

.nav-list button.active .el-icon {
  color: #fff4e3;
}

.nav-label {
  min-width: 0;
}

/* —— 用户卡 —— */
.sidebar-user {
  position: relative;
  margin-top: auto;
  border-radius: 14px;
  padding: 16px;
  display: grid;
  gap: 4px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.04));
  border: 1px solid rgba(255, 211, 154, 0.18);
  backdrop-filter: blur(10px);
  overflow: hidden;
  box-shadow: inset 0 1px 0 rgba(255, 246, 234, 0.18);
}

.user-tag {
  width: fit-content;
  border-radius: 999px;
  padding: 3px 9px;
  background: linear-gradient(120deg, rgba(255, 211, 154, 0.32), rgba(233, 121, 26, 0.32));
  color: #fff4e3;
  font-size: 10px;
  font-weight: 900;
  letter-spacing: 2px;
}

.sidebar-user strong {
  color: #fff;
  font-size: 16px;
  letter-spacing: 0.2px;
}

.sidebar-user small {
  color: rgba(255, 246, 234, 0.62);
  font-size: 12px;
}

.user-spark {
  position: absolute;
  right: -30px;
  top: -30px;
  width: 90px;
  height: 90px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 211, 154, 0.55), transparent 70%);
  filter: blur(8px);
  pointer-events: none;
}

@media (max-width: 820px) {
  .sidebar {
    position: sticky;
    left: auto;
    z-index: 20;
    height: auto;
    width: 100%;
    min-width: 0;
    padding: 12px 14px;
    gap: 10px;
    overflow: visible;
    box-shadow: 0 10px 30px rgba(43, 33, 28, 0.22);
    border-right: 0;
    border-bottom: 1px solid rgba(255, 211, 154, 0.14);
  }

  .sidebar-aurora {
    inset: -50% -30% auto -30%;
    height: 200%;
  }

  .brand {
    gap: 10px;
  }

  .brand-logo-wrap {
    width: 56px;
    height: 56px;
    padding: 4px;
    border-radius: 16px;
  }

  .brand-logo-glow {
    inset: -4px;
    border-radius: 20px;
    filter: blur(6px);
  }

  .brand-text small,
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

  .brand-logo-wrap {
    width: 52px;
    height: 52px;
  }

  .brand-text small,
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
