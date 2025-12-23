package uz.codebyz.message.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import uz.codebyz.message.dto.response.ResponseDto;
import uz.codebyz.message.dto.enums.ErrorCode;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 🔴 Auth-service yoki boshqa microservice 4xx qaytarsa
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ResponseDto<Void>> handleClientError(HttpClientErrorException ex) {

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ResponseDto.fail(
                        ex.getStatusCode().value(),
                        ErrorCode.EXTERNAL_SERVICE_ERROR,
                        "External service client error: " + ex.getMessage()
                ));
    }

    /**
     * 🔴 Auth-service yoki boshqa microservice 5xx qaytarsa
     */
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ResponseDto<Void>> handleServerError(HttpServerErrorException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(ResponseDto.fail(
                        502,
                        ErrorCode.EXTERNAL_SERVICE_ERROR,
                        "External service server error"
                ));
    }

    /**
     * 🔴 IllegalState, IllegalArgument
     */
    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<ResponseDto<Void>> handleIllegal(RuntimeException ex) {

        return ResponseEntity
                .badRequest()
                .body(ResponseDto.fail(
                        400,
                        ErrorCode.BAD_REQUEST,
                        ex.getMessage()
                ));
    }

    /**
     * 🔴 Default (fallback)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handleAll(Exception ex) {

        ex.printStackTrace(); // log uchun

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.fail(
                        500,
                        uz.codebyz.message.dto.enums.ErrorCode.INTERNAL_ERROR,
                        "Unexpected server error"
                ));
    }
}
