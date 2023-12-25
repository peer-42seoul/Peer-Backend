package peer.backend.converter;

import javax.persistence.AttributeConverter;
import peer.backend.entity.notice.NoticeStatus;

public class NoticeStatusConverter implements AttributeConverter<NoticeStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(NoticeStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public NoticeStatus convertToEntityAttribute(Long dbData) {
        return NoticeStatus.ofCode(dbData);
    }
}
