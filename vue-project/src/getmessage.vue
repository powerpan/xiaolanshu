<script setup>
import { ref } from 'vue';
import axios from 'axios'
let messagearray = ref([]);
const jwttoken = localStorage.getItem('jwttoken');
const headers = {
    'Authorization': `${jwttoken}`
};
axios.get('http://localhost:8080/user/getmessage', {
    headers: headers
})
    .then(result => {
        if (result.data.status === 1) {
            messagearray.value = [result.data.data];
        }
        else alert(result.data.message);
    }).catch(err => {
        console.log(err);
    })

const showtable = ref(true);
const editusername = ref('');
const editnickname = ref('');
const edituserpic = ref('');
const password = ref('');
const editidentity = ref('user');
const editspecialty = ref('');
const editheight = ref('');
const editweight = ref('');
const editmessage = async (param1) => {
    showtable.value = false;
    editusername.value = param1.username;
    editnickname.value = param1.nickname;
    edituserpic.value = param1.userpic;
    password.value = param1.password;
    editidentity.value = param1.identity;
    editspecialty.value = param1.specialty;
    editheight.value = param1.height;
    editweight.value = param1.weight;
}

const editmessagego = async () => {
    try {
        const params = new URLSearchParams();
        params.append('username', editusername.value)
        params.append('jwttoken', jwttoken);
        params.append('nickname', editnickname.value);
        params.append('password', password.value);
        params.append('userpic', edituserpic.value);
        params.append('identity', editidentity.value);
        params.append('specialty', editspecialty.value);
        params.append('height',editheight.value);
        params.append('weight',editweight.value);
        const response = await axios.put('http://localhost:8080/user/editmessage?' + params.toString());
        if (response.data.status === 1) {
            alert(response.data.message);
        } else {
            alert('操作失败：' + response.data.message);
        }
    } catch (error) {
        console.error(error);
        alert('请求失败，请稍后重试');
    }
    editnickname.value = ''; editusername.value = ''; edituserpic.value = ''; password.value = '';
    showtable.value = true; editidentity.value = '';editspecialty.value = '';editheight.value = '';editweight.value = '';
    axios.get('http://localhost:8080/user/getmessage', {
        headers: headers
    })
        .then(result => {
            if (result.data.status === 1)
        {
            messagearray.value = [result.data.data];
            console.log(result.data);
        }
            else alert(result.data.message);
        }).catch(err => {
            console.log(err);
        })
}

const back = async () => {
    showtable.value = true;
}
</script>

<template>
    <!-- 展示页面 -->
    <div v-if="showtable" class="users-wrapper">
        <div class="users-container">
            <div v-for="(user, index) in messagearray" :key="index" class="user-card">
                <table cellspacing="0" class="vertical-table">
                    <tr>
                        <td class="attr-label">Username</td>
                        <td class="attr-value">{{ user.username }}</td>
                    </tr>
                    <tr>
                        <td class="attr-label">Password</td>
                        <td class="attr-value">{{ user.password }}</td>
                    </tr>
                    <tr>
                        <td class="attr-label">Nickname</td>
                        <td class="attr-value">{{ user.nickname }}</td>
                    </tr>
                    <tr>
                        <td class="attr-label">身份</td>
                        <td class="attr-value">
                            <span v-if="user.identity == 'user'">用户</span>
                            <span v-if="user.identity == 'ADMIN'">管理员</span>
                        </td>
                    </tr>
                    <tr>
                        <td class="attr-label">身高</td>
                        <td class="attr-value">{{ user.height }}</td>
                    </tr>
                    <tr>
                        <td class="attr-label">体重</td>
                        <td class="attr-value">{{ user.weight }}</td>
                    </tr>
                    <tr>
                        <td class="attr-label">特长</td>
                        <td class="attr-value">{{ user.specialty }}</td>
                    </tr>
                    <tr>
                        <td colspan="2" class="action-cell">
                            <button @click="editmessage(user)" class="btn-grad">编辑</button>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>

    <!-- 编辑表单 - 继续向左移动，宽度增加20% -->
    <div class="edit-wrapper" v-if="!showtable">
        <div class="edit-container">
            <div class="edit-card">
                <div class="edit-header">
                    <div class="edit-icon">
                        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M3 17.25V21H6.75L17.81 9.94L14.06 6.19L3 17.25ZM20.71 7.04C21.1 6.65 21.1 6.02 20.71 5.63L18.37 3.29C17.98 2.9 17.35 2.9 16.96 3.29L15.13 5.12L18.88 8.87L20.71 7.04Z" fill="currentColor"/>
                        </svg>
                    </div>
                    <h3 class="edit-title">编辑个人信息</h3>
                </div>
                
                <div class="edit-form">
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label">昵称</label>
                            <el-input v-model="editnickname" class="input-field" placeholder="昵称" clearable />
                        </div>
                        <div class="form-group">
                            <label class="form-label">密码</label>
                            <el-input v-model="password" class="input-field" placeholder="密码" type="password" clearable show-password />
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label">身高(cm)</label>
                            <el-input v-model="editheight" class="input-field" placeholder="身高" clearable />
                        </div>
                        <div class="form-group">
                            <label class="form-label">体重(kg)</label>
                            <el-input v-model="editweight" class="input-field" placeholder="体重" clearable />
                        </div>
                    </div>
                    
                    <div class="form-group full">
                        <label class="form-label">特长</label>
                        <el-input v-model="editspecialty" class="input-field" placeholder="特长" clearable />
                    </div>
                </div>
                
                <div class="edit-actions">
                    <button class="cancel-btn" @click="back">返回</button>
                    <button class="confirm-btn" @click="editmessagego">确认修改</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* 展示页面包装器 - 不干扰左侧菜单 */
