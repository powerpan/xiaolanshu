<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from './services/api'

const router = useRouter()
const mode = ref('login')
const username = ref('')
const password = ref('')
const loading = ref(false)

const isLogin = () => mode.value === 'login'

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
</script>

<template>
  <main class="auth-page">
    <section class="brand-panel">
      <div class="brand-mark">XL</div>
      <h1>小蓝书健身指导系统</h1>
      <p>训练计划、动作指导、内容沉淀和习惯追踪集中在一个工作台里。</p>
      <div class="brand-metrics">
        <span>计划生成</span>
        <span>动作库</span>
        <span>打卡统计</span>
      </div>
    </section>

    <section class="auth-card">
      <div class="auth-heading">
        <p>{{ isLogin() ? 'Welcome back' : 'Create account' }}</p>
        <h2>{{ isLogin() ? '登录账号' : '注册新账号' }}</h2>
      </div>

      <div class="auth-form" @keyup.enter="submit">
        <label>
          <span>用户名</span>
          <el-input v-model="username" size="large" placeholder="请输入用户名" clearable />
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
          />
        </label>

        <el-button class="primary-action" type="primary" size="large" :loading="loading" @click="submit">
          {{ isLogin() ? '登录' : '注册' }}
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
  grid-template-columns: minmax(420px, 1.1fr) minmax(360px, 480px);
  align-items: stretch;
  background:
    linear-gradient(120deg, rgba(22, 121, 97, 0.08), transparent 34%),
    linear-gradient(0deg, #f7f8f3, #f7f8f3);
  color: #17211c;
}

.brand-panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: clamp(48px, 8vw, 120px);
  min-height: 100vh;
}

.brand-mark {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  display: grid;
  place-items: center;
  background: #174f42;
  color: #fff;
  font-weight: 800;
  letter-spacing: 0;
  margin-bottom: 28px;
}

.brand-panel h1 {
  max-width: 760px;
  margin: 0;
  font-size: clamp(42px, 5vw, 76px);
  line-height: 1.02;
  letter-spacing: 0;
}

.brand-panel p {
  max-width: 560px;
  margin: 24px 0 0;
  color: #51615a;
  font-size: 18px;
  line-height: 1.8;
}

.brand-metrics {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 36px;
}

.brand-metrics span {
  border: 1px solid #d8ddd2;
  border-radius: 8px;
  padding: 10px 14px;
  background: rgba(255, 255, 255, 0.72);
  color: #33423b;
  font-weight: 600;
}

.auth-card {
  align-self: center;
  margin: 32px clamp(24px, 6vw, 80px) 32px 0;
  padding: 36px;
  border: 1px solid #d9ded6;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: 0 24px 80px rgba(29, 42, 36, 0.12);
}

.auth-heading p {
  margin: 0 0 8px;
  color: #c45f3c;
  font-size: 13px;
  font-weight: 800;
  text-transform: uppercase;
}

.auth-heading h2 {
  margin: 0 0 28px;
  font-size: 30px;
  letter-spacing: 0;
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
  }

  .brand-panel {
    min-height: auto;
    padding: 48px 28px 24px;
  }

  .brand-panel h1 {
    font-size: 42px;
  }

  .auth-card {
    margin: 0 28px 48px;
  }
}
</style>
