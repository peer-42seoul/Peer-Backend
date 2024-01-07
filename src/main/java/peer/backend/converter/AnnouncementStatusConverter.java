package peer.backend.converter;

import javax.persistence.AttributeConverter;
import peer.backend.entity.announcement.AnnouncementStatus;

public class AnnouncementStatusConverter implements AttributeConverter<AnnouncementStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(AnnouncementStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public AnnouncementStatus convertToEntityAttribute(Long dbData) {
        return AnnouncementStatus.ofCode(dbData);
    }
}
