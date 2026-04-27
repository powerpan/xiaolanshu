<script setup>
import { ref } from 'vue';
import axios from 'axios'
let messagearray = ref([]);
const jwttoken = localStorage.getItem('jwttoken');
const headers = {
    'Authorization': `${jwttoken}`
};
axios.get('http://localhost:8080/user/getallmessage', {
    headers: headers
})
    .then(result => {
        if (result.data.status === 1) {
            messagearray.value = result.data.data;
        }
        else alert(result.data.message);
    }).catch(err => {
        console.log(err);
    });


const deleteuser = async (param1) => {
    try {
        const params = new URLSearchParams();
        params.append('jwttoken', jwttoken);
        params.append('username', param1);
        const response = await axios.put('http://localhost:8080/user/deleteuser?' + params.toString());
        if (response.data.status === 1) {
            alert(response.data.message);
            axios.get('http://localhost:8080/user/getallmessage', {
                headers: headers
            })
                .then(result => {
                    if (result.data.status === 1) {
                        messagearray.value = result.data.data;
                    }
                    else alert(result.data.message);
                }).catch(err => {
                    console.log(err);
                });
        } else {
            alert('操作失败：' + response.data.message);
        }
    } catch (error) {
        console.error(error);
        alert('请求失败，请稍后重试');
    }
}

const registeredchange = async (param1) => {
    try {
        const params = new URLSearchParams();
        params.append('jwttoken', jwttoken);
        params.append('username', param1.username);
        params.append('registered', param1.registered)
        const response = await axios.put('http://localhost:8080/user/editregister?' + params.toString());
        if (response.data.status === 1) {
            alert(response.data.message);
            axios.get('http://localhost:8080/user/getallmessage', {
                headers: headers
            })
                .then(result => {
                    if (result.data.status === 1) {
                        messagearray.value = result.data.data;
                    }
                    else alert(result.data.message);
                }).catch(err => {
                    console.log(err);
                });
        } else {
            alert('操作失败：' + response.data.message);
            axios.get('http://localhost:8080/user/getallmessage', {
                headers: headers
            })
                .then(result => {
                    if (result.data.status === 1) {
                        messagearray.value = result.data.data;
                    }
                    else alert(result.data.message);
                }).catch(err => {
                    console.log(err);
                });
        }
    } catch (error) {
        console.error(error);
        alert('请求失败，请稍后重试');
    }
}
const showtable = ref(true);
const editusername = ref('');
const editnickname = ref('');
const edituserpic = ref('');
const password = ref('');
const editidentity = ref('');
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
        const response = await axios.put('http://localhost:8080/user/editallmessage?' + params.toString());
        if (response.data.status === 1) {
            alert(response.data.message);
        } else {
            alert('操作失败：' + response.data.message);
        }
    } catch (error) {
        console.error(error);
        alert('请求失败，请稍后重试');
    }
    editnickname.value = ''; editusername.value = '';  edituserpic.value = ''; password.value = '';
    showtable.value = true; editidentity.value = ''; editspecialty.value = ''; editheight.value = '';editweight.value = '';
    axios.get('http://localhost:8080/user/getallmessage', {
        headers: headers
    })
        .then(result => {
            if (result.data.status === 1) {
                messagearray.value = result.data.data;
            }
            else alert(result.data.message);
        }).catch(err => {
            console.log(err);
        });
}

const back = async () => {
    showtable.value = true;
}
</script>

