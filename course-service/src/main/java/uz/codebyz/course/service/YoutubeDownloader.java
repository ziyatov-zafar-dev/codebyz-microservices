package uz.codebyz.course.service;

import uz.codebyz.course.dto.YoutubeVideoDto;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class YoutubeDownloader {

    /* ================= BINARIES ================= */

    private static Path YT_DLP;
    private static Path FFMPEG;

    private static synchronized void initBinaries() throws IOException {
        if (YT_DLP == null) {
            YT_DLP = extractBinary("/bin/yt-dlp.exe", ".exe");
        }
        if (FFMPEG == null) {
            FFMPEG = extractBinary("/bin/ffmpeg.exe", ".exe");
        }
    }

    private static Path extractBinary(String resourcePath, String suffix) throws IOException {
        try (InputStream in = YoutubeDownloader.class.getResourceAsStream(resourcePath)) {
            if (in == null) throw new FileNotFoundException(resourcePath + " topilmadi");
            Path temp = Files.createTempFile("bin-", suffix);
            Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
            temp.toFile().setExecutable(true);
            return temp;
        }
    }

    /* ================= URL ================= */

    private static String normalizeUrl(String url) {
        url = url.trim();
        if (url.contains("youtu.be/")) {
            url = "https://www.youtube.com/watch?v=" +
                    url.substring(url.lastIndexOf("/") + 1);
        }
        int idx = url.indexOf("&");
        return idx > 0 ? url.substring(0, idx) : url;
    }

    /* ================= PIXELS (ENG MUHIM) ================= */

    public static List<Integer> getPixelsFast(String rawUrl) {
        long start = System.currentTimeMillis();

        // 1. Input validation
        if (rawUrl == null || rawUrl.trim().isEmpty()) {
            System.err.println("❌ URL bo'sh yoki null");
            return null;
        }

        Process process = null;
        BufferedReader reader = null;

        try {
            // 2. Binarni ishga tushirish (xatolik bilan)
            try {
                initBinaries();
            } catch (IOException e) {
                System.err.println("❌ Binary fayllar topilmadi: " + e.getMessage());
                return null;
            }

            // 3. URL ni normalizatsiya qilish
            String url;
            try {
                url = normalizeUrl(rawUrl);
                System.out.println("🌐 Normalizatsiya qilingan URL: " + url);
            } catch (IllegalArgumentException e) {
                System.err.println("❌ Noto'g'ri URL: " + e.getMessage());
                return null;
            }

            // 4. ProcessBuilder - optimized parameters
            ProcessBuilder pb = new ProcessBuilder(
                    YT_DLP.toString(),
                    "-f", "bv*[height>=144]/b[height>=144]/wv*[height>=144]", // Faqat 144p+ video formatlar
                    "--print", "%(height)s",
                    "--no-playlist",
                    "--quiet", // --no-warnings o'rniga
                    "--no-check-certificates",
                    "--user-agent", "Mozilla/5.0",
                    url
            );

            // 5. Environment setup
            Map<String, String> env = pb.environment();
            env.put("PATH", FFMPEG.getParent().toString() + ";" + env.get("PATH"));

            // 6. Redirects
            pb.redirectErrorStream(true);

            // 7. Processni ishga tushirish
            process = pb.start();

            // 8. Natijalarni o'qish
            reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            );

            Set<Integer> pixels = new LinkedHashSet<>(); // Tartibni saqlash uchun
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // 9. Barcha bo'sh va noqonuniy qiymatlarni o'tkazib yuborish
                if (line.isEmpty() ||
                        line.equals("0") ||
                        line.equals("null") ||
                        line.contains("ERROR") ||
                        line.contains("WARNING")) {
                    continue;
                }

                // 10. Raqamga tekshirish
                if (!line.matches("^\\d+$")) {
                    continue;
                }

                try {
                    int h = Integer.parseInt(line);

                    // 11. Realistik piksellar oralig'ini tekshirish
                    // YouTube: 144, 240, 360, 480, 720, 1080, 1440, 2160, 4320
                    if (h >= 144 && h <= 4320) {
                        pixels.add(h);
                    }
                } catch (NumberFormatException nfe) {
                    // Raqamga o'girib bo'lmaydigan satr
                    continue;
                }
            }

            // 12. Processni kutish (optimized timeout)
            boolean finished = process.waitFor(5, TimeUnit.SECONDS);
            if (!finished) {
                System.err.println("⚠️ Process timeout, destroy qilinyapti...");
                process.destroy();
                if (process.isAlive()) {
                    process.destroyForcibly();
                }
            }

            // 13. Exit code ni tekshirish
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                System.err.println("⚠️ yt-dlp exit code: " + exitCode);
                // Exit code 0 emas, lekin pixels topilgan bo'lishi mumkin
            }

            // 14. Natijalarni tekshirish
            if (pixels.isEmpty()) {
                System.err.println("⚠️ Hech qanday piksellar topilmadi");

                // Alternativ metod bilan urinib ko'rish
                List<Integer> fallback = getPixelsFallback(url);
                if (fallback != null && !fallback.isEmpty()) {
                    System.out.println("🔄 Fallback metod ishlatildi");
                    return fallback;
                }

                return null;
            }

            // 15. Natijalarni tartiblash (yuqoridan pastga)
            List<Integer> result = new ArrayList<>(pixels);
            result.sort((a, b) -> b - a); // Teskari tartib: 4320, 2160, 1080, ...

            // 16. Faqat standart piksellarni saqlash (agar kerak bo'lsa)
            List<Integer> filteredResult = filterStandardResolutions(result);

            // 17. Log chiqarish
            long duration = System.currentTimeMillis() - start;
            System.out.printf("✅ %.2f s | Piksellar: %s%n",
                    duration / 1000.0, filteredResult);

            return filteredResult.isEmpty() ? null : filteredResult;

        } catch (IOException e) {
            System.err.println("❌ IO xatosi: " + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            System.err.println("❌ Process interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Interrupt status ni qayta tiklash
            return null;
        } catch (Exception e) {
            System.err.println("❈ Noma'lum xatolik: " + e.getClass().getSimpleName() +
                    " - " + e.getMessage());
            return null;
        } finally {
            // 18. Resource cleanup
            closeQuietly(reader);
            destroyProcess(process);
        }
    }

    // Yordamchi metodlar
    private static List<Integer> getPixelsFallback(String url) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    YT_DLP.toString(),
                    "-F", // Barcha formatlarni ko'rish
                    "--no-playlist",
                    "--quiet",
                    url
            );

            Process process = pb.start();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            Set<Integer> pixels = new HashSet<>();
            String line;

            while ((line = br.readLine()) != null) {
                // Format: 137 mp4   1920x1080
                if (line.contains("x")) {
                    String[] parts = line.split("\\s+");
                    for (String part : parts) {
                        if (part.contains("x")) {
                            String[] res = part.split("x");
                            if (res.length == 2) {
                                try {
                                    int height = Integer.parseInt(res[1].trim());
                                    if (height >= 144 && height <= 4320) {
                                        pixels.add(height);
                                    }
                                } catch (NumberFormatException ignored) {}
                            }
                            break;
                        }
                    }
                }
            }

            process.waitFor(3, TimeUnit.SECONDS);

            if (!pixels.isEmpty()) {
                List<Integer> result = new ArrayList<>(pixels);
                result.sort((a, b) -> b - a);
                return result;
            }
        } catch (Exception e) {
            // Fallback ham ishlamadi
        }
        return null;
    }

    private static List<Integer> filterStandardResolutions(List<Integer> resolutions) {
        // Standart YouTube resolutions
        int[] standard = {4320, 2160, 1440, 1080, 720, 480, 360, 240, 144};

        List<Integer> result = new ArrayList<>();
        for (int std : standard) {
            if (resolutions.contains(std)) {
                result.add(std);
            }
        }

        // Agar standart bo'lmasa, asl ro'yxatni qaytar
        return result.isEmpty() ? resolutions : result;
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // Quietly ignore
            }
        }
    }

    private static void destroyProcess(Process process) {
        if (process != null && process.isAlive()) {
            try {
                process.destroy();
                if (process.isAlive()) {
                    process.destroyForcibly();
                }
            } catch (Exception e) {
                // Ignore
            }
        }
    }




    /* ================= DOWNLOAD ================= */

    public static YoutubeVideoDto downloadByPixel(String rawUrl, int pixel) throws Exception {

        initBinaries();
        String url = normalizeUrl(rawUrl);

        Path outDir = Paths.get("uploads");
        Files.createDirectories(outDir);

        ProcessBuilder pb = new ProcessBuilder(
                YT_DLP.toString(),
                "-f", "bestvideo[height<=" + pixel + "]+bestaudio/best",
                "--merge-output-format", "mp4",
                "--ffmpeg-location", FFMPEG.toString(),
                "-o", outDir.resolve("%(title).200s [%(id)s].%(ext)s").toString(),
                "--no-playlist",
                url
        );

        pb.inheritIO();
        pb.start().waitFor(1, TimeUnit.HOURS);

        Path file;
        try (var s = Files.list(outDir)) {
            file = s.max(Comparator.comparingLong(f -> f.toFile().lastModified()))
                    .orElseThrow();
        }

        long size = Files.size(file);

        YoutubeVideoDto dto = new YoutubeVideoDto();
        dto.setFileName(file.getFileName().toString());
        dto.setUrl(url);
        dto.setVideoSize(size);
        dto.setVideoSizeMb(formatSize(size));
        dto.setDownloadPath(file.toAbsolutePath().toString());

        return dto;
    }

    /* ================= HELPERS ================= */

    public static String safeFilename(String filename) {
        String name = filename.trim();
        name = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        name = name.replaceAll("[^a-zA-Z0-9._-]", "_");
        name = name.replaceAll("_+", "_");
        return name.replaceAll("^_+|_+$", "");
    }

    private static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        double kb = bytes / 1024d;
        if (kb < 1024) return String.format("%.2f KB", kb);
        double mb = kb / 1024d;
        if (mb < 1024) return String.format("%.2f MB", mb);
        return String.format("%.2f GB", mb / 1024d);
    }

    /* ================= MAIN (TEST) ================= */

    public static void main(String[] args) throws Exception {
        String url = "https://youtu.be/HjhPVapBGQw?list=RDHjhPVapBGQw";
        List<Integer> pixels = getPixelsFast(url);
        System.out.println("📊 Mavjud piksellar: " + pixels);
        assert pixels != null;
//        downloadByPixel(url, pixels.get());
    }
}
