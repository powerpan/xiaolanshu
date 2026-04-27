<script setup>
import { ref } from 'vue'
import { onMounted } from 'vue'
import axios from 'axios' 
import { useRouter } from 'vue-router';
import getmessage from './getmessage.vue';
import editusermessage from './editusermessage.vue';
import notice from './notice.vue'
import getuserprofile from './getuserprofile.vue'
import adduser from './adduser.vue'
import editnotice from './editnotice.vue'
import addnotice from './addnotice.vue'
import getfitnessplan from './getfitnessplan.vue'
import getexerciseguide from './getexerciseguide.vue'
import getarticle from './getarticle.vue'
import addarticle from './addarticle.vue'
import getmyarticle from './getmyarticle.vue'
const router =useRouter()
const  pageshow = ref(0);
const username = ref('');
const identity = ref('');
const userpic =ref('https://th.bing.com/th?id=OIP.QongheeldWhdvvb235Y2xgHaDr&w=349&h=174&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2');
const jwttoken = localStorage.getItem('jwttoken');
console.log(jwttoken);
if(jwttoken=='')
{
  router.push({
      path:'/',
      });
}
const headers = {
    'Authorization': `${jwttoken}` // 正确的 Bearer token 格式
};
axios.get('http://localhost:8080/user/getmessage', {
    headers: headers
})
.then(result => {
        if(result.data.status===1)
        {
          username.value = result.data.data.username;
          identity.value = result.data.data.identity;
          console.log(identity.value);
          console.log(username.value);
          userpic.value ='https://th.bing.com/th?id=OIP.QongheeldWhdvvb235Y2xgHaDr&w=349&h=174&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2';
          if( result.data.data.height=== null ||result.data.data.weight === null || result.data.data.specialty=== null )
          {
            pageshow.value = 1;
            alert("请完善信息");
          }
        }
    else 
    {
      alert(result.data.message);
      router.push({
        path:'/',
        });
    }
    }).catch(err => {
        console.log(err);
    })
const getmessage1  = async () => {pageshow.value = 1;}
const adduser1  = async () => {pageshow.value = 2;}
const editusermessage1  = async () => {pageshow.value = 3;}
const getnotice1  = async () => {pageshow.value = 4;}
const addnotice1 = async () => {pageshow.value = 5;}
const editnotice1  = async () => {pageshow.value = 6;}
const getuserprofile1  = async () => {pageshow.value = 7;}
const getfitnessplan1 = async () =>{pageshow.value = 8;}
const getexerciseguide1 = async () =>{pageshow.value = 9;}
const getarticle1 = async () =>{pageshow.value = 10;}
const addarticle1 = async() =>{pageshow.value = 11;}
const getmyarticle1 = async () =>{pageshow.value = 12;}
  const outlogin=async()=>
  {
    localStorage.setItem('jwttoken', '');
    router.push('/');
  }
</script>

<template>
     <div class="header">
    <div class="profile-box">
      <img :src= "userpic" alt="头像" class="avatar" />
      <div class="user-info">
        <p class="nickname">{{ username }}</p>
      </div>
    </div>
  </div>

  <div class="leftcollapse">
    <el-collapse v-model="activeName" accordion>
      <el-collapse-item title="个人信息" name="1">
        <div>
            <el-button v-on:click="getmessage1" type="default" >
                查看和修改个人信息
            </el-button>
            <el-button v-on:click="adduser1" type="default" v-if="identity == 'ADMIN'" >
                添加用户
            </el-button>
            <el-button v-on:click="editusermessage1" type="default" v-if="identity == 'ADMIN'">
                修改用户信息
            </el-button>
        </div>
      </el-collapse-item>
      <el-collapse-item title="公告" name="2">
        <div>
            <el-button v-on:click="getnotice1" type="default" >
                查看公告
            </el-button>
            <el-button v-on:click="addnotice1" type="default" v-if="identity == 'ADMIN'">
                添加公告
            </el-button>
            <el-button v-on:click="editnotice1" type="default" v-if="identity == 'ADMIN'" >
                编辑公告
            </el-button>
        </div>
      </el-collapse-item>
      <el-collapse-item title="健身计划生成" name="3">
        <div>
            <el-button v-on:click="getuserprofile1" type="default" >
                编辑健身需求
            </el-button>
            <el-button v-on:click="getfitnessplan1" type="default" >
                查看健身计划
            </el-button>
            <el-button v-on:click="getexerciseguide1" type="default" >
                查看动作指导
            </el-button>
        </div>
      </el-collapse-item>
      <el-collapse-item title="相关文章" name="4">
        <div>
            <el-button v-on:click="getarticle1" type="default" >
                查看和搜索文章
            </el-button>
            <el-button v-on:click="addarticle1" type="default" >
                发布文章
            </el-button>
            <el-button v-on:click="getmyarticle1" type="default" >
                编辑文章
            </el-button>
        </div>
      </el-collapse-item>
      <el-collapse-item title="退出登录" name="5">
        <el-button v-on:click="outlogin" type="default" >
                退出登录
            </el-button>
      </el-collapse-item>
    </el-collapse>
  </div>

  <div v-if="pageshow==1">
    <getmessage/>
  </div>
  <div v-if="pageshow==2">
    <adduser/>
  </div>
  <div v-if="pageshow==3">
    <editusermessage/>
  </div>
  <div v-if="pageshow==4">
    <notice/>
  </div>
  <div v-if="pageshow==5">
    <addnotice/>
  </div>
  <div v-if="pageshow==6">
    <editnotice/>
  </div>
  <div v-if="pageshow==7">
    <getuserprofile/>
  </div>
  <div v-if="pageshow==8">
    <getfitnessplan/>
  </div>
  <div v-if="pageshow==9">
    <getexerciseguide/>
  </div>
  <div v-if="pageshow==10">
    <getarticle/>
  </div>
  <div v-if="pageshow==11">
    <addarticle/>
  </div>
  <div v-if="pageshow==12">
    <getmyarticle/>
  </div>
  </template>