<template>
    <!-- 表格包装器 - 不干扰左侧菜单 -->
    <div v-if="showtable" class="table-wrapper">
        <div class="table-container">
            <div class="table-scroll">
                <table cellspacing="0" class="center-table">
                    <thead>
                        <tr>
                            <th>用户名</th>
                            <th>密码</th>
                            <th>昵称</th>
                            <th>身份</th>
                            <th>身高</th>
                            <th>体重</th>
                            <th>特长</th>
                            <th>注册</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(user, index) in messagearray" :key="index">
                            <td>{{ user.username }}</td>
                            <td>{{ user.password }}</td>
                            <td>{{ user.nickname }}</td>
                            <td>
                                <span v-if="user.identity == 'user'" class="identity-badge">用户</span>
                                <span v-if="user.identity == 'ADMIN'" class="identity-badge admin">管理员</span>
                            </td>
                            <td>{{ user.height }}</td>
                            <td>{{ user.weight }}</td>
                            <td>{{ user.specialty }}</td>
                            <td>
                                <el-switch v-model="user.registered" @change="registeredchange(user)" />
                            </td>
                            <td>
                                <div class="button-container">
                                    <button @click="editmessage(user)" class="btn-grad">编辑</button>
                                    <button @click="deleteuser(user.username)" class="btn-grad">删除</button>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- 优化后的编辑表单 -->
    <div v-if="!showtable" class="edit-wrapper">
        <div class="edit-container">
            <div class="edit-card">
                <div class="edit-header">
                    <div class="edit-icon">
                        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M3 17.25V21H6.75L17.81 9.94L14.06 6.19L3 17.25ZM20.71 7.04C21.1 6.65 21.1 6.02 20.71 5.63L18.37 3.29C17.98 2.9 17.35 2.9 16.96 3.29L15.13 5.12L18.88 8.87L20.71 7.04Z" fill="currentColor"/>
                        </svg>
                    </div>
                    <h3 class="edit-title">编辑用户信息</h3>
                </div>
                
                <div class="edit-form">
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label">用户名</label>
                            <el-input v-model="editusername" class="edit-input" placeholder="用户名" disabled />
                        </div>
                        <div class="form-group">
                            <label class="form-label">身份</label>
                            <el-select v-model="editidentity" class="edit-input" placeholder="请选择身份" clearable>
                                <el-option value="user" label="用户" />
                                <el-option value="ADMIN" label="管理员" />
                            </el-select>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label">昵称</label>
                            <el-input v-model="editnickname" class="edit-input" placeholder="昵称" clearable />
                        </div>
                        <div class="form-group">
                            <label class="form-label">密码</label>
                            <el-input v-model="password" class="edit-input" placeholder="密码" type="password" clearable show-password />
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label">身高(cm)</label>
                            <el-input v-model="editheight" class="edit-input" placeholder="身高" clearable />
                        </div>
                        <div class="form-group">
                            <label class="form-label">体重(kg)</label>
                            <el-input v-model="editweight" class="edit-input" placeholder="体重" clearable />
                        </div>
                    </div>
                    
                    <div class="form-group full">
                        <label class="form-label">特长</label>
                        <el-input v-model="editspecialty" class="edit-input" placeholder="特长" clearable />
                    </div>
                </div>
                
                <div class="edit-actions">
                    <button class="edit-cancel-btn" @click="back">返回</button>
                    <button class="edit-confirm-btn" @click="editmessagego">确认修改</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* 表格包装器 - 不干扰左侧菜单 */
.table-wrapper {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    overflow-y: auto;
    pointer-events: none;
    z-index: 10;
}

/* 表格容器 */
.table-container {
    pointer-events: auto;
    width: calc(100% - 380px);
    max-width: 1100px;
    min-width: 550px;
    margin: 50px 20px 50px 360px;
}

/* 表格滚动容器 */
.table-scroll {
    overflow-x: auto;
    border-radius: 16px;
}

/* 表格样式 */
.center-table {
    border-collapse: collapse;
    width: 100%;
    min-width: 750px;
    background-color: rgba(255, 254, 247, 0.75);
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
    backdrop-filter: blur(1px);
    font-size: 13px;
}

th, td {
    padding: 12px 10px;
    text-align: center;
    border-bottom: 1px solid rgba(203, 185, 160, 0.4);
}

th {
    background-color: rgba(230, 215, 195, 0.7);
    color: #5a3e2b;
    font-weight: 600;
    font-size: 13px;
    letter-spacing: 0.5px;
    white-space: nowrap;
}

td {
    background-color: rgba(255, 255, 255, 0.5);
}

tr:hover td {
    background-color: rgba(250, 245, 235, 0.6);
}

.identity-badge {
    display: inline-block;
    padding: 3px 12px;
    border-radius: 30px;
    font-size: 11px;
    background: rgba(234, 227, 213, 0.85);
    color: #5a3e2b;
    white-space: nowrap;
}
.identity-badge.admin {
    background: rgba(217, 180, 139, 0.85);
    color: white;
}

.el-switch {
    --el-switch-on-color: #d9b48b;
    --el-switch-off-color: rgba(192, 160, 128, 0.6);
    transform: scale(0.9);
}

.button-container {
    display: flex;
    justify-content: center;
    gap: 8px;
    flex-wrap: wrap;
}

