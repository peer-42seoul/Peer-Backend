package peer.backend.service.board.team;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.entity.board.team.Board;
import peer.backend.entity.board.team.enums.BoardType;
import peer.backend.entity.team.Team;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.board.team.BoardRepository;
import peer.backend.repository.team.TeamRepository;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final TeamRepository teamRepository;
    private final BoardRepository boardRepository;
    @Transactional
    public void createNoticeBoard(Long teamId) {

        Team team = teamRepository.findById(teamId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 팀입니다."));
        Board board = Board.builder()
                .team(team)
                .name("공지사항")
                .type(BoardType.NOTICE)
                .build();
        boardRepository.save(board);
    }

}
