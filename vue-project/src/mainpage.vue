<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api, { getToken, unwrap, withToken } from './services/api'

const router = useRouter()
const loading = ref(false)
const activeView = ref('overview')

const user = ref(null)
const profile = ref(null)
const notices = ref([])
const adminUsers = ref([])
const articles = ref([])
const myArticles = ref([])
const actionTasks = ref([])
const splitMode = ref('')
const selectedDay = ref(1)
const guideResult = ref(null)
const todayCheckin = ref(null)
const checkinStats = ref(null)
const nutrition = ref(null)

const isAdmin = computed(() => user.value?.identity === 'ADMIN')
const displayName = computed(() => user.value?.nickname || user.value?.username || '用户')

const profileForm = reactive({
  username: '',
  nickname: '',
  password: '',
  specialty: '',
  height: '',
  weight: '',
})

const fitnessForm = reactive({
  username: '',
  fitnessGoal: '保持健康',
  weeklyFrequency: 3,
  equipment: '徒手',
  exLevel: '新手',
})

const adminAddForm = reactive({
  username: '',
  password: '',
  identity: 'user',
})

const adminEditVisible = ref(false)
const adminEditForm = reactive({
  username: '',
  nickname: '',
  password: '',
  identity: 'user',
  specialty: '',
  userpic: '',
  height: '',
  weight: '',
})

const noticeForm = reactive({
  title: '',
  content: '',
})
const noticeEditing = ref(null)

const articleSearch = reactive({
  title: '',
  topic: '',
})
const articleForm = reactive({
  title: '',
  topic: '',
  content: '',
})
const articleEditVisible = ref(false)
const articleEditForm = reactive({
  lasttitle: '',
  lastcontent: '',
  title: '',
  content: '',
})

const guideForm = reactive({
  actionPattern: '水平推',
  equipment: '徒手',
})

const checkinForm = reactive({
  durationMinutes: 35,
  mood: '状态不错',
  note: '',
})

const fitnessGoals = ['保持健康', '增肌', '减脂', '塑形', '提升力量']
const equipments = ['徒手', '哑铃', '弹力带', '杠铃', '固定器械', '瑜伽垫']
const levels = ['新手', '进阶', '熟练', '资深']
const actionPatterns = ['水平推', '垂直拉', '下肢蹲', '核心', '下肢硬拉', '垂直推', '弯举', '臂屈伸', '水平拉']
const moods = ['状态不错', '轻松', '有挑战', '需要恢复']

const navGroups = computed(() => [
  {
    title: '工作台',
    items: [
      { key: 'overview', label: '总览' },
      { key: 'checkin', label: '训练打卡' },
      { key: 'nutrition', label: '饮食建议' },
    ],
  },
  {
    title: '训练',
    items: [
      { key: 'profile', label: '个人资料' },
      { key: 'fitness', label: '健身需求' },
      { key: 'plan', label: '训练计划' },
      { key: 'guide', label: '动作指导' },
    ],
  },
  {
    title: '内容',
    items: [
      { key: 'notices', label: '公告' },
      { key: 'articles', label: '文章' },
    ],
  },
  {
    title: '管理',
    items: isAdmin.value ? [{ key: 'admin', label: '用户管理' }] : [],
  },
].filter((group) => group.items.length))

const weeklyDays = computed(() => {
  const count = Number(profile.value?.weeklyFrequency || fitnessForm.weeklyFrequency || 0)
  return Array.from({ length: count }, (_, index) => index + 1)
})

const overviewCards = computed(() => [
  { label: '本月打卡', value: checkinStats.value?.monthDays ?? 0, unit: '天' },
  { label: '连续打卡', value: checkinStats.value?.currentStreak ?? 0, unit: '天' },
  { label: '训练时长', value: checkinStats.value?.totalMinutes ?? 0, unit: '分钟' },
  { label: '计划频率', value: profile.value?.weeklyFrequency ?? 0, unit: '次/周' },
])

const ensureToken = () => {
  if (!getToken()) {
    router.replace('/')
    return false
  }
  return true
}

const formatDate = (value) => {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 16)
}

const setProfileForm = () => {
  if (!user.value) return
  Object.assign(profileForm, {
    username: user.value.username || '',
    nickname: user.value.nickname || '',
    password: user.value.password || '',
    specialty: user.value.specialty || '',
    height: user.value.height ?? '',
    weight: user.value.weight ?? '',
  })
}

