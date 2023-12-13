package peer.backend.converter;

import javax.persistence.AttributeConverter;
import peer.backend.entity.report.ReportProcessingStatus;

public class ReportProcessingStatusConverter implements
    AttributeConverter<ReportProcessingStatus, Long> {

    @Override
    public Long convertToDatabaseColumn(ReportProcessingStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public ReportProcessingStatus convertToEntityAttribute(Long dbData) {
        return ReportProcessingStatus.ofCode(dbData);
    }
}
