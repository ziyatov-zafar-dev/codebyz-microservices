package uz.codebyz.auth.device;

public class DeviceNameUtil {

    public static String parse(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "Unknown device";
        }
        userAgent = userAgent.toLowerCase();

        if (userAgent.contains("windows")) return "Windows PC";
        if (userAgent.contains("mac os")) return "MacBook";
        if (userAgent.contains("android")) return "Android";
        if (userAgent.contains("iphone")) return "iPhone";
        if (userAgent.contains("ipad")) return "iPad";
        if (userAgent.contains("linux")) return "Linux device";

        return "Unknown device";
    }
}