const setFitnessForm = () => {
  if (!profile.value) return
  Object.assign(fitnessForm, {
    username: profile.value.username || user.value?.username || '',
    fitnessGoal: profile.value.fitnessGoal || '保持健康',
    weeklyFrequency: profile.value.weeklyFrequency || 3,
    equipment: profile.value.equipment || '徒手',
    exLevel: profile.value.exLevel || '新手',
  })
}

const loadUser = async () => {
  user.value = await unwrap(api.get('/user/getmessage'))
  setProfileForm()
}

const loadProfile = async () => {
  profile.value = await unwrap(api.get('/userprofile/getuserprofile'))
  setFitnessForm()
}

const loadNotices = async () => {
  notices.value = await unwrap(api.get('/notice/getnotice'))
}

const loadUsers = async () => {
  if (!isAdmin.value) return
  adminUsers.value = await unwrap(api.get('/user/getallmessage'))
}

const loadArticles = async () => {
  articles.value = await unwrap(api.get('/article/getarticle', {
    params: withToken({
      title: articleSearch.title,
      topic: articleSearch.topic,
    }),
  }))
}

const loadMyArticles = async () => {
  myArticles.value = await unwrap(api.get('/article/getmyarticle', {
    params: withToken(),
  }))
}

const loadPlan = async (day = selectedDay.value) => {
  if (!profile.value) return
  selectedDay.value = day
  const params = withToken({ daytime: day })
  const [mode, tasks] = await Promise.all([
    unwrap(api.get('/fitnessplan/getsplitmode', { params })),
    unwrap(api.get('/fitnessplan/getactiontask', { params })),
  ])
  splitMode.value = mode
  actionTasks.value = tasks || []
}

const loadGuide = async () => {
  guideResult.value = await unwrap(api.get('/exerciseguide/getexerciseguide', {
    params: withToken({
      actionPattern: guideForm.actionPattern,
      equipment: guideForm.equipment,
    }),
  }))
}

const loadCheckin = async () => {
  const [today, stats] = await Promise.all([
    unwrap(api.get('/checkin/today')),
    unwrap(api.get('/checkin/stats')),
  ])
  todayCheckin.value = today
  checkinStats.value = stats
  if (today) {
    Object.assign(checkinForm, {
      durationMinutes: today.durationMinutes || 0,
      mood: today.mood || '状态不错',
      note: today.note || '',
    })
  }
}

const loadNutrition = async () => {
  nutrition.value = await unwrap(api.get('/nutrition/recommendation'))
}

const bootstrap = async () => {
  if (!ensureToken()) return
  loading.value = true
  try {
    await loadUser()
    await loadProfile()
    await Promise.allSettled([
      loadNotices(),
      loadArticles(),
      loadMyArticles(),
      loadCheckin(),
      loadNutrition(),
      loadUsers(),
    ])
    await loadPlan(1)
  } catch (error) {
    ElMessage.error(error.message || '加载失败')
    if (error.message === '未登录') {
      router.replace('/')
    }
  } finally {
    loading.value = false
  }
}

const openView = async (key) => {
  activeView.value = key
  try {
    if (key === 'admin') await loadUsers()
    if (key === 'notices') await loadNotices()
    if (key === 'articles') {
      await loadArticles()
      await loadMyArticles()
    }
    if (key === 'plan') await loadPlan(selectedDay.value || 1)
    if (key === 'checkin') await loadCheckin()
    if (key === 'nutrition') await loadNutrition()
  } catch (error) {
    ElMessage.error(error.message || '加载失败')
  }
}

