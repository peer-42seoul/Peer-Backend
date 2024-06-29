package peer.backend.dto.board.Job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.swing.border.TitledBorder;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class JobResponse {
    String title;
    String writerName;
    String createdAt;
    String content;
    Long id;
}
