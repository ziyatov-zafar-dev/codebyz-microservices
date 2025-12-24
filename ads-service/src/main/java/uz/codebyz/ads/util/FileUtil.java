package uz.codebyz.ads.util;

public class FileUtil {

    private static final double KB = 1024.0;
    private static final double MB = KB * 1024;
    private static final double GB = MB * 1024;

    public static String toMB(long bytes) {
        if (bytes < KB) {
            return bytes + " B";
        }
        if (bytes < MB) {
            return String.format("%.2f KB", bytes / KB);
        }
        if (bytes < GB) {
            return String.format("%.2f MB", bytes / MB);
        }
        return String.format("%.2f GB", bytes / GB);
    }
}
