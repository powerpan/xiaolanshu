<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router';
const username = ref('')
const password = ref('')
const isregister = ref(true)
const router = useRouter()
const register = async () => {
  try {
    const params = new URLSearchParams();
    params.append('username', username.value);
    params.append('password', password.value);
    params.append('identity','user');
    const response = await axios.post('http://localhost:8080/user/register?' + params.toString());
    if (response.data.status === 1) {
      alert(response.data.message);
      isregister.value = true;
      username.value = '';
      password.value = '';
    } else {
      alert('注册失败：' + response.data.message);
    }
  } catch (error) {
    console.error(error);
    alert('请求失败，请稍后重试');
  }
}

const login = async () => {
  try {
    const params = new URLSearchParams();
    params.append('username', username.value);
    params.append('password', password.value);
    const response = await axios.post('http://localhost:8080/user/login?' + params.toString());
    if (response.data.status === 1) {
      alert(response.data.message);
      const jwttoken = response.data.data;
      router.push({
        path: '/mainpage',
      });
      localStorage.setItem('jwttoken', jwttoken);
    } else {
      alert('登录失败：' + response.data.message);
    }
  } catch (error) {
    console.error(error);
    alert('请求失败，请稍后重试');
  }
}
</script>

<template>
  <div class="auth-container">
    <!-- 注册表单 -->
    <div v-if="!isregister" class="auth-card">
      <!-- 装饰图标 -->
      <div class="auth-icon">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 12C14.21 12 16 10.21 16 8C16 5.79 14.21 4 12 4C9.79 4 8 5.79 8 8C8 10.21 9.79 12 12 12ZM12 14C9.33 14 4 15.34 4 18V20H20V18C20 15.34 14.67 14 12 14Z" fill="currentColor"/>
        </svg>
      </div>
      
      <h2 class="auth-title">创建账号</h2>
      <p class="auth-subtitle">欢迎加入，开始你的健身之旅</p>
      
      <div class="auth-form">
        <div class="input-group">
          <label class="input-label">用户名</label>
          <el-input 
            v-model="username" 
            class="custom-input" 
            placeholder="请输入用户名" 
            clearable 
            size="large"
            prefix-icon="User"
          />
        </div>
        
        <div class="input-group">
          <label class="input-label">密码</label>
          <el-input 
            v-model="password" 
            class="custom-input" 
            placeholder="请输入密码" 
            type="password" 
            clearable 
            size="large"
            show-password
            prefix-icon="Lock"
          />
        </div>
      </div>
      
      <div class="auth-actions">
        <button class="auth-btn-secondary" @click="isregister = true, username = '', password = ''">
          已有账号？去登录
        </button>
        <button class="auth-btn-primary" @click="register">
          注册新账号
        </button>
      </div>
    </div>

    <!-- 登录表单 -->
    <div v-if="isregister" class="auth-card">
      <!-- 装饰图标 -->
      <div class="auth-icon">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 12C14.21 12 16 10.21 16 8C16 5.79 14.21 4 12 4C9.79 4 8 5.79 8 8C8 10.21 9.79 12 12 12ZM12 14C9.33 14 4 15.34 4 18V20H20V18C20 15.34 14.67 14 12 14Z" fill="currentColor"/>
          <path d="M20 8V6H18V8H16V10H18V12H20V10H22V8H20Z" fill="currentColor"/>
        </svg>
      </div>
      
      <h2 class="auth-title">欢迎回来</h2>
      <p class="auth-subtitle">请登录您的账号</p>
      
      <div class="auth-form">
        <div class="input-group">
          <label class="input-label">用户名</label>
          <el-input 
            v-model="username" 
            class="custom-input" 
            placeholder="请输入用户名" 
            clearable 
            size="large"
            prefix-icon="User"
          />
        </div>
        
        <div class="input-group">
          <label class="input-label">密码</label>
          <el-input 
            v-model="password" 
            class="custom-input" 
            placeholder="请输入密码" 
            type="password" 
            clearable 
            size="large"
            show-password
            prefix-icon="Lock"
          />
        </div>
      </div>
      
      <div class="auth-actions">
        <button class="auth-btn-secondary" @click="isregister = false, username = '', password = ''">
          没有账号？去注册
        </button>
        <button class="auth-btn-primary" @click="login">
          登录账号
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 容器 */
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f0e7 0%, #e8e0d5 100%);
  position: relative;
}

