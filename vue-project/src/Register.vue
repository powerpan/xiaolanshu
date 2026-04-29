<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Aim,
  Calendar,
  Check,
  DataAnalysis,
  Food,
  Guide,
  Lock,
  TrendCharts,
  User,
} from '@element-plus/icons-vue'
import api from './services/api'

const router = useRouter()
const mode = ref('login')
const username = ref('')
const password = ref('')
const loading = ref(false)

const isLogin = () => mode.value === 'login'
const authTitle = computed(() => (isLogin() ? '欢迎回到小蓝书' : '创建训练账号'))
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
    const params = new URLSearchParams()
    params.append('username', username.value.trim())
    params.append('password', password.value)

    if (isLogin()) {
      const response = await api.post(`/user/login?${params.toString()}`)
      if (response.data.status !== 1) {
        throw new Error(response.data.message)
      }
      localStorage.setItem('jwttoken', response.data.data)
      ElMessage.success('登录成功')
      router.push('/mainpage')
    } else {
      params.append('identity', 'user')
      const response = await api.post(`/user/register?${params.toString()}`)
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

const loginDemo = async () => {
  mode.value = 'login'
  username.value = 'demo'
  password.value = 'demo123'
  await submit()
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-hero">
      <div class="brand-line">
        <span class="brand-mark">XL</span>
        <div>
          <strong>小蓝书</strong>
          <small>Fitness Planning OS</small>
        </div>
      </div>

      <div class="hero-copy">
        <p>Personal training workspace</p>
        <h1>把训练计划、动作学习和复盘数据放到同一个产品里</h1>
        <span>适合课程设计、毕业答辩和真实演示：普通用户看内容和训练，管理员维护公告、文章和账号。</span>
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

        <el-button v-if="isLogin()" class="demo-action" size="large" :icon="Food" :disabled="loading" @click="loginDemo">
          使用演示账号进入
        </el-button>
      </div>

      <button class="text-action" @click="switchMode">
        {{ isLogin() ? '没有账号？创建一个' : '已有账号？返回登录' }}
      </button>

      <div class="demo-note">
        <span>演示账号</span>
        <strong>demo / demo123</strong>
      </div>
    </section>
  </main>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(390px, 0.92fr);
  gap: clamp(24px, 5vw, 72px);
  align-items: center;
  padding: clamp(18px, 3vw, 38px);
  background:
    linear-gradient(110deg, rgba(36, 59, 56, 0.96) 0 48%, transparent 48%),
    linear-gradient(135deg, #f6f8f1 0%, #eef5ef 52%, #f9efd9 100%);
  color: #17211c;
}

.auth-hero {
  min-height: calc(100vh - clamp(36px, 6vw, 76px));
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 34px;
  padding: clamp(26px, 5vw, 72px);
  color: #fff;
}

.brand-line {
  display: flex;
  align-items: center;
  gap: 14px;
}

.brand-mark,
.brand-line .brand-mark {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #f2c35d, #85d4ff);
  color: #17211c;
  font-weight: 900;
  letter-spacing: 0;
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
  font-size: clamp(42px, 6vw, 78px);
  line-height: 1.03;
  letter-spacing: 0;
  overflow-wrap: anywhere;
}

.hero-copy span {
  display: block;
  max-width: 640px;
  margin-top: 22px;
  font-size: 18px;
  line-height: 1.8;
}

.feature-grid {
  max-width: 960px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.feature-grid article {
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 8px;
  padding: 18px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(12px);
  display: grid;
  gap: 10px;
  transition: transform 220ms ease, background 220ms ease;
}

.feature-grid article:hover {
  transform: translateY(-4px);
  background: rgba(255, 255, 255, 0.12);
}

.feature-grid .el-icon {
  width: 34px;
  height: 34px;
  border-radius: 8px;
  display: grid;
  place-items: center;
  background: rgba(255, 255, 255, 0.12);
  color: #85d4ff;
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
  max-width: 500px;
  justify-self: center;
  padding: clamp(24px, 4vw, 38px);
  border: 1px solid #d9ded6;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 24px 80px rgba(29, 42, 36, 0.12);
  backdrop-filter: blur(18px);
}

.product-preview {
  margin-bottom: 28px;
  border: 1px solid #dfe5d9;
  border-radius: 8px;
  padding: 18px;
  background:
    linear-gradient(135deg, #173f37, #2c8f72),
    repeating-linear-gradient(90deg, rgba(255, 255, 255, 0.08) 0 1px, transparent 1px 42px);
  color: #fff;
  display: grid;
  gap: 16px;
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
  font-size: 22px;
}

.preview-ring {
  width: 118px;
  aspect-ratio: 1;
  margin: 4px auto;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background:
    radial-gradient(circle at 50% 50%, #1d5a4e 0 57%, transparent 58%),
    conic-gradient(#85d4ff 0 84%, rgba(255, 255, 255, 0.18) 84% 100%);
  box-shadow: 0 18px 34px rgba(0, 0, 0, 0.16);
}

.preview-ring strong {
  font-size: 34px;
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
  padding: 10px;
  background: rgba(255, 255, 255, 0.1);
}

.preview-stats article {
  display: grid;
  gap: 4px;
}

.preview-stats strong {
  font-size: 18px;
}

.workflow-line {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.workflow-line span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  color: rgba(255, 255, 255, 0.86);
  font-size: 12px;
  font-weight: 900;
}

.auth-heading h2 {
  margin: 0;
  font-size: 32px;
  letter-spacing: 0;
}

.auth-heading span {
  display: block;
  margin: 10px 0 28px;
  color: #607169;
  line-height: 1.7;
}

.auth-form {
  display: grid;
  gap: 18px;
}

.auth-form label {
  display: grid;
  gap: 8px;
  color: #33423b;
  font-weight: 700;
}

.primary-action {
  width: 100%;
  margin-top: 8px;
}

.demo-action {
  width: 100%;
  margin-left: 0;
}

.text-action {
  width: 100%;
  border: 0;
  background: transparent;
  color: #174f42;
  font-weight: 800;
  margin-top: 18px;
  cursor: pointer;
}

.demo-note {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 28px;
  padding-top: 18px;
  border-top: 1px solid #e5e8e0;
  color: #6a756f;
}

.demo-note strong {
  color: #17211c;
}

@media (max-width: 960px) {
  .auth-page {
    grid-template-columns: 1fr;
    background:
      linear-gradient(180deg, rgba(36, 59, 56, 0.98) 0 52%, #f6f8f1 52%),
      linear-gradient(135deg, #f6f8f1 0%, #eef5ef 52%, #f9efd9 100%);
  }

  .auth-hero {
    min-height: auto;
    padding: 34px 18px 0;
  }

  .hero-copy h1 {
    font-size: 42px;
  }

  .feature-grid {
    grid-template-columns: 1fr;
  }

  .auth-card {
    max-width: none;
  }
}

@media (max-width: 520px) {
  .auth-page {
    padding: 12px;
  }

  .hero-copy h1 {
    font-size: 34px;
  }

  .hero-copy span {
    font-size: 16px;
  }

  .feature-grid {
    display: none;
  }

  .auth-card {
    padding: 20px;
  }

  .preview-stats,
  .workflow-line {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .demo-note {
    flex-direction: column;
  }
}
</style>
