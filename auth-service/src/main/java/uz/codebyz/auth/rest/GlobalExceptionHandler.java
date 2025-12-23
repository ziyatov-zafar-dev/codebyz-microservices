package uz.codebyz.auth.rest;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.*;
//import uz.codebyz.auth.common.ErrorCode;
//import uz.codebyz.auth.common.ResponseDto;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseDto<Void> validation(MethodArgumentNotValidException ex) {
//        String msg = ex.getBindingResult().getFieldErrors().stream()
//                .findFirst()
//                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
//                .orElse("Validation error");
//        return ResponseDto.fail(400, ErrorCode.VALIDATION_ERROR, msg);
//    }
//
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ResponseDto<Void> any(Exception ex) {
//        return ResponseDto.fail(500, ErrorCode.INTERNAL_ERROR, ex.getMessage());
//    }
//}


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uz.codebyz.auth.common.ErrorCode;
import uz.codebyz.auth.common.ResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ================= AUTH / SECURITY =================

    /**
     * JWT yo‘q, noto‘g‘ri yoki JwtUser null
     * 401
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseDto<Void> handleAuthenticationException(AuthenticationException ex) {
        return ResponseDto.fail(
                401,
                ErrorCode.UNAUTHORIZED,
                ex.getMessage()
        );
    }

    /**
     * Role mos kelmadi (ROLE_TEACHER vs TEACHER)
     * 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseDto<Void> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseDto.fail(
                403,
                ErrorCode.FORBIDDEN,
                ex.getMessage()
        );
    }

    /**
     * JWT eskirgan
     * 401
     */
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseDto<Void> handleExpiredJwtException(ExpiredJwtException ex) {
        return ResponseDto.fail(
                401,
                ErrorCode.TOKEN_EXPIRED,
                ex.getMessage()
        );
    }

    /**
     * JWT umumiy xatolik
     * 401
     */
    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseDto<Void> handleJwtException(JwtException ex) {
        return ResponseDto.fail(
                401,
                ErrorCode.INVALID_TOKEN,
                ex.getMessage()
        );
    }

    // ================= BUSINESS / RUNTIME =================

    /**
     * IllegalState, IllegalArgument va hokazo
     * 400
     */
    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto<Void> handleBadRequest(RuntimeException ex) {
        return ResponseDto.fail(
                400,
                ErrorCode.BAD_REQUEST,
                ex.getMessage()
        );
    }

    /**
     * Boshqa kutilmagan xatolar
     * 500
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseDto<Void> handleAll(Exception ex) {
        System.err.println(ex.getMessage());
        return ResponseDto.fail(
                500,
                ErrorCode.INTERNAL_ERROR,
                ex.getMessage()
        );
    }
}
