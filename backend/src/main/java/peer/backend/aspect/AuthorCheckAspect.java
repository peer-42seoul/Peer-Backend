package peer.backend.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.user.User;
import peer.backend.exception.ForbiddenException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.board.recruit.RecruitRepository;
import peer.backend.repository.user.UserRepository;

@Aspect
@Component
public class AuthorCheckAspect {
    private final RecruitRepository recruitRepository;

    @Autowired
    public AuthorCheckAspect(RecruitRepository recruitRepository){
        this.recruitRepository = recruitRepository;
    }
    @Before("@annotation(peer.backend.annotation.AuthorCheck)")
    public void checkAuthorization(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof Authentication) {
                Authentication authentication = (Authentication) arg;
                Long recruit_id = extractRecruitId(args); // recruit_id 추출

                //recruit_id와 authentication 사용하여 사용자가 게시글 작성자인지 확인함.
                Recruit recruit = recruitRepository.findById(recruit_id).orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));

                String authorUsername = recruit.getWriter().getNickname();
                String currentUsername = User.authenticationToUser(authentication).getNickname();

                if (!authorUsername.equals(currentUsername)) {
                    throw new ForbiddenException("게시글 작성자만 수정할 수 있습니다.");
                }
            }
        }
    }

    private Long extractRecruitId(Object[] args) {
        // args 배열에서 recruit_id를 추출하는 로직을 구현하세요.
        // 예를 들어, 파라미터의 타입을 확인하여 recruit_id를 추출할 수 있습니다.
        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
        }
        throw new IllegalArgumentException("post_id 파라미터를 찾을 수 없습니다.");
    }
}




//    @Before("@annotation(peer.backend.annotation.AuthorCheck) && args(authentication, recruit_id) ")
//    private void checkAuthor(Authentication authentication, Long recruit_id){
//        System.out.println("gheihaiehfiaheofihaoeifh");
//        Object[] args = joinPoint.getArgs();
//        System.out.println(args[0]);
//
//        if (args.length < 2 || !(args[0] instanceof Authentication) || !(args[1] instanceof Long)) {
//            throw new IllegalArgumentException("메서드 파라미터가 부적절합니다.");
//        }
//        System.out.println("gheihaiehfiaheofihaoeifh");
//        Authentication authentication = (Authentication) args[0];
//        Long recruit_id = (Long) args[1];
//        Recruit recruit = recruitRepository.findById(recruit_id).orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
//        System.out.println(User.authenticationToUser(authentication).getNickname() + " " + recruit.getWriter().getNickname());
//        if (!User.authenticationToUser(authentication).getNickname().equals(recruit.getWriter().getNickname())){
//            System.out.println("gheihaiehfiaheofihaoeifh");
//            throw new ForbiddenException("작성자가 아닙니다.");
//        }
//    }
//}
