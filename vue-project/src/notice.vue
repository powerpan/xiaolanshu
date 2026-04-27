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

</script>

<template>
    <!-- 表格包装器 - 不干扰左侧菜单 -->
    <div class="table-wrapper">
        <div class="table-container">
            <div class="table-scroll">
                <table cellspacing="0" class="center-table">
                    <!-- 表头行 -->
                    <thead>
                        <tr>
                            <th>标题</th>
                            <th>内容</th>
                            <th>作者</th>
                            <th>创建时间</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(notice, index) in noticearray" :key="index">
                            <td class="multi-line-title">{{ notice.title }}</td>
                            <td class="multi-line-content">{{ notice.content }}</td>
                            <td>{{ notice.author }}</td>
                            <td>{{ notice.noticetime }}</td>
                        </tr>
                    </tbody>
                </table>
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
    width: calc(100% - 400px);
    max-width: 1200px;
    min-width: 500px;
    margin: 60px 20px 60px 380px;
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
    min-width: 600px;
    background-color: rgba(255, 254, 247, 0.75);
    border-radius: 20px;
    overflow: hidden;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
    backdrop-filter: blur(1px);
}

/* 单元格通用样式 */
th, td {
    padding: 16px 12px;
    text-align: left;
    border-bottom: 1px solid rgba(203, 185, 160, 0.4);
    border-right: none;
    border-left: none;
    white-space: normal;
    word-break: break-word;
}

/* 表头样式 */
th {
    background-color: rgba(230, 215, 195, 0.7);
    color: #5a3e2b;
    font-weight: 600;
    font-size: 15px;
    letter-spacing: 0.5px;
}

/* 普通单元格 */
td {
    background-color: rgba(255, 255, 255, 0.5);
}

/* 行悬停效果 */
tr:hover td {
    background-color: rgba(250, 245, 235, 0.6);
    transition: background-color 0.2s;
}

/* 标题列：限制最大宽度，允许换行 */
.multi-line-title {
    word-wrap: break-word;
    white-space: normal;
    max-width: 200px;
}

/* 内容列：限制最大宽度，允许换行 */
.multi-line-content {
    word-wrap: break-word;
    white-space: normal;
    max-width: 450px;
}

/* 响应式适配 */
@media (max-width: 1200px) {
    .table-container {
        width: calc(100% - 220px);
        margin: 40px 20px 40px 200px;
    }
    th, td {
        padding: 12px 8px;
        font-size: 13px;
    }
    .multi-line-title {
        max-width: 150px;
    }
    .multi-line-content {
        max-width: 280px;
    }
}

@media (max-width: 900px) {
    .table-container {
        width: calc(100% - 40px);
        margin: 30px auto;
    }
}

@media (max-width: 768px) {
    th, td {
        padding: 10px 6px;
        font-size: 12px;
    }
    .multi-line-title {
        max-width: 100px;
    }
    .multi-line-content {
        max-width: 180px;
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