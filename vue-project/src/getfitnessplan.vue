<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios'

const jwttoken = localStorage.getItem('jwttoken');
const headers = {
    'Authorization': `${jwttoken}`
};

// 响应式数据
const weeklyFrequency = ref(1);
const currentDay = ref(1);
const splitMode = ref('');
const actionTasks = ref([]);
const loading = ref(false);

// 获取用户每周训练频率
const getUserProfile = async () => {
    try {
        const result = await axios.get('http://localhost:8080/userprofile/getuserprofile', {
            headers: headers
        });
        if (result.data.status === 1) {
            weeklyFrequency.value = result.data.data.weeklyFrequency;
            console.log(weeklyFrequency.value);
            // 获取用户信息后，加载第一天的计划
            await loadDayPlan(1);
        } else {
            alert(result.data.message);
        }
    } catch (err) {
        console.error('获取用户信息失败:', err);
    }
};

// 获取当前天的训练分化模式
const getSplitMode = async (day) => {
    try {
        console.log(day);
        const result = await axios.get('http://localhost:8080/fitnessplan/getsplitmode', {
            params: {
                jwttoken: jwttoken,
                daytime: day
            },
            headers: headers
        });
        if (result.data.status === 1) {
            splitMode.value = result.data.data;
        } else {
            alert(result.data.message);
        }
    } catch (err) {
        console.error('获取训练分化模式失败:', err);
    }
};

// 获取当前天的训练任务
const getActionTask = async (day) => {
    try {
        const result = await axios.get('http://localhost:8080/fitnessplan/getactiontask', {
            params: {
                jwttoken: jwttoken,
                daytime: day
            },
            headers: headers
        });
        if (result.data.status === 1) {
            actionTasks.value = result.data.data;
        } else {
            alert(result.data.message);
        }
    } catch (err) {
        console.error('获取训练任务失败:', err);
    }
};

// 加载某一天的完整计划
const loadDayPlan = async (day) => {
    loading.value = true;
    currentDay.value = day;
    try {
        // 并行请求提高效率
        await Promise.all([
            getSplitMode(day),
            getActionTask(day)
        ]);
    } catch (error) {
        console.error('加载计划失败:', error);
    } finally {
        loading.value = false;
    }
};

// 切换天数
const changeDay = (day) => {
    if (day >= 1 && day <= weeklyFrequency.value) {
        loadDayPlan(day);
    }
};

// 上一页
const prevDay = () => {
    if (currentDay.value > 1) {
        changeDay(currentDay.value - 1);
    }
};

// 下一页
const nextDay = () => {
    if (currentDay.value < weeklyFrequency.value) {
        changeDay(currentDay.value + 1);
    }
};

// 组件挂载时获取用户信息
onMounted(() => {
    getUserProfile();
});
</script>

<template>
    <div class="fitnessplan-right-content">
        <div class="fitnessplan-inner">
            <h2 class="page-title">📋 我的健身计划</h2>
            
            <!-- 训练频率显示 -->
            <div class="frequency-info">
                <span class="label">📅 每周训练频率：</span>
                <span class="value">{{ weeklyFrequency }} 天/周</span>
            </div>

            <!-- 天数选择滑块 -->
            <div class="day-selector" v-if="weeklyFrequency > 0">
                <div class="selector-header">
                    <button 
                        class="nav-btn" 
                        @click="prevDay" 
                        :disabled="currentDay === 1"
                        :class="{ disabled: currentDay === 1 }"
                    >
                        ← 前一天
                    </button>
                    <div class="day-info">
                        <span class="current-day">第 {{ currentDay }} 天</span>
                        <span class="total-days">/ 共 {{ weeklyFrequency }} 天</span>
                    </div>
                    <button 
                        class="nav-btn" 
                        @click="nextDay" 
                        :disabled="currentDay === weeklyFrequency"
                        :class="{ disabled: currentDay === weeklyFrequency }"
                    >
                        后一天 →
                    </button>
                </div>
                
                <!-- 滑块 -->
                <input 
                    type="range" 
                    v-model.number="currentDay" 
                    :min="1" 
                    :max="weeklyFrequency"
                    @input="changeDay(currentDay)"
                    class="day-slider"
                />
                <div class="slider-labels">
                    <span v-for="day in weeklyFrequency" :key="day" class="slider-label">
                        {{ day }}
                    </span>
                </div>
            </div>

            <!-- 加载状态 -->
            <div v-if="loading" class="loading">
                <div class="spinner"></div>
                <p>加载训练计划中...</p>
            </div>

            <!-- 健身计划内容 -->
            <div v-else-if="splitMode && actionTasks.length > 0" class="plan-content">
                <!-- 训练分化模式 -->
                <div class="split-mode-card">
                    <h3 class="section-title">🏋️ 今日训练模式</h3>
                    <div class="split-mode-badge">{{ splitMode }}</div>
                </div>

                <!-- 训练任务列表 -->
                <div class="tasks-section">
                    <h3 class="section-title">💪 训练动作列表</h3>
                    <div class="tasks-list">
                        <div v-for="(task, index) in actionTasks" :key="index" class="task-card">
                            <div class="task-header">
                                <span class="task-number">动作 {{ index + 1 }}</span>
                                <span class="action-pattern">{{ task.actionPattern }}</span>
                            </div>
                            
                            <div class="task-details">
                                <div class="detail-group">
                                    <div class="detail-item">
                                        <span class="detail-label">📊 组数：</span>
                                        <span class="detail-value">
                                            {{ task.minSets }} - {{ task.maxSets }} 组
                                        </span>
                                    </div>
                                    <div class="detail-item">
                                        <span class="detail-label">🔄 每组次数：</span>
                                        <span class="detail-value">
                                            {{ task.minReps }} - {{ task.maxReps }} 次
                                        </span>
                                    </div>
                                    <div class="detail-item">
                                        <span class="detail-label">⏱️ 组间休息：</span>
                                        <span class="detail-value">
                                            {{ task.minRestSeconds }} - {{ task.maxRestSeconds }} 秒
                                        </span>
                                    </div>
                                </div>
                                
                                <div class="description-group">
                                    <span class="detail-label">📝 动作描述：</span>
                                    <p class="description">{{ task.description }}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 无数据状态 -->
            <div v-else-if="!loading && weeklyFrequency > 0" class="no-data">
                <p>暂无训练计划数据，请先完善健身需求</p>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* ========== 健身计划样式 - 显示在左侧菜单右侧 ========== */
