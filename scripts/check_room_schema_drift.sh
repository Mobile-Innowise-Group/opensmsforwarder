#!/bin/bash

set -e

echo "🔍 Checking Room schema drift..."

# Determine base branch/commit for diff
if [[ "$GITHUB_EVENT_NAME" == "pull_request" ]]; then
  echo "📦 Pull request detected"
  BASE_REF="origin/${GITHUB_BASE_REF}"
else
  echo "📦 Push detected"
  BASE_REF=$(git merge-base HEAD HEAD^)
fi

echo "🔁 Comparing with: $BASE_REF"
git fetch origin "$BASE_REF"

# Show changed schema files between current HEAD and base ref
DIFF_FILES=$(git diff --name-only "$BASE_REF" HEAD -- app/schemas/)

if [[ -z "$DIFF_FILES" ]]; then
  echo "✅ No Room schema changes detected between commits."
else
  echo "❌ Room schema has changed between commits:"
  echo "$DIFF_FILES"
  echo "💡 If this change was intentional:"
  echo "   - Bump the Room DB version"
  echo "   - Add proper migration"
  echo "   - Regenerate schema with: ./gradlew assembleDebug"
  echo "   - Write instrumentation test to validate migration"
  exit 1
fi
