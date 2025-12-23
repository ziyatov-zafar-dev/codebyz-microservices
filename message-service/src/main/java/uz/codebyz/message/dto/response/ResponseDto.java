package uz.codebyz.message.dto.response;


import uz.codebyz.message.dto.enums.ErrorCode;

public class ResponseDto<T> {
    private boolean success;
    private String message;
    private int code;
    private Error error;
    private T data;

    public ResponseDto() {
    }

    public ResponseDto(boolean success, String message, int code, Error error, T data) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.error = error;
        this.data = data;
    }

    public static <T> ResponseDto<T> ok(String message, T data) {
        return new ResponseDto<T>(true, message, 200, null, data);
    }

    public static <T> ResponseDto<T> ok(String message) {
        return new ResponseDto<T>(true, message, 200, null, null);
    }

    public static <T> ResponseDto<T> fail(int code, ErrorCode errorCode, String message) {
        return new ResponseDto<T>(false, message, code, new Error(errorCode), null);
    }

    public static <T> ResponseDto<T> fail(int code, ErrorCode errorCode, String message, T data) {
        return new ResponseDto<T>(false, message, code, new Error(errorCode), data);
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

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
