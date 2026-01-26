#!/usr/bin/env bash
set -euo pipefail

# --- CONFIGURATION ---
TARGET_USER="agent"
# On mémorise le chemin absolu avant toute bascule
ABS_REPO_DIR="$(cd "$(dirname "$0")" && pwd)"
INTERVAL="${2:-180}"

## --- AUTO-BASCULE SÉCURISÉE ---
#if [ "$(whoami)" != "$TARGET_USER" ]; then
#  echo "🛡️  Passage sous l'utilisateur '$TARGET_USER'..."
#  # On relance le script en passant le chemin absolu comme argument
#  exec sudo -u "$TARGET_USER" "$0" "$ABS_REPO_DIR" "$INTERVAL"
#fi

REPO_DIR="${1:-$(pwd)}"
#cd "$REPO_DIR" || { echo "❌ Erreur critique : Impossible d'accéder à $REPO_DIR"; exit 1; }

# --- VÉRIFICATIONS DE LA CONSTITUTION ---
if [[ ! -f "AGENTS.md" || ! -f "IMPLEMENTATION_PLAN.md" ]]; then
  echo "❌ Erreur : AGENTS.md ou IMPLEMENTATION_PLAN.md manquant dans $(pwd)." >&2
  exit 1
fi

REPO="$(gh repo view --json nameWithOwner -q '.nameWithOwner' 2>/dev/null || true)"
STATE_DIR=".codex-pr-watch/state"
mkdir -p "$STATE_DIR"
STATE_FILE="$STATE_DIR/seen-$(echo "$REPO" | tr '/:' '__').txt"
touch "$STATE_FILE"

# --- LOGIQUE DE RÉCUPÉRATION (GraphQL) ---
seen_ids_json() {
  local prefix="$1"
  awk -v p="$prefix" 'index($0, p)==1 { sub(p,""); print }' "$STATE_FILE" | jq -Rsc 'split("\n") | map(select(length>0))'
}

read -r -d '' GQL <<'EOF' || true
query($owner:String!, $name:String!, $number:Int!) {
  repository(owner:$owner, name:$name) {
    pullRequest(number:$number) {
      number, title, url, headRefName
      comments(first:100) { nodes { id body createdAt author { login } } }
      reviewThreads(first:100) {
        nodes { id isResolved path line originalLine
          comments(first:100) { nodes { id body createdAt author { login } } }
        }
      }
    }
  }
}
EOF

# --- TRAITEMENT DES PR ---
poll_once() {
  local pr_list
  pr_list="$(gh pr list --state open --json number,title,url,headRefName 2>/dev/null || echo '[]')"

  echo "$pr_list" | jq -c '.[]?' | while IFS= read -r pr; do
    number=$(echo "$pr" | jq -r '.number')
    headRefName=$(echo "$pr" | jq -r '.headRefName')
    owner="${REPO%%/*}"
    name="${REPO##*/}"

    resp="$(gh api graphql -f query="$GQL" -f owner="$owner" -f name="$name" -F number="$number" 2>/dev/null || echo '{}')"
    prdata=$(echo "$resp" | jq -c '.data.repository.pullRequest // {}')
    [[ "$prdata" == "{}" ]] && continue

    # Filtrage des nouveaux commentaires
    new_issue=$(jq -c --argjson seen "$(seen_ids_json "ic:")" '[.comments.nodes[]? | select(.id as $id | ($seen | index($id)) == null)]' <<<"$prdata")
    new_inline=$(jq -c --argjson seen "$(seen_ids_json "rc:")" '[.reviewThreads.nodes[]? | select(.isResolved == false) | .comments.nodes[]? | select(.id as $id | ($seen | index($id)) == null)]' <<<"$prdata")

    if [[ "$(echo "$new_issue" | jq 'length')" -eq 0 && "$(echo "$new_inline" | jq 'length')" -eq 0 ]]; then continue; fi

    echo "--- 🧠 Évaluation critique de la PR #$number ---"

    if codex exec --sandbox danger-full-access "
MISSION: Process feedback for PR #$number while strictly adhering to the project Constitution.

HIERARCHY OF TRUTH (Strict Priority):
1. AGENTS.md
2. IMPLEMENTATION_PLAN.md
3. User Feedback from PR

MANDATE:
- READ AGENTS.md and IMPLEMENTATION_PLAN.md FIRST.
- CRITIQUE: If feedback violates AGENTS.md or IMPLEMENTATION_PLAN.md (e.g. premature optimization of a placeholder, requested changes on not final code), you MUST REFUSE it.
- REFUSAL: Use 'gh pr comment $number --body \"[CRITICAL REFUSAL] ... justification ...\"'.
- ACCEPTANCE: Apply changes to branch '$headRefName', commit and push.

STEPS:
1) git fetch --all && git checkout \"$headRefName\" && git pull --ff-only
2) Evaluate feedback vs Constitution.
3) Apply changes OR comment to refuse.
4) If changed: commit and push your changes (do not stage and commit changes that are not done by you)
"; then
      echo "✅ PR #$number traitée."
      echo "$new_issue" | jq -r '.[]?.id' | while read -r id; do echo "ic:$id" >> "$STATE_FILE"; done
      echo "$new_inline" | jq -r '.[]?.id' | while read -r id; do echo "rc:$id" >> "$STATE_FILE"; done
    fi
  done
}

echo "🚀 Monitoring lancé sur $REPO (User: $(whoami))"
while true; do poll_once || true; sleep "$INTERVAL"; done