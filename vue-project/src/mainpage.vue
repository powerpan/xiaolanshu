<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Aim,
  ArrowLeft,
  Bell,
  Calendar,
  Check,
  DataAnalysis,
  Delete as DeleteIcon,
  Document,
  Edit,
  Food,
  Guide,
  House,
  MagicStick,
  Management,
  Notebook,
  Plus,
  Reading,
  Refresh,
  Search,
  Setting,
  SwitchButton,
  Timer,
  TrendCharts,
  User,
  View,
} from '@element-plus/icons-vue'
import api, { getToken, unwrap, withToken } from './services/api'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const activeView = ref('overview')
const contentAdminTab = ref('articles')

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
const selectedArticle = ref(null)
const selectedNotice = ref(null)

const isAdmin = computed(() => user.value?.identity === 'ADMIN')
const displayName = computed(() => user.value?.nickname || user.value?.username || '用户')
const greetingName = computed(() => displayName.value.replace(/\s/g, '') || '训练者')
const goalText = computed(() => profile.value?.fitnessGoal || '保持健康')
const weeklyFrequency = computed(() => Number(profile.value?.weeklyFrequency || 0))
const nextWorkoutTitle = computed(() => splitMode.value || '完成一次基础训练')

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
  id: '',
  lasttitle: '',
  lastcontent: '',
  title: '',
  topic: '',
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
    title: '今日',
    items: [
      { key: 'overview', label: '首页', icon: House },
      { key: 'checkin', label: '训练打卡', icon: Calendar },
      { key: 'nutrition', label: '饮食建议', icon: Food },
    ],
  },
  {
    title: '训练',
    items: [
      { key: 'profile', label: '个人资料', icon: User },
      { key: 'fitness', label: '健身需求', icon: Aim },
      { key: 'plan', label: '训练计划', icon: TrendCharts },
      { key: 'guide', label: '动作指导', icon: Guide },
    ],
  },
  {
    title: '内容',
    items: [
      { key: 'notices', label: '公告中心', icon: Bell },
      { key: 'articles', label: '文章广场', icon: Reading },
    ],
  },
  {
    title: '管理员',
    items: isAdmin.value
      ? [
          { key: 'contentAdmin', label: '内容管理', icon: Setting },
          { key: 'admin', label: '用户管理', icon: Management },
        ]
      : [],
  },
].filter((group) => group.items.length))

const weeklyDays = computed(() => {
  const count = Math.max(weeklyFrequency.value || Number(fitnessForm.weeklyFrequency || 0), 1)
  return Array.from({ length: count }, (_, index) => index + 1)
})

const overviewCards = computed(() => [
  { label: '本月打卡', value: checkinStats.value?.monthDays ?? 0, unit: '天', tone: 'green' },
  { label: '连续打卡', value: checkinStats.value?.currentStreak ?? 0, unit: '天', tone: 'orange' },
  { label: '训练时长', value: checkinStats.value?.totalMinutes ?? 0, unit: '分钟', tone: 'blue' },
  { label: '计划频率', value: weeklyFrequency.value, unit: '次/周', tone: 'ink' },
])

const featuredArticles = computed(() => articles.value.slice(0, 3))
const featuredNotices = computed(() => notices.value.slice(0, 3))
const articleParagraphs = computed(() => splitParagraphs(selectedArticle.value?.content))
const noticeParagraphs = computed(() => splitParagraphs(selectedNotice.value?.content))
const checkinPercent = computed(() => Math.min(100, Math.round(((checkinStats.value?.monthDays || 0) / 18) * 100)))
const planPercent = computed(() => Math.min(100, Math.round((weeklyFrequency.value / 6) * 100)))
const proteinPercent = computed(() => Math.min(100, Math.round(((nutrition.value?.proteinGrams || 0) / 140) * 100)))

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

const excerpt = (value, limit = 108) => {
  const text = String(value || '').replace(/\s+/g, ' ').trim()
  if (!text) return '暂无内容'
  return text.length > limit ? `${text.slice(0, limit)}...` : text
}

const splitParagraphs = (value) => {
  const text = String(value || '').trim()
  if (!text) return []
  const direct = text.split(/\n+/).map((item) => item.trim()).filter(Boolean)
  if (direct.length > 1) return direct
  return text
    .split(/(?<=[。！？])/)
    .map((item) => item.trim())
    .filter(Boolean)
}

const readingMinutes = (value) => Math.max(1, Math.ceil(String(value || '').length / 360))

