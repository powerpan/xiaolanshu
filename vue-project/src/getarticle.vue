<script setup>
import { ref } from 'vue';
import axios from 'axios';

// 搜索结果数据
const articles = ref([]);
// 搜索条件
const searchTitle = ref('');
const searchTopic = ref(''); // '' 代表不限

// 主题选项（不限 + 指定列表）
const topicOptions = [
  { label: '不限', value: '' },
  { label: '比赛', value: '比赛' },
  { label: '教学', value: '教学' },
  { label: '打卡', value: '打卡' },
  { label: '成果', value: '成果' },
  { label: '日常', value: '日常' },
  { label: '励志', value: '励志' },
  { label: '干货', value: '干货' },
  { label: '解析', value: '解析' },
  { label: '避坑', value: '避坑' }
];

// 获取token
const jwttoken = localStorage.getItem('jwttoken');

// 搜索文章
const searchArticles = () => {
  // 标题不能为空（trim后校验）
  const trimmedTitle = searchTitle.value.trim();
  if (!trimmedTitle) {
    alert('标题不能为空');
    return;
  }

  // 基础参数（两个接口都需要）
  const baseParams = {
    jwttoken: jwttoken,
    title: trimmedTitle
  };

  let url = '';
  let params = { ...baseParams };

  // 根据主题是否为空选择接口
  if (searchTopic.value === '') {
    // 不限主题 -> 调用不带主题的接口
    url = 'http://localhost:8080/article/getarticlewithouttopic';
    // 不需要添加 topic 参数
  } else {
    // 指定主题 -> 调用带主题的接口
    url = 'http://localhost:8080/article/getarticle';
    params.topic = searchTopic.value;
  }

  axios.get(url, {
    headers: {
      'Authorization': `${jwttoken}`
    },
    params: params
  })
  .then(result => {
    if (result.data.status === 1) {
      articles.value = result.data.data;
      if (articles.value.length === 0) {
        //alert('未找到相关文章');
      }
    } else {
      alert(result.data.message);
    }
  })
  .catch(err => {
    console.error(err);
    alert('请求失败，请检查网络或登录状态');
  });
};
</script>

<template>
  <div class="page-wrapper">
    <!-- 搜索表单 -->
    <div class="search-form">
      <div class="form-item">
        <label>标题：</label>
        <input 
          type="text" 
          v-model="searchTitle" 
          placeholder="请输入文章标题（必填）"
          class="title-input"
        />
      </div>
      <div class="form-item">
        <label>主题：</label>
        <select v-model="searchTopic" class="topic-select">
          <option 
            v-for="opt in topicOptions" 
            :key="opt.value" 
            :value="opt.value"
          >
            {{ opt.label }}
          </option>
        </select>
      </div>
      <div class="form-item">
        <button @click="searchArticles" class="search-btn">搜索</button>
      </div>
    </div>

    <!-- 纵向卡片列表 -->
    <div class="cards-container">
      <div v-if="articles.length === 0" class="empty-state">
        暂无数据，请尝试搜索
      </div>
      <div 
        v-for="(article, index) in articles" 
        :key="index" 
        class="article-card"
      >
        <div class="card-title">{{ article.title }}</div>
        <div class="card-meta">
          <span class="author">作者：{{ article.author }}</span>
          <span class="topic">主题：{{ article.topic }}</span>
        </div>
        <div class="card-content">{{ article.content }}</div>
        <div class="card-time">{{ article.articletime }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 整体容器 - 不干扰左侧菜单 */
.page-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow-y: auto;
  pointer-events: none;
  z-index: 10;
}

