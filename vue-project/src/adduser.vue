<script setup>
import { ref } from 'vue'
import axios from 'axios' 
import { useRouter } from 'vue-router';
const username = ref('')
const password = ref('')
const jwttoken = localStorage.getItem('jwttoken');
const headers = {
    'Authorization': `${jwttoken}`
};
const register = async () => {
  try {
    const params = new URLSearchParams();
    params.append('jwttoken', jwttoken);
    params.append('username', username.value);
    params.append('password', password.value);
    params.append('identity', 'user');
    const response = await axios.post('http://localhost:8080/user/adduser?' + params.toString());
    if (response.data.status === 1) {
      alert(response.data.message);
      username.value = '';
      password.value = '';
    } else {
      alert('操作失败：' + response.data.message);
    }
  } catch (error) {
    console.error(error);
    alert('请求失败，请稍后重试');
  }
}
</script>

<template>
    <div class="add-user-wrapper">
        <div class="add-user-container">
            <div class="add-user-card">
                <!-- 图标区域 -->
                <div class="icon-area">
                    <div class="icon-circle">
                        <svg class="user-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M12 12C14.21 12 16 10.21 16 8C16 5.79 14.21 4 12 4C9.79 4 8 5.79 8 8C8 10.21 9.79 12 12 12ZM12 14C9.33 14 4 15.34 4 18V20H20V18C20 15.34 14.67 14 12 14Z" fill="currentColor"/>
                        </svg>
                    </div>
                </div>
                
                <!-- 标题 -->
                <h2 class="card-title">添加新用户</h2>
                <p class="card-subtitle">填写以下信息创建用户账号</p>
                
                <!-- 表单区域 -->
                <div class="form-area">
                    <div class="input-group">
                        <label class="input-label">
                            <svg class="label-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M12 12C14.21 12 16 10.21 16 8C16 5.79 14.21 4 12 4C9.79 4 8 5.79 8 8C8 10.21 9.79 12 12 12ZM12 14C9.33 14 4 15.34 4 18V20H20V18C20 15.34 14.67 14 12 14Z" fill="currentColor"/>
                            </svg>
                            用户名
                        </label>
                        <el-input 
                            v-model="username" 
                            class="custom-input" 
                            placeholder="请输入用户名" 
                            clearable 
                            size="large"
                        />
                    </div>
                    
                    <div class="input-group">
                        <label class="input-label">
                            <svg class="label-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M18 8H17V6C17 3.24 14.76 1 12 1C9.24 1 7 3.24 7 6V8H6C4.9 8 4 8.9 4 10V20C4 21.1 4.9 22 6 22H18C19.1 22 20 21.1 20 20V10C20 8.9 19.1 8 18 8ZM12 17C10.9 17 10 16.1 10 15C10 13.9 10.9 13 12 13C13.1 13 14 13.9 14 15C14 16.1 13.1 17 12 17ZM15 8H9V6C9 4.34 10.34 3 12 3C13.66 3 15 4.34 15 6V8Z" fill="currentColor"/>
                            </svg>
                            密码
                        </label>
                        <el-input 
                            v-model="password" 
                            class="custom-input" 
                            placeholder="请输入密码" 
                            type="password" 
                            clearable 
                            size="large"
                            show-password
                        />
                    </div>
                </div>
                
                <!-- 按钮区域 -->
                <div class="button-area">
                    <el-button @click="register" class="submit-btn">
                        <svg class="btn-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M19 13H13V19H11V13H5V11H11V5H13V11H19V13Z" fill="currentColor"/>
                        </svg>
                        确认添加
                    </el-button>
                </div>
                
                <!-- 底部提示 -->
                <div class="footer-tip">
                    <span>添加后用户将获得普通权限</span>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* 外层包装器 - 不干扰左侧菜单 */
.add-user-wrapper {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    background: transparent;
    pointer-events: none;
    z-index: 10;
}

/* 内层容器 - 可点击区域，向左移动 */
.add-user-container {
    pointer-events: auto;
    width: 624px;
    max-width: 90%;
    margin-left: 200px;  /* 从300px减少到200px，向左移动100px */
}

