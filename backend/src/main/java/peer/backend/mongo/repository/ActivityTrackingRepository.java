package peer.backend.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import peer.backend.mongo.entity.ActivityTracking;

public interface ActivityTrackingRepository extends MongoRepository<ActivityTracking, Long> {

}
