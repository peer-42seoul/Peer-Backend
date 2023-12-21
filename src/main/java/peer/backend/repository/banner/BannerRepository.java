package peer.backend.repository.banner;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.banner.Banner;

public interface BannerRepository extends JpaRepository<Banner, Long> {

}
