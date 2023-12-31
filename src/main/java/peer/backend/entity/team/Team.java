package peer.backend.entity.team;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import peer.backend.dto.board.recruit.RecruitUpdateRequestDTO;
import peer.backend.dto.team.TeamJobDto;
import peer.backend.dto.team.TeamJobRequestDto;
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
    @Enumerated(EnumType.STRING)
    private RecruitDueEnum dueTo;
    @Column
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

    @Column(nullable = false)
    private Integer maxMember;

    @Column(length = 10)
    private String region1;

    @Column(length = 10)
    private String region2;

    @Column
    private LocalDateTime end;

    @OneToMany(mappedBy = "team", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<TeamUser> teamUsers = new ArrayList<>();

    @OneToOne(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private Recruit recruit;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamJob> jobs;


    public void update(TeamSettingInfoDto teamSettingInfoDto) {
        this.name = teamSettingInfoDto.getName();
        this.dueTo = RecruitDueEnum.from(teamSettingInfoDto.getDueTo());
        this.status = teamSettingInfoDto.getStatus();
        String[] regions = teamSettingInfoDto.getRegion();
        if (teamSettingInfoDto.getRegion().length == 2) {
            this.region1 = regions[0];
            this.region2 = regions[1];
        }
        this.operationFormat = teamSettingInfoDto.getOperationForm();
    }

    public void update(RecruitUpdateRequestDTO request) {
        this.name = request.getName();
        this.dueTo = RecruitDueEnum.from(request.getDue());
        if (!request.getRegion().isEmpty()) {
            this.region1 = request.getRegion1();
            this.region2 = request.getRegion2();
        }
        this.operationFormat = TeamOperationFormat.from(request.getPlace());
        jobs.clear();
        if (request.getRoleList() != null && !request.getInterviewList().isEmpty())
            request.getRoleList().forEach(this::addRole);
    }

    public void addRole(TeamJobDto role) {
        if (this.getJobs() == null) {
            this.jobs = new ArrayList<>();
        }
        this.jobs.add(TeamJob.builder()
                .name(role.getName())
                .max(role.getNumber())
                .team(this)
                .build());
    }

    public void addRole(TeamJobRequestDto role) {
        if (this.getJobs() == null) {
            this.jobs = new ArrayList<>();
        }
        this.jobs.add(TeamJob.builder()
                .name(role.getName())
                .max(role.getMax())
                .team(this)
                .build());
    }


    @PrePersist
    @PreUpdate
    @PostLoad
    private void updateDueValue() {
        if (this.dueTo != null) {
            this.dueValue = this.dueTo.getValue();
        }
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

    public void setTeamLogoPath(String teamLogoPath) {
        this.teamLogoPath = teamLogoPath;
    }
}
