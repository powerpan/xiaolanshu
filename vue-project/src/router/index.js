import {createRouter,createWebHistory} from 'vue-router'

//导入组件
import register from '../Register.vue';
import mainpage from '../mainpage.vue';

const routes = [
    { path:'/', component:register, name:'login'},
    { path:'/mainpage',component:mainpage, name:'mainpage'},
    { path:'/articles/:id',component:mainpage, name:'article-detail'},
    { path:'/notices/:id',component:mainpage, name:'notice-detail'}
]

const router = createRouter({
    history: createWebHistory(),
    routes:routes
  });
  
  export default router;
