#!/usr/bin/env bash
# Manual test cases of the assignment (needs curl). Run from anywhere:  bash tests/run-tests.sh
# Start user-service first (port 9090) and order-service (port 8080).
BASE="${BASE:-http://localhost:8080}"
DIR="$(cd "$(dirname "$0")" && pwd)/requests"

post() {   # $1 = title, $2 = json file
  echo
  echo "=================== $1 ==================="
  curl -s -i -X POST "$BASE/api/orders" -H "Content-Type: application/json" --data @"$DIR/$2"
  echo
}

post "Case 1: user exists  -> expect 201 Created" valid.json
post "Case 2: user not found -> expect 404, nothing saved" user-not-found.json
post "Case 4: invalid data -> expect 400 Bad Request" invalid.json

echo
echo "=================== Saved orders (expect ONLY the order of case 1) ==================="
curl -s -i "$BASE/api/orders"
echo

echo
read -r -p "Case 3: now STOP user-service (Ctrl+C in its terminal), then press Enter... " _
post "Case 3: user-service down -> expect 503, nothing saved" valid.json

echo
echo "=================== Saved orders again (still only the order of case 1) ==================="
curl -s -i "$BASE/api/orders"
echo
