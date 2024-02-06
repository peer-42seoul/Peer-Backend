package peer.backend.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Objects;
import javax.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilService {

    public boolean isBeforeThanNow(LocalDateTime date) {
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");
        return date.isBefore(LocalDateTime.now(seoulZone));
    }

    public Cookie getCookieByName(Cookie[] cookies, String name) {
        if (Objects.nonNull(cookies)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static <T> Iterable<T> emptyIfNull(Iterable<T> iterable) {
        return iterable == null ? Collections.emptyList() : iterable;
    }
}
