#!/bin/bash
# ============================================================
# Script chạy đồng thời Backend (Spring Boot) và Frontend (Java Swing)
# Yêu cầu: Java 17+, Maven
# ============================================================

# Đường dẫn tuyệt đối tới thư mục chứa script này
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BE_DIR="$SCRIPT_DIR/be"
FE_DIR="$SCRIPT_DIR/fe"

BE_PORT=8080
LOG_BE="$SCRIPT_DIR/be.log"
LOG_FE="$SCRIPT_DIR/fe.log"

echo "========================================"
echo "  Smart Restaurant — Khởi động hệ thống"
echo "========================================"

# --- Kiểm tra Maven ---
if ! command -v mvn &> /dev/null; then
    echo "[LỖI] Không tìm thấy 'mvn'. Hãy cài Apache Maven và thêm vào PATH."
    exit 1
fi

# --- Tắt process cũ đang giữ cổng 8080 (nếu có) ---
OLD_PID=$(lsof -ti tcp:$BE_PORT 2>/dev/null)
if [ -n "$OLD_PID" ]; then
    echo "[INFO] Đang tắt process cũ trên port $BE_PORT (PID: $OLD_PID)..."
    kill -9 $OLD_PID 2>/dev/null
    sleep 1
fi

# --- Khởi động Backend ---
echo ""
echo "[BE] Đang khởi động Backend (Spring Boot) trên port $BE_PORT..."
echo "[BE] Log: $LOG_BE"
cd "$BE_DIR" || { echo "[LỖI] Không tìm thấy thư mục be/"; exit 1; }
mvn spring-boot:run > "$LOG_BE" 2>&1 &
BE_PID=$!
echo "[BE] PID = $BE_PID"

# --- Chờ Backend sẵn sàng ---
echo ""
echo "[BE] Đang chờ Backend khởi động..."
WAIT=0
MAX_WAIT=120  # tối đa 2 phút
until curl -s "http://localhost:$BE_PORT/api/v1/auth/login" -o /dev/null 2>/dev/null || \
      curl -s "http://localhost:$BE_PORT" -o /dev/null 2>/dev/null; do
    sleep 2
    WAIT=$((WAIT + 2))
    echo -n "."
    if [ $WAIT -ge $MAX_WAIT ]; then
        echo ""
        echo "[CẢNH BÁO] Backend chưa phản hồi sau ${MAX_WAIT}s. Kiểm tra $LOG_BE"
        echo "[INFO] Vẫn tiếp tục khởi động Frontend..."
        break
    fi
done
echo ""
echo "[BE] Backend đã sẵn sàng!"

# --- Khởi động Frontend ---
echo ""
echo "[FE] Đang khởi động Frontend (Java Swing)..."
echo "[FE] Log: $LOG_FE"
cd "$FE_DIR" || { echo "[LỖI] Không tìm thấy thư mục fe/"; kill $BE_PID; exit 1; }
mvn exec:java -Dexec.mainClass="com.restaurant.Main" > "$LOG_FE" 2>&1
FE_EXIT=$?

# --- Khi Frontend đóng, tắt Backend ---
echo ""
echo "[FE] Frontend đã đóng (exit code: $FE_EXIT)."
echo "[BE] Đang tắt Backend (PID: $BE_PID)..."
kill $BE_PID 2>/dev/null
wait $BE_PID 2>/dev/null
echo "[BE] Backend đã dừng."
echo ""
echo "Hệ thống đã tắt hoàn toàn."
