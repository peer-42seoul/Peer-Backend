package peer.backend.entity.board.recruit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import peer.backend.dto.Board.Recruit.RecruitUpdateRequestDTO;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
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
public class Recruit extends BaseEntity {
    @Id
    @Column(name = "recruit_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "recruit", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<RecruitFavorite> favorites = new ArrayList<>();
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<RecruitApplicant> applicants = new ArrayList<>();
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<RecruitRole> roles = new ArrayList<>();
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<RecruitInterview> interviews = new ArrayList<>();
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<RecruitFile> files = new ArrayList<>();

    @Column
    private Long hit;
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
    @Column
    private Long writerId;



    public void update(RecruitUpdateRequestDTO request, String content){
        this.title = request.getTitle();
        this.due = request.getDue();
        this.content = content;
        this.status = request.getStatus();
        this.region = request.getRegion();
        this.link = request.getLink();
        this.tags.clear();
        this.tags = request.getTagList();
        this.interviews.clear();
        if (!request.getInterviewList().isEmpty()) {
            for (RecruitInterview interview : request.getInterviewList()) {
                this.addInterview(interview);
            }
        }
        this.roles.clear();
        if (!request.getInterviewList().isEmpty()) {
            for (RecruitRole role : request.getRoleList()) {
                this.addRole(role);
            }
        }
    }

    public void addInterview(RecruitInterview interview) {
        if (this.getInterviews() == null)
            this.interviews = new ArrayList<>();
        this.interviews.add(interview);
        interview.setRecruit(this);
    }
    public void addRole(RecruitRole role) {
        if (this.getRoles() == null)
            this.roles = new ArrayList<>();
        this.roles.add(role);
        role.setRecruit(this);
    }
}