.btn-grad {
    background: linear-gradient(95deg, #e6b980 0%, #d98c5f 100%);
    border: none;
    margin: 0;
    padding: 5px 12px;
    font-size: 11px;
    font-weight: 600;
    border-radius: 30px;
    color: white;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: all 0.3s ease;
    display: inline-block;
    white-space: nowrap;
}
.btn-grad:hover {
    background: linear-gradient(95deg, #d98c5f 0%, #c46a3a 100%);
    transform: translateY(-1px);
    box-shadow: 0 4px 10px rgba(217, 140, 95, 0.3);
}

/* ========== 优化后的编辑表单样式 ========== */
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
    width: 580px;
    max-width: 90%;
    margin-left: 180px;
}

.edit-card {
    background: rgba(255, 254, 247, 0.95);
    backdrop-filter: blur(4px);
    border-radius: 24px;
    padding: 32px 36px;
    box-shadow: 0 15px 30px -10px rgba(0, 0, 0, 0.15);
    border: 1px solid rgba(210, 190, 170, 0.5);
}

.edit-header {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    margin-bottom: 28px;
}

.edit-icon svg {
    width: 30px;
    height: 30px;
    color: #d98c5f;
}

.edit-title {
    font-size: 24px;
    font-weight: 700;
    color: #5a3e2b;
    margin: 0;
}

.edit-form {
    margin-bottom: 28px;
}

.form-row {
    display: flex;
    gap: 24px;
    margin-bottom: 22px;
}

.form-group {
    flex: 1;
}

.form-group.full {
    flex: 100%;
}

.form-label {
    display: block;
    font-size: 14px;
    font-weight: 500;
    color: #6b4c34;
    margin-bottom: 10px;
}

.edit-input {
    width: 100%;
}
.edit-input :deep(.el-input__wrapper) {
    padding: 11px 18px;
    font-size: 15px;
    border-radius: 40px;
    background: rgba(255, 255, 255, 0.8);
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
    transition: all 0.2s;
}
.edit-input :deep(.el-input__wrapper:hover) {
    box-shadow: 0 0 0 1px rgba(217, 180, 139, 0.6);
    background: rgba(255, 255, 255, 0.95);
}
.edit-input :deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 0 0 1px #d98c5f;
}
.edit-input :deep(.el-input__inner) {
    font-size: 15px;
}
.edit-input :deep(.el-input__inner:disabled) {
    background: rgba(0, 0, 0, 0.03);
    color: #999;
}
.edit-input :deep(.el-select__wrapper) {
    padding: 11px 18px;
    font-size: 15px;
    border-radius: 40px;
    background: rgba(255, 255, 255, 0.8);
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}
.edit-input :deep(.el-select__wrapper:hover) {
    box-shadow: 0 0 0 1px rgba(217, 180, 139, 0.6);
}
.edit-input :deep(.el-select__wrapper.is-focus) {
    box-shadow: 0 0 0 1px #d98c5f;
}

.edit-actions {
    display: flex;
    gap: 24px;
    justify-content: center;
}

.edit-cancel-btn, .edit-confirm-btn {
    flex: 1;
    padding: 11px 24px;
    font-size: 15px;
    font-weight: 600;
    border-radius: 40px;
    cursor: pointer;
    transition: all 0.2s;
    border: none;
}

.edit-cancel-btn {
    background: rgba(237, 227, 214, 0.9);
    color: #6b4c34;
}
.edit-cancel-btn:hover {
    background: rgba(224, 209, 192, 0.9);
    transform: translateY(-1px);
}

.edit-confirm-btn {
    background: linear-gradient(95deg, #e6b980, #d98c5f);
    color: white;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
.edit-confirm-btn:hover {
    background: linear-gradient(95deg, #d98c5f, #c46a3a);
    transform: translateY(-1px);
    box-shadow: 0 4px 10px rgba(217, 140, 95, 0.3);
}

/* 响应式适配 */
@media (max-width: 1200px) {
    .table-container {
        width: calc(100% - 220px);
        margin: 40px 20px 40px 200px;
    }
    .edit-container {
        margin-left: 100px;
        width: 90%;
    }
}

@media (max-width: 900px) {
    .table-container {
        width: calc(100% - 40px);
        margin: 30px auto;
    }
    .edit-container {
        margin-left: 0;
    }
    th, td {
        padding: 10px 8px;
        font-size: 12px;
    }
    .btn-grad {
        padding: 4px 10px;
        font-size: 10px;
    }
}

@media (max-width: 600px) {
    .edit-card {
        padding: 24px 20px;
    }
    .form-row {
        flex-direction: column;
        gap: 18px;
    }
    .edit-title {
        font-size: 22px;
    }
    .edit-icon svg {
        width: 26px;
        height: 26px;
    }
    .edit-actions {
        gap: 16px;
        flex-direction: column;
    }
    .edit-cancel-btn, .edit-confirm-btn {
        padding: 10px 20px;
        font-size: 14px;
    }
}

/* 滚动条样式 */
.table-scroll::-webkit-scrollbar {
    height: 6px;
}

.table-scroll::-webkit-scrollbar-track {
    background: rgba(0, 0, 0, 0.05);
    border-radius: 3px;
}

.table-scroll::-webkit-scrollbar-thumb {
    background: rgba(217, 180, 139, 0.5);
    border-radius: 3px;
}

.table-scroll::-webkit-scrollbar-thumb:hover {
    background: rgba(217, 180, 139, 0.7);
}
</style>