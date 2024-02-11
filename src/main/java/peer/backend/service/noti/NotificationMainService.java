package peer.backend.service.noti;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import peer.backend.entity.user.User;
import peer.backend.repository.noti.NotificationRepository;
import peer.backend.repository.noti.NotificationSubscriptionKeysRepsitory;
import peer.backend.repository.noti.NotificationTargetRepository;
import peer.backend.repository.user.UserRepository;

import javax.transaction.Transactional;
import java.io.IOException;

// 포어 그라운드에서 동작하는 내용들을 담아야 하는 서비스
// 사실상 외부에서 들어오는 내용에 대해 구동 가능하게 하는 서비스
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationMainService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationTargetRepository notificationTargetRepository;
    private final NotificationSubscriptionKeysRepsitory notificationSubscriptionKeysRepsitory;
    private final NotificationCreationService notificationCreationService;


    @Transactional
    public void setAlarmForUser(User user,String type, boolean value) throws IOException {
        if (type.equals("keyword")) {
            user.setKeywordRecommendAlarm(value);
        }
        else if (type.equals("team")) {
            user.setTeamAlarm(value);
        }
        else if (type.equals("message")) {
            user.setMessageAlarm(value);
        }
        else {
            user.setNightAlarm(value);
        }
        userRepository.save(user);
    }



}
