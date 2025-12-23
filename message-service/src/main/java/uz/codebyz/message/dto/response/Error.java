package uz.codebyz.message.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uz.codebyz.message.dto.enums.ErrorCode;

public class Error {
    private final String name;
    private final String description;
    @JsonIgnore
    private ErrorCode errorCode;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Error(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.name = errorCode.getName();
        this.description = errorCode.getDescription();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
