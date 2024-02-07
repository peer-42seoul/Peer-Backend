package peer.backend.entity.banner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import peer.backend.converter.BannerStatusConverter;
import peer.backend.converter.BannerTypeConverter;
import peer.backend.entity.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "banner")
public class Banner extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Convert(converter = BannerTypeConverter.class)
    private BannerType bannerType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false)
    @Convert(converter = BannerStatusConverter.class)
    private BannerStatus bannerStatus;

    @Column
    private LocalDateTime reservationDate;

    @Column(columnDefinition = "TEXT")
    private String announcementUrl;

    public void setBannerStatus(BannerStatus bannerStatus) {
        if (bannerStatus.equals(BannerStatus.ONGOING)) {
            ZoneId seoulZone = ZoneId.of("Asia/Seoul");
            this.setCreatedAt(LocalDateTime.now(seoulZone));
        }
        this.bannerStatus = bannerStatus;
    }
}
