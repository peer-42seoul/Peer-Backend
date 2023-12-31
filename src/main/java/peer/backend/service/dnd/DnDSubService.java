package peer.backend.service.dnd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import peer.backend.dto.dndSub.CalendarEventDTO;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@EnableWebMvc
@Slf4j
public class DnDSubService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    private final List<CalendarEventDTO> tempMemeoryEventList;

}
