package uz.codebyz.message.dto.enums;

public enum ErrorCode {
    NOT_FOUNT_CHAT_ID("NOT_FOUNT_CHAT_ID", "Chat id topilmadi"),
    NOT_FOUND_USER("NOT_FOUND_USER", "User topilmadi"),
    ALREADY_EXIST_CHAT("ALREADY_EXIST_CHAT", "Bu chat oldindan mavjud"),
    NOT_FOUND_MESSAGE_ID("NOT_FOUND_MESSAGE_ID", "Chat id topilmadi"),
    INTERNAL_ERROR("INTERNAL_ERROR", "Kutilmagan xatolik"),
    EXTERNAL_SERVICE_ERROR(
            "EXTERNAL_SERVICE_ERROR",
            "External service client error"
    ),
    BAD_REQUEST("BAD_REQUEST", "Noto‘g‘ri so‘rov"),
    CHAT_ALREADY_EXISTS("CHAT_ALREADY_EXISTS","Bu chat allaqachon mavjud" );

    private String name;
    private String description;

    ErrorCode(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
