package peer.backend.entity.board.recruit;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import peer.backend.dto.board.recruit.RecruitInterviewDto;
import peer.backend.dto.board.recruit.RecruitUpdateRequestDTO;
import peer.backend.dto.team.TeamJobDto;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.board.recruit.enums.RecruitDueEnum;
import peer.backend.entity.board.recruit.enums.RecruitInterviewType;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.tag.RecruitTag;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamJob;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
@Table(name = "recruit")
public class Recruit extends BaseEntity {

    @Id
    @Column(name = "team_id")
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
    private List<TeamJob> jobs = new ArrayList<>();
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitInterview> interviews = new ArrayList<>();
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitFile> files = new ArrayList<>();

    @Column
    private Long hit = 0L;
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;
    @Column
    private String link;
    @Column(nullable = false)
    private RecruitStatus status;
    @Column
    private String thumbnailUrl;
    @OneToMany(mappedBy = "recruit", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitTag> recruitTags = new ArrayList<>();
    @Column
    private Long writerId;

    public void update(RecruitUpdateRequestDTO request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.status = request.getStatus();
        this.link = request.getLink();
        this.recruitTags.clear();
        if (request.getTagList() != null && !request.getTagList().isEmpty())
        this.recruitTags = request.getTagList().stream()
            .map(e -> (new RecruitTag(this.id, e)))
            .collect(
                Collectors.toList());
        this.interviews.clear();
        if (request.getInterviewList() != null && !request.getInterviewList().isEmpty()) {
            for (RecruitInterviewDto interview : request.getInterviewList()) {
                this.addInterview(interview);
            }
        }
        this.jobs.clear();
        if (request.getRoleList() != null && !request.getInterviewList().isEmpty()) {
            request.getRoleList().stream()
                    .forEach(role -> { this.addRole(role); });
        }
    }

    public void addInterview(RecruitInterviewDto interview) {
        if (this.getInterviews() == null) {
            this.interviews = new ArrayList<>();
        }
        this.interviews.add(RecruitInterview.builder()
            .question(interview.getQuestion())
            .type(RecruitInterviewType.valueOf(interview.getType()))
            .options(interview.getOptions())
            .recruit(this)
            .build());
    }

    public void addRole(TeamJobDto role) {
        if (this.getJobs() == null) {
            this.jobs = new ArrayList<>();
        }
        this.jobs.add(TeamJob.builder()
            .name(role.getName())
            .max(role.getNumber())
            .recruit(this).build());
    }

    public void setHit(Long hit) {
        this.hit = hit;
    }
}
