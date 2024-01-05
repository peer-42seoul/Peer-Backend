package peer.backend.service.board.team;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.annotation.tracking.PostCreateTracking;
import peer.backend.dto.board.team.BoardCreateRequest;
import peer.backend.dto.board.team.BoardUpdateRequest;
import peer.backend.dto.board.team.PostCreateRequest;
import peer.backend.dto.board.team.PostUpdateRequest;
import peer.backend.entity.board.team.Board;
import peer.backend.entity.board.team.Post;
import peer.backend.entity.board.team.enums.BoardType;
import peer.backend.entity.team.Team;
import peer.backend.entity.user.User;
import peer.backend.exception.ConflictException;
import peer.backend.exception.ForbiddenException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.board.team.BoardRepository;
import peer.backend.repository.board.team.PostRepository;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.service.file.ObjectService;
import peer.backend.service.team.TeamService;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final TeamRepository teamRepository;
    private final TeamService teamService;
    private final ObjectService objectService;
    private final TeamUserRepository teamUserRepository;

    @Transactional
    public void createBoard(BoardCreateRequest request, Authentication auth) {
        User user = User.authenticationToUser(auth);
        Team team = teamRepository.findById(request.getTeamId()).orElseThrow(
            () -> new NotFoundException("존재하지 않는 팀입니다."));
        boardRepository.findByTeamAndName(team, request.getName()).ifPresent(board -> {
            throw new ConflictException("이미 존재하는 게시판입니다.");
        });

        Board board = Board.builder()
            .team(team)
            .name(request.getName())
            .type(BoardType.from(request.getType()))
            .build();
        boardRepository.save(board);
    }

    @PostCreateTracking
    @Transactional
    public Post createPost(PostCreateRequest request, Authentication auth) {
        User user = User.authenticationToUser(auth);
        Board board = boardRepository.findById(request.getBoardId()).orElseThrow(
            () -> new NotFoundException("존재하지 않는 게시판입니다."));
        Team team = board.getTeam();
        if (!teamService.isLeader(team.getId(), user)) {
            throw new ForbiddenException("팀 리더가 아닙니다.");
        }
        Post post = Post.builder()
            .board(board)
            .title(request.getTitle())
            .content(request.getContent())
            .hit(0)
            .user(user)
            .image(request.getImage() == null ?
                null : objectService.uploadObject(
                request.getImage(), "board/" + board.getId(), "image"))
            .build();
        return postRepository.save(post);
    }

    @Transactional
    public void getBoardList(Long teamId, Authentication auth) {
        User user = User.authenticationToUser(auth);
        if (teamUserRepository.findByUserIdAndTeamId(user.getId(), teamId) == null) {
            throw new ForbiddenException("팀 멤버가 아닙니다.");
        }

    }


    @Transactional
    public void updateBoard(Long boardId, BoardUpdateRequest request, Authentication auth) {
        User user = User.authenticationToUser(auth);
        Board board = boardRepository.findById(boardId).orElseThrow(
            () -> new NotFoundException("존재하지 않는 게시판입니다."));
        boardRepository.findByTeamAndName(board.getTeam(), request.getName())
            .ifPresent(tempBoard -> {
                throw new ConflictException("이미 존재하는 게시판입니다.");
            });
        if (!teamService.isLeader(board.getTeam().getId(), user)) {
            throw new ForbiddenException("게시판을 수정할 권한이 없습니다.");
        }
        board.update(request);
    }

    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request, Authentication auth) {
        User user = User.authenticationToUser(auth);
        Post post = postRepository.findById(postId).orElseThrow(
            () -> new NotFoundException("존재하지 않는 게시글입니다."));
        if (!post.getUser().equals(user) && !teamService.isLeader(post.getBoard().getTeam().getId(),
            user)) {
            throw new ForbiddenException("게시글을 수정할 권한이 업습니다.");
        }
        post.update(request);
        if (request.getImage() != null) {
            objectService.deleteObject(post.getImage());
            objectService.uploadObject(request.getImage(), "board/" + post.getBoard().getId(),
                "image");
        }

    }


    @Transactional
    public void deleteBoard(Long boardId, Authentication auth) {
        User user = User.authenticationToUser(auth);
        Board board = boardRepository.findById(boardId).orElseThrow(
            () -> new NotFoundException("존재하지 않는 게시판입니다."));
        if (!teamService.isLeader(board.getTeam().getId(), user)) {
            throw new ForbiddenException("팀을 삭제할 권한이 없습니다.");
        }
        boardRepository.delete(board);
    }

    @Transactional
    public void deletePost(Long postId, Authentication auth) {
        User user = User.authenticationToUser(auth);
        Post post = postRepository.findById(postId).orElseThrow(
            () -> new NotFoundException("존재하지 않는 게시글입니다."));
        if (!post.getUser().equals(user) && !teamService.isLeader(post.getBoard().getTeam().getId(),
            user)) {
            throw new ForbiddenException("게시글을 삭제할 권한이 업습니다.");
        }
        if (post.getImage() != null) {
            objectService.deleteObject(post.getImage());
        }
        postRepository.delete(post);
    }
}
