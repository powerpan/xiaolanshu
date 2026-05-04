#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$ROOT_DIR/fitnessGuidance"
FRONTEND_DIR="$ROOT_DIR/vue-project"
LOG_DIR="$ROOT_DIR/.dev-logs"

BACKEND_PORT="${BACKEND_PORT:-8080}"
FRONTEND_PORT="${FRONTEND_PORT:-5173}"

BACKEND_LOG="$LOG_DIR/backend.log"
FRONTEND_LOG="$LOG_DIR/frontend.log"

PIDS=()

print_step() {
  printf '\n[%s] %s\n' "$(date '+%H:%M:%S')" "$1"
}

need_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    printf 'Missing command: %s\n' "$1" >&2
    exit 1
  fi
}

port_in_use() {
  local port="$1"
  if command -v lsof >/dev/null 2>&1; then
    lsof -nP -iTCP:"$port" -sTCP:LISTEN >/dev/null 2>&1
  else
    return 1
  fi
}

port_pids() {
  local port="$1"
  if command -v lsof >/dev/null 2>&1; then
    lsof -nP -tiTCP:"$port" -sTCP:LISTEN 2>/dev/null || true
  fi
}

pid_cwd() {
  local pid="$1"
  lsof -a -p "$pid" -d cwd -Fn 2>/dev/null | awk '/^n/ { print substr($0, 2); exit }'
}

stop_pid() {
  local pid="$1"
  if ! kill -0 "$pid" >/dev/null 2>&1; then
    return 0
  fi
  kill "$pid" >/dev/null 2>&1 || true
  for _ in $(seq 1 20); do
    if ! kill -0 "$pid" >/dev/null 2>&1; then
      return 0
    fi
    sleep 0.3
  done
  kill -9 "$pid" >/dev/null 2>&1 || true
}

ensure_port_available() {
  local name="$1"
  local port="$2"
  local cwd
  local found=0
  local project_pids=()

  for pid in $(port_pids "$port"); do
    found=1
    cwd="$(pid_cwd "$pid")"
    if [[ "$cwd" != "$ROOT_DIR"* ]]; then
      printf '%s port %s is already used by PID %s outside this project: %s\n' "$name" "$port" "$pid" "${cwd:-unknown}" >&2
      printf 'Stop that service first, or run with another port, for example: %s_PORT=5174 ./start-dev.command\n' "$(printf '%s' "$name" | tr '[:lower:]' '[:upper:]')" >&2
      exit 1
    fi
    project_pids+=("$pid")
  done

  if [ "$found" -eq 1 ]; then
    print_step "Stopping existing $name process on port $port"
    for pid in "${project_pids[@]}"; do
      stop_pid "$pid"
    done
  fi

  if port_in_use "$port"; then
    printf '%s port %s is still in use after stopping project processes.\n' "$name" "$port" >&2
    exit 1
  fi
}

cleanup() {
  if [ "${#PIDS[@]}" -gt 0 ]; then
    print_step "Stopping services"
    for pid in "${PIDS[@]}"; do
      if kill -0 "$pid" >/dev/null 2>&1; then
        kill "$pid" >/dev/null 2>&1 || true
      fi
    done
    wait "${PIDS[@]}" >/dev/null 2>&1 || true
  fi
}

wait_for_port() {
  local name="$1"
  local port="$2"
  local pid="$3"
  local log_file="$4"

  for _ in $(seq 1 90); do
    if port_in_use "$port"; then
      printf '%s is ready: http://127.0.0.1:%s\n' "$name" "$port"
      return 0
    fi
    if ! kill -0 "$pid" >/dev/null 2>&1; then
      printf '%s failed to start. Last log lines:\n' "$name" >&2
      tail -80 "$log_file" >&2 || true
      exit 1
    fi
    sleep 1
  done

  printf '%s did not become ready on port %s. Last log lines:\n' "$name" "$port" >&2
  tail -80 "$log_file" >&2 || true
  exit 1
}

trap cleanup EXIT INT TERM

need_command java
need_command node
need_command npm
need_command sh

if [ ! -d "$BACKEND_DIR" ] || [ ! -d "$FRONTEND_DIR" ]; then
  printf 'Run this script from the project root, or keep it in the project root.\n' >&2
  exit 1
fi

ensure_port_available "Backend" "$BACKEND_PORT"
ensure_port_available "Frontend" "$FRONTEND_PORT"

mkdir -p "$LOG_DIR"
: > "$BACKEND_LOG"
: > "$FRONTEND_LOG"

if [ ! -x "$BACKEND_DIR/mvnw" ]; then
  chmod +x "$BACKEND_DIR/mvnw"
fi

if [ ! -d "$FRONTEND_DIR/node_modules" ]; then
  print_step "Installing frontend dependencies"
  (cd "$FRONTEND_DIR" && npm install)
fi

print_step "Starting backend on port $BACKEND_PORT"
(
  cd "$BACKEND_DIR"
  MAVEN_USER_HOME=.m2 ./mvnw spring-boot:run \
    -Dspring-boot.run.arguments="--server.port=$BACKEND_PORT"
) >"$BACKEND_LOG" 2>&1 &
BACKEND_PID=$!
PIDS+=("$BACKEND_PID")

wait_for_port "Backend" "$BACKEND_PORT" "$BACKEND_PID" "$BACKEND_LOG"

print_step "Starting frontend on port $FRONTEND_PORT"
(
  cd "$FRONTEND_DIR"
  npm run dev -- --host 127.0.0.1 --port "$FRONTEND_PORT"
) >"$FRONTEND_LOG" 2>&1 &
FRONTEND_PID=$!
PIDS+=("$FRONTEND_PID")

wait_for_port "Frontend" "$FRONTEND_PORT" "$FRONTEND_PID" "$FRONTEND_LOG"

cat <<EOF

All services are running.
Frontend: http://127.0.0.1:$FRONTEND_PORT
Backend:  http://127.0.0.1:$BACKEND_PORT

Logs:
  $BACKEND_LOG
  $FRONTEND_LOG

Press Ctrl+C to stop both services.
EOF

wait "${PIDS[@]}"
