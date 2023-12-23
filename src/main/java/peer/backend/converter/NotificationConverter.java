package peer.backend.converter;

import javax.persistence.AttributeConverter;
import peer.backend.entity.notice.NoticeNotification;

public class NotificationConverter implements AttributeConverter<NoticeNotification, Long> {

    @Override
    public Long convertToDatabaseColumn(NoticeNotification attribute) {
        return attribute.getCode();
    }

    @Override
    public NoticeNotification convertToEntityAttribute(Long dbData) {
        return NoticeNotification.ofCode(dbData);
    }
}
