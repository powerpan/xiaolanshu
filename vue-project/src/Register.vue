<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Aim,
  Calendar,
  Check,
  DataAnalysis,
  Guide,
  Lock,
  TrendCharts,
  User,
} from '@element-plus/icons-vue'
import api, { formBody } from './services/api'

const router = useRouter()
const mode = ref('login')
const username = ref('')
const password = ref('')
const loading = ref(false)

const isLogin = () => mode.value === 'login'
const authTitle = computed(() => (isLogin() ? '欢迎回到小蓝鼠' : '创建训练账号'))
const authSubtitle = computed(() => (
  isLogin()
    ? '继续查看训练计划、动作指导和最近打卡复盘。'
    : '注册后等待管理员审核，通过后即可生成个人训练计划。'
))

const featureCards = [
  { icon: TrendCharts, title: '计划生成', copy: '按目标、频率、器材和水平组合训练日。' },
  { icon: Guide, title: '动作百科', copy: '把动作步骤、注意事项和本地指导图集中管理。' },
  { icon: DataAnalysis, title: '训练复盘', copy: '用打卡、热力图和连续记录判断执行趋势。' },
]

const previewStats = [
  { label: '准备度', value: '84' },
  { label: '连续', value: '5 天' },
  { label: '动作', value: '10+' },
]

const workflowSteps = [
  { icon: User, label: '资料' },
  { icon: Aim, label: '目标' },
  { icon: Calendar, label: '计划' },
  { icon: Check, label: '打卡' },
]

const resetForm = () => {
  username.value = ''
  password.value = ''
}

const submit = async () => {
  if (!username.value || !password.value) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    const params = {
      username: username.value.trim(),
      password: password.value,
    }

    if (isLogin()) {
      const response = await api.post('/user/login', formBody(params))
      if (response.data.status !== 1) {
        throw new Error(response.data.message)
      }
      localStorage.setItem('jwttoken', response.data.data)
      ElMessage.success('登录成功')
      router.push('/mainpage')
    } else {
      const response = await api.post('/user/register', formBody(params))
      if (response.data.status !== 1) {
        throw new Error(response.data.message)
      }
      ElMessage.success('注册成功，等待管理员审核')
      mode.value = 'login'
      resetForm()
    }
  } catch (error) {
    ElMessage.error(error.message || '请求失败')
  } finally {
    loading.value = false
  }
}

const switchMode = () => {
  mode.value = isLogin() ? 'register' : 'login'
  resetForm()
}

</script>

