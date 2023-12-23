package peer.backend.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import peer.backend.exception.ConflictException;
import peer.backend.exception.ErrorResponse;
import peer.backend.exception.ForbiddenException;
import peer.backend.exception.NotFoundException;
import peer.backend.exception.UnauthorizedException;

@Component
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            if (e instanceof UnauthorizedException) {
                setErrorResponse(HttpStatus.UNAUTHORIZED, request, response, e);
            } else if (e instanceof NotFoundException) {
                setErrorResponse(HttpStatus.NOT_FOUND, request, response, e);
            } else if (e instanceof ConflictException) {
                setErrorResponse(HttpStatus.CONFLICT, request, response, e);
            } else if (e instanceof ForbiddenException) {
                setErrorResponse(HttpStatus.FORBIDDEN, request, response, e);
            }
        }
    }

    public void setErrorResponse(HttpStatus status, HttpServletRequest request,
        HttpServletResponse response, Exception e) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");
        ErrorResponse errorResponse = new ErrorResponse(request, status, e);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
