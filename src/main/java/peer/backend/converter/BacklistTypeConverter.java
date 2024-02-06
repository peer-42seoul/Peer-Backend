package peer.backend.converter;

import javax.persistence.AttributeConverter;
import peer.backend.entity.blacklist.BlacklistType;

public class BacklistTypeConverter implements AttributeConverter<BlacklistType, Long> {

    @Override
    public Long convertToDatabaseColumn(BlacklistType attribute) {
        return attribute.getCode();
    }

    @Override
    public BlacklistType convertToEntityAttribute(Long dbData) {
        return BlacklistType.ofCode(dbData);
    }
}
