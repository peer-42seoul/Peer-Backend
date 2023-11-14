package peer.backend.service.team;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import peer.backend.dto.team.TeamAddressBook;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.user.User;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.board.recruit.RecruitApplicantRepository;
import peer.backend.repository.board.recruit.RecruitRepository;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.file.FileService;

import javax.transaction.Transactional;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TeamToolsBoxService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final RecruitRepository recruitRepository;
    private final RecruitApplicantRepository recruitApplicantRepository;
    private final FileService fileService;
    @Value("${custom.filePath}")
    private String filePath;

    @Transactional
    public ArrayList<TeamAddressBook> getAddressBook(Long teamId, int size, User user) {
        ArrayList<TeamAddressBook> teamAddressBooks = new ArrayList<>();
        for (TeamUser teamUser : this.teamUserRepository.findByTeamId(teamId)) {
            teamAddressBooks.add(new TeamAddressBook(this.userRepository.findById(teamUser.getUserId()).orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."))));
        }
        return teamAddressBooks;
    }
}
