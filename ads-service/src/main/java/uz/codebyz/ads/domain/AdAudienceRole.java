package uz.codebyz.ads.domain;

public enum AdAudienceRole {
    ADMIN,
    TEACHER,
    STUDENT,
    ALL;

    public static boolean matches(AdAudienceRole allowed, String userRole) {
        if (allowed == ALL) return true;
        if (userRole == null) return allowed == STUDENT || allowed == ALL;
        return allowed.name().equalsIgnoreCase(userRole);
    }
}
