package peer.backend.entity.team;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import peer.backend.dto.team.TeamJobDto;
import peer.backend.dto.team.TeamSettingInfoDto;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.enums.RecruitDueEnum;
import peer.backend.entity.team.enums.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
@Table(name = "team")
@EqualsAndHashCode(callSuper = false)
public class Team extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Size(min = 2, max = 30)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamType type;

    @Column(nullable = false)
    @Enumerated
    private RecruitDueEnum dueTo;
    private int dueValue;

    @Column()
    private String teamPicturePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamOperationFormat operationFormat;

    @Column()
    private String teamLogoPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamMemberStatus teamMemberStatus;

    @Column(nullable = false)
    private Boolean isLock;

    @Column()
    private Integer maxMember;

    @Column(length = 10)
    private String region1;

    @Column(length = 10)
    private String region2;

    @Column(length = 10)
    private String region3;

    @Column
    private LocalDateTime end;

    @OneToMany(mappedBy = "team", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<TeamUser> teamUsers = new ArrayList<>();

    @OneToOne(mappedBy = "team", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Recruit recruit;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamJob> jobs;

    @PostLoad
    private void updateValue(){
        this.maxMember = this.getJobs().stream().mapToInt(TeamJob::getMax).sum();
    }

    public void update(TeamSettingInfoDto teamSettingInfoDto) {
        this.name = teamSettingInfoDto.getName();
        this.dueTo = RecruitDueEnum.from(teamSettingInfoDto.getDueTo());
        this.status = teamSettingInfoDto.getStatus();
        String[] regions = teamSettingInfoDto.getRegion();
        this.region1 = regions.length > 0 ? regions[0] : "";
        this.region2 = regions.length > 1 ? regions[1] : "";
        this.region3 = regions.length > 2 ? regions[2] : "";
        this.operationFormat = teamSettingInfoDto.getOperationForm();
        this.maxMember = Integer.valueOf(teamSettingInfoDto.getMaxMember());
    }

    @PrePersist
    @PreUpdate
    @PostLoad
    private void updateValues() {
        if (this.dueTo != null) {
            this.dueValue = this.dueTo.getValue();
        }
        this.maxMember = getJobs().stream().mapToInt(TeamJob::getMax).sum();
    }

    public boolean deleteTeamUser(Long deletingToUserId) {
        return this.teamUsers.removeIf(teamUser -> teamUser.getUserId().equals(deletingToUserId));
    }

    public void grantLeaderPermission(Long grantingUserId, TeamUserRoleType teamUserRoleType) {
        for (TeamUser teamUser: this.teamUsers) {
            if (teamUser.getUserId().equals(grantingUserId)) {
                teamUser.grantLeader(teamUserRoleType);
            }
        }
    }



    public void addRole(TeamJobDto role) {
        if (this.getJobs() == null) {
            this.jobs = new ArrayList<>();
        }
        this.jobs.add(TeamJob.builder()
                .name(role.getName())
                .max(role.getNumber())
                .current(0)
                .team(this).build());
    }

    public void setTeamLogoPath(String teamLogoPath) {
        this.teamLogoPath = teamLogoPath;
    }
}