const saveProfile = async () => {
  try {
    await unwrap(api.put('/user/editmessage', null, {
      params: withToken({
        username: profileForm.username,
        nickname: profileForm.nickname,
        password: profileForm.password,
        userpic: user.value?.userpic || '',
        identity: user.value?.identity || 'user',
        specialty: profileForm.specialty,
        height: profileForm.height,
        weight: profileForm.weight,
      }),
    }))
    ElMessage.success('个人资料已更新')
    await loadUser()
    await loadNutrition()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const saveFitnessProfile = async () => {
  try {
    await unwrap(api.put('/userprofile/edituserprofile', null, {
      params: withToken({
        username: fitnessForm.username,
        fitnessGoal: fitnessForm.fitnessGoal,
        weeklyFrequency: fitnessForm.weeklyFrequency,
        equipment: fitnessForm.equipment,
        exLevel: fitnessForm.exLevel,
      }),
    }))
    ElMessage.success('健身需求已更新')
    await loadProfile()
    await Promise.allSettled([loadPlan(1), loadNutrition()])
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const addAdminUser = async () => {
  try {
    await unwrap(api.post('/user/adduser', null, {
      params: withToken(adminAddForm),
    }))
    ElMessage.success('用户已添加')
    Object.assign(adminAddForm, { username: '', password: '', identity: 'user' })
    await loadUsers()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const openAdminEdit = (row) => {
  Object.assign(adminEditForm, {
    username: row.username,
    nickname: row.nickname || '',
    password: row.password || '',
    identity: row.identity || 'user',
    specialty: row.specialty || '',
    userpic: row.userpic || '',
    height: row.height ?? '',
    weight: row.weight ?? '',
  })
  adminEditVisible.value = true
}

const saveAdminEdit = async () => {
  try {
    await unwrap(api.put('/user/editallmessage', null, {
      params: withToken(adminEditForm),
    }))
    ElMessage.success('用户信息已更新')
    adminEditVisible.value = false
    await loadUsers()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const toggleRegister = async (row) => {
  try {
    await unwrap(api.put('/user/editregister', null, {
      params: withToken({
        username: row.username,
        registered: !row.registered,
      }),
    }))
    ElMessage.success('审核状态已更新')
    await loadUsers()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const deleteUser = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除用户 ${row.username}？`, '删除确认', { type: 'warning' })
    await unwrap(api.put('/user/deleteuser', null, {
      params: withToken({ username: row.username }),
    }))
    ElMessage.success('用户已删除')
    await loadUsers()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '操作取消')
  }
}

const resetNoticeForm = () => {
  noticeEditing.value = null
  Object.assign(noticeForm, { title: '', content: '' })
}

const editNotice = (row) => {
  noticeEditing.value = { title: row.title, content: row.content }
  Object.assign(noticeForm, { title: row.title, content: row.content })
}

const saveNotice = async () => {
  try {
    if (noticeEditing.value) {
      await unwrap(api.put('/notice/editnotice', null, {
        params: withToken({
          lasttitle: noticeEditing.value.title,
          lastcontent: noticeEditing.value.content,
          title: noticeForm.title,
          content: noticeForm.content,
        }),
      }))
      ElMessage.success('公告已更新')
    } else {
      await unwrap(api.put('/notice/addnotice', null, {
        params: withToken(noticeForm),
      }))
      ElMessage.success('公告已发布')
    }
    resetNoticeForm()
    await loadNotices()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const deleteNotice = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除这条公告？', '删除确认', { type: 'warning' })
    await unwrap(api.put('/notice/deletenotice', null, {
      params: withToken({ title: row.title, content: row.content }),
    }))
    ElMessage.success('公告已删除')
    await loadNotices()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '操作取消')
  }
}

const saveNoticeDraft = async () => {
  try {
    await unwrap(api.put('/notice/savetempnotice', null, {
      params: withToken(noticeForm),
    }))
    ElMessage.success('草稿已保存')
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const loadNoticeDraft = async () => {
  try {
    const drafts = await unwrap(api.get('/notice/gettempnotice'))
    if (drafts?.length) {
      Object.assign(noticeForm, {
        title: drafts[0].title,
        content: drafts[0].content,
      })
      ElMessage.success('已载入草稿')
    } else {
      ElMessage.info('暂无草稿')
    }
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const clearNoticeDraft = async () => {
  try {
    await unwrap(api.put('/notice/deletetempnotice', null, {
      params: withToken(),
    }))
    ElMessage.success('草稿已清空')
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const addArticle = async () => {
  try {
    await unwrap(api.put('/article/addarticle', null, {
      params: withToken(articleForm),
    }))
    ElMessage.success('文章已发布')
    Object.assign(articleForm, { title: '', topic: '', content: '' })
    await Promise.all([loadArticles(), loadMyArticles()])
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const openArticleEdit = (row) => {
  Object.assign(articleEditForm, {
    lasttitle: row.title,
    lastcontent: row.content,
    title: row.title,
    content: row.content,
  })
  articleEditVisible.value = true
}

const saveArticleEdit = async () => {
  try {
    await unwrap(api.put('/article/editarticle', null, {
      params: withToken(articleEditForm),
    }))
    ElMessage.success('文章已更新')
    articleEditVisible.value = false
    await Promise.all([loadArticles(), loadMyArticles()])
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const deleteArticle = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除这篇文章？', '删除确认', { type: 'warning' })
    await unwrap(api.put('/article/deletearticle', null, {
      params: withToken({ title: row.title, content: row.content }),
    }))
    ElMessage.success('文章已删除')
    await Promise.all([loadArticles(), loadMyArticles()])
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '操作取消')
  }
}

const saveCheckin = async () => {
  try {
    await unwrap(api.put('/checkin/today', null, {
      params: checkinForm,
    }))
    ElMessage.success('今日打卡已保存')
    await loadCheckin()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const logout = () => {
  localStorage.removeItem('jwttoken')
  router.replace('/')
}

onMounted(bootstrap)
</script>

<template>
  <div class="workspace-shell" v-loading="loading">
    <aside class="sidebar">
      <div class="brand">
        <span class="brand-logo">XL</span>
        <div>
          <strong>小蓝书</strong>
          <small>Fitness Desk</small>
        </div>
      </div>

      <nav class="nav-list">
        <section v-for="group in navGroups" :key="group.title">
          <p>{{ group.title }}</p>
          <button
            v-for="item in group.items"
            :key="item.key"
            :class="{ active: activeView === item.key }"
            @click="openView(item.key)"
          >
            {{ item.label }}
          </button>
        </section>
      </nav>
    </aside>

    <main class="main">
      <header class="topbar">
        <div>
          <p>当前账号</p>
          <h1>{{ displayName }}</h1>
        </div>
        <div class="topbar-actions">
          <span class="role-badge">{{ isAdmin ? '管理员' : '普通用户' }}</span>
          <el-button @click="logout">退出登录</el-button>
        </div>
      </header>

      <section v-if="activeView === 'overview'" class="content-stack">
        <div class="hero-band">
          <div>
            <p>今日训练</p>
            <h2>{{ splitMode || '还没有训练计划' }}</h2>
            <span>{{ profile?.fitnessGoal || '保持健康' }} · 每周 {{ profile?.weeklyFrequency || 0 }} 次</span>
          </div>
          <el-button type="primary" @click="openView('plan')">查看计划</el-button>
        </div>

        <div class="metric-grid">
          <article v-for="card in overviewCards" :key="card.label" class="metric-card">
            <span>{{ card.label }}</span>
            <strong>{{ card.value }}</strong>
            <small>{{ card.unit }}</small>
          </article>
        </div>

        <div class="two-column">
          <section class="panel">
            <div class="panel-heading">
              <div>
                <p>近期打卡</p>
                <h2>训练习惯</h2>
              </div>
              <el-button text @click="openView('checkin')">去打卡</el-button>
            </div>
            <div class="timeline">
              <div v-for="item in checkinStats?.recentCheckins || []" :key="item.id" class="timeline-row">
                <span>{{ item.checkinDate }}</span>
                <strong>{{ item.durationMinutes }} 分钟</strong>
                <small>{{ item.mood || '-' }}</small>
              </div>
              <el-empty v-if="!checkinStats?.recentCheckins?.length" description="暂无打卡" />
            </div>
          </section>

          <section class="panel">
            <div class="panel-heading">
              <div>
                <p>饮食建议</p>
                <h2>{{ nutrition?.targetCalories || 0 }} kcal</h2>
              </div>
              <el-button text @click="openView('nutrition')">查看详情</el-button>
            </div>
            <p class="muted">{{ nutrition?.summary || '完善资料后获取饮食建议。' }}</p>
            <div class="nutrition-strip">
              <span>蛋白 {{ nutrition?.proteinGrams || 0 }}g</span>
              <span>饮水 {{ nutrition?.waterMl || 0 }}ml</span>
            </div>
          </section>
        </div>
      </section>

      <section v-if="activeView === 'profile'" class="panel">
        <div class="panel-heading">
          <div>
            <p>基础资料</p>
            <h2>个人信息</h2>
          </div>
          <el-button type="primary" @click="saveProfile">保存资料</el-button>
        </div>
        <div class="form-grid">
          <label><span>用户名</span><el-input v-model="profileForm.username" disabled /></label>
          <label><span>昵称</span><el-input v-model="profileForm.nickname" /></label>
          <label><span>密码</span><el-input v-model="profileForm.password" show-password /></label>
          <label><span>擅长方向</span><el-input v-model="profileForm.specialty" /></label>
          <label><span>身高 cm</span><el-input v-model="profileForm.height" type="number" /></label>
          <label><span>体重 kg</span><el-input v-model="profileForm.weight" type="number" /></label>
        </div>
      </section>

      <section v-if="activeView === 'fitness'" class="panel">
        <div class="panel-heading">
          <div>
            <p>计划参数</p>
            <h2>健身需求</h2>
          </div>
          <el-button type="primary" @click="saveFitnessProfile">生成计划</el-button>
        </div>
        <div class="form-grid">
          <label><span>健身目标</span><el-select v-model="fitnessForm.fitnessGoal"><el-option v-for="item in fitnessGoals" :key="item" :label="item" :value="item" /></el-select></label>
          <label><span>每周频率</span><el-input-number v-model="fitnessForm.weeklyFrequency" :min="1" :max="6" /></label>
          <label><span>可用器材</span><el-select v-model="fitnessForm.equipment"><el-option v-for="item in equipments" :key="item" :label="item" :value="item" /></el-select></label>
          <label><span>训练水平</span><el-select v-model="fitnessForm.exLevel"><el-option v-for="item in levels" :key="item" :label="item" :value="item" /></el-select></label>
        </div>
      </section>

      <section v-if="activeView === 'plan'" class="content-stack">
        <div class="panel">
          <div class="panel-heading">
            <div>
              <p>训练计划</p>
              <h2>第 {{ selectedDay }} 天 · {{ splitMode }}</h2>
            </div>
            <div class="segmented">
              <button v-for="day in weeklyDays" :key="day" :class="{ active: selectedDay === day }" @click="loadPlan(day)">
                {{ day }}
              </button>
            </div>
          </div>
          <el-table :data="actionTasks" stripe>
            <el-table-column prop="actionPattern" label="动作模式" min-width="130" />
            <el-table-column label="组数" min-width="120">
              <template #default="{ row }">{{ row.minSets }} - {{ row.maxSets }} 组</template>
            </el-table-column>
            <el-table-column label="次数" min-width="120">
              <template #default="{ row }">{{ row.minReps }} - {{ row.maxReps }} 次</template>
            </el-table-column>
            <el-table-column label="休息" min-width="120">
              <template #default="{ row }">{{ row.minRestSeconds }} - {{ row.maxRestSeconds }} 秒</template>
            </el-table-column>
            <el-table-column prop="description" label="说明" min-width="280" />
          </el-table>
        </div>
      </section>

      <section v-if="activeView === 'guide'" class="panel">
        <div class="panel-heading">
          <div>
            <p>动作库</p>
            <h2>动作指导</h2>
          </div>
          <el-button type="primary" @click="loadGuide">搜索动作</el-button>
        </div>
        <div class="form-grid compact">
          <label><span>动作模式</span><el-select v-model="guideForm.actionPattern"><el-option v-for="item in actionPatterns" :key="item" :label="item" :value="item" /></el-select></label>
          <label><span>器材</span><el-select v-model="guideForm.equipment"><el-option v-for="item in equipments" :key="item" :label="item" :value="item" /></el-select></label>
        </div>
        <div v-if="guideResult" class="result-block">
          <h3>{{ guideResult.actionPattern }} · {{ guideResult.equipment }}</h3>
          <p>{{ guideResult.description || '暂无说明' }}</p>
          <img v-if="guideResult.imageurl" :src="guideResult.imageurl" alt="动作图示" />
        </div>
      </section>

      <section v-if="activeView === 'checkin'" class="content-stack">
        <div class="panel">
          <div class="panel-heading">
            <div>
              <p>每日记录</p>
              <h2>训练打卡</h2>
            </div>
            <el-button type="primary" @click="saveCheckin">保存今日打卡</el-button>
          </div>
          <div class="form-grid">
            <label><span>训练时长</span><el-input-number v-model="checkinForm.durationMinutes" :min="0" :max="300" /></label>
            <label><span>状态</span><el-select v-model="checkinForm.mood"><el-option v-for="item in moods" :key="item" :label="item" :value="item" /></el-select></label>
            <label class="wide"><span>备注</span><el-input v-model="checkinForm.note" type="textarea" :rows="3" /></label>
          </div>
        </div>
        <div class="panel">
          <div class="panel-heading">
            <div>
              <p>历史记录</p>
              <h2>统计</h2>
            </div>
          </div>
          <div class="metric-grid small">
            <article class="metric-card"><span>累计天数</span><strong>{{ checkinStats?.totalDays || 0 }}</strong><small>天</small></article>
            <article class="metric-card"><span>连续打卡</span><strong>{{ checkinStats?.currentStreak || 0 }}</strong><small>天</small></article>
            <article class="metric-card"><span>累计时长</span><strong>{{ checkinStats?.totalMinutes || 0 }}</strong><small>分钟</small></article>
          </div>
          <el-table :data="checkinStats?.recentCheckins || []" stripe>
            <el-table-column prop="checkinDate" label="日期" width="140" />
            <el-table-column prop="durationMinutes" label="分钟" width="100" />
            <el-table-column prop="mood" label="状态" width="130" />
            <el-table-column prop="note" label="备注" />
          </el-table>
        </div>
      </section>

      <section v-if="activeView === 'nutrition'" class="panel">
        <div class="panel-heading">
          <div>
            <p>营养策略</p>
            <h2>饮食建议</h2>
          </div>
          <el-button @click="loadNutrition">刷新建议</el-button>
        </div>
        <div class="metric-grid small">
          <article class="metric-card"><span>目标热量</span><strong>{{ nutrition?.targetCalories || 0 }}</strong><small>kcal</small></article>
          <article class="metric-card"><span>蛋白质</span><strong>{{ nutrition?.proteinGrams || 0 }}</strong><small>g</small></article>
          <article class="metric-card"><span>饮水</span><strong>{{ nutrition?.waterMl || 0 }}</strong><small>ml</small></article>
        </div>
        <p class="muted block-copy">{{ nutrition?.summary }}</p>
        <div class="two-column">
          <div>
            <h3>一日结构</h3>
            <ul class="clean-list"><li v-for="item in nutrition?.meals || []" :key="item">{{ item }}</li></ul>
          </div>
          <div>
            <h3>执行提示</h3>
            <ul class="clean-list"><li v-for="item in nutrition?.tips || []" :key="item">{{ item }}</li></ul>
          </div>
        </div>
      </section>

      <section v-if="activeView === 'notices'" class="content-stack">
        <div class="panel" v-if="isAdmin">
          <div class="panel-heading">
            <div>
              <p>{{ noticeEditing ? '编辑公告' : '发布公告' }}</p>
              <h2>公告管理</h2>
            </div>
            <div class="button-row">
              <el-button @click="loadNoticeDraft">载入草稿</el-button>
              <el-button @click="saveNoticeDraft">保存草稿</el-button>
              <el-button @click="clearNoticeDraft">清空草稿</el-button>
              <el-button type="primary" @click="saveNotice">{{ noticeEditing ? '保存修改' : '发布' }}</el-button>
            </div>
          </div>
          <div class="form-grid">
            <label><span>标题</span><el-input v-model="noticeForm.title" /></label>
            <label class="wide"><span>内容</span><el-input v-model="noticeForm.content" type="textarea" :rows="4" /></label>
          </div>
        </div>
        <div class="panel">
          <div class="panel-heading"><div><p>消息中心</p><h2>公告列表</h2></div><el-button @click="loadNotices">刷新</el-button></div>
          <el-table :data="notices" stripe>
            <el-table-column prop="title" label="标题" min-width="180" />
            <el-table-column prop="content" label="内容" min-width="320" />
            <el-table-column prop="author" label="作者" width="120" />
            <el-table-column label="时间" width="170"><template #default="{ row }">{{ formatDate(row.noticetime) }}</template></el-table-column>
            <el-table-column v-if="isAdmin" label="操作" width="160" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="editNotice(row)">编辑</el-button>
                <el-button link type="danger" @click="deleteNotice(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </section>

      <section v-if="activeView === 'articles'" class="content-stack">
        <div class="panel">
          <div class="panel-heading"><div><p>知识库</p><h2>文章搜索</h2></div><el-button @click="loadArticles">搜索</el-button></div>
          <div class="form-grid compact">
            <label><span>标题</span><el-input v-model="articleSearch.title" clearable /></label>
            <label><span>主题</span><el-input v-model="articleSearch.topic" clearable /></label>
          </div>
          <div class="article-grid">
            <article v-for="item in articles" :key="`${item.title}-${item.articletime}`" class="article-card">
              <span>{{ item.topic || '未分类' }}</span>
              <h3>{{ item.title }}</h3>
              <p>{{ item.content }}</p>
              <small>{{ item.author }} · {{ formatDate(item.articletime) }}</small>
            </article>
          </div>
        </div>
        <div class="panel">
          <div class="panel-heading"><div><p>创作</p><h2>发布文章</h2></div><el-button type="primary" @click="addArticle">发布</el-button></div>
          <div class="form-grid">
            <label><span>标题</span><el-input v-model="articleForm.title" /></label>
            <label><span>主题</span><el-input v-model="articleForm.topic" /></label>
            <label class="wide"><span>内容</span><el-input v-model="articleForm.content" type="textarea" :rows="4" /></label>
          </div>
        </div>
        <div class="panel">
          <div class="panel-heading"><div><p>我的内容</p><h2>文章管理</h2></div><el-button @click="loadMyArticles">刷新</el-button></div>
          <el-table :data="myArticles" stripe>
            <el-table-column prop="title" label="标题" min-width="180" />
            <el-table-column prop="topic" label="主题" width="140" />
            <el-table-column prop="content" label="内容" min-width="320" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="openArticleEdit(row)">编辑</el-button>
                <el-button link type="danger" @click="deleteArticle(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </section>

      <section v-if="activeView === 'admin'" class="content-stack">
        <div class="panel">
          <div class="panel-heading"><div><p>管理员</p><h2>添加用户</h2></div><el-button type="primary" @click="addAdminUser">添加</el-button></div>
          <div class="form-grid compact">
            <label><span>用户名</span><el-input v-model="adminAddForm.username" /></label>
            <label><span>密码</span><el-input v-model="adminAddForm.password" show-password /></label>
            <label><span>身份</span><el-select v-model="adminAddForm.identity"><el-option label="普通用户" value="user" /><el-option label="管理员" value="ADMIN" /></el-select></label>
          </div>
        </div>
        <div class="panel">
          <div class="panel-heading"><div><p>账号审核</p><h2>用户列表</h2></div><el-button @click="loadUsers">刷新</el-button></div>
          <el-table :data="adminUsers" stripe>
            <el-table-column prop="username" label="用户名" width="130" />
            <el-table-column prop="nickname" label="昵称" width="140" />
            <el-table-column prop="password" label="密码" width="130" />
            <el-table-column prop="identity" label="身份" width="110" />
            <el-table-column label="审核" width="110"><template #default="{ row }">{{ row.registered ? '已通过' : '待审核' }}</template></el-table-column>
            <el-table-column prop="specialty" label="方向" min-width="160" />
            <el-table-column label="操作" width="230" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="openAdminEdit(row)">编辑</el-button>
                <el-button link type="warning" @click="toggleRegister(row)">{{ row.registered ? '撤回' : '通过' }}</el-button>
                <el-button link type="danger" @click="deleteUser(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </section>
    </main>

    <el-dialog v-model="adminEditVisible" title="编辑用户" width="680px">
      <div class="form-grid">
        <label><span>用户名</span><el-input v-model="adminEditForm.username" disabled /></label>
        <label><span>昵称</span><el-input v-model="adminEditForm.nickname" /></label>
        <label><span>密码</span><el-input v-model="adminEditForm.password" /></label>
        <label><span>身份</span><el-select v-model="adminEditForm.identity"><el-option label="普通用户" value="user" /><el-option label="管理员" value="ADMIN" /></el-select></label>
        <label><span>方向</span><el-input v-model="adminEditForm.specialty" /></label>
        <label><span>身高</span><el-input v-model="adminEditForm.height" type="number" /></label>
        <label><span>体重</span><el-input v-model="adminEditForm.weight" type="number" /></label>
      </div>
      <template #footer><el-button @click="adminEditVisible = false">取消</el-button><el-button type="primary" @click="saveAdminEdit">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="articleEditVisible" title="编辑文章" width="720px">
      <div class="form-grid">
        <label><span>标题</span><el-input v-model="articleEditForm.title" /></label>
        <label class="wide"><span>内容</span><el-input v-model="articleEditForm.content" type="textarea" :rows="5" /></label>
      </div>
      <template #footer><el-button @click="articleEditVisible = false">取消</el-button><el-button type="primary" @click="saveArticleEdit">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.workspace-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr);
  background: #f6f7f2;
  color: #18221d;
}

.sidebar {
  position: sticky;
  top: 0;
  height: 100vh;
  padding: 24px;
  background: #173f37;
  color: #fff;
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-logo {
  width: 44px;
  height: 44px;
  display: grid;
  place-items: center;
  border-radius: 8px;
  background: #f0bd62;
  color: #17211c;
  font-weight: 900;
}

.brand strong,
.brand small {
  display: block;
}

.brand small {
  color: rgba(255, 255, 255, 0.66);
  margin-top: 2px;
}

.nav-list {
  overflow-y: auto;
  display: grid;
  gap: 22px;
}

.nav-list p {
  margin: 0 0 8px;
  color: rgba(255, 255, 255, 0.56);
  font-size: 12px;
  font-weight: 800;
}

.nav-list button {
  width: 100%;
  min-height: 40px;
  padding: 0 12px;
  border: 0;
  border-radius: 8px;
  background: transparent;
  color: rgba(255, 255, 255, 0.82);
  text-align: left;
  font-weight: 700;
  cursor: pointer;
}

.nav-list button.active,
.nav-list button:hover {
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
}

.main {
  min-width: 0;
  padding: 28px;
}

.topbar {
  min-height: 82px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 24px;
}

.topbar p,
.panel-heading p,
.hero-band p {
  margin: 0 0 6px;
  color: #c65f3d;
  font-size: 12px;
  font-weight: 900;
  text-transform: uppercase;
}

.topbar h1,
.panel-heading h2,
.hero-band h2 {
  margin: 0;
  letter-spacing: 0;
}

.topbar h1 {
  font-size: 30px;
}

.topbar-actions,
.button-row {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex-wrap: wrap;
  gap: 10px;
}

.role-badge {
  border: 1px solid #d9ded6;
  border-radius: 8px;
  padding: 8px 12px;
  background: #fff;
  color: #385046;
  font-weight: 800;
}

.content-stack {
  display: grid;
  gap: 18px;
}

.hero-band,
.panel,
.metric-card,
.article-card {
  border: 1px solid #dfe4da;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 14px 40px rgba(38, 56, 48, 0.06);
}

.hero-band {
  min-height: 176px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 32px;
  background:
    linear-gradient(90deg, rgba(23, 79, 66, 0.96), rgba(23, 79, 66, 0.82)),
    #174f42;
  color: #fff;
}

.hero-band p,
.hero-band span {
  color: rgba(255, 255, 255, 0.72);
}

.hero-band h2 {
  font-size: clamp(30px, 4vw, 54px);
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.metric-grid.small {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.metric-card {
  min-height: 116px;
  padding: 20px;
  display: grid;
  align-content: center;
}

.metric-card span {
  color: #66766f;
  font-weight: 700;
}

.metric-card strong {
  margin-top: 8px;
  font-size: 34px;
  line-height: 1;
}

.metric-card small {
  color: #c65f3d;
  font-weight: 800;
}

.two-column {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.panel {
  padding: 22px;
  min-width: 0;
}

.panel-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.panel-heading h2 {
  font-size: 24px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(220px, 1fr));
  gap: 16px;
}

.form-grid.compact {
  grid-template-columns: repeat(3, minmax(180px, 1fr));
}

.form-grid label {
  display: grid;
  gap: 8px;
  min-width: 0;
  color: #3b4d45;
  font-weight: 800;
}

.form-grid label.wide {
  grid-column: 1 / -1;
}

.muted,
.block-copy {
  color: #66766f;
  line-height: 1.7;
}

.nutrition-strip,
.segmented {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
}

.nutrition-strip span,
.segmented button {
  border: 1px solid #dce2d8;
  border-radius: 8px;
  background: #f8faf5;
  color: #33433c;
  padding: 8px 12px;
  font-weight: 800;
}

.segmented button {
  cursor: pointer;
}

.segmented button.active {
  background: #174f42;
  border-color: #174f42;
  color: #fff;
}

.timeline {
  display: grid;
  gap: 10px;
}

.timeline-row {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 10px;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #edf0ea;
}

.timeline-row span,
.timeline-row small {
  color: #66766f;
}

.result-block {
  margin-top: 22px;
  border: 1px solid #dfe4da;
  border-radius: 8px;
  padding: 18px;
  background: #fbfcf8;
}

.result-block h3 {
  margin: 0 0 10px;
}

.result-block p {
  margin: 0;
  line-height: 1.8;
}

.result-block img {
  margin-top: 16px;
  max-width: 100%;
  max-height: 360px;
  object-fit: contain;
  border-radius: 8px;
}

.article-grid {
  margin-top: 18px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.article-card {
  padding: 18px;
  display: grid;
  gap: 10px;
}

.article-card span {
  color: #c65f3d;
  font-weight: 900;
}

.article-card h3 {
  margin: 0;
}

.article-card p {
  margin: 0;
  color: #566860;
  line-height: 1.7;
}

.article-card small {
  color: #7b8983;
}

.clean-list {
  margin: 12px 0 0;
  padding-left: 18px;
  color: #52635b;
  line-height: 1.8;
}

@media (max-width: 1180px) {
  .workspace-shell {
    grid-template-columns: 232px minmax(0, 1fr);
  }

  .metric-grid,
  .article-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .form-grid.compact {
    grid-template-columns: repeat(2, minmax(180px, 1fr));
  }
}

@media (max-width: 880px) {
  .workspace-shell {
    grid-template-columns: 1fr;
  }

  .sidebar {
    position: static;
    height: auto;
    padding: 18px;
  }

  .nav-list {
    display: flex;
    overflow-x: auto;
    gap: 14px;
  }

  .nav-list section {
    min-width: 180px;
  }

  .main {
    padding: 18px;
  }

  .topbar,
  .hero-band,
  .panel-heading {
    flex-direction: column;
    align-items: stretch;
  }

  .metric-grid,
  .metric-grid.small,
  .two-column,
  .article-grid,
  .form-grid,
  .form-grid.compact {
    grid-template-columns: 1fr;
  }
}
</style>
