package uz.codebyz.course.dto;

// Umumiy javob DTO (success, message, kod va errorCode bilan)
public class ApiResponse<T> {
    private boolean success; // Amal muvaffaqiyatli bo'ldimi
    private String message; // Foydalanuvchiga ko'rsatiladigan xabar
    private int code; // HTTP kodga mos raqam (200, 201, 404 va hokazo)
    private ErrorCode errorCode; // Tizim ichki error kodi
    private T data; // Asosiy javob ma'lumotlari

    public ApiResponse() {
    }

    public ApiResponse(boolean success, String message, int code, ErrorCode errorCode, T data) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.errorCode = errorCode;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, 200, ErrorCode.OK, data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(true, message, 201, ErrorCode.OK, data);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(false, message, 404, ErrorCode.NOT_FOUND, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
