package peer.backend.converter;

import javax.persistence.AttributeConverter;
import peer.backend.entity.NoticeTargetType;

public class AlarmTargetTypeConverter implements AttributeConverter<NoticeTargetType, Long> {

    @Override
    public Long convertToDatabaseColumn(NoticeTargetType attribute) {
        return attribute.getCode();
    }

    @Override
    public NoticeTargetType convertToEntityAttribute(Long dbData) {
        return NoticeTargetType.ofCode(dbData);
    }
}