<style scoped>
/* ========== 全局风格变量（暖色系） ========== */
:root {
  --bg-gradient-start: #f5f0e7;
  --bg-gradient-end: #e8e0d5;
  --card-bg: #fffef7;
  --primary-warm: #d9b48b;
  --primary-warm-dark: #b97f54;
  --text-dark: #5a3e2b;
  --border-light: #e2d8cc;
  --shadow-sm: 0 4px 8px rgba(0, 0, 0, 0.05);
  --shadow-md: 0 12px 24px -12px rgba(0, 0, 0, 0.15);
}


/* ========== 头部头像区域（保留原有绝对定位） ========== */
.header {
  position: absolute;
  top: 12%;
  left: 9%;
  transform: translate(-50%, -50%);
  display: flex;
  justify-content: flex-start;
  align-items: center;
  z-index: 1;
}

/* 头像卡片：暖色风格 + 渐变边框 */
.profile-box {
  display: flex;
  align-items: center;
  padding: 20px;
  width: 200px;
  height: 100px;
  border: 3px solid transparent;
  border-image: linear-gradient(45deg, var(--primary-warm), #8bc34a) 1;
  border-radius: 20px;
  background: var(--card-bg);
  box-shadow: var(--shadow-md);
  backdrop-filter: blur(2px);
}

.avatar {
  width: 75px;
  height: 75px;
  border-radius: 50%;
  margin-right: 15px;
  border: 2px solid var(--primary-warm);
  object-fit: cover;
}

.user-info {
  font-size: 18px;
  color: var(--text-dark);
}

.nickname {
  margin: 0;
  font-weight: 600;
  color: var(--text-dark);
}

/* ========== 左侧折叠面板（保留原有定位） ========== */
.leftcollapse {
  position: absolute;
  top: 50%;
  left: 0%;
  transform: translateY(-50%);
  width: 280px;
  background: var(--card-bg);
  padding: 20px;
  box-shadow: var(--shadow-md);
  border-radius: 24px;
  max-height: 80vh;
  overflow-y: auto;
  border: 1px solid var(--border-light);
}

/* 自定义折叠面板样式（覆盖 Element Plus 默认样式） */
.leftcollapse :deep(.el-collapse) {
  border: none;
  background: transparent;
}
.leftcollapse :deep(.el-collapse-item__header) {
  background: transparent;
  border-bottom: 1px solid var(--border-light);
  font-weight: 600;
  color: var(--text-dark);
  font-size: 16px;
  padding: 12px 0;
}
.leftcollapse :deep(.el-collapse-item__wrap) {
  background: transparent;
  border-bottom: none;
}
.leftcollapse :deep(.el-collapse-item__content) {
  padding: 12px 0 8px 0;
  background: transparent;
}

/* 折叠面板内按钮容器 */
.leftcollapse .el-collapse-item__content div {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 统一按钮样式（与之前优化风格一致） */
.el-button {
  font-size: 14px;
  font-weight: 600;
  padding: 8px 16px;
  border-radius: 40px;
  transition: all 0.2s;
  background: #ede3d6;
  border: none;
  color: var(--text-dark);
  cursor: pointer;
  width: 100%;
  margin: 0;
  box-shadow: var(--shadow-sm);
}
.el-button:hover {
  background: #e0d1c0;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
/* 针对“退出登录”按钮可以轻微区分（可选） */
.el-button:active {
  transform: translateY(0);
}

/* 保持原有折叠面板的标题图标颜色 */
.leftcollapse :deep(.el-collapse-item__arrow) {
  color: var(--primary-warm-dark);
}

/* 滚动条美化 */
.leftcollapse::-webkit-scrollbar {
  width: 6px;
}
.leftcollapse::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}
.leftcollapse::-webkit-scrollbar-thumb {
  background: var(--primary-warm);
  border-radius: 10px;
}

/* 右侧内容区域容器（由子组件自行控制，此处只保证背景透明） */
/* 若需要统一右侧内容位置，可在全局设置，但此处保留原有逻辑 */
</style>
  