.users-wrapper {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    overflow-y: auto;
    pointer-events: none;
    z-index: 10;
}

/* 展示页面容器 */
.users-container {
    pointer-events: auto;
    width: 780px;
    margin: 60px 0 60px 380px;
    padding: 0 20px;
    transition: all 0.2s;
}

/* 每个用户的卡片 */
.user-card {
    margin-bottom: 35px;
    background: rgba(255, 254, 247, 0.85);
    border-radius: 20px;
    overflow: hidden;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
    transition: transform 0.2s, box-shadow 0.2s;
    border: 1px solid rgba(233, 224, 208, 0.6);
    position: relative;
    backdrop-filter: blur(2px);
}
.user-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 12px 24px -10px rgba(0, 0, 0, 0.15);
}
.user-card::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 6px;
    background: linear-gradient(90deg, rgba(217, 180, 139, 0.8), rgba(185, 127, 84, 0.8), rgba(217, 180, 139, 0.8));
    z-index: 1;
}

.vertical-table {
    width: 100%;
    border-collapse: collapse;
    background-color: transparent;
}

.attr-label {
    width: 260px;
    background: rgba(250, 246, 237, 0.9);
    font-weight: 700;
    color: #5a3e2b;
    padding: 18px 20px;
    border: 1px solid rgba(229, 217, 204, 0.6);
    text-align: right;
    letter-spacing: 0.5px;
    font-size: 15px;
    border-right-width: 2px;
    border-right-color: rgba(212, 196, 176, 0.7);
}

.attr-value {
    padding: 18px 20px;
    border: 1px solid rgba(229, 217, 204, 0.6);
    text-align: left;
    word-break: break-word;
    white-space: normal;
    background-color: rgba(255, 255, 255, 0.7);
    color: #2c3e2f;
    font-size: 15px;
}

.action-cell {
    padding: 20px;
    text-align: center;
    background: rgba(254, 250, 242, 0.8);
    border: 1px solid rgba(229, 217, 204, 0.6);
    border-top: 2px solid rgba(222, 203, 174, 0.7);
}