/* 搜索表单容器（保留原风格但微调位置） */
.search-form {
  pointer-events: auto;
  background-color: rgba(255, 254, 247, 0.85);
  backdrop-filter: blur(2px);
  border-radius: 20px;
  padding: 20px 24px;
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  width: calc(100% - 400px);
  max-width: 1200px;
  min-width: 500px;
  margin: 60px 20px 30px 380px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-item label {
  font-size: 14px;
  font-weight: 500;
  color: #5a3e2b;
}

.title-input,
.topic-select {
  padding: 8px 12px;
  border-radius: 12px;
  border: 1px solid rgba(203, 185, 160, 0.6);
  background-color: white;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}

.title-input {
  width: 240px;
}

.topic-select {
  width: 140px;
  cursor: pointer;
}

.title-input:focus,
.topic-select:focus {
  border-color: #d9b48b;
  box-shadow: 0 0 0 2px rgba(217, 180, 139, 0.2);
}

.search-btn {
  background-color: #d9b48b;
  border: none;
  padding: 8px 20px;
  border-radius: 30px;
  color: white;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-top: 20px;
}

.search-btn:hover {
  background-color: #c49a6c;
}

/* 纵向卡片容器 */
.cards-container {
  pointer-events: auto;
  width: calc(100% - 400px);
  max-width: 1200px;
  min-width: 500px;
  margin: 0 20px 60px 380px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 单个卡片样式 */
.article-card {
  background-color: rgba(255, 254, 247, 0.75);
  backdrop-filter: blur(1px);
  border-radius: 24px;
  padding: 24px 28px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
  transition: all 0.2s ease;
  border: 1px solid rgba(203, 185, 160, 0.2);
}

.article-card:hover {
  background-color: rgba(255, 254, 247, 0.9);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

/* 标题 */
.card-title {
  font-size: 20px;
  font-weight: 700;
  color: #5a3e2b;
  margin-bottom: 12px;
  line-height: 1.3;
  word-break: break-word;
}

/* 作者 + 主题 同行 */
.card-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  margin-bottom: 16px;
  font-size: 14px;
  color: #8b6b4b;
  border-bottom: 1px dashed rgba(203, 185, 160, 0.4);
  padding-bottom: 10px;
}

.author, .topic {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

/* 内容区域 */
.card-content {
  font-size: 15px;
  line-height: 1.6;
  color: #3e2a1f;
  margin-bottom: 20px;
  word-break: break-word;
  white-space: pre-wrap;
  max-height: 300px;
  overflow-y: auto;
  padding-right: 8px;
}

/* 时间 */
.card-time {
  font-size: 12px;
  color: #b49b7b;
  text-align: right;
  border-top: 1px solid rgba(203, 185, 160, 0.3);
  padding-top: 12px;
  margin-top: 4px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background-color: rgba(255, 254, 247, 0.6);
  backdrop-filter: blur(1px);
  border-radius: 24px;
  color: #8b6b4b;
  font-size: 16px;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .search-form,
  .cards-container {
    width: calc(100% - 220px);
    margin-left: 200px;
    margin-right: 20px;
  }
  .search-form {
    margin-top: 40px;
  }
  .cards-container {
    margin-bottom: 40px;
  }
  .article-card {
    padding: 20px 24px;
  }
  .card-title {
    font-size: 18px;
  }
}

@media (max-width: 900px) {
  .search-form,
  .cards-container {
    width: calc(100% - 40px);
    margin-left: auto;
    margin-right: auto;
  }
  .search-form {
    margin-top: 30px;
  }
  .search-form {
    flex-direction: column;
    align-items: stretch;
  }
  .title-input,
  .topic-select,
  .search-btn {
    width: 100%;
  }
  .search-btn {
    margin-top: 0;
  }
}

@media (max-width: 768px) {
  .article-card {
    padding: 16px 20px;
  }
  .card-title {
    font-size: 16px;
  }
  .card-meta {
    gap: 12px;
    font-size: 12px;
  }
  .card-content {
    font-size: 13px;
    max-height: 250px;
  }
}

/* 内容滚动条美观 */
.card-content::-webkit-scrollbar {
  width: 4px;
}
.card-content::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 2px;
}
.card-content::-webkit-scrollbar-thumb {
  background: rgba(217, 180, 139, 0.5);
  border-radius: 2px;
}
</style>