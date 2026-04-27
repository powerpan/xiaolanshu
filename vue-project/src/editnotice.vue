<script setup>
import { ref } from 'vue';
import axios from 'axios'
let noticearray = ref([]);
const jwttoken = localStorage.getItem('jwttoken');
const headers = {
    'Authorization': `${jwttoken}`
};
axios.get('http://localhost:8080/notice/getnotice', {
    headers: headers
})
    .then(result => {
        if (result.data.status === 1) {
            noticearray.value = result.data.data;
        }
        else alert(result.data.message);
    }).catch(err => {
        console.log(err);
    });

const deletenotice = async (param1) => {
    try {
        const params = new URLSearchParams();
        params.append('jwttoken', jwttoken);
        params.append('title', param1.title);
        params.append('content', param1.content);
        const response = await axios.put('http://localhost:8080/notice/deletenotice?' + params.toString());
        if (response.data.status === 1) {
            alert(response.data.message);
            axios.get('http://localhost:8080/notice/getnotice', {
                headers: headers
            })
                .then(result => {
                    if (result.data.status === 1) {
                        noticearray.value = result.data.data;
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
const showtable = ref(true);
const lasttitle = ref('');
const lastcontent = ref('');
const title = ref('');
const content = ref('');
const editnotice = async (param1) => {
    showtable.value = false;
    lasttitle.value = param1.title;
    lastcontent.value = param1.content;
    title.value = param1.title;
    content.value = param1.content;
}
const editnoticego = async () => {
    try {
        const params = new URLSearchParams();
        params.append('lasttitle', lasttitle.value)
        params.append('jwttoken', jwttoken);
        params.append('lastcontent', lastcontent.value);
        params.append('title', title.value);
        params.append('content', content.value);
        const response = await axios.put('http://localhost:8080/notice/editnotice?' + params.toString());
        if (response.data.status === 1) {
            alert(response.data.message);
        } else {
            alert('操作失败：' + response.data.message);
        }
    } catch (error) {
        console.error(error);
        alert('请求失败，请稍后重试');
    }
    lasttitle.value = ''; lastcontent.value = ''; title.value = ''; content.value = ''; showtable.value = true;
    axios.get('http://localhost:8080/notice/getnotice', {
        headers: headers
    })
        .then(result => {
            if (result.data.status === 1) {
                noticearray.value = result.data.data;
            }
            else alert(result.data.message);
        }).catch(err => {
            console.log(err);
        });
}

const back = () => {
    showtable.value = true;
}
</script>

<template>
    <!-- 表格容器 - 使用相对定位，不干扰左侧菜单 -->
    <div v-if="showtable" class="table-wrapper">
        <div class="table-scroll">
            <table cellspacing="0" class="center-table">
                <thead>
                    <tr>
                        <th>标题</th>
                        <th>内容</th>
                        <th>作者</th>
                        <th>创建时间</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="(notice, index) in noticearray" :key="index">
                        <td class="multi-line-title">{{ notice.title }}</td>
                        <td class="multi-line-content">{{ notice.content }}</td>
                        <td>{{ notice.author }}</td>
                        <td>{{ notice.noticetime }}</td>
                        <td>
                            <div class="button-group-table">
                                <button class="btn-grad" @click="editnotice(notice)">编辑</button>
                                <button class="btn-grad" @click="deletenotice(notice)">删除</button>
                            </div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

    <!-- 编辑表单 - 增加返回按钮 -->
    <div v-if="!showtable" class="edit-wrapper">
        <div class="edit-container">
            <div class="edit-card">
                <div class="edit-header">
                    <div class="edit-icon">
                        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M3 17.25V21H6.75L17.81 9.94L14.06 6.19L3 17.25ZM20.71 7.04C21.1 6.65 21.1 6.02 20.71 5.63L18.37 3.29C17.98 2.9 17.35 2.9 16.96 3.29L15.13 5.12L18.88 8.87L20.71 7.04Z" fill="currentColor"/>
                        </svg>
                    </div>
                    <h3 class="edit-title">编辑公告</h3>
                </div>
                
                <div class="edit-form">
                    <div class="form-group">
                        <label class="form-label">标题</label>
                        <el-input v-model="title" class="edit-input" placeholder="标题" clearable />
                    </div>
                    
                    <div class="form-group">
                        <label class="form-label">内容</label>
                        <el-input v-model="content" class="edit-input" placeholder="内容" type="textarea" :rows="8" />
                    </div>
                </div>
                
                <div class="edit-actions">
                    <button class="edit-cancel-btn" @click="back">返回</button>
                    <button class="edit-confirm-btn" @click="editnoticego">确认修改</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* 表格包装器 - 使用普通块级元素，不影响左侧菜单 */
.table-wrapper {
    width: 100%;
    padding-left: 380px;
    padding-right: 20px;
    padding-top: 60px;
    box-sizing: border-box;
}

/* 表格滚动容器 */
.table-scroll {
    overflow-x: auto;
    border-radius: 20px;
}

/* 表格样式 */
.center-table {
    border-collapse: collapse;
    width: 100%;
    min-width: 800px;
    background-color: rgba(255, 254, 247, 0.75);
    border-radius: 20px;
    overflow: hidden;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
    backdrop-filter: blur(1px);
}

th, td {
    padding: 14px 12px;
    text-align: left;
    border-bottom: 1px solid rgba(203, 185, 160, 0.4);
    white-space: normal;
    word-break: break-word;
}

/* 表头样式 */
th {
    background-color: rgba(230, 215, 195, 0.7);
    color: #5a3e2b;
    font-weight: 600;
    font-size: 14px;
    letter-spacing: 0.5px;
}

/* 普通单元格 */
td {
    background-color: rgba(255, 255, 255, 0.5);
}

tr:hover td {
    background-color: rgba(250, 245, 235, 0.6);
    transition: background-color 0.2s;
}

/* 标题列换行限制 */
.multi-line-title {
    word-wrap: break-word;
    white-space: normal;
    max-width: 200px;
}

/* 内容列换行限制 */
.multi-line-content {
    word-wrap: break-word;
    white-space: normal;
    max-width: 400px;
}

/* 表格内按钮组 */
.button-group-table {
    display: flex;
    justify-content: flex-start;
    gap: 10px;
    flex-wrap: wrap;
}

/* 操作按钮 */
.btn-grad {
    background: linear-gradient(95deg, #e6b980 0%, #d98c5f 100%);
    border: none;
    margin: 0;
    padding: 5px 14px;
    font-size: 12px;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    border-radius: 40px;
    color: white;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: all 0.3s ease;
    display: inline-block;
}
.btn-grad:hover {
    background: linear-gradient(95deg, #d98c5f 0%, #c46a3a 100%);
    transform: translateY(-1px);
    box-shadow: 0 4px 10px rgba(217, 140, 95, 0.3);
}

/* 编辑表单包装器 */
.edit-wrapper {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    background: rgba(0, 0, 0, 0.3);
    pointer-events: auto;
    z-index: 100;
}

/* 编辑表单容器 */
.edit-container {
    width: 560px;
    max-width: 90%;
}

/* 编辑卡片样式 */
.edit-card {
    background: rgba(255, 254, 247, 0.95);
    backdrop-filter: blur(4px);
    border-radius: 24px;
    padding: 32px 36px;
    box-shadow: 0 15px 30px -10px rgba(0, 0, 0, 0.2);
    border: 1px solid rgba(210, 190, 170, 0.5);
}

/* 顶部装饰条 */
.edit-card::before {
    content: "";
    position: absolute;
    top: 0;
    left: 20px;
    right: 20px;
    height: 5px;
    background: linear-gradient(90deg, rgba(217, 180, 139, 0.8), rgba(185, 127, 84, 0.8), rgba(217, 180, 139, 0.8));
    border-radius: 24px 24px 0 0;
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

.form-group {
    margin-bottom: 22px;
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
.edit-input :deep(.el-textarea__inner) {
    padding: 12px 16px;
    font-size: 15px;
    border-radius: 20px;
    font-family: inherit;
    resize: vertical;
    background: rgba(255, 255, 255, 0.8);
    border: 1px solid rgba(210, 190, 170, 0.5);
    transition: border-color 0.2s, box-shadow 0.2s;
}
.edit-input :deep(.el-textarea__inner:focus) {
    border-color: #d9b48b;
    box-shadow: 0 0 0 1px #d9b48b;
}

.edit-actions {
    display: flex;
    gap: 20px;
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
    .table-wrapper {
        padding-left: 200px;
    }
}

@media (max-width: 900px) {
    .table-wrapper {
        padding-left: 20px;
        padding-right: 20px;
    }
}

@media (max-width: 768px) {
    .table-wrapper {
        padding-left: 10px;
        padding-right: 10px;
        padding-top: 40px;
    }
    th, td {
        padding: 8px 6px;
        font-size: 11px;
    }
    .btn-grad {
        padding: 3px 10px;
        font-size: 10px;
    }
    .edit-card {
        padding: 24px 20px;
    }
    .edit-title {
        font-size: 20px;
    }
    .edit-actions {
        flex-direction: column;
        gap: 12px;
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