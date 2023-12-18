package peer.backend.converter;

import javax.persistence.AttributeConverter;
import peer.backend.mongo.entity.enums.ActionTypeEnum;

public class ActionTypeEnumConverter implements AttributeConverter<ActionTypeEnum, Long> {

    @Override
    public Long convertToDatabaseColumn(ActionTypeEnum attribute) {
        return attribute.getCode();
    }

    @Override
    public ActionTypeEnum convertToEntityAttribute(Long dbData) {
        return ActionTypeEnum.ofCode(dbData);
    }
}
