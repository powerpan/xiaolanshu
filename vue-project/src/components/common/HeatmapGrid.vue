<script setup>
defineProps({
  days: {
    type: Array,
    default: () => [],
  },
  large: {
    type: Boolean,
    default: false,
  },
  compact: {
    type: Boolean,
    default: false,
  },
})
</script>

<template>
  <div class="heatmap-grid" :class="{ large, compact }">
    <span
      v-for="day in days"
      :key="day.key"
      :class="`level-${day.level}`"
      :title="`${large ? day.key : day.label} · ${day.minutes || 0} 分钟 ${day.mood || ''}`"
    >
      <small v-if="large">{{ day.label }}</small>
    </span>
  </div>
</template>

<style scoped>
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

.heatmap-grid.compact span {
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

@media (max-width: 820px) {
  .heatmap-grid {
    grid-template-columns: repeat(7, minmax(0, 1fr));
  }
}
</style>
