<script setup>
import { ref } from 'vue';
import axios from 'axios'
let requirementarray = ref([]);
const jwttoken = localStorage.getItem('jwttoken');
const headers = {
    'Authorization': `${jwttoken}` // 正确的 Bearer token 格式
};
axios.get('http://localhost:8080/requirement/getmyrequirement', {
    headers: headers
})
    .then(result => {
        if (result.data.status === 1)
            requirementarray.value = result.data.data;
        else alert(result.data.message);
    }).catch(err => {
        console.log(err);
    })

const showtable = ref(true);
const createtime = ref('');
const requirementtitle = ref('');
const requirementcontent = ref('');
const requirementreply = async (param1) => {
    showtable.value = false;
    createtime.value = param1.createtime;
    requirementtitle.value = param1.title;
    requirementcontent.value = param1.content;
    console.log(createtime.value);
}

const requirementreplygo = async () => {
    try {
        const params = new URLSearchParams();
        params.append('createtime', createtime.value)
        params.append('jwttoken', jwttoken);
        params.append('content', requirementcontent.value);
        params.append('title', requirementtitle.value);
        const response = await axios.put('http://localhost:8080/requirement/editrequirement?' + params.toString());
        if (response.data.status === 1) {
            alert(response.data.message);
        } else {
            alert('操作失败：' + response.data.message);
        }
    } catch (error) {
        console.error(error);
        alert('请求失败，请稍后重试');
    }
    requirementtitle.value = ''; requirementcontent.value = ''; createtime.value = ''; showtable.value = true;
    axios.get('http://localhost:8080/requirement/getmyrequirement', {
        headers: headers
    })
        .then(result => {
            if (result.data.status === 1)
                requirementarray.value = result.data.data;
            else alert(result.data.message);
        }).catch(err => {
            console.log(err);
        })
}

const back = async () => {
    showtable.value = true;
}

const deleterequirement = async (param1) => {
    try {
        const params = new URLSearchParams();
        params.append('jwttoken', jwttoken);
        params.append('createtime', param1.createtime);
        params.append('requirementtitle',param1.title);
        params.append('requirementcontent',param1.content);
        const response = await axios.put('http://localhost:8080/requirement/deleterequirement?' + params.toString());
        if (response.data.status === 1) {
            alert(response.data.message);
            axios.get('http://localhost:8080/requirement/getmyrequirement', {
                headers: headers
            })
                .then(result => {
                    if (result.data.status === 1)
                        requirementarray.value = result.data.data;
                    else alert(result.data.message);
                }).catch(err => {
                    console.log(err);
                })
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
    <table cellspacing="0" class="center-table" v-if="showtable">
        <!-- 表头行 -->
        <tr>
            <th>title</th>
            <th>content</th>
            <th>author</th>
            <th>createtime</th>
            <th>lastchangetime</th>
            <th>操作</th>
        </tr>
        <tr v-for="(requirement, index) in requirementarray" :key="index">
            <td class="multi-line-title">{{ requirement.title }}</td>
            <td class="multi-line-content">{{ requirement.content }}</td>
            <td>{{ requirement.author }}</td>
            <td>{{ requirement.createtime }}</td>
            <td>{{ requirement.lastchangetime }}</td>
            <td>
                <div class="button-group">
                    <button class="btn-grad" v-on:click="requirementreply(requirement)">编辑</button>
                    <button class="btn-grad" v-on:click="deleterequirement(requirement)">删除需求</button>
                </div>
            </td>
        </tr>
    </table>

    <div class="register-container" v-if="!showtable">
        <div class="register-box">
            <p>编辑我的需求</p>
            <el-input v-model="requirementtitle" class="input-field" placeholder="title" clearable />
            <el-input v-model="requirementcontent" class="input-field" placeholder="content" clearable />
            <div class="button-group">
                <el-button v-on:click="back">
                    返回
                </el-button>
                <el-button v-on:click="requirementreplygo">
                    确认
                </el-button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.center-table {
    border-collapse: collapse;
    width: 60%;
    /* 设置表格宽度 */
    margin-top: 5%;
    /* 设置距离顶部的距离，使表格垂直居中 */
    margin-left: 350px;
    /* 设置距离左侧的距离 */
}

th,
td {
    padding: 8px;
    text-align: left;
    border: 1px solid #4caf50;
}

.center-table {
    border-collapse: collapse;
    width: 60%;
    /* 设置表格宽度 */
    margin-top: 5%;
    /* 设置距离顶部的距离，使表格垂直居中 */
    margin-left: 350px;
    /* 设置距离左侧的距离 */
}


.register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
}

.register-box {
    padding: 30px;
    border: 1px solid #ddd;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    text-align: center;
    width: 280px;
    background-color: #fff;
}

.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
}

.login-box {
    padding: 30px;
    border: 1px solid #ddd;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    text-align: center;
    width: 280px;
    background-color: #fff;
}

.input-field {
    width: 240px;
    margin-bottom: 20px;
    /* 增加文本框间的垂直间距 */
}

.button-group {
    display: flex;
    justify-content: center;
    /* 居中按钮 */
    gap: 15px;
    /* 使用 gap 来增加按钮之间的间距 */
    display: flex;
    justify-content: flex-start; /* 左对齐按钮 */
    gap: 15px;
    margin-left: 0px; /* 减少外边距 */
    padding-left: 10px; /* 或者减少内边距 */
}

.button-group el-button {
    margin-top: 20px;
    /* 增加按钮与输入框之间的垂直间距 */
}

p {
    margin-bottom: 20px;
    /* 增加标题与输入框之间的间距 */
}
.btn-grad {
    background-image: linear-gradient(to right, #EDE574 0%, #E1F5C4 51%, #EDE574 100%)
}

.btn-grad {
    margin: 5px;
    padding: 5px 10px;
    text-align: center;
    text-transform: uppercase;
    transition: 0.5s;
    background-size: 200% auto;
    color: black;
    box-shadow: 0 0 20px #eee;
    border-radius: 5px;
    display: block;
}

.btn-grad:hover {
    background-position: right center;
    /* change the direction of the change here */
    color: red;
    text-decoration: none;
}

/* 设置content内容支持换行 */
.multi-line-content {
    word-wrap: break-word;  /* 强制长单词换行 */
    white-space: normal;  /* 允许内容换行 */
    max-width: 450px;        /* 设置最大宽度为200px */
}

/* 设置content内容支持换行 */
.multi-line-title {
    word-wrap: break-word;  /* 强制长单词换行 */
    white-space: normal;  /* 允许内容换行 */
    max-width: 200px;        /* 设置最大宽度为200px */
}
</style>
