package peer.backend.service.teampage;

import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import peer.backend.dto.team.PostRes;
import peer.backend.repository.board.team.PostRepository;


@Service
@RequiredArgsConstructor
public class TeamPageService {
    private final PostRepository postRepository;

    @Transactional
    public Page<PostRes> getPostsByBoardId(Long boardId, Pageable pageable) {
        return postRepository.findPostsByBoardOrderByIdDesc(boardId, pageable)
                .map(post -> new PostRes(post.getId(), post.getTitle(), post.getUser().getNickname(), post.getHit(),
                        post.getCreatedAt()));
    }
}
