package peer.backend.entity.team;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.team.enums.TeamMemberStatus;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamType;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "team")
public class Team {

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

    @Column(length = 10, nullable = false)
    String region1;

    @Column(length = 10, nullable = false)
    String region2;
}
