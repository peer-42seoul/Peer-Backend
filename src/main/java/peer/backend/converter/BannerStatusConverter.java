package peer.backend.converter;

import javax.persistence.AttributeConverter;
import peer.backend.entity.banner.BannerStatus;

public class BannerStatusConverter implements AttributeConverter<BannerStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(BannerStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public BannerStatus convertToEntityAttribute(Long dbData) {
        return BannerStatus.ofCode(dbData);
    }
}