/* 背景装饰 */
.auth-container::before {
  content: "";
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: radial-gradient(#c0a080 1px, transparent 1px);
  background-size: 32px 32px;
  opacity: 0.08;
  pointer-events: none;
}

/* 卡片样式 - 更大更现代 */
.auth-card {
  position: relative;
  width: 480px;
  max-width: 90%;
  padding: 48px 44px;
  background: rgba(255, 254, 247, 0.96);
  backdrop-filter: blur(10px);
  border-radius: 48px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25),
              0 0 0 1px rgba(217, 180, 139, 0.2);
  transition: all 0.3s ease;
}

.auth-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 30px 60px -12px rgba(0, 0, 0, 0.3),
              0 0 0 1px rgba(217, 180, 139, 0.3);
}

/* 顶部装饰条 */
.auth-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 20px;
  right: 20px;
  height: 6px;
  background: linear-gradient(90deg, 
    rgba(217, 180, 139, 0) 0%,
    #d9b48b 30%,
    #b97f54 50%,
    #d9b48b 70%,
    rgba(217, 180, 139, 0) 100%);
  border-radius: 48px 48px 0 0;
}

/* 图标区域 */
.auth-icon {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.auth-icon svg {
  width: 64px;
  height: 64px;
  color: #d98c5f;
  opacity: 0.8;
}

/* 标题样式 */
.auth-title {
  text-align: center;
  font-size: 32px;
  font-weight: 700;
  color: #5a3e2b;
  margin: 0 0 8px 0;
  letter-spacing: 1px;
}

.auth-subtitle {
  text-align: center;
  font-size: 15px;
  color: #8b6b4e;
  margin: 0 0 36px 0;
}

/* 表单区域 */
.auth-form {
  margin-bottom: 32px;
}

.input-group {
  margin-bottom: 24px;
}

.input-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #6b4c34;
  margin-bottom: 8px;
}

/* 自定义输入框 */
.custom-input {
  width: 100%;
}

.custom-input :deep(.el-input__wrapper) {
  padding: 14px 18px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(217, 180, 139, 0.3);
  border-radius: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02);
  transition: all 0.3s ease;
}

.custom-input :deep(.el-input__wrapper:hover) {
  border-color: rgba(217, 180, 139, 0.6);
  background: rgba(255, 255, 255, 1);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  border-color: #d98c5f;
  box-shadow: 0 0 0 3px rgba(217, 140, 95, 0.1);
}

.custom-input :deep(.el-input__inner) {
  font-size: 15px;
  color: #333;
}

.custom-input :deep(.el-input__inner::placeholder) {
  color: #bbb;
}

.custom-input :deep(.el-input__prefix-inner) {
  color: #d98c5f;
}

/* 按钮区域 */
.auth-actions {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.auth-btn-primary,
.auth-btn-secondary {
  width: 100%;
  padding: 14px 24px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 40px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
}

.auth-btn-primary {
  background: linear-gradient(135deg, #e6b980 0%, #d98c5f 100%);
  border: none;
  color: white;
  box-shadow: 0 4px 12px rgba(217, 140, 95, 0.3);
}

.auth-btn-primary:hover {
  transform: translateY(-2px);
  background: linear-gradient(135deg, #d98c5f 0%, #c46a3a 100%);
  box-shadow: 0 8px 20px rgba(217, 140, 95, 0.4);
}

.auth-btn-primary:active {
  transform: translateY(0);
}

.auth-btn-secondary {
  background: rgba(237, 227, 214, 0.9);
  border: 1px solid rgba(217, 180, 139, 0.3);
  color: #6b4c34;
}

.auth-btn-secondary:hover {
  background: rgba(224, 209, 192, 0.9);
  transform: translateY(-1px);
  border-color: rgba(217, 180, 139, 0.5);
}

.auth-btn-secondary:active {
  transform: translateY(0);
}

/* 响应式适配 */
@media (max-width: 560px) {
  .auth-card {
    padding: 36px 28px;
    width: 85%;
  }
  
  .auth-title {
    font-size: 28px;
  }
  
  .auth-subtitle {
    font-size: 14px;
    margin-bottom: 28px;
  }
  
  .auth-icon svg {
    width: 52px;
    height: 52px;
  }
  
  .custom-input :deep(.el-input__wrapper) {
    padding: 12px 16px;
  }
  
  .auth-btn-primary,
  .auth-btn-secondary {
    padding: 12px 20px;
    font-size: 15px;
  }
}

@media (max-width: 400px) {
  .auth-card {
    padding: 28px 20px;
  }
  
  .auth-title {
    font-size: 24px;
  }
  
  .input-group {
    margin-bottom: 18px;
  }
}
</style>