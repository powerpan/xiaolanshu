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
import api, { formBody, getToken, unwrap, withToken } from './services/api'

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
const guideLibrary = ref([])
const guideLibraryLoading = ref(false)
const todayCheckin = ref(null)
const checkinStats = ref(null)
const nutrition = ref(null)
const selectedArticle = ref(null)
const selectedNotice = ref(null)
const checkedTasks = ref({})

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

const guideFilters = reactive({
  actionPattern: '',
  equipment: '',
})

const checkinForm = reactive({
  durationMinutes: 35,
  mood: '状态不错',
  note: '',
})

const fitnessGoals = ['保持健康', '增肌', '减脂', '塑形', '提升力量']
const equipments = ['徒手', '哑铃', '弹力带', '杠铃', '单杠', '综合器械']
const levels = ['新手', '进阶', '熟练', '资深']
const actionPatterns = ['水平推', '垂直推', '水平拉', '垂直拉', '下肢蹲', '髋铰链', '单腿训练', '核心稳定', '手臂弯举', '手臂伸展', '灵活恢复']
const moods = ['状态不错', '轻松', '有挑战', '需要恢复']

const navGroups = computed(() => [
  {
    title: '今日',
    items: [
      { key: 'overview', label: '首页', icon: House },
      { key: 'checkin', label: '训练打卡', icon: Calendar },
      { key: 'insights', label: '训练复盘', icon: DataAnalysis },
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

const featuredArticles = computed(() => articles.value.slice(0, 2))
const featuredNotices = computed(() => notices.value.slice(0, 2))
const articleParagraphs = computed(() => splitParagraphs(selectedArticle.value?.content))
const noticeParagraphs = computed(() => splitParagraphs(selectedNotice.value?.content))
const checkinPercent = computed(() => Math.min(100, Math.round(((checkinStats.value?.monthDays || 0) / 18) * 100)))
const planPercent = computed(() => Math.min(100, Math.round((weeklyFrequency.value / 6) * 100)))
const proteinPercent = computed(() => Math.min(100, Math.round(((nutrition.value?.proteinGrams || 0) / 140) * 100)))
const recentCheckins = computed(() => checkinStats.value?.recentCheckins || [])
const timelineRecords = computed(() => recentCheckins.value.slice(0, 10))
const heroBars = computed(() => {
  const records = recentCheckins.value.slice(0, 5).reverse()
  return records.map((item) => Math.min(100, Math.max(18, Math.round(((item.durationMinutes || 0) / 60) * 100))))
})
const todayKey = computed(() => formatLocalDate(new Date()))
const planCheckKey = computed(() => `xl-plan-${user.value?.username || 'guest'}-${todayKey.value}-${selectedDay.value}`)
const completedTaskMap = computed(() => checkedTasks.value[planCheckKey.value] || {})
const completedTaskCount = computed(() => Object.values(completedTaskMap.value).filter(Boolean).length)
const dayCompletionPercent = computed(() => {
  if (!actionTasks.value.length) return 0
  return Math.round((completedTaskCount.value / actionTasks.value.length) * 100)
})
const estimatedMinutes = computed(() => {
  if (!actionTasks.value.length) return 0
  const workMinutes = actionTasks.value.reduce((sum, task) => sum + Number(task.maxSets || task.minSets || 2) * 3, 0)
  return Math.max(20, Math.round(workMinutes + actionTasks.value.length * 4))
})
const plannedVolume = computed(() => actionTasks.value.reduce((sum, task) => {
  const sets = Number(task.maxSets || task.minSets || 0)
  const reps = Number(task.maxReps || task.minReps || 0)
  return sum + sets * reps
}, 0))
const readinessScore = computed(() => {
  const streak = Number(checkinStats.value?.currentStreak || 0)
  const avg = Number(checkinStats.value?.averageMinutes || 0)
  const trainedToday = todayCheckin.value ? 12 : 0
  const frequencyBonus = Math.min(12, Number(weeklyFrequency.value || 0) * 2)
  return Math.min(100, Math.round(46 + streak * 5 + avg * 0.35 + trainedToday + frequencyBonus))
})
const macroItems = computed(() => [
  { label: '蛋白质', value: nutrition.value?.proteinGrams || 0, unit: 'g', percent: proteinPercent.value },
  { label: '碳水', value: nutrition.value?.carbohydrateGrams || 0, unit: 'g', percent: Math.min(100, Math.round(((nutrition.value?.carbohydrateGrams || 0) / 320) * 100)) },
  { label: '脂肪', value: nutrition.value?.fatGrams || 0, unit: 'g', percent: Math.min(100, Math.round(((nutrition.value?.fatGrams || 0) / 90) * 100)) },
  { label: '饮水', value: nutrition.value?.waterMl || 0, unit: 'ml', percent: Math.min(100, Math.round(((nutrition.value?.waterMl || 0) / 3000) * 100)) },
])
const heatmapDays = computed(() => {
  const recordMap = new Map(recentCheckins.value.map((item) => [String(item.checkinDate), item]))
  const days = []
  const cursor = new Date()
  cursor.setHours(0, 0, 0, 0)
  for (let offset = 34; offset >= 0; offset -= 1) {
    const date = new Date(cursor)
    date.setDate(cursor.getDate() - offset)
    const key = formatLocalDate(date)
    const record = recordMap.get(key)
    const minutes = Number(record?.durationMinutes || 0)
    days.push({
      key,
      label: `${date.getMonth() + 1}/${date.getDate()}`,
      minutes,
      mood: record?.mood || '',
      level: minutes >= 50 ? 4 : minutes >= 35 ? 3 : minutes >= 20 ? 2 : minutes > 0 ? 1 : 0,
    })
  }
  return days
})
const coachingTips = computed(() => {
  const tips = []
  if (!todayCheckin.value) {
    tips.push({ title: '今日优先级', body: `完成第 ${selectedDay.value} 天训练，预计 ${estimatedMinutes.value || 30} 分钟。` })
  } else {
    tips.push({ title: '今日状态', body: `已记录 ${todayCheckin.value.durationMinutes || 0} 分钟，建议补充水分并记录动作感受。` })
  }
  tips.push({ title: '计划调整', body: `当前目标为「${goalText.value}」，每周 ${weeklyFrequency.value || 0} 次；连续两周稳定后再提高训练量。` })
  tips.push({ title: '饮食配合', body: nutrition.value?.trainingDayTip || '训练日前后优先补足蛋白质、水分和易消化碳水。' })
  return tips
})
const guideCoverage = computed(() => {
  const patterns = new Set(guideLibrary.value.map((item) => item.actionPattern).filter(Boolean))
  const equipmentsUsed = new Set(guideLibrary.value.map((item) => item.equipment).filter(Boolean))
  return { patterns: patterns.size, equipments: equipmentsUsed.size, total: guideLibrary.value.length }
})
const profileCompleteness = computed(() => {
  const checks = [
    Boolean(user.value?.nickname),
    Boolean(user.value?.height),
    Boolean(user.value?.weight),
    Boolean(user.value?.specialty),
    Boolean(profile.value?.fitnessGoal),
    Boolean(profile.value?.weeklyFrequency),
    Boolean(profile.value?.equipment),
    Boolean(profile.value?.exLevel),
  ]
  return Math.round((checks.filter(Boolean).length / checks.length) * 100)
})
const nextBestAction = computed(() => {
  if (profileCompleteness.value < 75) return '先补全身体数据和训练偏好，计划会更稳定。'
  if (!todayCheckin.value) return `完成第 ${selectedDay.value} 天训练并保存打卡。`
  if (!nutrition.value?.trainingDayTip) return '刷新饮食建议，确认训练日前后的补给策略。'
  return '今天已经形成闭环，建议查看复盘趋势并准备下一次训练。'
})
const onboardingItems = computed(() => [
  {
    key: 'profile',
    icon: User,
    label: '完善身体资料',
    body: '身高、体重、昵称和训练方向会影响饮食建议展示。',
    done: profileCompleteness.value >= 50,
    action: 'profile',
  },
  {
    key: 'fitness',
    icon: Aim,
    label: '配置训练目标',
    body: `当前目标「${goalText.value}」，每周 ${weeklyFrequency.value || 0} 次。`,
    done: Boolean(profile.value?.fitnessGoal && profile.value?.equipment && profile.value?.exLevel),
    action: 'fitness',
  },
  {
    key: 'plan',
    icon: TrendCharts,
    label: '生成今日计划',
    body: actionTasks.value.length ? `已生成 ${actionTasks.value.length} 个动作。` : '暂无动作，请先生成训练需求。',
    done: actionTasks.value.length > 0,
    action: 'plan',
  },
  {
    key: 'checkin',
    icon: Calendar,
    label: '完成训练打卡',
    body: todayCheckin.value ? `已记录 ${todayCheckin.value.durationMinutes || 0} 分钟。` : '训练后记录时长、状态和备注。',
    done: Boolean(todayCheckin.value),
    action: 'checkin',
  },
])
const achievementCards = computed(() => [
  {
    label: '启动训练',
    value: '1 次打卡',
    done: Number(checkinStats.value?.totalDays || 0) >= 1,
  },
  {
    label: '稳定连续',
    value: '连续 3 天',
    done: Number(checkinStats.value?.bestStreak || 0) >= 3,
  },
  {
    label: '百分钟积累',
    value: '累计 100 分钟',
    done: Number(checkinStats.value?.totalMinutes || 0) >= 100,
  },
  {
    label: '计划执行',
    value: '完成今日动作',
    done: actionTasks.value.length > 0 && completedTaskCount.value === actionTasks.value.length,
  },
])
const suggestedGuides = computed(() => {
  const seen = new Set()
  const byPattern = actionTasks.value
    .map((task) => guideLibrary.value.find((guide) => guide.actionPattern === task.actionPattern))
    .filter(Boolean)
  return byPattern
    .filter((item) => {
      const key = `${item.actionPattern}-${item.equipment}-${item.actionName}`
      if (seen.has(key)) return false
      seen.add(key)
      return true
    })
    .slice(0, 4)
})

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

const formatLocalDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
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

const splitGuideText = (value) => String(value || '')
  .split('|')
  .map((item) => item.trim())
  .filter(Boolean)

const withExerciseMediaState = (item) => item ? { ...item, imageBroken: false } : null
const markGuideImageBroken = () => {
  if (guideResult.value) guideResult.value.imageBroken = true
}

const readingMinutes = (value) => Math.max(1, Math.ceil(String(value || '').length / 360))

const restoreTaskState = () => {
  try {
    checkedTasks.value = JSON.parse(localStorage.getItem('xl-task-completions') || '{}')
  } catch (error) {
    checkedTasks.value = {}
  }
}

const persistTaskState = () => {
  localStorage.setItem('xl-task-completions', JSON.stringify(checkedTasks.value))
}

const isTaskDone = (index) => Boolean(completedTaskMap.value[index])

const toggleTaskDone = (index, value) => {
  const current = { ...completedTaskMap.value, [index]: value }
  checkedTasks.value = {
    ...checkedTasks.value,
    [planCheckKey.value]: current,
  }
  persistTaskState()
}

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
  const result = await unwrap(api.get('/exerciseguide/getexerciseguide', {
    params: withToken({
      actionPattern: guideForm.actionPattern,
      equipment: guideForm.equipment,
    }),
  }))
  guideResult.value = withExerciseMediaState(result)
}

const loadGuideLibrary = async () => {
  guideLibraryLoading.value = true
  try {
    guideLibrary.value = await unwrap(api.get('/exerciseguide/list', {
      params: withToken({
        actionPattern: guideFilters.actionPattern || undefined,
        equipment: guideFilters.equipment || undefined,
      }),
    }))
  } finally {
    guideLibraryLoading.value = false
  }
}

const clearGuideFilters = async () => {
  Object.assign(guideFilters, { actionPattern: '', equipment: '' })
  await loadGuideLibrary()
}

const openGuideFromLibrary = (item) => {
  guideResult.value = withExerciseMediaState(item)
  Object.assign(guideForm, {
    actionPattern: item.actionPattern || guideForm.actionPattern,
    equipment: item.equipment || guideForm.equipment,
  })
}

const openSuggestedGuide = (item) => {
  openGuideFromLibrary(item)
  activeView.value = 'guide'
  if (route.name !== 'mainpage') {
    router.replace('/mainpage')
  }
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
      loadGuideLibrary(),
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
    if (key === 'guide') await loadGuideLibrary()
    if (key === 'checkin') await loadCheckin()
    if (key === 'insights') await loadCheckin()
    if (key === 'nutrition') await loadNutrition()
  } catch (error) {
    ElMessage.error(error.message || '加载失败')
  }
}

const saveProfile = async () => {
  try {
    await unwrap(api.put('/user/editmessage', formBody({
        username: profileForm.username,
        nickname: profileForm.nickname,
        password: profileForm.password,
        userpic: user.value?.userpic || '',
        identity: user.value?.identity || 'user',
        specialty: profileForm.specialty,
        height: profileForm.height,
        weight: profileForm.weight,
      })))
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
    await unwrap(api.post('/user/adduser', formBody(adminAddForm)))
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
    await unwrap(api.put('/user/editallmessage', formBody(adminEditForm)))
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

const savePlanCheckin = async () => {
  if (!actionTasks.value.length) {
    ElMessage.warning('当前没有可打卡的训练动作')
    return
  }
  if (!completedTaskCount.value) {
    ElMessage.warning('先勾选已完成的动作')
    return
  }

  Object.assign(checkinForm, {
    durationMinutes: Math.max(Number(checkinForm.durationMinutes || 0), estimatedMinutes.value),
    mood: completedTaskCount.value === actionTasks.value.length ? '状态不错' : '有挑战',
    note: `完成第 ${selectedDay.value} 天「${splitMode.value || '训练计划'}」：${completedTaskCount.value}/${actionTasks.value.length} 个动作。`,
  })
  await saveCheckin()
}

const logout = () => {
  localStorage.removeItem('jwttoken')
  router.replace('/')
}

watch(() => route.fullPath, syncRouteDetail)
onMounted(() => {
  restoreTaskState()
  bootstrap()
})
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

      <section v-if="activeView === 'overview'" class="content-stack overview-stack">
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
          <div class="hero-visual">
            <section class="visual-ring-card">
              <div class="progress-ring" :style="{ '--progress': `${checkinPercent}%` }">
                <strong>{{ checkinPercent }}%</strong>
              </div>
              <div>
                <span>本月习惯进度</span>
                <small>{{ checkinStats?.monthDays || 0 }} 天已打卡 · 目标 18 天</small>
              </div>
            </section>
            <section class="visual-data-grid">
              <article>
                <span>连续</span>
                <strong>{{ checkinStats?.currentStreak || 0 }}</strong>
                <small>天</small>
              </article>
              <article>
                <span>累计</span>
                <strong>{{ checkinStats?.totalMinutes || 0 }}</strong>
                <small>分钟</small>
              </article>
              <article>
                <span>计划</span>
                <strong>{{ weeklyFrequency || 0 }}</strong>
                <small>次/周</small>
              </article>
            </section>
            <section class="visual-bars-card">
              <div class="bar-head">
                <span>最近训练节奏</span>
                <small>按最近打卡时长</small>
              </div>
              <div v-if="heroBars.length" class="mini-bars">
                <i v-for="(height, index) in heroBars" :key="index" :style="{ height: `${height}%` }"></i>
              </div>
              <p v-else class="visual-empty">暂无最近打卡数据</p>
            </section>
          </div>
        </div>

        <div class="metric-grid">
          <article v-for="card in overviewCards" :key="card.label" class="metric-card" :class="card.tone">
            <span>{{ card.label }}</span>
            <strong>{{ card.value }}</strong>
            <small>{{ card.unit }}</small>
          </article>
        </div>

        <div class="product-command-grid">
          <section class="panel action-brief">
            <div class="panel-heading">
              <div>
                <p>今日执行</p>
                <h2>训练指挥台</h2>
              </div>
              <strong class="readiness-score" :style="{ '--score': readinessScore }">{{ readinessScore }}</strong>
            </div>
            <div class="brief-body">
              <div class="brief-meter">
                <i :style="{ width: `${readinessScore}%` }"></i>
              </div>
              <p>{{ todayCheckin ? '今日训练已记录，建议关注恢复和饮食执行。' : `建议完成「${splitMode || nextWorkoutTitle}」，预计 ${estimatedMinutes || 30} 分钟。` }}</p>
              <div class="brief-actions">
                <el-button type="primary" :icon="TrendCharts" @click="openView('plan')">进入计划</el-button>
                <el-button :icon="DataAnalysis" @click="openView('insights')">看复盘</el-button>
              </div>
            </div>
          </section>

          <section class="panel heatmap-panel">
            <div class="panel-heading">
              <div>
                <p>习惯热力</p>
                <h2>最近 35 天</h2>
              </div>
              <span class="coverage-badge">最佳连续 {{ checkinStats?.bestStreak || 0 }} 天</span>
            </div>
            <div class="heatmap-grid">
              <span
                v-for="day in heatmapDays"
                :key="day.key"
                :class="`level-${day.level}`"
                :title="`${day.label} · ${day.minutes || 0} 分钟 ${day.mood || ''}`"
              ></span>
            </div>
          </section>
        </div>

        <div class="product-flow-grid">
          <section class="panel onboarding-panel">
            <div class="panel-heading">
              <div>
                <p>产品引导</p>
                <h2>训练闭环完成度</h2>
              </div>
              <strong class="completion-pill">{{ profileCompleteness }}%</strong>
            </div>
            <p class="muted flow-copy">{{ nextBestAction }}</p>
            <div class="onboarding-list">
              <article v-for="item in onboardingItems.slice(0, 3)" :key="item.key" :class="{ done: item.done }">
                <el-icon><component :is="item.icon" /></el-icon>
                <div>
                  <strong>{{ item.label }}</strong>
                  <p>{{ item.body }}</p>
                </div>
                <el-button size="small" :type="item.done ? 'success' : 'primary'" plain @click="openView(item.action)">
                  {{ item.done ? '已完成' : '去完善' }}
                </el-button>
              </article>
            </div>
          </section>

          <section class="panel guide-suggestion-panel">
            <div class="panel-heading">
              <div>
                <p>动作提示</p>
                <h2>今日计划相关动作</h2>
              </div>
              <el-button text :icon="Guide" @click="openView('guide')">动作库</el-button>
            </div>
            <div class="suggestion-list" v-if="suggestedGuides.length">
              <article v-for="item in suggestedGuides.slice(0, 2)" :key="`${item.actionPattern}-${item.actionName}`" @click="openSuggestedGuide(item)">
                <span>{{ item.actionPattern }} · {{ item.equipment }}</span>
                <strong>{{ item.actionName || item.actionPattern }}</strong>
                <p>{{ excerpt(item.description, 76) }}</p>
              </article>
            </div>
            <div v-else class="empty-mini">
              <el-icon><Guide /></el-icon>
              <span>生成计划后会自动推荐相关动作指导。</span>
            </div>
          </section>
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
          <label><span>新密码</span><el-input v-model="profileForm.password" show-password placeholder="留空则不修改密码" /></label>
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
          <div class="plan-execution-bar">
            <div>
              <span>执行进度</span>
              <strong>{{ completedTaskCount }}/{{ actionTasks.length }} 个动作</strong>
              <i><b :style="{ width: `${dayCompletionPercent}%` }"></b></i>
            </div>
            <div>
              <span>预计时长</span>
              <strong>{{ estimatedMinutes }} 分钟</strong>
            </div>
            <div>
              <span>训练容量</span>
              <strong>{{ plannedVolume }} 次/秒</strong>
            </div>
            <el-button type="primary" :icon="Check" @click="savePlanCheckin">按完成情况打卡</el-button>
          </div>
          <div class="plan-coach-strip">
            <article>
              <span>今日重点</span>
              <strong>{{ splitMode || '基础训练' }}</strong>
              <p>先保证动作质量，再追求组数、次数或保持时间。</p>
            </article>
            <article>
              <span>节奏建议</span>
              <strong>{{ estimatedMinutes || 30 }} 分钟</strong>
              <p>每组之间按计划休息，动作变形时主动降级。</p>
            </article>
            <article>
              <span>结束标准</span>
              <strong>{{ completedTaskCount }}/{{ actionTasks.length }}</strong>
              <p>勾选完成动作后可直接生成今日打卡记录。</p>
            </article>
          </div>
          <div class="task-grid">
            <article
              v-for="(task, index) in actionTasks"
              :key="`${task.actionPattern}-${task.description}`"
              class="task-card"
              :class="{ done: isTaskDone(index) }"
            >
              <div class="task-checkbar">
                <el-checkbox :model-value="isTaskDone(index)" @change="(value) => toggleTaskDone(index, value)">
                  动作 {{ index + 1 }} 已完成
                </el-checkbox>
              </div>
              <div class="task-body">
                <span>{{ task.actionPattern }}</span>
                <h3>{{ task.actionName || task.actionPattern }}</h3>
                <div class="task-dose">
                  <strong>{{ task.minSets }}-{{ task.maxSets }} 组</strong>
                  <strong>{{ task.minReps }}-{{ task.maxReps }} 次/秒</strong>
                  <strong>休息 {{ task.minRestSeconds }}-{{ task.maxRestSeconds }} 秒</strong>
                </div>
                <p>{{ task.guideDescription || task.description }}</p>
                <div class="guide-columns">
                  <div>
                    <h4>执行步骤</h4>
                    <ol>
                      <li v-for="step in splitGuideText(task.steps)" :key="step">{{ step }}</li>
                    </ol>
                  </div>
                  <div>
                    <h4>训练要点</h4>
                    <ul>
                      <li v-for="tip in splitGuideText(task.tips)" :key="tip">{{ tip }}</li>
                    </ul>
                  </div>
                </div>
              </div>
            </article>
            <el-empty v-if="!actionTasks.length" description="暂无训练动作，请先完善健身需求" />
          </div>
        </div>
      </section>

      <section v-if="activeView === 'guide'" class="content-stack">
        <div class="panel">
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
            <div class="result-media">
              <img
                v-if="guideResult.imageurl && !guideResult.imageBroken"
                :src="guideResult.imageurl"
                :alt="`${guideResult.actionName || guideResult.actionPattern}动作图`"
                loading="lazy"
                decoding="async"
                @error="markGuideImageBroken"
              />
              <div v-else class="task-placeholder">
                <el-icon><Guide /></el-icon>
              </div>
              <a v-if="guideResult.imageSourceUrl" class="image-source" :href="guideResult.imageSourceUrl" target="_blank" rel="noreferrer">
                图片来源<span v-if="guideResult.imageCredit"> · {{ guideResult.imageCredit }}</span>
              </a>
            </div>
            <h3>{{ guideResult.actionName || guideResult.actionPattern }} · {{ guideResult.equipment }}</h3>
            <p>{{ guideResult.description || '暂无说明' }}</p>
            <div class="guide-columns">
              <div>
                <h4>执行步骤</h4>
                <ol>
                  <li v-for="step in splitGuideText(guideResult.steps)" :key="step">{{ step }}</li>
                </ol>
              </div>
              <div>
                <h4>训练要点</h4>
                <ul>
                  <li v-for="tip in splitGuideText(guideResult.tips)" :key="tip">{{ tip }}</li>
                </ul>
              </div>
            </div>
          </div>
        </div>

        <div class="panel">
          <div class="panel-heading">
            <div>
              <p>动作百科</p>
              <h2>{{ guideCoverage.total }} 个动作指导</h2>
            </div>
            <span class="coverage-badge">{{ guideCoverage.patterns }} 类模式 · {{ guideCoverage.equipments }} 类器材</span>
          </div>
          <div class="form-grid compact guide-filter-row">
            <label><span>动作模式</span><el-select v-model="guideFilters.actionPattern" clearable><el-option v-for="item in actionPatterns" :key="item" :label="item" :value="item" /></el-select></label>
            <label><span>器材</span><el-select v-model="guideFilters.equipment" clearable><el-option v-for="item in equipments" :key="item" :label="item" :value="item" /></el-select></label>
            <div class="field-action">
              <el-button type="primary" :icon="Search" :loading="guideLibraryLoading" @click="loadGuideLibrary">筛选</el-button>
              <el-button @click="clearGuideFilters">重置</el-button>
            </div>
          </div>
          <div class="guide-library-grid">
            <article v-for="item in guideLibrary" :key="`${item.actionPattern}-${item.equipment}-${item.id}`" class="guide-library-card" @click="openGuideFromLibrary(item)">
              <img v-if="item.imageurl" :src="item.imageurl" :alt="`${item.actionName || item.actionPattern}动作图`" loading="lazy" decoding="async" />
              <div v-else class="task-placeholder"><el-icon><Guide /></el-icon></div>
              <div>
                <span>{{ item.actionPattern }} · {{ item.equipment }}</span>
                <h3>{{ item.actionName || item.actionPattern }}</h3>
                <p>{{ excerpt(item.description, 88) }}</p>
              </div>
            </article>
            <el-empty v-if="!guideLibrary.length && !guideLibraryLoading" description="暂无动作指导" />
          </div>
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
            <div v-for="item in timelineRecords" :key="item.id" class="timeline-row">
              <span>{{ item.checkinDate }}</span>
              <strong>{{ item.durationMinutes }} 分钟</strong>
              <small>{{ item.mood || '-' }}</small>
              <p>{{ item.note || '没有备注' }}</p>
            </div>
            <el-empty v-if="!checkinStats?.recentCheckins?.length" description="暂无打卡" />
          </div>
        </div>
      </section>

      <section v-if="activeView === 'insights'" class="content-stack">
        <div class="page-intro insights-intro">
          <p>训练复盘</p>
          <h2>从记录里看趋势，而不是只看单次表现</h2>
          <span>基于最近打卡、计划频率、营养建议和当前目标生成复盘视图。</span>
        </div>
        <div class="metric-grid small">
          <article class="metric-card green"><span>准备度</span><strong>{{ readinessScore }}</strong><small>/100</small></article>
          <article class="metric-card orange"><span>最佳连续</span><strong>{{ checkinStats?.bestStreak || 0 }}</strong><small>天</small></article>
          <article class="metric-card blue"><span>平均时长</span><strong>{{ checkinStats?.averageMinutes || 0 }}</strong><small>分钟</small></article>
        </div>
        <div class="dashboard-grid">
          <section class="panel heatmap-panel expanded">
            <div class="panel-heading">
              <div>
                <p>执行日历</p>
                <h2>最近 35 天训练热力</h2>
              </div>
            </div>
            <div class="heatmap-grid large">
              <span
                v-for="day in heatmapDays"
                :key="day.key"
                :class="`level-${day.level}`"
                :title="`${day.key} · ${day.minutes || 0} 分钟 ${day.mood || ''}`"
              >
                <small>{{ day.label }}</small>
              </span>
            </div>
          </section>
          <section class="panel">
            <div class="panel-heading">
              <div>
                <p>教练建议</p>
                <h2>下一步怎么做</h2>
              </div>
            </div>
            <div class="coach-tip-list">
              <article v-for="tip in coachingTips" :key="tip.title">
                <span></span>
                <div>
                  <strong>{{ tip.title }}</strong>
                  <p>{{ tip.body }}</p>
                </div>
              </article>
            </div>
          </section>
        </div>
        <section class="panel achievement-panel">
          <div class="panel-heading">
            <div>
              <p>成就墙</p>
              <h2>把训练行为变成可见进度</h2>
            </div>
          </div>
          <div class="achievement-grid">
            <article v-for="item in achievementCards" :key="item.label" :class="{ done: item.done }">
              <span><el-icon><Check /></el-icon></span>
              <strong>{{ item.label }}</strong>
              <small>{{ item.value }}</small>
            </article>
          </div>
        </section>
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
        <div class="macro-grid">
          <article v-for="item in macroItems" :key="item.label">
            <div>
              <span>{{ item.label }}</span>
              <strong>{{ item.value }}{{ item.unit }}</strong>
            </div>
            <i><b :style="{ width: `${item.percent}%` }"></b></i>
          </article>
        </div>
        <p class="muted block-copy">{{ nutrition?.summary }}</p>
        <p class="training-day-tip">{{ nutrition?.trainingDayTip }}</p>
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
        <label><span>新密码</span><el-input v-model="adminEditForm.password" show-password placeholder="留空则不修改密码" /></label>
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
  width: 100%;
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr);
  background:
    linear-gradient(135deg, #f7f8f1 0%, #eef5ef 42%, #f7f2e6 100%);
  color: #18221d;
}

.sidebar {
  position: sticky;
  top: 0;
  height: 100vh;
  padding: 24px;
  background:
    linear-gradient(150deg, rgba(16, 42, 40, 0.96), rgba(21, 77, 67, 0.96)),
    repeating-linear-gradient(135deg, rgba(255, 255, 255, 0.08) 0 1px, transparent 1px 18px);
  color: #fff;
  display: flex;
  flex-direction: column;
  gap: 26px;
  box-shadow: 20px 0 55px rgba(21, 42, 36, 0.14);
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
  background: linear-gradient(135deg, #ffe29a, #f2c35d 50%, #85d4ff);
  color: #17211c;
  font-weight: 900;
  box-shadow: 0 12px 28px rgba(242, 195, 93, 0.28);
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
  position: relative;
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
  overflow: hidden;
  transition: transform 180ms ease, background 180ms ease, color 180ms ease;
}

.nav-list button::after {
  content: "";
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.18), transparent);
  transform: translateX(-120%);
  transition: transform 420ms ease;
}

.nav-list button.active,
.nav-list button:hover {
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
  transform: translateX(4px);
}

.nav-list button.active::after,
.nav-list button:hover::after {
  transform: translateX(120%);
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
  padding: 24px;
}

.topbar {
  min-height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 16px;
}

.topbar > div:first-child {
  min-width: 0;
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
  max-width: 100%;
  font-size: clamp(22px, 2.4vw, 28px);
  line-height: 1.22;
  overflow-wrap: anywhere;
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

.overview-stack {
  gap: 14px;
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
  animation: surface-in 520ms cubic-bezier(0.2, 0.8, 0.2, 1) both;
}

.hero-stage {
  position: relative;
  isolation: isolate;
  min-width: 0;
  min-height: 260px;
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(320px, 0.92fr);
  gap: 18px;
  align-items: stretch;
  overflow: hidden;
  background:
    linear-gradient(105deg, #123b36 0%, #1b5b4e 46%, #2e8d7a 86%, #f2c35d 150%);
  background-size: 180% 180%;
  color: #fff;
  animation: surface-in 520ms cubic-bezier(0.2, 0.8, 0.2, 1) both, hero-flow 9s ease-in-out infinite alternate;
}

.hero-stage::before {
  content: "";
  position: absolute;
  inset: 0;
  z-index: 0;
  background:
    linear-gradient(120deg, rgba(255, 255, 255, 0.12), transparent 38%),
    repeating-linear-gradient(90deg, rgba(255, 255, 255, 0.08) 0 1px, transparent 1px 54px),
    repeating-linear-gradient(0deg, rgba(255, 255, 255, 0.06) 0 1px, transparent 1px 54px);
  opacity: 0.7;
  pointer-events: none;
  transform: translate3d(0, 0, 0);
  animation: grid-drift 14s linear infinite;
}

.hero-copy {
  position: relative;
  z-index: 1;
  min-width: 0;
  padding: 28px;
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
  font-size: clamp(32px, 4vw, 54px);
  line-height: 1.05;
  overflow-wrap: anywhere;
}

.hero-actions {
  justify-content: flex-start;
  margin-top: 18px;
}

.hero-visual {
  position: relative;
  z-index: 1;
  min-width: 0;
  min-height: 244px;
  display: grid;
  align-content: center;
  gap: 10px;
  padding: 18px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.12), rgba(255, 255, 255, 0.04)),
    repeating-linear-gradient(90deg, rgba(255, 255, 255, 0.08) 0 1px, transparent 1px 48px);
}

.visual-ring-card,
.visual-data-grid,
.visual-bars-card {
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(12px);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.14);
  transition: transform 220ms ease, background 220ms ease;
}

.visual-ring-card:hover,
.visual-data-grid:hover,
.visual-bars-card:hover {
  transform: translateY(-3px);
  background: rgba(255, 255, 255, 0.16);
}

.visual-ring-card {
  display: grid;
  grid-template-columns: 98px minmax(0, 1fr);
  gap: 12px;
  align-items: center;
  padding: 12px;
}

.visual-ring-card > div {
  min-width: 0;
}

.progress-ring {
  position: relative;
  width: 98px;
  aspect-ratio: 1;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: conic-gradient(#85d4ff var(--progress), rgba(255, 255, 255, 0.18) 0);
  filter: drop-shadow(0 16px 26px rgba(133, 212, 255, 0.18));
  animation: ring-breathe 2.8s ease-in-out infinite;
}

.progress-ring::after {
  content: "";
  position: absolute;
  inset: 11px;
  border-radius: 50%;
  background: #1c5b50;
}

.progress-ring strong {
  position: relative;
  z-index: 1;
  font-size: 26px;
}

.visual-ring-card span,
.bar-head span,
.visual-data-grid span {
  display: block;
  color: rgba(255, 255, 255, 0.88);
  font-weight: 900;
  overflow-wrap: anywhere;
}

.visual-ring-card small,
.bar-head small,
.visual-data-grid small {
  display: block;
  margin-top: 4px;
  color: rgba(255, 255, 255, 0.66);
  overflow-wrap: anywhere;
}

.visual-data-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.visual-data-grid article {
  padding: 10px 12px;
  min-width: 0;
}

.visual-data-grid article + article {
  border-left: 1px solid rgba(255, 255, 255, 0.16);
}

.visual-data-grid strong {
  display: inline-block;
  margin-top: 6px;
  font-size: 22px;
  line-height: 1;
}

.visual-bars-card {
  padding: 12px;
}

.bar-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.mini-bars {
  height: 54px;
  display: flex;
  align-items: end;
  gap: 12px;
}

.mini-bars i {
  flex: 1;
  min-width: 18px;
  border-radius: 8px 8px 0 0;
  background: linear-gradient(180deg, #f2c35d, #85d4ff);
  transform-origin: bottom;
  animation: bar-rise 720ms cubic-bezier(0.18, 0.78, 0.24, 1) both;
}

.mini-bars i:nth-child(2) { animation-delay: 80ms; }
.mini-bars i:nth-child(3) { animation-delay: 150ms; }
.mini-bars i:nth-child(4) { animation-delay: 220ms; }
.mini-bars i:nth-child(5) { animation-delay: 290ms; }

.visual-empty {
  margin: 0;
  min-height: 54px;
  display: grid;
  place-items: center;
  border: 1px dashed rgba(255, 255, 255, 0.28);
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.68);
  font-weight: 800;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.metric-grid.small {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.metric-card {
  min-height: 92px;
  padding: 16px;
  display: grid;
  align-content: center;
  border-left-width: 5px;
  position: relative;
  overflow: hidden;
  transition: transform 220ms ease, box-shadow 220ms ease;
}

.metric-card::after {
  content: "";
  position: absolute;
  inset: auto 18px 14px 18px;
  height: 3px;
  border-radius: 999px;
  background: linear-gradient(90deg, #2c8f72, #85d4ff, #f2c35d);
  opacity: 0.34;
}

.metric-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 18px 45px rgba(31, 49, 42, 0.1);
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
  margin-top: 6px;
  font-size: 28px;
  line-height: 1;
}

.metric-card small {
  color: #c65f3d;
  font-weight: 800;
}

.dashboard-grid,
.home-content-grid,
.two-column,
.admin-grid,
.product-command-grid,
.product-flow-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.panel {
  padding: 22px;
  min-width: 0;
}

.overview-stack .panel {
  padding: 16px;
}

.panel-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.overview-stack .panel-heading {
  margin-bottom: 12px;
}

.panel-heading h2 {
  font-size: 24px;
}

.overview-stack .panel-heading h2 {
  font-size: 21px;
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

.overview-stack .nutrition-strip {
  margin-top: 10px;
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
  transition: transform 180ms ease, border-color 180ms ease, background 180ms ease, color 180ms ease;
}

.segmented button.active {
  background: #174f42;
  border-color: #174f42;
  color: #fff;
  box-shadow: 0 10px 24px rgba(23, 79, 66, 0.18);
}

.segmented button:hover {
  transform: translateY(-2px);
  border-color: #9cc8b7;
}

.action-brief .panel-heading {
  align-items: center;
}

.readiness-score {
  min-width: 74px;
  height: 74px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background:
    radial-gradient(circle at 50% 50%, #fff 0 56%, transparent 57%),
    conic-gradient(#2c8f72 calc(var(--score, 72) * 1%), #e7ece4 0);
  border: 1px solid #dfe6dc;
  color: #174f42;
  font-size: 24px;
  box-shadow: 0 16px 30px rgba(31, 49, 42, 0.08);
}

.overview-stack .readiness-score {
  min-width: 58px;
  height: 58px;
  font-size: 21px;
}

.brief-body {
  display: grid;
  gap: 14px;
}

.overview-stack .brief-body {
  gap: 10px;
}

.brief-body p {
  margin: 0;
  color: #52635b;
  line-height: 1.75;
}

.overview-stack .brief-body p,
.overview-stack .flow-copy,
.overview-stack .muted {
  line-height: 1.55;
}

.brief-meter,
.macro-grid i,
.plan-execution-bar i {
  height: 10px;
  border-radius: 999px;
  overflow: hidden;
  background: #edf0e8;
}

.brief-meter i,
.macro-grid b,
.plan-execution-bar b {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, #2c8f72, #85d4ff, #f2c35d);
  transition: width 520ms cubic-bezier(0.2, 0.8, 0.2, 1);
}

.brief-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.completion-pill {
  min-width: 74px;
  border-radius: 8px;
  padding: 10px 12px;
  background: #173f37;
  color: #fff;
  text-align: center;
  font-size: 20px;
  box-shadow: 0 14px 28px rgba(23, 63, 55, 0.14);
}

.flow-copy {
  margin: 0 0 16px;
}

.overview-stack .flow-copy {
  margin-bottom: 10px;
}

.onboarding-list,
.suggestion-list {
  display: grid;
  gap: 12px;
}

.overview-stack .onboarding-list,
.overview-stack .suggestion-list,
.overview-stack .feed-list,
.overview-stack .notice-list {
  gap: 8px;
}

.onboarding-list article,
.suggestion-list article {
  border: 1px solid #e0e6dc;
  border-radius: 8px;
  background: #fbfcf8;
  padding: 14px;
  transition: transform 190ms ease, border-color 190ms ease, box-shadow 190ms ease, background 190ms ease;
}

.overview-stack .onboarding-list article,
.overview-stack .suggestion-list article,
.overview-stack .feed-row,
.overview-stack .notice-list article {
  padding: 10px 12px;
}

.onboarding-list article {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr) auto;
  gap: 12px;
  align-items: center;
}

.onboarding-list article > .el-icon {
  width: 42px;
  height: 42px;
  border-radius: 8px;
  display: grid;
  place-items: center;
  background: #eaf6ef;
  color: #2c8f72;
  font-size: 20px;
}

.onboarding-list article.done {
  border-color: #bde0cf;
  background: #f4fbf6;
}

.onboarding-list strong,
.suggestion-list strong {
  color: #17211c;
}

.onboarding-list p,
.suggestion-list p {
  margin: 5px 0 0;
  color: #607169;
  line-height: 1.65;
}

.overview-stack .onboarding-list p,
.overview-stack .suggestion-list p,
.overview-stack .feed-row p {
  display: -webkit-box;
  overflow: hidden;
  line-height: 1.5;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.suggestion-list article {
  cursor: pointer;
}

.suggestion-list article:hover,
.onboarding-list article:hover {
  transform: translateY(-3px);
  border-color: #b8d3c8;
  background: #fff;
  box-shadow: 0 16px 30px rgba(31, 49, 42, 0.08);
}

.suggestion-list span {
  display: block;
  margin-bottom: 6px;
  color: #c65f3d;
  font-size: 12px;
  font-weight: 900;
}

.empty-mini {
  min-height: 220px;
  border: 1px dashed #d0dbd4;
  border-radius: 8px;
  background: #fbfcf8;
  color: #607169;
  display: grid;
  place-items: center;
  align-content: center;
  gap: 10px;
  text-align: center;
  font-weight: 800;
}

.overview-stack .empty-mini {
  min-height: 120px;
}

.empty-mini .el-icon {
  color: #8aa097;
  font-size: 32px;
}

.coverage-badge {
  border: 1px solid #dce2d8;
  border-radius: 8px;
  background: #f8faf5;
  color: #385046;
  padding: 8px 12px;
  font-weight: 900;
  white-space: nowrap;
}

.heatmap-grid {
  display: grid;
  grid-template-columns: repeat(35, minmax(8px, 1fr));
  gap: 5px;
  align-items: end;
}

.heatmap-grid span {
  min-height: 34px;
  border-radius: 6px;
  border: 1px solid #e0e6dc;
  background: #f1f4ee;
  transition: transform 180ms ease, border-color 180ms ease;
}

.overview-stack .heatmap-grid span {
  min-height: 24px;
}

.heatmap-grid span:hover {
  transform: translateY(-3px);
  border-color: #9cc8b7;
}

.heatmap-grid .level-1 { background: #dff4ea; }
.heatmap-grid .level-2 { background: #b9e5d3; }
.heatmap-grid .level-3 { background: #6fc19f; }
.heatmap-grid .level-4 { background: #2c8f72; }

.heatmap-grid.large {
  grid-template-columns: repeat(7, minmax(0, 1fr));
  gap: 10px;
}

.heatmap-grid.large span {
  min-height: 72px;
  display: flex;
  align-items: flex-end;
  padding: 8px;
}

.heatmap-grid.large small {
  color: rgba(23, 42, 36, 0.72);
  font-weight: 800;
}

.heatmap-grid.large .level-3 small,
.heatmap-grid.large .level-4 small {
  color: #fff;
}

.plan-execution-bar {
  display: grid;
  grid-template-columns: minmax(240px, 1.4fr) repeat(2, minmax(120px, 0.6fr)) auto;
  gap: 12px;
  align-items: stretch;
  margin-bottom: 18px;
}

.plan-execution-bar > div {
  border: 1px solid #dfe4da;
  border-radius: 8px;
  background: #fbfcf8;
  padding: 12px;
  display: grid;
  align-content: center;
  gap: 8px;
}

.plan-execution-bar span {
  color: #66766f;
  font-weight: 800;
}

.plan-execution-bar strong {
  color: #17211c;
  font-size: 18px;
}

.plan-coach-strip {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 18px;
}

.plan-coach-strip article {
  border: 1px solid #dfe4da;
  border-radius: 8px;
  background:
    linear-gradient(180deg, #fff, #f8fbf6);
  padding: 14px;
}

.plan-coach-strip span {
  display: block;
  color: #c65f3d;
  font-size: 12px;
  font-weight: 900;
}

.plan-coach-strip strong {
  display: block;
  margin-top: 6px;
  color: #17211c;
  font-size: 20px;
}

.plan-coach-strip p {
  margin: 8px 0 0;
  color: #607169;
  line-height: 1.65;
}

.progress-rows {
  display: grid;
  gap: 18px;
}

.overview-stack .progress-rows {
  gap: 12px;
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

.overview-stack .progress-rows i,
.overview-stack .brief-meter {
  height: 8px;
}

.progress-rows b {
  position: relative;
  display: block;
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, #2c8f72, #85d4ff);
  overflow: hidden;
  transition: width 520ms cubic-bezier(0.2, 0.8, 0.2, 1);
}

.progress-rows b::after {
  content: "";
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.5), transparent);
  transform: translateX(-100%);
  animation: progress-sheen 2.4s ease-in-out infinite;
}

.feed-list,
.notice-list,
.admin-list,
.timeline {
  display: grid;
  gap: 12px;
}

.overview-stack .feed-row h3 {
  font-size: 16px;
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
  transition: transform 190ms ease, border-color 190ms ease, box-shadow 190ms ease, background 190ms ease;
}

.feed-row:hover,
.notice-list article:hover,
.related-panel button:hover {
  transform: translateY(-3px);
  border-color: #bfd7cd;
  background: #fff;
  box-shadow: 0 16px 30px rgba(31, 49, 42, 0.08);
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

.task-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.notice-card,
.article-card,
.task-card {
  padding: 20px;
  display: grid;
  gap: 12px;
  cursor: pointer;
  transition: transform 220ms ease, border-color 220ms ease, box-shadow 220ms ease;
}

.notice-card:hover,
.article-card:hover {
  transform: translateY(-5px);
  border-color: #b8d3c8;
  box-shadow: 0 22px 50px rgba(31, 49, 42, 0.12);
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
  padding: 0;
  overflow: hidden;
  cursor: default;
  box-shadow: 0 14px 34px rgba(31, 49, 42, 0.07);
}

.task-card:hover {
  transform: translateY(-5px);
  border-color: #b7d1c7;
  box-shadow: 0 24px 58px rgba(31, 49, 42, 0.13);
}

.task-card.done {
  border-color: #85c9ad;
  background: linear-gradient(180deg, #fff, #f3fbf6);
}

.task-card.done .task-body {
  opacity: 0.82;
}

.task-checkbar {
  border-bottom: 1px solid #edf0ea;
  background: #f8faf5;
  padding: 10px 14px;
}

.result-media:hover img {
  transform: scale(1.035);
  filter: saturate(1.08) contrast(1.02);
}

.task-placeholder {
  min-height: 190px;
  display: grid;
  place-items: center;
  color: #8aa097;
  font-size: 42px;
}

.image-source {
  position: absolute;
  right: 10px;
  bottom: 10px;
  max-width: calc(100% - 20px);
  border: 1px solid rgba(255, 255, 255, 0.62);
  border-radius: 8px;
  padding: 6px 9px;
  background: rgba(23, 42, 36, 0.72);
  color: #fff;
  font-size: 12px;
  font-weight: 800;
  text-decoration: none;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  backdrop-filter: blur(10px);
  transition: transform 180ms ease, background 180ms ease;
}

.image-source:hover {
  transform: translateY(-2px);
  background: rgba(23, 42, 36, 0.88);
}

.task-body {
  padding: 18px;
  display: grid;
  gap: 12px;
}

.task-body span {
  color: #c65f3d;
  font-weight: 900;
}

.task-body h3,
.task-body p,
.guide-columns h4 {
  margin: 0;
}

.task-body h3 {
  font-size: 24px;
}

.task-dose {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.task-dose strong {
  border: 1px solid #dce2d8;
  border-radius: 8px;
  padding: 7px 10px;
  background: #f8faf5;
  color: #33433c;
  font-size: 13px;
}

.task-body p {
  color: #5b6c64;
  line-height: 1.7;
}

.guide-columns {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.guide-columns h4 {
  color: #173f37;
}

.guide-columns ol,
.guide-columns ul {
  margin: 8px 0 0;
  padding-left: 18px;
  color: #566860;
  line-height: 1.75;
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
  transition: transform 190ms ease, border-color 190ms ease, box-shadow 190ms ease, background 190ms ease;
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

.result-media {
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 16px;
  background:
    linear-gradient(135deg, #eef5ef, #f7f1df),
    repeating-linear-gradient(90deg, rgba(35, 66, 58, 0.07) 0 1px, transparent 1px 38px);
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
  width: 100%;
  max-height: 360px;
  object-fit: contain;
  border-radius: 8px;
  display: block;
  background: transparent;
  transition: transform 360ms ease, filter 360ms ease;
}

.guide-filter-row {
  margin-bottom: 18px;
}

.guide-library-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.guide-library-card {
  border: 1px solid #dfe4da;
  border-radius: 8px;
  background: #fff;
  overflow: hidden;
  cursor: pointer;
  display: grid;
  grid-template-rows: 180px minmax(0, 1fr);
  transition: transform 220ms ease, box-shadow 220ms ease, border-color 220ms ease;
}

.guide-library-card:hover {
  transform: translateY(-5px);
  border-color: #b7d1c7;
  box-shadow: 0 22px 50px rgba(31, 49, 42, 0.12);
}

.guide-library-card img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: linear-gradient(135deg, #eef5ef, #f7f1df);
  padding: 12px;
}

.guide-library-card > div:last-child {
  padding: 14px;
  display: grid;
  gap: 8px;
}

.guide-library-card span {
  color: #c65f3d;
  font-size: 12px;
  font-weight: 900;
}

.guide-library-card h3,
.guide-library-card p {
  margin: 0;
}

.guide-library-card p {
  color: #5b6c64;
  line-height: 1.65;
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

.insights-intro {
  background:
    linear-gradient(120deg, rgba(255, 255, 255, 0.96), rgba(236, 247, 241, 0.96)),
    repeating-linear-gradient(90deg, rgba(44, 143, 114, 0.08) 0 1px, transparent 1px 42px);
}

.coach-tip-list {
  display: grid;
  gap: 14px;
}

.coach-tip-list article {
  display: grid;
  grid-template-columns: 12px minmax(0, 1fr);
  gap: 12px;
  border: 1px solid #edf0ea;
  border-radius: 8px;
  background: #fbfcf8;
  padding: 14px;
}

.coach-tip-list span {
  width: 12px;
  min-height: 100%;
  border-radius: 999px;
  background: linear-gradient(180deg, #2c8f72, #85d4ff);
}

.coach-tip-list strong {
  color: #17211c;
}

.coach-tip-list p {
  margin: 6px 0 0;
  color: #5b6c64;
  line-height: 1.7;
}

.achievement-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.achievement-grid article {
  border: 1px solid #e0e6dc;
  border-radius: 8px;
  background: #fbfcf8;
  padding: 16px;
  display: grid;
  gap: 8px;
  transition: transform 190ms ease, border-color 190ms ease, box-shadow 190ms ease;
}

.achievement-grid article.done {
  border-color: #b9dccb;
  background: linear-gradient(180deg, #fff, #f1fbf5);
}

.achievement-grid article:hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 30px rgba(31, 49, 42, 0.08);
}

.achievement-grid span {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: #e9eee6;
  color: #8a9a92;
}

.achievement-grid article.done span {
  background: #2c8f72;
  color: #fff;
}

.achievement-grid strong {
  color: #17211c;
}

.achievement-grid small {
  color: #66766f;
  font-weight: 800;
}

.macro-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin: 18px 0;
}

.macro-grid article {
  border: 1px solid #dfe4da;
  border-radius: 8px;
  background: #fbfcf8;
  padding: 14px;
  display: grid;
  gap: 12px;
}

.macro-grid article div {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

.macro-grid span {
  color: #66766f;
  font-weight: 800;
}

.training-day-tip {
  border-left: 5px solid #2c8f72;
  border-radius: 8px;
  background: #f4faf6;
  padding: 14px 16px;
  color: #31443c;
  font-weight: 800;
  line-height: 1.7;
}

@keyframes surface-in {
  from {
    opacity: 0;
    transform: translateY(14px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes hero-flow {
  from { background-position: 0% 50%; }
  to { background-position: 100% 50%; }
}

@keyframes grid-drift {
  from { background-position: 0 0, 0 0, 0 0; }
  to { background-position: 0 0, 108px 0, 0 108px; }
}

@keyframes ring-breathe {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.035); }
}

@keyframes bar-rise {
  from {
    transform: scaleY(0.2);
    opacity: 0.35;
  }
  to {
    transform: scaleY(1);
    opacity: 1;
  }
}

@keyframes progress-sheen {
  0% { transform: translateX(-100%); }
  55%, 100% { transform: translateX(100%); }
}

@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    scroll-behavior: auto !important;
    transition-duration: 0.01ms !important;
  }
}

@media (max-width: 1180px) {
  .hero-stage {
    grid-template-columns: 1fr;
  }

  .hero-visual {
    min-height: 230px;
  }

  .notice-grid,
  .article-grid,
  .task-grid,
  .guide-library-grid {
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
  .product-command-grid,
  .product-flow-grid,
  .two-column,
  .admin-grid,
  .macro-grid,
  .reader-page,
  .guide-columns {
    grid-template-columns: 1fr;
  }

  .form-grid.compact {
    grid-template-columns: repeat(2, minmax(180px, 1fr));
  }

  .plan-execution-bar {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .plan-coach-strip,
  .achievement-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 820px) {
  .workspace-shell {
    grid-template-columns: 1fr;
  }

  .sidebar {
    position: sticky;
    z-index: 20;
    height: auto;
    padding: 10px 12px;
    gap: 10px;
    box-shadow: 0 10px 30px rgba(21, 42, 36, 0.16);
  }

  .brand {
    gap: 10px;
  }

  .brand-logo {
    width: 36px;
    height: 36px;
  }

  .brand small,
  .nav-list p,
  .sidebar-user {
    display: none;
  }

  .nav-list {
    display: flex;
    overflow-x: auto;
    gap: 8px;
    padding-bottom: 2px;
    scrollbar-width: none;
  }

  .nav-list section {
    min-width: 0;
    display: flex;
    gap: 8px;
  }

  .nav-list button {
    width: auto;
    min-width: 94px;
    min-height: 36px;
    flex: 0 0 auto;
    justify-content: center;
    padding: 0 10px;
  }

  .nav-list button.active,
  .nav-list button:hover {
    transform: none;
  }

  .main {
    padding: 18px;
  }

  .topbar h1 {
    font-size: 26px;
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

  .panel-heading > .el-button,
  .topbar-actions,
  .hero-actions {
    width: 100%;
  }

  .hero-actions .el-button,
  .topbar-actions .el-button {
    flex: 1 1 150px;
  }

  .panel-heading > .el-button {
    justify-content: center;
  }

  .metric-grid,
  .metric-grid.small,
  .notice-grid,
  .article-grid,
  .task-grid,
  .guide-library-grid,
  .plan-execution-bar,
  .plan-coach-strip,
  .achievement-grid,
  .form-grid,
  .form-grid.compact {
    grid-template-columns: 1fr;
  }

  .overview-stack .metric-grid {
    display: grid !important;
    grid-template-columns: repeat(2, minmax(0, 1fr)) !important;
  }

  .onboarding-list article {
    grid-template-columns: 42px minmax(0, 1fr);
  }

  .onboarding-list article .el-button {
    grid-column: 1 / -1;
  }

  .heatmap-grid {
    grid-template-columns: repeat(7, minmax(0, 1fr));
  }

  .hero-copy {
    padding: 28px;
  }

  .hero-visual {
    padding: 18px;
  }

  .visual-ring-card {
    grid-template-columns: 1fr;
    text-align: center;
  }

  .progress-ring {
    margin: 0 auto;
  }

  .bar-head {
    align-items: flex-start;
    flex-direction: column;
  }

  .mini-bars {
    gap: 8px;
  }

  .hero-copy h2,
  .page-intro h2 {
    font-size: 34px;
  }
}

@media (max-width: 520px) {
  .main {
    padding: 14px;
  }

  .sidebar {
    padding: 10px 12px;
  }

  .topbar h1 {
    font-size: 24px;
  }

  .hero-stage {
    min-height: 0;
  }

  .hero-copy {
    padding: 24px 18px;
  }

  .hero-copy h2,
  .page-intro h2 {
    font-size: 30px;
    line-height: 1.08;
  }

  .hero-visual {
    min-height: 0;
    padding: 14px;
  }

  .overview-stack .hero-visual {
    display: none;
  }

  .overview-stack > .metric-grid {
    display: grid !important;
    gap: 10px;
    grid-template-columns: repeat(2, minmax(0, 1fr)) !important;
  }

  .overview-stack .metric-card {
    min-height: 78px;
    padding: 12px;
  }

  .visual-ring-card,
  .visual-bars-card {
    padding: 14px;
  }

  .progress-ring {
    width: 88px;
  }

  .progress-ring strong {
    font-size: 24px;
  }

  .coverage-badge {
    width: 100%;
    white-space: normal;
    text-align: center;
  }

  .heatmap-grid span {
    min-height: 30px;
  }

  .heatmap-grid.large span {
    min-height: 54px;
    padding: 6px;
  }

  .feed-row {
    gap: 12px;
  }
}
</style>
