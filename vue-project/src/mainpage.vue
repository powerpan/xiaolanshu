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
  Timer,
  TrendCharts,
  Upload,
  User,
  View,
} from '@element-plus/icons-vue'
import AppSidebar from './components/layout/AppSidebar.vue'
import PageHeader from './components/layout/PageHeader.vue'
import HeatmapGrid from './components/common/HeatmapGrid.vue'
import MetricCard from './components/common/MetricCard.vue'
import AdminDashboard from './views/AdminDashboard.vue'
import UserDashboard from './views/UserDashboard.vue'
import { getToken } from './services/api'
import {
  addArticleApi,
  addNotice,
  deleteArticleById,
  deleteNoticeById,
  deleteNoticeDrafts,
  getArticleDetail,
  getArticles,
  getMyArticles,
  getNoticeDetail,
  getNoticeDrafts,
  getNotices,
  saveNoticeDraftApi,
  updateArticle,
  updateNotice,
} from './services/contentApi'
import {
  addExerciseGuide,
  deleteExerciseGuide,
  getExerciseGuide,
  listExerciseGuides,
  updateExerciseGuide,
  uploadExerciseGuideImage,
} from './services/exerciseGuideApi'
import {
  addEatingScenario,
  addFoodItem,
  addFoodReplacement,
  addMealTemplate,
  deleteEatingScenario,
  deleteFoodItem,
  deleteFoodReplacement,
  deleteMealTemplate,
  getAdminNutritionLibrary,
  getNutritionLibrary,
  getNutritionRecommendation,
  updateEatingScenario,
  updateFoodItem,
  updateFoodReplacement,
  updateMealTemplate,
} from './services/nutritionApi'
import {
  getActionTasks,
  getCheckinStats,
  getPlanAdjustments,
  getPlanTaskRecords,
  getSplitMode,
  getTodayCheckin,
  getTrainingCycle,
  getTrainingPlanInsight,
  replacePlanAction,
  savePlanTaskRecord,
  saveTodayCheckin,
} from './services/trainingApi'
import {
  addUser,
  deleteUserByUsername,
  getAllUsers,
  getCurrentUser,
  getUserProfile,
  updateCurrentUser,
  updateRegisterState,
  updateUser,
  updateUserProfile,
} from './services/userApi'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const activeView = ref('overview')
const contentAdminTab = ref('articles')
const nutritionAdminTab = ref('templates')

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
const guideEditVisible = ref(false)
const guideImageUploading = ref(false)
const todayCheckin = ref(null)
const checkinStats = ref(null)
const nutrition = ref(null)
const nutritionLibrary = ref(null)
const nutritionAdminLibrary = ref(null)
const planInsight = ref(null)
const trainingCycle = ref(null)
const planAdjustments = ref([])
const selectedArticle = ref(null)
const selectedNotice = ref(null)
const checkedTasks = ref({})

const isAdmin = computed(() => user.value?.identity === 'ADMIN')
const displayName = computed(() => user.value?.nickname || user.value?.username || '用户')
const greetingName = computed(() => displayName.value.replace(/\s/g, '') || '训练者')
const goalText = computed(() => isAdmin.value ? '管理端' : (profile.value?.fitnessGoal || '保持健康'))
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
const noticeEditVisible = ref(false)
const noticeEditForm = reactive({
  id: '',
  title: '',
  content: '',
})

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
  missingImageOnly: false,
  incompleteOnly: false,
  missingStepsOnly: false,
  missingTipsOnly: false,
  missingMistakesOnly: false,
  missingAlternativesOnly: false,
})

const guideAdminForm = reactive({
  id: '',
  actionPattern: '水平推',
  actionName: '',
  equipment: '徒手',
  description: '',
  steps: '',
  tips: '',
  primaryMuscles: '',
  secondaryMuscles: '',
  difficulty: '新手',
  contraindications: '',
  commonMistakes: '',
  suitableFor: '',
  alternatives: '',
  imageurl: '',
})

const guideEditForm = reactive({
  id: '',
  actionPattern: '水平推',
  actionName: '',
  equipment: '徒手',
  description: '',
  steps: '',
  tips: '',
  primaryMuscles: '',
  secondaryMuscles: '',
  difficulty: '新手',
  contraindications: '',
  commonMistakes: '',
  suitableFor: '',
  alternatives: '',
  imageurl: '',
})

const foodItemForm = reactive({
  id: '',
  name: '',
  category: '低脂蛋白',
  caloriesPer100g: 100,
  proteinPer100g: 10,
  carbohydratePer100g: 0,
  fatPer100g: 1,
  tags: '',
  scene: '通用',
})

const mealTemplateForm = reactive({
  id: '',
  name: '',
  mealType: '午餐',
  goal: '通用',
  scene: '通用',
  targetCalories: 600,
  description: '',
  foods: '',
})

const foodReplacementForm = reactive({
  id: '',
  sourceFood: '',
  replacementFood: '',
  reason: '',
  category: '低脂蛋白',
})

