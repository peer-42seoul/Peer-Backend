package peer.backend.entity.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
*   추후 사용할 엔티티 아직 사용 X
*/

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_private_info")
public class UserPrivateInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
