<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router';
const jwttoken = localStorage.getItem('jwttoken');
const headers = {
  'Authorization': `${jwttoken}`
};
let tempnoticearray = ref([]);
const title = ref('')
const content = ref('')
axios.get('http://localhost:8080/notice/gettempnotice', {
  headers: headers
})
  .then(result => {
    if (result.data.status === 1) {
      tempnoticearray.value = result.data.data;
      console.log(tempnoticearray.value);
      if (tempnoticearray.value.length > 0) {
        title.value = tempnoticearray.value[0].title;
        content.value = tempnoticearray.value[0].content;
      }
    }
    else alert(result.data.message);
  }).catch(err => {
    console.log(err);
  });

const addnotice = async () => {
  try {
    const params = new URLSearchParams();
    params.append('jwttoken', jwttoken);
    params.append('title', title.value);
    params.append('content', content.value);
    const response = await axios.put('http://localhost:8080/notice/addnotice?' + params.toString(),);
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
  axios.get('http://localhost:8080/notice/gettempnotice', {
    headers: headers
  })
    .then(result => {
      if (result.data.status === 1) {
        tempnoticearray.value = result.data.data;
        console.log(tempnoticearray.value);
        if (tempnoticearray.value.length > 0) {
          title.value = tempnoticearray.value[0].title;
          content.value = tempnoticearray.value[0].content;
        }
      }
      else alert(result.data.message);
    }).catch(err => {
      console.log(err);
    });
}

const deletetempnotice = async () =>
{
  try {
      const params = new URLSearchParams();
      params.append('jwttoken', jwttoken);
      const response = await axios.put('http://localhost:8080/notice/deletetempnotice?' + params.toString(),);
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

const addtempnotice = async () => {
  if (title.value == '' || content.value == '') {
    alert('标题和内容均不能为空');
  }
  else {
    try {
      const params = new URLSearchParams();
      params.append('jwttoken', jwttoken);
      const response = await axios.put('http://localhost:8080/notice/deletetempnotice?' + params.toString(),);
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
      const response = await axios.put('http://localhost:8080/notice/savetempnotice?' + params.toString(),);
      if (response.data.status === 1) {
        alert(response.data.message);
      } else {
        alert('操作失败：' + response.data.message);
      }
    } catch (error) {
      console.error(error);
      alert('请求失败，请稍后重试');
    }
  }
}
</script>

<template>
  <!-- 包装器 - 不干扰左侧菜单 -->
  <div class="notice-wrapper">
    <div class="notice-container">
      <div class="notice-box">
        <p>添加公告</p>
        <el-input v-model="title" class="input-field" placeholder="标题" clearable />
        <el-input v-model="content" class="input-field1" placeholder="内容" clearable type="textarea" :rows="12" />
        <div class="button-group">
          <el-button @click="deletetempnotice" class="btn-secondary">删除暂存</el-button>
          <el-button @click="addtempnotice" class="btn-secondary">暂存</el-button>
          <el-button @click="addnotice" class="btn-primary">确定</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 包装器 - 不干扰左侧菜单 */
.notice-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow-y: auto;
  pointer-events: none;
  z-index: 10;
}

/* 容器 */
.notice-container {
  pointer-events: auto;
  width: calc(100% - 420px);
  max-width: 900px;
  min-width: 500px;
  margin: 80px 20px 60px 380px;
}

/* 卡片：半透明背景 + 毛玻璃 + 淡化边框 */
.notice-box {
  position: relative;
  padding: 32px 30px;
  border: 1px solid rgba(210, 190, 170, 0.5);
  border-radius: 28px;
  box-shadow: 0 15px 30px -10px rgba(0, 0, 0, 0.1);
  text-align: center;
  background: rgba(255, 254, 247, 0.9);
  backdrop-filter: blur(4px);
}

/* 顶部装饰条半透明 */
.notice-box::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 5px;
  background: linear-gradient(90deg, rgba(217, 180, 139, 0.8), rgba(185, 127, 84, 0.8), rgba(217, 180, 139, 0.8));
  border-radius: 28px 28px 0 0;
}

/* 标题 */
.notice-box p {
  font-size: 24px;
  font-weight: 700;
  color: #6b4c34;
  margin-bottom: 24px;
}

/* 普通输入框（标题） */
.input-field {
  width: 100%;
  margin-bottom: 20px;
}
.input-field :deep(.el-input__wrapper) {
  padding: 6px 14px;
  font-size: 14px;
  border-radius: 40px;
  background: rgba(255, 255, 255, 0.7);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  transition: box-shadow 0.2s;
}
.input-field :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px rgba(217, 180, 139, 0.6);
}

/* 多行文本框（内容） */
.input-field1 {
  width: 100%;
  margin-bottom: 20px;
}
.input-field1 :deep(.el-textarea__inner) {
  padding: 10px 14px;
  font-size: 14px;
  border-radius: 20px;
  font-family: inherit;
  resize: vertical;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(210, 190, 170, 0.5);
  transition: border-color 0.2s, box-shadow 0.2s;
}
.input-field1 :deep(.el-textarea__inner:focus) {
  border-color: #d9b48b;
  box-shadow: 0 0 0 1px #d9b48b;
}

/* 按钮组 */
.button-group {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-top: 20px;
}

/* 次要按钮：半透明背景 */
.btn-secondary, .btn-primary {
  border: none;
  padding: 8px 24px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-secondary {
  background: rgba(237, 227, 214, 0.9);
  color: #6b4c34;
}
.btn-secondary:hover {
  background: rgba(224, 209, 192, 0.9);
  transform: translateY(-1px);
}
.btn-primary {
  background: linear-gradient(95deg, #e6b980, #d98c5f);
  color: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
.btn-primary:hover {
  background: linear-gradient(95deg, #d98c5f, #c46a3a);
  transform: translateY(-1px);
  box-shadow: 0 4px 10px rgba(217, 140, 95, 0.3);
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .notice-container {
    width: calc(100% - 240px);
    margin: 60px 20px 40px 200px;
  }
  .notice-box {
    padding: 28px 24px;
  }
  .button-group {
    gap: 20px;
    flex-wrap: wrap;
  }
}

@media (max-width: 900px) {
  .notice-container {
    width: calc(100% - 60px);
    margin: 40px auto;
  }
}

@media (max-width: 768px) {
  .notice-container {
    width: calc(100% - 40px);
    margin: 30px auto;
  }
  .notice-box {
    padding: 20px 18px;
  }
  .notice-box p {
    font-size: 20px;
    margin-bottom: 18px;
  }
  .btn-secondary, .btn-primary {
    padding: 6px 16px;
    font-size: 12px;
  }
}
</style>