const eatingScenarioForm = reactive({
  id: '',
  name: '',
  goal: '通用',
  strategy: '',
  avoid: '',
  example: '',
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
const foodCategories = ['低脂蛋白', '高蛋白主食', '蔬菜', '水果', '饮品']
const mealTypes = ['早餐', '午餐', '晚餐', '加餐', '训练前后']
const eatingScenes = ['通用', '食堂', '便利店', '外卖', '家庭做饭']

const userViewKeys = new Set(['overview', 'profile', 'fitness', 'plan', 'guide', 'checkin', 'insights', 'nutrition', 'notices', 'articles', 'articleDetail', 'noticeDetail'])
const adminViewKeys = new Set(['contentAdmin', 'guideAdmin', 'nutritionAdmin', 'admin', 'articleDetail', 'noticeDetail'])

const navGroups = computed(() => {
  if (isAdmin.value) {
    return [
      {
        title: '管理',
        items: [
          { key: 'contentAdmin', label: '内容管理', icon: Setting },
          { key: 'guideAdmin', label: '动作库管理', icon: Guide },
          { key: 'nutritionAdmin', label: '饮食模板', icon: Food },
          { key: 'admin', label: '用户管理', icon: Management },
        ],
      },
    ]
  }
  return [
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
  ]
})

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
const checkedPlanDays = computed(() => new Set(
  recentCheckins.value
    .map((item) => Number(item.planDay || 0))
    .filter((day) => day > 0),
))
const timelineRecords = computed(() => recentCheckins.value.slice(0, 10))
const heroBars = computed(() => {
  const records = recentCheckins.value.slice(0, 5).reverse()
  return records.map((item) => Math.min(100, Math.max(18, Math.round(((item.durationMinutes || 0) / 60) * 100))))
})
const todayKey = computed(() => formatLocalDate(new Date()))
const planCheckKey = computed(() => `xl-plan-${user.value?.username || 'guest'}-${todayKey.value}-${selectedDay.value}`)
const completedTaskMap = computed(() => checkedTasks.value[planCheckKey.value] || {})
const completedTaskCount = computed(() => Object.values(completedTaskMap.value).filter(Boolean).length)
const firstLockedPlanDay = (day) => {
  const currentDay = Number(day || 1)
  for (let index = 1; index < currentDay; index += 1) {
    if (!checkedPlanDays.value.has(index)) return index
  }
  return null
}
const isPlanDayCheckinAvailable = (day) => firstLockedPlanDay(day) === null
const selectedPlanCheckinBlockedDay = computed(() => firstLockedPlanDay(selectedDay.value))
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
const adminQualityCards = computed(() => {
  const users = adminUsers.value || []
  const guides = guideLibrary.value || []
  const missingImage = guides.filter((item) => !item.imageurl).length
  const incomplete = guides.filter((item) => item.incomplete || Number(item.qualityScore || 0) < 85).length
  const avgQuality = guides.length
    ? Math.round(guides.reduce((sum, item) => sum + Number(item.qualityScore || 0), 0) / guides.length)
    : 0
  return [
    { label: '待审核用户', value: users.filter((item) => !item.registered).length, unit: '人', tone: 'orange' },
    { label: '动作完整度', value: avgQuality, unit: '%', tone: 'green' },
    { label: '待补图片', value: missingImage, unit: '项', tone: 'blue' },
    { label: '待补字段', value: incomplete, unit: '项', tone: 'ink' },
  ]
})
const nutritionAdminCounts = computed(() => ({
  templates: nutritionAdminLibrary.value?.mealTemplates?.length || 0,
  foods: nutritionAdminLibrary.value?.foodItems?.length || 0,
  replacements: nutritionAdminLibrary.value?.foodReplacements?.length || 0,
  scenarios: nutritionAdminLibrary.value?.eatingScenarios?.length || 0,
}))
const uniqueCompact = (items = [], limit = 8) => {
  const seen = new Set()
  return items
    .map((item) => String(item || '').trim())
    .filter((item) => {
      if (!item || seen.has(item)) return false
      seen.add(item)
      return true
    })
    .slice(0, limit)
}
const compactMealPlan = computed(() => {
  const templates = nutrition.value?.mealTemplates || []
  if (templates.length) return uniqueCompact(templates, 5)
  return uniqueCompact([
    ...(nutrition.value?.trainingDayMeals || []),
    ...(nutrition.value?.restDayMeals || []),
  ], 5)
})
const compactFoodPlan = computed(() => uniqueCompact([
  ...(nutrition.value?.foodChoices || []),
  ...(nutrition.value?.shoppingList || []),
], 6))
const compactReplacements = computed(() => {
  const managed = nutrition.value?.foodReplacements || []
  if (managed.length) return uniqueCompact(managed, 6)
  return uniqueCompact(nutrition.value?.replacements || [], 6)
})
const compactScenarioTips = computed(() => uniqueCompact(nutrition.value?.eatingScenarios || [], 4))
const compactNutritionNotes = computed(() => uniqueCompact([
  ...(nutrition.value?.mealTiming || []),
  ...(nutrition.value?.tips || []),
  ...(nutrition.value?.watchouts || []),
], 7))

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

const toggleTaskDone = async (index, value) => {
  const lockedDay = firstLockedPlanDay(selectedDay.value)
  if (lockedDay) {
    ElMessage.warning(`请先完成第 ${lockedDay} 天训练打卡，再记录第 ${selectedDay.value} 天动作`)
    return
  }
  const completed = Boolean(value)
  const current = { ...completedTaskMap.value, [index]: completed }
  checkedTasks.value = {
    ...checkedTasks.value,
    [planCheckKey.value]: current,
  }
  persistTaskState()
  const task = actionTasks.value[index] || {}
  try {
    await savePlanTaskRecord({
      daytime: selectedDay.value,
      actionIndex: index,
      actionPattern: task.actionPattern || '',
      actionName: task.actionName || '',
      equipment: task.equipment || profile.value?.equipment || '',
      completed,
    })
    await loadPlanInsight()
  } catch (error) {
    ElMessage.error(error.message || '动作完成记录保存失败')
  }
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
    password: '',
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
  user.value = await getCurrentUser()
  setProfileForm()
}

const loadProfile = async () => {
  profile.value = await getUserProfile()
  setFitnessForm()
}

const loadNotices = async () => {
  notices.value = await getNotices()
}

const loadUsers = async () => {
  if (!isAdmin.value) return
  adminUsers.value = await getAllUsers()
}

const loadArticles = async () => {
  articles.value = await getArticles({
    title: articleSearch.title,
    topic: articleSearch.topic,
  })
}

const loadMyArticles = async () => {
  myArticles.value = await getMyArticles()
}

const loadPlan = async (day = selectedDay.value) => {
  if (!profile.value) return
  selectedDay.value = day
  const [mode, tasks, cycle, adjustments] = await Promise.all([
    getSplitMode(day),
    getActionTasks(day),
    getTrainingCycle(),
    getPlanAdjustments(),
  ])
  splitMode.value = mode
  actionTasks.value = tasks || []
  trainingCycle.value = cycle
  planAdjustments.value = adjustments || []
  await Promise.allSettled([loadPlanTaskRecords(day), loadPlanInsight()])
}

const openPlanDay = async (day) => {
  await loadPlan(day)
}

const loadPlanTaskRecords = async (day = selectedDay.value) => {
  const records = await getPlanTaskRecords(day)
  const current = {}
  ;(records || []).forEach((record) => {
    current[record.actionIndex] = Boolean(record.completed)
  })
  checkedTasks.value = {
    ...checkedTasks.value,
    [planCheckKey.value]: current,
  }
  persistTaskState()
}

const loadPlanInsight = async () => {
  planInsight.value = await getTrainingPlanInsight()
  if (planInsight.value?.adjustmentHistory) {
    planAdjustments.value = planInsight.value.adjustmentHistory
  }
}

const replaceTaskWithGuide = async (index, guide = null) => {
  const current = actionTasks.value[index]
  if (!current) return
  try {
    const replacement = guide || await replacePlanAction({
      actionPattern: current.actionPattern,
      preferredEquipment: profile.value?.equipment || current.equipment,
      currentGuideId: current.id,
    })
    actionTasks.value = actionTasks.value.map((task, taskIndex) => {
      if (taskIndex !== index) return task
      return {
        ...task,
        actionName: replacement.actionName || replacement.actionPattern,
        equipment: replacement.equipment || task.equipment,
        guideDescription: replacement.description || task.guideDescription,
        description: replacement.description || task.description,
        steps: replacement.steps || task.steps,
        tips: replacement.tips || task.tips,
        imageurl: replacement.imageurl || '',
        imageCredit: replacement.imageCredit || '',
        imageSourceUrl: replacement.imageSourceUrl || '',
        primaryMuscles: replacement.primaryMuscles || task.primaryMuscles,
        secondaryMuscles: replacement.secondaryMuscles || task.secondaryMuscles,
        difficulty: replacement.difficulty || task.difficulty,
        contraindications: replacement.contraindications || task.contraindications,
        commonMistakes: replacement.commonMistakes || task.commonMistakes,
        suitableFor: replacement.suitableFor || task.suitableFor,
        alternatives: replacement.alternatives || task.alternatives,
      }
    })
    ElMessage.success('已替换为同动作模式的可选动作')
  } catch (error) {
    ElMessage.error(error.message || '暂无可替换动作')
  }
}

const loadGuide = async () => {
  const result = await getExerciseGuide({
    actionPattern: guideForm.actionPattern,
    equipment: guideForm.equipment,
  })
  guideResult.value = withExerciseMediaState(result)
}

const loadGuideLibrary = async () => {
  guideLibraryLoading.value = true
  try {
    guideLibrary.value = await listExerciseGuides(guideFilters)
  } finally {
    guideLibraryLoading.value = false
  }
}

const clearGuideFilters = async () => {
  Object.assign(guideFilters, {
    actionPattern: '',
    equipment: '',
    missingImageOnly: false,
    incompleteOnly: false,
    missingStepsOnly: false,
    missingTipsOnly: false,
    missingMistakesOnly: false,
    missingAlternativesOnly: false,
  })
  await loadGuideLibrary()
}

const openGuideFromLibrary = (item) => {
  guideResult.value = withExerciseMediaState(item)
  Object.assign(guideForm, {
    actionPattern: item.actionPattern || guideForm.actionPattern,
    equipment: item.equipment || guideForm.equipment,
  })
}

const resetGuideAdminForm = () => {
  Object.assign(guideAdminForm, {
    id: '',
    actionPattern: '水平推',
    actionName: '',
    equipment: '徒手',
    description: '',
    steps: '',
    tips: '',
    primaryMuscles: '',
    secondaryMuscles: '',
    difficulty: '新手',
    contraindications: '',
    commonMistakes: '',
    suitableFor: '',
    alternatives: '',
    imageurl: '',
  })
}

const openGuideAdminEdit = (item) => {
  Object.assign(guideEditForm, {
    id: item.id,
    actionPattern: item.actionPattern || '水平推',
    actionName: item.actionName || '',
    equipment: item.equipment || '徒手',
    description: item.description || '',
    steps: item.steps || '',
    tips: item.tips || '',
    primaryMuscles: item.primaryMuscles || '',
    secondaryMuscles: item.secondaryMuscles || '',
    difficulty: item.difficulty || '新手',
    contraindications: item.contraindications || '',
    commonMistakes: item.commonMistakes || '',
    suitableFor: item.suitableFor || '',
    alternatives: item.alternatives || '',
    imageurl: item.imageurl || '',
  })
  guideEditVisible.value = true
}

const saveGuideAdmin = async () => {
  try {
    if (!isAdmin.value) {
      ElMessage.warning('该操作仅管理员可用')
      return
    }
    const payload = {
      ...guideAdminForm,
      imageCredit: guideAdminForm.imageurl ? '管理员上传' : '',
      imageSourceUrl: '',
    }
    await addExerciseGuide(payload)
    ElMessage.success('动作指导已添加')
    resetGuideAdminForm()
    await loadGuideLibrary()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const saveGuideEdit = async () => {
  try {
    if (!isAdmin.value) {
      ElMessage.warning('该操作仅管理员可用')
      return
    }
    await updateExerciseGuide({
      ...guideEditForm,
      imageCredit: guideEditForm.imageurl ? '管理员上传' : '',
      imageSourceUrl: '',
    })
    ElMessage.success('动作指导已更新')
    guideEditVisible.value = false
    await loadGuideLibrary()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const deleteGuideAdmin = async (item) => {
  try {
    await ElMessageBox.confirm(`确认删除动作「${item.actionName || item.actionPattern}」？`, '删除确认', { type: 'warning' })
    await deleteExerciseGuide(item.id)
    ElMessage.success('动作指导已删除')
    await loadGuideLibrary()
    if (guideEditForm.id === item.id) guideEditVisible.value = false
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '操作取消')
  }
}

const uploadGuideImageTo = async (targetForm, { file, onSuccess, onError }) => {
  guideImageUploading.value = true
  try {
    const imageUrl = await uploadExerciseGuideImage(file)
    targetForm.imageurl = imageUrl
    ElMessage.success('图片已上传，保存动作后生效')
    onSuccess?.({ url: imageUrl })
  } catch (error) {
    ElMessage.error(error.message || '图片上传失败')
    onError?.(error)
  } finally {
    guideImageUploading.value = false
  }
}

const uploadGuideImage = (options) => uploadGuideImageTo(guideAdminForm, options)
const uploadGuideEditImage = (options) => uploadGuideImageTo(guideEditForm, options)

const clearGuideAdminImage = () => {
  guideAdminForm.imageurl = ''
}

const clearGuideEditImage = () => {
  guideEditForm.imageurl = ''
}

const openSuggestedGuide = (item) => {
  if (isAdmin.value) return
  openGuideFromLibrary(item)
  activeView.value = 'guide'
  if (route.name !== 'mainpage') {
    router.replace('/mainpage')
  }
}

const loadCheckin = async () => {
  const [today, stats] = await Promise.all([
    getTodayCheckin(),
    getCheckinStats(),
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
  const [recommendation, library] = await Promise.all([
    getNutritionRecommendation(),
    getNutritionLibrary(),
  ])
  nutrition.value = recommendation
  nutritionLibrary.value = library
}

const loadNutritionAdmin = async () => {
  nutritionAdminLibrary.value = await getAdminNutritionLibrary()
}

const loadArticleDetail = async (id) => {
  selectedArticle.value = await getArticleDetail(id)
  activeView.value = 'articleDetail'
}

const loadNoticeDetail = async (id) => {
  selectedNotice.value = await getNoticeDetail(id)
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
    activeView.value = isAdmin.value ? 'contentAdmin' : 'articles'
    router.replace('/mainpage')
  }
}

const bootstrap = async () => {
  if (!ensureToken()) return
  loading.value = true
  try {
    await loadUser()
    if (isAdmin.value) {
      activeView.value = 'contentAdmin'
      await Promise.allSettled([
        loadNotices(),
        loadArticles(),
        loadMyArticles(),
        loadUsers(),
        loadGuideLibrary(),
        loadNutritionAdmin(),
      ])
      await syncRouteDetail()
      return
    }
    await loadProfile()
    const tasks = [
      loadNotices(),
      loadArticles(),
      loadCheckin(),
      loadNutrition(),
      loadGuideLibrary(),
    ]
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
  if (!isAdmin.value && !userViewKeys.has(key)) {
    ElMessage.warning('该区域仅管理员可见')
    return
  }
  if (isAdmin.value && !adminViewKeys.has(key)) {
    ElMessage.warning('管理员端仅显示管理功能')
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
    if (key === 'guideAdmin') await loadGuideLibrary()
    if (key === 'nutritionAdmin') await loadNutritionAdmin()
    if (key === 'notices') await loadNotices()
    if (key === 'articles') await loadArticles()
    if (key === 'plan') await loadPlan(selectedDay.value || 1)
    if (key === 'guide') await loadGuideLibrary()
    if (key === 'checkin') await Promise.all([loadCheckin(), loadPlanInsight()])
    if (key === 'insights') await Promise.all([loadCheckin(), loadPlanInsight()])
    if (key === 'nutrition') await loadNutrition()
  } catch (error) {
    ElMessage.error(error.message || '加载失败')
  }
}

const openIdentityEntry = async () => {
  await openView(isAdmin.value ? 'admin' : 'profile')
}

const saveProfile = async () => {
  try {
    await updateCurrentUser({
      username: profileForm.username,
      nickname: profileForm.nickname,
      password: profileForm.password,
      userpic: user.value?.userpic || '',
      identity: user.value?.identity || 'user',
      specialty: profileForm.specialty,
      height: profileForm.height,
      weight: profileForm.weight,
    })
    ElMessage.success('个人资料已更新')
    await loadUser()
    await loadNutrition()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const saveFitnessProfile = async () => {
  try {
    await updateUserProfile({
      username: fitnessForm.username,
      fitnessGoal: fitnessForm.fitnessGoal,
      weeklyFrequency: fitnessForm.weeklyFrequency,
      equipment: fitnessForm.equipment,
      exLevel: fitnessForm.exLevel,
    })
    ElMessage.success('健身需求已更新')
    await loadProfile()
    await Promise.allSettled([loadPlan(1), loadNutrition()])
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const addAdminUser = async () => {
  try {
    await addUser(adminAddForm)
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
    password: '',
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
    await updateUser(adminEditForm)
    ElMessage.success('用户信息已更新')
    adminEditVisible.value = false
    await loadUsers()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const toggleRegister = async (row) => {
  try {
    await updateRegisterState({
      username: row.username,
      registered: !row.registered,
    })
    ElMessage.success('审核状态已更新')
    await loadUsers()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const deleteUser = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除用户 ${row.username}？`, '删除确认', { type: 'warning' })
    await deleteUserByUsername(row.username)
    ElMessage.success('用户已删除')
    await loadUsers()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '操作取消')
  }
}

const resetNoticeForm = () => {
  Object.assign(noticeForm, { title: '', content: '' })
}

const editNotice = (row) => {
  Object.assign(noticeEditForm, { id: row.id, title: row.title, content: row.content })
  noticeEditVisible.value = true
}

const saveNotice = async () => {
  try {
    if (!isAdmin.value) {
      ElMessage.warning('该操作仅管理员可用')
      return
    }
    await addNotice(noticeForm)
    ElMessage.success('公告已发布')
    resetNoticeForm()
    await loadNotices()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const saveNoticeEdit = async () => {
  try {
    if (!isAdmin.value) {
      ElMessage.warning('该操作仅管理员可用')
      return
    }
    await updateNotice({
      id: noticeEditForm.id,
      title: noticeEditForm.title,
      content: noticeEditForm.content,
    })
    ElMessage.success('公告已更新')
    noticeEditVisible.value = false
    await loadNotices()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const deleteNotice = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除这条公告？', '删除确认', { type: 'warning' })
    await deleteNoticeById(row.id)
    ElMessage.success('公告已删除')
    await loadNotices()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '操作取消')
  }
}

const saveNoticeDraft = async () => {
  try {
    await saveNoticeDraftApi(noticeForm)
    ElMessage.success('草稿已保存')
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const loadNoticeDraft = async () => {
  try {
    const drafts = await getNoticeDrafts()
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
    await deleteNoticeDrafts()
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
    await addArticleApi(articleForm)
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
  if (isAdmin.value && (target === 'articles' || target === 'notices')) {
    contentAdminTab.value = target
    activeView.value = 'contentAdmin'
    return
  }
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
    await updateArticle(articleEditForm)
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
    await deleteArticleById({ id: row.id, title: row.title, content: row.content })
    ElMessage.success('文章已删除')
    await Promise.all([loadArticles(), loadMyArticles()])
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '操作取消')
  }
}

const saveCheckin = async (payload = checkinForm) => {
  try {
    await saveTodayCheckin(payload)
    ElMessage.success('今日打卡已保存')
    await loadCheckin()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const resetFoodItemForm = () => {
  Object.assign(foodItemForm, {
    id: '',
    name: '',
    category: '低脂蛋白',
    caloriesPer100g: 100,
    proteinPer100g: 10,
    carbohydratePer100g: 0,
    fatPer100g: 1,
    tags: '',
    scene: '通用',
  })
}

const resetMealTemplateForm = () => {
  Object.assign(mealTemplateForm, {
    id: '',
    name: '',
    mealType: '午餐',
    goal: '通用',
    scene: '通用',
    targetCalories: 600,
    description: '',
    foods: '',
  })
}

const resetFoodReplacementForm = () => {
  Object.assign(foodReplacementForm, {
    id: '',
    sourceFood: '',
    replacementFood: '',
    reason: '',
    category: '低脂蛋白',
  })
}

const resetEatingScenarioForm = () => {
  Object.assign(eatingScenarioForm, {
    id: '',
    name: '',
    goal: '通用',
    strategy: '',
    avoid: '',
    example: '',
  })
}

const editFoodItem = (item) => Object.assign(foodItemForm, { ...item })
const editMealTemplate = (item) => Object.assign(mealTemplateForm, { ...item })
const editFoodReplacement = (item) => Object.assign(foodReplacementForm, { ...item })
const editEatingScenario = (item) => Object.assign(eatingScenarioForm, { ...item })

const saveFoodItem = async () => {
  try {
    if (foodItemForm.id) await updateFoodItem(foodItemForm)
    else await addFoodItem(foodItemForm)
    ElMessage.success('食材已保存')
    resetFoodItemForm()
    await loadNutritionAdmin()
  } catch (error) {
    ElMessage.error(error.message || '食材保存失败')
  }
}

const saveMealTemplate = async () => {
  try {
    if (mealTemplateForm.id) await updateMealTemplate(mealTemplateForm)
    else await addMealTemplate(mealTemplateForm)
    ElMessage.success('餐单模板已保存')
    resetMealTemplateForm()
    await loadNutritionAdmin()
  } catch (error) {
    ElMessage.error(error.message || '餐单模板保存失败')
  }
}

const saveFoodReplacement = async () => {
  try {
    if (foodReplacementForm.id) await updateFoodReplacement(foodReplacementForm)
    else await addFoodReplacement(foodReplacementForm)
    ElMessage.success('替换规则已保存')
    resetFoodReplacementForm()
    await loadNutritionAdmin()
  } catch (error) {
    ElMessage.error(error.message || '替换规则保存失败')
  }
}

const saveEatingScenario = async () => {
  try {
    if (eatingScenarioForm.id) await updateEatingScenario(eatingScenarioForm)
    else await addEatingScenario(eatingScenarioForm)
    ElMessage.success('饮食场景已保存')
    resetEatingScenarioForm()
    await loadNutritionAdmin()
  } catch (error) {
    ElMessage.error(error.message || '饮食场景保存失败')
  }
}

const removeFoodItem = async (item) => {
  try {
    await ElMessageBox.confirm(`确认删除食材「${item.name}」？`, '删除确认', { type: 'warning' })
    await deleteFoodItem(item.id)
    await loadNutritionAdmin()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '删除失败')
  }
}

const removeMealTemplate = async (item) => {
  try {
    await ElMessageBox.confirm(`确认删除餐单「${item.name}」？`, '删除确认', { type: 'warning' })
    await deleteMealTemplate(item.id)
    await loadNutritionAdmin()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '删除失败')
  }
}

const removeFoodReplacement = async (item) => {
  try {
    await ElMessageBox.confirm(`确认删除「${item.sourceFood}」替换规则？`, '删除确认', { type: 'warning' })
    await deleteFoodReplacement(item.id)
    await loadNutritionAdmin()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '删除失败')
  }
}

const removeEatingScenario = async (item) => {
  try {
    await ElMessageBox.confirm(`确认删除场景「${item.name}」？`, '删除确认', { type: 'warning' })
    await deleteEatingScenario(item.id)
    await loadNutritionAdmin()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '删除失败')
  }
}

const savePlanCheckin = async () => {
  const lockedDay = firstLockedPlanDay(selectedDay.value)
  if (lockedDay) {
    ElMessage.warning(`请先完成第 ${lockedDay} 天训练打卡，再打卡第 ${selectedDay.value} 天`)
    return
  }
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
  await saveCheckin({ ...checkinForm, planDay: selectedDay.value })
  await loadPlanInsight()
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
    <AppSidebar
      :nav-groups="navGroups"
      :active-view="activeView"
      :is-admin="isAdmin"
      :display-name="displayName"
      :goal-text="goalText"
      :weekly-frequency="weeklyFrequency"
      @open-view="openView"
    />

    <main class="main">
      <PageHeader
        :is-admin="isAdmin"
        :greeting-name="greetingName"
        @open-profile="openIdentityEntry"
        @logout="logout"
      />

      <UserDashboard v-if="!isAdmin || activeView === 'noticeDetail' || activeView === 'articleDetail'">
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
          <MetricCard
            v-for="card in overviewCards"
            :key="card.label"
            :label="card.label"
            :value="card.value"
            :unit="card.unit"
            :tone="card.tone"
          />
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
            <HeatmapGrid :days="heatmapDays" compact />
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
              <button
                v-for="day in weeklyDays"
                :key="day"
                :class="{ active: selectedDay === day, 'checkin-locked': !isPlanDayCheckinAvailable(day) }"
                @click="openPlanDay(day)"
              >
                {{ day }}
              </button>
            </div>
          </div>
          <p v-if="selectedPlanCheckinBlockedDay" class="plan-lock-hint">
            可提前查看第 {{ selectedDay }} 天训练计划；完成第 {{ selectedPlanCheckinBlockedDay }} 天打卡后，才可打卡第 {{ selectedDay }} 天。
          </p>
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
            <el-button type="primary" :icon="Check" :disabled="!!selectedPlanCheckinBlockedDay" @click="savePlanCheckin">按完成情况打卡</el-button>
          </div>
          <div class="cycle-grid" v-if="trainingCycle || planInsight">
            <article>
              <span>当前周期</span>
              <strong>第 {{ planInsight?.cycleWeek || trainingCycle?.cycleWeek || 1 }} 周</strong>
              <p>{{ planInsight?.stage || trainingCycle?.stage || '基础适应期' }}</p>
            </article>
            <article>
              <span>周期目标</span>
              <strong>{{ trainingCycle?.status === 'active' ? '进行中' : '未开始' }}</strong>
              <p>{{ planInsight?.cycleGoal || trainingCycle?.cycleGoal || '保持每周训练节奏并记录动作质量。' }}</p>
            </article>
            <article>
              <span>恢复安排</span>
              <strong>第 {{ planInsight?.recoveryDay || trainingCycle?.recoveryDay || 4 }} 天</strong>
              <p>恢复日优先做低强度活动、拉伸和睡眠补足。</p>
            </article>
          </div>
          <details class="detail-disclosure plan-secondary">
            <summary>
              <span>计划反馈与执行建议</span>
              <strong>{{ planInsight?.nextAdjustment || '查看本周反馈' }}</strong>
            </summary>
            <div class="plan-insight-grid" v-if="planInsight">
              <article>
                <span>本周动作完成率</span>
                <strong>{{ planInsight.completionRate || 0 }}%</strong>
                <p>{{ planInsight.completedTasks || 0 }}/{{ planInsight.recordedTasks || 0 }} 个动作已记录完成</p>
              </article>
              <article>
                <span>调整建议</span>
                <strong>{{ planInsight.nextAdjustment || '保持当前计划' }}</strong>
                <p>{{ planInsight.recommendation }}</p>
              </article>
              <article>
                <span>恢复提醒</span>
                <strong>连续 {{ planInsight.currentStreak || 0 }} 天</strong>
                <p>{{ planInsight.recoveryHint }}</p>
              </article>
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
            <div class="adjustment-list" v-if="planAdjustments.length">
              <article v-for="item in planAdjustments" :key="item.id">
                <span>{{ item.adjustmentType || '计划调整' }}</span>
                <strong>{{ item.reason }}</strong>
                <p>{{ item.summary }}</p>
              </article>
            </div>
          </details>
          <div class="task-grid">
            <article
              v-for="(task, index) in actionTasks"
              :key="`${task.actionPattern}-${task.description}`"
              class="task-card"
              :class="{ done: isTaskDone(index) }"
            >
              <div class="task-checkbar">
                <el-checkbox :model-value="isTaskDone(index)" :disabled="!!selectedPlanCheckinBlockedDay" @change="(value) => toggleTaskDone(index, value)">
                  动作 {{ index + 1 }} 已完成
                </el-checkbox>
              </div>
              <div class="task-body">
                <span>{{ task.actionPattern }}</span>
                <h3>{{ task.actionName || task.actionPattern }}</h3>
                <div class="task-tags">
                  <em>{{ task.equipment || profile?.equipment || '推荐器材' }}</em>
                  <em>{{ task.targetArea || '基础训练' }}</em>
                  <em v-if="task.difficulty">{{ task.difficulty }}</em>
                  <em v-if="task.intensityLevel">{{ task.intensityLevel }}</em>
                </div>
                <div class="task-dose">
                  <strong>{{ task.minSets }}-{{ task.maxSets }} 组</strong>
                  <strong>{{ task.minReps }}-{{ task.maxReps }} 次/秒</strong>
                  <strong>休息 {{ task.minRestSeconds }}-{{ task.maxRestSeconds }} 秒</strong>
                </div>
                <p>{{ task.guideDescription || task.description }}</p>
                <details class="detail-disclosure task-secondary">
                  <summary>
                    <span>动作细节</span>
                    <strong>{{ task.primaryMuscles || task.trainingFocus || '步骤、要点和风险提示' }}</strong>
                  </summary>
                  <p v-if="task.planReason" class="task-coach-note">{{ task.planReason }}</p>
                  <p v-if="task.trainingFocus" class="task-coach-note">{{ task.trainingFocus }}</p>
                  <p v-if="task.progressionAdvice" class="task-coach-note">{{ task.progressionAdvice }}</p>
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
                  <div class="guide-quality-grid" v-if="task.primaryMuscles || task.commonMistakes || task.contraindications || task.suitableFor">
                    <article v-if="task.primaryMuscles">
                      <span>主肌群</span>
                      <p>{{ task.primaryMuscles }}<template v-if="task.secondaryMuscles"> · 辅助：{{ task.secondaryMuscles }}</template></p>
                    </article>
                    <article v-if="task.commonMistakes">
                      <span>常见错误</span>
                      <p>{{ task.commonMistakes }}</p>
                    </article>
                    <article v-if="task.contraindications">
                      <span>注意禁忌</span>
                      <p>{{ task.contraindications }}</p>
                    </article>
                    <article v-if="task.suitableFor">
                      <span>适合人群</span>
                      <p>{{ task.suitableFor }}</p>
                    </article>
                  </div>
                  <p v-if="task.alternative" class="task-alternative">{{ task.alternative }}</p>
                  <p v-if="task.alternatives" class="task-alternative">动作库替代：{{ task.alternatives }}</p>
                  <div class="replacement-actions" v-if="task.alternativeGuides?.length">
                    <span>可替换为</span>
                    <el-button
                      v-for="guide in task.alternativeGuides"
                      :key="`${guide.id}-${guide.actionName}`"
                      size="small"
                      plain
                      @click="replaceTaskWithGuide(index, guide)"
                    >
                      {{ guide.actionName || guide.actionPattern }} · {{ guide.equipment }}
                    </el-button>
                  </div>
                </details>
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
            <div class="detail-tag-grid" v-if="guideResult.difficulty || guideResult.primaryMuscles || guideResult.secondaryMuscles">
              <span v-if="guideResult.difficulty">难度：{{ guideResult.difficulty }}</span>
              <span v-if="guideResult.primaryMuscles">主肌群：{{ guideResult.primaryMuscles }}</span>
              <span v-if="guideResult.secondaryMuscles">辅助：{{ guideResult.secondaryMuscles }}</span>
            </div>
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
            <div class="guide-quality-grid" v-if="guideResult.commonMistakes || guideResult.contraindications || guideResult.suitableFor || guideResult.alternatives">
              <article v-if="guideResult.commonMistakes">
                <span>常见错误</span>
                <p>{{ guideResult.commonMistakes }}</p>
              </article>
              <article v-if="guideResult.contraindications">
                <span>禁忌提醒</span>
                <p>{{ guideResult.contraindications }}</p>
              </article>
              <article v-if="guideResult.suitableFor">
                <span>适合人群</span>
                <p>{{ guideResult.suitableFor }}</p>
              </article>
              <article v-if="guideResult.alternatives">
                <span>替代动作</span>
                <p>{{ guideResult.alternatives }}</p>
              </article>
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
            <label class="checkbox-field"><span>图片状态</span><el-checkbox v-model="guideFilters.missingImageOnly">只看未自定义图片</el-checkbox></label>
            <label class="checkbox-field"><span>完整度</span><el-checkbox v-model="guideFilters.incompleteOnly">只看不完整</el-checkbox></label>
            <label class="checkbox-field"><span>步骤</span><el-checkbox v-model="guideFilters.missingStepsOnly">缺步骤</el-checkbox></label>
            <label class="checkbox-field"><span>要点</span><el-checkbox v-model="guideFilters.missingTipsOnly">缺要点</el-checkbox></label>
            <label class="checkbox-field"><span>错误提示</span><el-checkbox v-model="guideFilters.missingMistakesOnly">缺常见错误</el-checkbox></label>
            <label class="checkbox-field"><span>替代动作</span><el-checkbox v-model="guideFilters.missingAlternativesOnly">缺替代动作</el-checkbox></label>
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
                <small>{{ item.difficulty || '未标难度' }} · {{ item.primaryMuscles || '未标肌群' }}</small>
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
          <MetricCard label="累计天数" :value="checkinStats?.totalDays || 0" unit="天" tone="green" />
          <MetricCard label="连续打卡" :value="checkinStats?.currentStreak || 0" unit="天" tone="orange" />
          <MetricCard label="累计时长" :value="checkinStats?.totalMinutes || 0" unit="分钟" tone="blue" />
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
          <MetricCard label="准备度" :value="readinessScore" unit="/100" tone="green" />
          <MetricCard label="最佳连续" :value="checkinStats?.bestStreak || 0" unit="天" tone="orange" />
          <MetricCard label="平均时长" :value="checkinStats?.averageMinutes || 0" unit="分钟" tone="blue" />
        </div>
        <div class="dashboard-grid">
          <section class="panel heatmap-panel expanded">
            <div class="panel-heading">
              <div>
                <p>执行日历</p>
                <h2>最近 35 天训练热力</h2>
              </div>
            </div>
            <HeatmapGrid :days="heatmapDays" large />
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

      <section v-if="activeView === 'nutrition'" class="content-stack">
        <div class="panel">
          <div class="panel-heading">
            <div>
              <p>营养策略</p>
              <h2>饮食建议</h2>
            </div>
            <el-button :icon="Refresh" @click="loadNutrition">刷新建议</el-button>
          </div>
          <div class="metric-grid small">
            <MetricCard label="目标热量" :value="nutrition?.targetCalories || 0" unit="kcal" tone="green" />
            <MetricCard label="蛋白质" :value="nutrition?.proteinGrams || 0" unit="g" tone="orange" />
            <MetricCard label="饮水" :value="nutrition?.waterMl || 0" unit="ml" tone="blue" />
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
          <p class="preference-summary" v-if="nutrition?.preferenceSummary">{{ nutrition.preferenceSummary }}</p>
          <div class="nutrition-tip-grid">
            <p class="training-day-tip">{{ nutrition?.trainingDayTip }}</p>
            <p class="training-day-tip rest">{{ nutrition?.restDayTip }}</p>
          </div>
          <details class="detail-disclosure nutrition-secondary" open>
            <summary>
              <span>推荐餐单</span>
              <strong>{{ compactMealPlan.length }} 条可执行模板</strong>
            </summary>
            <div class="reader-box">
              <ul class="clean-list"><li v-for="item in compactMealPlan" :key="item">{{ item }}</li></ul>
            </div>
          </details>
          <details class="detail-disclosure nutrition-secondary">
            <summary>
              <span>食材与替换</span>
              <strong>采购重点和同类替换</strong>
            </summary>
            <div class="two-column nutrition-detail-grid">
              <div class="reader-box">
                <h3>优先食材</h3>
                <ul class="clean-list"><li v-for="item in compactFoodPlan" :key="item">{{ item }}</li></ul>
              </div>
              <div class="reader-box">
                <h3>替换规则</h3>
                <ul class="clean-list"><li v-for="item in compactReplacements" :key="item">{{ item }}</li></ul>
              </div>
            </div>
          </details>
          <details class="detail-disclosure nutrition-secondary">
            <summary>
              <span>场景与注意</span>
              <strong>外食策略、训练前后和风险提醒</strong>
            </summary>
            <div class="two-column nutrition-detail-grid">
              <div class="reader-box" v-if="compactScenarioTips.length">
                <h3>外食场景</h3>
                <ul class="clean-list"><li v-for="item in compactScenarioTips" :key="item">{{ item }}</li></ul>
              </div>
              <div class="reader-box nutrition-watchouts">
                <h3>执行提醒</h3>
                <ul class="clean-list"><li v-for="item in compactNutritionNotes" :key="item">{{ item }}</li></ul>
              </div>
            </div>
          </details>
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

      </UserDashboard>

      <AdminDashboard v-if="isAdmin">
      <section v-if="activeView === 'contentAdmin'" class="content-stack">
        <div class="page-intro admin-intro">
          <p>内容管理</p>
          <h2>公告与文章发布台</h2>
          <span>这个区域只对管理员开放，普通用户不会看到编辑入口。</span>
        </div>
        <div class="metric-grid small">
          <MetricCard
            v-for="card in adminQualityCards"
            :key="card.label"
            :label="card.label"
            :value="card.value"
            :unit="card.unit"
            :tone="card.tone"
          />
        </div>
        <el-tabs v-model="contentAdminTab" class="admin-tabs">
          <el-tab-pane label="文章" name="articles">
            <div class="admin-stack">
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
              <section class="panel">
                <div class="panel-heading"><div><p>新增文章</p><h2>内容创作</h2></div><el-button type="primary" :icon="Plus" @click="addArticle">发布</el-button></div>
                <div class="form-grid">
                  <label><span>标题</span><el-input v-model="articleForm.title" /></label>
                  <label><span>主题</span><el-input v-model="articleForm.topic" /></label>
                  <label class="wide"><span>内容</span><el-input v-model="articleForm.content" type="textarea" :rows="8" /></label>
                </div>
              </section>
            </div>
          </el-tab-pane>
          <el-tab-pane label="公告" name="notices">
            <div class="admin-stack">
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
              <section class="panel">
                <div class="panel-heading">
                  <div>
                    <p>新增公告</p>
                    <h2>公告管理</h2>
                  </div>
                  <div class="button-row">
                    <el-button :icon="Notebook" @click="loadNoticeDraft">载入</el-button>
                    <el-button :icon="Document" @click="saveNoticeDraft">存草稿</el-button>
                    <el-button type="primary" :icon="Plus" @click="saveNotice">发布</el-button>
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
            </div>
          </el-tab-pane>
        </el-tabs>
      </section>

      <section v-if="activeView === 'guideAdmin'" class="content-stack">
        <div class="page-intro admin-intro">
          <p>动作库管理</p>
          <h2>维护动作内容和指导图片</h2>
          <span>训练计划会读取这里的动作名称、步骤、要点和图片。图片由管理员手动上传。</span>
        </div>
        <div class="admin-stack guide-admin-grid">
          <section class="panel">
            <div class="panel-heading">
              <div>
                <p>动作指导库</p>
                <h2>{{ guideCoverage.total }} 条记录</h2>
              </div>
              <el-button :icon="Refresh" :loading="guideLibraryLoading" @click="loadGuideLibrary">刷新</el-button>
            </div>
            <div class="form-grid compact guide-filter-row">
              <label><span>动作模式</span><el-select v-model="guideFilters.actionPattern" clearable><el-option v-for="item in actionPatterns" :key="item" :label="item" :value="item" /></el-select></label>
              <label><span>器材</span><el-select v-model="guideFilters.equipment" clearable><el-option v-for="item in equipments" :key="item" :label="item" :value="item" /></el-select></label>
              <label class="checkbox-field"><span>图片状态</span><el-checkbox v-model="guideFilters.missingImageOnly">只看未自定义图片</el-checkbox></label>
              <label class="checkbox-field"><span>完整度</span><el-checkbox v-model="guideFilters.incompleteOnly">只看不完整</el-checkbox></label>
              <label class="checkbox-field"><span>步骤</span><el-checkbox v-model="guideFilters.missingStepsOnly">缺步骤</el-checkbox></label>
              <label class="checkbox-field"><span>要点</span><el-checkbox v-model="guideFilters.missingTipsOnly">缺要点</el-checkbox></label>
              <label class="checkbox-field"><span>错误提示</span><el-checkbox v-model="guideFilters.missingMistakesOnly">缺常见错误</el-checkbox></label>
              <label class="checkbox-field"><span>替代动作</span><el-checkbox v-model="guideFilters.missingAlternativesOnly">缺替代动作</el-checkbox></label>
              <div class="field-action">
                <el-button type="primary" :icon="Search" :loading="guideLibraryLoading" @click="loadGuideLibrary">筛选</el-button>
                <el-button @click="clearGuideFilters">重置</el-button>
              </div>
            </div>
            <div class="admin-list guide-admin-list">
              <article v-for="item in guideLibrary" :key="item.id">
                <div>
                  <span>{{ item.actionPattern }} · {{ item.equipment }}</span>
                  <strong>{{ item.actionName || item.actionPattern }}</strong>
                  <small>{{ item.imageurl ? '已上传图片' : '未上传图片' }} · 完整度 {{ item.qualityScore || 0 }}% · {{ item.difficulty || '未标难度' }} · {{ item.primaryMuscles || '未标肌群' }} · {{ excerpt(item.description, 48) }}</small>
                </div>
                <div class="icon-actions">
                  <el-button :icon="Edit" circle @click="openGuideAdminEdit(item)" />
                  <el-button :icon="DeleteIcon" circle type="danger" @click="deleteGuideAdmin(item)" />
                </div>
              </article>
              <el-empty v-if="!guideLibrary.length && !guideLibraryLoading" description="暂无动作指导" />
            </div>
          </section>

          <section class="panel">
            <div class="panel-heading">
              <div>
                <p>新增动作</p>
                <h2>动作指导表单</h2>
              </div>
              <div class="button-row">
                <el-button @click="resetGuideAdminForm">重置</el-button>
                <el-button type="primary" :icon="Check" @click="saveGuideAdmin">添加</el-button>
              </div>
            </div>
            <div class="form-grid">
              <label><span>动作模式</span><el-select v-model="guideAdminForm.actionPattern"><el-option v-for="item in actionPatterns" :key="item" :label="item" :value="item" /></el-select></label>
              <label><span>器材</span><el-select v-model="guideAdminForm.equipment"><el-option v-for="item in equipments" :key="item" :label="item" :value="item" /></el-select></label>
              <label><span>动作名称</span><el-input v-model="guideAdminForm.actionName" placeholder="例如：俯卧撑" /></label>
              <label><span>难度</span><el-select v-model="guideAdminForm.difficulty"><el-option v-for="item in levels" :key="item" :label="item" :value="item" /></el-select></label>
              <label><span>主肌群</span><el-input v-model="guideAdminForm.primaryMuscles" placeholder="例如：胸大肌、肱三头肌" /></label>
              <label><span>辅助肌群</span><el-input v-model="guideAdminForm.secondaryMuscles" placeholder="例如：核心、前锯肌" /></label>
              <label class="wide"><span>动作描述</span><el-input v-model="guideAdminForm.description" type="textarea" :rows="3" /></label>
              <label class="wide"><span>执行步骤</span><el-input v-model="guideAdminForm.steps" type="textarea" :rows="4" placeholder="用 | 分隔步骤" /></label>
              <label class="wide"><span>训练要点</span><el-input v-model="guideAdminForm.tips" type="textarea" :rows="4" placeholder="用 | 分隔要点" /></label>
              <label class="wide"><span>常见错误</span><el-input v-model="guideAdminForm.commonMistakes" type="textarea" :rows="3" /></label>
              <label class="wide"><span>禁忌提醒</span><el-input v-model="guideAdminForm.contraindications" type="textarea" :rows="3" /></label>
              <label class="wide"><span>适合人群</span><el-input v-model="guideAdminForm.suitableFor" type="textarea" :rows="3" /></label>
              <label class="wide"><span>替代动作</span><el-input v-model="guideAdminForm.alternatives" type="textarea" :rows="3" /></label>
            </div>
            <div class="guide-upload-panel">
              <div class="upload-preview">
                <img v-if="guideAdminForm.imageurl" :src="guideAdminForm.imageurl" alt="动作指导图预览" />
                <div v-else class="task-placeholder"><el-icon><Guide /></el-icon></div>
              </div>
              <div class="upload-actions">
                <el-upload
                  :show-file-list="false"
                  accept="image/*"
                  :http-request="uploadGuideImage"
                >
                  <el-button :icon="Upload" :loading="guideImageUploading">上传图片</el-button>
                </el-upload>
                <el-button text type="danger" :disabled="!guideAdminForm.imageurl" @click="clearGuideAdminImage">清空图片</el-button>
                <small v-if="guideAdminForm.imageurl">{{ guideAdminForm.imageurl }}</small>
              </div>
            </div>
          </section>
        </div>
      </section>

      <section v-if="activeView === 'nutritionAdmin'" class="content-stack">
        <div class="page-intro admin-intro">
          <p>饮食模板</p>
          <h2>维护食材、餐单、替换规则和外食场景</h2>
          <span>普通用户的饮食建议会读取这里的模板数据。</span>
        </div>
        <div class="metric-grid small">
          <MetricCard label="餐单模板" :value="nutritionAdminCounts.templates" unit="条" tone="green" />
          <MetricCard label="食材" :value="nutritionAdminCounts.foods" unit="项" tone="orange" />
          <MetricCard label="替换规则" :value="nutritionAdminCounts.replacements" unit="条" tone="blue" />
          <MetricCard label="饮食场景" :value="nutritionAdminCounts.scenarios" unit="类" tone="ink" />
        </div>
        <el-tabs v-model="nutritionAdminTab" class="admin-tabs">
          <el-tab-pane label="餐单模板" name="templates">
            <div class="admin-stack">
              <section class="panel">
                <div class="panel-heading"><div><p>餐单库</p><h2>训练日、休息日和外食模板</h2></div><el-button :icon="Refresh" @click="loadNutritionAdmin">刷新</el-button></div>
                <div class="admin-list">
                  <article v-for="item in nutritionAdminLibrary?.mealTemplates || []" :key="item.id">
                    <div>
                      <span>{{ item.goal }} · {{ item.scene }} · {{ item.mealType }}</span>
                      <strong>{{ item.name }}</strong>
                      <small>{{ item.targetCalories || 0 }} kcal · {{ excerpt(item.foods, 70) }}</small>
                    </div>
                    <div class="icon-actions">
                      <el-button :icon="Edit" circle @click="editMealTemplate(item)" />
                      <el-button :icon="DeleteIcon" circle type="danger" @click="removeMealTemplate(item)" />
                    </div>
                  </article>
                </div>
              </section>
              <section class="panel">
                <div class="panel-heading"><div><p>{{ mealTemplateForm.id ? '编辑餐单' : '新增餐单' }}</p><h2>餐单模板表单</h2></div><div class="button-row"><el-button @click="resetMealTemplateForm">重置</el-button><el-button type="primary" :icon="Check" @click="saveMealTemplate">保存</el-button></div></div>
                <div class="form-grid">
                  <label><span>名称</span><el-input v-model="mealTemplateForm.name" /></label>
                  <label><span>餐次</span><el-select v-model="mealTemplateForm.mealType"><el-option v-for="item in mealTypes" :key="item" :label="item" :value="item" /></el-select></label>
                  <label><span>目标</span><el-select v-model="mealTemplateForm.goal"><el-option label="通用" value="通用" /><el-option v-for="item in fitnessGoals" :key="item" :label="item" :value="item" /></el-select></label>
                  <label><span>场景</span><el-select v-model="mealTemplateForm.scene"><el-option v-for="item in eatingScenes" :key="item" :label="item" :value="item" /></el-select></label>
                  <label><span>热量</span><el-input-number v-model="mealTemplateForm.targetCalories" :min="0" :max="1600" /></label>
                  <label class="wide"><span>描述</span><el-input v-model="mealTemplateForm.description" type="textarea" :rows="3" /></label>
                  <label class="wide"><span>食物组合</span><el-input v-model="mealTemplateForm.foods" type="textarea" :rows="3" /></label>
                </div>
              </section>
            </div>
          </el-tab-pane>
          <el-tab-pane label="食材" name="foods">
            <div class="admin-stack">
              <section class="panel">
                <div class="panel-heading"><div><p>食材库</p><h2>宏量营养基础数据</h2></div><el-button :icon="Refresh" @click="loadNutritionAdmin">刷新</el-button></div>
                <div class="admin-list">
                  <article v-for="item in nutritionAdminLibrary?.foodItems || []" :key="item.id">
                    <div>
                      <span>{{ item.category }} · {{ item.scene || '通用' }}</span>
                      <strong>{{ item.name }}</strong>
                      <small>{{ item.caloriesPer100g || 0 }} kcal/100g · 蛋白 {{ item.proteinPer100g || 0 }}g · {{ item.tags || '无标签' }}</small>
                    </div>
                    <div class="icon-actions">
                      <el-button :icon="Edit" circle @click="editFoodItem(item)" />
                      <el-button :icon="DeleteIcon" circle type="danger" @click="removeFoodItem(item)" />
                    </div>
                  </article>
                </div>
              </section>
              <section class="panel">
                <div class="panel-heading"><div><p>{{ foodItemForm.id ? '编辑食材' : '新增食材' }}</p><h2>食材表单</h2></div><div class="button-row"><el-button @click="resetFoodItemForm">重置</el-button><el-button type="primary" :icon="Check" @click="saveFoodItem">保存</el-button></div></div>
                <div class="form-grid">
                  <label><span>名称</span><el-input v-model="foodItemForm.name" /></label>
                  <label><span>分类</span><el-select v-model="foodItemForm.category"><el-option v-for="item in foodCategories" :key="item" :label="item" :value="item" /></el-select></label>
                  <label><span>场景</span><el-select v-model="foodItemForm.scene"><el-option v-for="item in eatingScenes" :key="item" :label="item" :value="item" /></el-select></label>
                  <label><span>热量/100g</span><el-input-number v-model="foodItemForm.caloriesPer100g" :min="0" :max="900" /></label>
                  <label><span>蛋白/100g</span><el-input-number v-model="foodItemForm.proteinPer100g" :min="0" :max="100" :precision="1" /></label>
                  <label><span>碳水/100g</span><el-input-number v-model="foodItemForm.carbohydratePer100g" :min="0" :max="100" :precision="1" /></label>
                  <label><span>脂肪/100g</span><el-input-number v-model="foodItemForm.fatPer100g" :min="0" :max="100" :precision="1" /></label>
                  <label class="wide"><span>标签</span><el-input v-model="foodItemForm.tags" placeholder="用逗号分隔" /></label>
                </div>
              </section>
            </div>
          </el-tab-pane>
          <el-tab-pane label="替换规则" name="replacements">
            <div class="admin-stack">
              <section class="panel">
                <div class="panel-heading"><div><p>替换规则</p><h2>同类食物替换</h2></div><el-button :icon="Refresh" @click="loadNutritionAdmin">刷新</el-button></div>
                <div class="admin-list">
                  <article v-for="item in nutritionAdminLibrary?.foodReplacements || []" :key="item.id">
                    <div>
                      <span>{{ item.category || '通用' }}</span>
                      <strong>{{ item.sourceFood }} → {{ item.replacementFood }}</strong>
                      <small>{{ item.reason }}</small>
                    </div>
                    <div class="icon-actions">
                      <el-button :icon="Edit" circle @click="editFoodReplacement(item)" />
                      <el-button :icon="DeleteIcon" circle type="danger" @click="removeFoodReplacement(item)" />
                    </div>
                  </article>
                </div>
              </section>
              <section class="panel">
                <div class="panel-heading"><div><p>{{ foodReplacementForm.id ? '编辑规则' : '新增规则' }}</p><h2>替换规则表单</h2></div><div class="button-row"><el-button @click="resetFoodReplacementForm">重置</el-button><el-button type="primary" :icon="Check" @click="saveFoodReplacement">保存</el-button></div></div>
                <div class="form-grid">
                  <label><span>原食物</span><el-input v-model="foodReplacementForm.sourceFood" /></label>
                  <label><span>替换食物</span><el-input v-model="foodReplacementForm.replacementFood" /></label>
                  <label><span>分类</span><el-select v-model="foodReplacementForm.category"><el-option v-for="item in foodCategories" :key="item" :label="item" :value="item" /></el-select></label>
                  <label class="wide"><span>替换理由</span><el-input v-model="foodReplacementForm.reason" type="textarea" :rows="3" /></label>
                </div>
              </section>
            </div>
          </el-tab-pane>
          <el-tab-pane label="外食场景" name="scenarios">
            <div class="admin-stack">
              <section class="panel">
                <div class="panel-heading"><div><p>场景库</p><h2>食堂、便利店、外卖和家庭做饭</h2></div><el-button :icon="Refresh" @click="loadNutritionAdmin">刷新</el-button></div>
                <div class="admin-list">
                  <article v-for="item in nutritionAdminLibrary?.eatingScenarios || []" :key="item.id">
                    <div>
                      <span>{{ item.goal }} · {{ item.name }}</span>
                      <strong>{{ item.strategy }}</strong>
                      <small>少选：{{ item.avoid }} · 示例：{{ item.example }}</small>
                    </div>
                    <div class="icon-actions">
                      <el-button :icon="Edit" circle @click="editEatingScenario(item)" />
                      <el-button :icon="DeleteIcon" circle type="danger" @click="removeEatingScenario(item)" />
                    </div>
                  </article>
                </div>
              </section>
              <section class="panel">
                <div class="panel-heading"><div><p>{{ eatingScenarioForm.id ? '编辑场景' : '新增场景' }}</p><h2>外食场景表单</h2></div><div class="button-row"><el-button @click="resetEatingScenarioForm">重置</el-button><el-button type="primary" :icon="Check" @click="saveEatingScenario">保存</el-button></div></div>
                <div class="form-grid">
                  <label><span>场景名称</span><el-select v-model="eatingScenarioForm.name" filterable allow-create><el-option v-for="item in eatingScenes.slice(1)" :key="item" :label="item" :value="item" /></el-select></label>
                  <label><span>目标</span><el-select v-model="eatingScenarioForm.goal"><el-option label="通用" value="通用" /><el-option v-for="item in fitnessGoals" :key="item" :label="item" :value="item" /></el-select></label>
                  <label class="wide"><span>策略</span><el-input v-model="eatingScenarioForm.strategy" type="textarea" :rows="3" /></label>
                  <label class="wide"><span>少选内容</span><el-input v-model="eatingScenarioForm.avoid" type="textarea" :rows="3" /></label>
                  <label class="wide"><span>示例组合</span><el-input v-model="eatingScenarioForm.example" type="textarea" :rows="3" /></label>
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
          <div class="table-shell">
            <el-table :data="adminUsers" stripe class="admin-user-table">
              <el-table-column prop="username" label="用户名" width="130" />
              <el-table-column prop="nickname" label="昵称" width="140" />
              <el-table-column prop="identity" label="身份" width="110" />
              <el-table-column label="审核" width="110"><template #default="{ row }">{{ row.registered ? '已通过' : '待审核' }}</template></el-table-column>
              <el-table-column prop="specialty" label="方向" min-width="160" />
              <el-table-column label="操作" width="220">
                <template #default="{ row }">
                  <el-button link type="primary" @click="openAdminEdit(row)">编辑</el-button>
                  <el-button link type="warning" @click="toggleRegister(row)">{{ row.registered ? '撤回' : '通过' }}</el-button>
                  <el-button link type="danger" @click="deleteUser(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </section>
      </AdminDashboard>
    </main>

    <el-dialog v-model="adminEditVisible" title="编辑用户" width="min(680px, 92vw)">
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

    <el-dialog v-model="articleEditVisible" title="编辑文章" width="min(760px, 92vw)">
      <div class="form-grid">
        <label><span>标题</span><el-input v-model="articleEditForm.title" /></label>
        <label><span>主题</span><el-input v-model="articleEditForm.topic" /></label>
        <label class="wide"><span>内容</span><el-input v-model="articleEditForm.content" type="textarea" :rows="9" /></label>
      </div>
      <template #footer><el-button @click="articleEditVisible = false">取消</el-button><el-button type="primary" @click="saveArticleEdit">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="noticeEditVisible" title="编辑公告" width="min(720px, 92vw)">
      <div class="form-grid">
        <label><span>标题</span><el-input v-model="noticeEditForm.title" /></label>
        <label class="wide"><span>内容</span><el-input v-model="noticeEditForm.content" type="textarea" :rows="8" /></label>
      </div>
      <template #footer>
        <el-button @click="noticeEditVisible = false">取消</el-button>
        <el-button type="primary" @click="saveNoticeEdit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="guideEditVisible" title="编辑动作指导" width="min(820px, 92vw)">
      <div class="form-grid">
        <label><span>动作模式</span><el-select v-model="guideEditForm.actionPattern"><el-option v-for="item in actionPatterns" :key="item" :label="item" :value="item" /></el-select></label>
        <label><span>器材</span><el-select v-model="guideEditForm.equipment"><el-option v-for="item in equipments" :key="item" :label="item" :value="item" /></el-select></label>
        <label><span>动作名称</span><el-input v-model="guideEditForm.actionName" placeholder="例如：俯卧撑" /></label>
        <label><span>难度</span><el-select v-model="guideEditForm.difficulty"><el-option v-for="item in levels" :key="item" :label="item" :value="item" /></el-select></label>
        <label><span>主肌群</span><el-input v-model="guideEditForm.primaryMuscles" /></label>
        <label><span>辅助肌群</span><el-input v-model="guideEditForm.secondaryMuscles" /></label>
        <label class="wide"><span>动作描述</span><el-input v-model="guideEditForm.description" type="textarea" :rows="3" /></label>
        <label class="wide"><span>执行步骤</span><el-input v-model="guideEditForm.steps" type="textarea" :rows="4" placeholder="用 | 分隔步骤" /></label>
        <label class="wide"><span>训练要点</span><el-input v-model="guideEditForm.tips" type="textarea" :rows="4" placeholder="用 | 分隔要点" /></label>
        <label class="wide"><span>常见错误</span><el-input v-model="guideEditForm.commonMistakes" type="textarea" :rows="3" /></label>
        <label class="wide"><span>禁忌提醒</span><el-input v-model="guideEditForm.contraindications" type="textarea" :rows="3" /></label>
        <label class="wide"><span>适合人群</span><el-input v-model="guideEditForm.suitableFor" type="textarea" :rows="3" /></label>
        <label class="wide"><span>替代动作</span><el-input v-model="guideEditForm.alternatives" type="textarea" :rows="3" /></label>
      </div>
      <div class="guide-upload-panel dialog-upload-panel">
        <div class="upload-preview">
          <img v-if="guideEditForm.imageurl" :src="guideEditForm.imageurl" alt="动作指导图预览" />
          <div v-else class="task-placeholder"><el-icon><Guide /></el-icon></div>
        </div>
        <div class="upload-actions">
          <el-upload
            :show-file-list="false"
            accept="image/*"
            :http-request="uploadGuideEditImage"
          >
            <el-button :icon="Upload" :loading="guideImageUploading">上传图片</el-button>
          </el-upload>
          <el-button text type="danger" :disabled="!guideEditForm.imageurl" @click="clearGuideEditImage">清空图片</el-button>
          <small v-if="guideEditForm.imageurl">{{ guideEditForm.imageurl }}</small>
        </div>
      </div>
      <template #footer>
        <el-button @click="guideEditVisible = false">取消</el-button>
        <el-button type="primary" @click="saveGuideEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.workspace-shell {
  position: relative;
  isolation: isolate;
  min-height: 100vh;
  width: 100%;
  display: block;
  padding-left: 280px;
  background: transparent;
  color: #2b211c;
  overflow-x: hidden;
}

.workspace-shell::before {
  content: "";
  position: fixed;
  right: clamp(-140px, -8vw, -56px);
  bottom: clamp(-130px, -10vw, -64px);
  z-index: 0;
  width: min(48vw, 620px);
  min-width: 320px;
  aspect-ratio: 2 / 3;
  background: url('./assets/yuelu-logo.png') center / contain no-repeat;
  opacity: 0.08;
  filter: drop-shadow(0 30px 60px rgba(122, 55, 27, 0.18));
  pointer-events: none;
}

/* —— 主区域光斑：随滚动悬停在右上 —— */
.workspace-shell::after {
  content: "";
  position: fixed;
  top: -180px;
  right: -120px;
  z-index: 0;
  width: 520px;
  height: 520px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 211, 154, 0.55), transparent 65%);
  filter: blur(20px);
  pointer-events: none;
}

.workspace-shell > :deep(.sidebar) {
  z-index: 30;
}

.main {
  position: relative;
  z-index: 1;
}

.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 30;
  width: 280px;
  height: 100vh;
  padding: 24px;
  background:
    linear-gradient(160deg, rgba(58, 33, 23, 0.98) 0%, rgba(114, 49, 24, 0.98) 54%, rgba(169, 77, 26, 0.96) 100%),
    linear-gradient(90deg, rgba(255, 255, 255, 0.06), transparent 34%);
  color: #fff;
  display: flex;
  flex-direction: column;
  gap: clamp(14px, 3vh, 26px);
  overflow: hidden;
  box-shadow: 18px 0 46px rgba(78, 44, 26, 0.16);
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
  background: linear-gradient(135deg, #fff1d1 0%, #ffb454 58%, #e9791a 100%);
  color: #2b211c;
  font-weight: 900;
  box-shadow: 0 12px 28px rgba(233, 121, 26, 0.28);
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
  overflow: hidden;
  display: grid;
  align-content: start;
  gap: clamp(10px, 2.2vh, 20px);
}

.nav-list p {
  margin: 0 0 clamp(4px, 0.8vh, 8px);
  color: rgba(255, 255, 255, 0.48);
  font-size: 12px;
  font-weight: 800;
}

.nav-list button {
  position: relative;
  width: 100%;
  min-height: clamp(34px, 5vh, 42px);
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
.page-intro p {
  margin: 0 0 6px;
  color: #d96b1f;
  font-size: 11px;
  font-weight: 900;
  letter-spacing: 1.6px;
  text-transform: uppercase;
}

.topbar h1,
.panel-heading h2,
.hero-copy h2,
.page-intro h2 {
  margin: 0;
  letter-spacing: -0.4px;
  font-weight: 800;
}

.panel-heading h2,
.page-intro h2 {
  background: linear-gradient(120deg, #2b211c 0%, #7b371b 60%, #c65a1a 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
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
  border: 1px solid #eadfd4;
  border-radius: 8px;
  padding: 8px 12px;
  background: #fff;
  color: #4e4038;
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
  border: 1px solid rgba(234, 223, 212, 0.85);
  border-radius: 16px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.96) 0%, rgba(255, 250, 244, 0.96) 100%);
  box-shadow:
    0 14px 40px rgba(84, 62, 48, 0.06),
    inset 0 1px 0 rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(8px) saturate(1.04);
  animation: surface-in 520ms cubic-bezier(0.2, 0.8, 0.2, 1) both;
}

/* —— Hero stage —— 电影感主舞台 */
.hero-stage {
  position: relative;
  isolation: isolate;
  min-width: 0;
  min-height: 280px;
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(320px, 0.92fr);
  gap: 18px;
  align-items: stretch;
  overflow: hidden;
  border-radius: 20px;
  background:
    radial-gradient(circle at 18% 20%, rgba(255, 211, 154, 0.45) 0%, transparent 38%),
    radial-gradient(circle at 88% 88%, rgba(255, 180, 84, 0.5) 0%, transparent 40%),
    linear-gradient(118deg, #1c130e 0%, #3a2117 32%, #7b371b 64%, #c65a1a 100%);
  background-size: 220% 220%, 220% 220%, 200% 200%;
  color: #fff;
  border: 1px solid rgba(255, 211, 154, 0.18);
  box-shadow:
    0 30px 60px rgba(43, 33, 28, 0.32),
    inset 0 1px 0 rgba(255, 246, 234, 0.18);
  animation: surface-in 520ms cubic-bezier(0.2, 0.8, 0.2, 1) both, hero-flow 14s ease-in-out infinite alternate;
}

/* —— 网格视差层 —— */
.hero-stage::before {
  content: "";
  position: absolute;
  inset: 0;
  z-index: 0;
  background:
    linear-gradient(120deg, rgba(255, 255, 255, 0.14), transparent 40%),
    repeating-linear-gradient(90deg, rgba(255, 255, 255, 0.055) 0 1px, transparent 1px 64px),
    repeating-linear-gradient(0deg, rgba(255, 255, 255, 0.04) 0 1px, transparent 1px 64px);
  opacity: 0.62;
  pointer-events: none;
  transform: translate3d(0, 0, 0);
  animation: grid-drift 14s linear infinite;
  mask-image: linear-gradient(180deg, rgba(0, 0, 0, 1), rgba(0, 0, 0, 0.4));
}

/* —— 极光层 —— */
.hero-stage::after {
  content: "";
  position: absolute;
  top: -40%;
  right: -20%;
  width: 80%;
  height: 200%;
  z-index: 0;
  background: var(--grad-aurora);
  filter: blur(60px);
  opacity: 0.55;
  mix-blend-mode: screen;
  pointer-events: none;
  animation: ring-spin 22s linear infinite;
}

.hero-copy {
  position: relative;
  z-index: 2;
  min-width: 0;
  padding: 32px;
  display: grid;
  align-content: center;
}

.hero-copy p,
.hero-copy span {
  color: rgba(255, 246, 234, 0.78);
}

.hero-copy p {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  width: fit-content;
  border-radius: 999px;
  padding: 6px 14px;
  background: linear-gradient(120deg, rgba(255, 211, 154, 0.22), rgba(233, 121, 26, 0.22));
  border: 1px solid rgba(255, 211, 154, 0.36);
  color: #fff4e3 !important;
  font-size: 11px;
  font-weight: 900;
  letter-spacing: 1.6px;
  backdrop-filter: blur(8px);
}

.hero-copy h2 {
  margin: 14px 0 12px;
  max-width: 760px;
  font-family: 'Space Grotesk', 'Inter', sans-serif;
  font-size: clamp(34px, 4.5vw, 60px);
  line-height: 1.02;
  letter-spacing: -1px;
  font-weight: 800;
  overflow-wrap: anywhere;
  background: linear-gradient(120deg, #ffffff 0%, #ffe3bd 35%, #ffb454 78%, #fff4e3 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  text-shadow: 0 8px 30px rgba(255, 180, 84, 0.18);
}

.hero-actions {
  justify-content: flex-start;
  margin-top: 18px;
}

.hero-visual {
  position: relative;
  z-index: 2;
  min-width: 0;
  min-height: 244px;
  display: grid;
  align-content: center;
  gap: 12px;
  padding: 22px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.14), rgba(255, 255, 255, 0.04));
  border-left: 1px solid rgba(255, 246, 234, 0.12);
}

.visual-ring-card,
.visual-data-grid,
.visual-bars-card {
  border: 1px solid rgba(255, 246, 234, 0.22);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(14px) saturate(1.18);
  box-shadow:
    inset 0 1px 0 rgba(255, 246, 234, 0.28),
    0 10px 28px rgba(43, 33, 28, 0.18);
  transition: transform 280ms cubic-bezier(0.2, 0.8, 0.2, 1),
              background 280ms ease,
              border-color 280ms ease;
}

.visual-ring-card:hover,
.visual-data-grid:hover,
.visual-bars-card:hover {
  transform: translateY(-3px);
  background: rgba(255, 255, 255, 0.16);
  border-color: rgba(255, 211, 154, 0.45);
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
  width: 102px;
  aspect-ratio: 1;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background:
    conic-gradient(from -90deg, #ffd39a, #ffb454, #e9791a var(--progress), rgba(255, 255, 255, 0.16) 0);
  filter: drop-shadow(0 18px 30px rgba(255, 180, 84, 0.32));
  animation: ring-breathe 3.2s ease-in-out infinite;
}

.progress-ring::before {
  content: "";
  position: absolute;
  inset: -8px;
  border-radius: 50%;
  background: var(--grad-aurora);
  filter: blur(12px);
  opacity: 0.55;
  z-index: -1;
  animation: ring-spin 6s linear infinite;
}

.progress-ring::after {
  content: "";
  position: absolute;
  inset: 11px;
  border-radius: 50%;
  background: radial-gradient(circle at 35% 30%, #743220 0%, #4a2615 60%, #2c1610 100%);
  box-shadow: inset 0 1px 0 rgba(255, 211, 154, 0.18);
}

.progress-ring strong {
  position: relative;
  z-index: 1;
  font-family: 'Space Grotesk', 'Inter', sans-serif;
  font-size: 26px;
  font-weight: 800;
  letter-spacing: -0.6px;
  background: linear-gradient(180deg, #fff4e3, #ffb454);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-feature-settings: 'tnum';
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
  border-radius: 10px 10px 2px 2px;
  background:
    linear-gradient(180deg, #ffd39a 0%, #ffb454 38%, #e9791a 100%);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.45),
    0 6px 14px rgba(233, 121, 26, 0.32);
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
  grid-template-columns: repeat(auto-fit, minmax(210px, 1fr));
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
  background: linear-gradient(90deg, #e9791a, #ffb454, #8a5a44);
  opacity: 0.34;
}

.metric-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 18px 45px rgba(84, 62, 48, 0.1);
}

.metric-card.green { border-left-color: #e9791a; }
.metric-card.orange { border-left-color: #f59e0b; }
.metric-card.blue { border-left-color: #8a5a44; }
.metric-card.ink { border-left-color: #2f2925; }

.metric-card span {
  color: #75675e;
  font-weight: 700;
}

.metric-card strong {
  margin-top: 6px;
  font-size: 28px;
  line-height: 1;
}

.metric-card small {
  color: #c65a1a;
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
  position: relative;
  padding: 24px;
  min-width: 0;
  isolation: isolate;
  transition: transform 320ms cubic-bezier(0.2, 0.8, 0.2, 1), box-shadow 320ms ease, border-color 320ms ease;
}

.panel::before {
  content: "";
  position: absolute;
  inset: 0;
  border-radius: inherit;
  z-index: -1;
  background: radial-gradient(420px 320px at 0% 0%, rgba(255, 211, 154, 0.16), transparent 60%);
  opacity: 0;
  transition: opacity 360ms ease;
  pointer-events: none;
}

.panel:hover::before {
  opacity: 1;
}

.overview-stack .panel {
  padding: 18px;
}

.panel-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.panel-heading > div:first-child {
  position: relative;
  padding-left: 14px;
}

.panel-heading > div:first-child::before {
  content: "";
  position: absolute;
  left: 0;
  top: 6px;
  bottom: 6px;
  width: 4px;
  border-radius: 999px;
  background: linear-gradient(180deg, #ffb454, #e9791a, #7b371b);
  box-shadow: 0 0 12px rgba(233, 121, 26, 0.45);
}

.panel-heading > div,
.panel-heading h2,
.panel-heading p,
.topbar-actions,
.button-row,
.icon-actions {
  min-width: 0;
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
  color: #51443b;
  font-weight: 800;
}

.form-grid label span,
.form-grid :deep(.el-input),
.form-grid :deep(.el-select),
.form-grid :deep(.el-input-number),
.form-grid :deep(.el-textarea) {
  min-width: 0;
  width: 100%;
}

.form-grid label.wide {
  grid-column: 1 / -1;
}

.form-grid label.checkbox-field {
  align-content: end;
}

.checkbox-field :deep(.el-checkbox) {
  min-height: 32px;
  height: auto;
  align-items: center;
}

.checkbox-field :deep(.el-checkbox__label) {
  min-width: 0;
  white-space: normal;
  overflow-wrap: anywhere;
}

.field-action {
  display: flex;
  align-items: end;
  flex-wrap: wrap;
  gap: 8px;
  min-width: 0;
}

.field-action :deep(.el-button + .el-button) {
  margin-left: 0;
}

.field-action :deep(.el-button) {
  min-width: 0;
}

.muted,
.block-copy {
  color: #75675e;
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
  border: 1px solid #eadfd4;
  border-radius: 10px;
  background: #fffaf4;
  color: #4d4038;
  padding: 8px 14px;
  font-weight: 800;
  font-feature-settings: 'tnum';
}

.segmented button {
  position: relative;
  cursor: pointer;
  overflow: hidden;
  transition: transform 220ms ease, border-color 220ms ease, background 220ms ease, color 220ms ease, box-shadow 220ms ease;
}

.segmented button.active {
  background: linear-gradient(135deg, #c65a1a 0%, #8a3d12 60%, #3a2117 100%);
  border-color: rgba(255, 211, 154, 0.4);
  color: #fff4e3;
  box-shadow:
    0 12px 26px rgba(166, 71, 22, 0.34),
    inset 0 1px 0 rgba(255, 246, 234, 0.32);
  transform: translateY(-1px);
}

.segmented button.active::before {
  content: "";
  position: absolute;
  inset: 0;
  background: linear-gradient(120deg, transparent 30%, rgba(255, 211, 154, 0.32), transparent 70%);
  transform: translateX(-130%);
  animation: segmented-sheen 2.6s ease-in-out infinite;
  pointer-events: none;
}

@keyframes segmented-sheen {
  0% { transform: translateX(-130%); }
  60%, 100% { transform: translateX(130%); }
}

.segmented button:hover {
  transform: translateY(-2px);
  border-color: #efb07b;
  box-shadow: 0 8px 18px rgba(166, 71, 22, 0.14);
}

.segmented button.checkin-locked {
  color: #8a6a38;
  background: #fff7e8;
  border-color: #efd6ad;
}

.segmented button:disabled {
  cursor: not-allowed;
  color: #8b9691;
  background: #f2ede6;
  border-color: #eadfd4;
  box-shadow: none;
  transform: none;
}

.plan-lock-hint {
  margin: -4px 0 16px;
  padding: 10px 12px;
  border: 1px solid #ead9aa;
  border-radius: 8px;
  background: #fff8e6;
  color: #755b16;
  font-weight: 800;
}

.action-brief .panel-heading {
  align-items: center;
}

.readiness-score {
  position: relative;
  min-width: 76px;
  height: 76px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background:
    radial-gradient(circle at 50% 50%, #fff 0 55%, transparent 56%),
    conic-gradient(from -90deg, #ffb454, #e9791a calc(var(--score, 72) * 1%), rgba(240, 227, 216, 0.5) 0);
  border: 1px solid rgba(234, 223, 212, 0.9);
  color: #7b371b;
  font-family: 'Space Grotesk', 'Inter', sans-serif;
  font-size: 24px;
  font-weight: 800;
  letter-spacing: -0.5px;
  box-shadow:
    0 16px 30px rgba(166, 71, 22, 0.18),
    inset 0 0 0 1px rgba(255, 255, 255, 0.8);
  font-feature-settings: 'tnum';
}

.readiness-score::after {
  content: "";
  position: absolute;
  inset: -6px;
  border-radius: 50%;
  background: var(--grad-aurora);
  filter: blur(10px);
  opacity: 0.42;
  z-index: -1;
  animation: ring-spin 8s linear infinite;
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
  color: #665a52;
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
  background: #f3ece4;
}

.brief-meter i,
.macro-grid b,
.plan-execution-bar b {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, #e9791a, #f7b66a, #ffb454);
  transition: width 520ms cubic-bezier(0.2, 0.8, 0.2, 1);
}

.brief-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.completion-pill {
  position: relative;
  min-width: 74px;
  border-radius: 999px;
  padding: 10px 16px;
  background: linear-gradient(135deg, #c65a1a 0%, #7b371b 60%, #3a2117 100%);
  color: #fff4e3;
  text-align: center;
  font-family: 'Space Grotesk', 'Inter', sans-serif;
  font-size: 20px;
  font-weight: 800;
  letter-spacing: -0.4px;
  box-shadow:
    0 14px 28px rgba(122, 55, 27, 0.32),
    inset 0 1px 0 rgba(255, 246, 234, 0.3);
  font-feature-settings: 'tnum';
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
  border: 1px solid #eadfd4;
  border-radius: 8px;
  background: #fffaf4;
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
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #fff4e3 0%, #ffe3bd 100%);
  border: 1px solid rgba(255, 211, 154, 0.45);
  color: #c65a1a;
  font-size: 20px;
  box-shadow:
    0 6px 14px rgba(233, 121, 26, 0.18),
    inset 0 1px 0 rgba(255, 255, 255, 0.7);
}

.onboarding-list article.done {
  border-color: rgba(233, 154, 90, 0.55);
  background: linear-gradient(180deg, #fff 0%, #fff7ed 100%);
}

.onboarding-list article.done > .el-icon {
  background: linear-gradient(135deg, #ffb454 0%, #e9791a 100%);
  color: #fff;
  border-color: rgba(255, 211, 154, 0.7);
}

.onboarding-list strong,
.suggestion-list strong {
  color: #2b211c;
}

.onboarding-list p,
.suggestion-list p {
  margin: 5px 0 0;
  color: #71645b;
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
  border-color: #edb98f;
  background: #fff;
  box-shadow: 0 16px 30px rgba(84, 62, 48, 0.08);
}

.suggestion-list span {
  display: block;
  margin-bottom: 6px;
  color: #c65a1a;
  font-size: 12px;
  font-weight: 900;
}

.empty-mini {
  min-height: 220px;
  border: 1px dashed #eadfd4;
  border-radius: 8px;
  background: #fffaf4;
  color: #71645b;
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
  color: #a98a73;
  font-size: 32px;
}

.coverage-badge {
  border: 1px solid #eadfd4;
  border-radius: 8px;
  background: #fffaf4;
  color: #4e4038;
  padding: 8px 12px;
  font-weight: 900;
  white-space: nowrap;
}

.heatmap-grid {
  display: grid;
  width: 100%;
  max-width: 100%;
  min-width: 0;
  grid-template-columns: repeat(35, minmax(0, 1fr));
  gap: clamp(2px, 0.35vw, 5px);
  align-items: end;
}

.heatmap-grid span {
  min-width: 0;
  min-height: 34px;
  border-radius: 6px;
  border: 1px solid #eadfd4;
  background: #f8efe6;
  transition: transform 180ms ease, border-color 180ms ease;
}

.overview-stack .heatmap-grid span {
  min-height: 24px;
}

.heatmap-grid span:hover {
  transform: translateY(-3px);
  border-color: #efb07b;
}

.heatmap-grid .level-1 { background: #ffe6c7; }
.heatmap-grid .level-2 { background: #ffc078; }
.heatmap-grid .level-3 { background: #f59e0b; }
.heatmap-grid .level-4 { background: #c65a1a; }

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
  color: rgba(43, 33, 28, 0.72);
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
  position: relative;
  border: 1px solid rgba(234, 223, 212, 0.85);
  border-radius: 12px;
  background:
    linear-gradient(180deg, #ffffff 0%, #fffaf4 100%);
  padding: 14px;
  display: grid;
  align-content: center;
  gap: 8px;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.6);
  transition: transform 240ms ease, border-color 240ms ease, box-shadow 240ms ease;
}

.plan-execution-bar > div:hover {
  transform: translateY(-2px);
  border-color: rgba(233, 154, 90, 0.45);
  box-shadow: 0 14px 30px rgba(166, 71, 22, 0.12);
}

.plan-execution-bar span {
  color: #87776b;
  font-weight: 800;
  font-size: 11px;
  letter-spacing: 1.2px;
  text-transform: uppercase;
}

.plan-execution-bar strong {
  font-family: 'Space Grotesk', 'Inter', sans-serif;
  background: linear-gradient(120deg, #2b211c 0%, #7b371b 60%, #c65a1a 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-size: 20px;
  font-weight: 800;
  letter-spacing: -0.4px;
  font-feature-settings: 'tnum';
}

.plan-coach-strip {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 18px;
}

.cycle-grid,
.adjustment-list {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 14px;
}

.cycle-grid article,
.adjustment-list article {
  position: relative;
  min-width: 0;
  border: 1px solid rgba(234, 223, 212, 0.85);
  border-radius: 12px;
  background: linear-gradient(135deg, #ffffff 0%, #fff4e3 100%);
  padding: 16px;
  overflow: hidden;
  isolation: isolate;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.6);
  transition: transform 280ms ease, border-color 280ms ease, box-shadow 280ms ease;
}

.cycle-grid article::before,
.adjustment-list article::before {
  content: "";
  position: absolute;
  top: -40%;
  right: -30%;
  width: 80%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 211, 154, 0.32), transparent 70%);
  filter: blur(20px);
  opacity: 0;
  z-index: -1;
  transition: opacity 320ms ease;
  pointer-events: none;
}

.cycle-grid article:hover,
.adjustment-list article:hover {
  transform: translateY(-3px);
  border-color: rgba(233, 154, 90, 0.55);
  box-shadow: 0 18px 36px rgba(166, 71, 22, 0.14);
}

.cycle-grid article:hover::before,
.adjustment-list article:hover::before {
  opacity: 1;
}

.cycle-grid span,
.adjustment-list span {
  color: #87776b;
  font-size: 12px;
  font-weight: 900;
}

.cycle-grid strong,
.adjustment-list strong {
  display: block;
  margin-top: 6px;
  color: #3a2117;
  font-size: 16px;
}

.cycle-grid p,
.adjustment-list p {
  margin: 8px 0 0;
  color: #71645b;
  line-height: 1.65;
}

.replacement-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
}

.replacement-actions > span {
  color: #87776b;
  font-weight: 900;
}

.plan-insight-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 18px;
}

.plan-insight-grid article {
  border: 1px solid #eadfd4;
  border-radius: 8px;
  background: linear-gradient(135deg, #fffaf4, #fff9e8);
  padding: 14px;
  min-width: 0;
}

.plan-insight-grid span {
  display: block;
  color: #75675e;
  font-size: 12px;
  font-weight: 900;
}

.plan-insight-grid strong {
  display: block;
  margin-top: 6px;
  color: #7b371b;
  font-size: 18px;
  overflow-wrap: anywhere;
}

.plan-insight-grid p {
  margin: 8px 0 0;
  color: #71645b;
  line-height: 1.65;
  overflow-wrap: anywhere;
}

.plan-coach-strip article {
  border: 1px solid #eadfd4;
  border-radius: 8px;
  background:
    linear-gradient(180deg, #fff, #fffaf4);
  padding: 14px;
}

.plan-coach-strip span {
  display: block;
  color: #c65a1a;
  font-size: 12px;
  font-weight: 900;
}

.plan-coach-strip strong {
  display: block;
  margin-top: 6px;
  color: #2b211c;
  font-size: 20px;
}

.plan-coach-strip p {
  margin: 8px 0 0;
  color: #71645b;
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
  color: #75675e;
  font-weight: 800;
}

.progress-rows i {
  grid-column: 1 / -1;
  height: 10px;
  border-radius: 999px;
  background: #f3ece4;
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
  background: linear-gradient(90deg, #e9791a, #f7b66a);
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
  border: 1px solid #f3ece4;
  border-radius: 8px;
  padding: 14px;
  display: grid;
  gap: 10px;
  cursor: pointer;
  background: #fffdf9;
  transition: transform 190ms ease, border-color 190ms ease, box-shadow 190ms ease, background 190ms ease;
}

.feed-row:hover,
.notice-list article:hover,
.related-panel button:hover {
  transform: translateY(-3px);
  border-color: #edb98f;
  background: #fff;
  box-shadow: 0 16px 30px rgba(84, 62, 48, 0.08);
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

.feed-row > div,
.notice-list article > div,
.admin-list article > div:first-child,
.article-card,
.notice-card,
.task-card,
.reader-box,
.related-panel,
.guide-library-card,
.guide-library-card > div:last-child {
  min-width: 0;
}

.feed-row h3,
.feed-row p,
.feed-row small,
.article-card h3,
.article-card p,
.article-card small,
.notice-card h3,
.notice-card p,
.notice-card small,
.task-body h3,
.task-body p,
.task-tags em,
.task-dose strong,
.guide-columns li,
.reader-article h2,
.reader-article p,
.reader-meta small,
.related-panel button,
.timeline-row p,
.result-block h3,
.result-block p,
.reader-box h3,
.clean-list li,
.guide-library-card span,
.guide-library-card h3,
.guide-library-card p,
.admin-list strong,
.admin-list small,
.admin-list span,
.macro-grid strong,
.training-day-tip {
  overflow-wrap: anywhere;
}

.feed-row p,
.article-card p,
.notice-card p {
  color: #71645b;
  line-height: 1.7;
}

.feed-row span,
.article-card span,
.admin-list span {
  width: fit-content;
  max-width: 100%;
  border-radius: 8px;
  padding: 7px 10px;
  font-size: 12px;
  font-weight: 900;
}

.feed-row > span {
  max-width: 132px;
}

.mint { background: #ffe3bd; color: #8a3d12; }
.sun { background: #fff0c8; color: #8a5a10; }
.sky { background: #e7eef9; color: #355d7a; }
.rose { background: #ffe2d6; color: #9b4d2c; }

.page-intro {
  position: relative;
  padding: 28px 30px;
  overflow: hidden;
  isolation: isolate;
  border-radius: 18px;
  background:
    linear-gradient(120deg, rgba(255, 255, 255, 0.98) 0%, rgba(255, 244, 227, 0.92) 60%, rgba(255, 220, 178, 0.85) 100%);
  border: 1px solid rgba(255, 211, 154, 0.45);
}

.page-intro::before {
  content: "";
  position: absolute;
  top: -60%;
  right: -10%;
  width: 60%;
  height: 220%;
  background: var(--grad-aurora);
  filter: blur(40px);
  opacity: 0.32;
  z-index: -1;
  animation: ring-spin 18s linear infinite;
  pointer-events: none;
}

.page-intro h2 {
  font-family: 'Space Grotesk', 'Inter', sans-serif;
  font-size: clamp(28px, 3.2vw, 38px);
  letter-spacing: -0.6px;
}

.page-intro span {
  display: block;
  margin-top: 10px;
  color: #75675e;
  line-height: 1.7;
}

.article-intro,
.admin-intro {
  background:
    linear-gradient(120deg, rgba(255, 255, 255, 0.96), rgba(255, 244, 227, 0.92) 60%, rgba(255, 211, 154, 0.55));
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
  padding: 22px;
  display: grid;
  gap: 12px;
  cursor: pointer;
  transition: transform 320ms cubic-bezier(0.2, 0.8, 0.2, 1), border-color 320ms ease, box-shadow 320ms ease;
}

.notice-card,
.article-card {
  position: relative;
  isolation: isolate;
  overflow: hidden;
}

.notice-card::after,
.article-card::after {
  content: "";
  position: absolute;
  top: 0;
  left: -100%;
  width: 60%;
  height: 100%;
  background: linear-gradient(105deg, transparent, rgba(255, 211, 154, 0.22), transparent);
  transition: left 0.7s cubic-bezier(0.2, 0.8, 0.2, 1);
  pointer-events: none;
}

.notice-card:hover,
.article-card:hover {
  transform: translateY(-6px);
  border-color: rgba(233, 154, 90, 0.6);
  box-shadow: 0 26px 56px rgba(166, 71, 22, 0.18);
}

.notice-card:hover::after,
.article-card:hover::after {
  left: 130%;
}

.article-card footer {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  color: #87776b;
}

.article-card footer small {
  min-width: 0;
}

.article-card em {
  font-style: normal;
  color: #c65a1a;
  font-weight: 800;
}

.notice-card span,
.notice-card small,
.task-card small,
.timeline-row span,
.timeline-row small {
  color: #87776b;
}

.task-card {
  position: relative;
  border: 1px solid rgba(234, 223, 212, 0.85);
  border-radius: 16px;
  background: linear-gradient(180deg, #fff 0%, #fffaf4 100%);
  padding: 0;
  overflow: hidden;
  cursor: default;
  box-shadow: 0 14px 34px rgba(84, 62, 48, 0.07), inset 0 1px 0 rgba(255, 255, 255, 0.6);
  isolation: isolate;
  transition: transform 320ms cubic-bezier(0.2, 0.8, 0.2, 1), box-shadow 320ms ease, border-color 320ms ease;
}

.task-card::before {
  content: "";
  position: absolute;
  inset: 0;
  border-radius: inherit;
  z-index: -1;
  background: radial-gradient(360px 200px at 30% 0%, rgba(255, 211, 154, 0.28), transparent 60%);
  opacity: 0;
  transition: opacity 360ms ease;
  pointer-events: none;
}

.task-card:hover {
  transform: translateY(-6px);
  border-color: rgba(233, 154, 90, 0.55);
  box-shadow: 0 28px 60px rgba(166, 71, 22, 0.18);
}

.task-card:hover::before {
  opacity: 1;
}

.task-card.done {
  border-color: rgba(233, 130, 45, 0.6);
  background: linear-gradient(180deg, #fff 0%, #fff6ea 100%);
  box-shadow:
    0 18px 40px rgba(233, 130, 45, 0.18),
    inset 0 1px 0 rgba(255, 255, 255, 0.7);
}

.task-card.done::after {
  content: "✓";
  position: absolute;
  top: 12px;
  right: 14px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #ffb454, #e9791a);
  color: #fff;
  font-size: 14px;
  font-weight: 900;
  box-shadow: 0 6px 16px rgba(233, 121, 26, 0.42);
  z-index: 2;
}

.task-card.done .task-body {
  opacity: 0.82;
}

.task-checkbar {
  border-bottom: 1px solid #f3ece4;
  background: #fffaf4;
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
  color: #a98a73;
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
  background: rgba(43, 33, 28, 0.72);
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
  background: rgba(43, 33, 28, 0.88);
}

.task-body {
  padding: 18px;
  display: grid;
  gap: 12px;
}

.task-body span {
  color: #c65a1a;
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

.task-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.task-tags em {
  border-radius: 999px;
  background: linear-gradient(120deg, #fff4e3 0%, #ffe3bd 100%);
  border: 1px solid rgba(255, 211, 154, 0.45);
  color: #8a3d12;
  padding: 5px 11px;
  font-size: 12px;
  font-style: normal;
  font-weight: 800;
  letter-spacing: 0.2px;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.6);
}

.detail-tag-grid,
.guide-quality-grid {
  display: grid;
  gap: 10px;
}

.detail-tag-grid {
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  margin: 10px 0 14px;
}

.detail-tag-grid span {
  border: 1px solid #eadfd4;
  border-radius: 8px;
  background: #fff;
  color: #4e4038;
  padding: 8px 10px;
  font-size: 13px;
  font-weight: 900;
  overflow-wrap: anywhere;
}

.guide-quality-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
  margin-top: 4px;
}

.guide-quality-grid article {
  border: 1px solid #eee4da;
  border-radius: 8px;
  background: #fffaf4;
  padding: 10px 12px;
  min-width: 0;
}

.guide-quality-grid span {
  display: block;
  margin-bottom: 5px;
  color: #c65a1a;
  font-size: 12px;
  font-weight: 900;
}

.guide-quality-grid p {
  margin: 0;
  color: #665a52;
  line-height: 1.65;
  overflow-wrap: anywhere;
}

.task-dose {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.task-dose strong {
  border: 1px solid rgba(234, 223, 212, 0.85);
  border-radius: 10px;
  padding: 7px 11px;
  background: linear-gradient(180deg, #ffffff, #fffaf4);
  color: #2b211c;
  font-family: 'Space Grotesk', 'Inter', sans-serif;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: -0.2px;
  font-feature-settings: 'tnum';
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.6);
}

.task-body p {
  color: #71645b;
  line-height: 1.7;
}

.task-coach-note,
.task-alternative {
  border: 1px solid #eee4da;
  border-radius: 8px;
  background: #fffaf4;
  padding: 10px 12px;
}

.task-coach-note {
  color: #5f3928;
  font-weight: 800;
}

.task-alternative {
  color: #7a5825;
  background: #fff9e8;
}

.detail-disclosure {
  border: 1px solid rgba(234, 223, 212, 0.78);
  border-radius: 12px;
  background: linear-gradient(180deg, #fffdf9 0%, #fffaf4 100%);
  overflow: hidden;
  transition: border-color 240ms ease, box-shadow 240ms ease, background 240ms ease;
}

.detail-disclosure:hover {
  border-color: rgba(233, 154, 90, 0.45);
  box-shadow: 0 10px 22px rgba(166, 71, 22, 0.08);
}

.detail-disclosure[open] {
  border-color: rgba(233, 121, 26, 0.42);
  box-shadow: 0 16px 30px rgba(166, 71, 22, 0.1);
  background: #fff;
}

.detail-disclosure + .detail-disclosure {
  margin-top: 12px;
}

.detail-disclosure summary {
  min-height: 48px;
  padding: 12px 14px;
  display: grid;
  grid-template-columns: minmax(0, 150px) minmax(0, 1fr) auto;
  gap: 10px;
  align-items: center;
  cursor: pointer;
  list-style: none;
}

.detail-disclosure summary::-webkit-details-marker {
  display: none;
}

.detail-disclosure summary::after {
  content: "+";
  justify-self: end;
  width: 28px;
  height: 28px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #fff4e3, #ffe3bd);
  border: 1px solid rgba(255, 211, 154, 0.45);
  color: #8a3d12;
  font-weight: 900;
  font-size: 16px;
  line-height: 1;
  transition: transform 280ms cubic-bezier(0.2, 0.8, 0.2, 1), background 280ms ease, color 280ms ease, box-shadow 280ms ease;
}

.detail-disclosure[open] summary::after {
  content: "−";
  background: linear-gradient(135deg, #ffb454, #e9791a);
  border-color: rgba(255, 211, 154, 0.7);
  color: #fff;
  transform: rotate(180deg);
  box-shadow: 0 6px 14px rgba(233, 121, 26, 0.32);
}

.detail-disclosure summary span {
  color: #c65a1a;
  font-size: 12px;
  font-weight: 900;
}

.detail-disclosure summary strong {
  color: #7b371b;
  font-size: 14px;
  overflow-wrap: anywhere;
}

.detail-disclosure[open] {
  background: #fff;
}

.detail-disclosure[open] > :not(summary) {
  margin: 0 14px 14px;
}

.plan-secondary {
  margin-bottom: 18px;
}

.task-secondary {
  margin-top: 4px;
}

.nutrition-secondary {
  margin-top: 12px;
}

.guide-columns {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.guide-columns h4 {
  color: #7b371b;
}

.guide-columns ol,
.guide-columns ul {
  margin: 8px 0 0;
  padding-left: 18px;
  color: #665a52;
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
  color: #8a3d12;
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
  color: #c65a1a;
  font-weight: 900;
}

.reader-meta small {
  color: #87776b;
}

.reader-article h2 {
  margin: 0 0 24px;
  font-family: 'Space Grotesk', 'Inter', sans-serif;
  font-size: clamp(34px, 4vw, 58px);
  font-weight: 800;
  letter-spacing: -1px;
  line-height: 1.06;
  background: linear-gradient(120deg, #2b211c 0%, #7b371b 50%, #c65a1a 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.reader-article p {
  margin: 0 0 18px;
  color: #4d4038;
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
  border: 1px solid #eadfd4;
  border-radius: 8px;
  background: #fffaf4;
  padding: 12px;
  text-align: left;
  color: #4d4038;
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
  border-bottom: 1px solid #f3ece4;
}

.timeline-row p {
  margin: 0;
  color: #665a52;
}

.result-block,
.reader-box {
  border: 1px solid #eadfd4;
  border-radius: 8px;
  padding: 18px;
  background: #fffaf4;
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
    linear-gradient(135deg, #fff2e3, #f7f1df),
    repeating-linear-gradient(90deg, rgba(122, 55, 27, 0.07) 0 1px, transparent 1px 38px);
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
  align-items: end;
}

.form-grid.compact.guide-filter-row {
  grid-template-columns: repeat(auto-fit, minmax(190px, 1fr));
}

.guide-filter-row .field-action :deep(.el-button) {
  flex: 1 1 82px;
}

.guide-library-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.guide-library-card {
  border: 1px solid #eadfd4;
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
  border-color: #edb98f;
  box-shadow: 0 22px 50px rgba(84, 62, 48, 0.12);
}

.guide-library-card img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: linear-gradient(135deg, #fff2e3, #f7f1df);
  padding: 12px;
}

.guide-library-card > div:last-child {
  padding: 14px;
  display: grid;
  gap: 8px;
}

.guide-library-card span {
  color: #c65a1a;
  font-size: 12px;
  font-weight: 900;
}

.guide-library-card h3,
.guide-library-card p {
  margin: 0;
}

.guide-library-card p {
  color: #71645b;
  line-height: 1.65;
}

.clean-list {
  margin: 12px 0 0;
  padding-left: 18px;
  color: #665a52;
  line-height: 1.8;
}

.admin-tabs :deep(.el-tabs__content) {
  overflow: visible;
}

.admin-stack {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 18px;
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

.guide-admin-grid {
  align-items: start;
}

.guide-upload-panel {
  display: grid;
  grid-template-columns: 210px minmax(0, 1fr);
  gap: 16px;
  margin-top: 16px;
  border: 1px solid #eadfd4;
  border-radius: 8px;
  background: #fffaf4;
  padding: 14px;
}

.upload-preview {
  min-height: 150px;
  border-radius: 8px;
  overflow: hidden;
  background: linear-gradient(135deg, #fff2e3, #f7f1df);
}

.upload-preview img {
  width: 100%;
  height: 150px;
  object-fit: contain;
  display: block;
  padding: 8px;
}

.upload-preview .task-placeholder {
  min-height: 150px;
}

.upload-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  gap: 10px;
  min-width: 0;
}

.upload-actions small {
  max-width: 100%;
  color: #75675e;
  word-break: break-all;
}

.guide-admin-list {
  max-height: 720px;
  overflow: auto;
  padding-right: 4px;
}

.draft-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 12px;
}

.insights-intro {
  background:
    linear-gradient(120deg, rgba(255, 255, 255, 0.96), rgba(255, 243, 223, 0.96)),
    repeating-linear-gradient(90deg, rgba(233, 121, 26, 0.08) 0 1px, transparent 1px 42px);
}

.coach-tip-list {
  display: grid;
  gap: 14px;
}

.coach-tip-list article {
  display: grid;
  grid-template-columns: 12px minmax(0, 1fr);
  gap: 12px;
  border: 1px solid #f3ece4;
  border-radius: 8px;
  background: #fffaf4;
  padding: 14px;
}

.coach-tip-list span {
  width: 12px;
  min-height: 100%;
  border-radius: 999px;
  background: linear-gradient(180deg, #e9791a, #f7b66a);
}

.coach-tip-list strong {
  color: #2b211c;
}

.coach-tip-list p {
  margin: 6px 0 0;
  color: #71645b;
  line-height: 1.7;
}

.achievement-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.achievement-grid article {
  border: 1px solid #eadfd4;
  border-radius: 8px;
  background: #fffaf4;
  padding: 16px;
  display: grid;
  gap: 8px;
  transition: transform 190ms ease, border-color 190ms ease, box-shadow 190ms ease;
}

.achievement-grid article.done {
  border-color: #edb98f;
  background: linear-gradient(180deg, #fff, #fff6ea);
}

.achievement-grid article:hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 30px rgba(84, 62, 48, 0.08);
}

.achievement-grid span {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: #f4eadf;
  color: #a98a73;
}

.achievement-grid article.done span {
  background: #e9791a;
  color: #fff;
}

.achievement-grid strong {
  color: #2b211c;
}

.achievement-grid small {
  color: #75675e;
  font-weight: 800;
}

.macro-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin: 18px 0;
}

.macro-grid article {
  border: 1px solid #eadfd4;
  border-radius: 8px;
  background: #fffaf4;
  padding: 14px;
  display: grid;
  gap: 12px;
}

.macro-grid article div {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 10px;
}

.macro-grid span {
  color: #75675e;
  font-weight: 800;
}

.training-day-tip {
  position: relative;
  border-left: 4px solid #e9791a;
  border-radius: 12px;
  background: linear-gradient(120deg, #fff7ed 0%, #ffe3bd 100%);
  padding: 14px 18px;
  color: #4d4038;
  font-weight: 700;
  line-height: 1.7;
  box-shadow:
    0 8px 22px rgba(233, 121, 26, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.6);
}

.preference-summary {
  border: 1px solid #eadfd4;
  border-radius: 8px;
  background: #fffaf4;
  color: #4e4038;
  padding: 12px 14px;
  line-height: 1.7;
  font-weight: 800;
}

.nutrition-tip-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin: 16px 0;
}

.training-day-tip.rest {
  border-left-color: #ffb454;
  background: #fff9e8;
}

.nutrition-detail-grid,
.nutrition-watchouts {
  margin-top: 16px;
}

.table-shell {
  width: 100%;
  overflow-x: auto;
}

.admin-user-table {
  min-width: 870px;
}

:deep(.admin-user-table .cell) {
  white-space: normal;
  overflow-wrap: anywhere;
}

:deep(.admin-user-table .el-button + .el-button) {
  margin-left: 8px;
}

:deep(.el-dialog) {
  max-width: calc(100vw - 24px);
}

:deep(.el-dialog__body) {
  max-height: min(72vh, 680px);
  overflow: auto;
}

:deep(.el-dialog__footer) {
  display: flex;
  justify-content: flex-end;
  flex-wrap: wrap;
  gap: 8px;
}

:deep(.el-dialog__footer .el-button + .el-button) {
  margin-left: 0;
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
  .nutrition-tip-grid,
  .macro-grid,
  .reader-page,
  .guide-columns,
  .guide-quality-grid {
    grid-template-columns: 1fr;
  }

  .form-grid.compact {
    grid-template-columns: repeat(2, minmax(180px, 1fr));
  }

  .plan-execution-bar {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .plan-coach-strip,
  .plan-insight-grid,
  .cycle-grid,
  .adjustment-list,
  .achievement-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 820px) {
  .workspace-shell {
    grid-template-columns: 1fr;
    padding-left: 0;
    overflow-x: hidden;
  }

  .sidebar {
    position: sticky;
    left: auto;
    z-index: 20;
    height: auto;
    width: 100%;
    min-width: 0;
    padding: 10px 12px;
    gap: 10px;
    overflow: visible;
    box-shadow: 0 10px 30px rgba(43, 33, 28, 0.16);
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
    flex-wrap: wrap;
    width: 100%;
    max-width: 100%;
    min-width: 0;
    overflow: visible;
    gap: 8px;
    padding-bottom: 0;
  }

  .nav-list section {
    min-width: 0;
    display: contents;
  }

  .nav-list button {
    width: auto;
    min-width: max(96px, calc((100% - 16px) / 3));
    min-height: 36px;
    flex: 1 1 96px;
    justify-content: center;
    padding: 0 10px;
  }

  .nav-list button.active,
  .nav-list button:hover {
    transform: none;
  }

  .main {
    width: 100%;
    padding: 18px;
    overflow-x: hidden;
  }

  .hero-stage {
    grid-template-columns: 1fr;
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

  .topbar-actions,
  .hero-actions {
    display: grid;
    grid-template-columns: 1fr;
  }

  .topbar-actions .el-button,
  .hero-actions .el-button,
  .role-badge {
    width: 100%;
    margin-left: 0 !important;
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
  .product-command-grid,
  .product-flow-grid,
  .guide-library-grid,
  .guide-upload-panel,
  .nutrition-tip-grid,
  .plan-execution-bar,
  .plan-coach-strip,
  .plan-insight-grid,
  .cycle-grid,
  .adjustment-list,
  .achievement-grid,
  .form-grid,
  .form-grid.compact {
    grid-template-columns: 1fr;
  }

  .detail-disclosure summary {
    grid-template-columns: minmax(0, 1fr) auto;
  }

  .detail-disclosure summary span {
    grid-column: 1 / -1;
  }

  .overview-stack .metric-grid {
    display: grid !important;
    grid-template-columns: 1fr !important;
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

  .overview-stack .hero-visual {
    display: none;
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

  .nav-list button {
    min-width: calc((100% - 8px) / 2);
    flex-basis: calc((100% - 8px) / 2);
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

  .overview-stack > .metric-grid {
    display: grid !important;
    gap: 10px;
    grid-template-columns: 1fr !important;
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

@media (max-height: 680px) and (min-width: 821px) {
  .sidebar {
    padding: 16px;
    gap: 12px;
  }

  .brand-logo {
    width: 40px;
    height: 40px;
  }

  .brand small,
  .sidebar-user small {
    display: none;
  }

  .nav-list {
    gap: 8px;
  }

  .nav-list p {
    margin-bottom: 4px;
  }

  .nav-list button {
    min-height: 32px;
  }

  .sidebar-user {
    padding: 10px;
  }
}
</style>
