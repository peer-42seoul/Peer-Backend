package peer.backend.entity.board.team;

import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;
import peer.backend.dto.board.team.BoardUpdateRequest;
import peer.backend.entity.board.team.enums.BoardType;
import peer.backend.entity.team.Team;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"team_id", "name"})
})
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Enumerated(EnumType.STRING)
    private BoardType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    public void update(BoardUpdateRequest request){
        this.name = request.getName();
    }
}
