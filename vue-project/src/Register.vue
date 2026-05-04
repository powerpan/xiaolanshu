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
import yueluLogo from './assets/yuelu-logo.png'
import api, { formBody } from './services/api'

const router = useRouter()
const mode = ref('login')
const username = ref('')
const password = ref('')
const loading = ref(false)

const isLogin = () => mode.value === 'login'
const authTitle = computed(() => (isLogin() ? '欢迎回到跃鹿运动' : '创建训练账号'))
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
    <section class="auth-hero">
      <div class="brand-line">
        <img class="brand-mark" :src="yueluLogo" alt="跃鹿运动标识">
        <div>
          <strong>跃鹿运动</strong>
          <small>Yuelu Fitness Planning OS</small>
        </div>
      </div>

      <div class="feature-grid">
        <article v-for="item in featureCards" :key="item.title">
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
  background:
    linear-gradient(110deg, rgba(58, 33, 23, 0.97) 0 48%, transparent 48%),
    linear-gradient(135deg, #fffaf4 0%, #fff2e3 54%, #f7e4d2 100%);
  color: #2b211c;
  overflow-x: hidden;
}

.auth-page::before {
  content: "";
  position: absolute;
  left: clamp(18px, 6vw, 92px);
  bottom: clamp(-110px, -8vw, -42px);
  width: min(58vw, 700px);
  aspect-ratio: 2 / 3;
  background: url('./assets/yuelu-logo.png') center / contain no-repeat;
  opacity: 0.16;
  pointer-events: none;
}

.auth-hero {
  position: relative;
  z-index: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 22px;
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
  gap: 14px;
  min-width: 0;
}

.brand-mark,
.brand-line .brand-mark {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: block;
  background: rgba(255, 255, 255, 0.94);
  object-fit: cover;
  object-position: center 54%;
  box-shadow: 0 14px 30px rgba(0, 0, 0, 0.18);
}

.brand-line strong,
.brand-line small {
  display: block;
}

.brand-line strong {
  font-size: 20px;
}

.brand-line small,
.hero-copy span,
.hero-copy p,
.feature-grid p {
  color: rgba(255, 255, 255, 0.72);
}

.hero-copy p,
.auth-heading p {
  margin: 0 0 8px;
  color: #f0b35d;
  font-size: 13px;
  font-weight: 900;
  text-transform: uppercase;
}

.hero-copy h1 {
  max-width: 820px;
  margin: 0;
  font-size: clamp(34px, 5vw, 60px);
  line-height: 1.06;
  letter-spacing: 0;
  overflow-wrap: anywhere;
}

.hero-copy span {
  display: block;
  max-width: 640px;
  margin-top: 16px;
  font-size: 16px;
  line-height: 1.68;
}

