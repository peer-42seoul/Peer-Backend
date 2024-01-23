package peer.backend.service.board.team;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.annotation.tracking.PostCreateTracking;
import peer.backend.dto.board.team.*;
import peer.backend.dto.team.SimpleBoardRes;
import peer.backend.entity.board.team.Board;
import peer.backend.entity.board.team.Post;
import peer.backend.entity.board.team.PostComment;
import peer.backend.entity.board.team.enums.BoardType;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.enums.TeamUserStatus;
import peer.backend.entity.user.User;
import peer.backend.exception.ConflictException;
import peer.backend.exception.ForbiddenException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.board.team.BoardRepository;
import peer.backend.repository.board.team.PostCommentRepository;
import peer.backend.repository.board.team.PostRepository;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.service.file.ObjectService;
import peer.backend.service.team.TeamService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final TeamRepository teamRepository;
    private final TeamService teamService;
    private final ObjectService objectService;
    private final TeamUserRepository teamUserRepository;
    private final PostCommentRepository postCommentRepository;

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
        if (teamUserRepository.findByUserIdAndTeamId(user.getId(), teamId).isEmpty()) {
            throw new ForbiddenException("팀 멤버가 아닙니다.");
        }

    }

    @Transactional
    public List<SimpleBoardRes> getSimpleBoards(Long teamId, Authentication auth) {
        User user = User.authenticationToUser(auth);
        List<SimpleBoardRes> boards = boardRepository.findByTeamId(teamId)
                .stream()
                .map(board -> new SimpleBoardRes(board.getId(), board.getName()))
                .collect(Collectors.toList());

        return boards;
    }

    @Transactional
    public Board getBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));
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

    @Transactional
    public void createComment(PostCommentRequest request, Authentication auth) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시물입니다."));
        User user = User.authenticationToUser(auth);
        if (!teamUserRepository.existsByUserIdAndTeamIdAndStatus(
                User.authenticationToUser(auth).getId(),
                post.getBoard().getTeam().getId(),
                TeamUserStatus.APPROVED)) {
            throw new ForbiddenException("답글을 게시할 수 없습니다.");
        }
        post.addComment(request.getContent(), user);
    }

    @Transactional
    public void updateComment(Long commentId, PostCommentUpdateRequest request, Authentication auth) {
        User user = User.authenticationToUser(auth);
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 댓글입니다."));
        if (!user.equals(comment.getUser())) {
            throw new ForbiddenException("작성자가 아닙니다.");
        }
        comment.update(request.getContent());
    }

    @Transactional
    public Page<PostCommentListResponse> getComments(Long postId, int page, int pageSize, Authentication auth) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
        User user = User.authenticationToUser(auth);

        boolean isApproved = teamUserRepository.existsByUserIdAndTeamIdAndStatus(
                user.getId(),
                post.getBoard().getTeam().getId(),
                TeamUserStatus.APPROVED
        );
        if (!isApproved) {
            throw new ForbiddenException("답글을 불러올 권한이 없습니다.");
        }
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<PostComment> comments = postCommentRepository.findByPostId(postId, pageable);
        return comments.map(PostCommentListResponse::new);
    }

    @Transactional
    public ResponseEntity<Object> deleteComment(Long commentId, Authentication auth) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 댓글입니다."));
        if (!comment.getUser().equals(User.authenticationToUser(auth))) {
            throw new ForbiddenException("작성자가 아닙니다");
        }
        postCommentRepository.delete(comment);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public Board getNoticeBoard(Long teamId, User user) {
        if (!teamUserRepository.existsByUserIdAndTeamIdAndStatus(
                user.getId(),
                teamId,
                TeamUserStatus.APPROVED)) {
            throw new ForbiddenException("공지사항을 가져올 권한이 없습니다.");
        }
        teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        return boardRepository.findByTeamIdAndType(teamId, BoardType.NOTICE)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 공지사항 게시판입니다."));

    }
}
