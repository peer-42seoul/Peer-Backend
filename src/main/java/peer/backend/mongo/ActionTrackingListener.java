package peer.backend.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import peer.backend.mongo.entity.ActionTracking;

@RequiredArgsConstructor
@Component
public class ActionTrackingListener extends AbstractMongoEventListener<ActionTracking> {

    private final SequenceGeneratorService sequenceGeneratorService;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<ActionTracking> event) {
        event.getSource()
            .setActId(sequenceGeneratorService.generateSequence(ActionTracking.SEQUENCE_NAME));
    }
}
