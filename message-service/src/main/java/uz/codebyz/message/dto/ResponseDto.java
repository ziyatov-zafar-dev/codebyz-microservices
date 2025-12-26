package uz.codebyz.message.dto;

public class ResponseDto<T> {
    private boolean success;
    private String message;
    private int code;
    private String errorCode;
    private T data;

    public ResponseDto() {
    }

    private ResponseDto(boolean success, String message, int code, String errorCode, T data) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.errorCode = errorCode;
        this.data = data;
    }

    public static <T> ResponseDto<T> ok(String message, T data) {
        return new ResponseDto<>(true, message, 200, null, data);
    }

    public static <T> ResponseDto<T> ok(String message) {
        return new ResponseDto<>(true, message, 200, null, null);
    }

    public static <T> ResponseDto<T> fail(int code, String message) {
        return new ResponseDto<>(false, message, code, null, null);
    }

    public static <T> ResponseDto<T> fail(int code, String errorCode, String message) {
        return new ResponseDto<>(false, message, code, errorCode, null);
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

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