/* 主卡片样式 */
.add-user-card {
    position: relative;
    padding: 36px 48px 43px;
    background: rgba(255, 254, 247, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 32px;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1),
                0 4px 12px rgba(0, 0, 0, 0.05);
    border: 1px solid rgba(217, 180, 139, 0.3);
    transition: all 0.3s ease;
}

.add-user-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 24px 48px rgba(0, 0, 0, 0.15);
    border-color: rgba(217, 180, 139, 0.5);
}

/* 顶部装饰条 */
.add-user-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 20px;
    right: 20px;
    height: 6px;
    background: linear-gradient(90deg, 
        rgba(217, 180, 139, 0) 0%,
        rgba(217, 180, 139, 0.8) 20%,
        rgba(185, 127, 84, 0.8) 50%,
        rgba(217, 180, 139, 0.8) 80%,
        rgba(217, 180, 139, 0) 100%);
    border-radius: 3px;
}

/* 图标区域 */
.icon-area {
    display: flex;
    justify-content: center;
    margin-bottom: 18px;
}

.icon-circle {
    width: 72px;
    height: 72px;
    background: linear-gradient(135deg, rgba(217, 180, 139, 0.2), rgba(185, 127, 84, 0.2));
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 2px solid rgba(217, 180, 139, 0.5);
}

.user-icon {
    width: 43px;
    height: 43px;
    color: #d98c5f;
}

/* 标题样式 */
.card-title {
    text-align: center;
    font-size: 25px;
    font-weight: 700;
    color: #5a3e2b;
    margin: 0 0 7px 0;
    letter-spacing: 1px;
}

.card-subtitle {
    text-align: center;
    font-size: 13px;
    color: #8b6b4e;
    margin: 0 0 29px 0;
}

/* 表单区域 */
.form-area {
    margin-bottom: 29px;
}

.input-group {
    margin-bottom: 22px;
}

.input-label {
    display: flex;
    align-items: center;
    gap: 7px;
    font-size: 13px;
    font-weight: 500;
    color: #6b4c34;
    margin-bottom: 7px;
}

.label-icon {
    width: 14px;
    height: 14px;
    color: #d98c5f;
}

/* 自定义输入框样式 */
.custom-input {
    width: 100%;
}

.custom-input :deep(.el-input__wrapper) {
    padding: 11px 16px;
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(217, 180, 139, 0.3);
    border-radius: 16px;
    box-shadow: none;
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
    font-size: 14px;
    color: #333;
}

.custom-input :deep(.el-input__inner::placeholder) {
    color: #bbb;
}

/* 按钮区域 */
.button-area {
    margin-bottom: 18px;
}

.submit-btn {
    width: 100%;
    padding: 13px 24px;
    background: linear-gradient(135deg, #e6b980 0%, #d98c5f 100%);
    border: none;
    border-radius: 40px;
    font-size: 14px;
    font-weight: 600;
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 7px;
    transition: all 0.3s ease;
    cursor: pointer;
}

.submit-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(217, 140, 95, 0.4);
    background: linear-gradient(135deg, #d98c5f 0%, #c46a3a 100%);
}

.submit-btn:active {
    transform: translateY(0);
}

.btn-icon {
    width: 18px;
    height: 18px;
}

/* 底部提示 */
.footer-tip {
    text-align: center;
    font-size: 11px;
    color: #a8886a;
    padding-top: 14px;
    border-top: 1px solid rgba(217, 180, 139, 0.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
    .add-user-container {
        margin-left: 0;  /* 小屏幕下居中显示 */
        width: 90%;
    }
    
    .add-user-card {
        padding: 32px 24px 38px;
    }
    
    .card-title {
        font-size: 22px;
    }
    
    .icon-circle {
        width: 64px;
        height: 64px;
    }
    
    .user-icon {
        width: 36px;
        height: 36px;
    }
    
    .submit-btn {
        padding: 11px 20px;
        font-size: 13px;
    }
}

@media (max-width: 480px) {
    .add-user-card {
        padding: 28px 20px 32px;
    }
    
    .card-title {
        font-size: 20px;
    }
    
    .custom-input :deep(.el-input__wrapper) {
        padding: 9px 14px;
    }
}
</style>