.feature-grid {
  max-width: 960px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.feature-grid article {
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  padding: 14px;
  background: rgba(83, 43, 24, 0.76);
  backdrop-filter: blur(12px);
  display: grid;
  gap: 10px;
  box-shadow: 0 14px 34px rgba(47, 28, 18, 0.18);
  transition: transform 220ms ease, background 220ms ease;
}

.feature-grid article:hover {
  transform: translateY(-4px);
  background: rgba(122, 55, 27, 0.84);
}

.feature-grid .el-icon {
  width: 34px;
  height: 34px;
  border-radius: 8px;
  display: grid;
  place-items: center;
  background: rgba(255, 255, 255, 0.12);
  color: #ffbd64;
  font-size: 19px;
}

.feature-grid strong {
  color: #fff;
  font-size: 16px;
}

.feature-grid p {
  margin: 0;
  line-height: 1.7;
}

.auth-card {
  width: 100%;
  max-width: 460px;
  justify-self: center;
  padding: clamp(20px, 3vw, 30px);
  border: 1px solid #eadfd4;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 24px 80px rgba(84, 62, 48, 0.12);
  backdrop-filter: blur(18px);
}

.product-preview {
  margin-bottom: 20px;
  border: 1px solid #eadfd4;
  border-radius: 8px;
  padding: 14px;
  background:
    linear-gradient(135deg, #3a2117, #c65a1a),
    repeating-linear-gradient(90deg, rgba(255, 255, 255, 0.08) 0 1px, transparent 1px 42px);
  color: #fff;
  display: grid;
  gap: 12px;
  overflow: hidden;
  position: relative;
}

.product-preview::before {
  content: "";
  position: absolute;
  inset: 0;
  background:
    repeating-linear-gradient(90deg, rgba(255, 255, 255, 0.08) 0 1px, transparent 1px 42px),
    repeating-linear-gradient(0deg, rgba(255, 255, 255, 0.06) 0 1px, transparent 1px 42px);
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
  width: 94px;
  aspect-ratio: 1;
  margin: 0 auto;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background:
    radial-gradient(circle at 50% 50%, #5a2c1b 0 57%, transparent 58%),
    conic-gradient(#ffbd64 0 84%, rgba(255, 255, 255, 0.18) 84% 100%);
  box-shadow: 0 18px 34px rgba(0, 0, 0, 0.16);
}

.preview-ring strong {
  font-size: 27px;
  line-height: 1;
}

.preview-ring small {
  font-size: 12px;
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
  font-size: 30px;
  letter-spacing: 0;
  overflow-wrap: anywhere;
}

.auth-heading span {
  display: block;
  margin: 8px 0 22px;
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
  font-weight: 700;
  min-width: 0;
}

.auth-form :deep(.el-input) {
  min-width: 0;
  width: 100%;
}

.primary-action {
  width: 100%;
  margin-top: 8px;
}

.text-action {
  width: 100%;
  border: 0;
  background: transparent;
  color: #8a3d12;
  font-weight: 800;
  margin-top: 14px;
  cursor: pointer;
}

@media (max-width: 960px) {
  .auth-page {
    grid-template-columns: 1fr;
    align-content: start;
    gap: 14px;
    background:
      linear-gradient(180deg, rgba(58, 33, 23, 0.98) 0 38%, #fffaf4 38%),
      linear-gradient(135deg, #fffaf4 0%, #fff2e3 54%, #f7e4d2 100%);
  }

  .auth-hero {
    min-height: auto;
    gap: 12px;
    padding: 12px 4px 0;
  }

  .brand-mark,
  .brand-line .brand-mark {
    width: 42px;
    height: 42px;
  }

  .brand-line strong {
    font-size: 18px;
  }

  .hero-copy p {
    margin-bottom: 6px;
  }

  .hero-copy h1 {
    font-size: clamp(28px, 7vw, 36px);
    line-height: 1.08;
  }

  .hero-copy span {
    margin-top: 10px;
    font-size: 14px;
    line-height: 1.55;
  }

  .feature-grid {
    display: none;
  }

  .auth-card {
    max-width: none;
    padding: 18px;
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

  .hero-copy h1 {
    font-size: clamp(32px, 4vw, 44px);
  }

  .feature-grid {
    display: none;
  }
}

@media (max-width: 520px) {
  .auth-page {
    gap: 10px;
    padding: 10px;
  }

  .auth-hero {
    gap: 8px;
    padding-top: 6px;
  }

  .brand-line {
    gap: 10px;
  }

  .brand-mark,
  .brand-line .brand-mark {
    width: 36px;
    height: 36px;
  }

  .brand-line small {
    display: none;
  }

  .hero-copy h1 {
    font-size: 28px;
    line-height: 1.06;
  }

  .hero-copy span {
    display: none;
  }

  .feature-grid {
    display: none;
  }

  .auth-card {
    padding: 16px;
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
    gap: 16px;
    padding: 24px;
  }

  .hero-copy h1 {
    font-size: clamp(32px, 4.2vw, 48px);
  }

  .hero-copy span {
    margin-top: 12px;
    line-height: 1.55;
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
    gap: 12px;
    padding-top: 18px;
  }

  .hero-copy span {
    display: none;
  }

  .auth-card {
    padding: 20px;
  }
}
</style>
