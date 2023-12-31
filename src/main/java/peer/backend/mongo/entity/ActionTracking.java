package peer.backend.mongo.entity;


import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Convert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import peer.backend.converter.ActionTypeEnumConverter;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.mongo.entity.enums.ActionTypeEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "action_tracking")
public class ActionTracking {

    @Transient
    public static final String SEQUENCE_NAME = "action_tracking_sequence";

    private Long actId;
    private Long userId;
    private String intraId;
    private Long registeredTeamId;
    private TeamType teamType;
    @Convert(converter = ActionTypeEnumConverter.class)
    private ActionTypeEnum actionTypeEnum;
    private String toolboxSubKey;
    @CreatedDate
    private LocalDate actDate;
    private double wallet;
    private boolean handled = false;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
