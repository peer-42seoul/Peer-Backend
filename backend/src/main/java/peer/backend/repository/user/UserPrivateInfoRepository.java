package peer.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.user.UserPrivateInfo;

public interface UserPrivateInfoRepository extends JpaRepository<UserPrivateInfo, Long> {

}
