package peer.backend.controller.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.board.Job.JobListResponse;
import peer.backend.dto.board.Job.JobResponse;
import peer.backend.service.board.Job.JobService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/job")
@Slf4j
public class JobController {

    private final JobService jobService;
    @GetMapping("")
    public Page<JobListResponse> getJobList(@RequestParam int page, @RequestParam int pageSize){
       return jobService.getJobList(page, pageSize);
    }

    @GetMapping("/{jobId}")
    public JobResponse getJob(@PathVariable Long jobId){
        return jobService.getJob(jobId);
    }
}
