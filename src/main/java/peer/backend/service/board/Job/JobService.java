package peer.backend.service.board.Job;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import peer.backend.dto.board.Job.JobListResponse;
import peer.backend.dto.board.Job.JobResponse;
import peer.backend.entity.board.team.Post;
import peer.backend.entity.board.team.enums.BoardType;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.board.team.PostRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobService {

    private final PostRepository postRepository;
    public Page<JobListResponse> getJobList(int page, int pageSize){
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Post> result = postRepository.findAllByBoardType(BoardType.JOB, pageable);

        return result.map(post ->
                new JobListResponse(post.getTitle(),
                        post.getCreatedAt().toString(),
                        post.getUser().getNickname(),
                        post.getId()));
    }

    public JobResponse getJob(Long jobId){
        Post job = postRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Not Found"));
        return new JobResponse(job.getTitle(),
                job.getUser().getNickname(),
                job.getContent(),
                job.getCreatedAt().toString(),
                job.getId());
    }
}