.fitnessplan-right-content {
    position: fixed;
    left: 300px; /* 为左侧菜单留出空间，根据实际左侧菜单宽度调整 */
    right: 0;
    top: 0;
    bottom: 0;
    overflow-y: auto;
    padding: 20px 30px;
    background: rgba(245, 240, 231, 0.3);
    box-sizing: border-box;
}

/* 自定义滚动条 */
.fitnessplan-right-content::-webkit-scrollbar {
    width: 8px;
}

.fitnessplan-right-content::-webkit-scrollbar-track {
    background: rgba(217, 180, 139, 0.1);
    border-radius: 10px;
}

.fitnessplan-right-content::-webkit-scrollbar-thumb {
    background: var(--primary-warm, #d9b48b);
    border-radius: 10px;
}

.fitnessplan-right-content::-webkit-scrollbar-thumb:hover {
    background: var(--primary-warm-dark, #b97f54);
}

.fitnessplan-inner {
    max-width: 1100px;
    margin: 0 auto;
    background: rgba(255, 254, 247, 0.95);
    border-radius: 24px;
    padding: 28px 32px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
    backdrop-filter: blur(2px);
    border: 1px solid var(--border-light, #e2d8cc);
}

.page-title {
    color: var(--text-dark, #5a3e2b);
    font-size: 24px;
    font-weight: 600;
    margin: 0 0 20px 0;
    padding-bottom: 15px;
    border-bottom: 2px solid var(--primary-warm, #d9b48b);
    display: inline-block;
}

/* 训练频率显示 */
.frequency-info {
    background: linear-gradient(135deg, rgba(217, 180, 139, 0.15) 0%, rgba(139, 195, 74, 0.1) 100%);
    border-radius: 16px;
    padding: 12px 20px;
    margin-bottom: 24px;
    border: 1px solid var(--border-light, #e2d8cc);
}

.frequency-info .label {
    font-size: 15px;
    color: var(--text-dark, #5a3e2b);
    font-weight: 500;
}

.frequency-info .value {
    font-size: 22px;
    font-weight: bold;
    color: var(--primary-warm-dark, #b97f54);
    margin-left: 10px;
}

/* 天数选择器 */
.day-selector {
    background: var(--card-bg, #fffef7);
    border-radius: 16px;
    padding: 20px;
    margin-bottom: 24px;
    border: 1px solid var(--border-light, #e2d8cc);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
}

.selector-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    gap: 15px;
}

.nav-btn {
    padding: 8px 20px;
    background: var(--primary-warm, #d9b48b);
    color: white;
    border: none;
    border-radius: 40px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.3s ease;
}

.nav-btn:hover:not(.disabled) {
    background: var(--primary-warm-dark, #b97f54);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(185, 127, 84, 0.3);
}

.nav-btn.disabled {
    background: #d4c5b5;
    cursor: not-allowed;
    opacity: 0.6;
}

.day-info {
    font-size: 16px;
    font-weight: 500;
    background: #f5f0e7;
    padding: 6px 16px;
    border-radius: 40px;
}

.current-day {
    color: var(--primary-warm-dark, #b97f54);
    font-size: 18px;
    font-weight: bold;
}

.total-days {
    color: #8b7a6b;
}

.day-slider {
    width: 100%;
    margin: 15px 0;
    -webkit-appearance: none;
    background: var(--border-light, #e2d8cc);
    height: 6px;
    border-radius: 3px;
    outline: none;
}

.day-slider::-webkit-slider-thumb {
    -webkit-appearance: none;
    width: 18px;
    height: 18px;
    background: var(--primary-warm, #d9b48b);
    border-radius: 50%;
    cursor: pointer;
    box-shadow: 0 2px 6px rgba(0,0,0,0.2);
    transition: transform 0.2s;
}

.day-slider::-webkit-slider-thumb:hover {
    transform: scale(1.2);
    background: var(--primary-warm-dark, #b97f54);
}

.slider-labels {
    display: flex;
    justify-content: space-between;
    margin-top: 10px;
    padding: 0 5px;
}

.slider-label {
    font-size: 12px;
    color: #8b7a6b;
    font-weight: 500;
}

/* 加载动画 */
.loading {
    text-align: center;
    padding: 60px 20px;
}

.spinner {
    border: 3px solid rgba(217, 180, 139, 0.2);
    border-top: 3px solid var(--primary-warm, #d9b48b);
    border-radius: 50%;
    width: 40px;
    height: 40px;
    animation: spin 1s linear infinite;
    margin: 0 auto 20px;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

/* 计划内容动画 */
.plan-content {
    animation: fadeIn 0.4s ease;
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(15px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* 训练模式卡片 */
.split-mode-card {
    background: linear-gradient(135deg, rgba(217, 180, 139, 0.2) 0%, rgba(139, 195, 74, 0.15) 100%);
    border-radius: 16px;
    padding: 20px;
    margin-bottom: 24px;
    border: 1px solid var(--border-light, #e2d8cc);
}

.section-title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 12px;
    color: var(--text-dark, #5a3e2b);
}

.split-mode-badge {
    font-size: 22px;
    font-weight: bold;
    text-align: center;
    padding: 12px;
    color: var(--primary-warm-dark, #b97f54);
    background: rgba(255, 255, 255, 0.6);
    border-radius: 12px;
}

/* 训练任务区域 */
.tasks-section {
    background: transparent;
}

.tasks-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.task-card {
    background: var(--card-bg, #fffef7);
    border: 1px solid var(--border-light, #e2d8cc);
    border-radius: 16px;
    overflow: hidden;
    transition: all 0.3s ease;
}

.task-card:hover {
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
    transform: translateY(-2px);
    border-color: var(--primary-warm, #d9b48b);
}

.task-header {
    background: rgba(217, 180, 139, 0.1);
    padding: 14px 20px;
    border-bottom: 1px solid var(--border-light, #e2d8cc);
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 20px;
    flex-wrap: wrap;
}

.task-number {
    font-weight: bold;
    color: var(--primary-warm-dark, #b97f54);
    font-size: 15px;
    background: rgba(217, 180, 139, 0.2);
    padding: 4px 12px;
    border-radius: 20px;
}

.action-pattern {
    font-size: 20px;
    font-weight: 700;
    color: var(--text-dark, #5a3e2b);
    text-align: center;
    letter-spacing: 1px;
}

.task-details {
    padding: 20px;
}

.detail-group {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 15px;
    margin-bottom: 16px;
}

.detail-item {
    display: flex;
    align-items: baseline;
    flex-wrap: wrap;
    gap: 8px;
}

.detail-label {
    font-weight: 600;
    color: #8b7a6b;
    min-width: 80px;
    font-size: 13px;
}

.detail-value {
    color: var(--text-dark, #5a3e2b);
    font-size: 14px;
    font-weight: 500;
}

.description-group {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px dashed var(--border-light, #e2d8cc);
}

.description-group .detail-label {
    display: block;
    margin-bottom: 8px;
}

.description {
    color: #6b5a4b;
    line-height: 1.6;
    margin: 0;
    font-size: 14px;
}

/* 无数据状态 */
.no-data {
    text-align: center;
    padding: 60px 20px;
    color: #8b7a6b;
    font-size: 15px;
    background: rgba(255, 254, 247, 0.8);
    border-radius: 16px;
}

/* 响应式适配 */
@media (max-width: 768px) {
    .fitnessplan-right-content {
        left: 0;
        padding: 10px;
    }
    
    .fitnessplan-inner {
        padding: 16px 20px;
    }
    
    .selector-header {
        flex-direction: column;
    }
    
    .nav-btn {
        width: 100%;
    }
    
    .detail-group {
        grid-template-columns: 1fr;
    }
    
    .task-header {
        flex-direction: column;
        text-align: center;
    }
    
    .split-mode-badge {
        font-size: 18px;
    }
    
    .action-pattern {
        font-size: 18px;
    }
}
</style>