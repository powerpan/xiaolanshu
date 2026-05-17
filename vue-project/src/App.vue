<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { VideoPause, VideoPlay } from '@element-plus/icons-vue'
import backgroundMusic from './assets/橙色制高点.mp3'

const musicAudio = ref(null)
const musicPlaying = ref(false)
const musicBusy = ref(false)
const desktopSidebarWidth = 280
const desktopSidebarPadding = 24
const mobileEdgePadding = 12
const musicPosition = reactive({
  x: 192,
  y: 420,
})
const musicDrag = reactive({
  active: false,
  moved: false,
  pointerId: null,
  startX: 0,
  startY: 0,
  originX: 0,
  originY: 0,
})

const musicPlayerStyle = computed(() => ({
  '--music-x': `${musicPosition.x}px`,
  '--music-y': `${musicPosition.y}px`,
}))

const getDiscSize = () => (window.matchMedia('(max-width: 520px)').matches ? 54 : 64)
const getEdgePadding = () => (window.matchMedia('(max-width: 820px)').matches ? mobileEdgePadding : desktopSidebarPadding)
const getDefaultMusicX = () => (
  window.matchMedia('(max-width: 820px)').matches
    ? mobileEdgePadding
    : desktopSidebarWidth - desktopSidebarPadding - getDiscSize()
)

const clampMusicPosition = (x, y) => {
  const padding = getEdgePadding()
  const size = getDiscSize()
  return {
    x: Math.min(Math.max(x, padding), Math.max(padding, window.innerWidth - size - padding)),
    y: Math.min(Math.max(y, padding), Math.max(padding, window.innerHeight - size - padding)),
  }
}

const placeMusicPlayer = () => {
  const padding = getEdgePadding()
  const size = getDiscSize()
  const next = clampMusicPosition(musicPosition.x, musicPosition.y || window.innerHeight - size - padding)
  musicPosition.x = next.x
  musicPosition.y = next.y
}

const ensureAudio = () => {
  if (!musicAudio.value) {
    musicAudio.value = new Audio(backgroundMusic)
    musicAudio.value.loop = true
    musicAudio.value.volume = 0.36
    musicAudio.value.addEventListener('play', () => {
      musicPlaying.value = true
    })
    musicAudio.value.addEventListener('pause', () => {
      musicPlaying.value = false
    })
  }
  return musicAudio.value
}

const toggleMusic = async () => {
  const player = ensureAudio()
  if (musicBusy.value) return

  if (musicPlaying.value) {
    player.pause()
    return
  }

  musicBusy.value = true
  try {
    await player.play()
  } catch (error) {
    ElMessage.warning('浏览器阻止了音乐播放，请再点击一次')
  } finally {
    musicBusy.value = false
  }
}

const handleMusicPointerDown = (event) => {
  if (event.button !== undefined && event.button !== 0) return

  musicDrag.active = true
  musicDrag.moved = false
  musicDrag.pointerId = event.pointerId
  musicDrag.startX = event.clientX
  musicDrag.startY = event.clientY
  musicDrag.originX = musicPosition.x
  musicDrag.originY = musicPosition.y
  event.currentTarget.setPointerCapture?.(event.pointerId)
}

const handleMusicPointerMove = (event) => {
  if (!musicDrag.active || event.pointerId !== musicDrag.pointerId) return

  const dx = event.clientX - musicDrag.startX
  const dy = event.clientY - musicDrag.startY
  if (Math.hypot(dx, dy) > 4) {
    musicDrag.moved = true
  }

  const next = clampMusicPosition(musicDrag.originX + dx, musicDrag.originY + dy)
  musicPosition.x = next.x
  musicPosition.y = next.y
}

const finishMusicPointer = async (event) => {
  if (!musicDrag.active || event.pointerId !== musicDrag.pointerId) return

  event.currentTarget.releasePointerCapture?.(event.pointerId)
  const shouldToggle = !musicDrag.moved
  musicDrag.active = false
  musicDrag.pointerId = null

  if (shouldToggle) {
    await toggleMusic()
  }
}

const cancelMusicPointer = (event) => {
  if (event.pointerId !== musicDrag.pointerId) return
  musicDrag.active = false
  musicDrag.pointerId = null
}

onMounted(() => {
  musicPosition.x = getDefaultMusicX()
  musicPosition.y = window.innerHeight - getDiscSize() - getEdgePadding()
  placeMusicPlayer()
  window.addEventListener('resize', placeMusicPlayer)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', placeMusicPlayer)
  if (musicAudio.value) {
    musicAudio.value.pause()
    musicAudio.value.src = ''
    musicAudio.value = null
  }
})
</script>

<template>
  <router-view />
  <button
    class="music-disc"
    :class="{ playing: musicPlaying, dragging: musicDrag.active }"
    :style="musicPlayerStyle"
    type="button"
    :aria-label="musicPlaying ? '暂停橙色制高点' : '播放橙色制高点'"
    :title="musicPlaying ? '暂停橙色制高点' : '播放橙色制高点'"
    @pointerdown="handleMusicPointerDown"
    @pointermove="handleMusicPointerMove"
    @pointerup="finishMusicPointer"
    @pointercancel="cancelMusicPointer"
    @keydown.enter.prevent="toggleMusic"
    @keydown.space.prevent="toggleMusic"
  >
    <span class="disc-surface" aria-hidden="true"></span>
    <span class="disc-icon">
      <el-icon><component :is="musicPlaying ? VideoPause : VideoPlay" /></el-icon>
    </span>
    <span class="disc-equalizer" aria-hidden="true">
      <i></i>
      <i></i>
      <i></i>
    </span>
    <span class="disc-label">橙色制高点</span>
  </button>
