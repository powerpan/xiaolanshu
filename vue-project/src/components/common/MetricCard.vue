<script setup>
defineProps({
  label: {
    type: String,
    required: true,
  },
  value: {
    type: [String, Number],
    default: 0,
  },
  unit: {
    type: String,
    default: '',
  },
  tone: {
    type: String,
    default: 'green',
  },
})
</script>

<template>
  <article class="metric-card" :class="tone">
    <span class="metric-label">{{ label }}</span>
    <strong class="metric-value">{{ value }}</strong>
    <small class="metric-unit">{{ unit }}</small>
    <span class="metric-spark" aria-hidden="true"></span>
  </article>
</template>

<style scoped>
.metric-card {
  min-height: 96px;
  padding: 18px 18px 20px;
  display: grid;
  align-content: center;
  border: 1px solid rgba(234, 223, 212, 0.9);
  border-radius: 14px;
  background:
    linear-gradient(180deg, #ffffff 0%, #fffaf4 100%);
  box-shadow: 0 14px 40px rgba(84, 62, 48, 0.06), inset 0 1px 0 rgba(255, 255, 255, 0.6);
  position: relative;
  overflow: hidden;
  isolation: isolate;
  animation: surface-in 520ms cubic-bezier(0.2, 0.8, 0.2, 1) both;
  transition: transform 280ms cubic-bezier(0.2, 0.8, 0.2, 1), box-shadow 280ms ease, border-color 280ms ease;
}

/* —— 流光边框 (条件渲染：仅 hover 时显示) —— */
.metric-card::before {
  content: "";
  position: absolute;
  inset: -1px;
  z-index: -1;
  border-radius: inherit;
  padding: 1px;
  background: conic-gradient(from var(--angle, 0deg),
    transparent 0%, rgba(255, 180, 84, 0.85) 25%, transparent 50%);
  -webkit-mask:
    linear-gradient(#fff 0 0) content-box,
    linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 320ms ease;
}

/* —— 左侧高亮条 —— */
.metric-card::after {
  content: "";
  position: absolute;
  left: 14px;
  top: 18px;
  bottom: 18px;
  width: 3px;
  border-radius: 999px;
  background: linear-gradient(180deg, #e9791a, #ffb454);
  box-shadow: 0 0 14px rgba(233, 121, 26, 0.4);
}

/* —— 角落微光 —— */
.metric-spark {
  position: absolute;
  right: -28px;
  bottom: -28px;
  width: 110px;
  height: 110px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 211, 154, 0.55), transparent 70%);
  opacity: 0.55;
  transition: opacity 320ms ease, transform 320ms ease;
  pointer-events: none;
}

.metric-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 22px 50px rgba(166, 71, 22, 0.16);
  border-color: rgba(233, 154, 90, 0.55);
}

.metric-card:hover .metric-spark {
  opacity: 0.85;
  transform: scale(1.18);
}

.metric-card:hover::before {
  opacity: 1;
  animation: metric-rotate 4.5s linear infinite;
}

@keyframes metric-rotate {
  to { --angle: 360deg; }
}

@property --angle {
  syntax: '<angle>';
  initial-value: 0deg;
  inherits: false;
}

.metric-card.green::after { background: linear-gradient(180deg, #e9791a, #ffb454); }
.metric-card.orange::after { background: linear-gradient(180deg, #f59e0b, #ffd39a); }
.metric-card.blue::after { background: linear-gradient(180deg, #8a5a44, #c5917b); }
.metric-card.ink::after { background: linear-gradient(180deg, #3a2117, #7b371b); }

.metric-card .metric-label {
  margin-left: 12px;
  color: #87776b;
  font-weight: 800;
  font-size: 12px;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.metric-card .metric-value {
  margin: 8px 0 2px 12px;
  font-family: 'Space Grotesk', 'Inter', sans-serif;
  font-size: 32px;
  font-weight: 800;
  line-height: 1;
  letter-spacing: -0.5px;
  background: linear-gradient(120deg, #2b211c 0%, #7b371b 55%, #e9791a 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-feature-settings: 'tnum';
}

.metric-card.orange .metric-value {
  background: linear-gradient(120deg, #7b371b 0%, #e9791a 50%, #ffb454 100%);
  -webkit-background-clip: text;
  background-clip: text;
}

.metric-card.ink .metric-value {
  background: linear-gradient(120deg, #1c130e 0%, #3a2117 60%, #7b371b 100%);
  -webkit-background-clip: text;
  background-clip: text;
}

.metric-card .metric-unit {
  margin-left: 12px;
  color: #c65a1a;
  font-weight: 800;
  font-size: 12px;
  letter-spacing: 0.5px;
}

@media (max-width: 520px) {
  .metric-card {
    min-height: 82px;
    padding: 14px;
  }

  .metric-card .metric-value {
    font-size: 26px;
  }
}
</style>
