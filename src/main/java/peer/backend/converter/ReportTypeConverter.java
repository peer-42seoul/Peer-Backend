package peer.backend.converter;

import javax.persistence.AttributeConverter;
import peer.backend.entity.report.ReportType;

public class ReportTypeConverter implements AttributeConverter<ReportType, Long> {

    @Override
    public Long convertToDatabaseColumn(ReportType attribute) {
        return attribute.getCode();
    }

    @Override
    public ReportType convertToEntityAttribute(Long dbData) {
        return ReportType.ofCode(dbData);
    }
}
