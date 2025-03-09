package net.ss.dungeonwaves.event;

import net.minecraftforge.eventbus.api.Event;
import net.ss.dungeonwaves.util.Log;

import java.util.HashMap;
import java.util.Map;

public class EventCancel {
    private static final Map<String, Long> lastLogTime = new HashMap<>();
    private static final long LOG_COOLDOWN_MS = 2000; // 2 giây cooldown để tránh spam log

    public static void cancel(Event event) {
        if (event == null) return;

        String eventName = event.getClass().getSimpleName();
        long currentTime = System.currentTimeMillis();

        // ✅ Kiểm tra nếu cần log hay không
        if (shouldLog(eventName, currentTime)) {
            if (event.isCancelable()) {
                event.setCanceled(true);
                Log.d("⛔ Sự kiện đã bị hủy: " + eventName);
            } else if (event.hasResult()) {
                event.setResult(Event.Result.DENY);
                Log.w("⚠ Sự kiện không thể bị hủy nhưng đã đặt kết quả thành DENY: " + eventName);
            } else {
                Log.w("⚠ Không thể hủy sự kiện hoặc đặt kết quả: " + eventName);
            }

            // Cập nhật thời gian log cuối cùng của sự kiện
            lastLogTime.put(eventName, currentTime);
        }
    }

    private static boolean shouldLog(String eventName, long currentTime) {
        return !lastLogTime.containsKey(eventName) || (currentTime - lastLogTime.get(eventName) > LOG_COOLDOWN_MS);
    }
}
