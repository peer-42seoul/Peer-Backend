package peer.backend.aspect;

import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import peer.backend.exception.BadRequestException;

@Aspect
@Component
public class ValidUserIdAspect {

    @Pointcut("@annotation(peer.backend.annotation.ValidUserId)")
    public void validUserId() {
    }

    @Before("validUserId()")
    public void validUserId(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        for (int i = 0; i < method.getParameters().length; i++) {
            String parameterName = method.getParameters()[i].getName();
            if (parameterName.equals("userId")) {
                Long userId = (Long) args[i];
                if (userId < 0) {
                    throw new BadRequestException("임시 혹은 탈퇴한 유저입니다!");
                }
            }
        }
    }
}