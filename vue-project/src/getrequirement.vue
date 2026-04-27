<script setup>
import { ref } from 'vue';
import axios from 'axios'
let requirementarray = ref([]);
const jwttoken = localStorage.getItem('jwttoken');
const headers = {
    'Authorization': `${jwttoken}` // 正确的 Bearer token 格式
};
axios.get('http://localhost:8080/requirement/getrequirement', {
    headers: headers
})
    .then(result => {
        if (result.data.status === 1)
            requirementarray.value = result.data.data;
        else alert(result.data.message);
    }).catch(err => {
        console.log(err);
    })

const searchtitle = ref('');
const searchrequirement = async () => {
    if (searchtitle.value != '') {
        const params = new URLSearchParams();
        params.append('title', searchtitle.value)
        params.append('jwttoken', jwttoken);
        axios.get('http://localhost:8080/requirement/searchrequirement?'+params.toString())
            .then(result => {
                if (result.data.status === 1)
                    requirementarray.value = result.data.data;
                else alert(result.data.message);
            }).catch(err => {
                console.log(err);
            })
    }
    else {
        axios.get('http://localhost:8080/requirement/getrequirement', {
            headers: headers
        })
            .then(result => {
                if (result.data.status === 1) {
                    requirementarray.value = result.data.data;
                }
                else alert(result.data.message);
            }).catch(err => {
                console.log(err);
            })
    }
}

const showtable = ref(true);
const requirementtitle = ref('');
const requirementcontent = ref('');
const requirementauthor = ref('');
const content = ref('');
const requirementreply = async (param1) => {
    showtable.value = false;
    requirementtitle.value = param1.title;
    requirementcontent.value = param1.content;
    requirementauthor.value = param1.author;
}

const requirementreplygo = async () => {
    try {
        const params = new URLSearchParams();
        params.append('requirementtitle', requirementtitle.value)
        params.append('jwttoken', jwttoken);
        params.append('requirementcontent', requirementcontent.value);
        params.append('requirementauthor', requirementauthor.value);
        params.append('content', content.value);
        const response = await axios.put('http://localhost:8080/requirement/requirementreply?' + params.toString());
        if (response.data.status === 1) {
            alert(response.data.message);
        } else {
            alert('操作失败：' + response.data.message);
        }
    } catch (error) {
        console.error(error);
        alert('请求失败，请稍后重试');
    }
    requirementtitle.value = ''; requirementcontent.value = ''; requirementauthor.value = ''; content.value = ''; showtable.value = true;
    axios.get('http://localhost:8080/requirement/getrequirement', {
        headers: headers
    })
        .then(result => {
            if (result.data.status === 1) {
                requirementarray.value = result.data.data;
            }
            else alert(result.data.message);
        }).catch(err => {
            console.log(err);
        })
}
</script>

<template>
    <div style="display: flex; justify-content: center; gap: 10px; margin-top: 50px;"  v-if="showtable">
        <el-input v-model="searchtitle" class="input-field" placeholder="需求标题" clearable style="width: 400px;" />
        <el-button v-on:click="searchrequirement">搜索</el-button>
    </div>

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
                <button class="btn-grad" v-on:click="requirementreply(requirement)">发布回复</button>
            </td>
        </tr>
    </table>

    <div class="register-container" v-if="!showtable">
        <div class="register-box">
            <p>发布回复</p>
            <el-input v-model="content" class="input-field1" placeholder="内容" clearable type="textarea" rows="16" />
            <div class="button-group">
                <el-button v-on:click="requirementreplygo">
                    确认发布
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
    margin-top: 0%;
    /* 设置距离顶部的距离，使表格垂直居中 */
    margin-left: 350px;
    /* 设置距离左侧的距离 */
}

th,
td {
    padding: 8px;
    text-align: left;
    border: 1px solid #4caf50; /* 加深颜色并增加边框宽度 */
}


.action-button {
    margin-right: 20px;
    /* 按钮之间的水平间距 */
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
    width: 1000px;
    background-color: #fff;
    margin-left: 230px; /* 设置距离左侧的具体距离 */
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
    gap: 50px;
    /* 使用 gap 来增加按钮之间的间距 */
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

.input-field1 textarea {
  width: 100%;
  /* 让文本框宽度适应父容器 */
  max-width: 40ch;
  /* 设置每行最多显示40个字符 */
  word-wrap: break-word;
  /* 防止单词溢出 */
}
</style>
