package net.ss.dungeonwaves.util;

import net.ss.dungeonwaves.DungeonWavesMod;

public class Log {

    private static String getCallerInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 3) { // Vị trí thứ 3-4 là nơi gọi log
            StackTraceElement caller = stackTrace[3];
            String className = caller.getFileName(); // Lấy tên file (tên class + .java)
            if (className != null && className.endsWith(".java")) {
                className = className.substring(0, className.length() - 5); // Bỏ .java
            }
            String methodName = caller.getMethodName(); // Lấy tên phương thức
            return className + "." + methodName; // Format: TênTệp.TênPhươngThức
        }
        return "Unknown.UnknownMethod";
    }

    public static void l(String message) {
        DungeonWavesMod.LOGGER.info("[{}]: {}", getCallerInfo(), message);
    }

    public static void d(String message) {
        DungeonWavesMod.LOGGER.debug("[{}]: {}", getCallerInfo(), message);
    }

    public static void w(String message) {
        DungeonWavesMod.LOGGER.warn("[{}]: {}", getCallerInfo(), message);
    }

    public static void e(String message) {
        DungeonWavesMod.LOGGER.error("[{}]: {}", getCallerInfo(), message);
    }
}
