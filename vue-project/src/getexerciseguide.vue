<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const jwttoken = localStorage.getItem('jwttoken');
const headers = {
  'Authorization': `${jwttoken}`
};

const actionPattern = ref('水平推');
const equipment = ref('徒手');
const description = ref('');
const image_url = ref('');
const showResult = ref(false);
const hasSearched = ref(false);

const actionPatternOptions = [
  '水平推', '垂直拉', '下肢蹲', '核心', 
  '下肢硬拉', '垂直推', '弯举', '臂屈伸', '水平拉'
];
const equipmentOptions = [
  '徒手', '哑铃', '弹力带', '杠铃', '固定器械', '瑜伽垫'
];

const getUserProfile = async () => {
  try {
    const result = await axios.get('http://localhost:8080/userprofile/getuserprofile', {
      headers: headers
    });
    if (result.data.status === 1 && result.data.data.equipment && equipmentOptions.includes(result.data.data.equipment)) {
      equipment.value = result.data.data.equipment;
    }
  } catch (err) {
    console.error('获取用户信息失败:', err);
  }
};

const getExerciseGuide = async () => {
  if (!actionPattern.value || !equipment.value) {
    alert('请选择动作模式和训练器材');
    return;
  }
  try {
    const params = {
      jwttoken: jwttoken,
      actionPattern: actionPattern.value,
      equipment: equipment.value
    };
    const response = await axios.get('http://localhost:8080/exerciseguide/getexerciseguide', { params });
    if (response.data.status === 1) {
      description.value = response.data.data.description;
      image_url.value = response.data.data.imageurl;
      console.log('后端返回的 image_url:', response.data.data.imageurl);
      showResult.value = true;
      hasSearched.value = true;
    } else {
      showResult.value = false;
      hasSearched.value = true;
      alert('未找到相关动作指导：' + response.data.message);
    }
  } catch (err) {
    console.error('获取动作指导失败:', err);
    showResult.value = false;
    hasSearched.value = true;
    //alert('网络或服务器错误，请稍后重试');
  }
};

onMounted(async () => {
  await getUserProfile();
});
</script>

<template>
  <!-- 根容器：不拦截鼠标事件，让左侧UI可点击 -->
  <div class="exercise-guide-page">
    <!-- 内部容器：恢复事件响应，保持原有UI可交互 -->
    <div class="exercise-guide-inner">
      <div class="filter-card">
        <div class="filter-item">
          <label>动作模式：</label>
          <select v-model="actionPattern">
            <option v-for="item in actionPatternOptions" :key="item" :value="item">{{ item }}</option>
          </select>
        </div>
        <div class="filter-item">
          <label>训练器材：</label>
          <select v-model="equipment">
            <option v-for="item in equipmentOptions" :key="item" :value="item">{{ item }}</option>
          </select>
        </div>
        <button class="search-btn" @click="getExerciseGuide">搜索</button>
      </div>

      <div class="result-card" v-if="showResult">
        <h3>动作指导详情</h3>
        <div class="result-item"><strong>动作模式：</strong> {{ actionPattern }}</div>
        <div class="result-item"><strong>动作描述：</strong><p>{{ description }}</p></div>
        <div class="result-item">
          <strong>动作图示：</strong><br />
          <img v-if="image_url" :src="image_url" alt="动作图解" class="guide-image" />
          <span v-else>暂无图片</span>
        </div>
      </div>
      <div v-else-if="hasSearched && !showResult" class="no-data-card">
        未找到相关动作指导，请尝试其他组合。
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 根容器：仅负责偏移和事件穿透，不拦截左侧UI点击 */
.exercise-guide-page {
  margin-left: 340px;          /* 保持原UI位置不变 */
  padding: 30px 40px;
  background: transparent;
  position: relative;
  z-index: 1;
  min-height: 100vh;
  pointer-events: none;        /* 关键：整个区域（含左侧空白区）不拦截事件，左侧UI可点 */
}

/* 内部容器：恢复正常交互，筛选、按钮等可点 */
.exercise-guide-inner {
  pointer-events: auto;
}

/* 以下样式完全保持原样，无需修改 */
.filter-card {
  background: var(--card-bg, #fffef7);
  border-radius: 24px;
  padding: 24px 28px;
  margin-bottom: 28px;
  box-shadow: var(--shadow-md, 0 12px 24px -12px rgba(0, 0, 0, 0.15));
  border: 1px solid var(--border-light, #e2d8cc);
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: 24px;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.filter-item label {
  font-weight: 600;
  color: var(--text-dark, #5a3e2b);
  font-size: 14px;
}
select {
  padding: 8px 12px;
  border: 1px solid var(--border-light, #e2d8cc);
  border-radius: 16px;
  background-color: white;
  font-size: 14px;
  min-width: 160px;
  cursor: pointer;
  color: var(--text-dark, #5a3e2b);
}
select:focus {
  outline: none;
  border-color: var(--primary-warm, #d9b48b);
  box-shadow: 0 0 0 2px rgba(217, 180, 139, 0.2);
}
.search-btn {
  background-color: var(--primary-warm, #d9b48b);
  color: white;
  border: none;
  border-radius: 40px;
  padding: 8px 28px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  height: 40px;
  align-self: flex-end;
}
.search-btn:hover {
  background-color: var(--primary-warm-dark, #b97f54);
  transform: translateY(-1px);
}
.result-card {
  background: var(--card-bg, #fffef7);
  border-radius: 24px;
  padding: 28px;
  box-shadow: var(--shadow-md, 0 12px 24px -12px rgba(0, 0, 0, 0.15));
  border: 1px solid var(--border-light, #e2d8cc);
}
.result-card h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: var(--text-dark, #5a3e2b);
  border-left: 4px solid var(--primary-warm, #d9b48b);
  padding-left: 16px;
}
.result-item {
  margin-bottom: 20px;
}
.result-item strong {
  color: var(--text-dark, #5a3e2b);
  display: inline-block;
  width: 90px;
}
.result-item p {
  margin: 8px 0 0;
  line-height: 1.6;
  color: var(--text-dark, #5a3e2b);
}
.guide-image {
  max-width: 100%;
  max-height: 320px;
  margin-top: 12px;
  border-radius: 20px;
  border: 1px solid var(--border-light, #e2d8cc);
  object-fit: contain;
  background: #faf7f2;
}
.no-data-card {
  background: var(--card-bg, #fffef7);
  border-radius: 24px;
  padding: 40px;
  text-align: center;
  color: #b97f54;
  border: 1px solid var(--border-light, #e2d8cc);
}
@media (max-width: 800px) {
  .exercise-guide-page {
    margin-left: 20px;
    padding: 20px;
  }
}
</style>