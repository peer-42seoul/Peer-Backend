package peer.backend.entity.board.recruit;


import lombok.*;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.board.recruit.enums.RecruitApplicantStatus;
import peer.backend.entity.composite.RecruitApplicantPK;
import peer.backend.entity.user.User;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(RecruitApplicantPK.class)
@Table(name = "recruit_applicant")
public class RecruitApplicant extends BaseEntity {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "recruit_id")
    private Long recruitId;

    @Id
    @Column(name = "job")
    private String job;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("recruitId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id")
    private Recruit recruit;

    @Enumerated(EnumType.STRING)
    private RecruitApplicantStatus status;

    @ElementCollection
    private List<String> answerList;

    @Column(nullable = false, length = 7)
    private String nickname;
}
