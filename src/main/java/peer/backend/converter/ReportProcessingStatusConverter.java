package peer.backend.converter;

import javax.persistence.AttributeConverter;
import peer.backend.entity.report.ReportHandleType;

public class ReportProcessingStatusConverter implements
    AttributeConverter<ReportHandleType, Long> {

    @Override
    public Long convertToDatabaseColumn(ReportHandleType attribute) {
        return attribute.getCode();
    }

    @Override
    public ReportHandleType convertToEntityAttribute(Long dbData) {
        return ReportHandleType.ofCode(dbData);
    }
}
