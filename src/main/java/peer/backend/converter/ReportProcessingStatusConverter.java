package peer.backend.converter;

import javax.persistence.AttributeConverter;
import peer.backend.entity.report.ReportProcessingType;

public class ReportProcessingStatusConverter implements
    AttributeConverter<ReportProcessingType, Long> {

    @Override
    public Long convertToDatabaseColumn(ReportProcessingType attribute) {
        return attribute.getCode();
    }

    @Override
    public ReportProcessingType convertToEntityAttribute(Long dbData) {
        return ReportProcessingType.ofCode(dbData);
    }
}
