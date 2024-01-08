package peer.backend.entity.team;

import lombok.*;
import peer.backend.dto.team.TeamJobRequestDto;
import peer.backend.dto.team.TeamJobUpdateDto;
import peer.backend.entity.team.enums.TeamUserStatus;
import peer.backend.exception.ConflictException;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "team_job")
public class TeamJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_job_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "teamJob", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TeamUserJob> teamUserJobs;

    @Column(nullable = false, length = 10)
    private String name;
    @Column(nullable = false)
    private Integer max;


    public int getCurrent(){
        return teamUserJobs.stream().filter(
                job -> job.getStatus().equals(TeamUserStatus.APPROVED))
                .collect(Collectors.toList()).size();
    }

    public void update(TeamJobRequestDto request){
        this.name = request.getName();
        if (this.getCurrent() <= request.getMax() )
            this.max = request.getMax();
        else
            throw new ConflictException("현재 역할에 배정된 인원보다 적은 수로 설정할 수 없습니다.");
    }


}