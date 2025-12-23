package uz.codebyz.auth.util;

public class FileUtil {

    private static final double KB = 1024.0;
    private static final double MB = KB * 1024;
    private static final double GB = MB * 1024;

    /**
     * Convert bytes to human readable size
     * Examples:
     *  - 512       -> 512 B
     *  - 2048      -> 2.00 KB
     *  - 1048576   -> 1.00 MB
     *  - 3221225472-> 3.00 GB
     */
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
