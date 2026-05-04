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
    <span>{{ label }}</span>
    <strong>{{ value }}</strong>
    <small>{{ unit }}</small>
  </article>
</template>

<style scoped>
.metric-card {
  min-height: 92px;
  padding: 16px;
  display: grid;
  align-content: center;
  border: 1px solid #eadfd4;
  border-left-width: 5px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 14px 40px rgba(84, 62, 48, 0.06);
  position: relative;
  overflow: hidden;
  animation: surface-in 520ms cubic-bezier(0.2, 0.8, 0.2, 1) both;
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

@media (max-width: 520px) {
  .metric-card {
    min-height: 78px;
    padding: 12px;
  }
}
</style>
