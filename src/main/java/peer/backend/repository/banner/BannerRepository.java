package peer.backend.repository.banner;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.banner.Banner;
import peer.backend.entity.banner.BannerStatus;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    List<Banner> findAllByBannerStatus(BannerStatus status);

    Page<Banner> findAllByOrderByIdDesc(Pageable pageable);
}
