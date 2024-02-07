package peer.backend.entity.board.recruit;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import peer.backend.dto.board.recruit.RecruitInterviewDto;
import peer.backend.dto.board.recruit.RecruitUpdateRequestDTO;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.board.recruit.enums.RecruitInterviewType;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.tag.RecruitTag;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserPortfolio;

import javax.persistence.*;
import javax.validation.constraints.Size;
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
    private List<RecruitInterview> interviews = new ArrayList<>();
    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitFile> files = new ArrayList<>();

    @Column
    private Long hit = 0L;
    @Column(nullable = false)
    @Size(max=100)
    private String title;

    @Column(nullable = false)
    @Lob
    private String content;
    @Column
    private String link;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecruitStatus status;
    @Column
    private String thumbnailUrl;
    @OneToMany(mappedBy = "recruit", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitTag> recruitTags = new ArrayList<>();
    @Column
    private Long writerId;

    @OneToMany(mappedBy = "recruit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserPortfolio> userPortfoliosHistories;

    public void update(RecruitUpdateRequestDTO request, List<String> urls) {
        this.getTeam().update(request);
        this.title = request.getTitle();
        this.content = request.getContent();
        this.status = RecruitStatus.from(request.getStatus());
        this.link = request.getLink();
        if (request.getType().equals(TeamType.STUDY.getValue()))
            this.team.getJobs().get(1).setMax(request.getMax());
        this.recruitTags.clear();
        if (request.getTagList() != null && !request.getTagList().isEmpty())
            addTags(request.getTagList());
        this.interviews.clear();
        if (request.getInterviewList() != null && !request.getInterviewList().isEmpty()) {
            for (RecruitInterviewDto interview : request.getInterviewList()) {
                this.addInterview(interview);
            }
        }
        this.files.clear();
        if (urls != null && !urls.isEmpty()) {
            urls.forEach(this::addFile);
        }
    }

    private void addTag(Long tagId){
        this.recruitTags.add(new RecruitTag(this.id, tagId));
    }

    private void addTags(List<Long> tags){
        tags.forEach(this::addTag);
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

    public void setHit(Long hit) {
        this.hit = hit;
    }

    public void addFiles(List<String> urls){
        urls.forEach(this::addFile);
    }

    public void addFile(String url) {
        if (this.files == null)
            this.files = new ArrayList<>();
        this.files.add(
                RecruitFile.builder()
                        .recruit(this)
                        .url(url)
                        .build()
        );
    }
}
