package peer.backend.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import peer.backend.exception.ErrorResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            //logout
            log.error(e.getMessage());
            setErrorResponse(HttpStatus.UNAUTHORIZED, request, response, e);
        } catch (Exception e) {
            log.error(e.getMessage());
            setErrorResponse(HttpStatus.UNAUTHORIZED, request, response, e);
        }
    }

    public void setErrorResponse(HttpStatus status, HttpServletRequest request,
        HttpServletResponse response, Exception e) throws IOException {
        ObjectMapper om = new ObjectMapper();
        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");
        ErrorResponse errorResponse = new ErrorResponse(request, status, e);
        response.getWriter().write(om.writeValueAsString(errorResponse));
    }
}
