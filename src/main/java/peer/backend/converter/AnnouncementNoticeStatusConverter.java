package peer.backend.converter;

import javax.persistence.AttributeConverter;
import peer.backend.entity.announcement.AnnouncementNoticeStatus;

public class AnnouncementNoticeStatusConverter implements
    AttributeConverter<AnnouncementNoticeStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(AnnouncementNoticeStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public AnnouncementNoticeStatus convertToEntityAttribute(Long dbData) {
        return AnnouncementNoticeStatus.ofCode(dbData);
    }
}
