package peer.backend.converter;

import javax.persistence.AttributeConverter;
import peer.backend.entity.banner.BannerType;
import peer.backend.entity.blacklist.BlacklistType;

public class BannerTypeConverter implements AttributeConverter<BannerType, Long> {

    @Override
    public Long convertToDatabaseColumn(BannerType attribute) {
        return attribute.getCode();
    }

    @Override
    public BannerType convertToEntityAttribute(Long dbData) {
        return BannerType.ofCode(dbData);
    }
}
