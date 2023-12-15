package peer.backend.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import peer.backend.mongo.entity.ActionTracking;

public interface ActionTrackingRepository extends MongoRepository<ActionTracking, Long> {

}
