package peer.backend.converter;

import javax.persistence.AttributeConverter;
import peer.backend.entity.AlarmTargetType;

public class AlarmTargetTypeConverter implements AttributeConverter<AlarmTargetType, Long> {

    @Override
    public Long convertToDatabaseColumn(AlarmTargetType attribute) {
        return attribute.getCode();
    }

    @Override
    public AlarmTargetType convertToEntityAttribute(Long dbData) {
        return AlarmTargetType.ofCode(dbData);
    }
}