.btn-grad {
    background: linear-gradient(95deg, #e6b980 0%, #d98c5f 100%);
    border: none;
    margin: 0 auto;
    padding: 10px 32px;
    text-align: center;
    text-transform: uppercase;
    font-weight: 600;
    letter-spacing: 1px;
    transition: all 0.3s ease;
    border-radius: 40px;
    color: white;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    display: inline-block;
    font-size: 14px;
}
.btn-grad:hover {
    background: linear-gradient(95deg, #d98c5f 0%, #c46a3a 100%);
    transform: translateY(-2px);
    box-shadow: 0 6px 14px rgba(217, 140, 95, 0.3);
}

.attr-value span {
    display: inline-block;
    padding: 4px 14px;
    border-radius: 30px;
    font-size: 13px;
    background: rgba(234, 227, 213, 0.9);
    color: #5a3e2b;
}

/* 编辑表单 - 继续向左移动，宽度增加20% */
.edit-wrapper {
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

.edit-container {
    pointer-events: auto;
    width: 696px;  /* 原580px * 1.2 = 696px，增加20% */
    max-width: 90%;
    margin-left: 160px;  /* 从260px减小到160px，继续向左移动 */
}

.edit-card {
    background: rgba(255, 254, 247, 0.95);
    backdrop-filter: blur(4px);
    border-radius: 28px;
    padding: 40px 44px;
    box-shadow: 0 15px 30px -10px rgba(0, 0, 0, 0.15);
    border: 1px solid rgba(210, 190, 170, 0.5);
}

.edit-header {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 14px;
    margin-bottom: 36px;
}

.edit-icon svg {
    width: 36px;
    height: 36px;
    color: #d98c5f;
}

.edit-title {
    font-size: 28px;
    font-weight: 700;
    color: #5a3e2b;
    margin: 0;
}

.edit-form {
    margin-bottom: 36px;
}

.form-row {
    display: flex;
    gap: 28px;
    margin-bottom: 28px;
}

.form-group {
    flex: 1;
}

.form-group.full {
    flex: 100%;
}

.form-label {
    display: block;
    font-size: 15px;
    font-weight: 500;
    color: #6b4c34;
    margin-bottom: 12px;
}

.input-field {
    width: 100%;
}
.input-field :deep(.el-input__wrapper) {
    padding: 14px 20px;
    font-size: 16px;
    border-radius: 40px;
    background: rgba(255, 255, 255, 0.8);
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
    transition: all 0.2s;
}
.input-field :deep(.el-input__wrapper:hover) {
    box-shadow: 0 0 0 1px rgba(217, 180, 139, 0.6);
    background: rgba(255, 255, 255, 0.95);
}
.input-field :deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 0 0 1px #d98c5f;
}
.input-field :deep(.el-input__inner) {
    font-size: 16px;
}

.edit-actions {
    display: flex;
    gap: 28px;
    justify-content: center;
}

.cancel-btn, .confirm-btn {
    flex: 1;
    padding: 14px 28px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 40px;
    cursor: pointer;
    transition: all 0.2s;
    border: none;
}

.cancel-btn {
    background: rgba(237, 227, 214, 0.9);
    color: #6b4c34;
}
.cancel-btn:hover {
    background: rgba(224, 209, 192, 0.9);
    transform: translateY(-1px);
}

.confirm-btn {
    background: linear-gradient(95deg, #e6b980, #d98c5f);
    color: white;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
.confirm-btn:hover {
    background: linear-gradient(95deg, #d98c5f, #c46a3a);
    transform: translateY(-1px);
    box-shadow: 0 4px 10px rgba(217, 140, 95, 0.3);
}

/* 响应式 */
@media (max-width: 1200px) {
    .users-container {
        width: 650px;
        margin: 40px 0 40px 280px;
    }
    .attr-label {
        width: 220px;
        padding: 14px 12px;
    }
    .attr-value {
        padding: 14px 12px;
    }
    .edit-container {
        margin-left: 100px;
        width: 90%;
    }
}

@media (max-width: 900px) {
    .users-container {
        width: 90%;
        margin: 30px auto;
    }
    .attr-label {
        width: 180px;
    }
    .edit-container {
        margin-left: 0;
    }
}

@media (max-width: 600px) {
    .edit-card {
        padding: 28px 24px;
    }
    .form-row {
        flex-direction: column;
        gap: 20px;
    }
    .edit-title {
        font-size: 24px;
    }
    .edit-icon svg {
        width: 30px;
        height: 30px;
    }
    .edit-actions {
        gap: 16px;
        flex-direction: column;
    }
    .cancel-btn, .confirm-btn {
        padding: 12px 24px;
        font-size: 15px;
    }
}
</style>