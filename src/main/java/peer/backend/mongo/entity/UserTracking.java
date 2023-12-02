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
import peer.backend.mongo.entity.enums.UserTrackingStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "user_tracking")
public class UserTracking {

    private Long _id;
    private Long userId;
    private String userEmail;
    private LocalDate registrationDate;
    private LocalDate unRegistrationDate;
    private String intraId = null;
    private boolean ftOAuthRegistered;
    private LocalDate peerMemberDate;
    private int accumulatedWallet;
    private int monthlyAccumulatedWallet;
    private UserTrackingStatus status = UserTrackingStatus.NORMAL;
    private int reportCount;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
