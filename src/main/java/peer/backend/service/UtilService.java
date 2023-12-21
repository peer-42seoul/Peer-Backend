package peer.backend.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilService {

    public boolean checkDatePastNow(LocalDateTime date) {
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");
        return date.isAfter(LocalDateTime.now(seoulZone));
    }
}
