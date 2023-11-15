package peer.backend.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import peer.backend.mongo.entity.ActivityTracking;

@RequiredArgsConstructor
@Component
public class ActivityTrackingListener extends AbstractMongoEventListener<ActivityTracking> {

    private final SequenceGeneratorService sequenceGeneratorService;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<ActivityTracking> event) {
        event.getSource()
            .setActId(sequenceGeneratorService.generateSequence(ActivityTracking.SEQUENCE_NAME));
    }
}
