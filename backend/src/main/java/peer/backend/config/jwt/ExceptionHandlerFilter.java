package peer.backend.config.jwt;

import io.jsonwebtoken.JwtException;
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
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            //logout
            setErrorResponse(HttpStatus.UNAUTHORIZED, request, response, e);
        }catch (Exception e) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, request, response, e);
        }
    }

    public void setErrorResponse(HttpStatus status, HttpServletRequest request,
                                 HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().println(new ErrorResponse(request, status, e).convertToJson());
    }
}
