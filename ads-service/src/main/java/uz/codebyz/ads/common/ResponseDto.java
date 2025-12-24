package uz.codebyz.ads.common;

public class ResponseDto<T> {
    private boolean success;
    private String message;
    private int code;
    private ErrorCode errorCode;
    private T data;

    public ResponseDto() {
    }

    public ResponseDto(boolean success, String message, int code, ErrorCode errorCode, T data) {
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

    public static <T> ResponseDto<T> fail(int code, ErrorCode errorCode, String message) {
        return new ResponseDto<>(false, message, code, errorCode, null);
    }

    public static <T> ResponseDto<T> fail(int code, ErrorCode errorCode, String message, T data) {
        return new ResponseDto<>(false, message, code, errorCode, data);
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
