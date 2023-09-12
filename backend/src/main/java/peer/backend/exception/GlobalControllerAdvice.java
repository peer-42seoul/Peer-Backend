package peer.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity illegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    @ExceptionHandler(value = MultipartException.class)
    public ResponseEntity multipartException(MultipartException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity methodArgumentNotValidException(HttpServletRequest req,
        MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = new ErrorResponse(req, HttpStatus.BAD_REQUEST, e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(value = ForbiddenException.class)
    public ResponseEntity forbiddenException(HttpServletRequest req, ForbiddenException e) {
        ErrorResponse errorResponse = new ErrorResponse(req, HttpStatus.FORBIDDEN, e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity notFoundException(HttpServletRequest req, NotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(req, HttpStatus.NOT_FOUND, e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(value = ConflictException.class)
    public ResponseEntity conflictException(HttpServletRequest req, ConflictException e) {
        ErrorResponse errorResponse = new ErrorResponse(req, HttpStatus.FORBIDDEN, e);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity unauthorizedException(HttpServletRequest req, UnauthorizedException e) {
        ErrorResponse errorResponse = new ErrorResponse(req, HttpStatus.UNAUTHORIZED, e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity badRequestException(HttpServletRequest req, BadRequestException e) {
        ErrorResponse errorResponse = new ErrorResponse(req, HttpStatus.BAD_REQUEST, e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity exception(Exception e) {
        //		e.printStackTrace(); 디버깅용 코드
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