<template>
  <main class="auth-page">
    <div class="auth-aurora" aria-hidden="true">
      <span class="aurora-blob aurora-a"></span>
      <span class="aurora-blob aurora-b"></span>
      <span class="aurora-blob aurora-c"></span>
      <span class="aurora-grid"></span>
    </div>

    <section class="auth-hero">
      <div class="brand-line">
        <span class="brand-mark-wrap">
          <span class="brand-mark" role="img" aria-label="小蓝鼠标识">小</span>
          <i class="brand-mark-glow" aria-hidden="true"></i>
        </span>
        <div>
          <strong>小蓝鼠</strong>
          <small>Xiaolanshu Fitness Planning OS · v2.0</small>
        </div>
      </div>

      <div class="hero-headline">
        <p class="hero-eyebrow">
          <span class="dot" aria-hidden="true"></span>
          Powered by data &amp; intuition
        </p>
        <h1>把每一次训练<br><em>变成可见的进度。</em></h1>
        <span class="hero-sub">基于目标、频率、器材和水平自动生成训练计划，配合打卡热力图与饮食建议，建立稳定的训练节奏。</span>
      </div>

      <div class="feature-grid">
        <article v-for="(item, idx) in featureCards" :key="item.title" :style="{ '--i': idx }">
          <span class="feature-glow" aria-hidden="true"></span>
          <el-icon><component :is="item.icon" /></el-icon>
          <strong>{{ item.title }}</strong>
          <p>{{ item.copy }}</p>
        </article>
      </div>
    </section>

    <section class="auth-card">
      <div class="product-preview">
        <div class="preview-head">
          <span>今日训练</span>
          <strong>上肢训练</strong>
        </div>
        <div class="preview-ring">
          <strong>84</strong>
          <small>准备度</small>
        </div>
        <div class="preview-stats">
          <article v-for="item in previewStats" :key="item.label">
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
          </article>
        </div>
        <div class="workflow-line">
          <span v-for="step in workflowSteps" :key="step.label">
            <el-icon><component :is="step.icon" /></el-icon>
            {{ step.label }}
          </span>
        </div>
      </div>

      <div class="auth-heading">
        <p>{{ isLogin() ? 'Welcome back' : 'Create account' }}</p>
        <h2>{{ authTitle }}</h2>
        <span>{{ authSubtitle }}</span>
      </div>

      <div class="auth-form" @keyup.enter="submit">
        <label>
          <span>用户名</span>
          <el-input v-model="username" size="large" placeholder="请输入用户名" clearable>
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </label>

        <label>
          <span>密码</span>
          <el-input
            v-model="password"
            size="large"
            placeholder="请输入密码"
            type="password"
            show-password
            clearable
          >
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </label>

        <el-button class="primary-action" type="primary" size="large" :loading="loading" @click="submit">
          {{ isLogin() ? '登录' : '注册' }}
        </el-button>

      </div>

      <button class="text-action" @click="switchMode">
        {{ isLogin() ? '没有账号？创建一个' : '已有账号？返回登录' }}
      </button>
    </section>
  </main>
</template>

<style scoped>
.auth-page {
  position: relative;
  min-height: 100svh;
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(360px, 440px);
  gap: clamp(16px, 4vw, 52px);
  align-items: center;
  padding: clamp(12px, 2vw, 28px);
  background: #1a0e08;
  color: #2b211c;
  overflow-x: hidden;
  overflow-y: hidden;
  isolation: isolate;
}

/* —— 纯色背景层 —— */
.auth-aurora {
  position: absolute;
  inset: 0;
  z-index: -2;
  overflow: hidden;
  pointer-events: none;
  background: #1a0e08;
}

.aurora-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(70px);
  mix-blend-mode: screen;
  display: none;
}

.aurora-a {
  top: -10%;
  left: 8%;
  width: 480px;
  height: 480px;
  background: radial-gradient(circle, rgba(255, 211, 154, 0.62), transparent 65%);
  animation: aurora-float-a 18s ease-in-out infinite alternate;
}

.aurora-b {
  bottom: -20%;
  left: 28%;
  width: 580px;
  height: 580px;
  background: radial-gradient(circle, rgba(233, 121, 26, 0.7), transparent 65%);
  animation: aurora-float-b 22s ease-in-out infinite alternate;
}

.aurora-c {
  top: 18%;
  right: -8%;
  width: 460px;
  height: 460px;
  background: radial-gradient(circle, rgba(255, 246, 234, 0.6), transparent 60%);
  animation: aurora-float-c 26s ease-in-out infinite alternate;
}

.aurora-grid {
  position: absolute;
  inset: 0;
  background:
    repeating-linear-gradient(0deg, rgba(255, 246, 234, 0.04) 0 1px, transparent 1px 64px),
    repeating-linear-gradient(90deg, rgba(255, 246, 234, 0.035) 0 1px, transparent 1px 64px);
  mask-image: radial-gradient(circle at 50% 50%, rgba(0, 0, 0, 1) 30%, transparent 80%);
  display: none;
}

@keyframes aurora-float-a {
  to { transform: translate3d(60px, 30px, 0) scale(1.08); }
}
@keyframes aurora-float-b {
  to { transform: translate3d(-40px, -50px, 0) scale(1.12); }
}
@keyframes aurora-float-c {
  to { transform: translate3d(-80px, 40px, 0) scale(1.05); }
}

.auth-page::before {
  content: none;
}

