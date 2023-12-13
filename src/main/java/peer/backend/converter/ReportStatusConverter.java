package peer.backend.converter;

import javax.persistence.AttributeConverter;
import peer.backend.entity.report.ReportStatus;

public class ReportStatusConverter implements AttributeConverter<ReportStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(ReportStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public ReportStatus convertToEntityAttribute(Long dbData) {
        return ReportStatus.ofCode(dbData);
    }
}
