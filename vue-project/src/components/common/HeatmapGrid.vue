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
  position: relative;
  min-width: 0;
  min-height: 34px;
  border-radius: 7px;
  border: 1px solid rgba(234, 223, 212, 0.78);
  background: linear-gradient(160deg, #fdf3e6, #f5e6d3);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.55);
  transition: transform 220ms cubic-bezier(0.2, 0.8, 0.2, 1),
              border-color 220ms ease,
              box-shadow 220ms ease,
              filter 220ms ease;
  animation: heat-pop 520ms cubic-bezier(0.2, 0.8, 0.2, 1) both;
}

@keyframes heat-pop {
  from {
    opacity: 0;
    transform: translateY(6px) scale(0.92);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.heatmap-grid span:nth-child(7n+2) { animation-delay: 30ms; }
.heatmap-grid span:nth-child(7n+3) { animation-delay: 60ms; }
.heatmap-grid span:nth-child(7n+4) { animation-delay: 90ms; }
.heatmap-grid span:nth-child(7n+5) { animation-delay: 120ms; }
.heatmap-grid span:nth-child(7n+6) { animation-delay: 150ms; }
.heatmap-grid span:nth-child(7n+7) { animation-delay: 180ms; }

.heatmap-grid.compact span {
  min-height: 24px;
  border-radius: 5px;
}

.heatmap-grid span:hover {
  transform: translateY(-3px) scale(1.06);
  border-color: rgba(233, 121, 26, 0.65);
  box-shadow:
    0 8px 18px rgba(166, 71, 22, 0.22),
    inset 0 1px 0 rgba(255, 255, 255, 0.7);
  filter: saturate(1.18) brightness(1.04);
  z-index: 1;
}

.heatmap-grid .level-1 {
  background: linear-gradient(160deg, #ffe6c7, #ffd9aa);
  border-color: rgba(255, 200, 130, 0.5);
}
.heatmap-grid .level-2 {
  background: linear-gradient(160deg, #ffc078, #ffa84a);
  border-color: rgba(255, 154, 60, 0.55);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.5), 0 4px 12px rgba(255, 154, 60, 0.18);
}
.heatmap-grid .level-3 {
  background: linear-gradient(160deg, #f59e0b, #e9791a);
  border-color: rgba(233, 121, 26, 0.6);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.42), 0 6px 16px rgba(233, 121, 26, 0.28);
}
.heatmap-grid .level-4 {
  background: linear-gradient(160deg, #c65a1a, #7b371b);
  border-color: rgba(122, 55, 27, 0.7);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.32), 0 8px 22px rgba(122, 55, 27, 0.34);
}

.heatmap-grid.large {
  grid-template-columns: repeat(7, minmax(0, 1fr));
  gap: 10px;
}

.heatmap-grid.large span {
  min-height: 72px;
  border-radius: 12px;
  display: flex;
  align-items: flex-end;
  padding: 10px;
  font-feature-settings: 'tnum';
}

.heatmap-grid.large small {
  color: rgba(43, 33, 28, 0.72);
  font-weight: 800;
  font-size: 12px;
  letter-spacing: 0.4px;
}

.heatmap-grid.large .level-3 small,
.heatmap-grid.large .level-4 small {
  color: #fff;
  text-shadow: 0 1px 2px rgba(43, 33, 28, 0.32);
}

@media (max-width: 820px) {
  .heatmap-grid {
    grid-template-columns: repeat(7, minmax(0, 1fr));
  }
}

@media (prefers-reduced-motion: reduce) {
  .heatmap-grid span {
    animation: none;
  }
}
</style>
