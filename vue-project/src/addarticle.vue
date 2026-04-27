<script setup>
import { ref } from 'vue'
import axios from 'axios'

const jwttoken = localStorage.getItem('jwttoken')
const headers = {
  'Authorization': `${jwttoken}`
}

// 表单数据
const title = ref('')
const content = ref('')
const topic = ref('') // 默认未选择

// 主题选项（与之前保持一致）
const topicOptions = [
  { label: '比赛', value: '比赛' },
  { label: '教学', value: '教学' },
  { label: '打卡', value: '打卡' },
  { label: '成果', value: '成果' },
  { label: '日常', value: '日常' },
  { label: '励志', value: '励志' },
  { label: '干货', value: '干货' },
  { label: '解析', value: '解析' },
  { label: '避坑', value: '避坑' }
]

// 发布文章
const addArticle = async () => {
  // 表单校验
  if (!title.value.trim()) {
    alert('标题不能为空')
    return
  }
  if (!content.value.trim()) {
    alert('内容不能为空')
    return
  }
  if (!topic.value) {
    alert('请选择主题')
    return
  }

  try {
    const params = new URLSearchParams()
    params.append('jwttoken', jwttoken)
    params.append('title', title.value)
    params.append('content', content.value)
    params.append('topic', topic.value)

    const response = await axios.put('http://localhost:8080/article/addarticle?' + params.toString())
    if (response.data.status === 1) {
      alert(response.data.message || '发布成功')
      // 清空表单
      title.value = ''
      content.value = ''
      topic.value = ''
    } else {
      alert('发布失败：' + response.data.message)
    }
  } catch (error) {
    console.error(error)
    alert('请求失败，请稍后重试')
  }
}
</script>

<template>
  <!-- 包装器 - 不干扰左侧菜单 -->
  <div class="article-wrapper">
    <div class="article-container">
      <div class="article-box">
        <p>发布文章</p>

        <!-- 标题输入 -->
        <el-input 
          v-model="title" 
          class="input-field" 
          placeholder="标题（必填）" 
          clearable 
        />

        <!-- 主题选择下拉 -->
        <el-select 
          v-model="topic" 
          class="topic-select" 
          placeholder="请选择主题（必填）" 
          clearable
        >
          <el-option 
            v-for="opt in topicOptions" 
            :key="opt.value" 
            :label="opt.label" 
            :value="opt.value" 
          />
        </el-select>

        <!-- 内容文本域 -->
        <el-input 
          v-model="content" 
          class="input-field1" 
          placeholder="内容（必填）" 
          type="textarea" 
          :rows="12" 
          clearable 
        />

        <!-- 仅保留确定按钮 -->
        <div class="button-group">
          <el-button @click="addArticle" class="btn-primary">确定</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 包装器 - 不干扰左侧菜单 */
.article-wrapper {
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
.article-container {
  pointer-events: auto;
  width: calc(100% - 420px);
  max-width: 900px;
  min-width: 500px;
  margin: 80px 20px 60px 380px;
}

/* 卡片：半透明背景 + 毛玻璃 + 淡化边框 */
.article-box {
  position: relative;
  padding: 32px 30px;
  border: 1px solid rgba(210, 190, 170, 0.5);
  border-radius: 28px;
  box-shadow: 0 15px 30px -10px rgba(0, 0, 0, 0.1);
  text-align: center;
  background: rgba(255, 254, 247, 0.9);
  backdrop-filter: blur(4px);
}

/* 顶部装饰条 */
.article-box::before {
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
.article-box p {
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

/* 主题下拉框 */
.topic-select {
  width: 100%;
  margin-bottom: 20px;
}
.topic-select :deep(.el-input__wrapper) {
  padding: 6px 14px;
  font-size: 14px;
  border-radius: 40px;
  background: rgba(255, 255, 255, 0.7);
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

/* 按钮组（仅一个按钮居中） */
.button-group {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 确定按钮 */
.btn-primary {
  border: none;
  padding: 8px 32px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.2s;
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
  .article-container {
    width: calc(100% - 240px);
    margin: 60px 20px 40px 200px;
  }
  .article-box {
    padding: 28px 24px;
  }
}

@media (max-width: 900px) {
  .article-container {
    width: calc(100% - 60px);
    margin: 40px auto;
  }
}

@media (max-width: 768px) {
  .article-container {
    width: calc(100% - 40px);
    margin: 30px auto;
  }
  .article-box {
    padding: 20px 18px;
  }
  .article-box p {
    font-size: 20px;
    margin-bottom: 18px;
  }
  .btn-primary {
    padding: 6px 24px;
    font-size: 12px;
  }
}
</style>