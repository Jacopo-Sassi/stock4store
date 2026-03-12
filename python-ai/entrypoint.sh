#!/bin/sh
set -e

MODEL_PATH="/app/models/mistral-7b-instruct-v0.2.Q4_K_M.gguf"
MODEL_URL="https://huggingface.co/TheBloke/Mistral-7B-Instruct-v0.2-GGUF/resolve/main/mistral-7b-instruct-v0.2.Q4_K_M.gguf"

if [ ! -f "$MODEL_PATH" ]; then
  echo "Model not found, downloading..."
  mkdir -p /app/models
  curl -L "$MODEL_URL" -o "$MODEL_PATH"
else
  echo "Model already exists"
fi

exec "$@"