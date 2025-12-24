package uz.codebyz.auth.util;

import uz.codebyz.auth.device.enums.DeviceType;

public final class UserAgentUtil {

    private UserAgentUtil() {
    }

    /* ===================== BROWSER NAME ===================== */

    public static String getBrowserName(String ua) {
        if (ua == null || ua.isBlank()) return "Unknown";

        ua = ua.toLowerCase();

        if (ua.contains("edg")) return "Edge";
        if (ua.contains("opr") || ua.contains("opera")) return "Opera";
        if (ua.contains("chrome")) return "Chrome";
        if (ua.contains("firefox")) return "Firefox";
        if (ua.contains("safari")) return "Safari";

        return "Unknown";
    }

    /* ===================== BROWSER VERSION ===================== */

    public static String getBrowserVersion(String ua) {
        if (ua == null || ua.isBlank()) return null;

        try {
            if (ua.contains("edg/"))
                return extractVersion(ua, "edg/");
            if (ua.contains("opr/"))
                return extractVersion(ua, "opr/");
            if (ua.contains("opera"))
                return extractVersion(ua, "version/");
            if (ua.contains("chrome/"))
                return extractVersion(ua, "chrome/");
            if (ua.contains("firefox/"))
                return extractVersion(ua, "firefox/");
            if (ua.contains("version/"))
                return extractVersion(ua, "version/");

        } catch (Exception ignored) {
        }

        return null;
    }

    /* ===================== DEVICE TYPE ===================== */

    public static DeviceType getDeviceType(String ua) {
        if (ua == null || ua.isBlank()) return DeviceType.UNKNOWN;
        ua = ua.toLowerCase();
        if (ua.contains("mobile")) {
            return DeviceType.MOBILE;
        } else if (ua.contains("android"))
            return DeviceType.ANDROID;
        else if (ua.contains("iphone"))
            return DeviceType.IPHONE;
        else if (ua.contains("ipad") || ua.contains("tablet"))
            return DeviceType.TABLET;
        else return DeviceType.DESKTOP;
    }

    /* ===================== INTERNAL ===================== */

    private static String extractVersion(String ua, String key) {
        int index = ua.indexOf(key);
        if (index == -1) return null;

        String version = ua.substring(index + key.length());
        int end = version.indexOf(" ");

        return end > 0 ? version.substring(0, end) : version;
    }
}
