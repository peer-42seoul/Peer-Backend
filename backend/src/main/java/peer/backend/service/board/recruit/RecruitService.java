package peer.backend.service.board.recruit;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import peer.backend.dto.Board.Recruit.RecruitUpdateRequestDTO;
import peer.backend.dto.board.recruit.RecruitRequestDTO;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.*;
import peer.backend.entity.user.User;
import peer.backend.exception.IllegalArgumentException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.board.recruit.RecruitRepository;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.team.TeamService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruitService {
    private final UserRepository userRepository;
    private final RecruitRepository recruitRepository;
    private final TeamService teamService;
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;

    @Transactional
    public void createRecruit(RecruitRequestDTO recruitRequestDTO){
        //유저 검사
        User user = userRepository.findById(recruitRequestDTO.getUserId()).orElseThrow(
                () -> new NotFoundException("사용자를 찾을 수 없습니다.")
        );
        //동일한 팀 이름 검사
        Optional<Team> findTeam = teamRepository.findByName(recruitRequestDTO.getName());
        if (findTeam.isPresent())
            throw new IllegalArgumentException("이미 존재하는 팀 이름입니다.");

        //팀 생성
        Team team = Team.builder()
                .name(recruitRequestDTO.getName())
                .type(TeamType.from(recruitRequestDTO.getType()))
                .dueTo(recruitRequestDTO.getDue())
                .operationFormat(TeamOperationFormat.from(recruitRequestDTO.getPlace()))
                .status(TeamStatus.RECRUITING)
                .teamMemberStatus(TeamMemberStatus.RECRUITING)
                .isLock(false)
                .region1(recruitRequestDTO.getRegion())
                .region2(recruitRequestDTO.getRegion())
                .region3(recruitRequestDTO.getRegion())
                .build();
        teamRepository.save(team);

        //팀에 리더 추가
        TeamUser teamUser = TeamUser.builder()
                .teamId(team.getId())
                .userId(user.getId())
                .role(TeamUserRoleType.LEADER)
                .build();
        teamUserRepository.save(teamUser);

        //모집게시글 생성
        Recruit recruit = Recruit.builder()
                .team(team)
                .type(TeamType.from(recruitRequestDTO.getType()))
                .title(recruitRequestDTO.getTitle())
                .due(recruitRequestDTO.getDue())
                .link(recruitRequestDTO.getLink())
                .content(recruitRequestDTO.getContent())
                .place(TeamOperationFormat.from(recruitRequestDTO.getPlace()))
                .region(recruitRequestDTO.getRegion())
                .roles(recruitRequestDTO.getRoleList())
                .interviews(recruitRequestDTO.getInterviewList())
                .status(RecruitStatus.ONGOING)
                .thumbnailUrl(null)
                .build();
        recruitRepository.save(recruit);
    }

    public void updateRecruit(Long recruit_id, RecruitUpdateRequestDTO recruitUpdateRequestDTO){
        Recruit recruit = recruitRepository.findById(recruit_id).orElseThrow(() -> new NotFoundException("존재하지 않는 모집게시글입니다."));
        Team team = recruit.getTeam();

        recruit.update(recruitUpdateRequestDTO);
        recruitRepository.save(recruit);
    }


}