.auth-hero {
  position: relative;
  z-index: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 26px;
  padding: clamp(18px, 4vw, 50px);
  color: #fff;
}

.auth-card {
  position: relative;
  z-index: 1;
}

.brand-line {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
}

.brand-mark-wrap {
  position: relative;
  isolation: isolate;
  flex-shrink: 0;
  display: grid;
  place-items: center;
  width: 56px;
  height: 56px;
  border-radius: 14px;
  padding: 3px;
  background: #fff4e3;
  box-shadow:
    0 16px 32px rgba(233, 121, 26, 0.42),
    inset 0 1px 0 rgba(255, 255, 255, 0.65);
}

.brand-mark,
.brand-line .brand-mark {
  width: 100%;
  height: 100%;
  max-width: 100%;
  max-height: 100%;
  border-radius: calc(14px - 3px);
  display: grid;
  place-items: center;
  background: #e9791a;
  color: #fff;
  font-size: 24px;
  font-weight: 900;
  line-height: 1;
}

.brand-mark-glow {
  position: absolute;
  inset: -6px;
  border-radius: 20px;
  background: rgba(233, 121, 26, 0.55);
  filter: blur(10px);
  opacity: 0.46;
  z-index: -1;
  pointer-events: none;
  animation: ring-spin 8s linear infinite;
}

.brand-line > div {
  min-width: 0;
  overflow: hidden;
}

