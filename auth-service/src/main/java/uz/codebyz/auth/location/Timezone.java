package uz.codebyz.auth.location;

import com.nimbusds.jose.shaded.gson.annotations.SerializedName;

public class Timezone {
    private String id;
    @SerializedName("current_time")
    private String currentTime;
    private String utc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getUtc() {
        return utc;
    }

    public void setUtc(String utc) {
        this.utc = utc;
    }
}
