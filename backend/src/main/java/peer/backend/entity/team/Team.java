package peer.backend.entity.team;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.team.enums.TeamMemberStatus;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.user.InterestedProject;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "team")
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TeamType type;

    @Column(length = 30, nullable = false)
    String dueTo;

    @Column()
    String teamPicturePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TeamOperationFormat operationFormat;

    @Column()
    String teamLogoPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TeamStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TeamMemberStatus teamMemberStatus;

    @Column(nullable = false)
    Boolean isLock;

    @Column()
    Integer maxMember;

    @Column(length = 10, nullable = false)
    String region1;

    @Column(length = 10, nullable = false)
    String region2;

    @Column(length = 10, nullable = false)
    String region3;

    @OneToMany(mappedBy = "team", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<TeamUser> teamUsers = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<InterestedProject> interestedProjects = new ArrayList<>();

    @Override
    public String toString() {
        return "Team{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", type=" + type +
            ", dueTo='" + dueTo + '\'' +
            ", teamPicturePath='" + teamPicturePath + '\'' +
            ", operationFormat=" + operationFormat +
            ", teamLogoPath='" + teamLogoPath + '\'' +
            ", status=" + status +
            ", teamMemberStatus=" + teamMemberStatus +
            ", isLock=" + isLock +
            ", maxMember=" + maxMember +
            ", region1='" + region1 + '\'' +
            ", region2='" + region2 + '\'' +
            ", region3='" + region3 + '\'' +
            ", teamUsers=" + teamUsers +
            ", interestedProjects=" + interestedProjects +
            '}';
    }
}