.brand-line strong,
.brand-line small {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.brand-line strong,
.brand-line small {
  display: block;
}

.brand-line strong {
  font-size: 22px;
  font-weight: 900;
  letter-spacing: 0.6px;
  color: #fff;
}

.brand-line strong em {
  font-style: normal;
  margin-left: 6px;
  background: linear-gradient(120deg, #ffe3bd 0%, #ffb454 50%, #fff4e3 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  letter-spacing: 1.4px;
}

.brand-line small {
  margin-top: 4px;
  font-family: 'Space Grotesk', 'Inter', sans-serif;
  letter-spacing: 1.6px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  color: rgba(255, 246, 234, 0.6);
}

/* —— Hero headline —— */
.hero-headline {
  display: grid;
  gap: 18px;
}

.hero-eyebrow {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  gap: 10px;
  padding: 6px 14px;
  border-radius: 999px;
  background: linear-gradient(120deg, rgba(255, 211, 154, 0.18), rgba(233, 121, 26, 0.14));
  border: 1px solid rgba(255, 211, 154, 0.3);
  color: #fff4e3;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 1.6px;
  text-transform: uppercase;
  backdrop-filter: blur(10px);
  margin: 0;
}

.hero-eyebrow .dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 30%, #ffd39a, #e9791a);
  box-shadow: 0 0 0 3px rgba(255, 180, 84, 0.18), 0 0 12px rgba(255, 211, 154, 0.65);
  animation: glow-pulse 2.4s ease-in-out infinite;
}

.hero-headline h1 {
  margin: 0;
  font-family: 'Space Grotesk', 'Inter', sans-serif;
  font-size: clamp(38px, 5.4vw, 68px);
  line-height: 1.02;
  letter-spacing: -1.5px;
  font-weight: 800;
  color: #fff;
  overflow-wrap: anywhere;
  text-shadow: 0 8px 30px rgba(255, 180, 84, 0.16);
}

.hero-headline h1 em {
  font-style: normal;
  background: linear-gradient(120deg, #ffe3bd 0%, #ffb454 40%, #ff8a3a 70%, #ffe3bd 100%);
  background-size: 220% 100%;
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  animation: hero-gradient-flow 6s ease-in-out infinite;
}

@keyframes hero-gradient-flow {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.hero-sub {
  display: block;
  max-width: 600px;
  color: rgba(255, 246, 234, 0.72);
  font-size: 15px;
  line-height: 1.78;
}

.feature-grid {
  max-width: 960px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.feature-grid article {
  position: relative;
  border: 1px solid rgba(255, 211, 154, 0.18);
  border-radius: 16px;
  padding: 16px;
  background:
    linear-gradient(180deg, rgba(255, 246, 234, 0.08), rgba(255, 246, 234, 0.02));
  backdrop-filter: blur(14px) saturate(1.2);
  display: grid;
  gap: 10px;
  box-shadow:
    0 14px 34px rgba(15, 8, 4, 0.32),
    inset 0 1px 0 rgba(255, 246, 234, 0.18);
  overflow: hidden;
  isolation: isolate;
  transition: transform 320ms cubic-bezier(0.2, 0.8, 0.2, 1), border-color 320ms ease, background 320ms ease;
  animation: feature-rise 700ms cubic-bezier(0.2, 0.8, 0.2, 1) calc(var(--i, 0) * 90ms) both;
}

@keyframes feature-rise {
  from {
    opacity: 0;
    transform: translateY(18px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.feature-glow {
  position: absolute;
  inset: -40% -20% auto auto;
  width: 80%;
  aspect-ratio: 1;
  background: radial-gradient(circle, rgba(255, 211, 154, 0.32), transparent 70%);
  filter: blur(10px);
  z-index: -1;
  opacity: 0;
  transition: opacity 320ms ease;
  pointer-events: none;
}

.feature-grid article:hover {
  transform: translateY(-5px);
  border-color: rgba(255, 211, 154, 0.55);
  background:
    linear-gradient(180deg, rgba(255, 246, 234, 0.14), rgba(255, 246, 234, 0.04));
}

.feature-grid article:hover .feature-glow {
  opacity: 1;
}

.feature-grid .el-icon {
  width: 38px;
  height: 38px;
  border-radius: 11px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, rgba(255, 211, 154, 0.22), rgba(233, 121, 26, 0.18));
  border: 1px solid rgba(255, 211, 154, 0.35);
  color: #ffd39a;
  font-size: 19px;
  box-shadow: inset 0 1px 0 rgba(255, 246, 234, 0.32);
}

.feature-grid strong {
  color: #fff;
  font-size: 16px;
  font-weight: 800;
  letter-spacing: 0.2px;
}

.feature-grid p {
  margin: 0;
  color: rgba(255, 246, 234, 0.7);
  line-height: 1.7;
  font-size: 13px;
}

.auth-card {
  width: 100%;
  max-width: 480px;
  justify-self: center;
  padding: clamp(22px, 3vw, 32px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 22px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.94) 0%, rgba(255, 250, 244, 0.94) 100%);
  box-shadow:
    0 30px 80px rgba(15, 8, 4, 0.42),
    inset 0 1px 0 rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(22px) saturate(1.2);
  position: relative;
  isolation: isolate;
}

.auth-card::before {
  content: "";
  position: absolute;
  inset: -1px;
  z-index: -1;
  border-radius: inherit;
  padding: 1px;
  background: linear-gradient(135deg, rgba(255, 211, 154, 0.7), rgba(255, 255, 255, 0) 40%, rgba(255, 211, 154, 0) 60%, rgba(233, 121, 26, 0.5));
  -webkit-mask:
    linear-gradient(#fff 0 0) content-box,
    linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
}

.product-preview {
  margin-bottom: 22px;
  border: 1px solid rgba(255, 211, 154, 0.18);
  border-radius: 16px;
  padding: 16px;
  background:
    radial-gradient(circle at 18% 20%, rgba(255, 211, 154, 0.32) 0%, transparent 40%),
    radial-gradient(circle at 88% 88%, rgba(255, 180, 84, 0.4) 0%, transparent 40%),
    linear-gradient(118deg, #1c130e 0%, #3a2117 35%, #7b371b 70%, #c65a1a 100%);
  color: #fff;
  display: grid;
  gap: 12px;
  overflow: hidden;
  position: relative;
  isolation: isolate;
  box-shadow:
    0 18px 36px rgba(15, 8, 4, 0.32),
    inset 0 1px 0 rgba(255, 246, 234, 0.18);
}

.product-preview::before {
  content: "";
  position: absolute;
  inset: 0;
  background:
    repeating-linear-gradient(90deg, rgba(255, 246, 234, 0.06) 0 1px, transparent 1px 42px),
    repeating-linear-gradient(0deg, rgba(255, 246, 234, 0.045) 0 1px, transparent 1px 42px);
  pointer-events: none;
  z-index: 0;
  opacity: 0.7;
}

.product-preview::after {
  content: "";
  position: absolute;
  top: -50%;
  right: -20%;
  width: 60%;
  height: 200%;
  background: var(--grad-aurora);
  filter: blur(40px);
  opacity: 0.45;
  z-index: 0;
  mix-blend-mode: screen;
  animation: ring-spin 18s linear infinite;
  pointer-events: none;
}

.preview-head,
.preview-ring,
.preview-stats,
.workflow-line {
  position: relative;
  z-index: 1;
}

.preview-head {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 14px;
  align-items: center;
}

.preview-head span,
.preview-ring small,
.preview-stats span {
  color: rgba(255, 255, 255, 0.72);
  font-weight: 800;
}

.preview-head strong {
  font-size: 19px;
}

.preview-ring {
  position: relative;
  width: 100px;
  aspect-ratio: 1;
  margin: 0 auto;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background:
    radial-gradient(circle at 50% 50%, #4a2615 0 57%, transparent 58%),
    conic-gradient(from -90deg, #ffd39a, #ffb454 60%, #e9791a 84%, rgba(255, 255, 255, 0.16) 84% 100%);
  box-shadow: 0 22px 40px rgba(15, 8, 4, 0.36);
}

.preview-ring::before {
  content: "";
  position: absolute;
  inset: -8px;
  border-radius: 50%;
  background: var(--grad-aurora);
  filter: blur(10px);
  opacity: 0.55;
  z-index: -1;
  animation: ring-spin 7s linear infinite;
}

.preview-ring strong {
  font-family: 'Space Grotesk', 'Inter', sans-serif;
  font-size: 28px;
  line-height: 1;
  font-weight: 800;
  letter-spacing: -0.6px;
  background: linear-gradient(180deg, #fff4e3, #ffb454);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-feature-settings: 'tnum';
}

.preview-ring small {
  font-size: 11px;
  letter-spacing: 1.4px;
  text-transform: uppercase;
}

.preview-stats,
.workflow-line {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
}

.preview-stats article,
.workflow-line span {
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 8px;
  padding: 8px;
  background: rgba(255, 255, 255, 0.1);
}

.preview-stats article {
  display: grid;
  gap: 4px;
  min-width: 0;
}

.preview-stats strong {
  font-size: 18px;
  overflow-wrap: anywhere;
}

.workflow-line {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.workflow-line span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 0;
  gap: 5px;
  color: rgba(255, 255, 255, 0.86);
  font-size: 12px;
  font-weight: 900;
  overflow-wrap: anywhere;
}

.auth-heading h2 {
  margin: 0;
  font-family: 'Space Grotesk', 'Inter', sans-serif;
  font-size: 30px;
  font-weight: 800;
  letter-spacing: -0.6px;
  overflow-wrap: anywhere;
  background: linear-gradient(120deg, #2b211c 0%, #7b371b 50%, #c65a1a 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.auth-heading p {
  margin: 0 0 8px;
  color: #c65a1a;
  font-size: 11px;
  font-weight: 900;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.auth-heading span {
  display: block;
  margin: 10px 0 24px;
  color: #71645b;
  line-height: 1.7;
}

.auth-form {
  display: grid;
  gap: 14px;
}

.auth-form label {
  display: grid;
  gap: 8px;
  color: #4d4038;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.6px;
  text-transform: uppercase;
  min-width: 0;
}

.auth-form :deep(.el-input) {
  min-width: 0;
  width: 100%;
}

.auth-form :deep(.el-input__wrapper) {
  border-radius: 12px !important;
  background: linear-gradient(180deg, #ffffff, #fffaf4) !important;
  box-shadow: 0 0 0 1px rgba(234, 223, 212, 0.85), inset 0 1px 0 rgba(255, 255, 255, 0.6) !important;
  transition: box-shadow 220ms ease;
}

.auth-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--brand-500), 0 0 0 4px rgba(233, 121, 26, 0.16), inset 0 1px 0 rgba(255, 255, 255, 0.6) !important;
}

.primary-action {
  width: 100%;
  margin-top: 12px;
  height: 48px !important;
  font-size: 15px !important;
  letter-spacing: 0.5px;
}

.text-action {
  width: 100%;
  border: 0;
  background: transparent;
  color: #8a3d12;
  font-weight: 800;
  margin-top: 14px;
  padding: 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 220ms ease, color 220ms ease;
}

.text-action:hover {
  background: rgba(233, 121, 26, 0.08);
  color: #7b371b;
}

@media (max-width: 960px) {
  .auth-page {
    grid-template-columns: 1fr;
    align-content: start;
    gap: 14px;
  }

  .auth-hero {
    min-height: auto;
    gap: 14px;
    padding: 16px 8px 0;
  }

  .brand-mark-wrap {
    width: 46px;
    height: 46px;
    padding: 2px;
  }

  .brand-line strong {
    font-size: 19px;
  }

  .hero-headline h1 {
    font-size: clamp(30px, 7vw, 42px);
    line-height: 1.08;
  }

  .hero-sub {
    font-size: 14px;
    line-height: 1.65;
  }

  .feature-grid {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .auth-card {
    max-width: none;
    padding: 20px;
  }

  .product-preview {
    display: none;
  }

  .auth-heading h2 {
    font-size: 26px;
  }

  .auth-heading span {
    margin-bottom: 18px;
  }
}

@media (max-width: 1120px) and (min-width: 961px) {
  .auth-page {
    grid-template-columns: minmax(0, 1fr) minmax(340px, 420px);
    gap: 24px;
  }

  .auth-hero {
    padding: 28px;
  }

  .hero-headline h1 {
    font-size: clamp(34px, 4.4vw, 48px);
  }

  .feature-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 520px) {
  .auth-page {
    gap: 10px;
    padding: 12px;
  }

  .auth-hero {
    gap: 12px;
    padding-top: 8px;
  }

  .brand-line {
    gap: 10px;
  }

  .brand-mark-wrap {
    width: 40px;
    height: 40px;
    padding: 2px;
  }

  .brand-line small {
    display: none;
  }

  .hero-headline h1 {
    font-size: 30px;
    line-height: 1.06;
  }

  .hero-sub {
    display: none;
  }

  .feature-grid {
    display: none;
  }

  .auth-card {
    padding: 18px;
  }

  .auth-heading h2 {
    font-size: 24px;
  }

  .auth-heading span {
    margin-bottom: 14px;
    line-height: 1.5;
  }

  .auth-form {
    gap: 12px;
  }
}

@media (max-height: 760px) and (min-width: 961px) {
  .auth-page {
    gap: 24px;
    padding: 14px 22px;
  }

  .auth-hero {
    gap: 18px;
    padding: 24px;
  }

  .hero-headline h1 {
    font-size: clamp(32px, 4.2vw, 48px);
  }

  .feature-grid {
    display: none;
  }

  .product-preview {
    display: none;
  }
}

@media (max-height: 680px) and (min-width: 961px) {
  .auth-page {
    align-items: start;
  }

  .auth-hero {
    justify-content: start;
    gap: 14px;
    padding-top: 20px;
  }

  .hero-sub {
    display: none;
  }

  .auth-card {
    padding: 22px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .aurora-blob,
  .brand-mark-glow,
  .preview-ring::before,
  .product-preview::after,
  .hero-eyebrow .dot,
  .hero-headline h1 em {
    animation: none !important;
  }
}
</style>
