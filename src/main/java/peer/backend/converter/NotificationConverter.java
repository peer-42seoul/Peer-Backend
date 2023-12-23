package peer.backend.converter;

import javax.persistence.AttributeConverter;
import peer.backend.entity.notice.Notification;

public class NotificationConverter implements AttributeConverter<Notification, Long> {

    @Override
    public Long convertToDatabaseColumn(Notification attribute) {
        return attribute.getCode();
    }

    @Override
    public Notification convertToEntityAttribute(Long dbData) {
        return Notification.ofCode(dbData);
    }
}
