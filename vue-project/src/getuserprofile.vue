<script setup>
    import { ref } from 'vue';
    import axios from 'axios'
    const jwttoken = localStorage.getItem('jwttoken');
    const headers = {
        'Authorization': `${jwttoken}`
    };
    const showtable = ref(true);
    const editusername = ref('');
    const editfitnessGoal = ref('');
    const editweeklyFrequency = ref('');
    const editequipment = ref('');
    const editexLevel = ref('');

    // 使用数组来存储用户档案数据，保持与参考代码一致的结构
    let userprofilearray = ref([]);

    axios.get('http://localhost:8080/userprofile/getuserprofile', {
        headers: headers
    }).then(result => {
        if (result.data.status === 1) {
            userprofilearray.value = [result.data.data];
        }
        else alert(result.data.message);
    }).catch(err => {
        console.log(err);
    })

    const edituserprofile = async (profile) => {
        showtable.value = false;
        editusername.value = profile.username || '';
        editfitnessGoal.value = profile.fitnessGoal || '';
        editweeklyFrequency.value = profile.weeklyFrequency || '';
        editequipment.value = profile.equipment || '';
        editexLevel.value = profile.exLevel || '';
    }

    const edituserprofilego = async () => {
        try {
            const params = new URLSearchParams();
            params.append('username', editusername.value)
            params.append('jwttoken', jwttoken);
            params.append('fitnessGoal', editfitnessGoal.value);
            params.append('weeklyFrequency', editweeklyFrequency.value);
            params.append('equipment', editequipment.value);
            params.append('exLevel', editexLevel.value);
            const response = await axios.put('http://localhost:8080/userprofile/edituserprofile?' + params.toString());
            if (response.data.status === 1) {
                alert(response.data.message);
                // 修改成功后重新获取数据
                await refreshUserProfile();
                showtable.value = true;
            } else {
                alert('操作失败：' + response.data.message);
            }
        } catch (error) {
            console.error(error);
            alert('请求失败，请稍后重试');
        }
    }

    const refreshUserProfile = async () => {
        try {
            const result = await axios.get('http://localhost:8080/userprofile/getuserprofile', {
                headers: headers
            });
            if (result.data.status === 1) {
                userprofilearray.value = [result.data.data];
            } else {
                alert(result.data.message);
            }
        } catch (err) {
            console.log(err);
        }
    }

    const back = async () => {
        showtable.value = true;
    }
</script>

