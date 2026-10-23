/*
 * Decompiled with CFR 0.152.
 */
package me.ifamasssxd.cosmicevolutionkits.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static String formatDifference(long time) {
        String diff;
        if (time == 0L) {
            return "Never";
        }
        long day = TimeUnit.SECONDS.toDays(time);
        long hours = TimeUnit.SECONDS.toHours(time) - day * 24L;
        long minutes = TimeUnit.SECONDS.toMinutes(time) - TimeUnit.SECONDS.toHours(time) * 60L;
        long seconds = TimeUnit.SECONDS.toSeconds(time) - TimeUnit.SECONDS.toMinutes(time) * 60L;
        StringBuilder sb = new StringBuilder();
        if (day > 0L) {
            sb.append(day).append(day == 1L ? "d" : "d").append(" ");
        }
        if (hours > 0L) {
            sb.append(hours).append(hours == 1L ? "h" : "h").append(" ");
        }
        if (minutes > 0L) {
            sb.append(minutes).append(minutes == 1L ? "m" : "m").append(" ");
        }
        if (seconds > 0L) {
            sb.append(seconds).append(seconds == 1L ? "s" : "s");
        }
        return (diff = sb.toString()).isEmpty() ? "Now" : diff.trim();
    }

    public static String formatTime(long time) {
        if (time == System.currentTimeMillis()) {
            return "Now";
        }
        if (time == -1L) {
            return "Never";
        }
        return TimeUtils.formatDifference(time - System.currentTimeMillis() / 1000L);
    }
}

