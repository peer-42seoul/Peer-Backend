package peer.backend.service.noti;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import peer.backend.dto.noti.enums.NotificationPriority;
import peer.backend.dto.noti.enums.NotificationTargetType;
import peer.backend.dto.noti.enums.NotificationType;
import peer.backend.entity.message.MessagePiece;
import peer.backend.entity.noti.NotificationTarget;
import peer.backend.repository.noti.NotificationRepository;
import peer.backend.repository.noti.NotificationSubscriptionKeysRepsitory;
import peer.backend.repository.noti.NotificationTargetRepository;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.repository.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

// 다른 서비스에서 사용해야 하는 서비스, 메시지를 생성, 삭제 등의 역할을 함
// 그 외에 추가적인 일정 관리나 삭제 여부, 작업 여부 등을 위한 용도
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSubService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationTargetRepository notificationTargetRepository;
    private final NotificationSubscriptionKeysRepsitory notificationSubscriptionKeysRepsitory;

    @PersistenceContext
    private final EntityManager entityManager;

    //TODO : 어떻게 할 건지 정하자
    private static final String SYSTEM_DEFAULT = "logo_url";

    private List<?> executeNativeSQLQueryForUser(String sql, Map<String, Long> mapping) {
        Query query = entityManager.createNativeQuery(sql, MessagePiece.class);
        mapping.forEach(query::setParameter);
        return query.getResultList();
    }

    public void makeNotiofication(String imageUrl,
                                  String title,
                                  String body,
                                  String link,
                                  NotificationPriority priority,
                                  NotificationType type,
                                  NotificationTargetType targets,
                                  List<Long> ids) {

    }
}