<template>
    <!-- 展示页面 - 显示在右侧区域 -->
    <div v-if="showtable" class="users-container">
        <div v-for="(profile, index) in userprofilearray" :key="index" class="user-card">
            <table cellspacing="0" class="vertical-table">
                <tr>
                    <td class="attr-label">用户名</td>
                    <td class="attr-value">{{ profile.username }}</td>
                </tr>
                <tr>
                    <td class="attr-label">健身目标</td>
                    <td class="attr-value">{{ profile.fitnessGoal }}</td>
                </tr>
                <tr>
                    <td class="attr-label">每周频率</td>
                    <td class="attr-value">{{ profile.weeklyFrequency }} 次/周</td>
                </tr>
                <tr>
                    <td class="attr-label">可用器材</td>
                    <td class="attr-value">{{ profile.equipment }}</td>
                </tr>
                <tr>
                    <td class="attr-label">运动水平</td>
                    <td class="attr-value">{{ profile.exLevel }}</td>
                </tr>
                <tr>
                    <td colspan="2" class="action-cell">
                        <button @click="edituserprofile(profile)" class="btn-grad">编辑</button>
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <!-- 编辑表单 - 使用绝对定位，不影响主页面布局 -->
    <div class="register-wrapper" v-if="!showtable">
        <div class="register-container">
            <div class="register-box">
                <p>编辑健身需求</p>
                
                <!-- 用户名 -->
                <div class="select-field">
                    <div class="select-label">用户名：</div>
                    <el-input v-model="editusername" class="input-field" placeholder="用户名" disabled clearable />
                </div>
                
                <!-- 健身目标 - 下拉选择 -->
                <div class="select-field">
                    <div class="select-label">健身目标：</div>
                    <el-select v-model="editfitnessGoal" class="input-field" placeholder="请选择健身目标" clearable>
                        <el-option value="保持健康" label="保持健康" />
                        <el-option value="增肌" label="增肌" />
                        <el-option value="减脂" label="减脂" />
                        <el-option value="塑形" label="塑形" />
                        <el-option value="提升力量" label="提升力量" />
                    </el-select>
                </div>

                <!-- 每周频率 - 单选按钮组 -->
                <div class="radio-field">
                    <div class="radio-label">每周频率：</div>
                    <div class="radio-group">
                        <label class="radio-option" v-for="freq in [1,2,3,4,5,6]" :key="freq">
                            <input type="radio" :value="freq.toString()" v-model="editweeklyFrequency" />
                            <span>{{ freq }} 次/周</span>
                        </label>
                    </div>
                </div>

                <!-- 可用器材 - 单选按钮组 -->
                <div class="radio-field">
                    <div class="radio-label">可用器材：</div>
                    <div class="radio-group">
                        <label class="radio-option">
                            <input type="radio" value="徒手" v-model="editequipment" />
                            <span>徒手</span>
                        </label>
                        <label class="radio-option">
                            <input type="radio" value="哑铃" v-model="editequipment" />
                            <span>哑铃</span>
                        </label>
                        <label class="radio-option">
                            <input type="radio" value="弹力带" v-model="editequipment" />
                            <span>弹力带</span>
                        </label>
                        <label class="radio-option">
                            <input type="radio" value="杠铃" v-model="editequipment" />
                            <span>杠铃</span>
                        </label>
                        <label class="radio-option">
                            <input type="radio" value="固定器械" v-model="editequipment" />
                            <span>固定器械(健身房)</span>
                        </label>
                        <label class="radio-option">
                            <input type="radio" value="瑜伽垫" v-model="editequipment" />
                            <span>瑜伽垫</span>
                        </label>
                    </div>
                </div>

                <!-- 运动水平 - 单选按钮组 -->
                <div class="radio-field">
                    <div class="radio-label">运动水平：</div>
                    <div class="radio-group">
                        <label class="radio-option">
                            <input type="radio" value="新手" v-model="editexLevel" />
                            <span>新手</span>
                        </label>
                        <label class="radio-option">
                            <input type="radio" value="进阶" v-model="editexLevel" />
                            <span>进阶</span>
                        </label>
                        <label class="radio-option">
                            <input type="radio" value="熟练" v-model="editexLevel" />
                            <span>熟练</span>
                        </label>
                        <label class="radio-option">
                            <input type="radio" value="资深" v-model="editexLevel" />
                            <span>资深</span>
                        </label>
                    </div>
                </div>

                <div class="button-group">
                    <button class="back-btn" @click="back">返回</button>
                    <button class="confirm-btn" @click="edituserprofilego">确认修改</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* 全局禁止横向滚动 */
* {
    overflow-x: hidden;
}

/* 展示页面 - 显示在右侧区域 */
.users-container {
    position: relative;
    max-width: 750px;
    margin: 0 auto;
    padding: 20px;
    transition: all 0.2s;
    overflow-x: hidden;
}

/* 每个用户的卡片：半透明白米色 + 毛玻璃效果 */
.user-card {
    margin-bottom: 35px;
    background: rgba(255, 254, 247, 0.85);
    border-radius: 20px;
    overflow: hidden;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
    transition: transform 0.2s, box-shadow 0.2s;
    border: 1px solid rgba(233, 224, 208, 0.6);
    position: relative;
    backdrop-filter: blur(2px);
}
.user-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 12px 24px -10px rgba(0, 0, 0, 0.15);
}
.user-card::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 6px;
    background: linear-gradient(90deg, rgba(217, 180, 139, 0.8), rgba(185, 127, 84, 0.8), rgba(217, 180, 139, 0.8));
    z-index: 1;
}

/* 纵向表格背景半透明 */
.vertical-table {
    width: 100%;
    border-collapse: collapse;
    background-color: transparent;
}

/* 属性标签单元格：半透明渐变 */
.attr-label {
    width: 200px;
    background: rgba(250, 246, 237, 0.9);
    font-weight: 700;
    color: #5a3e2b;
    padding: 18px 20px;
    border: 1px solid rgba(229, 217, 204, 0.6);
    text-align: right;
    letter-spacing: 0.5px;
    font-size: 15px;
    border-right-width: 2px;
    border-right-color: rgba(212, 196, 176, 0.7);
}

/* 属性值单元格：半透明白色 */
.attr-value {
    padding: 18px 20px;
    border: 1px solid rgba(229, 217, 204, 0.6);
    text-align: left;
    word-break: break-word;
    white-space: normal;
    background-color: rgba(255, 255, 255, 0.7);
    color: #2c3e2f;
    font-size: 15px;
}

/* 操作按钮行 */
.action-cell {
    padding: 20px;
    text-align: center;
    background: rgba(254, 250, 242, 0.8);
    border: 1px solid rgba(229, 217, 204, 0.6);
    border-top: 2px solid rgba(222, 203, 174, 0.7);
}

