package peer.backend.service.noti;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

//SubService 를 활용하여, 어드민쪽에서 알람을 생성 가능하게 만들어야 하는 서비스
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationAdminService {
    private final NotificationCreationService notificationCreationService;
}
