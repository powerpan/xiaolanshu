#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

BACKEND_PORT="${BACKEND_PORT:-8080}"
FRONTEND_PORT="${FRONTEND_PORT:-5173}"

print_step() {
  printf '\n[%s] %s\n' "$(date '+%H:%M:%S')" "$1"
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

stop_project_port() {
  local name="$1"
  local port="$2"
  local stopped=0
  local skipped=0
  local cwd

  for pid in $(port_pids "$port"); do
    cwd="$(pid_cwd "$pid")"
    if [[ "$cwd" == "$ROOT_DIR"* ]]; then
      printf 'Stopping %s PID %s on port %s\n' "$name" "$pid" "$port"
      stop_pid "$pid"
      stopped=1
    else
      printf 'Skipping %s PID %s on port %s outside this project: %s\n' "$name" "$pid" "$port" "${cwd:-unknown}"
      skipped=1
    fi
  done

  if [ "$stopped" -eq 0 ] && [ "$skipped" -eq 0 ]; then
    printf '%s is not running on port %s\n' "$name" "$port"
  fi
}

if ! command -v lsof >/dev/null 2>&1; then
  printf 'Missing command: lsof\n' >&2
  exit 1
fi

print_step "Stopping local dev services"
stop_project_port "Backend" "$BACKEND_PORT"
stop_project_port "Frontend" "$FRONTEND_PORT"

print_step "Done"
