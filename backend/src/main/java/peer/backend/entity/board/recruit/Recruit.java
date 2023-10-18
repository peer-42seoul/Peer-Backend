package peer.backend.entity.board.recruit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import peer.backend.entity.board.recruit.enums.RecruitPlace;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.board.recruit.enums.RecruitType;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
@Table(name = "Recruit")
public class Recruit {
    @Id
    @Column(name="recruite_id")
    private Long id;

    @OneToOne
    @MapsId
    private Team team;

    @OneToMany(mappedBy = "recruit", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<RecruitApplicant> applicants = new ArrayList<>();
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<RecruitRole> roles;
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<RecruitInterview> interviews;
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<RecruitFile> files;
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<RecruitAnswer> answers;

    @Column
    private String title;
    @Column
    private String due;
    @Column
    private String content;
    @Column
    private String region;
    @Column
    private String link;
    @Enumerated(EnumType.STRING)
    private TeamType type;
    @Enumerated(EnumType.STRING)
    private TeamOperationFormat place;
    @Enumerated(EnumType.STRING)
    private RecruitStatus status;
    @Column
    private String thumbnailUrl;
    @ElementCollection
    private List<String> tags;
}