</template>

<style>
* {
  box-sizing: border-box;
}

html,
body,
#app {
  margin: 0;
  min-height: 100vh;
  font-family:
    'Inter', 'Space Grotesk', ui-sans-serif, system-ui, -apple-system,
    BlinkMacSystemFont, 'PingFang SC', 'Microsoft YaHei', "Segoe UI", sans-serif;
  color: #2b211c;
}

html,
body {
  background: #fffaf4;
}

button,
input,
textarea,
select {
  font: inherit;
}

/* —— 音乐盘：保留磁带概念，叠加 conic 极光 + 光晕呼吸 —— */
.music-disc {
  position: fixed;
  left: var(--music-x);
  top: var(--music-y);
  z-index: 80;
  width: 64px;
  height: 64px;
  border: 0;
  border-radius: 50%;
  padding: 0;
  background: transparent;
  color: #fff;
  cursor: grab;
  touch-action: none;
  user-select: none;
  isolation: isolate;
  filter: drop-shadow(0 18px 30px rgba(79, 39, 21, 0.32));
  transition: filter 220ms ease, transform 220ms ease;
}

.music-disc::before,
.music-disc::after {
  content: "";
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}

.music-disc::before {
  inset: -10px;
  z-index: -2;
  background:
    conic-gradient(from 120deg,
      rgba(255, 180, 84, 0),
      rgba(255, 180, 84, 0.85),
      rgba(233, 121, 26, 0.0),
      rgba(255, 241, 223, 0.7),
      rgba(255, 211, 154, 0.0),
      rgba(233, 121, 26, 0.5),
      rgba(255, 180, 84, 0));
  opacity: 0.55;
  filter: blur(2px);
  animation: disc-halo 5s linear infinite;
}

.music-disc::after {
  inset: -14px;
  z-index: -3;
  border: 1px solid rgba(233, 121, 26, 0.24);
  opacity: 0;
}

.music-disc:hover,
.music-disc.playing {
  filter: drop-shadow(0 22px 36px rgba(233, 121, 26, 0.42));
  transform: translateY(-2px);
}

.music-disc.dragging {
  cursor: grabbing;
  transform: scale(1.04);
}

.music-disc.playing::after {
  animation: disc-pulse 1.9s ease-out infinite;
}

.disc-surface {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background:
    radial-gradient(circle at 50% 50%, #fff7e7 0 7px, #e9791a 8px 13px, #74290d 14px 17px, transparent 18px),
    repeating-radial-gradient(circle at 50% 50%, rgba(255, 255, 255, 0.24) 0 2px, rgba(43, 33, 28, 0.1) 3px 5px, transparent 6px 9px),
    conic-gradient(from -45deg, #2b211c, #7b371b, #f59e0b, #fff1df, #c65a1a, #2b211c);
  box-shadow:
    inset 0 0 0 4px rgba(255, 255, 255, 0.2),
    inset 0 0 24px rgba(43, 33, 28, 0.5);
}

.music-disc.playing .disc-surface {
  animation: disc-spin 3.4s linear infinite;
}

.disc-icon {
  position: absolute;
  inset: 20px;
  border-radius: 50%;
  background: rgba(43, 33, 28, 0.76);
  color: #fff;
  display: grid;
  place-items: center;
  box-shadow: 0 8px 18px rgba(43, 33, 28, 0.24);
}

.disc-icon .el-icon {
  font-size: 16px;
}

.disc-equalizer {
  position: absolute;
  right: 5px;
  bottom: 7px;
  height: 15px;
  display: inline-flex;
  align-items: end;
  gap: 2px;
  opacity: 0;
}

.disc-equalizer i {
  width: 3px;
  height: 7px;
  border-radius: 999px;
  background: #fff1df;
}

.music-disc.playing .disc-equalizer {
  opacity: 1;
}

.music-disc.playing .disc-equalizer i {
  animation: equalizer-bounce 760ms ease-in-out infinite;
}

.music-disc.playing .disc-equalizer i:nth-child(2) {
  animation-delay: 120ms;
}

.music-disc.playing .disc-equalizer i:nth-child(3) {
  animation-delay: 240ms;
}

.disc-label {
  position: absolute;
  left: 72px;
  top: 50%;
  max-width: 142px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 8px;
  padding: 7px 10px;
  background: rgba(43, 33, 28, 0.86);
  color: #fff;
  font-size: 12px;
  font-weight: 900;
  line-height: 1;
  white-space: nowrap;
  opacity: 0;
  transform: translate(-8px, -50%);
  pointer-events: none;
  backdrop-filter: blur(10px);
  transition: opacity 180ms ease, transform 180ms ease;
}

.music-disc:hover .disc-label {
  opacity: 1;
  transform: translate(0, -50%);
}

@keyframes disc-spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes disc-halo {
  to {
    transform: rotate(360deg);
  }
}

@keyframes disc-pulse {
  0% {
    opacity: 0.5;
    transform: scale(0.92);
  }
  100% {
    opacity: 0;
    transform: scale(1.14);
  }
}

@keyframes equalizer-bounce {
  0%,
  100% {
    height: 6px;
  }
  50% {
    height: 16px;
  }
}

@media (max-width: 820px) {
  .disc-label {
    display: none;
  }
}

@media (max-width: 520px) {
  .music-disc {
    width: 54px;
    height: 54px;
  }

  .disc-icon {
    inset: 17px;
  }

  .disc-icon .el-icon {
    font-size: 13px;
  }
}
</style>
