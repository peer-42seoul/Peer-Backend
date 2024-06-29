package peer.backend.dto.board.Job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class JobListResponse {
    String title;
    String createdAt;
    String writerName;
    Long id;
}
