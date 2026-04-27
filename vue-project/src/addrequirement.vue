<script setup>
import { ref } from 'vue'
import axios from 'axios' 
const title = ref('')
const content = ref('')
let temprequirementarray = ref([]);
const jwttoken = localStorage.getItem('jwttoken');
const headers = {
    'Authorization': `${jwttoken}` // 正确的 Bearer token 格式
};
axios.get('http://localhost:8080/requirement/gettemprequirement', {
  headers: headers
})
  .then(result => {
    if (result.data.status === 1) {
      temprequirementarray.value = result.data.data;
      console.log(temprequirementarray.value);
      if (temprequirementarray.value.length > 0) {
        title.value = temprequirementarray.value[0].title;
        content.value = temprequirementarray.value[0].content;
      }
    }
    else alert(result.data.message);
  }).catch(err => {
    console.log(err);
  });
const addrequirement = async () => {
  try {
    const params = new URLSearchParams();
    params.append('jwttoken',jwttoken);
    params.append('title', title.value);
    params.append('content', content.value);
    const response = await axios.put('http://localhost:8080/requirement/addrequirement?' + params.toString(),);
    if (response.data.status===1) {
      alert(response.data.message);
    } else {
      alert('操作失败：' + response.data.message);
    }
  } catch (error) {
    console.error(error);
    alert('请求失败，请稍后重试');
  }
  title.value='';
  content.value='';
}

const deletetemprequirement = async () =>
{
  try {
      const params = new URLSearchParams();
      params.append('jwttoken', jwttoken);
      const response = await axios.put('http://localhost:8080/requirement/deletetemprequirement?' + params.toString(),);
      if (response.data.status === 1) {
        alert(response.data.message);
      } else {
        alert('操作失败：' + response.data.message);
      }
    } catch (error) {
      console.error(error);
      alert('请求失败，请稍后重试');
    }
    title.value = '';
    content.value = '';
}

const addtemprequirement = async () => {
  if (title.value == '' || content.value == '') {
    alert('标题和内容均不能为空');
  }
  else {
    try {
      const params = new URLSearchParams();
      params.append('jwttoken', jwttoken);
      const response = await axios.put('http://localhost:8080/requirement/deletetemprequirement?' + params.toString(),);
      if (response.data.status === 1) {
      } else {
        alert('操作失败：' + response.data.message);
      }
    } catch (error) {
      console.error(error);
      alert('请求失败，请稍后重试');
    }
    try {
      const params = new URLSearchParams();
      params.append('jwttoken', jwttoken);
      params.append('title', title.value);
      params.append('content', content.value);
      const response = await axios.put('http://localhost:8080/requirement/savetemprequirement?' + params.toString(),);
      if (response.data.status === 1) {
        alert(response.data.message);
      } else {
        alert('操作失败：' + response.data.message);
      }
    } catch (error) {
      console.error(error);
      alert('请求失败，请稍后重试');
    }

    axios.get('http://localhost:8080/requirement/gettemprequirement', {
  headers: headers
})
  .then(result => {
    if (result.data.status === 1) {
      temprequirementarray.value = result.data.data;
      console.log(temprequirementarray.value);
      if (temprequirementarray.value.length > 0) {
        title.value = temprequirementarray.value[0].title;
        content.value = temprequirementarray.value[0].content;
      }
    }
    else alert(result.data.message);
  }).catch(err => {
    console.log(err);
  });
  }
}
</script>

<template>
    <div class="register-container">
      <div class="register-box">
        <p>发布需求</p>
        <el-input v-model="title" class="input-field" placeholder="标题" clearable />
        <el-input v-model="content" class="input-field1" placeholder="内容" clearable type="textarea" rows="16" />
        <div class="button-group">
          <el-button v-on:click="deletetemprequirement">
            删除暂存
        </el-button>
          <el-button v-on:click="addtemprequirement">
            暂存
          </el-button>
          <el-button v-on:click="addrequirement">
            确定
          </el-button>
        </div>
      </div>
    </div>
  </template>
  
  <style scoped>

/* 这是外部容器的样式，通常用于设置包含的元素布局 */
.register-container {
  display: flex;  /* 设置为flex布局，使子元素能够灵活排列 */
  margin-left: 400px;
  margin-top: 60px;  /* 在垂直（交叉轴）方向上居中子元素 */
  height: 75vh;  /* 容器高度设置为视口高度的 75% */
}

/* 这是注册框的样式，通常用于设置注册框的外观和尺寸 */
.register-box {
  padding: 30px;  /* 设置内边距，给框内内容周围留出30px的空间 */
  border: 1px solid #ddd;  /* 设置边框，颜色为浅灰色，宽度为1px */
  border-radius: 8px;  /* 设置圆角，8px的圆角半径 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);  /* 设置阴影，水平偏移0px，垂直偏移4px，模糊半径8px，颜色为 rgba(0, 0, 0, 0.1)（轻微阴影） */
  text-align: center;  /* 使文本内容居中对齐 */
  width: 1000px;  /* 设置框的宽度为600px */
  background-color: #fff;  /* 设置背景颜色为白色 */
}

  
  .input-field {
    width: 1000px;
    margin-bottom: 20px; /* 增加文本框间的垂直间距 */
  }

  .input-field1 {
    width: 1000px;
    height: 350px;
    margin-bottom: 20px; /* 增加文本框间的垂直间距 */
  }
  
  .button-group {
    display: flex;
    justify-content: center; /* 居中按钮 */
    gap: 50px; /* 使用 gap 来增加按钮之间的间距 */
  }
  
  .button-group el-button {
    margin-top: 20px; /* 增加按钮与输入框之间的垂直间距 */
  }
  
  p {
    margin-bottom: 20px; /* 增加标题与输入框之间的间距 */
  }

  /* 设置每行最大显示字符数 */
.input-field1 textarea {
  width: 100%;
  /* 让文本框宽度适应父容器 */
  max-width: 40ch;
  /* 设置每行最多显示40个字符 */
  word-wrap: break-word;
  /* 防止单词溢出 */
}
  </style>