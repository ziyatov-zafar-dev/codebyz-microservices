package uz.codebyz.course.dto;


public class YoutubeVideoDto {

    private String fileName;
    private String url;
    private String videoSizeMb;
    private Long videoSize;
    private String downloadPath;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVideoSizeMb() {
        return videoSizeMb;
    }

    public void setVideoSizeMb(String videoSizeMb) {
        this.videoSizeMb = videoSizeMb;
    }

    public Long getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(Long videoSize) {
        this.videoSize = videoSize;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }
}