/* 编辑按钮 - 简单样式 */
.btn-grad {
    background: linear-gradient(95deg, #e6b980 0%, #d98c5f 100%);
    border: none;
    margin: 0 auto;
    padding: 10px 32px;
    text-align: center;
    text-transform: uppercase;
    font-weight: 600;
    letter-spacing: 1px;
    border-radius: 40px;
    color: white;
    cursor: pointer;
    display: inline-block;
    font-size: 14px;
}
.btn-grad:hover {
    background: linear-gradient(95deg, #d98c5f 0%, #c46a3a 100%);
}

/* 编辑表单外层包装器 - 使用绝对定位，不影响主页面布局 */
.register-wrapper {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
    background: rgba(0, 0, 0, 0.3);
}

/* 编辑表单容器 */
.register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 20px;
}

.register-box {
    position: relative;
    padding: 30px 42px;
    border: 1px solid rgba(210, 190, 170, 0.5);
    border-radius: 32px;
    box-shadow: 0 15px 30px -10px rgba(0, 0, 0, 0.15);
    text-align: center;
    width: 100%;
    max-width: 750px;
    background: rgba(255, 254, 247, 0.95);
    backdrop-filter: blur(4px);
    box-sizing: border-box;
}

.register-box::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 7.2px;
    background: linear-gradient(90deg, rgba(217, 180, 139, 0.8), rgba(185, 127, 84, 0.8), rgba(217, 180, 139, 0.8));
    border-radius: 32px 32px 0 0;
}

.register-box p {
    font-size: 28.8px;
    font-weight: 700;
    color: #6b4c34;
    margin-bottom: 24px;
}

/* 选择框标签样式 */
.select-field {
    margin-bottom: 19.2px;
    text-align: left;
}

.select-label {
    font-size: 14px;
    color: #6b4c34;
    margin-bottom: 8px;
    font-weight: 500;
}

.input-field {
    width: 100%;
}

.input-field :deep(.el-input__wrapper) {
    padding: 7.2px 16px;
    font-size: 15px;
    border-radius: 40px;
    background: rgba(255, 255, 255, 0.7);
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.input-field :deep(.el-input__wrapper:hover) {
    box-shadow: 0 0 0 1px rgba(217, 180, 139, 0.6);
}

.input-field :deep(.el-select__wrapper) {
    padding: 7.2px 16px;
    border-radius: 40px;
    background: rgba(255, 255, 255, 0.7);
}

/* 单选按钮组样式 */
.radio-field {
    margin-bottom: 19.2px;
    text-align: left;
}

.radio-label {
    font-size: 14px;
    color: #6b4c34;
    margin-bottom: 9.6px;
    font-weight: 500;
}

.radio-group {
    display: flex;
    flex-wrap: wrap;
    gap: 14.4px 27px;
}

.radio-option {
    display: flex;
    align-items: center;
    cursor: pointer;
    user-select: none;
}

.radio-option input[type="radio"] {
    margin-right: 6px;
    cursor: pointer;
    width: 15px;
    height: 15px;
}

.radio-option span {
    color: #5a3e2b;
    font-size: 15.6px;
}

.radio-option:hover span {
    color: #d98c5f;
}

.button-group {
    display: flex;
    justify-content: center;
    gap: 60px;
    margin-top: 9.6px;
}

/* 返回按钮样式 */
.back-btn {
    border-radius: 60px;
    padding: 9.6px 36px;
    font-size: 16.8px;
    font-weight: 500;
    border: none;
    cursor: pointer;
    background: rgba(237, 227, 214, 0.9);
    color: #6b4c34;
}

.back-btn:hover {
    background: rgba(224, 209, 192, 0.9);
}

/* 确认修改按钮样式 */
.confirm-btn {
    border-radius: 60px;
    padding: 9.6px 36px;
    font-size: 16.8px;
    font-weight: 500;
    border: none;
    cursor: pointer;
    background: linear-gradient(95deg, #e6b980, #d98c5f);
    color: white;
}

.confirm-btn:hover {
    background: linear-gradient(95deg, #d98c5f, #c46a3a);
}

/* 响应式 */
@media (max-width: 1400px) {
    .users-container {
        max-width: 600px;
    }
    .attr-label {
        width: 160px;
        padding: 14px 12px;
    }
    .attr-value {
        padding: 14px 12px;
    }
    .radio-group {
        gap: 12px 20px;
    }
}

@media (max-width: 900px) {
    .users-container {
        max-width: 500px;
        padding: 10px;
    }
    .register-box {
        padding: 20px 25px;
        margin: 20px;
    }
}

@media (max-width: 768px) {
    .register-box {
        padding: 20px;
        margin: 10px;
    }
    
    .button-group {
        gap: 20px;
    }
    
    .back-btn, .confirm-btn {
        padding: 8px 20px;
        font-size: 14px;
    }
}
</style>