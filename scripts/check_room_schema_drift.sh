#!/bin/bash

set -e

echo "🔍 Checking Room schema drift..."

# Determine base branch/commit for diff
if [[ "$GITHUB_EVENT_NAME" == "pull_request" ]]; then
  echo "📦 Pull request detected"
  BASE_BRANCH="$GITHUB_BASE_REF"
  BASE_REF="origin/${GITHUB_BASE_REF}"
  git fetch origin "$BASE_BRANCH"
else
  echo "📦 Push detected"
  BASE_REF=$(git merge-base HEAD HEAD^)
fi

echo "🔁 Comparing with: $BASE_REF"

# Get status of changed schema files (A=Added, M=Modified, D=Deleted, etc.)
SCHEMA_DIFFS=$(git diff --name-status "$BASE_REF" HEAD -- app/schemas/)

MODIFIED_OR_DELETED=()

while read -r status file; do
  if [[ "$status" == "M" || "$status" == "D" ]]; then
    MODIFIED_OR_DELETED+=("$file")
  fi
done <<< "$SCHEMA_DIFFS"

if [[ ${#MODIFIED_OR_DELETED[@]} -eq 0 ]]; then
  echo "✅ No Room schema changes detected between commits."
else
  echo "❌ Room schema modifications detected:"
  for file in "${MODIFIED_OR_DELETED[@]}"; do
    echo " - $file"
  done
  echo "💡 If this change was intentional, check if you have done the following steps:"
  echo "   - Bump the Room DB version"
  echo "   - Add proper migration"
  echo "   - Regenerate schema with: ./gradlew assembleDebug"
  echo "   - Write instrumentation test to validate migration"
  exit 1
fi
