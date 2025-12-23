package uz.codebyz.auth.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import uz.codebyz.auth.common.ErrorCode;
import uz.codebyz.auth.common.ResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto<Void> validation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .orElse("Validation error");
        return ResponseDto.fail(400, ErrorCode.VALIDATION_ERROR, msg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseDto<Void> any(Exception ex) {
        return ResponseDto.fail(500, ErrorCode.INTERNAL_ERROR, ex.getMessage());
    }
}
