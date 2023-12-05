package peer.backend.entity.board.recruit;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import peer.backend.dto.board.recruit.RecruitInterviewDto;
import peer.backend.dto.board.recruit.RecruitRoleDTO;
import peer.backend.dto.board.recruit.RecruitUpdateRequestDTO;
import peer.backend.dto.board.recruit.TagListResponse;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.board.recruit.enums.RecruitDueEnum;
import peer.backend.entity.board.recruit.enums.RecruitInterviewType;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.user.User;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
@Table(name = "Recruit")
public class Recruit extends BaseEntity {

    @Id
    @Column(name = "recruit_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User writer;

    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitFavorite> favorites = new ArrayList<>();
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitApplicant> applicants = new ArrayList<>();
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitRole> roles = new ArrayList<>();
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitInterview> interviews = new ArrayList<>();
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitFile> files = new ArrayList<>();

    @Column
    private Long hit = 0L;
    @Column(nullable = false)
    private String title;
    @Enumerated(EnumType.STRING)
    private RecruitDueEnum due;
    private int dueValue;
    @Column(nullable = false)
    private String content;
    @Column(length = 10)
    private String region1;
    @Column(length = 10)
    private String region2;
    @Column
    private String link;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamType type;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamOperationFormat place;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecruitStatus status;
    @Column
    private String thumbnailUrl;
    @ElementCollection
    private List<String> tags = new ArrayList<>();
    @Column
    private Long writerId;

    @PrePersist
    @PreUpdate
    private void updateDueValue() {
        if (this.due != null) {
            this.dueValue = this.due.getValue();
        }
    }

    public void update(RecruitUpdateRequestDTO request, String filePath){
        this.title = request.getTitle();
        this.due = RecruitDueEnum.from(request.getDue());
        this.content = request.getContent();
        this.status = request.getStatus();
        this.region1 = (request.getPlace().equals("온라인") ? null : request.getRegion1());
        this.region2 = (request.getPlace().equals("온라인") ? null : request.getRegion2());
        this.link = request.getLink();
        this.thumbnailUrl = filePath;
        this.tags.clear();
        this.tags = request.getTagList().stream().map(TagListResponse::getName)
            .collect(Collectors.toList());
        this.interviews.clear();
        if (!request.getInterviewList().isEmpty()) {
            for (RecruitInterviewDto interview : request.getInterviewList()) {
                this.addInterview(interview);
            }
        }
        this.roles.clear();
        if (!request.getInterviewList().isEmpty()) {
            for (RecruitRoleDTO role : request.getRoleList()) {
                this.addRole(role);
            }
        }
    }

    public void addInterview(RecruitInterviewDto interview) {
        if (this.getInterviews() == null) {
            this.interviews = new ArrayList<>();
        }
        this.interviews.add(RecruitInterview.builder()
            .question(interview.getQuestion())
            .type(RecruitInterviewType.valueOf(interview.getType()))
            .options(interview.getOptionList())
            .recruit(this)
            .build());
    }

    public void addRole(RecruitRoleDTO role) {
        if (this.getRoles() == null) {
            this.roles = new ArrayList<>();
        }
        this.roles.add(RecruitRole.builder()
            .name(role.getName())
            .number(role.getNumber())
            .recruit(this).build());
    }

    public void setHit(Long hit) {
        this.hit = hit;
    }
}