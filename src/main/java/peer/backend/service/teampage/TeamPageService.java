package peer.backend.service.teampage;

import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import peer.backend.dto.board.team.PostCreateRequest;
import peer.backend.dto.team.PostRes;
import peer.backend.entity.board.team.Board;
import peer.backend.entity.board.team.Post;
import peer.backend.entity.team.Team;
import peer.backend.entity.user.User;
import peer.backend.exception.ForbiddenException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.board.team.BoardRepository;
import peer.backend.repository.board.team.PostRepository;
import peer.backend.repository.team.TeamUserRepository;


@Service
@RequiredArgsConstructor
public class TeamPageService {
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final TeamUserRepository teamUserRepository;

    @Transactional
    public Page<PostRes> getPostsByBoardIdWithKeyword(Long boardId, Pageable pageable, String keyword) {
        Page<Post> posts;
        if (keyword == null) {
            posts = postRepository.findPostsByBoardOrderByIdDesc(boardId, pageable);
            return posts.map(
                    post -> new PostRes(post.getId(), post.getTitle(), post.getUser().getNickname(), post.getHit(),
                            post.getCreatedAt()));
        } else {
            posts = postRepository.findByBoardIdAndTitleOrContentContaining(boardId, keyword,
                    pageable);
            return posts.map(
                    post -> new PostRes(post.getId(), post.getTitle(), post.getUser().getNickname(), post.getHit(),
                            post.getCreatedAt()));
        }
    }

    @Transactional
    public Post createGeneralPost(PostCreateRequest request, Authentication auth) {
        User user = User.authenticationToUser(auth);
        Board board = boardRepository.findById(request.getBoardId()).orElseThrow(
                () -> new NotFoundException("존재하지 않는 게시판입니다."));
        Team team = board.getTeam();
        if (teamUserRepository.existsAndMemberByUserIdAndTeamId(user.getId(), team.getId())) {
            throw new ForbiddenException("해당 팀에 속해있지 않습니다.");
        }
        Post post = Post.builder()
                .board(board)
                .title(request.getTitle())
                .content(request.getContent())
                .hit(0)
                .user(user)
                .image(request.getImage()).build();
        return postRepository.save(post);
    }

    @Transactional
    public Post createNoticePost(PostCreateRequest request, Authentication auth) {
        User user = User.authenticationToUser(auth);
        Board board = boardRepository.findById(request.getBoardId()).orElseThrow(
                () -> new NotFoundException("존재하지 않는 게시판입니다."));
        if (!board.getType().getType().equals("NOTICE")) {
            throw new ForbiddenException("공지사항 게시판이 아닙니다.");
        }
        Team team = board.getTeam();
        if (!teamUserRepository.existsAndMemberByUserIdAndTeamId(user.getId(), team.getId())) {
            throw new ForbiddenException("팀 리더가 아닙니다.");
        }
        Post post = Post.builder()
                .board(board)
                .title(request.getTitle())
                .content(request.getContent())
                .hit(0)
                .user(user)
                .image(request.getImage()).build();
        return postRepository.save(post);
    }

    @Transactional
    public Post getPostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 게시글입니다."));
        post.increaseHit();
        return postRepository.save(post);

    }

}