const topicTone = (topic = '') => {
  const tones = ['mint', 'sun', 'sky', 'rose']
  let sum = 0
  String(topic || '内容').split('').forEach((char) => {
    sum += char.charCodeAt(0)
  })
  return tones[sum % tones.length]
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

const loadArticleDetail = async (id) => {
  selectedArticle.value = await unwrap(api.get('/article/getarticledetail', {
    params: withToken({ id }),
  }))
  activeView.value = 'articleDetail'
}

const loadNoticeDetail = async (id) => {
  selectedNotice.value = await unwrap(api.get('/notice/getnoticedetail', {
    params: withToken({ id }),
  }))
  activeView.value = 'noticeDetail'
}

const syncRouteDetail = async () => {
  if (!getToken()) return
  try {
    if (route.name === 'article-detail') {
      await loadArticleDetail(route.params.id)
    } else if (route.name === 'notice-detail') {
      await loadNoticeDetail(route.params.id)
    }
  } catch (error) {
    ElMessage.error(error.message || '内容加载失败')
    activeView.value = 'articles'
    router.replace('/mainpage')
  }
}

const bootstrap = async () => {
  if (!ensureToken()) return
  loading.value = true
  try {
    await loadUser()
    await loadProfile()
    const tasks = [
      loadNotices(),
      loadArticles(),
      loadCheckin(),
      loadNutrition(),
    ]
    if (isAdmin.value) {
      tasks.push(loadMyArticles(), loadUsers())
    }
    await Promise.allSettled(tasks)
    await loadPlan(1)
    await syncRouteDetail()
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
  if ((key === 'admin' || key === 'contentAdmin') && !isAdmin.value) {
    ElMessage.warning('该区域仅管理员可见')
    return
  }
  selectedArticle.value = null
  selectedNotice.value = null
  activeView.value = key
  if (route.name !== 'mainpage') {
    router.replace('/mainpage')
  }
  try {
    if (key === 'admin') await loadUsers()
    if (key === 'contentAdmin') await Promise.all([loadArticles(), loadNotices(), loadMyArticles()])
    if (key === 'notices') await loadNotices()
    if (key === 'articles') await loadArticles()
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
  contentAdminTab.value = 'notices'
  activeView.value = 'contentAdmin'
}

const saveNotice = async () => {
  try {
    if (!isAdmin.value) {
      ElMessage.warning('该操作仅管理员可用')
      return
    }
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
    if (!isAdmin.value) {
      ElMessage.warning('该操作仅管理员可用')
      return
    }
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

const openArticleDetail = (row) => {
  router.push(`/articles/${row.id}`)
}

const openNoticeDetail = (row) => {
  router.push(`/notices/${row.id}`)
}

const closeDetail = (target) => {
  router.push('/mainpage')
  activeView.value = target
}

const openArticleEdit = (row) => {
  Object.assign(articleEditForm, {
    id: row.id,
    lasttitle: row.title,
    lastcontent: row.content,
    title: row.title,
    topic: row.topic || '',
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
      params: withToken({ id: row.id, title: row.title, content: row.content }),
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

watch(() => route.fullPath, syncRouteDetail)
onMounted(bootstrap)
</script>

<template>
  <div class="workspace-shell" v-loading="loading">
    <aside class="sidebar">
      <div class="brand">
        <span class="brand-logo">XL</span>
        <div>
          <strong>小蓝书</strong>
          <small>训练、饮食与内容</small>
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
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </button>
        </section>
      </nav>

      <div class="sidebar-user">
        <span>{{ isAdmin ? 'ADMIN' : 'USER' }}</span>
        <strong>{{ displayName }}</strong>
        <small>{{ goalText }} · 每周 {{ weeklyFrequency || 0 }} 次</small>
      </div>
    </aside>

    <main class="main">
      <header class="topbar">
        <div>
          <p>欢迎回来</p>
          <h1>{{ greetingName }}，今天也稳一点进步</h1>
        </div>
        <div class="topbar-actions">
          <span class="role-badge">{{ isAdmin ? '管理员模式' : '普通用户' }}</span>
          <el-button :icon="SwitchButton" @click="logout">退出</el-button>
        </div>
      </header>

      <section v-if="activeView === 'overview'" class="content-stack">
        <div class="hero-stage">
          <div class="hero-copy">
            <p>Today Plan</p>
            <h2>{{ nextWorkoutTitle }}</h2>
            <span>{{ goalText }} · {{ profile?.equipment || '徒手' }} · {{ profile?.exLevel || '新手' }}</span>
            <div class="hero-actions">
              <el-button type="primary" :icon="TrendCharts" @click="openView('plan')">查看训练</el-button>
              <el-button :icon="Check" @click="openView('checkin')">完成打卡</el-button>
            </div>
          </div>
          <div class="hero-visual" aria-hidden="true">
            <div class="track-line">
              <span></span>
              <span></span>
              <span></span>
            </div>
            <div class="pulse-panel">
              <strong>{{ checkinPercent }}%</strong>
              <small>本月习惯进度</small>
            </div>
            <div class="mini-bars">
              <i style="height: 42%"></i>
              <i style="height: 64%"></i>
              <i style="height: 50%"></i>
              <i style="height: 78%"></i>
              <i style="height: 58%"></i>
            </div>
          </div>
        </div>

        <div class="metric-grid">
          <article v-for="card in overviewCards" :key="card.label" class="metric-card" :class="card.tone">
            <span>{{ card.label }}</span>
            <strong>{{ card.value }}</strong>
            <small>{{ card.unit }}</small>
          </article>
        </div>

        <div class="dashboard-grid">
          <section class="panel focus-panel">
            <div class="panel-heading">
              <div>
                <p>训练状态</p>
                <h2>本周执行</h2>
              </div>
              <el-button text :icon="Calendar" @click="openView('checkin')">记录</el-button>
            </div>
            <div class="progress-rows">
              <div>
                <span>计划频率</span>
                <strong>{{ weeklyFrequency || 0 }}/6</strong>
                <i><b :style="{ width: `${planPercent}%` }"></b></i>
              </div>
              <div>
                <span>本月习惯</span>
                <strong>{{ checkinStats?.monthDays || 0 }} 天</strong>
                <i><b :style="{ width: `${checkinPercent}%` }"></b></i>
              </div>
              <div>
                <span>蛋白目标</span>
                <strong>{{ nutrition?.proteinGrams || 0 }}g</strong>
                <i><b :style="{ width: `${proteinPercent}%` }"></b></i>
              </div>
            </div>
          </section>

          <section class="panel">
            <div class="panel-heading">
              <div>
                <p>饮食建议</p>
                <h2>{{ nutrition?.targetCalories || 0 }} kcal</h2>
              </div>
              <el-button text :icon="Food" @click="openView('nutrition')">详情</el-button>
            </div>
            <p class="muted">{{ nutrition?.summary || '完善资料后获取饮食建议。' }}</p>
            <div class="nutrition-strip">
              <span>蛋白 {{ nutrition?.proteinGrams || 0 }}g</span>
              <span>饮水 {{ nutrition?.waterMl || 0 }}ml</span>
            </div>
          </section>
        </div>

        <div class="home-content-grid">
          <section class="panel">
            <div class="panel-heading">
              <div>
                <p>精选文章</p>
                <h2>训练知识</h2>
              </div>
              <el-button text :icon="Reading" @click="openView('articles')">更多</el-button>
            </div>
            <div class="feed-list">
              <article v-for="item in featuredArticles" :key="item.id" class="feed-row" @click="openArticleDetail(item)">
                <span :class="topicTone(item.topic)">{{ item.topic || '未分类' }}</span>
                <div>
                  <h3>{{ item.title }}</h3>
                  <p>{{ excerpt(item.content, 84) }}</p>
                </div>
                <small>{{ readingMinutes(item.content) }} 分钟</small>
              </article>
            </div>
          </section>

          <section class="panel">
            <div class="panel-heading">
              <div>
                <p>公告</p>
                <h2>系统消息</h2>
              </div>
              <el-button text :icon="Bell" @click="openView('notices')">更多</el-button>
            </div>
            <div class="notice-list compact">
              <article v-for="item in featuredNotices" :key="item.id" @click="openNoticeDetail(item)">
                <strong>{{ item.title }}</strong>
                <span>{{ formatDate(item.noticetime) }}</span>
              </article>
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
          <el-button type="primary" :icon="Check" @click="saveProfile">保存资料</el-button>
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
          <el-button type="primary" :icon="MagicStick" @click="saveFitnessProfile">生成计划</el-button>
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
          <div class="task-grid">
            <article v-for="task in actionTasks" :key="`${task.actionPattern}-${task.description}`" class="task-card">
              <span>{{ task.actionPattern }}</span>
              <h3>{{ task.minSets }}-{{ task.maxSets }} 组 · {{ task.minReps }}-{{ task.maxReps }} 次</h3>
              <p>{{ task.description }}</p>
              <small>休息 {{ task.minRestSeconds }}-{{ task.maxRestSeconds }} 秒</small>
            </article>
          </div>
        </div>
      </section>

      <section v-if="activeView === 'guide'" class="panel">
        <div class="panel-heading">
          <div>
            <p>动作库</p>
            <h2>动作指导</h2>
          </div>
          <el-button type="primary" :icon="Search" @click="loadGuide">搜索动作</el-button>
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
        <div class="panel checkin-panel">
          <div class="panel-heading">
            <div>
              <p>每日记录</p>
              <h2>训练打卡</h2>
            </div>
            <el-button type="primary" :icon="Check" @click="saveCheckin">保存今日打卡</el-button>
          </div>
          <div class="form-grid">
            <label><span>训练时长</span><el-input-number v-model="checkinForm.durationMinutes" :min="0" :max="300" /></label>
            <label><span>状态</span><el-select v-model="checkinForm.mood"><el-option v-for="item in moods" :key="item" :label="item" :value="item" /></el-select></label>
            <label class="wide"><span>备注</span><el-input v-model="checkinForm.note" type="textarea" :rows="3" /></label>
          </div>
        </div>
        <div class="metric-grid small">
          <article class="metric-card green"><span>累计天数</span><strong>{{ checkinStats?.totalDays || 0 }}</strong><small>天</small></article>
          <article class="metric-card orange"><span>连续打卡</span><strong>{{ checkinStats?.currentStreak || 0 }}</strong><small>天</small></article>
          <article class="metric-card blue"><span>累计时长</span><strong>{{ checkinStats?.totalMinutes || 0 }}</strong><small>分钟</small></article>
        </div>
        <div class="panel">
          <div class="panel-heading">
            <div>
              <p>历史记录</p>
              <h2>最近打卡</h2>
            </div>
          </div>
          <div class="timeline">
            <div v-for="item in checkinStats?.recentCheckins || []" :key="item.id" class="timeline-row">
              <span>{{ item.checkinDate }}</span>
              <strong>{{ item.durationMinutes }} 分钟</strong>
              <small>{{ item.mood || '-' }}</small>
              <p>{{ item.note || '没有备注' }}</p>
            </div>
            <el-empty v-if="!checkinStats?.recentCheckins?.length" description="暂无打卡" />
          </div>
        </div>
      </section>

      <section v-if="activeView === 'nutrition'" class="panel">
        <div class="panel-heading">
          <div>
            <p>营养策略</p>
            <h2>饮食建议</h2>
          </div>
          <el-button :icon="Refresh" @click="loadNutrition">刷新建议</el-button>
        </div>
        <div class="metric-grid small">
          <article class="metric-card green"><span>目标热量</span><strong>{{ nutrition?.targetCalories || 0 }}</strong><small>kcal</small></article>
          <article class="metric-card orange"><span>蛋白质</span><strong>{{ nutrition?.proteinGrams || 0 }}</strong><small>g</small></article>
          <article class="metric-card blue"><span>饮水</span><strong>{{ nutrition?.waterMl || 0 }}</strong><small>ml</small></article>
        </div>
        <p class="muted block-copy">{{ nutrition?.summary }}</p>
        <div class="two-column">
          <div class="reader-box">
            <h3>一日结构</h3>
            <ul class="clean-list"><li v-for="item in nutrition?.meals || []" :key="item">{{ item }}</li></ul>
          </div>
          <div class="reader-box">
            <h3>执行提示</h3>
            <ul class="clean-list"><li v-for="item in nutrition?.tips || []" :key="item">{{ item }}</li></ul>
          </div>
        </div>
      </section>

      <section v-if="activeView === 'notices'" class="content-stack">
        <div class="page-intro">
          <p>公告中心</p>
          <h2>系统消息和训练提醒</h2>
          <span>普通用户只看到发布后的公共内容，管理操作统一收在管理员区域。</span>
        </div>
        <div class="notice-grid">
          <article v-for="item in notices" :key="item.id" class="notice-card" @click="openNoticeDetail(item)">
            <span>{{ formatDate(item.noticetime) }}</span>
            <h3>{{ item.title }}</h3>
            <p>{{ excerpt(item.content, 126) }}</p>
            <small>{{ item.author || '系统' }}</small>
          </article>
        </div>
      </section>

      <section v-if="activeView === 'noticeDetail'" class="reader-page">
        <button class="back-link" @click="closeDetail('notices')">
          <el-icon><ArrowLeft /></el-icon>
          返回公告
        </button>
        <article class="reader-article" v-if="selectedNotice">
          <div class="reader-meta">
            <span>公告</span>
            <small>{{ selectedNotice.author || '系统' }} · {{ formatDate(selectedNotice.noticetime) }}</small>
          </div>
          <h2>{{ selectedNotice.title }}</h2>
          <p v-for="paragraph in noticeParagraphs" :key="paragraph">{{ paragraph }}</p>
        </article>
      </section>

      <section v-if="activeView === 'articles'" class="content-stack">
        <div class="page-intro article-intro">
          <p>文章广场</p>
          <h2>训练知识、饮食策略和动作理解</h2>
          <span>长文支持进入详情阅读。创作、编辑和删除仅管理员可见。</span>
        </div>
        <div class="panel search-panel">
          <div class="form-grid compact">
            <label><span>标题</span><el-input v-model="articleSearch.title" clearable /></label>
            <label><span>主题</span><el-input v-model="articleSearch.topic" clearable /></label>
            <div class="field-action"><el-button type="primary" :icon="Search" @click="loadArticles">搜索</el-button></div>
          </div>
        </div>
        <div class="article-grid">
          <article v-for="item in articles" :key="item.id" class="article-card" @click="openArticleDetail(item)">
            <span :class="topicTone(item.topic)">{{ item.topic || '未分类' }}</span>
            <h3>{{ item.title }}</h3>
            <p>{{ excerpt(item.content, 148) }}</p>
            <footer>
              <small>{{ item.author }} · {{ formatDate(item.articletime) }}</small>
              <em>{{ readingMinutes(item.content) }} 分钟</em>
            </footer>
          </article>
        </div>
      </section>

      <section v-if="activeView === 'articleDetail'" class="reader-page">
        <button class="back-link" @click="closeDetail('articles')">
          <el-icon><ArrowLeft /></el-icon>
          返回文章
        </button>
        <article class="reader-article" v-if="selectedArticle">
          <div class="reader-meta">
            <span>{{ selectedArticle.topic || '未分类' }}</span>
            <small>{{ selectedArticle.author }} · {{ formatDate(selectedArticle.articletime) }} · {{ readingMinutes(selectedArticle.content) }} 分钟阅读</small>
          </div>
          <h2>{{ selectedArticle.title }}</h2>
          <p v-for="paragraph in articleParagraphs" :key="paragraph">{{ paragraph }}</p>
        </article>
        <aside class="related-panel">
          <h3>继续阅读</h3>
          <button v-for="item in featuredArticles.filter((article) => article.id !== selectedArticle?.id)" :key="item.id" @click="openArticleDetail(item)">
            {{ item.title }}
          </button>
        </aside>
      </section>

      <section v-if="activeView === 'contentAdmin'" class="content-stack">
        <div class="page-intro admin-intro">
          <p>内容管理</p>
          <h2>公告与文章发布台</h2>
          <span>这个区域只对管理员开放，普通用户不会看到编辑入口。</span>
        </div>
        <el-tabs v-model="contentAdminTab" class="admin-tabs">
          <el-tab-pane label="文章" name="articles">
            <div class="admin-grid">
              <section class="panel">
                <div class="panel-heading"><div><p>发布文章</p><h2>内容创作</h2></div><el-button type="primary" :icon="Plus" @click="addArticle">发布</el-button></div>
                <div class="form-grid">
                  <label><span>标题</span><el-input v-model="articleForm.title" /></label>
                  <label><span>主题</span><el-input v-model="articleForm.topic" /></label>
                  <label class="wide"><span>内容</span><el-input v-model="articleForm.content" type="textarea" :rows="8" /></label>
                </div>
              </section>
              <section class="panel">
                <div class="panel-heading"><div><p>文章库</p><h2>编辑发布内容</h2></div><el-button :icon="Refresh" @click="loadArticles">刷新</el-button></div>
                <div class="admin-list">
                  <article v-for="item in articles" :key="item.id">
                    <div>
                      <span>{{ item.topic || '未分类' }}</span>
                      <strong>{{ item.title }}</strong>
                      <small>{{ item.author }} · {{ formatDate(item.articletime) }}</small>
                    </div>
                    <div class="icon-actions">
                      <el-button :icon="View" circle @click="openArticleDetail(item)" />
                      <el-button :icon="Edit" circle @click="openArticleEdit(item)" />
                      <el-button :icon="DeleteIcon" circle type="danger" @click="deleteArticle(item)" />
                    </div>
                  </article>
                </div>
              </section>
            </div>
          </el-tab-pane>
          <el-tab-pane label="公告" name="notices">
            <div class="admin-grid">
              <section class="panel">
                <div class="panel-heading">
                  <div>
                    <p>{{ noticeEditing ? '编辑公告' : '发布公告' }}</p>
                    <h2>公告管理</h2>
                  </div>
                  <div class="button-row">
                    <el-button :icon="Notebook" @click="loadNoticeDraft">载入</el-button>
                    <el-button :icon="Document" @click="saveNoticeDraft">存草稿</el-button>
                    <el-button type="primary" :icon="Plus" @click="saveNotice">{{ noticeEditing ? '保存' : '发布' }}</el-button>
                  </div>
                </div>
                <div class="form-grid">
                  <label><span>标题</span><el-input v-model="noticeForm.title" /></label>
                  <label class="wide"><span>内容</span><el-input v-model="noticeForm.content" type="textarea" :rows="7" /></label>
                </div>
                <div class="draft-actions">
                  <el-button text @click="resetNoticeForm">重置表单</el-button>
                  <el-button text type="danger" @click="clearNoticeDraft">清空草稿</el-button>
                </div>
              </section>
              <section class="panel">
                <div class="panel-heading"><div><p>公告列表</p><h2>公共消息</h2></div><el-button :icon="Refresh" @click="loadNotices">刷新</el-button></div>
                <div class="admin-list">
                  <article v-for="item in notices" :key="item.id">
                    <div>
                      <span>{{ item.author || '系统' }}</span>
                      <strong>{{ item.title }}</strong>
                      <small>{{ formatDate(item.noticetime) }}</small>
                    </div>
                    <div class="icon-actions">
                      <el-button :icon="View" circle @click="openNoticeDetail(item)" />
                      <el-button :icon="Edit" circle @click="editNotice(item)" />
                      <el-button :icon="DeleteIcon" circle type="danger" @click="deleteNotice(item)" />
                    </div>
                  </article>
                </div>
              </section>
            </div>
          </el-tab-pane>
        </el-tabs>
      </section>

      <section v-if="activeView === 'admin'" class="content-stack">
        <div class="panel">
          <div class="panel-heading"><div><p>管理员</p><h2>添加用户</h2></div><el-button type="primary" :icon="Plus" @click="addAdminUser">添加</el-button></div>
          <div class="form-grid compact">
            <label><span>用户名</span><el-input v-model="adminAddForm.username" /></label>
            <label><span>密码</span><el-input v-model="adminAddForm.password" show-password /></label>
            <label><span>身份</span><el-select v-model="adminAddForm.identity"><el-option label="普通用户" value="user" /><el-option label="管理员" value="ADMIN" /></el-select></label>
          </div>
        </div>
        <div class="panel">
          <div class="panel-heading"><div><p>账号审核</p><h2>用户列表</h2></div><el-button :icon="Refresh" @click="loadUsers">刷新</el-button></div>
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

    <el-dialog v-model="articleEditVisible" title="编辑文章" width="760px">
      <div class="form-grid">
        <label><span>标题</span><el-input v-model="articleEditForm.title" /></label>
        <label><span>主题</span><el-input v-model="articleEditForm.topic" /></label>
        <label class="wide"><span>内容</span><el-input v-model="articleEditForm.content" type="textarea" :rows="9" /></label>
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
  background: #f5f7f2;
  color: #18221d;
}

.sidebar {
  position: sticky;
  top: 0;
  height: 100vh;
  padding: 24px;
  background: #102a28;
  color: #fff;
  display: flex;
  flex-direction: column;
  gap: 26px;
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
  background: #f2c35d;
  color: #17211c;
  font-weight: 900;
}

.brand strong,
.brand small {
  display: block;
}

.brand small {
  color: rgba(255, 255, 255, 0.64);
  margin-top: 2px;
}

.nav-list {
  overflow-y: auto;
  display: grid;
  gap: 20px;
}

.nav-list p {
  margin: 0 0 8px;
  color: rgba(255, 255, 255, 0.48);
  font-size: 12px;
  font-weight: 800;
}

.nav-list button {
  width: 100%;
  min-height: 42px;
  padding: 0 12px;
  border: 0;
  border-radius: 8px;
  background: transparent;
  color: rgba(255, 255, 255, 0.8);
  text-align: left;
  font-weight: 800;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
}

.nav-list button.active,
.nav-list button:hover {
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
}

.sidebar-user {
  margin-top: auto;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 8px;
  padding: 14px;
  display: grid;
  gap: 4px;
  background: rgba(255, 255, 255, 0.06);
}

.sidebar-user span,
.sidebar-user small {
  color: rgba(255, 255, 255, 0.64);
  font-size: 12px;
}

.main {
  min-width: 0;
  padding: 30px;
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
.hero-copy p,
.page-intro p {
  margin: 0 0 6px;
  color: #d16b43;
  font-size: 12px;
  font-weight: 900;
  text-transform: uppercase;
}

.topbar h1,
.panel-heading h2,
.hero-copy h2,
.page-intro h2 {
  margin: 0;
  letter-spacing: 0;
}

.topbar h1 {
  font-size: 30px;
}

.topbar-actions,
.button-row,
.hero-actions,
.icon-actions {
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

.hero-stage,
.panel,
.metric-card,
.article-card,
.notice-card,
.reader-article,
.related-panel,
.page-intro {
  border: 1px solid #dfe4da;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 14px 40px rgba(31, 49, 42, 0.06);
}

.hero-stage {
  min-height: 310px;
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(360px, 0.95fr);
  gap: 24px;
  align-items: stretch;
  overflow: hidden;
  background:
    linear-gradient(105deg, #123b36 0%, #1b5b4e 58%, #f2c35d 180%);
  color: #fff;
}

.hero-copy {
  padding: 36px;
  display: grid;
  align-content: center;
}

.hero-copy p,
.hero-copy span {
  color: rgba(255, 255, 255, 0.72);
}

.hero-copy h2 {
  margin: 4px 0 10px;
  max-width: 760px;
  font-size: clamp(36px, 5vw, 68px);
  line-height: 1.02;
}

.hero-actions {
  justify-content: flex-start;
  margin-top: 28px;
}

.hero-visual {
  position: relative;
  min-height: 300px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.12), rgba(255, 255, 255, 0.04)),
    repeating-linear-gradient(90deg, rgba(255, 255, 255, 0.08) 0 1px, transparent 1px 48px);
}

.track-line {
  position: absolute;
  left: 40px;
  right: 36px;
  top: 52px;
  height: 148px;
  border: 2px solid rgba(255, 255, 255, 0.38);
  border-left-color: transparent;
  border-bottom-color: transparent;
}

.track-line span {
  position: absolute;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #f2c35d;
}

.track-line span:nth-child(1) { left: 32px; top: 52px; }
.track-line span:nth-child(2) { left: 48%; top: 18px; background: #85d4ff; }
.track-line span:nth-child(3) { right: 32px; bottom: -6px; background: #f28d63; }

.pulse-panel {
  position: absolute;
  right: 42px;
  top: 76px;
  width: 148px;
  height: 148px;
  border: 14px solid rgba(255, 255, 255, 0.16);
  border-top-color: #f2c35d;
  border-right-color: #85d4ff;
  border-radius: 50%;
  display: grid;
  place-items: center;
  text-align: center;
}

.pulse-panel strong,
.pulse-panel small {
  grid-column: 1;
  grid-row: 1;
}

.pulse-panel strong {
  margin-top: -14px;
  font-size: 34px;
}

.pulse-panel small {
  margin-top: 42px;
  color: rgba(255, 255, 255, 0.7);
}

.mini-bars {
  position: absolute;
  left: 44px;
  right: 44px;
  bottom: 42px;
  height: 72px;
  display: flex;
  align-items: end;
  gap: 12px;
}

.mini-bars i {
  flex: 1;
  border-radius: 8px 8px 0 0;
  background: rgba(255, 255, 255, 0.82);
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
  min-height: 118px;
  padding: 20px;
  display: grid;
  align-content: center;
  border-left-width: 5px;
}

.metric-card.green { border-left-color: #2c8f72; }
.metric-card.orange { border-left-color: #df7d4f; }
.metric-card.blue { border-left-color: #4b95c8; }
.metric-card.ink { border-left-color: #243b38; }

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

.dashboard-grid,
.home-content-grid,
.two-column,
.admin-grid {
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

.field-action {
  display: flex;
  align-items: end;
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

.progress-rows {
  display: grid;
  gap: 18px;
}

.progress-rows div {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 8px 16px;
  align-items: center;
}

.progress-rows span {
  color: #65756e;
  font-weight: 800;
}

.progress-rows i {
  grid-column: 1 / -1;
  height: 10px;
  border-radius: 999px;
  background: #edf0e8;
  overflow: hidden;
}

.progress-rows b {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, #2c8f72, #85d4ff);
}

.feed-list,
.notice-list,
.admin-list,
.timeline {
  display: grid;
  gap: 12px;
}

.feed-row,
.notice-list article,
.admin-list article {
  border: 1px solid #edf0ea;
  border-radius: 8px;
  padding: 14px;
  display: grid;
  gap: 10px;
  cursor: pointer;
  background: #fcfdf9;
}

.feed-row {
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
}

.feed-row h3,
.feed-row p,
.article-card h3,
.article-card p,
.notice-card h3,
.notice-card p {
  margin: 0;
}

.feed-row p,
.article-card p,
.notice-card p {
  color: #5c6d65;
  line-height: 1.7;
}

.feed-row span,
.article-card span,
.admin-list span {
  border-radius: 8px;
  padding: 7px 10px;
  font-size: 12px;
  font-weight: 900;
}

.mint { background: #dff4ea; color: #21634e; }
.sun { background: #fff0c8; color: #8a5a10; }
.sky { background: #dcefff; color: #22658f; }
.rose { background: #ffe2d6; color: #9b4d2c; }

.page-intro {
  padding: 26px;
}

.page-intro h2 {
  font-size: 34px;
}

.page-intro span {
  display: block;
  margin-top: 10px;
  color: #66766f;
}

.article-intro,
.admin-intro {
  background: linear-gradient(120deg, #fff, #eef8f2);
}

.notice-grid,
.article-grid,
.task-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.notice-card,
.article-card,
.task-card {
  padding: 20px;
  display: grid;
  gap: 12px;
  cursor: pointer;
}

.article-card footer {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  color: #7b8983;
}

.article-card em {
  font-style: normal;
  color: #c65f3d;
  font-weight: 800;
}

.notice-card span,
.notice-card small,
.task-card small,
.timeline-row span,
.timeline-row small {
  color: #7b8983;
}

.task-card {
  border: 1px solid #dfe4da;
  border-radius: 8px;
  background: #fff;
}

.task-card span {
  color: #c65f3d;
  font-weight: 900;
}

.task-card h3,
.task-card p {
  margin: 0;
}

.task-card p {
  color: #5b6c64;
  line-height: 1.7;
}

.reader-page {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 300px;
  gap: 18px;
  align-items: start;
}

.back-link {
  grid-column: 1 / -1;
  border: 0;
  background: transparent;
  color: #174f42;
  font-weight: 900;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  width: fit-content;
}

.reader-article {
  padding: clamp(28px, 5vw, 56px);
}

.reader-meta {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 18px;
}

.reader-meta span {
  color: #c65f3d;
  font-weight: 900;
}

.reader-meta small {
  color: #74827c;
}

.reader-article h2 {
  margin: 0 0 24px;
  font-size: clamp(34px, 4vw, 58px);
  line-height: 1.08;
}

.reader-article p {
  margin: 0 0 18px;
  color: #33443d;
  font-size: 18px;
  line-height: 2;
}

.related-panel {
  padding: 20px;
  display: grid;
  gap: 10px;
}

.related-panel h3 {
  margin: 0 0 4px;
}

.related-panel button {
  border: 1px solid #e0e6dc;
  border-radius: 8px;
  background: #fbfcf8;
  padding: 12px;
  text-align: left;
  color: #31443c;
  font-weight: 800;
  cursor: pointer;
}

.timeline-row {
  display: grid;
  grid-template-columns: 150px 120px 110px minmax(0, 1fr);
  gap: 12px;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px solid #edf0ea;
}

.timeline-row p {
  margin: 0;
  color: #5d6f67;
}

.result-block,
.reader-box {
  border: 1px solid #dfe4da;
  border-radius: 8px;
  padding: 18px;
  background: #fbfcf8;
}

.result-block {
  margin-top: 22px;
}

.result-block h3,
.reader-box h3 {
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

.clean-list {
  margin: 12px 0 0;
  padding-left: 18px;
  color: #52635b;
  line-height: 1.8;
}

.admin-tabs :deep(.el-tabs__content) {
  overflow: visible;
}

.admin-list article {
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: center;
  cursor: default;
}

.admin-list strong,
.admin-list small {
  display: block;
}

.admin-list strong {
  margin: 8px 0 4px;
}

.draft-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 12px;
}

@media (max-width: 1280px) {
  .hero-stage {
    grid-template-columns: 1fr;
  }

  .hero-visual {
    min-height: 230px;
  }

  .notice-grid,
  .article-grid,
  .task-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 1120px) {
  .workspace-shell {
    grid-template-columns: 232px minmax(0, 1fr);
  }

  .metric-grid,
  .dashboard-grid,
  .home-content-grid,
  .two-column,
  .admin-grid,
  .reader-page {
    grid-template-columns: 1fr;
  }

  .form-grid.compact {
    grid-template-columns: repeat(2, minmax(180px, 1fr));
  }
}

@media (max-width: 820px) {
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
  .panel-heading,
  .feed-row,
  .admin-list article,
  .timeline-row {
    grid-template-columns: 1fr;
    flex-direction: column;
    align-items: stretch;
  }

  .metric-grid,
  .metric-grid.small,
  .notice-grid,
  .article-grid,
  .task-grid,
  .form-grid,
  .form-grid.compact {
    grid-template-columns: 1fr;
  }

  .hero-copy {
    padding: 28px;
  }

  .hero-copy h2,
  .page-intro h2 {
    font-size: 34px;
  }
}
</style>
