package peer.backend.mongo.entity;


import java.time.LocalDate;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.mongo.entity.enums.ActionType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "activity_tracking")
public class ActivityTracking {

    @Transient
    public static final String SEQUENCE_NAME = "activity_tracking_sequence";

    @Id
    private Long _id;
    private Long actId;
    private Long userId;
    private String intraId;
    private Long registeredTeamId;
    private TeamType teamType;
    private ActionType actionType;
    private String toolboxSubKey;
    private LocalDate actDate;
    private double wallet;
    private boolean handled;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
