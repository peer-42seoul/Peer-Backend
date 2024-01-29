package peer.backend.service.dnd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import peer.backend.dto.dnd.TeamMember;
import peer.backend.dto.dndSub.CalendarEventDTO;
import peer.backend.dto.dndSub.DeleteTargetDTO;
import peer.backend.dto.dndSub.MemberDTO;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.user.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@EnableWebMvc
@Slf4j
public class DnDSubService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    private Long eventCnt = 0L;

    private final RedisTemplate<String, List<TeamMember>> redisTemplate;

    private final HashSet<CalendarEventDTO> tempMemoryEventList;

    public void saveTeamDataInRedis(String key, String identifier, Team target) {
        List<TeamMember> members = new ArrayList<>();
        for (TeamUser teamUser : target.getTeamUsers()) {
            TeamMember member = TeamMember.builder()
                    .teamId(teamUser.getTeamId())
                    .userId(teamUser.getUserId())
                    .build();
            members.add(member);
        }
        if (members.isEmpty())
            return ;
        String newKey = key + "-" + identifier;
        redisTemplate.opsForValue().set(newKey, members);
    }

    public void saveTeamMemberInRedis(String key, String identifier, List<TeamMember> target) {
        String newKey = key + "-" + identifier;
        redisTemplate.opsForValue().set(newKey, target);
    }

    public List<TeamMember> getTeamDataInRedis(String key, String identifier) {
        return redisTemplate.opsForValue().get(key + "-" + identifier);
    }

    public Team getTeamByTeamId(Long teamId){
        return this.teamRepository.findById(teamId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 팀입니다."));
    }

    public boolean validCheckForTeam(Team target) {
        System.out.println("error 1");
        return target.getStatus().equals(TeamStatus.COMPLETE) || target.getStatus().equals(TeamStatus.DISPERSE);
    }

    public boolean validCheckUserWithTeam(Team target, User requester) {
        System.out.println("error 2");
        return target.getTeamUsers()
                .stream()
                .anyMatch(member -> member.getUserId().equals(requester.getId()));
    }

    public Long makeTemporaryEventId() {
        Long id;
        id = this.eventCnt++;

        return id;
    }

    public List<MemberDTO> getMemberList(User requesterUser, Team targetTeam) {

        HashSet<MemberDTO> ret = new HashSet<>();
        for (TeamUser target : targetTeam.getTeamUsers()) {
            User targetUser = target.getUser();
            MemberDTO member = MemberDTO.builder()
                    .nickname(targetUser.getNickname())
                    .userId(targetUser.getId())
                    .build();
            ret.add(member);
        }
        MemberDTO requester = MemberDTO.builder()
                .nickname(requesterUser.getNickname())
                .userId(requesterUser.getId())
                .build();
        if (!ret.contains(requester))
            throw new BadRequestException("요청자가 팀 멤버가 아닙니다.");
        return new ArrayList<>(ret);
    }

    public Long setEventToAlarm(CalendarEventDTO data) {
        Long id = this.makeTemporaryEventId();

        if(data.getTeamId() == -1L){
            throw new BadRequestException("비정상적인 요청입니다.");
        }

        CalendarEventDTO saveEvent = CalendarEventDTO.builder()
                .eventId(id)
                .teamId(data.getTeamId())
                .end(data.getEnd())
                .start(data.getStart())
                .member(data.getMember())
                .title(data.getTitle())
                .build();

        this.tempMemoryEventList.add(saveEvent);

        return id;
    }

    public Long updateEventToAlarm(CalendarEventDTO data){
        if (this.tempMemoryEventList.stream().noneMatch(event -> event.getEventId().equals(data.getEventId()))) {
            return -1L;
        }
        this.tempMemoryEventList.remove(data);
        this.tempMemoryEventList.add(data);
        return data.getEventId();
    }

    public void deleteEventFromAlarm(DeleteTargetDTO target) {
        CalendarEventDTO filtered = this.tempMemoryEventList.stream()
                .filter(event -> event.getEventId().equals(target.getEventId()))
                .findFirst()
                .orElse(null);

        if (filtered == null)
            throw new NoSuchElementException("이벤트가 존재하지 않습니다.");

        this.tempMemoryEventList.remove(filtered);
        return;
    }

    public List<CalendarEventDTO> getAllEvents() {
        return new ArrayList<>(this.tempMemoryEventList);
    }
}
