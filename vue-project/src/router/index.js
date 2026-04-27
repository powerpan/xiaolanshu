import {createRouter,createWebHistory} from 'vue-router'

//导入组件
import register from '../Register.vue';
import mainpage from '../mainpage.vue';

const routes = [
    { path:'/', component:register},
    { path:'/mainpage',component:mainpage}
]

const router = createRouter({
    history: createWebHistory(),
    routes:routes
  });
  
  export default router;