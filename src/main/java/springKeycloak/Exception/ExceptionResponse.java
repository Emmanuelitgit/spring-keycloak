package springKeycloak.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import springKeycloak.dto.ResponseDTO;
import springKeycloak.utils.AppUtils;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionResponse{

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException notFoundException){
        Map<String,String> response = new HashMap<>();
        response.put("message", notFoundException.getMessage());
        response.put("status", "404");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ResponseDTO> handleAuthorizationException(){
        ResponseDTO response = AppUtils.getResponseDto("Access Denied", HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<Object> handleInvalidDataException(InvalidDataException invalidDataException){
        Map<String,String> response = new HashMap<>();
        response.put("message", invalidDataException.getMessage());
        response.put("status", "400");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
