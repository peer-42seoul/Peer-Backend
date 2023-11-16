package peer.backend.mongo.entity;


import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "team_tracking")
public class TeamTracking {

    private Long _id;
    private Long teamId;
    private String teamName;
    private LocalDate actionDate;
    private TeamType actionType;
    private String tag;
    private LocalDate actionFinishedDate;
    private LocalDate actionUnproperFinishedDate;
    private TeamStatus teamStatus;
    private boolean ftSubject;
    private int in42;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
